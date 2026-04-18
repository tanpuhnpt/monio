from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.schemas.chat import ChatRequest, ChatResponse, HistoryItem
from app.services.ai_service import (
    classify_and_reply, get_category_id,
    generate_statistics_reply,  # report
    analyze_and_predict, # analysis
    detect_report_intent,        # 1 hàm thay vì 2
    reply_transaction_list 
)
from app.services.db_service import (
    save_message, get_history, get_full_history,
    save_pending, get_pending, update_pending_wallet, delete_pending
)
from app.services.spring_service import (
    get_user_id_from_token, get_wallets,
    find_wallet_by_name, save_transaction,
    get_full_report, _month_range, _current_month_range, # report
    get_transactions, get_last_3_months_transactions # analysis
)
from datetime import datetime

router = APIRouter()

@router.post("/chat", response_model=ChatResponse)
async def chat(req: ChatRequest, db: Session = Depends(get_db)):

    user_id = get_user_id_from_token(req.token)
    if not user_id:
        raise HTTPException(status_code=401, detail="Token không hợp lệ")

    save_message(db, user_id, "user", req.message)
    history = get_history(db, user_id, limit=10)

    # ── Detect report intent ────────────────────────────────────
    report = detect_report_intent(req.message)
    intent = report.get("intent", "none")

    # ── 1. Query transactions — liệt kê giao dịch ngắn gọn ────
    if intent == "query_transactions":
        start = report.get("start_date")
        end   = report.get("end_date")
        ttype = report.get("tx_type", "BOTH")

        # Lấy transactions từ Spring Boot
        txs = get_transactions(
            token      = req.token,
            tx_type    = None if ttype == "BOTH" else ttype,
            start_date = start,
            end_date   = end
        )

        reply = reply_transaction_list(req.message, txs)
        save_message(db, user_id, "assistant", reply)
        return ChatResponse(
            intent = "query_transactions",
            reply  = reply,
            data   = {"start_date": start, "end_date": end, "count": len(txs)}
        )

    # ── 2. Statistics — tổng hợp theo tháng ───────────────────
    elif intent == "statistics":
        month      = report.get("month") or datetime.now().month
        year       = report.get("year")  or datetime.now().year
        start, end = _month_range(year, month)
        rpt        = get_full_report(req.token, start, end)
        reply      = generate_statistics_reply(req.message, rpt, history)

        save_message(db, user_id, "assistant", reply)
        return ChatResponse(
            intent = "statistics",
            reply  = reply,
            data   = {"period": {"start": start, "end": end},
                      "summary": rpt.get("summary", {})}
        )

    # ── 3. Analysis — phân tích xu hướng + dự đoán ────────────
    elif intent == "analysis":
        txs_by_month   = get_last_3_months_transactions(req.token)
        start, end     = _current_month_range()
        current_report = get_full_report(req.token, start, end)
        reply          = analyze_and_predict(req.message, txs_by_month,
                                             current_report, history)

        save_message(db, user_id, "assistant", reply)
        return ChatResponse(
            intent = "analysis",
            reply  = reply,
            data   = {"period": {"start": start, "end": end}}
        )

    # ── 4. Chat thường ─────────────────────────────────────────
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
                reply = "Bạn chưa chọn ví. Các ví: " \
                        + ", ".join(w["name"] for w in wallets)
                save_message(db, user_id, "assistant", reply)
                return ChatResponse(intent=intent, reply=reply, data=data)

            success = save_transaction(req.token, pending)
            reply   = "Đã lưu giao dịch thành công!" if success \
                      else "Lưu thất bại, vui lòng thử lại."
            if success:
                delete_pending(db, user_id)

    elif intent == "cancel":
        delete_pending(db, user_id)

    save_message(db, user_id, "assistant", reply)
    result["reply"] = reply
    return ChatResponse(intent=intent, reply=reply, data=data)


@router.get("/history/{user_id}", response_model=list[HistoryItem])
def get_chat_history(user_id: int, limit: int = 50, db: Session = Depends(get_db)):
    return get_full_history(db, user_id, limit)