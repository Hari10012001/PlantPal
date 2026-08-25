# PROJECT_STATUS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform
## Version 13 — Milestone 7 Completed

---

## Current Phase

Milestone 7 (Growth Records & Tracking Management) COMPLETE.
Status: MILESTONE 7 VERIFIED AND COMPLETED — Awaiting Approval for Milestone 8
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
- Care Schedules Management module fully implemented with dynamic date arithmetic for watering status (`NOT_SET`, `WATER_TODAY`, `WATER_UPCOMING`, `WATER_OVERDUE`).
- Watering Records & History Management module fully implemented with cascade deletion and care schedule synchronization.
- **Growth Records & Tracking Management module fully implemented:**
  - `GrowthRecord` entity & `growth_records` table with FK to `plants(id)` ON DELETE CASCADE
  - `@OneToMany` relationship in `Plant` entity with JPA cascade & orphan removal
  - `GrowthRecordRepository` with `findByPlantIdAndPlantUserIdOrderByRecordDateDescCreatedAtDesc`
  - `GrowthRecordRequest` and `GrowthRecordResponse` DTOs (validating at least one observation field is present, `@PastOrPresent` date, non-negative bounds)
  - `GrowthRecordService` with ownership checks and history retrieval
  - `GrowthRecordController` (`GET /api/plants/{id}/growth`, `POST /api/plants/{id}/growth`)
- All unit & integration tests passing (`mvn clean test` 100% success — 71 tests passed).
- Live server verified on port 8080.
- Database schema, foreign key constraints (`ON DELETE CASCADE`), and cascade deletion verified in MySQL.

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
| **Milestone 6: Watering Records & History**    | **COMPLETED & VERIFIED** | 2026-08-25 |
| **Milestone 7: Growth Records & Tracking**     | **COMPLETED & VERIFIED** | 2026-08-25 |

---

## Milestone 7 Verification Summary

1. **Unit & Integration Tests (`mvn clean test`):** **SUCCESS (71/71 tests passed, 0 failures, 0 errors, 0 skipped)**
   - `GrowthRecordControllerTest.testRecordGrowth_Owner_AllFields_Success` (201 Created)
   - `GrowthRecordControllerTest.testRecordGrowth_OnlyHeight_Success` (201 Created)
   - `GrowthRecordControllerTest.testRecordGrowth_OnlyNotes_Success` (201 Created)
   - `GrowthRecordControllerTest.testGetGrowthHistory_Owner_Success` (200 OK + newest first sorting)
   - `GrowthRecordControllerTest.testRecordGrowth_NonOwner_Returns404` (404 Not Found)
   - `GrowthRecordControllerTest.testGetGrowthHistory_NonOwner_Returns404` (404 Not Found)
   - `GrowthRecordControllerTest.testRecordGrowth_FutureDate_BadRequest` (400 Bad Request)
   - `GrowthRecordControllerTest.testRecordGrowth_AllFieldsEmpty_BadRequest` (400 Bad Request)
   - `GrowthRecordControllerTest.testRecordGrowth_InvalidHeight_BadRequest` (400 Bad Request)
   - `GrowthRecordControllerTest.testRecordGrowth_NegativeLeafCount_BadRequest` (400 Bad Request)
   - `GrowthRecordControllerTest.testDeletePlant_CascadesToGrowthRecords` (Cascade deletion verified)
   - `GrowthRecordControllerTest.testUnauthenticated_AccessGrowth_Unauthorized` (401 Unauthorized)
   - All 59 tests from Milestones 1 through 6 continue to pass 100%.

2. **Live REST API Verification (Port 8080):** **SUCCESS**
   - User creates plant and posts growth observations (all fields, height only, notes only) -> 201 Created.
   - `GET /api/plants/{id}/growth` returns full history ordered by `recordDate DESC, createdAt DESC`.
   - Non-owner GET and POST return 404 Not Found (Anti-enumeration).
   - Future `recordDate`, all fields empty, negative height, negative leaf count, and notes > 500 characters return 400 Bad Request.
   - Unauthenticated requests return 401 Unauthorized.
   - Deleting plant cascades and removes all growth records.

3. **Database Verification (MySQL):**
   - Table `growth_records` verified with foreign key constraint `plant_id -> plants(id) ON DELETE CASCADE`.
   - Cascade deletion confirmed in MySQL.

---

## Pending Work

| Milestone | Task                                       | Status  |
|-----------|--------------------------------------------|---------|
| **M8**    | **Dashboard & Care Summary Statistics**    | **READY TO START (Awaiting Approval)** |
| M9        | Admin Panel                                | BLOCKED |
| M10       | Frontend + UI Polish                       | BLOCKED |
| —         | Viva Q&A Preparation                       | BLOCKED |
| —         | FINAL_STATUS.md                            | BLOCKED |

---

## Known Issues / Blockers

None.