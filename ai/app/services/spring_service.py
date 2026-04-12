import requests
from app.core.config import SPRING_BOOT_API_URL
from datetime import timedelta, timezone, date
import calendar

# ── Helpers ───────────────────────────────────────────────────
def _headers(token: str) -> dict:
    return {
        "Authorization": f"Bearer {token}",
        "Content-Type":  "application/json"
    }

def _current_month_range() -> tuple[str, str]:
    """Trả về (start, end) của tháng hiện tại dạng YYYY-MM-DD"""
    today     = date.today()
    start     = today.replace(day=1)
    last_day  = calendar.monthrange(today.year, today.month)[1]
    end       = today.replace(day=last_day)
    return start.strftime("%Y-%m-%d"), end.strftime("%Y-%m-%d")

def _month_range(year: int, month: int) -> tuple[str, str]:
    """Trả về (start, end) của tháng bất kỳ"""
    start    = date(year, month, 1)
    last_day = calendar.monthrange(year, month)[1]
    end      = date(year, month, last_day)
    return start.strftime("%Y-%m-%d"), end.strftime("%Y-%m-%d")


# ── Report APIs ───────────────────────────────────────────────
def get_summary_report(token: str, start_date: str, end_date: str) -> dict:
    """
    GET /reports/summary?startDate=...&endDate=...
    Returns: { totalIncome, totalExpense, netBalance }
    """
    try:
        res = requests.get(
            f"{SPRING_BOOT_API_URL}/reports/summary",
            params={"startDate": start_date, "endDate": end_date},
            headers=_headers(token),
            timeout=10
        )
        if res.status_code == 200:
            return res.json()
        print(f"Lỗi summary: {res.status_code} - {res.text}")
        return {}
    except Exception as e:
        print(f"Lỗi kết nối: {e}")
        return {}
    
def get_category_report(token: str, tx_type: str, start_date: str, end_date: str) -> list:
    """
    GET /reports/by-category?type=EXPENSE&startDate=...&endDate=...
    Returns: [{ id, name, totalAmount, percentage }]
    """
    try:
        res = requests.get(
            f"{SPRING_BOOT_API_URL}/reports/by-category",
            params={"type": tx_type, "startDate": start_date, "endDate": end_date},
            headers=_headers(token),
            timeout=10
        )
        if res.status_code == 200:
            return res.json()
        print(f"Lỗi category report: {res.status_code} - {res.text}")
        return []
    except Exception as e:
        print(f"Lỗi kết nối: {e}")
        return []
    
def get_wallet_report(token: str, tx_type: str, start_date: str, end_date: str) -> list:
    """
    GET /reports/by-wallet?type=EXPENSE&startDate=...&endDate=...
    Returns: [{ id, name, totalAmount, percentage }]
    """
    try:
        res = requests.get(
            f"{SPRING_BOOT_API_URL}/reports/by-wallet",
            params={"type": tx_type, "startDate": start_date, "endDate": end_date},
            headers=_headers(token),
            timeout=10
        )
        if res.status_code == 200:
            return res.json()
        print(f"Lỗi wallet report: {res.status_code} - {res.text}")
        return []
    except Exception as e:
        print(f"Lỗi kết nối: {e}")
        return []

def get_full_report(token: str, start_date: str, end_date: str) -> dict:
    """Gộp cả 3 API report lại thành 1 object cho AI dùng"""
    summary           = get_summary_report(token, start_date, end_date)
    expense_by_cat    = get_category_report(token, "EXPENSE", start_date, end_date)
    income_by_cat     = get_category_report(token, "INCOME",  start_date, end_date)
    expense_by_wallet = get_wallet_report(token, "EXPENSE", start_date, end_date)

    return {
        "period":             {"start": start_date, "end": end_date},
        "summary":            summary,
        "expense_by_category": expense_by_cat,
        "income_by_category":  income_by_cat,
        "expense_by_wallet":   expense_by_wallet,
    }


# ── Chat transaction APIs ───────────────────────────────────────────────

def get_user_id_from_token(token: str) -> int | None:
    """Decode JWT lấy user_id từ sub claim"""
    try:
        import base64, json
        payload = token.split(".")[1]
        # Thêm padding nếu thiếu
        payload += "=" * (4 - len(payload) % 4)
        decoded = json.loads(base64.b64decode(payload).decode("utf-8"))
        return int(decoded.get("sub"))
    except Exception as e:
        print(f"Lỗi decode token: {e}")
        return None


def get_wallets(token: str) -> list[dict]:
    """Lấy danh sách ví của user"""
    try:
        res = requests.get(
            f"{SPRING_BOOT_API_URL}/wallets",
            headers={"Authorization": f"Bearer {token}"},
            timeout=10
        )
        if res.status_code == 200:
            data = res.json()
            # API trả về list hoặc object có data field
            if isinstance(data, list):
                return data
            return data.get("data", [])
        print(f"Lỗi get wallets: {res.status_code} - {res.text}")
        return []
    except Exception as e:
        print(f"Lỗi kết nối Spring Boot: {e}")
        return []


def find_wallet_by_name(token: str, wallet_name: str) -> dict | None:
    """Tìm ví theo tên, không phân biệt hoa thường"""
    wallets = get_wallets(token)
    for w in wallets:
        if w.get("name", "").lower() == wallet_name.lower():
            return w
    return None


def save_transaction(token: str, pending) -> bool:
    """Gọi API Spring Boot lưu giao dịch"""
    # created_at = pending.tx_datetime.strftime("%Y-%m-%dT%H:%M:%S.000Z")


    # 1. Giả định pending.tx_datetime đang là giờ Việt Nam (Naive datetime)
    # Ta gán múi giờ +7 cho nó, sau đó convert sang UTC
    vn_tz = timezone(timedelta(hours=7))
    
    # Nếu tx_datetime chưa có múi giờ, ta 'localize' nó là giờ VN
    dt_vn = pending.tx_datetime.replace(tzinfo=vn_tz)
    
    # Chuyển sang UTC 0
    dt_utc = dt_vn.astimezone(timezone.utc)
    
    # 2. Định dạng theo chuẩn ISO 8601 kèm hậu tố 'Z'
    created_at = dt_utc.strftime("%Y-%m-%dT%H:%M:%S.000Z")

    payload = {
        "amount":     pending.amount,
        "note":       pending.note or "",
        "type":       pending.tx_type,
        "createdAt":  created_at,
        "categoryId": pending.category_id,
        "walletId":   pending.wallet_id
    }

    print(f"Gửi transaction: {payload}")

    try:
        res = requests.post(
            f"{SPRING_BOOT_API_URL}/transactions",
            json=payload,
            headers={
                "Authorization": f"Bearer {token}",
                "Content-Type":  "application/json"
            },
            timeout=10
        )
        print(f"Spring Boot response: {res.status_code} - {res.text}")
        return res.status_code in (200, 201)
    except Exception as e:
        print(f"Lỗi kết nối Spring Boot: {e}")
        return False

def get_transactions(token: str, tx_type: str = None,
                     start_date: str = None, end_date: str = None) -> list:
    """
    GET /transactions?type=EXPENSE&startDate=...&endDate=...
    Returns: [{ id, amount, note, createdAt, type, category, wallet }]
    """
    params = {}
    if tx_type:
        params["type"]      = tx_type
    if start_date:
        params["startDate"] = start_date
    if end_date:
        params["endDate"]   = end_date

    try:
        res = requests.get(
            f"{SPRING_BOOT_API_URL}/transactions",
            params=params,
            headers=_headers(token),
            timeout=10
        )
        if res.status_code == 200:
            return res.json()
        print(f"Lỗi get transactions: {res.status_code} - {res.text}")
        return []
    except Exception as e:
        print(f"Lỗi kết nối: {e}")
        return []


def get_last_3_months_transactions(token: str) -> dict:
    """Lấy transactions 3 tháng gần nhất để phân tích xu hướng"""
    from datetime import date
    import calendar

    today  = date.today()
    result = {}

    for i in range(3):
        # Tính tháng i tháng trước
        month = today.month - i
        year  = today.year
        if month <= 0:
            month += 12
            year  -= 1

        start, end = _month_range(year, month)
        txs = get_transactions(token, tx_type="EXPENSE",
                               start_date=start, end_date=end)
        result[f"{year}-{month:02d}"] = txs

    return result