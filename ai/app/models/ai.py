from datetime import datetime
from sqlalchemy import Column, Integer, Text, DateTime
from app.core.database import Base

class AIResponse(Base):
    __tablename__ = "ai_response"

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, index=True, nullable=False)
    response = Column(Text, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow, nullable=False)