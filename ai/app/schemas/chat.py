from pydantic import BaseModel
from datetime import datetime
from typing import Optional


class ChatRequest(BaseModel):
    token:   str   # JWT từ FE
    message: str


class ChatResponse(BaseModel):
    intent:  str
    reply:   str
    data:    Optional[dict] = None


class HistoryItem(BaseModel):
    id:         int
    user_id:    int
    role:       str
    message:    str
    created_at: datetime

    class Config:
        from_attributes = True