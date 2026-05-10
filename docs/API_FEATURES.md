# 📡 API Documentation & Features

## Backend REST API (Spring Boot)

### **Base URL**
```
http://localhost:8080
```

### **API Documentation**
```
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
```

---

## Authentication Endpoints

### **1. Register User**

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePassword123!"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "createdAt": "2025-05-09T10:00:00Z"
}
```

**Error (400 Bad Request):**
```json
{
  "error": "Username already exists"
}
```

---

### **2. Login**

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "token_type": "Bearer",
  "expires_in": 3600,
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com"
  }
}
```

---

### **3. Refresh Token**

```http
POST /api/auth/refresh
Authorization: Bearer {access_token}
```

**Response (200 OK):**
```json
{
  "access_token": "new_jwt_token_here",
  "token_type": "Bearer",
  "expires_in": 3600
}
```

---

## Wallet Endpoints

### **4. Get All Wallets**

```http
GET /api/wallets
Authorization: Bearer {jwt_token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Ví Tiền Mặt",
    "balance": 5000000,
    "currency": "VND",
    "createdAt": "2025-05-01T10:00:00Z"
  },
  {
    "id": 2,
    "name": "Tài Khoản Ngân Hàng",
    "balance": 50000000,
    "currency": "VND",
    "createdAt": "2025-05-02T10:00:00Z"
  }
]
```

---

### **5. Get Wallet by ID**

```http
GET /api/wallets/{walletId}
Authorization: Bearer {jwt_token}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Ví Tiền Mặt",
  "balance": 5000000,
  "currency": "VND",
  "createdAt": "2025-05-01T10:00:00Z",
  "updatedAt": "2025-05-09T10:00:00Z"
}
```

---

### **6. Create Wallet**

```http
POST /api/wallets
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "Ví Du Lịch",
  "initialBalance": 2000000,
  "currency": "VND"
}
```

**Response (201 Created):**
```json
{
  "id": 3,
  "name": "Ví Du Lịch",
  "balance": 2000000,
  "currency": "VND",
  "createdAt": "2025-05-09T10:30:00Z"
}
```

---

### **7. Update Wallet**

```http
PUT /api/wallets/{walletId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "name": "Ví Du Lịch - Cập Nhật",
  "balance": 1500000
}
```

**Response (200 OK):**
```json
{
  "id": 3,
  "name": "Ví Du Lịch - Cập Nhật",
  "balance": 1500000,
  "currency": "VND",
  "updatedAt": "2025-05-09T10:45:00Z"
}
```

---

### **8. Delete Wallet**

```http
DELETE /api/wallets/{walletId}
Authorization: Bearer {jwt_token}
```

**Response (204 No Content)**

---

## Transaction Endpoints

### **9. Get All Transactions**

```http
GET /api/transactions?walletId=1&page=0&size=10&sort=transactionDate,desc
Authorization: Bearer {jwt_token}
```

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": 101,
      "walletId": 1,
      "categoryId": 1,
      "amount": 50000,
      "type": "EXPENSE",
      "description": "Ăn sáng",
      "transactionDate": "2025-05-09T09:30:00Z",
      "createdAt": "2025-05-09T09:35:00Z"
    }
  ],
  "pageable": {
    "page": 0,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### **10. Get Transaction by ID**

```http
GET /api/transactions/{transactionId}
Authorization: Bearer {jwt_token}
```

**Response (200 OK):**
```json
{
  "id": 101,
  "walletId": 1,
  "categoryId": 1,
  "amount": 50000,
  "type": "EXPENSE",
  "description": "Ăn sáng",
  "transactionDate": "2025-05-09T09:30:00Z",
  "createdAt": "2025-05-09T09:35:00Z"
}
```

---

### **11. Create Transaction**

```http
POST /api/transactions
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "walletId": 1,
  "categoryId": 1,
  "amount": 75000,
  "type": "EXPENSE",
  "description": "Cà phê + bánh mì",
  "transactionDate": "2025-05-09T10:00:00Z"
}
```

**Response (201 Created):**
```json
{
  "id": 102,
  "walletId": 1,
  "categoryId": 1,
  "amount": 75000,
  "type": "EXPENSE",
  "description": "Cà phê + bánh mì",
  "transactionDate": "2025-05-09T10:00:00Z",
  "createdAt": "2025-05-09T10:05:00Z"
}
```

---

### **12. Update Transaction**

```http
PUT /api/transactions/{transactionId}
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "amount": 100000,
  "description": "Cà phê + bánh mì + sữa"
}
```

**Response (200 OK):**
```json
{
  "id": 102,
  "walletId": 1,
  "categoryId": 1,
  "amount": 100000,
  "type": "EXPENSE",
  "description": "Cà phê + bánh mì + sữa",
  "transactionDate": "2025-05-09T10:00:00Z",
  "updatedAt": "2025-05-09T10:15:00Z"
}
```

---

### **13. Delete Transaction**

```http
DELETE /api/transactions/{transactionId}
Authorization: Bearer {jwt_token}
```

**Response (204 No Content)**

---

## Category Endpoints

### **14. Get All Categories**

```http
GET /api/categories
Authorization: Bearer {jwt_token}
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Ăn uống",
    "type": "EXPENSE",
    "icon": "🍔"
  },
  {
    "id": 2,
    "name": "Giải trí",
    "type": "EXPENSE",
    "icon": "🎮"
  },
  {
    "id": 3,
    "name": "Di chuyển",
    "type": "EXPENSE",
    "icon": "🚗"
  },
  {
    "id": 4,
    "name": "Sức khỏe",
    "type": "EXPENSE",
    "icon": "🏥"
  },
  {
    "id": 10,
    "name": "Lương",
    "type": "INCOME",
    "icon": "💰"
  }
]
```

---

## Report Endpoints

### **15. Get Expense Summary**

```http
GET /api/reports/summary?startDate=2025-05-01&endDate=2025-05-31
Authorization: Bearer {jwt_token}
```

**Response (200 OK):**
```json
{
  "totalExpense": 2500000,
  "totalIncome": 15000000,
  "netBalance": 12500000,
  "averageExpensePerDay": 80645,
  "largestExpense": 500000,
  "transactionCount": 31,
  "period": "2025-05-01 to 2025-05-31"
}
```

---

### **16. Get Expenses by Category**

```http
GET /api/reports/by-category?startDate=2025-05-01&endDate=2025-05-31
Authorization: Bearer {jwt_token}
```

**Response (200 OK):**
```json
[
  {
    "categoryId": 1,
    "categoryName": "Ăn uống",
    "totalAmount": 1200000,
    "percentage": 48,
    "transactionCount": 15
  },
  {
    "categoryId": 3,
    "categoryName": "Di chuyển",
    "totalAmount": 600000,
    "percentage": 24,
    "transactionCount": 8
  },
  {
    "categoryId": 2,
    "categoryName": "Giải trí",
    "totalAmount": 400000,
    "percentage": 16,
    "transactionCount": 5
  },
  {
    "categoryId": 9,
    "categoryName": "Khác",
    "totalAmount": 300000,
    "percentage": 12,
    "transactionCount": 3
  }
]
```

---

### **17. Get Expenses by Date**

```http
GET /api/reports/by-date?startDate=2025-05-01&endDate=2025-05-31&groupBy=day
Authorization: Bearer {jwt_token}
```

**groupBy Options:** `day`, `week`, `month`

**Response (200 OK):**
```json
[
  {
    "date": "2025-05-01",
    "dayOfWeek": "THURSDAY",
    "expenseAmount": 85000,
    "incomeAmount": 0,
    "netAmount": -85000,
    "transactionCount": 3
  },
  {
    "date": "2025-05-02",
    "dayOfWeek": "FRIDAY",
    "expenseAmount": 120000,
    "incomeAmount": 500000,
    "netAmount": 380000,
    "transactionCount": 5
  }
]
```

---

## AI Service Endpoints (FastAPI)

### **Base URL**
```
http://localhost:8000
```

### **API Documentation**
```
Swagger UI: http://localhost:8000/docs
ReDoc: http://localhost:8000/redoc
```

---

## Chat Endpoints

### **1. Generate Chat Response**

```http
POST /chat/generate
Content-Type: application/json

{
  "wallet_id": 1,
  "message": "Ăn trưa 80 nghìn",
  "history": []
}
```

**Response (200 OK):**
```json
{
  "role": "assistant",
  "message": "Ghi nhận chi tiêu: ăn trưa 80k. Bạn muốn lưu vào ví nào? Các ví: Ví Tiền Mặt, Tài Khoản Ngân Hàng",
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

---

### **2. Process Voice Input**

```http
POST /chat/voice
Content-Type: multipart/form-data

Form Data:
- wallet_id: 1
- audio_file: [binary audio data]
- content_type: audio/webm
```

**Response (200 OK):**
```json
{
  "role": "assistant",
  "message": "Ghi nhận chi tiêu: ăn sáng 50k + cà phê 20k = 70k. Lưu vào ước nào?",
  "intent": "add_expense",
  "data": {
    "LocalDateTime": "2025-05-09 09:30:00",
    "Total": 70000,
    "Category": "Ăn uống",
    "Note": "ăn sáng + cà phê",
    "WalletName": null
  },
  "source": "voice"
}
```

---

## OCR Endpoints

### **3. Extract from Invoice Image**

```http
POST /ocr/extract
Content-Type: multipart/form-data

Form Data:
- image_file: [binary image data]
- content_type: image/jpeg
```

**Response (200 OK):**
```json
[
  {
    "LocalDateTime": "2025-05-09",
    "Total": 150000,
    "Category": "Ăn uống",
    "Note": "Cơm, rau, nước"
  },
  {
    "LocalDateTime": "2025-05-09",
    "Total": 50000,
    "Category": "Ăn uống",
    "Note": "Cà phê"
  }
]
```

---

## Audio Endpoints

### **4. Transcribe Audio**

```http
POST /audio/transcribe
Content-Type: multipart/form-data

Form Data:
- audio_file: [binary audio data]
- filename: audio.webm
- content_type: audio/webm
```

**Response (200 OK):**
```json
{
  "text": "ăn sáng năm mươi nghìn",
  "duration": 2.5,
  "language": "vi"
}
```

---

## Health Check

### **5. API Health Check**

```http
GET /health
```

**Response (200 OK):**
```json
{
  "status": "healthy",
  "timestamp": "2025-05-09T10:30:00Z",
  "version": "1.0.0"
}
```

---

## Features

### **📝 Input Methods**

1. **Manual Input**
   - Form nhập trực tiếp
   - Chọn category, amount, description
   - Chọn wallet

2. **Voice Input** 🎤
   - Ghi âm giọng nói
   - Groq Whisper STT
   - Xác thực input
   - Phân loại tự động via AI

3. **OCR Scanning** 📸
   - Chụp hóa đơn
   - Groq Vision extraction
   - Multi-item recognition
   - Chỉnh sửa trước lưu

4. **AI Chat** 💬
   - Natural language input
   - Groq LLM classification
   - Intent detection
   - Confirmation flow

---

### **💰 Transaction Types**

| Type | Mô Tả |
|------|-------|
| EXPENSE | Chi tiêu |
| INCOME | Thu nhập |

---

### **🏷️ Expense Categories** (14 categories)

```
EXPENSE:
1. Ăn uống - Food & Drink
2. Giải trí - Entertainment
3. Di chuyển - Transportation
4. Sức khỏe - Health
5. Giáo dục - Education
6. Du lịch - Travel
7. Quà - Gifts
8. Gia đình - Family
9. Khác - Others
13. Hóa đơn & Tiện ích - Bills & Utilities
14. Mua sắm - Shopping

INCOME:
10. Lương - Salary
11. Thưởng - Bonus
12. Khác - Others
```

---

### **📊 Reports Available**

1. **Summary Report**
   - Total income/expense
   - Net balance
   - Average expense per day
   - Largest transaction

2. **Category Report**
   - Expense by category
   - Percentage distribution
   - Transaction count

3. **Date Report**
   - Daily/Weekly/Monthly breakdown
   - Trend analysis
   - Comparison data

---

### **🔐 Security Features**

- ✅ JWT Authentication
- ✅ Spring Security
- ✅ CORS Protection
- ✅ Password Hashing (BCrypt)
- ✅ Token Expiration
- ✅ Role-based Access Control (optional)

---

### **⚡ Performance Features**

- ✅ Pagination on list endpoints
- ✅ Database indexing
- ✅ Caching (Redis/Ehcache)
- ✅ Connection pooling
- ✅ Audio preprocessing
- ✅ Image optimization

---

