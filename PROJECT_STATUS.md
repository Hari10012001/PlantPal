# PROJECT_STATUS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform
## Version 11 — Milestone 5 Completed

---

## Current Phase

Milestone 5 (Care Schedules Management) COMPLETE.
Status: MILESTONE 5 VERIFIED AND COMPLETED — Awaiting Approval for Milestone 6
Last Updated: 2026-08-25

---

## Repository & Implementation State

- Greenfield project initialized with Git.
- Spring Boot 3.2.5 application skeleton configured with Java 17/21 LTS.
- MySQL 8.x connection established via environment variable (`DB_PASSWORD`).
- Initial Admin password loaded via environment variable (`ADMIN_PASSWORD`). No hardcoded secrets anywhere in `src/main`.
- Spring Security 6 session-based authentication fully operational with CSRF protection (`CookieCsrfTokenRepository`).
- Full authentication suite verified: User entity, Role enum, UserRepository, AuthService, CustomUserDetailsService, AuthController.
- Default Admin account seeded idempotently via `ADMIN_PASSWORD` env var (`admin@plantpal.local`).
- Plant Categories module fully implemented strictly per the approved 27-endpoint API contract.
- Plant CRUD & User Ownership Management module fully implemented with strict anti-enumeration 404 security.
- **Care Schedules Management module fully implemented:**
  - `CareSchedule` entity & `care_schedules` table with 1-to-1 unique foreign key to `plants(id)`
  - `SunlightNeeds` enum (`FULL_SUN`, `PARTIAL_SUN`, `SHADE`)
  - `WateringStatus` enum (`NOT_SET`, `WATER_TODAY`, `WATER_UPCOMING`, `WATER_OVERDUE`)
  - Dynamic date arithmetic for `nextWateringDate` and `wateringStatus` (computed on-the-fly in Java, never stored in DB)
  - Auto-creation of care schedule upon plant creation (`POST /api/plants`)
  - `CareScheduleRepository` with plant & user scoping
  - `CareScheduleRequest` and `CareScheduleResponse` DTOs
  - `CareScheduleService` with strict ownership isolation (404 for non-owners)
  - `CareScheduleController` (`GET /api/plants/{id}/care`, `PUT /api/plants/{id}/care`)
- All unit & integration tests passing (`mvn clean test` 100% success — 48 tests passed).
- Live server verified on port 8080.
- Database schema, 1-to-1 unique constraints, and foreign key relationships verified in MySQL.

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
| **Milestone 3: Plant Categories Management**   | **COMPLETED & VERIFIED** | 2026-08-25 |
| **M3 Consistency Fix: Strict 27 Endpoint Sync**| **COMPLETED & VERIFIED** | 2026-08-25 |
| **Milestone 4: Plant CRUD & User Ownership**   | **COMPLETED & VERIFIED** | 2026-08-25 |
| **Milestone 5: Care Schedules Management**     | **COMPLETED & VERIFIED** | 2026-08-25 |

---

## Milestone 5 Verification Summary

1. **Unit & Integration Tests (`mvn clean test`):** **SUCCESS (48/48 tests passed, 0 failures, 0 errors)**
   - `CareScheduleControllerTest.testPlantCreation_AutoCreatesCareSchedule` (201 Created + NOT_SET status)
   - `CareScheduleControllerTest.testGetCareSchedule_Owner_Success` (200 OK)
   - `CareScheduleControllerTest.testCareSchedule_WateringStatus_WaterToday` (200 OK + WATER_TODAY)
   - `CareScheduleControllerTest.testCareSchedule_WateringStatus_WaterOverdue` (200 OK + WATER_OVERDUE)
   - `CareScheduleControllerTest.testUpdateCareSchedule_Owner_Success` (200 OK + WATER_UPCOMING)
   - `CareScheduleControllerTest.testGetCareSchedule_NonOwner_Returns404` (404 Not Found)
   - `CareScheduleControllerTest.testUpdateCareSchedule_NonOwner_Returns404` (404 Not Found)
   - `CareScheduleControllerTest.testUpdateCareSchedule_FutureDate_BadRequest` (400 Bad Request)
   - `CareScheduleControllerTest.testUpdateCareSchedule_InvalidInterval_BadRequest` (400 Bad Request)
   - `CareScheduleControllerTest.testUnauthenticated_AccessCareSchedule_Unauthorized` (401 Unauthorized)
   - All 38 tests from Milestones 1, 2, 3, and 4 continue to pass 100%.

2. **Live REST API Verification (Port 8080):** **SUCCESS**
   - Plant creation auto-creates care schedule with `wateringStatus = NOT_SET`
   - `GET /api/plants/{id}/care` returns full care schedule
   - `PUT /api/plants/{id}/care` dynamically computes `WATER_TODAY`, `WATER_OVERDUE`, `WATER_UPCOMING`
   - Non-owner access to `GET` and `PUT /api/plants/{id}/care` returns `404 Not Found`
   - Future `lastWateredDate` returns `400 Bad Request`
   - Unauthenticated access returns `401 Unauthorized`

3. **Database Verification (MySQL):**
   - Table `care_schedules` verified with unique constraint on `plant_id` and foreign key constraint `plant_id -> plants(id)`.
   - `nextWateringDate` and `wateringStatus` are dynamically computed and not stored in DB.

---

## Pending Work

| Milestone | Task                                       | Status  |
|-----------|--------------------------------------------|---------|
| **M6**    | **Watering Records & History Management**  | **READY TO START (Awaiting Approval)** |
| M7        | Growth Tracking                            | BLOCKED |
| M8        | Dashboard                                  | BLOCKED |
| M9        | Admin Panel                                | BLOCKED |
| M10       | Frontend + UI Polish                       | BLOCKED |
| —         | Viva Q&A Preparation                       | BLOCKED |
| —         | FINAL_STATUS.md                            | BLOCKED |

---

## Known Issues / Blockers

None.