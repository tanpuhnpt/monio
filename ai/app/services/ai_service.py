from groq import Groq
from app.core.config import GROQ_API_KEY
import json
import re
from datetime import datetime

groq_client = Groq(api_key=GROQ_API_KEY)

CATEGORIES = ["Ăn uống", "Di chuyển", "Giải trí", "Khác", "Quà",
              "Mua sắm", "Sức khoẻ", "Sắc đẹp", "Học tập", "Du lịch", "Lương", "Thưởng"]

def ask_groq(raw_text: str) -> dict:
    categories = CATEGORIES
    categories_str = ", ".join(f'"{c}"' for c in categories)

    prompt = f"""Extract information from this OCR invoice text and return ONLY a JSON object.

OCR Text:
{raw_text}

Return ONLY this JSON structure, nothing else, no explanation, no code:
{{
  "LocalDateTime": "YYYY-MM-DD HH:MM:SS",
  "Total": "number only",
  "Category": "one of the categories"
}}

Rules:
- LocalDateTime: invoice date/time in format YYYY-MM-DD HH:MM:SS, use 00:00:00 if no time found
- Total: total amount as number only, no currency symbols, no commas
- Category: must be exactly one of: {categories_str}
- If a field is not found, use null
- Return ONLY the JSON, no markdown, no explanation, no code
"""

    response = groq_client.chat.completions.create(
        model="llama-3.3-70b-versatile",  # đổi model mạnh hơn, hiểu lệnh tốt hơn
        messages=[
            {
                "role": "system",
                "content": "You are a data extraction assistant. You ONLY respond with valid JSON. Never write code. Never explain. Only output the JSON object."
            },
            {
                "role": "user",
                "content": prompt
            }
        ],
        temperature=0,
        max_tokens=256
    )

    raw_response = response.choices[0].message.content
    print("AI response:", raw_response)  # debug

    # Tìm JSON trong response
    match = re.search(r'\{.*?\}', raw_response, re.DOTALL)
    if not match:
        raise HTTPException(status_code=500, detail="AI không trả về JSON hợp lệ")

    json_str = match.group()
    json_str = json_str.replace("'", '"')
    json_str = json_str.replace("None", "null")
    json_str = json_str.replace("True", "true")
    json_str = json_str.replace("False", "false")

    try:
        result = json.loads(json_str)
    except json.JSONDecodeError as e:
        raise HTTPException(status_code=500, detail=f"Không parse được JSON: {e}")

    if result.get("Category") not in categories:
        result["Category"] = "Khác"

    return result


async def ask_ai_expense(text: str):
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
    json_str = re.search(r"\{.*?\}", raw_output, re.DOTALL).group()
    return json.loads(json_str)