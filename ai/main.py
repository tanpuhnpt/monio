from fastapi import FastAPI
from app.api import ocr

app = FastAPI(title="Invoice OCR API", version="1.0")
app.include_router(ocr.router, tags=["OCR"])
