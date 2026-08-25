# PROJECT_STATUS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform
## Version 15 — Milestone 8 Hardened & Completed

---

## Current Phase

Milestone 8 (Dashboard & Care Summary Statistics) HARDENED & COMPLETE.
Status: MILESTONE 8 HARDENED & VERIFIED — Awaiting Approval for Milestone 9
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
- Growth Records & Tracking Management module fully implemented with cascade deletion and observation tracking.
- **Dashboard & Care Summary Statistics module fully implemented & hardened:**
  - `PlantRepository.findTop5ByUserIdOrderByCreatedAtDesc` (DB-level `LIMIT 5` query for `recentPlants`)
  - `CareScheduleRepository.findByUserIdWithPlantAndCategory` (Bulk JOIN FETCH query completely eliminating N+1 loop)
  - `RecentPlantResponse`, `UpcomingCareResponse`, and `DashboardResponse` DTOs
  - `DashboardService` calculating `totalPlants`, `healthyPlants`, `needsAttentionPlants`, `inactivePlants`, `waterTodayCount`, `overdueCount`, `recentPlants` (max 5, newest-first), and `upcomingCare` (within 7 days, excluding `NOT_SET`, ascending chronological order)
  - `DashboardController` (`GET /api/dashboard`)
  - Strict user ownership isolation across all dashboard calculations
- All unit & integration tests passing (`mvn clean test` 100% success — 77 tests passed).
- Live server verified on port 8080.

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
| **Milestone 8: Dashboard & Summary Stats**     | **COMPLETED & VERIFIED** | 2026-08-25 |
| **M8 Performance & Quality Hardening**         | **COMPLETED & VERIFIED** | 2026-08-25 |

---

## Milestone 8 Verification Summary

1. **Unit & Integration Tests (`mvn clean test`):** **SUCCESS (77/77 tests passed, 0 failures, 0 errors, 0 skipped)**
   - `DashboardControllerTest.testDashboard_Empty_Success` (All counts 0, empty arrays)
   - `DashboardControllerTest.testDashboard_FullStatistics_Success` (Correct counts for total, healthy, needs attention, inactive, overdue, water today)
   - `DashboardControllerTest.testDashboard_RecentPlants_Max5_NewestFirst` (Recent plants capped at 5 and ordered newest first)
   - `DashboardControllerTest.testDashboard_UpcomingCare_Within7Days_ExcludesFarFutureAndNotSet` (Upcoming care includes <= 7 days in ascending order; excludes > 7 days and NOT_SET)
   - `DashboardControllerTest.testDashboard_UserIsolation` (User A only sees their own data, 0 of User B)
   - `DashboardControllerTest.testDashboard_Unauthenticated_Unauthorized` (401 Unauthorized)
   - All 71 tests from Milestones 1 through 7 continue to pass 100%.

2. **Performance Hardening Improvements:**
   - **N+1 Query Elimination:** Replaced individual in-loop queries with `CareScheduleRepository.findByUserIdWithPlantAndCategory`, executing 1 bulk `JOIN FETCH` query for all schedules.
   - **Database-Level Limit:** Replaced in-memory sub-listing with `PlantRepository.findTop5ByUserIdOrderByCreatedAtDesc`, executing a database-level query limited to 5 records.

3. **Live REST API Verification (Port 8080):** **SUCCESS**
   - Verified empty dashboard for newly registered user.
   - Alice creates 6 controlled plants with varied statuses and care intervals.
   - Bob creates 2 plants.
   - `GET /api/dashboard` for Alice: total=6, healthy=4, needsAttention=1, inactive=1, overdue=1, waterToday=1, recentPlants=5 (capped), upcomingCare=3.
   - `GET /api/dashboard` for Bob: total=2, recentPlants=2 (complete isolation from Alice).
   - Unauthenticated `GET /api/dashboard` returns 401 Unauthorized.

---

## Pending Work

| Milestone | Task                                       | Status  |
|-----------|--------------------------------------------|---------|
| **M9**    | **Admin Panel & User Profile Management**  | **READY TO START (Awaiting Approval)** |
| M10       | Frontend + UI Polish                       | BLOCKED |
| —         | Viva Q&A Preparation                       | BLOCKED |
| —         | FINAL_STATUS.md                            | BLOCKED |

---

## Known Issues / Blockers

None.