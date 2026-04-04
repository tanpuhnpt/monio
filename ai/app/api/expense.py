from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from app.schemas.expense import (
    ExpenseTextRequest,
    ExpenseAIResponse,
)
from app.schemas.ai import AIResponseItem
from app.schemas.user import UserRequestItem
from app.services.ai_service import ask_ai_expense
from app.services.db_service import get_user_requests, get_ai_responses
from app.core.database import get_db


router = APIRouter()


@router.post("/add-expense-text", response_model=ExpenseAIResponse)
async def add_expense_text(payload: ExpenseTextRequest, db: Session = Depends(get_db)):
    return await ask_ai_expense(payload.text, payload.user_id, db)


@router.get("/requests/{user_id}", response_model=list[UserRequestItem])
def list_user_requests(user_id: int, db: Session = Depends(get_db)):
    return get_user_requests(db, user_id)


@router.get("/responses/{user_id}", response_model=list[AIResponseItem])
def list_ai_responses(user_id: int, db: Session = Depends(get_db)):
    return get_ai_responses(db, user_id)