from groq import Groq
from app.core.config import GROQ_API_KEY
from fastapi import HTTPException
import base64, re, json

groq_client = Groq(api_key=GROQ_API_KEY)

CATEGORIES = ["Ăn uống", "Di chuyển", "Giải trí", "Khác", "Quà",
              "Mua sắm", "Sức khoẻ", "Sắc đẹp", "Học tập", "Du lịch", "Lương", "Thưởng", "Đầu tư", "Thu nhập khác"]


def extract_bills_from_image(image_bytes: bytes, content_type: str) -> list[dict]:
    image_b64      = base64.b64encode(image_bytes).decode("utf-8")
    categories_str = ", ".join(f'"{c}"' for c in CATEGORIES)

    response = groq_client.chat.completions.create(
        model="meta-llama/llama-4-scout-17b-16e-instruct",
        messages=[
            {
                "role": "system",
                "content": "Return ONLY valid JSON array. No explanation."
            },
            {
                "role": "user",
                "content": [
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:{content_type};base64,{image_b64}"
                        }
                    },
                    {
                        "type": "text",
                        "text": f"""Extract all bills from this invoice image.
Return ONLY a JSON array, each element:
{{
  "LocalDateTime": "YYYY-MM-DD HH:MM:SS or null",
  "Total": "number only or null",
  "Category": "one of categories or null"
}}
Categories: {categories_str}"""
                    }
                ]
            }
        ],
        temperature=0.2,
        max_tokens=1024
    )

    raw     = response.choices[0].message.content
    cleaned = re.sub(r'^```(?:json)?\s*|\s*```$', '', raw.strip())
    match   = re.search(r'\[.*\]', cleaned, re.DOTALL)

    if not match:
        raise HTTPException(status_code=500, detail="AI không trả về JSON hợp lệ")

    results = json.loads(match.group())

    if not isinstance(results, list):
        results = [results]

    for bill in results:
        if bill.get("Category") not in CATEGORIES:
            bill["Category"] = "Khác"

    return results