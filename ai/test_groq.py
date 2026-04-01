from groq import Groq
from app.core.config import GROQ_API_KEY

client = Groq(api_key=GROQ_API_KEY)

try:
    response = client.chat.completions.create(
        model="llama-3.1-8b-instant",
        messages=[{"role": "user", "content": "Say hello"}]
    )
    print("Key hoạt động:", response.choices[0].message.content)
except Exception as e:
    print("Lỗi:", e)