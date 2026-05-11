import io
import re
import json
import numpy as np
from groq import Groq
from pydub import AudioSegment
import noisereduce as nr
from app.core.config import GROQ_API_KEY

groq_client = Groq(api_key=GROQ_API_KEY)

BANNED_WORDS = [
    "đụ", "địt", "lồn", "cặc", "đéo", "mẹ kiếp", "dmm",
    "fuck", "shit", "bitch", "ass", "damn"
]

MIN_TEXT_LENGTH = 3

CONTENT_TYPE_MAP = {
    "audio/webm":  "audio.webm",
    "audio/mp4":   "audio.mp4",
    "audio/mpeg":  "audio.mp3",
    "audio/mp3":   "audio.mp3",
    "audio/wav":   "audio.wav",
    "audio/wave":  "audio.wav",
    "audio/ogg":   "audio.ogg",
    "audio/flac":  "audio.flac",
    "audio/m4a":   "audio.m4a",
    "audio/x-m4a": "audio.m4a",
    "video/mp4":   "audio.mp4",
    "video/webm":  "audio.webm",
}

SUPPORTED_EXTENSIONS = {
    ".webm", ".mp4", ".mp3",
    ".wav", ".ogg", ".flac", ".m4a"
}


# ── Preprocessing ──────────────────────────────────────────────

def preprocess_audio(audio_bytes: bytes, content_type: str = None) -> bytes:
    """
    Xử lý audio trước khi đưa vào Whisper:
    1. Convert về mono, 16kHz
    2. Normalize âm lượng
    3. Khử noise
    Trả về bytes WAV đã xử lý
    """
    print("[Voice] Preprocessing audio...")

    # Detect format từ content_type
    fmt = "webm"  # fallback
    if content_type:
        mapped = CONTENT_TYPE_MAP.get(content_type, "audio.webm")
        fmt = mapped.rsplit(".", 1)[-1]

    try:
        # Load audio từ bytes
        audio = AudioSegment.from_file(
            io.BytesIO(audio_bytes),
            format=fmt
        )
    except Exception:
        # Nếu detect format sai → thử webm
        try:
            audio = AudioSegment.from_file(
                io.BytesIO(audio_bytes),
                format="webm"
            )
        except Exception as e:
            print(f"[Voice] Không load được audio: {e}, bỏ qua preprocessing")
            return audio_bytes

    # 1. Convert về mono + 16kHz + normalize
    audio = audio.set_channels(1).set_frame_rate(16000).normalize()
    duration = len(audio) / 1000
    print(f"[Voice] Duration: {duration:.1f}s")

    # 2. Chuyển sang numpy để khử noise
    samples = np.array(audio.get_array_of_samples()).astype(np.float32)
    samples = samples / (np.max(np.abs(samples)) + 1e-8)

    # 3. Noise reduction
    print("[Voice] Reducing noise...")
    try:
        reduced = nr.reduce_noise(
            y             = samples,
            sr            = 16000,
            stationary    = False,
            prop_decrease = 0.45
        )
    except Exception as e:
        print(f"[Voice] Noise reduction lỗi: {e}, bỏ qua")
        reduced = samples

    # 4. Chuyển lại về AudioSegment
    reduced_int16 = (reduced * 32767).astype(np.int16)
    audio_clean   = AudioSegment(
        reduced_int16.tobytes(),
        frame_rate   = 16000,
        sample_width = 2,
        channels     = 1
    )

    # 5. Export ra WAV bytes
    output = io.BytesIO()
    audio_clean.export(output, format="wav")
    output.seek(0)

    print("[Voice] Preprocessing done!")
    return output.read()


# ── Transcribe ─────────────────────────────────────────────────

def transcribe_audio(audio_bytes: bytes,
                     filename: str     = None,
                     content_type: str = None) -> str:
    """
    Pipeline hoàn chỉnh:
    audio_bytes → preprocess → Groq Whisper → text
    """

    # 1. Preprocessing
    processed_bytes = preprocess_audio(audio_bytes, content_type)

    # 2. Xác định tên file cho Groq
    if filename and "." in filename:
        ext = "." + filename.rsplit(".", 1)[-1].lower()
        resolved_filename = filename if ext in SUPPORTED_EXTENSIONS \
                           else "audio.wav"
    elif content_type and content_type in CONTENT_TYPE_MAP:
        resolved_filename = CONTENT_TYPE_MAP[content_type]
    else:
        resolved_filename = "audio.wav"

    # Sau preprocessing luôn là WAV
    resolved_filename = resolved_filename.rsplit(".", 1)[0] + ".wav"

    print(f"[Voice] Sending to Whisper: {resolved_filename} "
          f"({len(processed_bytes)} bytes)")

    # 3. Gửi lên Groq Whisper
    audio_file      = io.BytesIO(processed_bytes)
    audio_file.name = resolved_filename

    transcription = groq_client.audio.transcriptions.create(
        model           = "whisper-large-v3-turbo",
        file            = audio_file,
        language        = "vi",
        response_format = "text"
    )

    text = transcription.strip() if isinstance(transcription, str) \
           else transcription.text.strip()

    print(f"[Voice] Transcribed: {text}")
    return text


# ── Validate ───────────────────────────────────────────────────

def validate_voice_input(text: str) -> dict:
    """
    Kiểm tra text có hợp lệ không.
    Returns: { valid: bool, reason: str | None }
    """
    # 1. Quá ngắn
    if not text or len(text.strip()) < MIN_TEXT_LENGTH:
        return {
            "valid":  False,
            "reason": "Tôi không nghe rõ, bạn thử nói lại nhé! 🎤"
        }

    # 2. Toàn số hoặc ký tự đặc biệt
    if re.fullmatch(r'[\d\s\W]+', text):
        return {
            "valid":  False,
            "reason": "Tôi không hiểu được, bạn nói rõ hơn nhé!"
        }

    # 3. Từ tục / nhạy cảm
    text_lower = text.lower()
    for word in BANNED_WORDS:
        if word in text_lower:
            return {
                "valid":  False,
                "reason": "Bạn ơi, mình chỉ hỗ trợ quản lý chi tiêu thôi nhé 😊"
            }

    # 4. AI kiểm tra có liên quan chi tiêu không
    relevance = _check_relevance(text)
    if not relevance["relevant"]:
        return {
            "valid":  False,
            "reason": relevance["message"]
        }

    return {"valid": True, "reason": None}


def _check_relevance(text: str) -> dict:
    """Dùng AI kiểm tra câu có liên quan chi tiêu không"""
    prompt = f"""Check if this Vietnamese message is related to personal finance.

Message: "{text}"

Return ONLY JSON:
{{
  "relevant": true | false,
  "message": "short friendly Vietnamese reply if not relevant, else empty string"
}}

relevant = true if about:
- Expense/income: ăn, mua, chi, tiêu, nhận lương, thưởng, grab, cafe
- Querying: chi bao nhiêu, thống kê, báo cáo, hôm nay chi gì
- Actions: lưu, hủy, xác nhận, ví nào, số dư

relevant = false if:
- Unrelated: thời tiết, thể thao, tình yêu, tin tức, chính trị
- Meaningless noise or random words
- Pure greeting with no finance context
"""

    response = groq_client.chat.completions.create(
        model    = "llama-3.3-70b-versatile",
        messages = [
            {"role": "system", "content": "Return ONLY valid JSON."},
            {"role": "user",   "content": prompt}
        ],
        temperature = 0,
        max_tokens  = 100
    )

    raw   = response.choices[0].message.content.strip()
    match = re.search(r'\{.*\}', raw, re.DOTALL)
    if not match:
        return {"relevant": True, "message": ""}

    try:
        return json.loads(match.group())
    except Exception:
        return {"relevant": True, "message": ""}