from datetime import datetime
from sqlalchemy import Column, Integer, Text, DateTime
from app.core.database import Base

class UserRequest(Base):
    __tablename__ = "user_request"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, index=True, nullable=False)
    message = Column(Text, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)