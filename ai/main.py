from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.database import Base, engine
from app.api import chat

Base.metadata.create_all(bind=engine)

app = FastAPI(title="Monio AI Service")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(chat.router, tags=["Chat"])

@app.get("/")
def root():
    return {"message": "Monio AI Service đang chạy!"}