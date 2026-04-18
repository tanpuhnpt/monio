from groq import Groq
from app.core.config import GROQ_API_KEY
from fastapi import HTTPException
from datetime import datetime
import json, re

groq_client = Groq(api_key=GROQ_API_KEY)

EXPENSE_CATEGORIES = {
    1: "Ăn uống", 2: "Giải trí", 3: "Di chuyển",
    4: "Sức khỏe", 5: "Giáo dục", 6: "Du lịch",
    7: "Quà", 8: "Gia đình", 9: "Khác",
    13: "Hóa đơn & Tiện ích", 14: "Mua sắm"
}
INCOME_CATEGORIES = {10: "Lương", 11: "Thưởng", 12: "Khác"}

EXPENSE_MAPPING = {v: k for k, v in EXPENSE_CATEGORIES.items()}
INCOME_MAPPING  = {v: k for k, v in INCOME_CATEGORIES.items()}
ALL_CATEGORIES  = list(EXPENSE_CATEGORIES.values()) + list(INCOME_CATEGORIES.values())


def get_category_id(category: str, tx_type: str) -> int:
    if tx_type == "INCOME":
        return INCOME_MAPPING.get(category, 12)
    return EXPENSE_MAPPING.get(category, 9)


def classify_and_reply(message: str, history: list, wallets: list) -> dict:
    now   = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    today = datetime.now().strftime("%Y-%m-%d")

    history_text = "\n".join([
        f"{h.role.upper()}: {h.message}" for h in history
    ]) or "Chưa có lịch sử."

    wallets_text = "\n".join([
        f"- id:{w['id']} name:{w['name']} balance:{w['balance']:,} {w['currency']}"
        for w in wallets
    ]) or "Không có ví nào."

    categories_str = ", ".join(f'"{c}"' for c in ALL_CATEGORIES)

    prompt = f"""You are a Vietnamese personal expense tracking chatbot.

NOW = "{now}"
TODAY = "{today}"

CONVERSATION HISTORY:
{history_text}

USER WALLETS:
{wallets_text}

USER: "{message}"

Classify intent and respond. Return ONLY this JSON:
{{
  "intent": "add_expense | add_income | missing_info | confirm_save | update_pending | cancel | query | out_of_scope",
  "reply": "Vietnamese response to user",
  "data": {{
    "LocalDateTime": "YYYY-MM-DD HH:MM:SS or null",
    "Total": number or null,
    "Category": "category name or null",
    "Note": "short note or null",
    "WalletName": "wallet name if user mentioned or null"
  }}
}}

## INTENT RULES

### add_expense
- User nói chi tiêu CÓ số tiền: "ăn sáng 50k", "mua sách 100k", "đổ xăng 200k"
- reply: "Ghi nhận chi tiêu: [mô tả]. Bạn muốn lưu vào ví nào? Các ví: [liệt kê tên ví]"

### add_income  
- User nói nhận tiền CÓ số tiền: "nhận lương 10 triệu", "được thưởng 500k"
- reply: "Ghi nhận thu nhập: [mô tả]. Bạn muốn lưu vào ví nào?"

### missing_info
- Giống add_expense/add_income NHƯNG thiếu số tiền hoặc không rõ
- reply: hỏi lại cho đủ thông tin

### confirm_save
- User đồng ý lưu: "ok", "lưu đi", "đồng ý", "có", "ừ", "yes", "lưu vào ví [tên]"
- Nếu user chỉ định ví trong lúc confirm → điền WalletName
- reply: "Đang lưu giao dịch..."

### update_pending
- User muốn sửa: "lộn rồi", "sửa lại", "không phải", "thực ra là..."
- reply: xác nhận thông tin mới và hỏi lại

### cancel
- User hủy: "thôi", "không lưu", "hủy", "cancel"
- reply: "Đã hủy giao dịch."

### query
- User hỏi về chi tiêu, số dư ví
- reply: trả lời dựa trên wallet data

### out_of_scope
- Ngoài phạm vi chi tiêu
- reply: "Tôi chỉ hỗ trợ quản lý chi tiêu cá nhân."

## DATE RULES
- "hôm nay", "nay", không nói ngày → LocalDateTime = "{now}"
- "hôm qua" → ngày hôm qua + "00:00:00"
- Nói giờ cụ thể "7h sáng" → TODAY + "07:00:00"
- Nói ngày cụ thể → dùng ngày đó + "00:00:00"

## AMOUNT RULES  
- "10k" = 10000, "50k" = 50000
- "1 triệu" = 1000000, "1.5 triệu" = 1500000
- "10 nghìn" = 10000

## CATEGORY RULES
Categories: {categories_str}
- Tự động chọn category phù hợp nhất
- "ăn sáng/trưa/tối/uống cà phê" → "Ăn uống"
- "grab/taxi/xe buýt/xăng" → "Di chuyển"
- "lương/thưởng" → income categories

Always reply in Vietnamese. Return ONLY valid JSON.
"""

    response = groq_client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[
            {"role": "system", "content": "You are an expense assistant. Return ONLY valid JSON. No explanation."},
            {"role": "user",   "content": prompt}
        ],
        temperature=0,
        max_tokens=512
    )

    raw = response.choices[0].message.content
    print(f"AI raw: {raw}")

    match = re.search(r'\{.*\}', raw, re.DOTALL)
    if not match:
        raise HTTPException(status_code=500, detail="AI không trả về JSON hợp lệ")

    result = json.loads(match.group())

    # Validate category
    data = result.get("data") or {}
    if data.get("Category") not in ALL_CATEGORIES:
        data["Category"] = "Khác"
    result["data"] = data

    return result

def generate_statistics_reply(message: str, report_data: dict, history: list) -> str:
    """Dùng AI để phân tích report data và trả lời bằng tiếng Việt tự nhiên"""

    history_text = "\n".join([
        f"{h.role.upper()}: {h.message}" for h in history[-6:]
    ]) or "Chưa có lịch sử."

    summary           = report_data.get("summary", {})
    expense_by_cat    = report_data.get("expense_by_category", [])
    income_by_cat     = report_data.get("income_by_category", [])
    expense_by_wallet = report_data.get("expense_by_wallet", [])
    period            = report_data.get("period", {})

    prompt = f"""You are a Vietnamese personal finance advisor.

PERIOD: {period.get('start')} → {period.get('end')}

FINANCIAL SUMMARY:
- Tổng thu:  {summary.get('totalIncome', 0):,.0f}đ
- Tổng chi:  {summary.get('totalExpense', 0):,.0f}đ
- Còn lại:   {summary.get('netBalance', 0):,.0f}đ

CHI TIÊU THEO DANH MỤC:
{chr(10).join([f"- {c['name']}: {c['totalAmount']:,.0f}đ ({c['percentage']}%)" for c in expense_by_cat]) or "Không có dữ liệu"}

THU NHẬP THEO DANH MỤC:
{chr(10).join([f"- {c['name']}: {c['totalAmount']:,.0f}đ ({c['percentage']}%)" for c in income_by_cat]) or "Không có dữ liệu"}

CHI TIÊU THEO VÍ:
{chr(10).join([f"- {w['name']}: {w['totalAmount']:,.0f}đ ({w['percentage']}%)" for w in expense_by_wallet]) or "Không có dữ liệu"}

CONVERSATION HISTORY:
{history_text}

USER QUESTION: "{message}"

Answer the user's question in Vietnamese based on the data above.
Then add 2-3 specific saving tips based on their spending pattern.
Format nicely with emoji. Be friendly and concise.
"""

    response = groq_client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[
            {"role": "system", "content": "You are a helpful Vietnamese financial advisor."},
            {"role": "user",   "content": prompt}
        ],
        temperature=0.3,
        max_tokens=800
    )

    return response.choices[0].message.content

# ── Chat Report APIs ───────────────────────────────────────────────
# def detect_statistics_intent(message: str) -> dict:
#     """
#     Phát hiện intent thống kê và trích xuất tháng/năm từ message
#     Returns: { is_stats: bool, month: int, year: int, tx_type: str }
#     """
#     now = datetime.now()

#     prompt = f"""Extract time range from this Vietnamese message about expense statistics.

# Message: "{message}"
# Current date: {now.strftime("%Y-%m-%d")}

# Return ONLY JSON:
# {{
#   "is_statistics": true | false,
#   "month": number or null,
#   "year": number or null,
#   "tx_type": "EXPENSE" | "INCOME" | "BOTH",
#   "specific_category": "category name or null"
# }}

# Rules:
# - is_statistics = true nếu user hỏi về thống kê, báo cáo, đã chi bao nhiêu, tháng này chi gì
# - "tháng này" → month={now.month}, year={now.year}
# - "tháng 12" → month=12, year={now.year}
# - "tháng 12 năm ngoái" → month=12, year={now.year - 1}
# - tx_type = "EXPENSE" nếu hỏi về chi tiêu, "INCOME" nếu hỏi thu nhập, "BOTH" nếu hỏi cả hai
# - specific_category: nếu hỏi về danh mục cụ thể như "ăn uống", "di chuyển"
# """

#     response = groq_client.chat.completions.create(
#         model="llama-3.3-70b-versatile",
#         messages=[
#             {"role": "system", "content": "Return ONLY valid JSON."},
#             {"role": "user",   "content": prompt}
#         ],
#         temperature=0,
#         max_tokens=150
#     )

#     raw = response.choices[0].message.content.strip()
#     match = re.search(r'\{.*\}', raw, re.DOTALL)
#     if not match:
#         return {"is_statistics": False}

#     return json.loads(match.group())

# ── Analyze and Predict Trend Expense APIs ───────────────────────────────────────────────
# def detect_analysis_intent(message: str) -> dict:
#     """Phát hiện user muốn phân tích/dự đoán chi tiêu"""
#     now = datetime.now()

#     prompt = f"""Detect if this Vietnamese message is about expense analysis or prediction.

# Message: "{message}"
# Current date: {now.strftime("%Y-%m-%d")}

# Return ONLY JSON:
# {{
#   "is_analysis": true | false,
#   "analysis_type": "analyze | predict | both",
#   "month": number or null,
#   "year": number or null,
#   "tx_type": "EXPENSE" | "INCOME" | "BOTH"
# }}

# is_analysis = true khi user hỏi:
# - phân tích chi tiêu, xu hướng, thói quen chi tiêu
# - dự đoán tháng tới, tháng sau sẽ chi bao nhiêu
# - so sánh tháng này vs tháng trước
# - tôi có đang chi tiêu hợp lý không
# - gợi ý cải thiện tài chính
# - AI thấy chi tiêu của tôi thế nào
# """

#     response = groq_client.chat.completions.create(
#         model="llama-3.3-70b-versatile",
#         messages=[
#             {"role": "system", "content": "Return ONLY valid JSON."},
#             {"role": "user",   "content": prompt}
#         ],
#         temperature=0,
#         max_tokens=150
#     )

#     raw   = response.choices[0].message.content.strip()
#     match = re.search(r'\{.*\}', raw, re.DOTALL)
#     if not match:
#         return {"is_analysis": False}
#     return json.loads(match.group())


def analyze_and_predict(message: str, transactions_by_month: dict,
                        current_report: dict, history: list) -> str:
    """
    Phân tích xu hướng chi tiêu 3 tháng + dự đoán tháng tới
    transactions_by_month: { "2026-04": [...], "2026-03": [...], "2026-02": [...] }
    """
    history_text = "\n".join([
        f"{h.role.upper()}: {h.message}" for h in history[-6:]
    ]) or "Chưa có."

    # Format transactions từng tháng
    def fmt_month(month_key: str, txs: list) -> str:
        if not txs:
            return f"\n{month_key}: Không có dữ liệu"

        # Group by category
        by_cat = {}
        for tx in txs:
            cat = tx.get("category", {}).get("name", "Khác")
            by_cat[cat] = by_cat.get(cat, 0) + tx.get("amount", 0)

        total   = sum(tx.get("amount", 0) for tx in txs)
        cat_str = "\n".join([
            f"  • {cat}: {amt:,.0f}đ" for cat, amt in
            sorted(by_cat.items(), key=lambda x: x[1], reverse=True)
        ])
        return f"\n{month_key} (Tổng: {total:,.0f}đ, {len(txs)} giao dịch):\n{cat_str}"

    months_text = "".join([
        fmt_month(k, v)
        for k, v in sorted(transactions_by_month.items(), reverse=True)
    ])

    # Summary tháng hiện tại
    summary = current_report.get("summary", {})
    summary_text = f"""
- Tổng thu:  {summary.get('totalIncome', 0):,.0f}đ
- Tổng chi:  {summary.get('totalExpense', 0):,.0f}đ
- Còn lại:   {summary.get('netBalance', 0):,.0f}đ
""" if summary else "Không có dữ liệu tháng này."

    # Lấy các giao dịch lớn bất thường
    all_txs   = [tx for txs in transactions_by_month.values() for tx in txs]
    avg_amt   = sum(tx["amount"] for tx in all_txs) / len(all_txs) if all_txs else 0
    big_txs   = sorted(
        [tx for tx in all_txs if tx["amount"] > avg_amt * 2],
        key=lambda x: x["amount"], reverse=True
    )[:5]
    big_txs_text = "\n".join([
        f"  • {tx['note']} ({tx['category']['name']}): {tx['amount']:,.0f}đ — {tx['createdAt']}"
        for tx in big_txs
    ]) or "Không có giao dịch bất thường."

    prompt = f"""You are an expert Vietnamese personal finance analyst.

USER QUESTION: "{message}"

CONVERSATION HISTORY:
{history_text}

=== SPENDING DATA (Last 3 months) ===
{months_text}

=== CURRENT MONTH SUMMARY ===
{summary_text}

=== LARGE/UNUSUAL TRANSACTIONS ===
{big_txs_text}

Based on this data, provide:

1. 📊 PHÂN TÍCH XU HƯỚNG
   - Tháng nào chi nhiều nhất? Danh mục nào chiếm nhiều nhất?
   - So sánh các tháng với nhau
   - Thói quen chi tiêu nổi bật

2. 🔮 DỰ ĐOÁN THÁNG TỚI
   - Ước tính tổng chi tiêu tháng tới dựa trên xu hướng
   - Danh mục nào có khả năng tăng/giảm
   - Đưa ra con số cụ thể

3. ⚠️ CẢNH BÁO
   - Giao dịch bất thường nếu có
   - Danh mục đang chi vượt mức

4. 💡 GỢI Ý CẢI THIỆN
   - 3 hành động cụ thể để tối ưu chi tiêu
   - Mục tiêu tiết kiệm thực tế

Answer in Vietnamese. Be specific with numbers. Use emoji for readability.
If not enough data (only 1 month), still analyze what you have and note the limitation.
"""

    response = groq_client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[
            {"role": "system", "content": "You are a helpful Vietnamese financial analyst."},
            {"role": "user",   "content": prompt}
        ],
        temperature=0.3,
        max_tokens=1200
    )

    return response.choices[0].message.content

def detect_report_intent(message: str) -> dict:
    """
    Phân loại rõ 3 loại intent liên quan đến báo cáo:
    - query_transactions : hỏi giao dịch cụ thể (ngày, tuần)
    - statistics         : thống kê tổng hợp (tháng, danh mục)
    - analysis           : phân tích xu hướng, dự đoán, gợi ý
    - none               : không liên quan báo cáo
    """
    now   = datetime.now()
    today = now.strftime("%Y-%m-%d")

    prompt = f"""Classify this Vietnamese financial message into exactly ONE category.

Message: "{message}"
Today: {today}

Return ONLY JSON:
{{
  "intent": "query_transactions | statistics | analysis | none",
  "start_date": "YYYY-MM-DD or null",
  "end_date": "YYYY-MM-DD or null",
  "month": number or null,
  "year": number or null,
  "tx_type": "EXPENSE | INCOME | BOTH"
}}

## CLASSIFICATION RULES (pick the BEST match):

### query_transactions
User asks about SPECIFIC transactions in a specific short time range.
Keywords: "ngày X", "hôm nay chi gì", "hôm qua mua gì", "tuần này mua gì",
          "giao dịch ngày", "tôi đã chi gì ngày", "liệt kê"
→ start_date = end_date = that specific date (YYYY-MM-DD)
→ Reply should be a SHORT LIST of transactions

### statistics  
User asks for a SUMMARY/TOTAL for a period (usually a month).
Keywords: "tháng này chi bao nhiêu", "thống kê", "tổng chi", "tháng X chi gì",
          "tháng này tôi đã chi", "chi tiêu tháng", "báo cáo tháng"
→ month + year extracted from message
→ Reply should show totals by category

### analysis
User asks for TREND ANALYSIS, PREDICTION, or ADVICE.
Keywords: "phân tích", "dự đoán", "xu hướng", "tháng tới chi bao nhiêu",
          "tôi có đang chi hợp lý không", "gợi ý tiết kiệm", "AI thấy thế nào",
          "cải thiện tài chính", "so sánh các tháng"
→ Requires multi-month data
→ Reply should be deep analysis + prediction

### none
Not related to reports at all → handle as normal chat

## DATE EXTRACTION:
- "hôm nay" → start_date = end_date = "{today}"
- "hôm qua" → start_date = end_date = yesterday
- "ngày 6/4/2026" → start_date = end_date = "2026-04-06"
- "tuần này" → start_date = Monday of current week, end_date = "{today}"
- "tháng này" → month = {now.month}, year = {now.year}
- "tháng 12" → month = 12, year = {now.year}

IMPORTANT: "ngày X tôi chi gì" is ALWAYS query_transactions, NEVER analysis.
"""

    response = groq_client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[
            {"role": "system", "content": "Return ONLY valid JSON. No explanation."},
            {"role": "user",   "content": prompt}
        ],
        temperature=0,
        max_tokens=200
    )

    raw   = response.choices[0].message.content.strip()
    match = re.search(r'\{.*\}', raw, re.DOTALL)
    if not match:
        return {"intent": "none"}
    return json.loads(match.group())

def reply_transaction_list(message: str, transactions: list) -> str:
    """Trả lời ngắn gọn danh sách giao dịch theo ngày"""

    if not transactions:
        return "Không tìm thấy giao dịch nào trong khoảng thời gian này. 🔍"

    # Format danh sách giao dịch
    tx_lines = []
    total = 0
    for tx in transactions:
        amount   = tx.get("amount", 0)
        note     = tx.get("note", "")
        cat      = tx.get("category", {}).get("name", "Khác")
        time_str = tx.get("createdAt", "")[:10]
        tx_type  = tx.get("type", "EXPENSE")
        icon     = "💸" if tx_type == "EXPENSE" else "💰"
        total   += amount if tx_type == "EXPENSE" else 0
        tx_lines.append(f"{icon} {note} ({cat}): {amount:,.0f}đ")

    tx_text = "\n".join(tx_lines)

    prompt = f"""User asked: "{message}"

Transactions found:
{tx_text}

Total expense: {total:,.0f}đ

Reply in Vietnamese with a SHORT, friendly summary.
Format:
- Start with the date/period
- List each transaction simply  
- End with total
- Max 5 lines, no analysis, no prediction, no tips
"""

    response = groq_client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[
            {"role": "system", "content": "Reply briefly in Vietnamese. No analysis needed."},
            {"role": "user",   "content": prompt}
        ],
        temperature=0.1,
        max_tokens=300
    )

    return response.choices[0].message.content