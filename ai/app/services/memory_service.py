from typing import Any

PENDING_EXPENSES: dict[str, dict[str, Any]] = {}


def get_pending_expense(user_id: str) -> dict[str, Any] | None:
    return PENDING_EXPENSES.get(user_id)


def set_pending_expense(user_id: str, pending: dict[str, Any]) -> None:
    PENDING_EXPENSES[user_id] = pending


def clear_pending_expense(user_id: str) -> None:
    PENDING_EXPENSES.pop(user_id, None)
