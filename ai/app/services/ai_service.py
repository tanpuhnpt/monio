from groq import Groq
from app.core.config import GROQ_API_KEY
import json
import re

groq_client = Groq(api_key=GROQ_API_KEY)

def ask_groq(raw_text: str) -> dict:
    categories = ["Ăn uống", "Di chuyển", "Giải trí", "Khác", "Quà",
                  "Mua sắm", "Sức khoẻ", "Sắc đẹp", "Học tập", "Du lịch", "Lương", "Thưởng"]
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