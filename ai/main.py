from fastapi import FastAPI
from app.api import expense, ocr

app = FastAPI(title="Personal Financial Management AI", version="1.0")
app.include_router(ocr.router, tags=["OCR"])
app.include_router(expense.router, tags=["Expense"])