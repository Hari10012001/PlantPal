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
