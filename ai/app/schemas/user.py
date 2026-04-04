from datetime import datetime
from pydantic import BaseModel

class UserRequestItem(BaseModel):
    id: int
    user_id: int
    message: str
    created_at: datetime

    class Config:
        orm_mode = True