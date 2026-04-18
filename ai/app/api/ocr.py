from fastapi import APIRouter, UploadFile, File, HTTPException
from app.services.ocr_service import extract_bills_from_image

router = APIRouter()


@router.post("/ocr")
async def ocr_invoice(file: UploadFile = File(...)):
    if file.content_type not in ["image/jpeg", "image/png", "image/webp"]:
        raise HTTPException(status_code=400, detail="Chỉ hỗ trợ JPG, PNG, WEBP")

    contents = await file.read()
    bills    = extract_bills_from_image(contents, file.content_type)

    return {"filename": file.filename, "bills": bills}