from PIL import Image
import easyocr
import numpy as np
import io

reader = easyocr.Reader(['en', 'vi'], gpu=False)

def extract_text(image_bytes: bytes) -> str:
    image = Image.open(io.BytesIO(image_bytes)).convert("RGB")
    image_np = np.array(image)
    results = reader.readtext(image_np)
    return "\n".join(text for (_, text, _) in results)