from sqlalchemy import Column, BigInteger, String, Text, DateTime, Float, Integer
from datetime import datetime
from app.core.database import Base


class ChatHistory(Base):
    __tablename__ = "chat_history"

    id         = Column(BigInteger, primary_key=True, autoincrement=True)
    user_id    = Column(BigInteger, nullable=False, index=True)
    role       = Column(String(10), nullable=False)  # "user" | "assistant"
    message    = Column(Text, nullable=False)
    created_at = Column(DateTime, default=datetime.now)


class PendingTransaction(Base):
    __tablename__ = "pending_transactions"

    id          = Column(BigInteger, primary_key=True, autoincrement=True)
    user_id     = Column(BigInteger, nullable=False, index=True)
    amount      = Column(Float, nullable=False)
    note        = Column(String(255), nullable=True)
    tx_type     = Column(String(10), nullable=False)   # "EXPENSE" | "INCOME"
    category_id = Column(Integer, nullable=False)
    wallet_id   = Column(Integer, nullable=True)       # null = chưa chọn ví
    tx_datetime = Column(DateTime, nullable=False)     # thời gian giao dịch
    created_at  = Column(DateTime, default=datetime.now)