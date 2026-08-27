# REQUIREMENTS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform
## Version 2 — Updated after Gate 2 Conditional Approval

---

## 1. Project Overview

PlantPal is a simple web application that helps users manage their household, indoor, balcony,
terrace, or small-garden plants. It enables plant lovers to register their plants, set care
schedules, track watering history, record growth observations, and monitor plant health through
a clean dashboard.

---

## 2. Problem Statement

Plant owners often forget to water their plants on time or lose track of when a plant was
last watered. There is no lightweight, free, locally-run tool for personal plant care
management that a beginner developer can build and use.

---

## 3. Existing System — Limitations

- Mobile apps with subscriptions
- AI-powered plant disease detection (overkill)
- Smart IoT irrigation systems (overkill)
- Cloud-hosted services (require internet, subscriptions, and paid APIs)

---

## 4. Proposed System

PlantPal: a local-only, free, Java Spring Boot + MySQL plant care management platform.

Users can:
- Register and log in securely
- Add plants with basic details
- Organize plants by category
- Set a watering interval (in days)
- Optionally record the last watered date (may be null for new plants)
- Calculate the next watering date automatically (when last watered date is known)
- Record watering events over time
- Record growth measurements over time
- View a dashboard with plant health and care statistics

---

## 5. Objectives

1. Build a fully functional plant care management system as a final-year Java Full Stack project
2. Demonstrate Java OOP, Spring Boot, REST APIs, JPA, MySQL, Spring Security, and role-based access
3. Keep the system simple enough for a beginner to understand, explain, and maintain in a viva
4. Run entirely on localhost with zero cost
5. Produce clean, professional, portfolio-quality code and UI

---

## 6. Target Users

- Plant enthusiasts managing household plants
- Students building a Java Full Stack portfolio project

---

## 7. Scope

### 7.1 In Scope (MVP)

- User registration and login (session-based, no auto-login after register)
- Role-based access (USER, ADMIN)
- Plant categories management by Admin
- Plant CRUD by User (own plants only)
- Watering interval and care schedule per plant
- Optional lastWateredDate (null for new plants)
- Watering records history
- Growth records history
- Dashboard statistics and alerts
- Plant search and filter
- Plant status: HEALTHY, NEEDS_ATTENTION, INACTIVE
- Watering status: WATER_TODAY, WATER_UPCOMING, WATER_OVERDUE, NOT_SET
- Admin: read-only user overview, category CRUD, system statistics

### 7.2 Future Scope

- Local plant photo upload
- PDF/CSV report export
- Dark mode

### 7.3 Explicitly Out of Scope

- AI, ML, computer vision, plant disease detection
- IoT, smart irrigation
- Weather API
- Email or SMS notifications
- Cloud hosting or deployment
- Payment or subscription
- External plant databases or APIs
- React or any frontend framework
- Microservices

---

## 8. Functional Requirements

### FR-AUTH: Authentication
- FR-AUTH-01: Users register with full name, email, and password
- FR-AUTH-02: Passwords are BCrypt hashed — never stored plain
- FR-AUTH-03: Users log in with email and password (JSON endpoint)
- FR-AUTH-04: No auto-login after registration — user must log in separately
- FR-AUTH-05: Users can log out (session invalidated)
- FR-AUTH-06: Unauthenticated requests are rejected (401)

### FR-CAT: Plant Categories
- FR-CAT-01: Admin can add, edit, delete plant categories
- FR-CAT-02: Category cannot be deleted if plants use it
- FR-CAT-03: Users see all categories in dropdowns when adding/editing plants
- FR-CAT-04: 8 seed categories pre-loaded: Herb, Succulent, Flowering, Vegetable, Tree, Shrub, Fern, Cactus

### FR-PLANT: Plant Management
- FR-PLANT-01: Users can add a plant with name (required), category (required), status (required), watering interval (required)
- FR-PLANT-02: Location, description, last watered date, sunlight needs, and fertilizing interval are optional
- FR-PLANT-03: lastWateredDate may be null (plant never yet watered)
- FR-PLANT-04: Users can view, edit, and delete their own plants
- FR-PLANT-05: Deleting a plant cascades to all its care, watering, and growth records
- FR-PLANT-06: Users can search plants by name and filter by category and status
- FR-PLANT-07: Users can ONLY access their own plants — other users' plants return 404

### FR-CARE: Care Schedules
- FR-CARE-01: Each plant has exactly one care schedule (created automatically with the plant)
- FR-CARE-02: Care schedule stores wateringIntervalDays (required, min 1) and lastWateredDate (optional)
- FR-CARE-03: nextWateringDate = lastWateredDate + wateringIntervalDays (computed in Java)
- FR-CARE-04: If lastWateredDate is null, wateringStatus = NOT_SET and nextWateringDate = null
- FR-CARE-05: Users can update care schedule at any time

### FR-WATER: Watering Records
- FR-WATER-01: Users can record a watering event (date required, notes optional)
- FR-WATER-02: wateredDate must not be in the future
- FR-WATER-03: Recording a watering event updates care_schedules.last_watered_date if the date is more recent
- FR-WATER-04: Users can view full watering history for their plants

### FR-WATER-STATUS: Watering Status Calculation
- FR-WS-01: NOT_SET    if lastWateredDate is null
- FR-WS-02: WATER_OVERDUE  if nextWateringDate < today
- FR-WS-03: WATER_TODAY    if nextWateringDate == today
- FR-WS-04: WATER_UPCOMING if nextWateringDate > today

### FR-GROWTH: Growth Records
- FR-GROWTH-01: Users can add growth records (date required; height, leaf count, notes all optional individually)
- FR-GROWTH-02: At least one of height, leaf count, or notes must be provided per record
- FR-GROWTH-03: Users can view full growth history for their plants

### FR-DASH: Dashboard
- FR-DASH-01: Show total plants, healthy count, needs-attention count, inactive count
- FR-DASH-02: Show count of plants needing water today
- FR-DASH-03: Show count of overdue plants
- FR-DASH-04: Show 5 most recently added plants
- FR-DASH-05: Show upcoming care list for next 7 days (excludes NOT_SET plants)

### FR-ADMIN: Admin Panel
- FR-ADMIN-01: Admin can VIEW all registered users (read-only: name, email, role, plant count, joined date)
- FR-ADMIN-02: Admin CANNOT edit or delete users in MVP
- FR-ADMIN-03: Admin can add, edit, and delete plant categories (with in-use guard)
- FR-ADMIN-04: Admin can view system statistics (total users, plants, records, categories)

---

## 9. Non-Functional Requirements

- NFR-01: Application runs on a 4 GB RAM laptop
- NFR-02: Standard operations complete within 3 seconds
- NFR-03: Passwords stored as BCrypt hashes only
- NFR-04: Users cannot access other users' data (ownership enforced in service layer)
- NFR-05: Code is beginner-readable with clear naming
- NFR-06: UI is responsive on desktop and tablet (Bootstrap 5 grid)
- NFR-07: Application runs entirely on localhost
- NFR-08: Zero cost — no paid APIs or services
- NFR-09: Runs with: mvn spring-boot:run
- NFR-10: CSRF protection enabled using CookieCsrfTokenRepository

---

## 10. User Roles

### USER
- Register, login, logout
- Manage own plants (CRUD)
- Set and update care schedules
- Record watering events
- Record growth observations
- View dashboard and plant histories
- Search and filter own plants

### ADMIN
- Login, logout (no public admin registration)
- View all users (read-only overview)
- Manage plant categories (full CRUD)
- View system statistics

---

## 11. Watering Status Logic (Authoritative)

lastWateredDate is OPTIONAL. It may be null for newly added plants.

IF   lastWateredDate IS NULL        -> wateringStatus = NOT_SET, nextWateringDate = null
ELIF nextWateringDate < today       -> wateringStatus = WATER_OVERDUE
ELIF nextWateringDate == today      -> wateringStatus = WATER_TODAY
ELSE                                -> wateringStatus = WATER_UPCOMING

Where nextWateringDate = lastWateredDate + wateringIntervalDays (Java LocalDate arithmetic).

---

## 12. Plant Status Values

- HEALTHY       — Plant is being cared for on schedule
- NEEDS_ATTENTION — Plant needs user attention (overdue or manually flagged)
- INACTIVE      — Plant is no longer actively tracked

---

## 13. MVP Feature Classification

MUST HAVE:
  Authentication (register, login, logout), Plant CRUD, Plant categories (admin),
  Care schedule (watering interval, optional lastWateredDate), Watering records,
  Watering status calculation (including NOT_SET), Growth records, Dashboard stats,
  Search, filter, Admin user overview (read-only), Admin category CRUD

SHOULD HAVE:
  Input validation (frontend + backend), Meaningful errors, Empty states,
  Responsive layout, CSRF protection

COULD HAVE:
  Local plant photo upload, PDF/CSV export, Dark mode

OUT OF SCOPE:
  AI, ML, IoT, weather API, email, SMS, cloud, payment, computer vision
