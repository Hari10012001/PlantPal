# 🌱 PlantPal — Final Viva Quick Revision Guide
### Last-Minute Study Material for College Viva | Language: Simple ELI5 Tanglish

---

## 1. PROJECT IN ONE LINE
- **Name:** PlantPal — Personal Plant Care, Watering Schedule & Growth Monitoring Platform.
- **Definition:** Java Spring Boot + MySQL + Vanilla JS use panni build panna full-stack plant care diary & smart watering assistant.
- **ELI5 Analogy:** Namma veetla irukura plants-ku digital diary madhiri; eppo thanni oothunom nu track panni, adutha thadava eppo oothanum nu automatic-a alert pannum.

---

## 2. WHY THIS PROJECT?
- **Real-World Problem:** Plants saaguradhuku main reason **irregular watering** (overwatering or underwatering).
- **Why it matters:** Ovvoru plant-kum different watering interval theva padum (e.g., Ferns: 3 days, Succulents: 15 days). Manushanala manual-a nyabagam vechuka mudiyadhu.
- **Main Objective:** Automated date calculation moolama plant mortality rate-ah zero aakuradhu.

---

## 3. EXISTING SYSTEM
- **Manual Methods:** Sticky notes, paper diary, phone calendar reminders, Excel sheets.
- **Disadvantages:** Notes tholanju pogum, reminders dynamic-a calculate aagadhu, growth history track panna mudiyadhu, centralized dashboard irukaadhu.

---

## 4. PROPOSED SYSTEM (PLANTPAL)
- **Centralized Platform:** User thanga plants-ah add panni categories, care schedules, watering history, and growth logs-ah ore edathula manage pannalaam.
- **Dynamic Calculation:** `lastWateredDate + interval = nextWateringDate` nu system-e calculate panni live alerts tharum.

---

## 5. MAIN FEATURES
1. **User & Admin Authentication:** Secure login, session cookies, BCrypt password hashing.
2. **Plant Catalog & Categories:** Categories based plant CRUD, live search & filters.
3. **Care Schedule Engine:** Dynamic watering status (`WATER_TODAY`, `WATER_UPCOMING`, `WATER_OVERDUE`, `NOT_SET`).
4. **Watering History:** Full timeline log with date and care notes.
5. **Growth Tracker:** Track plant height (cm), leaf count, and observation notes over time.
6. **Live Dashboard:** Total plants, health distribution, upcoming 7-day care timeline.
7. **Admin Panel:** Category CRUD with in-use deletion protection, user directory, system stats.

---

## 6. TECHNOLOGY STACK

| Layer | Technology | Why Used |
|---|---|---|
| **Backend** | Java 17/21 LTS, Spring Boot 3.2.5 | Enterprise OOP language, auto-configuration, embedded Tomcat. |
| **Security** | Spring Security 6 | Session management, BCrypt hashing, CSRF protection, RBAC. |
| **Persistence** | Spring Data JPA / Hibernate | ORM mapping Java entities to MySQL tables without raw SQL JDBC. |
| **Database** | MySQL 8.x | Relational ACID storage, foreign keys, cascading deletion. |
| **Frontend** | HTML5, CSS3, JavaScript (Fetch API) | Lightweight, 100% offline, zero npm/Node build steps needed. |
| **Build & Test**| Maven, JUnit 5, MockMvc, Puppeteer | Build automation, 92 unit/integration tests, 30 browser UI tests. |

---

## 7. WHY THESE TECHNOLOGIES?
- **Java & Spring Boot:** Robust backend architecture, fast development with embedded server.
- **Spring Data JPA:** Java object-ah database table-oda direct-a connect panna help pannum.
- **MySQL:** Structured relational plant data-va safe-a store panna.
- **Spring Security:** Passwords-ah hash panna, unauthorized access & CSRF attacks thadukka.
- **Vanilla JS:** React mathiri heavy build tools theva illaama native browser fetch API use panni fast-a run aagum.

---

## 8. ARCHITECTURE

```
[ Browser UI (HTML/CSS/JS) ]
         │ HTTP REST (JSON) + Cookie (JSESSIONID + XSRF-TOKEN)
         ▼
[ Spring Security Filter Chain ] ──> CsrfFilter ──> AuthorizationFilter
         │
         ▼
[ REST Controllers ] ──> PlantController, CareScheduleController, etc.
         │
         ▼
[ Service Layer ] ──> Business logic & Strict User Ownership Verification
         │
         ▼
[ Repositories (JPA) ] ──> PlantRepository, CareScheduleRepository
         │
         ▼
[ MySQL Database (6 Tables) ]
```
- **Layered Architecture:** Controller receives HTTP $\rightarrow$ Service enforces rules $\rightarrow$ Repository fetches data $\rightarrow$ MySQL stores data.

---

## 9. MAIN MODULES

| Module | What It Does |
|---|---|
| **Auth Module** | User registration, session login, logout, password change with BCrypt. |
| **Plant Module** | Plant CRUD, search by name, filter by category/status, ownership isolation. |
| **Care Schedule** | Calculates next watering date and classifies status into 4 enum states. |
| **Watering Module**| Logs watering events and updates `lastWateredDate` in care schedule. |
| **Growth Module** | Records plant height (cm), leaf count, and growth milestones. |
| **Dashboard** | Displays aggregate stats (healthy/overdue/today) and upcoming 7-day care. |
| **Admin Module** | Manages plant categories, views user directory and system-wide metrics. |

---

## 10. DATABASE (6 RELATIONAL TABLES)

```
[users] 1──N [plants] 1──1 [care_schedules]
               │   ├──N [watering_records]
               │   └──N [growth_records]
               └──N──1 [plant_categories]
```
- **`users`:** `id` (PK), `email` (UQ), `password` (BCrypt), `full_name`, `role` (USER/ADMIN).
- **`plant_categories`:** `id` (PK), `name` (UQ), `description`. (Seeded with 7 categories).
- **`plants`:** `id` (PK), `user_id` (FK), `category_id` (FK), `name`, `species`, `status`.
- **`care_schedules`:** `id` (PK), `plant_id` (FK, UQ $\rightarrow$ 1:1), `watering_interval_days`, `last_watered_date`.
- **`watering_records`:** `id` (PK), `plant_id` (FK), `watered_date`, `notes`.
- **`growth_records`:** `id` (PK), `plant_id` (FK), `record_date`, `height_cm`, `leaf_count`.
- **Key Concepts:**
  - **PK:** Primary Key (Unique row ID).
  - **FK:** Foreign Key (Links table to parent table).
  - **Cascade Delete (`CascadeType.ALL`):** Plant-ah delete pannina adhodha CareSchedule, WateringRecords, and GrowthRecords automatic-a MySQL-la delete aagidum.

---

## 11. HOW THE PROJECT WORKS (END-TO-END FLOW)

```
User logs in ──> Session cookie created ──> User clicks "Water Plant" on UI
  ──> api.js sends POST /api/plants/{id}/watering with X-XSRF-TOKEN header
  ──> SecurityConfig verifies Session + CSRF token
  ──> Controller validates request (@Valid)
  ──> Service verifies User owns the plant (IDOR protection)
  ──> Repository inserts row in watering_records & updates care_schedules
  ──> Hibernate commits SQL in MySQL
  ──> Controller returns 201 Created JSON
  ──> UI shows green Toast notification & updates watering badge!
```

---

## 12. IMPORTANT PLANT CARE FLOW
1. User clicks **Add Plant** (e.g., Name: Money Plant, Category: Indoor, Interval: 5 days, Last Watered: Today).
2. Backend creates `Plant` + auto-creates `CareSchedule`.
3. Date arithmetic calculates `nextWateringDate = Today + 5 Days`.
4. User later clicks **Log Watering** $\rightarrow$ `CareSchedule.lastWateredDate` updates to new date.
5. Dashboard instantly shifts the plant into the Upcoming Care timeline.

---

## 13. WATERING STATUS (THE 4 STATES)
- **Formula:** `nextWateringDate = lastWateredDate + wateringIntervalDays`
- **`NOT_SET`:** New plant added without a last watered date (Gray badge).
- **`WATER_TODAY`:** `nextWateringDate == today` (Blue badge - Water now!).
- **`WATER_UPCOMING`:** `nextWateringDate > today` (Green badge - Plant is hydrated).
- **`WATER_OVERDUE`:** `nextWateringDate < today` (Red badge - Urgent attention needed!).
- *Example:* Last watered Aug 20, interval 6 days $\rightarrow$ Next date Aug 26. If today is Aug 26 $\rightarrow$ `WATER_TODAY`.

---

## 14. SECURITY (MUST-KNOW CONCEPTS)
- **Authentication:** Verifying identity via `POST /api/auth/login` (BCrypt password check).
- **Session:** State stored in server `HttpSession`; browser sends `JSESSIONID` cookie automatically.
- **CSRF (Cross-Site Request Forgery):** Server sends `XSRF-TOKEN` cookie; client sends `X-XSRF-TOKEN` header on mutating requests (`POST`/`PUT`/`DELETE`).
- **RBAC:** Role-Based Access Control (`hasRole('USER')` vs `hasRole('ADMIN')`).
- **Ownership Isolation & Anti-Enumeration:** User A cannot access User B's plant. Accessing another user's plant returns **404 Not Found** (never 403) to prevent ID discovery.
- **HTTP Status Codes:**
  - **401 Unauthorized:** "Nee yaaru?" (Not logged in / session expired).
  - **403 Forbidden:** "Nee yaarunu theriyudhu, aana permission illa" (Normal user accessing `/api/admin` or missing CSRF).
  - **404 Not Found:** Resource doesn't exist OR belongs to another user.

---

## 15. REST API OVERVIEW (27 AUTHORITATIVE ENDPOINTS)
- **Auth (4):** `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/logout`, `GET /api/auth/me`.
- **Categories (1):** `GET /api/categories` (Publicly authenticated category list).
- **Plants (6):** `GET /api/plants`, `POST /api/plants`, `GET /api/plants/{id}`, `PUT /api/plants/{id}`, `DELETE /api/plants/{id}`, `PATCH /api/plants/{id}/status`.
- **Care (2):** `GET /api/plants/{id}/care`, `PUT /api/plants/{id}/care`.
- **Watering (2):** `GET /api/plants/{id}/watering`, `POST /api/plants/{id}/watering`.
- **Growth (2):** `GET /api/plants/{id}/growth`, `POST /api/plants/{id}/growth`.
- **Dashboard (1):** `GET /api/dashboard` (Aggregate statistics & 7-day care window).
- **Profile (3):** `GET /api/profile`, `PUT /api/profile`, `PUT /api/profile/password`.
- **Admin (6):** `GET /api/admin/stats`, `GET /api/admin/users`, `GET /api/admin/categories`, `POST /api/admin/categories`, `PUT /api/admin/categories/{id}`, `DELETE /api/admin/categories/{id}`.
- **Health (1):** `GET /api/health` (Public health check).

---

## 16. CRUD OPERATIONS
- **C (Create):** `POST /api/plants` (Returns 201 Created).
- **R (Read):** `GET /api/plants` or `GET /api/plants/{id}` (Returns 200 OK).
- **U (Update):** `PUT /api/plants/{id}` or `PATCH /api/plants/{id}/status` (Returns 200 OK).
- **D (Delete):** `DELETE /api/plants/{id}` (Returns 204 No Content).

---

## 17. INPUT VALIDATION (JAKARTA BEAN VALIDATION)
- **Why Backend Validation?** Frontend validation can be bypassed using Postman or cURL. Backend validation guarantees data integrity.
- **Key Annotations:** `@NotBlank` (name, email), `@Email`, `@Min(1)` / `@Max(365)` (interval), `@PastOrPresent` (dates), `@DecimalMin("0.1")` (height).

---

## 18. N+1 QUERY PROBLEM & OPTIMIZATION (VIVA FAVORITE!)
- **What is N+1?** Running 1 query to fetch parent plants, then running $N$ queries in a loop to fetch care schedules (100 plants = 101 queries $\rightarrow$ slow!).
- **How PlantPal Fixed It:**
  1. In `DashboardService`: Replaced loop queries with **1 bulk query** `findByPlantUserId(userId)`.
  2. In `AdminService`: Replaced user loop queries with **1 grouped projection query** `COUNT(p.id) GROUP BY p.user.id`.

---

## 19. TESTING STRATEGY (92 TESTS)
- **Maven Regression Suite:** `mvn clean test`
- **Result:** **92 Tests Run, 0 Failures, 0 Errors, 0 Skipped — BUILD SUCCESS.**
- **Scope Tested:** Auth, Plant CRUD, Care calculations, Watering sync, Growth logs, Dashboard aggregates, Admin stats, CSRF rejection, Cascade deletes.

---

## 20. REAL BROWSER UI AUDIT
- **Headless Chrome Audit:** Automated Puppeteer audit tested **30 / 30 End-to-End User Flows**.
- **Results:** 0 JavaScript console errors, 100% responsive at `375x667` mobile viewport with 0 horizontal overflow.

---

## 21. ADVANTAGES OF PLANTPAL
1. Automated watering status prevents plant death.
2. Complete timeline tracking for watering and growth milestones.
3. Strict user data privacy & anti-enumeration protection.
4. Robust security (BCrypt + Session + CSRF).
5. 100% offline-ready with zero external dependencies (Runs on ₹0 budget).

---

## 22. LIMITATIONS (BE HONEST!)
1. No native mobile app APK (Web-only responsive UI).
2. No automated SMS/Email reminders (Dashboard visual alerts only).
3. No IoT soil moisture hardware integration.
4. No AI/ML image disease detection.

---

## 23. FUTURE SCOPE
1. Mobile app using Flutter or React Native.
2. Email/SMS reminder push notifications via Twilio/SendGrid.
3. Weather API integration to auto-adjust watering for rainy days.
4. IoT soil sensor hardware connectivity.

---

## 24. HOW TO RUN THE PROJECT

```powershell
# 1. Navigate to directory
cd "d:\HARIHARAN P\000_JAVA FULL STACK - Final Year Main_Projects - 2026_2027\007_PlantPal..."

# 2. Set Environment Variables
$env:DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
$env:ADMIN_PASSWORD = "LiveAdminPassword@2026"

# 3. Test & Run
mvn clean test
mvn spring-boot:run

# 4. Open in Browser
# http://localhost:8080/
```

---

## 25. DEMO SEQUENCE FOR EXAMINERS
1. **Landing Page:** Open `http://localhost:8080/` (Show clean botanical UI).
2. **Register & Login:** Register `demo@plantpal.local` $\rightarrow$ Login $\rightarrow$ Dashboard shows 0 plants.
3. **Add Plant:** Add "Money Plant", Category: Indoor, Interval: 4 days, Last Watered: Today.
4. **Plant Detail:** Show Care Schedule card $\rightarrow$ Log Watering $\rightarrow$ Log Growth observation (Height: 18.5 cm).
5. **Dashboard:** Show updated stat cards & Upcoming 7-day care timeline.
6. **Profile:** Update name & change password.
7. **Admin Panel:** Login as `admin@plantpal.local` $\rightarrow$ Show system metrics, categories, & user directory.

---

## 26. TOP 35 VIVA QUESTIONS & SHORT TANGLISH ANSWERS

### Project & Tech
1. **Q: What is PlantPal?** $\rightarrow$ *A: Plant care schedule manage panni growth monitor panra full-stack web application.*
2. **Q: Why Spring Boot?** $\rightarrow$ *A: Auto-configuration, embedded Tomcat server, fast development.*
3. **Q: Why MySQL?** $\rightarrow$ *A: Relational data, foreign key integrity, cascading deletion.*
4. **Q: Why JPA / Hibernate?** $\rightarrow$ *A: Java objects-ah database tables-oda direct-a map panna (ORM).*
5. **Q: Why Vanilla JS?** $\rightarrow$ *A: Zero build tools, fast native browser execution with Fetch API.*

### Architecture & Database
6. **Q: Explain architecture.** $\rightarrow$ *A: Layered MVC (Browser $\rightarrow$ Controller $\rightarrow$ Service $\rightarrow$ Repository $\rightarrow$ MySQL).*
7. **Q: How many tables?** $\rightarrow$ *A: 6 tables (`users`, `plant_categories`, `plants`, `care_schedules`, `watering_records`, `growth_records`).*
8. **Q: What is Primary Key vs Foreign Key?** $\rightarrow$ *A: PK uniquely identifies row; FK links to parent table's PK.*
9. **Q: What is Cascade Delete?** $\rightarrow$ *A: Parent plant delete aana child records (care, watering, growth) automatic-a delete aagum.*
10. **Q: What is One-to-One?** $\rightarrow$ *A: 1 Plant has exactly 1 CareSchedule (`plant_id` UNIQUE).*
11. **Q: What is One-to-Many?** $\rightarrow$ *A: 1 Plant has multiple WateringRecords and GrowthRecords.*

### Security & API
12. **Q: What is REST API?** $\rightarrow$ *A: Stateless HTTP client-server communication using JSON.*
13. **Q: How many APIs in PlantPal?** $\rightarrow$ *A: Exactly 27 authoritative endpoints + 1 health check.*
14. **Q: Why session auth over JWT?** $\rightarrow$ *A: Monolith app-ku simple, server-side instant logout revocation easy.*
15. **Q: Why BCrypt?** $\rightarrow$ *A: Secure salted one-way password hashing.*
16. **Q: What is CSRF?** $\rightarrow$ *A: Malicious site sending unauthorized requests using victim's active session.*
17. **Q: How does PlantPal prevent CSRF?** $\rightarrow$ *A: Generates `XSRF-TOKEN` cookie, requires `X-XSRF-TOKEN` header on POST/PUT/DELETE.*
18. **Q: Difference between 401 and 403?** $\rightarrow$ *A: 401 = Not logged in; 403 = Logged in but no permission (or missing CSRF).*
19. **Q: What is IDOR / Anti-enumeration?** $\rightarrow$ *A: Other user's plant access panna 404 return pannuvom so attacker-ku ID iruka illayanu theriyadhu.*
20. **Q: What is RBAC?** $\rightarrow$ *A: Role-Based Access Control (`USER` vs `ADMIN`).*

### Logic & Performance
21. **Q: How is watering date calculated?** $\rightarrow$ *A: `nextWateringDate = lastWateredDate + intervalDays`.*
22. **Q: Name the 4 watering statuses.** $\rightarrow$ *A: `NOT_SET`, `WATER_TODAY`, `WATER_UPCOMING`, `WATER_OVERDUE`.*
23. **Q: Why calculate nextWateringDate on the fly?** $\rightarrow$ *A: Database normalization; interval change aana instantly recalculate aagum.*
24. **Q: What is N+1 query problem?** $\rightarrow$ *A: 1 parent query execute panni loop-la $N$ child queries run panradhu.*
25. **Q: How did you fix N+1?** $\rightarrow$ *A: Single bulk query `findByPlantUserId` and grouped JPQL `COUNT() ... GROUP BY`.*
26. **Q: What is `@Valid`?** $\rightarrow$ *A: Incoming DTO fields-ah validate panna use aagum.*
27. **Q: What is `@PastOrPresent`?** $\rightarrow$ *A: Watering/Growth dates future-la irukka koodadhu nu enforce pannum.*
28. **Q: What is `@ControllerAdvice`?** $\rightarrow$ *A: Global exception handler to return clean JSON errors.*
29. **Q: Difference between PUT and PATCH?** $\rightarrow$ *A: PUT updates full plant; PATCH updates only health status.*
30. **Q: How many automated tests?** $\rightarrow$ *A: 92 tests (0 failures, 100% passing).*
31. **Q: How was UI tested?** $\rightarrow$ *A: Headless Chrome Puppeteer audit (30 flows passed, 0 console errors).*
32. **Q: What is the release tag?** $\rightarrow$ *A: `v1.0.0-final` pointing to commit `a1e6f65`.*
33. **Q: What is the port number?** $\rightarrow$ *A: 8080.*
34. **Q: Can admin edit user's plants?** $\rightarrow$ *A: No, Admin manages categories and views system stats only.*
35. **Q: What is the budget of this project?** $\rightarrow$ *A: ₹0 (Zero external paid APIs, 100% localhost).*

---

## 27. 30-SECOND PITCH SCRIPT
> *"Good morning Sir/Mam. My project is **PlantPal** — A Personal Plant Care, Watering Schedule and Growth Monitoring Platform. It is a full-stack Spring Boot 3 monolith with MySQL and Vanilla JS. It prevents plant mortality by automatically calculating next watering dates (`lastWateredDate + interval`) and categorizing care into Overdue, Due Today, and Upcoming. It provides watering and growth timelines, CSRF security, BCrypt hashing, and passes 92 automated tests and 30 browser UI audit flows."*

---

## 28. 1-MINUTE PITCH SCRIPT
> *"Sir/Mam, PlantPal is a full-stack web application designed to solve irregular watering in household plants. 
> **Key Architecture:** Built with Java Spring Boot 3, Spring Data JPA, MySQL (6 tables), and Spring Security 6, exposing 27 REST endpoints. 
> **Core Features:** Automatically computes dynamic care schedules, maintains historical logs for watering and growth observations (height in cm, leaf count), and provides an interactive dashboard.
> **Security & Performance:** Implements session auth, BCrypt, CSRF token headers, anti-enumeration 404 security, and eliminated N+1 queries using bulk queries. The project is verified with 92 automated tests and 30 real-browser flows."*

---

## 29. FINAL VIVA CHEAT SHEET

| Parameter | Value |
|---|---|
| **Project Name** | PlantPal (Personal Plant Care Platform) |
| **Backend** | Java 17/21, Spring Boot 3.2.5, Spring Data JPA, Hibernate, Spring Security 6 |
| **Database** | MySQL 8.x (6 Tables: users, plant_categories, plants, care_schedules, watering_records, growth_records) |
| **Frontend** | Vanilla HTML5, CSS3 (Botanical Theme), JavaScript (Fetch API, 100% Offline) |
| **Security** | Session Cookies, BCrypt, CSRF (`X-XSRF-TOKEN`), RBAC (`USER`/`ADMIN`), Anti-Enumeration 404 |
| **Endpoints** | 27 Authoritative REST Endpoints + 1 Health Check |
| **Automated Tests** | 92 Tests (0 Failures, 0 Errors, 0 Skipped — BUILD SUCCESS) |
| **UI Browser Audit** | 30 / 30 End-to-End User Flows Verified in Headless Chrome |
| **Release Tag** | `v1.0.0-final` (Commit `a1e6f65`) |
| **Run Command** | `mvn spring-boot:run` on `http://localhost:8080/` |

---
**🌱 All the Best for your Viva Voce! You are 100% ready.**