# 📋 Hướng Dẫn Setup Chi Tiết

## Yêu Cầu Hệ Thống trong Máy

### **macOS**
```bash
# Kiểm tra Java
java -version

# Kiểm tra Node.js
node -v && npm -v

# Kiểm tra Python
python3 --version

# Cài đặt Homebrew nếu chưa có
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Cài Java 21 (nếu chưa có)
brew install openjdk@21
sudo ln -sfn /usr/local/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk

# Cài Node.js (nếu chưa có)
brew install node

# Cài Python (nếu chưa có)
brew install python@3.11
```

### **Windows**
```cmd
# Tải và cài đặt:
- Java 21: https://www.oracle.com/java/technologies/downloads/
- Node.js: https://nodejs.org/ (LTS version)
- Python 3.10+: https://www.python.org/downloads/
- PostgreSQL: https://www.postgresql.org/download/windows/
- Git: https://git-scm.com/download/win
```

---

## 🔧 Setup Chi Tiết Từng Phần

### **1. Backend Setup**

```bash
cd backend

# Tạo .env file (nếu cần)
touch .env

# Build project
./mvnw clean package -DskipTests

# Chạy migrations
./mvnw flyway:migrate  # nếu dùng Flyway

# Chạy application
./mvnw spring-boot:run

# Kiểm tra
curl http://localhost:8080/swagger-ui.html
```

**Configuration Files:**
- `src/main/resources/application.yml` - Development config
- `src/main/resources/application-prod.yml` - Production config

---

### **2. AI Service Setup**

```bash
cd ai

# Tạo virtual environment
python3 -m venv venv

# Kích hoạt venv
source venv/bin/activate  # macOS/Linux
# hoặc
venv\Scripts\activate  # Windows (PowerShell: venv\Scripts\Activate.ps1)

# Nâng cấp pip
python -m pip install --upgrade pip

# Cài dependencies
pip install -r requirements.txt

# Tạo .env file
cat > .env << EOF
GROQ_API_KEY=your_groq_api_key_here
DATABASE_URL=mysql+pymysql://root:password@localhost:3306/monio
SPRING_BOOT_API_URL=http://localhost:8080
EOF

# Test Groq API connection
python test_groq.py

# Chạy FastAPI
uvicorn main:app --reload --host 0.0.0.0 --port 8000

# Kiểm tra
curl http://localhost:8000/docs
```

**Dependency Versions:**
```
fastapi==0.104.1
uvicorn==0.24.0
groq==0.4.2
pillow==10.1.0
sqlalchemy==2.0.23
pymysql==1.1.0
python-dotenv==1.0.0
requests==2.31.0
```

---

### **3. Frontend Setup**

```bash
cd frontend

# Cài dependencies
npm install

# Tạo .env.local
cat > .env.local << EOF
VITE_API_BASE_URL=http://localhost:8080
VITE_AI_SERVICE_URL=http://localhost:8000
EOF

# Chạy dev server
npm run dev

# Chạy build
npm run build

# Preview build
npm run preview

# Lint code
npm run lint
```

**Environment Variables:**
```
VITE_API_BASE_URL=http://localhost:8080
VITE_AI_SERVICE_URL=http://localhost:8000
```

---

## 🗄️ Database Setup

### **PostgreSQL (for Backend)**

```bash
# macOS
brew install postgresql@15
brew services start postgresql@15

# Windows
# Chạy installer từ https://www.postgresql.org/download/windows/
```

```sql
-- Tạo database
CREATE DATABASE monio;

-- Tạo user
CREATE USER monio_user WITH PASSWORD 'your_password';

-- Cấp quyền
GRANT ALL PRIVILEGES ON DATABASE monio TO monio_user;

-- Kết nối database
psql -U monio_user -d monio -h localhost
```

### **MySQL (for AI Service - Optional)**

```bash
# macOS
brew install mysql@8.0
brew services start mysql@8.0

# Windows
# Tải từ https://dev.mysql.com/downloads/mysql/
```

```sql
-- Tạo database
CREATE DATABASE monio;

-- Tạo user
CREATE USER 'ai_service'@'localhost' IDENTIFIED BY 'password';

-- Cấp quyền
GRANT ALL PRIVILEGES ON monio.* TO 'ai_service'@'localhost';
FLUSH PRIVILEGES;
```

---

## 🌐 Environment Variables Hoàn Chỉnh

### **Backend - `backend/src/main/resources/application.yml`**

```yaml
spring:
  application:
    name: monio
  
  datasource:
    url: jdbc:postgresql://localhost:5432/monio
    username: monio_user
    password: your_password
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
  
  jpa:
    hibernate:
      ddl-auto: validate  # validate, update, create, create-drop
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your_auth_provider_url
          jwk-set-uri: https://your_auth_provider_url/.well-known/jwks.json

server:
  port: 8080
  servlet:
    context-path: /
  tomcat:
    threads:
      max: 200

logging:
  level:
    root: INFO
    com.mpt.monio: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n"
```

### **AI Service - `ai/.env`**

```env
# Groq AI
GROQ_API_KEY=gsk_your_actual_key_here

# Database
DATABASE_URL=mysql+pymysql://ai_service:password@localhost:3306/monio

# Backend Integration
SPRING_BOOT_API_URL=http://localhost:8080

# FastAPI
DEBUG=True
LOG_LEVEL=INFO
```

### **Frontend - `frontend/.env.local`**

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_AI_SERVICE_URL=http://localhost:8000
VITE_APP_NAME=Monio
```

---

## 🧪 Testing & Debugging

### **Backend Testing**

```bash
cd backend

# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AuthControllerTest

# Run with coverage
./mvnw test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### **AI Service Testing**

```bash
cd ai

# Run tests
python -m pytest

# Run with verbose
python -m pytest -v

# Run specific test
python -m pytest test_groq.py -v

# Test specific function
python -m pytest test_groq.py::test_transcribe -v

# Generate coverage
pip install pytest-cov
pytest --cov=app tests/
```

### **Frontend Testing**

```bash
cd frontend

# Run tests
npm test

# Run with coverage
npm test -- --coverage

# Run ESLint
npm run lint

# Fix linting issues
npm run lint -- --fix
```

---

## 🐛 Troubleshooting

### **Backend Issues**

| Problem | Solution |
|---------|----------|
| Port 8080 already in use | Change port in `application.yml` → `server.port: 8081` |
| Database connection error | Check `DATABASE_URL` in `application.yml`, verify PostgreSQL running |
| Maven not found | Install Maven or use `mvnw` wrapper |
| Java version mismatch | Run `java -version`, ensure Java 21 |

### **AI Service Issues**

| Problem | Solution |
|---------|----------|
| `ModuleNotFoundError: groq` | Run `pip install groq` |
| Groq API Key invalid | Check `GROQ_API_KEY` in `.env` |
| Port 8000 already in use | Run on different port: `uvicorn main:app --port 8001` |
| Audio processing error | Install `ffmpeg`: `brew install ffmpeg` |

### **Frontend Issues**

| Problem | Solution |
|---------|----------|
| Port 5173 already in use | Auto-changed to next available port |
| Import path errors | Clear `node_modules` → `npm install` |
| Build fails | Run `npm run lint -- --fix` first |

---

## 📊 Kiểm Tra Deployment

```bash
# Backend health check
curl http://localhost:8080/health

# AI Service health check
curl http://localhost:8000/health

# Frontend health check
curl http://localhost:5173

# View logs
# Backend
tail -f backend/target/*.log

# AI Service
# Logs hiển thị trong terminal

# Frontend
# Check browser console (F12 → Console tab)
```

---

## 🚀 Production Deployment

### **Chuẩn Bị**

1. **Build Backend**
   ```bash
   cd backend
   ./mvnw clean package -DskipTests
   ```

2. **Build AI Service**
   ```bash
   cd ai
   pip install -r requirements.txt
   # Tạo requirements-prod.txt
   ```

3. **Build Frontend**
   ```bash
   cd frontend
   npm run build
   ```

### **Docker Deployment**

```bash
# Build images
docker build -t monio-backend ./backend
docker build -t monio-ai ./ai
docker build -t monio-frontend ./frontend

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f
```

---

