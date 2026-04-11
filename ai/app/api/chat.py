from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.schemas.chat import ChatRequest, ChatResponse, HistoryItem
from app.services.ai_service import (
    classify_and_reply, get_category_id,
    generate_statistics_reply, detect_statistics_intent  # report
)
from app.services.db_service import (
    save_message, get_history, get_full_history,
    save_pending, get_pending, update_pending_wallet, delete_pending
)
from app.services.spring_service import (
    get_user_id_from_token, get_wallets,
    find_wallet_by_name, save_transaction,
    get_full_report, _month_range, _current_month_range # report
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

    # 3. Lấy lịch sử
    history = get_history(db, user_id, limit=10)

    # ── Kiểm tra statistics intent TRƯỚC khi gọi classify ─────
    stats_intent = detect_statistics_intent(req.message)

    if stats_intent.get("is_statistics"):
        month = stats_intent.get("month") or datetime.now().month
        year  = stats_intent.get("year")  or datetime.now().year

        start_date, end_date = _month_range(year, month)
        report_data = get_full_report(req.token, start_date, end_date)

        reply = generate_statistics_reply(req.message, report_data, history)
        save_message(db, user_id, "assistant", reply)

        return ChatResponse(
            intent = "statistics",
            reply  = reply,
            data   = {
                "period":  {"start": start_date, "end": end_date},
                "summary": report_data.get("summary", {})
            }
        )

    # ── Luồng chat thường (giữ nguyên như cũ) ─────────────────
    wallets = get_wallets(req.token)
    result  = classify_and_reply(req.message, history, wallets)
    intent  = result["intent"]
    data    = result.get("data") or {}
    reply   = result["reply"]

    if intent in ("add_expense", "add_income"):
        tx_type     = "EXPENSE" if intent == "add_expense" else "INCOME"
        category_id = get_category_id(data.get("Category", "Khác"), tx_type)
        wallet_id   = None
        if data.get("WalletName"):
            wallet = find_wallet_by_name(req.token, data["WalletName"])
            if wallet:
                wallet_id = wallet["id"]
        save_pending(db, user_id, data, tx_type, category_id, wallet_id)

    elif intent == "update_pending":
        tx_type     = "EXPENSE"
        category_id = get_category_id(data.get("Category", "Khác"), tx_type)
        save_pending(db, user_id, data, tx_type, category_id)

    elif intent == "confirm_save":
        pending = get_pending(db, user_id)
        if not pending:
            reply = "Không có giao dịch nào cần lưu."
        else:
            if data.get("WalletName") and not pending.wallet_id:
                wallet = find_wallet_by_name(req.token, data["WalletName"])
                if wallet:
                    update_pending_wallet(db, user_id, wallet["id"])
                    pending = get_pending(db, user_id)
                else:
                    reply = f"Không tìm thấy ví '{data['WalletName']}'. " \
                            f"Các ví: {', '.join(w['name'] for w in wallets)}"
                    save_message(db, user_id, "assistant", reply)
                    return ChatResponse(intent=intent, reply=reply, data=data)

            if not pending.wallet_id:
                reply = "Bạn chưa chọn ví. Các ví hiện có: " \
                        + ", ".join(w["name"] for w in wallets)
                save_message(db, user_id, "assistant", reply)
                return ChatResponse(intent=intent, reply=reply, data=data)

            success = save_transaction(req.token, pending)
            if success:
                delete_pending(db, user_id)
                reply = "Đã lưu giao dịch thành công!"
            else:
                reply = "Lưu thất bại, vui lòng thử lại."

    elif intent == "cancel":
        delete_pending(db, user_id)

    save_message(db, user_id, "assistant", reply)
    result["reply"] = reply

    return ChatResponse(intent=intent, reply=reply, data=data)


@router.get("/history/{user_id}", response_model=list[HistoryItem])
def get_chat_history(user_id: int, limit: int = 50, db: Session = Depends(get_db)):
    return get_full_history(db, user_id, limit)