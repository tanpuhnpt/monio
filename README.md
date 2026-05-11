<div align="center">

<img src="docs/images/monio_logo.svg" alt="Monio Logo" width="120" />

# 🏦 Monio

**AI-Powered Personal Finance Tracker**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.11-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19.2.0-61DAFB?logo=react)](https://react.dev)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.115-009688?logo=fastapi)](https://fastapi.tiangolo.com)
[![Python](https://img.shields.io/badge/Python-3.10+-3776AB?logo=python)](https://python.org)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)](https://openjdk.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-316192?logo=postgresql)](https://postgresql.org)
[![Groq](https://img.shields.io/badge/Groq-LLM%20%7C%20Whisper%20%7C%20Vision-F55036?logo=groq)](https://groq.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

[Features](#-features) • [Architecture](#-architecture) • [Getting Started](#-getting-started) • [API Docs](#-api-reference) • [Screenshots](#-screenshots)

</div>

---

## 📖 Overview

**Monio** is a modern personal finance management application that leverages AI to make expense tracking effortless. Record transactions by typing, speaking, or scanning receipts — Monio understands you.

```
💬 "Hôm nay ăn sáng 50k"  →  AI auto-classifies  →  ✅ Expense saved
🎤 Voice input             →  Whisper STT          →  ✅ Hands-free entry
📸 Receipt photo           →  Vision OCR           →  ✅ Auto-extracted
```

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🤖 **AI Chat** | Natural language expense recording with context memory |
| 🎤 **Voice Input** | Speech-to-text powered by Groq Whisper with noise reduction |
| 📸 **OCR Scanner** | Automatic receipt scanning via Groq Vision (llama-4-scout) |
| 📊 **Reports** | Visual analytics by category, wallet, and time range |
| 👛 **Multi-Wallet** | Manage multiple accounts and currencies |
| 🔒 **Secure Auth** | JWT-based authentication with Spring Security |
| 🌐 **REST API** | Full OpenAPI/Swagger documentation |

---

## 🏗 Architecture

### System Overview

![System Architecture](docs/architecture/ArchitectureOfWebsite.png)

> *3-layer architecture: React Frontend — Spring Boot Backend — FastAPI AI Service*

### AI Processing Pipelines

<details>
<summary><b>💬 Chat Pipeline</b></summary>

![Chat Pipeline](docs/architecture/ChatBotProcessingPipeline.png)

</details>

<details>
<summary><b>🎤 Voice Pipeline</b></summary>

![Voice Pipeline](docs/architecture/VoiceProcessinPipeline.png)

</details>

<details>
<summary><b>📸 OCR Pipeline</b></summary>

![OCR Pipeline](docs/architecture/OcrProcessingPipeline.png)

</details>

---

## 🛠 Tech Stack

### Frontend
| Package | Version | Role |
|---------|---------|------|
| React | `19.2.0` | UI Library |
| Vite | `7.2.4` | Build Tool |
| Tailwind CSS | `4.1.18` | Styling |
| Recharts | `3.7.0` | Charts |
| Lucide React | `0.563.0` | Icons |
| Axios | latest | HTTP Client |

### Backend
| Package | Version | Role |
|---------|---------|------|
| Spring Boot | `3.5.11` | Main Framework |
| Java | `21` | Language |
| Spring Security + JWT | — | Auth |
| Spring Data JPA | — | ORM |
| PostgreSQL | `15+` | Database |
| MapStruct | `1.5.5` | DTO Mapping |
| Lombok | — | Boilerplate |
| SpringDoc OpenAPI | `2.8.16` | API Docs |

### AI Service
| Package | Version | Role |
|---------|---------|------|
| FastAPI | `0.115+` | API Framework |
| Groq SDK | latest | LLM / STT / Vision |
| Whisper (via Groq) | `large-v3-turbo` | Speech-to-Text |
| PydUB | latest | Audio Processing |
| noisereduce | latest | Noise Reduction |
| SQLAlchemy | latest | ORM (MySQL) |
| Pillow | latest | Image Processing |
| NumPy | latest | Array Processing |

### DevOps
| Tool | Role |
|------|------|
| Docker + Compose | Containerization |
| Maven | Backend Build |
| Git | Version Control |

---

## 🚀 Getting Started

### Prerequisites

```bash
node  >= 18.0
java  >= 21
python >= 3.10
postgresql >= 13
ffmpeg  # required for audio processing
```

### Clone

```bash
git clone https://github.com/tanpuhnpt/monio.git
cd monio
```

### 1️⃣ Backend (Spring Boot)

```bash
cd backend
cp src/main/resources/application.yml.example \
   src/main/resources/application.yml
# Edit DB credentials in application.yml
./mvnw clean spring-boot:run
```

> API runs at **http://localhost:8080**  
> Swagger UI: **http://localhost:8080/swagger-ui.html**

### 2️⃣ AI Service (FastAPI)

```bash
cd ai
python -m venv venv && source venv/bin/activate   # Windows: venv\Scripts\activate
pip install -r requirements.txt
cp .env.example .env
# Edit .env:
#   GROQ_API_KEY=gsk_...
#   DATABASE_URL=mysql+pymysql://user:pass@localhost:3306/monio_ai
#   SPRING_BOOT_API_URL=http://localhost:8080
uvicorn main:app --reload --port 8000
```

> AI Service runs at **http://localhost:8000**  
> Swagger UI: **http://localhost:8000/docs**

### 3️⃣ Frontend (React)

```bash
cd frontend
cp .env.example .env.local
# Edit:
#   VITE_API_URL=http://localhost:8080
#   VITE_AI_SERVICE_URL=http://localhost:8000
npm install && npm run dev
```

> Frontend runs at **http://localhost:5173**

### 🐳 Docker (All-in-one)

```bash
docker compose up --build
```

---

## ⚙️ Environment Variables

### Backend — `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/monio
    username: postgres
    password: your_password
server:
  port: 8080
```

### AI Service — `.env`

```env
GROQ_API_KEY=gsk_xxxxxxxxxxxxxxxx
DATABASE_URL=mysql+pymysql://root:password@localhost:3306/monio_ai
SPRING_BOOT_API_URL=http://localhost:8080
```

### Frontend — `.env.local`

```env
VITE_API_URL=http://localhost:8080
VITE_AI_SERVICE_URL=http://localhost:8000
```

---

## 📡 API Reference

### Backend Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/login` | User login, returns JWT |
| `POST` | `/auth/register` | User registration |
| `GET` | `/wallets` | Get all wallets |
| `POST` | `/transactions` | Create transaction |
| `GET` | `/transactions` | List transactions (with filters) |
| `GET` | `/reports/summary` | Income/expense summary |
| `GET` | `/reports/by-category` | Stats by category |
| `GET` | `/reports/by-wallet` | Stats by wallet |

### AI Service Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/chat` | AI chat with memory |
| `GET` | `/history/{user_id}` | Chat history |
| `POST` | `/ocr` | Extract bills from image |
| `POST` | `/voice-chat` | Voice input → transaction |
| `GET` | `/` | Health check |

---

## 🗄 Database Schema

<details>
<summary><b>PostgreSQL (Spring Boot)</b></summary>

```sql
CREATE TABLE users (
    id          SERIAL PRIMARY KEY,
    username    VARCHAR(255) UNIQUE NOT NULL,
    email       VARCHAR(255) UNIQUE NOT NULL,
    password    VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE wallets (
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER REFERENCES users(id),
    name        VARCHAR(255) NOT NULL,
    balance     DECIMAL(15,2) DEFAULT 0,
    currency    VARCHAR(3) DEFAULT 'VND',
    created_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE categories (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    type        VARCHAR(10) CHECK (type IN ('EXPENSE','INCOME'))
);

CREATE TABLE transactions (
    id               SERIAL PRIMARY KEY,
    wallet_id        INTEGER REFERENCES wallets(id),
    category_id      INTEGER REFERENCES categories(id),
    amount           DECIMAL(15,2) NOT NULL,
    type             VARCHAR(10),
    note             TEXT,
    transaction_date TIMESTAMP,
    created_at       TIMESTAMP DEFAULT NOW()
);
```

</details>

<details>
<summary><b>MySQL (AI Service)</b></summary>

```sql
CREATE TABLE chat_history (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    role       VARCHAR(10) NOT NULL,  -- 'user' | 'assistant'
    message    TEXT NOT NULL,
    created_at DATETIME DEFAULT NOW()
);

CREATE TABLE pending_transactions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    amount      FLOAT NOT NULL,
    note        VARCHAR(255),
    tx_type     VARCHAR(10) NOT NULL,  -- 'EXPENSE' | 'INCOME'
    category_id INT NOT NULL,
    wallet_id   INT,
    tx_datetime DATETIME NOT NULL,
    created_at  DATETIME DEFAULT NOW()
);
```

</details>

---

## 📁 Project Structure

```
monio/
├── 📄 README.md
├── 🐳 docker-compose.yml
│
├── frontend/                      # React + Vite
│   ├── src/
│   │   ├── components/            # Reusable UI components
│   │   │   ├── Chat/              # AI chat interface
│   │   │   ├── Voice/             # Voice recording button
│   │   │   └── OCR/               # Receipt scanner
│   │   ├── pages/                 # Route pages
│   │   ├── services/              # Axios API clients
│   │   └── utils/                 # Helpers & formatters
│   ├── package.json
│   └── vite.config.js
│
├── backend/                       # Spring Boot
│   └── src/main/java/com/mpt/monio/
│       ├── auth/                  # JWT & Security
│       ├── controller/            # REST Controllers
│       ├── service/               # Business Logic
│       ├── model/                 # JPA Entities
│       ├── repository/            # Data Repositories
│       └── config/                # App Configuration
│
└── ai/                            # FastAPI AI Service
    ├── main.py                    # App entry point
    ├── requirements.txt
    ├── .env.example
    └── app/
        ├── api/
        │   ├── chat.py            # /chat, /voice-chat
        │   └── ocr.py             # /ocr
        ├── services/
        │   ├── ai_service.py      # Groq LLM logic
        │   ├── voice_service.py   # Whisper + preprocessing
        │   ├── ocr_service.py     # Vision API
        │   ├── db_service.py      # DB CRUD
        │   └── spring_service.py  # Spring Boot client
        ├── models/
        │   └── chat.py            # SQLAlchemy models
        ├── schemas/
        │   └── chat.py            # Pydantic schemas
        └── core/
            ├── config.py          # Env variables
            └── database.py        # DB connection
```

---

## 📸 Screenshots

| Dashboard | AI Chat |
|-----------|---------|
| ![Dashboard](docs/images/ui-screenshots/MainDashboard.jpg) | ![Chat](docs/images/chat-ui/chat_ui.jpeg) |

| Voice Input | OCR Scanner |
|-------------|-------------|
| ![Voice](docs/images/ui-screenshots/VoiceInputInterface.jpg) | ![OCR](docs/images/ui-screenshots/OcrScannerInterface.jpg) |

---

## 🤝 Contributing

```bash
# 1. Fork the repository
# 2. Create your feature branch
git checkout -b feature/your-feature-name

# 3. Commit your changes
git commit -m "feat: add your feature description"

# 4. Push to the branch
git push origin feature/your-feature-name

# 5. Open a Pull Request
```

## 📜 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ by the **Monio Team**

⭐ Star this repo if you find it helpful!

</div>