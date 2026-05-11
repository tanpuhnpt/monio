# 🏗️ Kiến Trúc Chi Tiết

## Các Thành Phần Chính

### **1. Frontend Layer**

```
┌─────────────────────────────────────────────────────┐
│          React SPA (Single Page Application)        │
├─────────────────────────────────────────────────────┤
│                                                      │
│  ┌────────────────────────────────────────────────┐ │
│  │           Pages & Components                   │ │
│  ├────────────────────────────────────────────────┤ │
│  │ • Dashboard - Tổng quan chi tiêu               │ │
│  │ • LoginPage - Xác thực người dùng             │ │
│  │ • RegisterPage - Đăng ký tài khoản            │ │
│  │ • TransactionsPage - Quản lý giao dịch        │ │
│  │ • ReportsPage - Báo cáo chi tiêu              │ │
│  │ • ChatComponent - Trò chuyện AI               │ │
│  │ • InAppScanner - Quét OCR hóa đơn             │ │
│  │ • VoiceInput - Ghi âm giọng nói               │ │
│  │ • WalletManager - Quản lý ví                  │ │
│  │                                                 │ │
│  └────────────────────────────────────────────────┘ │
│                          ▼                          │
│  ┌────────────────────────────────────────────────┐ │
│  │           Services & Utilities                 │ │
│  ├────────────────────────────────────────────────┤ │
│  │ • apiClient.js - HTTP client                  │ │
│  │ • authService.js - Auth logic                 │ │
│  │ • apiConfig.js - API configuration            │ │
│  │ • Custom hooks - React hooks                  │ │
│  │                                                 │ │
│  └────────────────────────────────────────────────┘ │
│                          ▼                          │
│  ┌────────────────────────────────────────────────┐ │
│  │         UI Framework & Libraries              │ │
│  ├────────────────────────────────────────────────┤ │
│  │ • React 19 - UI building                      │ │
│  │ • Tailwind CSS - Styling                      │ │
│  │ • Recharts - Charts & graphs                  │ │
│  │ • Lucide Icons - UI icons                     │ │
│  │                                                 │ │
│  └────────────────────────────────────────────────┘ │
│                                                      │
└─────────────────────────────────────────────────────┘
```

**Key Features:**
- ✅ Responsive design (Mobile-first)
- ✅ Real-time chat interface
- ✅ Camera integration for OCR
- ✅ Voice recording interface
- ✅ Charts & analytics
- ✅ JWT token management

---

### **2. Backend Layer (Spring Boot)**

```
┌──────────────────────────────────────────────────────┐
│     Spring Boot Application (Java 21)                │
│     Port: 8080                                       │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ┌───────────────────────────────────────────────┐  │
│  │       REST API Controllers                    │  │
│  ├───────────────────────────────────────────────┤  │
│  │ • AuthController(/api/auth)                  │  │
│  │   - POST /register                           │  │
│  │   - POST /login                              │  │
│  │   - POST /refresh-token                      │  │
│  │   - POST /logout                             │  │
│  │                                               │  │
│  │ • WalletController(/api/wallets)             │  │
│  │   - GET / (list all)                         │  │
│  │   - GET /{id}                                │  │
│  │   - POST / (create)                          │  │
│  │   - PUT /{id}                                │  │
│  │   - DELETE /{id}                             │  │
│  │                                               │  │
│  │ • TransactionController(/api/transactions)  │  │
│  │   - GET / (with filters)                     │  │
│  │   - POST / (create)                          │  │
│  │   - PUT /{id}                                │  │
│  │   - DELETE /{id}                             │  │
│  │                                               │  │
│  │ • CategoryController(/api/categories)       │  │
│  │   - GET / (list)                             │  │
│  │                                               │  │
│  │ • ReportController(/api/reports)            │  │
│  │   - GET /summary                             │  │
│  │   - GET /by-category                         │  │
│  │   - GET /by-date                             │  │
│  │                                               │  │
│  └───────────────────────────────────────────────┘  │
│                          ▼                           │
│  ┌───────────────────────────────────────────────┐  │
│  │      Business Logic Layer (Services)          │  │
│  ├───────────────────────────────────────────────┤  │
│  │ • AuthService                                 │  │
│  │   - User registration & validation           │  │
│  │   - Login & password management              │  │
│  │   - JWT token generation                     │  │
│  │                                               │  │
│  │ • WalletService                              │  │
│  │   - Create/Read/Update/Delete wallets       │  │
│  │   - Balance calculations                     │  │
│  │                                               │  │
│  │ • TransactionService                         │  │
│  │   - CRUD operations                          │  │
│  │   - Filtering & sorting                      │  │
│  │   - Amount calculations                      │  │
│  │                                               │  │
│  │ • ReportService                              │  │
│  │   - Generate reports                         │  │
│  │   - Aggregate data                           │  │
│  │   - Export formats (CSV, PDF)                │  │
│  │                                               │  │
│  └───────────────────────────────────────────────┘  │
│                          ▼                           │
│  ┌───────────────────────────────────────────────┐  │
│  │      Data Access Layer (Repositories)        │  │
│  ├───────────────────────────────────────────────┤  │
│  │ • UserRepository (Spring Data JPA)           │  │
│  │ • WalletRepository                           │  │
│  │ • TransactionRepository                      │  │
│  │ • CategoryRepository                         │  │
│  │                                               │  │
│  │ → Query Methods:                             │  │
│  │   - findById()                               │  │
│  │   - findByUserId()                           │  │
│  │   - findByDateBetween()                      │  │
│  │   - Custom @Query methods                    │  │
│  │                                               │  │
│  └───────────────────────────────────────────────┘  │
│                          ▼                           │
│  ┌───────────────────────────────────────────────┐  │
│  │      Entity Models (JPA/Hibernate)           │  │
│  ├───────────────────────────────────────────────┤  │
│  │ @Entity User                                 │  │
│  │ │ - id (Long)                               │  │
│  │ │ - username (String)                       │  │
│  │ │ - email (String)                          │  │
│  │ │ - password (String hashed)                │  │
│  │ │ - roles (Set<Role>)                       │  │
│  │ │ - wallets (OneToMany)                     │  │
│  │ │ - createdAt, updatedAt                    │  │
│  │                                               │  │
│  │ @Entity Wallet                              │  │
│  │ │ - id (Long)                               │  │
│  │ │ - user (ManyToOne)                        │  │
│  │ │ - name (String)                           │  │
│  │ │ - balance (BigDecimal)                    │  │
│  │ │ - currency (String)                       │  │
│  │ │ - transactions (OneToMany)                │  │
│  │                                               │  │
│  │ @Entity Transaction                         │  │
│  │ │ - id (Long)                               │  │
│  │ │ - wallet (ManyToOne)                      │  │
│  │ │ - category (ManyToOne)                    │  │
│  │ │ - amount (BigDecimal)                     │  │
│  │ │ - type (EXPENSE, INCOME)                  │  │
│  │ │ - description (String)                    │  │
│  │ │ - transactionDate (LocalDateTime)         │  │
│  │                                               │  │
│  │ @Entity Category                            │  │
│  │ │ - id (Integer)                            │  │
│  │ │ - name (String)                           │  │
│  │ │ - type (EXPENSE, INCOME)                  │  │
│  │ │ - transactions (OneToMany)                │  │
│  │                                               │  │
│  └───────────────────────────────────────────────┘  │
│                          ▼                           │
│  ┌───────────────────────────────────────────────┐  │
│  │    Security & Configuration                  │  │
│  ├───────────────────────────────────────────────┤  │
│  │ • SecurityConfig (Spring Security)           │  │
│  │   - JWT filter chain                         │  │
│  │   - CORS configuration                       │  │
│  │   - Method-level security                    │  │
│  │                                               │  │
│  │ • DatabaseConfig (Hibernate)                 │  │
│  │   - Connection pooling                       │  │
│  │   - Entity scanning                          │  │
│  │                                               │  │
│  │ • ApplicationConfig                          │  │
│  │   - Bean definitions                         │  │
│  │   - Error handling                           │  │
│  │                                               │  │
│  └───────────────────────────────────────────────┘  │
│                          ▼                           │
│            PostgreSQL Database                      │
│            (Hibernate ORM)                          │
│                                                      │
└──────────────────────────────────────────────────────┘
```

**Spring Boot Components:**
- ✅ Spring Web (REST)
- ✅ Spring Data JPA (ORM)
- ✅ Spring Security (AuthN/AuthZ)
- ✅ Spring Validation
- ✅ Lombok (Boilerplate)
- ✅ MapStruct (DTO mapping)

---

### **3. AI Service Layer (FastAPI)**

```
┌────────────────────────────────────────────────────┐
│     FastAPI Application (Python 3.11)              │
│     Port: 8000                                     │
├────────────────────────────────────────────────────┤
│                                                    │
│  ┌──────────────────────────────────────────────┐ │
│  │        API Routes (FastAPI)                  │ │
│  ├──────────────────────────────────────────────┤ │
│  │ /chat                                        │ │
│  │ ├─ POST /generate - Chat with AI            │ │
│  │ ├─ POST /voice - Process voice input        │ │
│  │ └─ POST /audio/transcribe - STT             │ │
│  │                                              │ │
│  │ /ocr                                         │ │
│  │ ├─ POST /extract - Extract from image       │ │
│  │ └─ GET /status                              │ │
│  │                                              │ │
│  │ /health                                      │ │
│  │ └─ GET / - Health check                     │ │
│  │                                              │ │
│  └──────────────────────────────────────────────┘ │
│                      ▼                             │
│  ┌──────────────────────────────────────────────┐ │
│  │      Services Layer (Business Logic)         │ │
│  ├──────────────────────────────────────────────┤ │
│  │                                              │ │
│  │ AI Service (ai_service.py)                  │ │
│  │ ├─ classify_and_reply()                     │ │
│  │ │  Input: message, history, wallets         │ │
│  │ │  → Groq LLM                               │ │
│  │ │  Output: intent, reply, transaction data  │ │
│  │ │                                            │ │
│  │ │  Intent Detection:                        │ │
│  │ │  ├─ add_expense                           │ │
│  │ │  ├─ add_income                            │ │
│  │ │  ├─ missing_info                          │ │
│  │ │  ├─ confirm_save                          │ │
│  │ │  ├─ update_pending                        │ │
│  │ │  ├─ cancel                                │ │
│  │ │  ├─ query                                 │ │
│  │ │  └─ out_of_scope                          │ │
│  │ │                                            │ │
│  │ ├─ _check_relevance()                       │ │
│  │ │  Check if message is about expenses       │ │
│  │ │                                            │ │
│  │ └─ get_category_id()                        │ │
│  │    Map category name to ID                  │ │
│  │                                              │ │
│  │ Voice Service (voice_service.py)            │ │
│  │ ├─ transcribe_audio()                       │ │
│  │ │  1. Preprocess audio                      │ │
│  │ │  2. Groq Whisper (Speech-to-Text)         │ │
│  │ │  3. Return text                           │ │
│  │ │                                            │ │
│  │ ├─ preprocess_audio()                       │ │
│  │ │  ├─ Detect format from content-type      │ │
│  │ │  ├─ Convert to mono, 16kHz               │ │
│  │ │  ├─ Normalize volume                     │ │
│  │ │  ├─ Reduce noise (noisereduce)           │ │
│  │ │  └─ Export as WAV                        │ │
│  │ │                                            │ │
│  │ ├─ validate_voice_input()                   │ │
│  │ │  ├─ Check text length                     │ │
│  │ │  ├─ Check for banned words               │ │
│  │ │  ├─ AI relevance check                   │ │
│  │ │  └─ Return validation result             │ │
│  │ │                                            │ │
│  │ OCR Service (ocr_service.py)                │ │
│  │ ├─ extract_bills_from_image()               │ │
│  │ │  1. Encode image to Base64               │ │
│  │ │  2. Send to Groq Vision (llama-4-scout)  │ │
│  │ │  3. Parse JSON response                  │ │
│  │ │  4. Validate categories                  │ │
│  │ │  5. Return bill array                    │ │
│  │ │                                            │ │
│  │ DB Service (db_service.py)                  │ │
│  │ ├─ get_user_wallets()                       │ │
│  │ ├─ save_transaction()                       │ │
│  │ ├─ get_transactions()                       │ │
│  │ └─ update_wallet_balance()                  │ │
│  │                                              │ │
│  │ Spring Service (spring_service.py)          │ │
│  │ ├─ call_backend_api()                       │ │
│  │ ├─ get_user_info()                          │ │
│  │ └─ create_transaction()                     │ │
│  │                                              │ │
│  └──────────────────────────────────────────────┘ │
│                      ▼                             │
│  ┌──────────────────────────────────────────────┐ │
│  │     External APIs & Libraries               │ │
│  ├──────────────────────────────────────────────┤ │
│  │ Groq API                                     │ │
│  │ ├─ chat.completions.create() - LLM         │ │
│  │ ├─ audio.transcriptions.create() - STT     │ │
│  │ └─ Vision API - Image analysis             │ │
│  │                                              │ │
│  │ Audio Libraries                             │ │
│  │ ├─ pydub (AudioSegment)                     │ │
│  │ ├─ noisereduce                              │ │
│  │ ├─ numpy                                    │ │
│  │ └─ scipy                                    │ │
│  │                                              │ │
│  │ Image Processing                            │ │
│  │ └─ PIL/Pillow                               │ │
│  │                                              │ │
│  │ Database                                    │ │
│  │ ├─ SQLAlchemy (ORM)                         │ │
│  │ └─ MySQL (pymysql)                          │ │
│  │                                              │ │
│  └──────────────────────────────────────────────┘ │
│                      ▼                             │
│  ┌──────────────────────────────────────────────┐ │
│  │  Models & Schemas (Pydantic)                │ │
│  ├──────────────────────────────────────────────┤ │
│  │ ChatRequest                                  │ │
│  │ ├─ wallet_id: int                           │ │
│  │ ├─ message: str                             │ │
│  │ └─ history: List[ChatMessage]               │ │
│  │                                              │ │
│  │ ChatResponse                                 │ │
│  │ ├─ role: str                                │ │
│  │ ├─ message: str                             │ │
│  │ ├─ intent: str                              │ │
│  │ └─ data: TransactionData                    │ │
│  │                                              │ │
│  │ TransactionData                             │ │
│  │ ├─ LocalDateTime: datetime                  │ │
│  │ ├─ Total: float                             │ │
│  │ ├─ Category: str                            │ │
│  │ ├─ Note: str                                │ │
│  │ └─ WalletName: str                          │ │
│  │                                              │ │
│  └──────────────────────────────────────────────┘ │
│                                                    │
└────────────────────────────────────────────────────┘
```

**AI Service Components:**
- ✅ FastAPI framework
- ✅ Groq LLM integration
- ✅ Audio preprocessing
- ✅ OCR vision processing
- ✅ SQLAlchemy ORM
- ✅ RESTful API design

---

## Communication Protocols

### **Frontend ↔ Backend**

```
Request:
POST /api/transactions
Headers: Authorization: Bearer {jwt_token}
Content-Type: application/json

Body: {
  "walletId": 1,
  "categoryId": 1,
  "amount": 50000,
  "type": "EXPENSE",
  "description": "Ăn sáng",
  "transactionDate": "2025-05-09T09:30:00"
}

Response:
{
  "id": 123,
  "walletId": 1,
  "categoryId": 1,
  "amount": 50000,
  "type": "EXPENSE",
  "description": "Ăn sáng",
  "transactionDate": "2025-05-09T09:30:00",
  "createdAt": "2025-05-09T09:35:00"
}
```

### **Frontend ↔ AI Service**

```
Request:
POST /chat/generate
Content-Type: application/json

Body: {
  "wallet_id": 1,
  "message": "Ăn trưa 80 nghìn",
  "history": []
}

Response:
{
  "role": "assistant",
  "message": "Ghi nhận chi tiêu: ăn trưa 80k. Bạn muốn lưu vào ví nào?",
  "intent": "add_expense",
  "data": {
    "LocalDateTime": "2025-05-09 12:30:00",
    "Total": 80000,
    "Category": "Ăn uống",
    "Note": "ăn trưa",
    "WalletName": null
  }
}
```

### **Backend ↔ AI Service**

```
Request (AI Service → Backend):
POST /api/transactions
Authorization: Bearer {internal_token}

Body: {
  "walletId": 1,
  "categoryId": 1,
  "amount": 50000,
  "type": "EXPENSE",
  "description": "Từ voice/chat",
  "transactionDate": "2025-05-09T10:00:00"
}
```

---

## Error Handling

### **Backend Error Responses**

```json
{
  "timestamp": "2025-05-09T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Wallet not found",
  "path": "/api/wallets/999"
}
```

### **AI Service Error Responses**

```json
{
  "detail": "Invalid audio format",
  "status": 422,
  "message": "Unsupported MIME type"
}
```

---

