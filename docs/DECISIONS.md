# DECISIONS.md
## PlantPal - Architecture and Design Decisions
## Version 2 — Updated after Gate 2 Conditional Approval

---

## Decision 1: MySQL as Database

Reason: Student already knows MySQL. Free, runs locally, integrates natively with Spring Data JPA
and Hibernate. No cost, no cloud dependency.

Alternatives considered: H2 (not persistent), PostgreSQL (unfamiliar), MongoDB (NoSQL misfit).
Decision: MySQL on localhost.

---

## Decision 2: Monolithic Architecture

Reason: 4 GB RAM laptop. Microservices require multiple JVM instances, Docker, service discovery
— all beyond hardware and skill constraints.

Decision: Single Spring Boot monolith on port 8080.

---

## Decision 3: HTML + CSS + Bootstrap + JavaScript (No React)

Reason: Student knows HTML, CSS, Bootstrap, JavaScript. React requires npm tooling, component
lifecycle, and CORS configuration. For a CRUD plant care app, HTML + fetch() is sufficient.

Frontend stack is strictly:
  HTML, CSS, Bootstrap 5, Vanilla JavaScript, fetch() API
No bundlers. No state-management libraries. No frontend frameworks.

Decision: Static HTML served from Spring Boot + fetch() REST calls.

---

## Decision 4: Six Tables Only

Tables: users, plant_categories, plants, care_schedules, watering_records, growth_records.
No notification table. No audit table. No user-session table. No extra tables.

Decision: Exactly six tables for the complete MVP schema.

---

## Decision 5: No External APIs

Budget: Rs.0. Zero internet dependency.
Decision: All data user-entered. No external APIs of any kind.

---

## Decision 6: No AI or ML

PlantPal is a care management platform. Watering status is simple date arithmetic — no AI required.
Decision: Zero AI/ML. Pure Java LocalDate arithmetic for watering schedule.

---

## Decision 7: Spring Security Session-Based Authentication with Custom JSON Login Endpoint

Reason: JWT requires token signing, expiry, refresh tokens, and client storage — complex for a
beginner. OAuth2 requires an external identity provider. Spring Security session auth is
well-documented, works out of the box, and is easy to explain in a viva.

Technically accurate description:
  "Spring Security session-based authentication with a custom JSON login endpoint"

This is NOT classic HTML form-login. PlantPal uses:
  POST /api/auth/login   (JSON body: { email, password })
  -> AuthenticationManager authenticates
  -> HttpSession created and bound to SecurityContext
  -> JSESSIONID cookie returned to browser

Decision: Spring Security HttpSession + BCrypt password hashing. No JWT. No OAuth.

---

## Decision 8: Watering Status Computed at Read Time (Not Stored)

nextWateringDate = lastWateredDate.plusDays(wateringIntervalDays)

wateringStatus:
  if lastWateredDate == null         -> NOT_SET
  if nextWateringDate.isBefore(today)-> WATER_OVERDUE
  if nextWateringDate.isEqual(today) -> WATER_TODAY
  else                               -> WATER_UPCOMING

Decision: Computed in Java service layer at read time. No stored computed fields. No cron jobs.

---

## Decision 9: No Docker

4 GB RAM. Docker Desktop uses 1-2 GB before any container starts.
Decision: No Docker. Run with: mvn spring-boot:run

---

## Decision 10: No Separate Notification Table for MVP

Overdue/today alerts are computed from watering data at dashboard query time.
Decision: No notification table for MVP. Dashboard query computes alerts on demand.

---

## Decision 11: No Auto-Login After Registration (Gate 1 Correction)

Registration and authentication are two separate flows.
  POST /api/auth/register -> 201 -> frontend shows success -> user navigates to /login
  POST /api/auth/login    -> 200 -> session established -> /dashboard

Decision: Register does not create a session. User must log in separately.

---

## Decision 12: System Font Stack Only (Gate 1 Correction)

No Google Fonts. No external font CDN. Application must run fully offline on localhost.

Font stack used in plantpal.css:
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;

Renders as:
  Windows:   Segoe UI
  macOS/iOS: San Francisco
  Android:   Roboto
  Fallback:  Helvetica Neue, Arial

Decision: System font stack. No external font dependency.

---

## Decision 13: lastWateredDate is OPTIONAL (Gate 2 Correction)

A newly added plant may never have been watered. Forcing lastWateredDate breaks the real workflow.

Rule (authoritative across all documents):
  lastWateredDate = NULL   -> wateringStatus = NOT_SET, nextWateringDate = null
  lastWateredDate = a date -> wateringStatus computed via date arithmetic

Database: care_schedules.last_watered_date is a nullable DATE column.
API: lastWateredDate in POST /api/plants request body is optional.
UI: Last Watered Date field in Add Plant form is optional (not marked required).

Decision: lastWateredDate is NULL-allowed. NOT_SET is a valid wateringStatus.

---

## Decision 14: Ownership Check Returns 404 (Not 403) (Gate 2 Correction)

Returning 403 reveals that a plant ID exists in the system — an information disclosure risk.
Returning 404 gives no information about whether the plant ID is valid.

Rule: If plant.user_id != currentUser.id -> throw ResourceNotFoundException -> 404.
Applied in: PlantService, CareService, WateringService, GrowthService.

Decision: All ownership failures return 404, not 403.

---

## Decision 15: Admin Seed via DataInitializer (Java) — Not data.sql (Gate 2 Correction)

data.sql cannot BCrypt a password — SQL files store plain text or pre-computed hashes,
which is fragile and non-transparent. BCrypt requires Java runtime.

DataInitializer.java (implements CommandLineRunner):
  On startup:
  1. Check if admin@plantpal.local exists in users table
  2. If NO: create admin user with passwordEncoder.encode("Admin@123")
  3. If YES: skip (idempotent — safe to run on every restart)
  4. Seed plant_categories if table is empty

Local development credentials (NOT for production):
  Email:    admin@plantpal.local
  Password: Admin@123

Decision: BCrypt hashing via Java CommandLineRunner. No plain-text passwords anywhere.

---

## Decision 16: CSRF Protection Using CookieCsrfTokenRepository (Gate 2 Correction)

CSRF attacks are real for session-cookie-based applications. Simply disabling CSRF without
justification is not acceptable security practice.

Chosen strategy: CookieCsrfTokenRepository.withHttpOnlyFalse()

How it works:
  1. Spring Security sets XSRF-TOKEN cookie (readable by JavaScript, HttpOnly=false).
  2. Frontend JavaScript reads document.cookie for XSRF-TOKEN value.
  3. Frontend includes X-XSRF-TOKEN header on every POST/PUT/PATCH/DELETE fetch() call.
  4. Spring Security validates: X-XSRF-TOKEN header must match XSRF-TOKEN cookie value.
  5. Cross-origin attackers cannot read XSRF-TOKEN (same-origin policy) -> request rejected.

Exemptions (no session = no CSRF risk in the classical sense):
  POST /api/auth/register
  POST /api/auth/login

All other state-changing endpoints (POST/PUT/PATCH/DELETE) require X-XSRF-TOKEN.

SecurityConfig snippet:
  http.csrf(csrf -> csrf
      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      .ignoringRequestMatchers("/api/auth/register", "/api/auth/login")
  );

Decision: CSRF enabled with CookieCsrfTokenRepository. Not disabled.

---

## Decision 17: Admin User Management is Read-Only in MVP (Gate 2 Correction)

Admin can VIEW users (name, email, role, plant count, registration date).
Admin cannot EDIT or DELETE users in MVP.

Justification: User editing/deletion adds significant complexity (cascading deletes, safety
prompts, confirmation flows) without adding core MVP value. The project scope does not require it.

Page title: "View Users" (not "Manage Users").
Endpoint: GET /api/admin/users (read-only, no POST/PUT/DELETE on /api/admin/users).

Decision: Admin user management = read-only overview for MVP.

---

## Decision 18: Category Seed List — No Location-Based Categories (Gate 2 Correction)

"Indoor" and "Outdoor" are plant LOCATIONS, not plant TYPES. They are already captured by
the plants.location ENUM column (INDOOR, OUTDOOR, BALCONY, TERRACE, GARDEN).

Using them as categories would be semantically incorrect and confusing.

Seed categories (8):
  Herb, Succulent, Flowering, Vegetable, Tree, Shrub, Fern, Cactus

Decision: 8 type-based seed categories. No location-based categories.

---

## Decision 19: Endpoint Count is 27 (Gate 2 Correction)

Earlier documents inconsistently stated "26 endpoints" in some places and "27" in others.
Correct count after recount: 27 endpoints.

This is the single authoritative number in all documents.

Auth (4): register, login, logout, me
Categories (1): GET /api/categories
Plants (6): list, detail, add, edit, delete, status-patch
Care (2): get, update
Watering (2): history, record
Growth (2): history, record
Dashboard (1): stats
Profile (3): get, update, password
Admin (6): users, categories-list, categories-add, categories-edit, categories-delete, stats

Total: 4+1+6+2+2+2+1+3+6 = 27

---

## Decision 20: Simplified Add Plant Form (Gate 1 Correction — Documented in Decisions)

Core required fields: Plant Name, Category, Status, Watering Interval
Core optional fields: Location, Description, Last Watered Date (may be null)
Additional optional: Sunlight Needs, Fertilizing Interval (stored in care_schedules)

Decision: Simple form. No field is added without a real user need.
