# 🌱 PlantPal — A Personal Plant Care, Watering Schedule and Growth Monitoring Platform

[![Live Demo](https://img.shields.io/badge/Live%20Demo-Render-46E3B7.svg?style=for-the-badge&logo=render)](https://plantpal-jppe.onrender.com)
[![Status](https://img.shields.io/badge/Status-200%20OK%20%7C%20Active-brightgreen.svg?style=for-the-badge)]()
[![Release](https://img.shields.io/badge/Release-v1.1.1-emerald.svg?style=for-the-badge)](https://github.com/Hari10012001/PlantPal/releases/tag/v1.1.1)
[![Backend Tests](https://img.shields.io/badge/Backend%20Tests-93%2F93%20PASS-brightgreen.svg?style=for-the-badge)]()
[![Browser QC](https://img.shields.io/badge/Browser%20QC-72%2F72%20PASS-success.svg?style=for-the-badge)]()
[![Java](https://img.shields.io/badge/Java-21%20LTS-ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)]()
[![Database](https://img.shields.io/badge/Database-MySQL%208.x%20%7C%20TiDB%20Cloud-00758F.svg?style=for-the-badge&logo=mysql&logoColor=white)]()
[![Theme](https://img.shields.io/badge/UI-Glassmorphism%20%7C%20Light%20%26%20Dark-blueviolet.svg?style=for-the-badge)]()

> **A modern, lightweight personal plant care companion designed to eliminate plant mortality through automated watering schedules, dynamic care recalculation, and longitudinal growth tracking.**

---

## 🌐 Live Production Deployment

| Service | Endpoint / Link | Description |
|---|---|---|
| **Live Web Application** | 🔗 **[https://plantpal-jppe.onrender.com](https://plantpal-jppe.onrender.com)** | Deployed on Render Cloud via Docker |
| **Health Check API** | 🩺 **[`/health`](https://plantpal-jppe.onrender.com/health)** & **[`/api/health`](https://plantpal-jppe.onrender.com/api/health)** | Public readiness probe (`status: UP`, HTTP 200) |
| **Cloud Database** | ☁️ **TiDB Cloud Serverless** | High-availability MySQL 8 compatible cluster (AWS Singapore) |
| **Uptime Monitoring** | ⏱️ **UptimeRobot / BetterStack** | Target URL: `https://plantpal-jppe.onrender.com/health` |

> ℹ️ **Cold Start Note (Render Free Tier):** After 15 minutes of inactivity, Render puts the free web service to sleep. The initial cold-start request takes ~15–30 seconds for the Docker container and Spring Boot to boot. Subsequent requests respond in milliseconds.

---

## 📖 Quick Links & Viva Documentation

- 📘 **[std_guide.md](std_guide.md)** — Student Tanglish Viva & Learning Guide (ELI5 Explanations).
- 🎓 **[viva_guide.md](viva_guide.md)** — Comprehensive Viva Question & Answer Manual for final year reviews.
- 📄 **[PlantPal - JAVA FSD - Project 3.docx](PlantPal%20-%20JAVA%20FSD%20-%20Project%203.docx)** — Complete Academic Project Report (Abstract, Literature Survey, System Architecture, Requirements).
- 🔌 **[docs/API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md)** — Full 27-endpoint REST contract specification.
- 🗄️ **[docs/DATABASE_DESIGN.md](docs/DATABASE_DESIGN.md)** — Relational database schema with 6 tables and ER relationships.

---

## 📌 Release Baseline (v1.1.1)

- **Version:** `v1.1.1`
- **Status:** Production-Ready Cloud & Localhost Monolith
- **Backend Regression Suite:** 93/93 Tests Passing (100% PASS, 0 failures, 0 errors, 0 skipped)
- **Browser Quality Control Suite:** 72/72 Headless Chrome Flows Passing (100% PASS, 0 console errors, 0 API failures)
- **UI System:** Modern Glassmorphism Design System with persistent Light/Dark theme engine
- **Utility Launchers:** 1-Click Server (`start.bat`), Shutdown (`stop.bat`), and Database Inspector (`DB.bat`)

---

## ✨ Key Features

- **🔐 Secure Authentication & Session Management:** Registration, login, BCrypt password hashing, secure HTTP-only cookies, and CSRF token protection (`X-XSRF-TOKEN`).
- **🌿 Plant Catalog (CRUD):** Add, update, view, and delete personal plants with species, location, category, and health status tracking.
- **🏷️ Plant Categories:** Administrator-managed botanical categorization (`Herb`, `Succulent`, `Flowering`, `Vegetable`, `Tree`, `Shrub`, `Fern`, `Cactus`).
- **📅 Dynamic Care Schedules:** Calculates next watering dates automatically based on interval days and watering history (`nextWateringDate = lastWateredDate + intervalDays`).
- **💧 Watering Event Logging:** Record deep watering events with observations and automatic recalculation of overdue/upcoming care alerts.
- **📈 Growth Observation Logs:** Longitudinal tracking of plant height (cm), leaf counts, and qualitative health notes.
- **📊 Real-Time Dashboard:** Overview of total plants, health status breakdown, overdue/due-today watering alerts, and upcoming 7-day care timeline.
- **🛡️ Dedicated Admin Panel:** System-wide metrics, user directory monitoring, and category lifecycle management with referential integrity safeguards.
- **🎨 Glassmorphism & Theme Engine:** Modern frosted-glass aesthetic with persistent Light and Dark theme toggle saved in `localStorage`.
- **📱 Responsive Layout:** Fully mobile, tablet, and desktop responsive with zero horizontal overflow.
- **🔒 Ownership Isolation & Anti-Enumeration:** Strict tenant boundary enforcement; cross-user plant lookups return secure `404 Not Found`.

---

## 🛠️ Technology Stack

| Layer | Technologies | Purpose |
|---|---|---|
| **Backend** | Java 21 LTS, Spring Boot 3.2.5 | Core application runtime, REST API controllers, business services |
| **Data & ORM** | MySQL 8.x / TiDB Cloud, Spring Data JPA, Hibernate 6 | Relational data persistence, schema enforcement, cascade deletions |
| **Connection Pool** | HikariCP | High-performance DB connection pooling with cloud keepalive |
| **Security** | Spring Security 6, BCrypt | Session authentication, role-based access control (USER, ADMIN), CSRF protection |
| **Frontend** | Vanilla HTML5, CSS3, ES6+ JavaScript | Lightweight client architecture; native `fetch()` API with zero heavy framework overhead |
| **Styling** | Glassmorphic CSS3, CSS Custom Properties | Frosted-glass backdrop filters, responsive grid, dynamic Light/Dark theme switching |
| **Deployment** | Docker (Multi-stage Temurin JRE), Render Web Services | Cloud-ready containerization and auto-deployment blueprint (`render.yaml`) |
| **Build & Tooling** | Apache Maven 3.9+ | Dependency management, compilation, and automated test execution |
| **Testing** | JUnit 5, MockMvc, AssertJ, Puppeteer Core | 93 backend tests + 72 automated browser QC end-to-end flows |

---

## 🚀 Quick Start (Local Setup)

### Prerequisites
- **JDK 21** or later installed and configured on your `PATH`.
- **MySQL Server 8.x** running locally.
- **Apache Maven 3.9+** (or Maven wrapper).

### 1. Database Setup
Create a MySQL database for PlantPal:
```sql
CREATE DATABASE plantpal_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Environment Variables Configuration
Configure the required database and admin credentials in your environment (never commit real credentials):

**Windows (PowerShell):**
```powershell
$env:DB_PASSWORD = "<your-database-password>"
$env:ADMIN_PASSWORD = "<your-admin-password>"
```

**Linux / macOS (Bash):**
```bash
export DB_PASSWORD="<your-database-password>"
export ADMIN_PASSWORD="<your-admin-password>"
```

### 3. One-Click Launchers (Windows)

- **Start Application:** Double-click **`start.bat`** (or run `.\start.bat` in terminal). The server will compile, run database migrations, seed default categories, wait for health readiness, and automatically launch your browser on port `8080`.
- **Stop Application:** Double-click **`stop.bat`** (or run `.\stop.bat` in terminal) to gracefully shut down the port 8080 process.
- **Inspect Database:** Double-click **`DB.bat`** (or run `.\DB.bat` in terminal) to inspect live database tables without running destructive queries:
  - `.\DB.bat` — Overall record counts and recent users summary
  - `.\DB.bat tables` — Lists all database tables
  - `.\DB.bat data` — Shows plant care schedules and next watering calculations
  - `.\DB.bat users` — Lists registered user directory and roles
  - `.\DB.bat plants` — Lists all registered plants and owners
  - `.\DB.bat categories` — Lists botanical plant categories
  - `.\DB.bat watering` — Lists recent watering event logs
  - `.\DB.bat growth` — Lists recent plant growth observation logs

---

## 👥 Default Accounts & Authentication

| Role | Email (Username) | Default Password | Access Level |
|---|---|---|---|
| **Administrator** | `admin@plantpal.local` | `<configured via ADMIN_PASSWORD>` | Full access to Category Management, User Directory & System Stats |
| **Standard User** | Register via UI (`/pages/register.html`) | User-defined password (BCrypt hashed) | Personal Plant Collection, Watering Schedules, Growth Logs & Dashboard |

---

## 📂 Repository Structure

```text
007_PlantPal/
├── Bin/                       # Operational scripts & maintenance tools
│   ├── README.md              # Bin utilities documentation
│   └── .github_modernize/     # Git hooks and modernize scripts
├── docs/                      # Architectural & Engineering Documentation
│   ├── API_DOCUMENTATION.md   # Complete 27-endpoint REST contract reference
│   ├── DATABASE_DESIGN.md     # 6-table relational schema and ER diagram
│   ├── DECISIONS.md           # Architectural Decision Records (ADRs 1-13)
│   ├── PROJECT_STATUS.md      # Milestones, verification metrics, and releases
│   └── REQUIREMENTS.md        # Functional and non-functional requirements
├── src/
│   ├── main/
│   │   ├── java/com/plantpal/ # Spring Boot backend source code
│   │   │   ├── config/        # Security, MVC, and Data Initializer
│   │   │   ├── controller/    # REST & Admin Controllers
│   │   │   ├── dto/           # Request & Response Data Transfer Objects
│   │   │   ├── entity/        # 6 JPA Database Entities
│   │   │   ├── enums/         # Status & Role Enums
│   │   │   ├── exception/     # Global Error Handling & Exceptions
│   │   │   ├── repository/    # Spring Data JPA Repositories
│   │   │   └── service/       # Business Logic & Validation Services
│   │   └── resources/
│   │       ├── application.properties # Spring configuration (dynamic PORT & HikariCP keepalive)
│   │       └── static/        # Frontend Client Suite
│   │           ├── css/       # plantpal.css (Glassmorphic theme system)
│   │           ├── js/        # api.js, auth.js, theme.js
│   │           ├── pages/     # HTML5 views (dashboard, plants, profile, admin)
│   │           └── index.html # Landing page
│   └── test/java/com/plantpal/# 93 Backend Unit & MockMvc Tests
├── .gitignore                 # Strict ignore rules for artifacts & secrets
├── DB.bat                     # 1-Click Local Database Inspector
├── Dockerfile                 # Multi-stage production container build
├── pom.xml                    # Maven build descriptor
├── README.md                  # Master project presentation
├── render.yaml                # Render cloud deployment blueprint
├── start.bat                  # 1-Click Server Launcher with auto-browser opening
├── std_guide.md               # Student Tanglish viva & learning guide
├── stop.bat                   # 1-Click Server Termination
└── viva_guide.md              # Comprehensive viva question & answer manual
```

---

## 🧪 Testing & Quality Assurance

### 1. Backend Integration Tests (93 Tests)
Run the complete backend test suite:
```bash
mvn clean test
```
- **93 Tests Run (100% PASS, 0 Failures, 0 Errors, 0 Skipped)** covering all 27 REST endpoints, lightweight health check probes (`/health`, `/api/health`), RBAC security, CSRF protection, cascade deletions, and database services.

### 2. Browser Smoke QC Automation (72 Flows)
Run the headless Chrome Puppeteer QC suite covering 10 functional sections:
```bash
node scratch/qc-72-master.js
```
- **72 Flows Verified (100% PASS):** Public Landing, Authentication, Plant CRUD, Care Schedules, Watering Events, Growth Observations, Profile, Admin Panel, Error Handling & Mobile Responsive Layouts.

---

## ☁️ Cloud & Docker Deployment

### Docker Container Build
```bash
docker build -t plantpal:latest .
docker run -p 8080:8080 -e DB_PASSWORD="<password>" -e ADMIN_PASSWORD="<admin_pass>" plantpal:latest
```

### Render Deployment Configuration (`render.yaml`):
```yaml
services:
  - type: web
    name: plantpal
    env: docker
    dockerfilePath: Dockerfile
    plan: free
    healthCheckPath: /health
    envVars:
      - key: PORT
        value: 8080
      - key: DB_URL
        sync: false
      - key: DB_USERNAME
        sync: false
      - key: DB_PASSWORD
        sync: false
      - key: ADMIN_PASSWORD
        sync: false
```

---

## 📄 License & Attribution

Developed as a Final-Year Java Full Stack Main Project (2026–2027) by **Hariharan P**.
Licensed under the [MIT License](LICENSE).
