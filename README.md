# 🌱 PlantPal — A Personal Plant Care, Watering Schedule and Growth Monitoring Platform

[![Release](https://img.shields.io/badge/Release-v1.1.0-emerald.svg)](https://github.com/Hari10012001/PlantPal/releases/tag/v1.1.0)
[![Backend Tests](https://img.shields.io/badge/Backend%20Tests-92%2F92%20PASS-brightgreen.svg)]()
[![Browser QC](https://img.shields.io/badge/Browser%20QC-30%2F30%20PASS-success.svg)]()
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)]()
[![Database](https://img.shields.io/badge/Database-MySQL%208.x-blue.svg)]()
[![Theme](https://img.shields.io/badge/UI-Glassmorphism%20%7C%20Light%20%26%20Dark-blueviolet.svg)]()

> **A modern, lightweight personal plant care companion designed to eliminate plant mortality through automated watering schedules, dynamic care recalculation, and longitudinal growth tracking.**

---

## 📌 Release Information

- **Version:** `v1.1.0`
- **Status:** Release (Production-Ready Localhost Monolith)
- **Backend Regression Suite:** 92/92 Tests Passing (100% PASS, 0 failures, 0 errors)
- **Browser Quality Control:** 30/30 Headless Chrome Flows Passing (100% PASS)
- **UI System:** Modern Glassmorphism Design System with persistent Light/Dark theme engine

---

## ✨ Key Features

- **🔐 Secure Authentication & Session Management:** Registration, login, BCrypt password hashing, secure HTTP-only cookies, and CSRF token protection (`X-XSRF-TOKEN`).
- **🌿 Plant Catalog (CRUD):** Add, update, view, and delete personal plants with species, location, category, and health status tracking.
- **🏷️ Plant Categories:** Administrator-managed botanical categorization (`Herb`, `Succulent`, `Flowering`, `Vegetable`, `Tree`, `Shrub`, `Fern`, `Cactus`).
- **📅 Dynamic Care Schedules:** Calculates next watering dates automatically based on interval days and watering history (`nextWateringDate = lastWateredDate + intervalDays`).
- **💧 Watering Event Logging:** Record deep watering events with observations and automatic recalculation of overdue/upcoming care alerts.
- **📈 Growth Observation Logs:** Longitudinal tracking of plant height (cm), leaf counts, and qualitative health notes.
- **📊 Real-Time Dashboard:** Overview of total plants, health status breakdown, overdue/due-today watering alerts, and upcoming 7-day care timeline.
- **🛡️ Dedicated Admin Panel:** System-wide metrics, user directory monitoring, and category lifecycle management.
- **🎨 Glassmorphism & Theme Engine:** Modern frosted-glass aesthetic with persistent Light and Dark theme toggle saved in `localStorage`.
- **📱 Responsive Layout:** Fully mobile, tablet, and desktop responsive with zero horizontal overflow.
- **🔒 Ownership Isolation & Anti-Enumeration:** Strict tenant boundary enforcement; cross-user plant lookups return secure `404 Not Found`.

---

## 🛠️ Technology Stack

| Layer | Technologies | Purpose |
|---|---|---|
| **Backend** | Java 21 LTS, Spring Boot 3.2.5 | Core application runtime, REST API controllers, business services |
| **Data & ORM** | MySQL 8.x, Spring Data JPA, Hibernate 6 | Relational data persistence, schema enforcement, cascade deletions |
| **Security** | Spring Security 6, BCrypt | Session authentication, role-based access control (USER, ADMIN), CSRF protection |
| **Frontend** | Vanilla HTML5, CSS3, ES6+ JavaScript | Lightweight client architecture; native `fetch()` API with zero heavy framework overhead |
| **Styling** | Glassmorphic CSS3, CSS Custom Properties | Frosted-glass backdrop filters, responsive grid, dynamic Light/Dark theme switching |
| **Build & Tooling** | Apache Maven 3.9+ | Dependency management, compilation, and automated test execution |
| **Testing** | JUnit 5, MockMvc, AssertJ, Puppeteer Core | 92 backend integration/unit tests + 30 automated browser QC end-to-end flows |

---

## 🚀 Quick Start

### Prerequisites
- **JDK 21** or later installed and configured on your `PATH`.
- **MySQL Server 8.x** running locally.
- **Apache Maven** (or Maven wrapper).

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

### 3. One-Click Launchers

- **Start Application:** Double-click **`start.bat`** (or run `.\start.bat` in terminal). The server will compile, run database migrations, seed default categories, and start on port `8080`.
- **Access Application:** Open your web browser and navigate to:
  ```text
  http://localhost:8080
  ```
- **Stop Application:** Double-click **`stop.bat`** (or run `.\stop.bat` in terminal) to gracefully shut down the port 8080 process.

---

## 👥 Default Accounts & Authentication

| Role | Email (Username) | Default Password | Access Level |
|---|---|---|---|
| **Administrator** | `admin@plantpal.local` | `<configured via ADMIN_PASSWORD>` | Full access to Category Management, User Directory & System Stats |
| **Standard User** | Register via UI (`/pages/register.html`) | User-defined password (BCrypt hashed) | Personal Plant Collection, Watering Schedules, Growth Logs & Dashboard |

---

## 📋 Realistic Demo Data (Copy-Paste Ready)

Use these realistic sample values during viva presentations or live testing:

### Add New Plant Demo Data
| Field | Plant 1 (Money Plant) | Plant 2 (Snake Plant) | Plant 3 (Aloe Vera) | Plant 4 (Red Rose) |
|---|---|---|---|---|
| **Plant Name** | Money Plant | Snake Plant | Aloe Vera | Red Rose |
| **Category** | Herb | Succulent | Succulent | Flowering |
| **Health Status** | HEALTHY | HEALTHY | NEEDS_ATTENTION | HEALTHY |
| **Interval (Days)** | 7 | 14 | 10 | 3 |
| **Last Watered** | 2026-08-24 | 2026-08-20 | 2026-08-10 | 2026-08-25 |
| **Species** | Epipremnum aureum | Sansevieria trifasciata | Aloe barbadensis miller | Rosa rubiginosa |
| **Location** | Living Room Shelf | Bedroom Corner | Sunny Balcony Sill | Terrace Garden |
| **Care Notes** | Indirect sunlight | Drought-tolerant air purifier | Lower leaves yellow; check drainage | Needs direct morning sun |

### Update Care Schedule Demo Data
| Plant | Watering Interval (Days) | Last Watered Date | Sunlight Needs | Fertilizing Interval (Days) |
|---|:---:|:---:|---|:---:|
| **Money Plant** | 7 | 2026-08-24 | Partial Sun (`PARTIAL_SUN`) | 30 |
| **Snake Plant** | 14 | 2026-08-20 | Shade (`SHADE`) | 60 |
| **Aloe Vera** | 10 | 2026-08-10 | Full Sun (`FULL_SUN`) | 45 |

### Record Watering Event Demo Data
| Target Plant | Watered Date | Notes |
|---|:---:|---|
| **Money Plant** | 2026-08-26 | Regular watering, cleaned leaves with soft wet cloth |
| **Red Rose** | 2026-08-25 | Morning deep soak; added seaweed bio-fertilizer |
| **Peace Lily** | 2026-08-26 | Watered after noticing gentle leaf droop; perked up in 2 hrs |

### Record Growth Observation Demo Data
| Target Plant | Observation Date | Height (cm) | Leaf Count | Observation Notes |
|---|:---:|:---:|:---:|---|
| **Money Plant** | 2026-08-26 | 32.5 | 14 | Two new vibrant green vines climbing up moss pole |
| **Red Rose** | 2026-08-26 | 48.0 | 22 | Three fresh crimson buds formed at stem apex |
| **Peace Lily** | 2026-08-25 | 28.2 | 9 | White spathe flower bloom fully open and glossy |

---

## 🏗️ Architecture Overview

PlantPal follows a clean **Monolithic MVC Architecture** designed for high reliability, minimal resource footprint, and zero cloud hosting cost:

```text
┌─────────────────────────────────────────────────────────────┐
│                 Client Browser Layer                        │
│   Vanilla JS (ES6+ fetch) + HTML5 + Glassmorphism CSS3      │
└──────────────────────────────▲──────────────────────────────┘
                               │ HTTP / JSON (CSRF Protected)
┌──────────────────────────────▼──────────────────────────────┐
│             Spring Boot Web MVC & Security Layer            │
│   AuthenticationFilter | CsrfFilter | 27 REST Endpoints     │
└──────────────────────────────▲──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│                    Service Business Layer                   │
│   Care Scheduling Logic | Growth Tracking | Ownership Guard │
└──────────────────────────────▲──────────────────────────────┘
                               │
┌──────────────────────────────▼──────────────────────────────┐
│             Spring Data JPA / Hibernate Layer               │
│   6 Repositories | Cascade Delete | Prepared Statements     │
└──────────────────────────────▲──────────────────────────────┘
                               │ JDBC
┌──────────────────────────────▼──────────────────────────────┐
│                      MySQL 8.x Database                     │
│   users | plant_categories | plants | care_schedules        │
│   watering_records | growth_records                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Repository Directory Structure

```text
007_PlantPal/
├── Bin/                         # Archived/obsolete project-support scripts
│   ├── .github_modernize/       # Modernization scripts retained for record
│   └── README.md                # Bin directory documentation
├── docs/                        # Technical specifications & architecture designs
│   ├── API_DOCUMENTATION.md     # 27 REST API endpoints reference specification
│   ├── DATABASE_DESIGN.md       # Relational schema, ERD, and SQL table structures
│   ├── DECISIONS.md             # Architecture & technology decisions log
│   ├── PROJECT_STATUS.md        # Milestone tracking & project phase history
│   └── REQUIREMENTS.md          # Functional & non-functional requirements specification
├── src/
│   ├── main/
│   │   ├── java/com/plantpal/   # Spring Boot backend source code
│   │   │   ├── config/          # Security, DataInitializer, WebMvc configs
│   │   │   ├── controller/      # REST API controllers
│   │   │   ├── dto/             # Request and Response transfer objects
│   │   │   ├── entity/          # JPA entity domain models
│   │   │   ├── enums/           # Domain enumerations (PlantStatus, Roles, etc.)
│   │   │   ├── exception/       # Global exception handler & custom exceptions
│   │   │   ├── repository/      # Spring Data JPA repositories
│   │   │   └── service/         # Business logic services
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/          # Frontend templates & assets
│   │           ├── css/         # plantpal.css (Glassmorphism & themes)
│   │           ├── js/          # theme.js, auth.js
│   │           ├── pages/       # HTML views (dashboard, plants, admin, etc.)
│   │           └── index.html   # Landing page
│   └── test/java/com/plantpal/  # 92 unit and integration test suite
├── .gitignore                   # Git ignore configurations
├── pom.xml                      # Maven project object model
├── README.md                    # Project landing documentation
├── start.bat                    # 1-Click Server launcher script
├── std_guide.md                 # Student study & technical learning guide (Tanglish)
├── stop.bat                     # 1-Click Server shutdown script
└── viva_guide.md                # Rapid viva revision & demo checklist (Tanglish)
```

---

## 📚 Documentation Directory

Explore the detailed technical documentation located in [`docs/`](docs/):

- 📖 [**API Documentation**](docs/API_DOCUMENTATION.md) — Comprehensive specification of all 27 REST API endpoints.
- 🗄️ [**Database Design**](docs/DATABASE_DESIGN.md) — Schema definitions, table relationships, foreign key cascades, and indexing.
- 📋 [**Requirements Specification**](docs/REQUIREMENTS.md) — Functional and non-functional requirements breakdown.
- ⚖️ [**Architecture Decisions**](docs/DECISIONS.md) — Rationale behind architectural choices and technology selections.
- 📈 [**Project Status**](docs/PROJECT_STATUS.md) — Detailed development milestones, implementation history, and progress logs.
- 🎓 [**Student Learning Guide**](std_guide.md) — Student revision manual with architecture, security mechanisms, and ELI5 Tanglish explanations.
- 🎯 [**Viva Voce Rapid Guide**](viva_guide.md) — 2-minute revision sheet, demonstration checklist, and Top 20 Q&A.

---

## 📄 License

Developed for academic and personal portfolio demonstration. Free for learning and educational use.
