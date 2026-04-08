from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.schemas.chat import ChatRequest, ChatResponse, HistoryItem
from app.services.ai_service import classify_and_reply, get_category_id
from app.services.db_service import (
    save_message, get_history, get_full_history,
    save_pending, get_pending, update_pending_wallet, delete_pending
)
from app.services.spring_service import (
    get_user_id_from_token, get_wallets,
    find_wallet_by_name, save_transaction
)

router = APIRouter()


@router.post("/chat", response_model=ChatResponse)
async def chat(req: ChatRequest, db: Session = Depends(get_db)):

    # 1. Lấy user_id từ JWT
    user_id = get_user_id_from_token(req.token)
    if not user_id:
        raise HTTPException(status_code=401, detail="Token không hợp lệ")

    # 2. Lưu tin nhắn user
    save_message(db, user_id, "user", req.message)

    # 3. Lấy lịch sử + ví
    history = get_history(db, user_id, limit=10)
    wallets = get_wallets(req.token)

    # 4. Gọi AI
    result = classify_and_reply(req.message, history, wallets)
    intent = result["intent"]
    data   = result.get("data") or {}
    reply  = result["reply"]

    # 5. Xử lý theo intent
    if intent in ("add_expense", "add_income"):
        tx_type     = "EXPENSE" if intent == "add_expense" else "INCOME"
        category_id = get_category_id(data.get("Category", "Khác"), tx_type)

        # Tìm ví nếu user đã chỉ định
        wallet_id = None
        if data.get("WalletName"):
            wallet = find_wallet_by_name(req.token, data["WalletName"])
            if wallet:
                wallet_id = wallet["id"]

        save_pending(db, user_id, data, tx_type, category_id, wallet_id)

    elif intent == "update_pending":
        # User sửa lại → xóa pending cũ, tạo mới
        tx_type     = "EXPENSE"  # giữ type cũ hoặc detect lại
        category_id = get_category_id(data.get("Category", "Khác"), tx_type)
        save_pending(db, user_id, data, tx_type, category_id)
        reply = f"Đã cập nhật. {reply}"

    elif intent == "confirm_save":
        pending = get_pending(db, user_id)
        if not pending:
            reply = "Không có giao dịch nào cần lưu."
        else:
            # Tìm ví nếu user chỉ định trong lúc confirm
            if data.get("WalletName") and not pending.wallet_id:
                wallet = find_wallet_by_name(req.token, data["WalletName"])
                if wallet:
                    update_pending_wallet(db, user_id, wallet["id"])
                    pending = get_pending(db, user_id)
                else:
                    # Ví không tồn tại
                    reply = f"Không tìm thấy ví '{data['WalletName']}'. Các ví hiện có: " \
                            + ", ".join(w["name"] for w in wallets)
                    save_message(db, user_id, "assistant", reply)
                    return ChatResponse(intent=intent, reply=reply, data=data)

            # Kiểm tra đã có ví chưa
            if not pending.wallet_id:
                reply = "Bạn chưa chọn ví. Các ví hiện có: " \
                        + ", ".join(w["name"] for w in wallets)
                save_message(db, user_id, "assistant", reply)
                return ChatResponse(intent=intent, reply=reply, data=data)

            # Gọi Spring Boot lưu giao dịch
            success = save_transaction(req.token, pending)
            if success:
                delete_pending(db, user_id)
                reply = "✅ Đã lưu giao dịch thành công!"
            else:
                reply = "❌ Lưu thất bại, vui lòng thử lại."

    elif intent == "cancel":
        delete_pending(db, user_id)

    # 6. Lưu reply AI
    save_message(db, user_id, "assistant", reply)
    result["reply"] = reply

    return ChatResponse(intent=intent, reply=reply, data=data)


@router.get("/history/{user_id}", response_model=list[HistoryItem])
def get_chat_history(user_id: int, limit: int = 50, db: Session = Depends(get_db)):
    return get_full_history(db, user_id, limit)