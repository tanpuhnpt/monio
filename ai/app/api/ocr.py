from fastapi import APIRouter, UploadFile, File, HTTPException
from app.services.ocr_service import extract_text
from app.services.ai_service import ask_groq

router = APIRouter()

@router.post("/extract")
async def extract_invoice(file: UploadFile = File(...)):
    if file.content_type not in ["image/jpeg", "image/png", "image/webp"]:
        raise HTTPException(status_code=400, detail="Chỉ hỗ trợ JPG, PNG, WEBP")

    contents = await file.read()
    raw_text = extract_text(contents)
    extracted = ask_groq(raw_text)

    return {
        "filename": file.filename,
        "raw_text": raw_text,
        "extracted": extracted
    }