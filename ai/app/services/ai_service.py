from groq import Groq
from app.core.config import GROQ_API_KEY
from app.services.db_service import save_ai_response, save_user_request
from fastapi import HTTPException
from sqlalchemy.orm import Session
import json
import re
from datetime import datetime
from typing import Any

from app.services.memory_service import clear_pending_expense, get_pending_expense, set_pending_expense

groq_client = Groq(api_key=GROQ_API_KEY)

CATEGORIES = ["Ăn uống", "Di chuyển", "Giải trí", "Khác", "Quà",
              "Mua sắm", "Sức khoẻ", "Sắc đẹp", "Học tập", "Du lịch", "Lương", "Thưởng"]

AMOUNT_ONLY_RE = re.compile(
    r"^[\d\.,\s]+(?:k|K|ngàn|nghìn|vnd|đ|dong)?$",
    re.IGNORECASE
)


def is_amount_only(text: str) -> bool:
    return bool(AMOUNT_ONLY_RE.fullmatch(text.strip()))


def parse_amount(text: str) -> float | None:
    normalized = text.strip().lower().replace(" ", "")
    normalized = normalized.replace("đ", "").replace("vnd", "").replace("dong", "")

    if normalized.endswith("k"):
        base = normalized[:-1].replace(",", ".")
        try:
            return float(base) * 1000
        except ValueError:
            return None

    normalized = normalized.replace(",", "").replace(".", "")
    try:
        return float(normalized)
    except ValueError:
        return None


def _clean_result(result: dict[str, Any]) -> dict[str, Any]:
    return {k: v for k, v in result.items() if k != "raw_text"}


def _extract_expense(text: str) -> dict[str, Any]:
    categories = CATEGORIES
    categories_str = ", ".join(f'"{c}"' for c in categories)
    today = datetime.now().strftime("%Y-%m-%d")

    prompt = f"""
You are an expense extractor. Read the Vietnamese text and return ONLY valid JSON.

DATE RULES:
- If text contains "hôm nay", "nay", "today" → LocalDateTime = {today} 00:00:00
- If text contains "hôm qua" → LocalDateTime = (today - 1 day) 00:00:00
- If date is given without year → use current year {datetime.now().year}
- Do NOT guess dates.

AMOUNT RULES (VERY IMPORTANT):
- If the amount contains "k", "K", "ngàn", "nghìn", "ngàn đồng":
    - Convert: 1k = 1000 VND.
    - Example: "25k" → 25000, "45k" → 45000.
- If multiple amounts are found, SUM them normally after conversion.
- If the amount contains ".", treat it as thousands separator only if appropriate (e.g. "50.5k" is NOT allowed).

IMPORTANT RULES:

- Do NOT guess missing information.
- If the text does NOT clearly contain the amount → "Total": null
- If the text does NOT contain a date expression (e.g. "hôm nay", "hôm qua", "23/6", "1-2") 
    → "LocalDateTime": null 
    → ai_message: "Bạn vui lòng cung cấp ngày giao dịch."
- If the text contains words like "hôm nay", "nay", "today" 
    → LocalDateTime = today's real date using format YYYY-MM-DD 00:00:00.
- If the date is given without year → automatically fill with current year {datetime.now().year}.
- If the text does NOT mention what was purchased → "Note": null
- If category cannot be determined → "Category": null
- If ANY important information is missing (amount / item / date) 
    → ai_message must describe exactly what is missing.

If all info is complete:
- ai_message = "Tôi đã ghi nhận giao dịch của bạn."

OPTION A RULES (MERGE):
- If multiple amounts are found → SUM into "Total".
- Note must summarize all items.
- Category is based on the overall meaning.

Return ONLY this JSON:
{{
  "LocalDateTime": "YYYY-MM-DD HH:MM:SS or null",
  "Total": number or null,
  "Category": one of [{categories_str}] or null,
  "Note": "short description in Vietnamese or null",
  "ai_message": "Vietnamese message"
}}

Text: "{text}"
"""

    resp = groq_client.chat.completions.create(
        model="llama-3.3-70b-versatile",
        messages=[
            {"role": "system", "content": "You only respond with valid JSON. No explanation."},
            {"role": "user", "content": prompt},
        ],
        temperature=0,
        max_tokens=256
    )

    raw_output = resp.choices[0].message.content
    json_match = re.search(r"\{.*?\}", raw_output, re.DOTALL)
    if not json_match:
        raise ValueError("AI không trả về JSON hợp lệ")

    json_str = json_match.group()
    json_str = json_str.replace("'", '"')
    json_str = json_str.replace("None", "null")
    json_str = json_str.replace("True", "true")
    json_str = json_str.replace("False", "false")

    return json.loads(json_str)


async def ask_ai_expense(text: str, user_id: int, db: Session):
    pending = get_pending_expense(str(user_id))

    # Save user request
    save_user_request(db, user_id, text)

    if pending and is_amount_only(text):
        amount_value = parse_amount(text)
        if amount_value is None:
            result = {
                "LocalDateTime": pending.get("LocalDateTime"),
                "Total": None,
                "Category": pending.get("Category"),
                "Note": pending.get("Note"),
                "ai_message": "Số tiền không hợp lệ. Vui lòng nhập lại số tiền đúng định dạng."
            }
            save_ai_response(db, user_id, result["ai_message"])
            return result

        pending["Total"] = amount_value
        description = pending.get("Note") or "giao dịch"
        pending["ai_message"] = f"Đã ghi nhận {description}, tổng số {int(amount_value)}."
        clear_pending_expense(str(user_id))
        save_ai_response(db, user_id, pending["ai_message"])
        return _clean_result(pending)

    if not pending and is_amount_only(text):
        result = {
            "LocalDateTime": None,
            "Total": None,
            "Category": None,
            "Note": None,
            "ai_message": "Bạn cần mô tả chi tiêu trước, ví dụ: 'Hôm nay tôi ăn sáng'."
        }
        save_ai_response(db, user_id, result["ai_message"])
        return result

    merged_text = text
    if pending:
        merged_text = f"{pending.get('raw_text', '')} {text}".strip()

    result = _extract_expense(merged_text)

    if result.get("Total") is None or result.get("LocalDateTime") is None:
        pending_data = {
            "LocalDateTime": result.get("LocalDateTime"),
            "Total": result.get("Total"),
            "Category": result.get("Category"),
            "Note": result.get("Note"),
            "ai_message": result.get("ai_message"),
            "raw_text": merged_text,
        }
        set_pending_expense(str(user_id), pending_data)
        save_ai_response(db, user_id, pending_data["ai_message"])
        return _clean_result(pending_data)

    clear_pending_expense(str(user_id))
    save_ai_response(db, user_id, result["ai_message"])
    return _clean_result(result)


# def ask_groq(raw_text: str) -> dict:
#     categories = CATEGORIES
#     categories_str = ", ".join(f'"{c}"' for c in categories)

#     prompt = f"""
# You are an invoice extractor. The OCR text may contain MULTIPLE bills mixed together.
# Your job is to detect and separate each bill, then extract structured data for each bill.

# OCR TEXT (noisy, may contain duplicates or overlapping bills):
# {raw_text}

# OUTPUT FORMAT:
# Return ONLY a JSON array. Each element MUST follow:
# {{
#   "LocalDateTime": "YYYY-MM-DD HH:MM:SS",
#   "Total": "number only",
#   "Category": "one of the categories"
# }}

# RULES FOR SPLITTING BILLS:
# - A new bill starts when one of these appears:
#   - A new time like “Giờ vào”, “Giờ in”, “HH:MM”, “HH.MM”
#   - A new invoice number “Số: XXXXX”
#   - A new block containing item names and prices
#   - A new ilike “THANH TOAN” , “Tổng”, “Tiền hàng”
# - Ignore duplicated or corrupted OCR lines.

# LOCALDATETIME RULES:
# - Extract date & time near the top of each bill.
# - Accept formats: HH:MM DD/MM/YYYY, HH.MM DD-MM-YYYY, HH:MM, DD/MM/YYYY.
# - If only time exists → use today’s date.
# - If only date exists → time = 00:00:00.
# - If missing → null.

# TOTAL RULES:
# - DO NOT sum numbers.
# - For each bill, choose the amount after to “THANH TOAN”, “Tổng”, “Total”, “Tiền hàng”.
# - If multiple candidates, choose the largest.
# - Remove formatting → return only digits.

# CATEGORY RULES:
# - Infer from item names (coffee, tea, food → Ăn uống).
# - Must be one of: {categories_str}
# - If can't determine → null.

# IMPORTANT:
# - Return ONLY a JSON array.
# - Every element must be one bill.
# - No markdown, no explanation, no extra text.
# """

#     response = groq_client.chat.completions.create(
#         model="llama-3.3-70b-versatile",
#         messages=[
#             {
#                 "role": "system",
#                 "content": "You are a data extraction assistant. You ONLY respond with valid JSON. Never write code. Never explain. Only output the JSON object."
#             },
#             {
#                 "role": "user",
#                 "content": prompt
#             }
#         ],
#         temperature=0,
#         max_tokens=512
#     )
#     print("Raw AI response:", response.choices)
#     raw_response = response.choices[0].message.content
#     print("Raw AI response:", raw_response)
#     match = re.search(r'\{.*?\}', raw_response, re.DOTALL)
#     if not match:
#         raise HTTPException(status_code=500, detail="AI không trả về JSON hợp lệ")

#     json_str = match.group()
#     json_str = json_str.replace("'", '"')
#     json_str = json_str.replace("None", "null")
#     json_str = json_str.replace("True", "true")
#     json_str = json_str.replace("False", "false")

#     try:
#         result = json.loads(json_str)
#     except json.JSONDecodeError as e:
#         raise HTTPException(status_code=500, detail=f"Không parse được JSON: {e}")

#     if result.get("Category") not in categories:
#         result["Category"] = "Khác"

#     return result

def ask_groq(raw_text: str) -> list[dict]:
    categories = CATEGORIES
    categories_str = ", ".join(f'"{c}"' for c in categories)

    prompt = f"""
You are an invoice extractor. The OCR text may contain MULTIPLE bills mixed together.
Your job is to detect and separate each bill, then extract structured data for each bill.

OCR TEXT (noisy, may contain duplicates or overlapping bills):
{raw_text}

OUTPUT FORMAT:
Return ONLY a JSON array. Each element MUST follow:
{{
  "LocalDateTime": "YYYY-MM-DD HH:MM:SS",
  "Total": "number only",
  "Category": "one of the categories"
}}

RULES FOR SPLITTING BILLS:
- A new bill starts when one of these appears:
  - A new time like “Giờ vào”, “Giờ in”, “HH:MM”, “HH.MM”
  - A new invoice number “Số: XXXXX”
  - A new block containing item names and prices
  - A new ilike “THANH TOAN” , “Tổng”, “Tiền hàng”
- Ignore duplicated or corrupted OCR lines.

LOCALDATETIME RULES:
- Extract date & time near the top of each bill.
- Accept formats: HH:MM DD/MM/YYYY, HH.MM DD-MM-YYYY, HH:MM, DD/MM/YYYY.
- If only time exists → use today’s date.
- If only date exists → time = 00:00:00.
- If missing → null.

TOTAL RULES:
- DO NOT sum numbers.
- For each bill, choose the amount after to “THANH TOAN”, “Tổng”, “Total”, “Tiền hàng”.
- If multiple candidates, choose the largest.
- Remove formatting → return only digits.

CATEGORY RULES:
- Infer from item names (coffee, tea, food → Ăn uống).
- Must be one of: {categories_str}
- If can't determine → null.

IMPORTANT:
- Return ONLY a JSON array.
- Every element must be one bill.
- No markdown, no explanation, no extra text.
"""

    response = groq_client.chat.completions.create(
        model="qwen/qwen3-32b",
        messages=[
            {
                "role": "system",
                "content": "You are a data extraction assistant. You ONLY respond with valid JSON array. Never write code. Never explain. Only output the JSON array."
            },
            {
                "role": "user",
                "content": prompt
            }
        ],
        temperature=0.2,
        max_tokens=4096,
        top_p=0.95
    )

    raw_response = response.choices[0].message.content
    print("Raw AI response:", raw_response)

    cleaned = raw_response.strip()
    
    if cleaned.startswith("```"):
        cleaned = re.sub(r'^```(?:json)?\s*', '', cleaned)
        cleaned = re.sub(r'\s*```$', '', cleaned)
        cleaned = cleaned.strip()

    match = re.search(r'\[.*\]', cleaned, re.DOTALL)
    if not match:
        raise HTTPException(
            status_code=500,
            detail=f"AI không trả về JSON array hợp lệ. Raw: {raw_response[:200]}"
        )

    json_str = match.group()
    json_str = json_str.replace("'", '"')
    json_str = json_str.replace("None", "null")
    json_str = json_str.replace("True", "true")
    json_str = json_str.replace("False", "false")

    try:
        results = json.loads(json_str)
    except json.JSONDecodeError as e:
        raise HTTPException(status_code=500, detail=f"Không parse được JSON: {e}")

    if not isinstance(results, list):
        results = [results]

    for bill in results:
        if bill.get("Category") not in categories:
            bill["Category"] = "Khác"

    return results