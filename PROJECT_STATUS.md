# PROJECT_STATUS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform
## Version 10 — Milestone 4 Completed

---

## Current Phase

Milestone 4 (Plant CRUD & User Ownership Management) COMPLETE.
Status: MILESTONE 4 VERIFIED AND COMPLETED — Awaiting Approval for Milestone 5
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
- **Plant CRUD & User Ownership Management module fully implemented:**
  - `Plant` entity & `plants` table with Foreign Keys to `users` and `plant_categories`
  - `PlantStatus` enum (`HEALTHY`, `NEEDS_ATTENTION`, `INACTIVE`)
  - `PlantRepository` with user-scoped queries and search/filter JPQL methods
  - `PlantRequest`, `PlantStatusRequest`, and `PlantResponse` DTOs
  - `PlantService` with strict ownership isolation (returns 404 for non-owners to prevent enumeration)
  - `PlantController` (`GET /api/plants`, `GET /api/plants/{id}`, `POST /api/plants`, `PUT /api/plants/{id}`, `PATCH /api/plants/{id}/status`, `DELETE /api/plants/{id}`)
  - Category in-use deletion protection (returns 409 Conflict when attempting to delete a category referenced by plants)
- All unit & integration tests passing (`mvn clean test` 100% success — 38 tests passed).
- Live server verified on port 8080.
- Database schema and foreign key constraints verified in MySQL.

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

---

## Milestone 4 Verification Summary

1. **Unit & Integration Tests (`mvn clean test`):** **SUCCESS (38/38 tests passed, 0 failures, 0 errors)**
   - `PlantControllerTest.testCreatePlant_Success` (201 Created)
   - `PlantControllerTest.testCreatePlant_InvalidCategory_NotFound` (404 Not Found)
   - `PlantControllerTest.testCreatePlant_BlankName_BadRequest` (400 Bad Request)
   - `PlantControllerTest.testGetMyPlants_UserOwnership` (200 OK — retrieves only own plants)
   - `PlantControllerTest.testSearchAndFilterPlants` (200 OK — category filter, status filter, keyword search)
   - `PlantControllerTest.testGetPlantById_Owner_Success` (200 OK)
   - `PlantControllerTest.testGetPlantById_NonOwner_Returns404` (404 Not Found — prevents ID enumeration)
   - `PlantControllerTest.testUpdatePlant_Owner_Success` (200 OK)
   - `PlantControllerTest.testUpdatePlant_NonOwner_Returns404` (404 Not Found)
   - `PlantControllerTest.testUpdatePlantStatus_Owner_Success` (200 OK)
   - `PlantControllerTest.testDeletePlant_Owner_Success` (204 No Content)
   - `PlantControllerTest.testDeletePlant_NonOwner_Returns404` (404 Not Found)
   - `PlantControllerTest.testCategoryDeletionProtection_WhenReferencedByPlant` (409 Conflict)
   - `PlantControllerTest.testUnauthenticated_AccessPlants_Unauthorized` (401 Unauthorized)
   - All Milestone 1, 2, and 3 tests (24 tests) continue to pass 100%.

2. **Live REST API Verification (Port 8080):** **SUCCESS**
   - User A registers, logs in, creates "Sweet Basil" (Herb) and "Aloe Vera" (Succulent): 201 Created
   - User B registers, logs in, retrieves own plants: returns 0 items (strict user isolation)
   - User B attempts `GET /api/plants/{alicePlantId}`: 404 Not Found
   - User B attempts `PUT /api/plants/{alicePlantId}`: 404 Not Found
   - User B attempts `DELETE /api/plants/{alicePlantId}`: 404 Not Found
   - User A updates plant: 200 OK
   - User A patches status (`NEEDS_ATTENTION`): 200 OK
   - Category deletion protection: 409 Conflict when category is in use by plant
   - User A deletes own plant: 204 No Content
   - Post-delete check: 404 Not Found
   - Unauthenticated requests: 401 Unauthorized

3. **Database Verification (MySQL):**
   - `plants` table exists with Foreign Keys: `user_id` -> `users(id)` and `category_id` -> `plant_categories(id)`.
   - Exactly the 8 approved categories preserved in database.

---

## Pending Work

| Milestone | Task                                       | Status  |
|-----------|--------------------------------------------|---------|
| **M5**    | **Care Schedules Management**              | **READY TO START (Awaiting Approval)** |
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