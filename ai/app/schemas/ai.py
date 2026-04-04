from datetime import datetime
from pydantic import BaseModel

class AIResponseItem(BaseModel):
    id: int
    user_id: int
    response: str
    created_at: datetime

    class Config:
        orm_mode = True