# PROJECT_STATUS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform
## Version 7 — Milestone 2 Security Review & Admin Password Cleanup Completed

---

## Current Phase

Milestone 2 (Authentication & User Management + Security Hardening) COMPLETE.
Status: MILESTONE 2 VERIFIED AND SECURED — Awaiting Approval for Milestone 3
Last Updated: 2026-08-24

---

## Repository & Implementation State

- Greenfield project initialized with Git.
- Spring Boot 3.2.5 application skeleton configured with Java 17/21 LTS.
- MySQL 8.x connection established via environment variable (`DB_PASSWORD`).
- Initial Admin password loaded via environment variable (`ADMIN_PASSWORD`). No hardcoded secrets anywhere in `src/main`.
- Spring Security 6 session-based authentication fully operational with CSRF protection (`CookieCsrfTokenRepository` + `CsrfCookieFilter`).
- Full authentication suite verified: User entity, Role enum, UserRepository, AuthService, CustomUserDetailsService, AuthController.
- Default Admin account seeded idempotently via `ADMIN_PASSWORD` env var (`admin@plantpal.local`).
- All unit & integration tests passing (`mvn clean test` 100% success — 12 tests passed).
- Live server verified on port 8080 (all 4 authentication endpoints tested and verified live).

---

## Completed Milestones

| Task / Milestone                               | Status | Date |
|------------------------------------------------|--------|------|
| Phase 0: Repository Inspection                 | DONE   | 2026-08-24 |
| Phase 1: Requirements Analysis                 | DONE   | 2026-08-24 |
| Phase 2: UI/UX Wireframe & Design              | DONE   | 2026-08-24 |
| Approval Gate 1                                | APPROVED | 2026-08-24 |
| Phase 3: System Architecture                   | DONE   | 2026-08-24 |
| Phase 4: Database Design (6 tables)            | DONE   | 2026-08-24 |
| Phase 5: REST API Design (27 endpoints)        | DONE   | 2026-08-24 |
| Approval Gate 2 (All 12 corrections verified)  | APPROVED | 2026-08-24 |
| **Milestone 1: Project Setup & Health Check**  | **COMPLETED & VERIFIED** | 2026-08-24 |
| **Security Cleanup: DB Credential via Env Var**| **COMPLETED & VERIFIED** | 2026-08-24 |
| **Milestone 2: Authentication & User Mgmt**    | **COMPLETED & VERIFIED** | 2026-08-24 |
| **M2 Security Fix: Admin Password via Env Var**| **COMPLETED & VERIFIED** | 2026-08-24 |

---

## Milestone 2 Security Fix Verification Summary

1. **Hardcoded Admin Password Eradicated:**
   - Removed `passwordEncoder.encode("Admin@123")` from `DataInitializer.java`.
   - Admin seed password configured via `app.admin.password=${ADMIN_PASSWORD:}`.
   - If `ADMIN_PASSWORD` is absent, seeding is skipped safely with a clear log warning.
2. **Automated Tests (`mvn clean test`):**
   - 12 tests passed, 0 failures, 0 errors.
   - `AuthControllerTest` uses dedicated test credentials and verifies no password exposure.
3. **Live Server Verification (Port 8080):**
   - Register new user: 201 Created
   - Register validation (mismatch): 400 Bad Request
   - Login: 200 OK (UserResponse contains no password)
   - GET `/api/auth/me` with session: 200 OK
   - GET `/api/auth/me` without session: 401 Unauthorized
   - Admin login via env var credential: 200 OK (`role: ADMIN`)
   - Invalid login: 401 Unauthorized
   - Logout with CSRF token (`X-XSRF-TOKEN`): 200 OK
   - Post-logout `/me`: 401 Unauthorized
4. **Credential Scan:**
   - 0 hardcoded secrets found in `src/main`.

---

## Pending Work

| Milestone | Task                                       | Status  |
|-----------|--------------------------------------------|---------|
| **M3**    | **Plant Categories (Admin Management & Public Dropdown)** | **READY TO START (Awaiting Approval)** |
| M4        | Plant CRUD                                 | BLOCKED |
| M5        | Care Schedules                             | BLOCKED |
| M6        | Watering Records                           | BLOCKED |
| M7        | Growth Tracking                            | BLOCKED |
| M8        | Dashboard                                  | BLOCKED |
| M9        | Admin Panel                                | BLOCKED |
| M10       | Frontend + UI Polish                       | BLOCKED |
| —         | Viva Q&A Preparation                       | BLOCKED |
| —         | FINAL_STATUS.md                            | BLOCKED |

---

## Known Issues / Blockers

None.