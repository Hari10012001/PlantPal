# DECISIONS.md
## PlantPal - Architecture and Design Decisions

---

## Decision 1: MySQL as Database

Reason: Student already knows MySQL. Free, runs locally, integrates natively with Spring Data JPA
and Hibernate. No cost, no cloud dependency.

Alternatives considered:
- H2 (in-memory, not persistent across restarts)
- PostgreSQL (student unfamiliar, no advantage here)
- MongoDB (NoSQL not suitable for relational plant data)

Decision: MySQL on localhost.

---

## Decision 2: Monolithic Architecture

Reason: Development laptop has only 4 GB RAM. Microservices would require running multiple JVM
instances, Docker containers, service discovery, and message queues -- all beyond the hardware
limit and the student skill level.

Alternatives considered:
- Microservices (too complex, too heavy for 4 GB RAM)
- Serverless (requires cloud)

Decision: Single Spring Boot monolith running on one port (8080).

---

## Decision 3: HTML + CSS + Bootstrap + JavaScript (No React)

Reason: Student knows HTML, CSS, Bootstrap, and JavaScript. React requires Node.js tooling, npm,
component lifecycle knowledge, and CORS configuration. For a CRUD plant care app, HTML pages
with fetch() API calls to REST endpoints is sufficient and beginner-understandable.

Alternatives considered:
- React (overkill, heavy tooling, npm dependency)
- Angular (steep learning curve)
- Thymeleaf templates (couples server and frontend tightly)
- Vue.js (still requires npm tooling)

Decision: Static HTML + Bootstrap + JavaScript using fetch() to call REST APIs.

---

## Decision 4: Six Tables Only

Reason: PlantPal data model is naturally represented by six entities. Adding more tables
(notifications, tags, media) would add complexity without delivering core value.

Tables:
1. users
2. plant_categories
3. plants
4. care_schedules
5. watering_records
6. growth_records

Decision: Six tables as the complete schema for MVP.

---

## Decision 5: No External APIs

Reason: Budget is Rs.0. The project must run with zero internet dependency. No plant database
API, no weather API, no email API, no SMS API is needed.

Decision: All data is user-entered. No external API calls of any kind.

---

## Decision 6: No AI or ML

Reason: Out of scope for a beginner Java Full Stack project. The student does not have AI or ML
knowledge. PlantPal is a care management platform, not a plant intelligence platform. Simple date
arithmetic (lastWatered + interval = nextWatering) is the correct and sufficient implementation.

Decision: Zero AI, ML, or prediction. Pure Java LocalDate arithmetic for watering schedule.

---

## Decision 7: Session-Based Authentication (Spring Security)

Reason: JWT tokens require Base64 encoding, token signing keys, expiry logic, refresh tokens,
and careful client-side storage -- all complex for a beginner. Spring Security session-based
authentication is well-documented, works out of the box, and is easy to understand and explain
in a viva.

Alternatives considered:
- JWT (complex for beginner)
- OAuth2 (requires external identity provider)
- Manual session handling (error-prone)

Decision: Spring Security with HTTP session. Username/password login. BCrypt password hashing.

---

## Decision 8: Simple Date Arithmetic for Watering Status

Reason: nextWateringDate = lastWateredDate + wateringIntervalDays is a single line of Java code.
No scheduling algorithm, no cron job, no background thread, no caching is needed. Status is
computed on the fly at read time.

Decision: Pure Java LocalDate arithmetic. Computed at read time. No stored calculated fields.
No background jobs.

---

## Decision 9: No Docker

Reason: 4 GB RAM. Docker Desktop on Windows consumes 1-2 GB RAM before any container runs.
The project must start and run with just Java + MySQL.

Decision: No Docker. Direct Maven build: mvn spring-boot:run

---

## Decision 10: No Separate Notification Table for MVP

Reason: Reminders are shown on the dashboard by querying plants whose nextWateringDate is <= today.
This is a simple database query. A separate notification table adds schema complexity without value.

Decision: Dashboard computes overdue and today-due plants at query time. No notification table for MVP.

---

## Decision 11: No Auto-Login After Registration

Reason: Approved correction from Approval Gate 1. Registration should be a separate step from
authentication. After registering, the user must explicitly log in. This is the standard pattern
for web applications and avoids complexity in session creation during the registration flow.

Flow: POST /api/auth/register -> 201 Created -> frontend shows success -> user goes to /login.

Decision: Registration and login are two separate, independent flows.

---

## Decision 12: System Font Stack (No Google Fonts)

Reason: Approved correction from Approval Gate 1. Loading Google Fonts at runtime creates an
external network dependency. The application must work fully offline on localhost. System fonts
are always available and render acceptably well for a professional UI.

System font stack:
font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;

This renders as:
- Segoe UI on Windows
- San Francisco on macOS/iOS
- Roboto on Android/Chrome OS
- Helvetica Neue or Arial as fallback

Decision: Use system font stack only. No Google Fonts or any external font CDN.

---

## Decision 13: Simplified Add Plant Form

Reason: Approved correction from Approval Gate 1. Keep the Add Plant form focused on the core
fields. Sunlight Needs and Fertilizing Interval remain as optional fields to avoid complicating
the implementation, but they must not add unnecessary complexity.

Core fields (required): Plant Name, Category, Status, Watering Interval, Last Watered Date
Optional fields: Location, Description, Sunlight Needs, Fertilizing Interval

Decision: All optional fields map to nullable columns in care_schedules. No changes to schema.

---

## Decision 14: Ownership Check Returns 404 (Not 403)

Reason: Returning 403 Forbidden when a user tries to access another user's plant reveals that
the plant ID exists in the system. Returning 404 Not Found is safer and gives no information
to a potential attacker about which IDs are valid.

Decision: All plant/care/watering/growth endpoints return 404 if plant does not exist OR if
plant belongs to a different user.

---

## Decision 15: Watering Status Computed at Read Time

Reason: Storing computed fields (next_watering_date, watering_status) creates a risk of stale
data if the background update fails. Computing at read time ensures the status is always
accurate with zero additional infrastructure.

Java logic (single method in a utility/service class):
  if (lastWateredDate == null) return "NOT_SET";
  LocalDate next = lastWateredDate.plusDays(intervalDays);
  if (next.isBefore(today)) return "WATER_OVERDUE";
  if (next.isEqual(today))  return "WATER_TODAY";
  return "WATER_UPCOMING";

Decision: No stored computed fields. Status computed in Java service layer at read time.
