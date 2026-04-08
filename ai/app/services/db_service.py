from sqlalchemy.orm import Session
from app.models.chat import ChatHistory, PendingTransaction
from datetime import datetime


# ── Chat History ──────────────────────────────────────────────

def save_message(db: Session, user_id: int, role: str, message: str):
    msg = ChatHistory(user_id=user_id, role=role, message=message)
    db.add(msg)
    db.commit()


def get_history(db: Session, user_id: int, limit: int = 10) -> list:
    rows = db.query(ChatHistory)\
        .filter(ChatHistory.user_id == user_id)\
        .order_by(ChatHistory.created_at.desc())\
        .limit(limit).all()
    return list(reversed(rows))


def get_full_history(db: Session, user_id: int, limit: int = 50) -> list:
    return db.query(ChatHistory)\
        .filter(ChatHistory.user_id == user_id)\
        .order_by(ChatHistory.created_at.asc())\
        .limit(limit).all()


# ── Pending Transaction ───────────────────────────────────────

def save_pending(db: Session, user_id: int, data: dict,
                 tx_type: str, category_id: int,
                 wallet_id: int = None) -> PendingTransaction:
    # Xóa pending cũ của user trước khi tạo mới
    db.query(PendingTransaction)\
        .filter(PendingTransaction.user_id == user_id)\
        .delete()

    tx_dt = datetime.strptime(data["LocalDateTime"], "%Y-%m-%d %H:%M:%S") \
            if data.get("LocalDateTime") else datetime.now()

    row = PendingTransaction(
        user_id     = user_id,
        amount      = float(data["Total"]),
        note        = data.get("Note", ""),
        tx_type     = tx_type,
        category_id = category_id,
        wallet_id   = wallet_id,
        tx_datetime = tx_dt
    )
    db.add(row)
    db.commit()
    db.refresh(row)
    return row


def get_pending(db: Session, user_id: int) -> PendingTransaction | None:
    return db.query(PendingTransaction)\
        .filter(PendingTransaction.user_id == user_id)\
        .order_by(PendingTransaction.created_at.desc())\
        .first()


def update_pending_wallet(db: Session, user_id: int, wallet_id: int):
    pending = get_pending(db, user_id)
    if pending:
        pending.wallet_id = wallet_id
        db.commit()


def delete_pending(db: Session, user_id: int):
    db.query(PendingTransaction)\
        .filter(PendingTransaction.user_id == user_id)\
        .delete()
    db.commit()