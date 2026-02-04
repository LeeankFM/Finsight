# FinSight - Intelligent Portfolio Manager 🐘🚀

FinSight is a high-performance, intelligent financial ecosystem built with **Hexagonal Architecture** and modern microservices. It features a professional-grade terminal for tracking strategies, market trends, and AI-powered quant analysis.

## 🏗️ Architecture & Modules

The system is divided into specialized modules following clean architecture principles:

### 1. `frontend` (Next.js 14 / React 18)
- **Role**: Professional Trading Terminal.
- **Technologies**: Tailwind CSS, Shadcn UI, Recharts, Framer Motion, Three.js.
- **Design**: Monochrome premium aesthetic with Glassmorphism and 3D backgrounds.
- **Key Features**: 
    - **Terminal Central**: Real-time aggregation of Total Net Worth.
    - **Quick Trade**: Universal dialog for BUY/SELL execution across any strategy.
    - **Strategy Hub**: Workspace isolation for different investment theses.
    - **Cash Management**: Secure Fund Transfer system for liquid capital.

### 2. `portfolio-manager` (Java/Spring Boot 3)
- **Role**: Core Business Logic & Persistence.
- **Technologies**: JPA/Hibernate, Redis (Real-time Analytics), PostgreSQL.
- **Hexagonal Layers**: Pure Domain rules, Application Ports/Services, and Infrastructure Adapters.
- **Functions**: ROI calculation, position management, and metric broadcast.

### 3. `data-collector` (Python/FastAPI)
- **Role**: High-speed Data Ingestion & Price Oracle.
- **Technologies**: Polars (ETL), PyMongo, `yfinance`.
- **Database**: MongoDB (Raw analytical data storage).
- **Oracle System**: Real-time market synchronization using Redis as a high-speed shared state.

## 📊 Real-time Intelligence (The Redis Edge)
We leverage Redis to power high-frequency features without stressing the relational database:
- **Global Asset Trends**: Sorted Set tracking most traded assets by volume globally via HyperLogLog-inspired logic.
- **Fair ROI Ranking**: Performance metrics normalized against cumulative deposits/withdrawals to ensure accurate leaderboard positions.

## 🛠️ Tech Stack & Infrastructure
- **Languages**: Java 21, Python 3.11, TypeScript.
- **Orchestration**: Docker Compose.
- **Persistence**: PostgreSQL 15, MongoDB 6.0, Redis 7.0.
- **UI/UX**: Custom Monochrome-glass theme.

## 🚀 Deployment Guide

1.  **Environment**: Ensure `docker` and `pnpm` are installed.
2.  **Infrastructure**: 
    ```bash
    docker compose up -d
    ```
3.  **Frontend**:
    ```bash
    cd frontend && pnpm install && pnpm dev
    ```
4.  **Backend Services**:
    - **Java**: `cd portfolio-manager && ./mvnw spring-boot:run`
    - **Python**: `cd data-collector && uvicorn main:app --reload`

## ⚠️ Troubleshooting
### PostgreSQL Port Conflict
We use **port 5433** for the PostgreSQL container to avoid conflicts with local PostgreSQL installations on Windows (which typically use 5432).
- **App Connection**: `jdbc:postgresql://localhost:5433/finsight_db`
- **Docker External Access**: `localhost:5433`

## 🔮 Future Roadmap (Phase 6+)
- [ ] **AI Quant Analyst**: Large Language Model integration for portfolio risk assessment.
- [ ] **API Gateway Integration**: Unified entry point with rate limiting and JWT security.
- [ ] **Real-time Price WebSockets**: Live asset price updates with interactive TradingView-style charts.
