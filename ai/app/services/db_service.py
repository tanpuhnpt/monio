from typing import List
from sqlalchemy.orm import Session

from app.models.user import UserRequest
from app.models.ai import AIResponse


def save_user_request(db: Session, user_id: int, message: str) -> UserRequest:
    record = UserRequest(user_id=user_id, message=message)
    db.add(record)
    db.commit()
    db.refresh(record)
    return record


def save_ai_response(db: Session, user_id: int, response: str) -> AIResponse:
    record = AIResponse(user_id=user_id, response=response)
    db.add(record)
    db.commit()
    db.refresh(record)
    return record


def get_user_requests(db: Session, user_id: int) -> List[UserRequest]:
    return db.query(UserRequest).filter(UserRequest.user_id == user_id).order_by(UserRequest.created_at.desc()).all()


def get_ai_responses(db: Session, user_id: int) -> List[AIResponse]:
    return db.query(AIResponse).filter(AIResponse.user_id == user_id).order_by(AIResponse.created_at.desc()).all()