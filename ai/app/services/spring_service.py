import requests
from app.core.config import SPRING_BOOT_API_URL
from datetime import datetime, timedelta, timezone


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