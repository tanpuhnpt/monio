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