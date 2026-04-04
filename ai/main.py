from fastapi import FastAPI
from app.api import expense, ocr
from app.core.database import engine, Base

app = FastAPI(title="Personal Financial Management AI", version="1.0")
Base.metadata.create_all(bind=engine)
app.include_router(ocr.router, tags=["OCR"])
app.include_router(expense.router, tags=["Expense"])