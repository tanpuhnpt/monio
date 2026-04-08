from dotenv import load_dotenv
import os

load_dotenv()

GROQ_API_KEY       = os.getenv("GROQ_API_KEY")
DATABASE_URL       = os.getenv("DATABASE_URL")
SPRING_BOOT_API_URL = os.getenv("SPRING_BOOT_API_URL")