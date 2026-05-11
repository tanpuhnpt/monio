# 📚 Documentation Index

Welcome to Monio Documentation! This guide helps you navigate all available documentation.

---

## 🎯 Quick Start

### **For First-Time Users**
1. Read: [README.md](../README.md) - Overview & Architecture
2. Read: [SETUP_GUIDE.md](./SETUP_GUIDE.md) - Installation & Configuration
3. Run: Follow the 5-step setup process
4. Test: Access http://localhost:5173 (Frontend)

### **For Developers**
1. Read: [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md) - Technical Architecture
2. Read: [API_FEATURES.md](./API_FEATURES.md) - API Documentation
3. Explore: [Source Code Structure](#source-code-structure)

### **For Project Managers**
1. Read: [README.md](../README.md) - Project Overview
2. Reference: Features & Technology Stack
3. Check: [Deployment Guide](#deployment-deployment-guide)

---

## 📖 Main Documentation Files

### **[README.md](../README.md)** 🌟
Main project documentation with:
- ✅ Project overview & context
- ✅ Technology stack with versions
- ✅ System architecture diagrams (text-based)
- ✅ Data flows (Voice, OCR, Chat)
- ✅ Database schema
- ✅ How to run the system (5 steps)
- ✅ Project folder structure
- ✅ API endpoints overview
- ✅ Environment variables
- ✅ Docker deployment

**Read when:** Starting with the project, understanding overview

---

### **[SETUP_GUIDE.md](./SETUP_GUIDE.md)** 🔧
Detailed setup and configuration guide:
- ✅ System requirements (macOS, Windows)
- ✅ Step-by-step installation
- ✅ Backend setup (Spring Boot)
- ✅ AI Service setup (FastAPI)
- ✅ Frontend setup (React + Vite)
- ✅ Database setup (PostgreSQL, MySQL)
- ✅ Environment variables (all services)
- ✅ Testing procedures
- ✅ Troubleshooting guide
- ✅ Production deployment

**Read when:** Installing the project, configuring services, troubleshooting

---

### **[ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md)** 🏗️
In-depth technical architecture:
- ✅ Frontend Layer architecture
- ✅ Backend Layer (Spring Boot) detailed structure
- ✅ AI Service Layer (FastAPI) detailed structure
- ✅ Component interactions
- ✅ Data flow diagrams (detailed)
- ✅ Communication protocols
- ✅ Error handling patterns
- ✅ Database schema SQL

**Read when:** Understanding system design, contributing to codebase, debugging

---

### **[API_FEATURES.md](./API_FEATURES.md)** 📡
Complete API documentation:
- ✅ Authentication API (register, login, refresh)
- ✅ Wallet API (CRUD operations)
- ✅ Transaction API (CRUD operations)
- ✅ Category API
- ✅ Report API (summary, by-category, by-date)
- ✅ Chat API (AI integration)
- ✅ OCR API (invoice extraction)
- ✅ Audio API (speech-to-text)
- ✅ Features overview
- ✅ Transaction types & categories
- ✅ Response examples

**Read when:** Integrating frontend/backend, testing API, building clients

---

### **[IMAGE_GUIDE.md](./images/README.md)** 🖼️
Image directory structure & guidelines:
- ✅ Directory layout for images
- ✅ Image naming conventions
- ✅ Resolution requirements
- ✅ Tools for creating images
- ✅ Image integration in docs
- ✅ Progress checklist

**Read when:** Adding screenshots or diagrams, preparing presentations

---

## 📁 Documentation Structure

```
monio/
├── README.md                          # Main documentation (YOU ARE HERE)
│
├── docs/
│   ├── SETUP_GUIDE.md                # Installation & configuration
│   ├── ARCHITECTURE_DETAILED.md      # Technical architecture
│   ├── API_FEATURES.md               # API documentation
│   ├── DOCUMENTATION_INDEX.md        # This file
│   │
│   ├── images/                        # Image storage
│   │   ├── README.md                 # Image guide
│   │   ├── architecture/             # System diagrams
│   │   │   ├── system-architecture.png
│   │   │   ├── data-flow-voice.png
│   │   │   ├── data-flow-ocr.png
│   │   │   ├── data-flow-chat.png
│   │   │   └── database-schema.png
│   │   ├── ui-screenshots/           # UI demo images
│   │   │   ├── login-page.png
│   │   │   ├── dashboard.png
│   │   │   └── ...
│   │   ├── chat-ui/                  # Chat demo images
│   │   │   ├── chat-interface.png
│   │   │   └── ...
│   │   ├── ocr-samples/              # OCR demo images
│   │   │   ├── camera-interface.png
│   │   │   └── ...
│   │   └── voice-demo/               # Voice demo images
│   │       ├── recording-interface.png
│   │       └── ...
│   │
│   └── architecture/                 # Additional architecture docs
│
├── ai/                                # AI Service (FastAPI)
├── backend/                           # Backend (Spring Boot)
└── frontend/                          # Frontend (React + Vite)
```

---

## 📚 Technology-Specific Guides

### **Frontend (React + Vite)**
- Main doc: [README.md](../README.md#frontend--🎨)
- Setup: [SETUP_GUIDE.md](./SETUP_GUIDE.md#3-frontend-setup)
- Architecture: [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md#3-ai-service-layer-fastapi)
- API calls: [API_FEATURES.md](./API_FEATURES.md)

### **Backend (Spring Boot)**
- Main doc: [README.md](../README.md#backend--🚀)
- Setup: [SETUP_GUIDE.md](./SETUP_GUIDE.md#1-backend-setup)
- Architecture: [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md#2-backend-layer-spring-boot)
- API endpoints: [API_FEATURES.md](./API_FEATURES.md#backend-rest-api-spring-boot)

### **AI Service (FastAPI + Python)**
- Main doc: [README.md](../README.md#ai-service--🤖)
- Setup: [SETUP_GUIDE.md](./SETUP_GUIDE.md#2-ai-service-setup)
- Architecture: [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md#3-ai-service-layer-fastapi)
- API endpoints: [API_FEATURES.md](./API_FEATURES.md#ai-service-endpoints-fastapi)

---

## 🔑 Key Resources by Role

### **👨‍💻 Backend Developer**
1. [SETUP_GUIDE.md](./SETUP_GUIDE.md#1-backend-setup) - Backend setup
2. [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md#2-backend-layer-spring-boot) - Backend structure
3. [API_FEATURES.md](./API_FEATURES.md#backend-rest-api-spring-boot) - REST API docs
4. [README.md](../README.md#-project-structure) - Project structure

**Key Files to Study:**
- `backend/src/main/java/com/mpt/monio/` - Backend code
- `backend/pom.xml` - Dependencies
- `backend/src/main/resources/application.yml` - Configuration

---

### **🎨 Frontend Developer**
1. [SETUP_GUIDE.md](./SETUP_GUIDE.md#3-frontend-setup) - Frontend setup
2. [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md#1-frontend-layer) - Frontend structure
3. [API_FEATURES.md](./API_FEATURES.md) - API endpoints to consume
4. [images/ui-screenshots/](./images/ui-screenshots/) - UI reference

**Key Files to Study:**
- `frontend/src/App.jsx` - Main component
- `frontend/src/pages/` - Page components
- `frontend/src/services/` - API services
- `frontend/package.json` - Dependencies

---

### **🤖 AI/ML Developer**
1. [SETUP_GUIDE.md](./SETUP_GUIDE.md#2-ai-service-setup) - AI service setup
2. [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md#3-ai-service-layer-fastapi) - AI architecture
3. [API_FEATURES.md](./API_FEATURES.md#ai-service-endpoints-fastapi) - AI APIs
4. [README.md](../README.md#data-flow-ghi-âm-giọng-nói) - Data flows

**Key Files to Study:**
- `ai/app/services/` - Business logic
- `ai/app/api/` - API routes
- `ai/main.py` - App entry point
- `ai/requirements.txt` - Dependencies

---

### **🏢 Project Manager / Team Lead**
1. [README.md](../README.md) - Full overview
2. [SETUP_GUIDE.md](./SETUP_GUIDE.md) - Deployment requirements
3. [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md) - High-level architecture
4. [API_FEATURES.md](./API_FEATURES.md#features) - Feature overview

---

### **🚀 DevOps / System Admin**
1. [SETUP_GUIDE.md](./SETUP_GUIDE.md#-production-deployment) - Production setup
2. [README.md](../README.md#-docker-deployment) - Docker deployment
3. [README.md](../README.md#environment-variables) - Configuration
4. [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md) - System architecture

---

## ❓ Finding Information

### **I want to...**

#### **Install & Run the System**
👉 [SETUP_GUIDE.md](./SETUP_GUIDE.md)

#### **Understand the Architecture**
👉 [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md) + [README.md](../README.md) Diagrams

#### **Use the APIs**
👉 [API_FEATURES.md](./API_FEATURES.md)

#### **Deploy to Production**
👉 [SETUP_GUIDE.md#-production-deployment](./SETUP_GUIDE.md#-production-deployment) + [README.md#-docker-deployment](../README.md#-docker-deployment)

#### **Troubleshoot Issues**
👉 [SETUP_GUIDE.md#-troubleshooting](./SETUP_GUIDE.md#-troubleshooting) or Search documentation

#### **Add Screenshots/Images**
👉 [images/README.md](./images/README.md)

#### **Understand Data Flow**
👉 [README.md](../README.md#-data-flow-ghi-âm-giọng-nói) + [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md#communication-protocols)

#### **Configure Services**
👉 [SETUP_GUIDE.md#-environment-variables-full](./SETUP_GUIDE.md#-environment-variables-full)

---

## 🔄 Documentation Update Checklist

When updating documentation, ensure:

- [ ] All code examples are tested
- [ ] API responses are accurate
- [ ] File paths are correct
- [ ] Links work properly
- [ ] Screenshots are up-to-date
- [ ] Version numbers are accurate
- [ ] Dependencies are listed

---

## 🎓 Learning Path

### **Week 1: Fundamentals**
1. Read: [README.md](../README.md)
2. Read: [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md)
3. Do: [SETUP_GUIDE.md](./SETUP_GUIDE.md) - Install all services
4. Test: Access each service (8080, 8000, 5173)

### **Week 2: Deep Dive**
1. Read: [API_FEATURES.md](./API_FEATURES.md)
2. Do: Test API endpoints with Postman/Curl
3. Study: Source code in each module
4. Do: Make a small contribution

### **Week 3: Integration**
1. Do: Full E2E testing
2. Do: Create example flows (Voice, OCR, Chat)
3. Do: Document your learnings
4. Do: Mentor newcomers

---

## 📖 External Resources

### **Frontend**
- React: https://react.dev
- Vite: https://vitejs.dev
- Tailwind CSS: https://tailwindcss.com
- Recharts: https://recharts.org

### **Backend**
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Data JPA: https://spring.io/projects/spring-data-jpa
- Spring Security: https://spring.io/projects/spring-security
- PostgreSQL: https://www.postgresql.org

### **AI Service**
- FastAPI: https://fastapi.tiangolo.com
- Groq API: https://console.groq.com
- SQLAlchemy: https://www.sqlalchemy.org
- PyDUB: https://github.com/jiaaro/pydub

### **DevOps**
- Docker: https://docs.docker.com
- Docker Compose: https://docs.docker.com/compose
- Maven: https://maven.apache.org

---

## 💬 Documentation Style Guide

When writing documentation:

✅ **DO:**
- Use clear, concise language
- Include code examples
- Add step-by-step instructions
- Include screenshots/diagrams
- Link to relevant sections
- Use emoji for visual clarity

❌ **DON'T:**
- Use overly technical jargon
- Write walls of text
- Assume prior knowledge
- Forget to update examples
- Create inconsistent formatting

---

## 🔗 Quick Navigation

| Document | Purpose | Read Time |
|----------|---------|-----------|
| [README.md](../README.md) | Overview & Quick Start | 15 min |
| [SETUP_GUIDE.md](./SETUP_GUIDE.md) | Installation & Configuration | 30 min |
| [ARCHITECTURE_DETAILED.md](./ARCHITECTURE_DETAILED.md) | Technical Deep Dive | 45 min |
| [API_FEATURES.md](./API_FEATURES.md) | API Reference | 30 min |
| [images/README.md](./images/README.md) | Image Guidelines | 10 min |

---

## 📞 Need Help?

1. **Check Documentation** - Search relevant docs first
2. **Check Troubleshooting** - [SETUP_GUIDE.md#-troubleshooting](./SETUP_GUIDE.md#-troubleshooting)
3. **GitHub Issues** - Check existing issues
4. **Contact** - Reach out to team

---

**Last Updated:** May 9, 2026

**Maintained by:** Development Team

**Next Review:** May 31, 2026

---

