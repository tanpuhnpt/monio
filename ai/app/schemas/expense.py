from pydantic import BaseModel

class ExpenseTextRequest(BaseModel):
    text: str

class ExpenseAIResponse(BaseModel):
    LocalDateTime: str | None
    Total: float | None
    Category: str | None
    Note: str | None
    ai_message: str