from fastapi import APIRouter
from app.schemas.expense import ExpenseTextRequest
from app.services.ai_service import ask_ai_expense

router = APIRouter()

@router.post("/add-expense-text")
async def add_expense_text(payload: ExpenseTextRequest):
    return await ask_ai_expense(payload.text)