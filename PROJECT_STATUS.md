# PROJECT_STATUS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform
## Version 8 — Milestone 3 Completed

---

## Current Phase

Milestone 3 (Plant Categories Management) COMPLETE.
Status: MILESTONE 3 VERIFIED AND COMPLETED — Awaiting Approval for Milestone 4
Last Updated: 2026-08-25

---

## Repository & Implementation State

- Greenfield project initialized with Git.
- Spring Boot 3.2.5 application skeleton configured with Java 17/21 LTS.
- MySQL 8.x connection established via environment variable (`DB_PASSWORD`).
- Spring Security 6 session-based authentication fully operational with CSRF protection (`CookieCsrfTokenRepository`).
- Full authentication suite verified: User entity, Role enum, UserRepository, AuthService, CustomUserDetailsService, AuthController.
- Default Admin account seeded idempotently (`admin@plantpal.local`).
- Plant Categories module fully implemented:
  - `PlantCategory` entity & `plant_categories` table
  - `PlantCategoryRepository` with case-insensitive uniqueness queries
  - `CategoryService` with full CRUD, duplicate name guard, and in-use protection
  - `CategoryController` (`GET /api/categories`, `GET /api/categories/{id}`)
  - `AdminCategoryController` (`GET`, `POST`, `PUT`, `DELETE /api/admin/categories` guarded with `ADMIN` role)
  - 8 approved seed categories initialized idempotently: Herb, Succulent, Flowering, Vegetable, Tree, Shrub, Fern, Cactus
- All unit & integration tests passing (`mvn clean test` 100% success — 22 tests passed).
- Live server verified on port 8080 (Category endpoints tested and verified live).

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

---

## Milestone 3 Verification Summary

1. **Unit & Integration Tests (`mvn clean test`):** **SUCCESS (22/22 tests passed, 0 failures, 0 errors)**
   - `CategoryControllerTest.testGetAllCategories_UserRole_Success` (200 OK)
   - `CategoryControllerTest.testGetCategoryById_Success` (200 OK)
   - `CategoryControllerTest.testGetCategoryById_NotFound` (404 Not Found)
   - `CategoryControllerTest.testAdminCreateCategory_Success` (201 Created)
   - `CategoryControllerTest.testAdminCreateCategory_DuplicateName` (409 Conflict)
   - `CategoryControllerTest.testAdminCreateCategory_ValidationError` (400 Bad Request)
   - `CategoryControllerTest.testAdminUpdateCategory_Success` (200 OK)
   - `CategoryControllerTest.testAdminDeleteCategory_Success` (204 No Content)
   - `CategoryControllerTest.testUserRole_AccessAdminEndpoints_Forbidden` (403 Forbidden)
   - `CategoryControllerTest.testUnauthenticated_AccessCategories_Unauthorized` (401 Unauthorized)
   - All 12 Milestone 1 & 2 tests continue to pass 100%.

2. **Live REST API Verification (Port 8080):** **SUCCESS (10/10 live checks passed)**
   - `GET /api/categories` by standard USER: 200 OK (all 8 approved categories returned)
   - `POST /api/admin/categories` by standard USER: 403 Forbidden
   - `POST /api/admin/categories` by ADMIN: 201 Created
   - `POST /api/admin/categories` duplicate name: 409 Conflict
   - `PUT /api/admin/categories/{id}` by ADMIN: 200 OK
   - `DELETE /api/admin/categories/{id}` by ADMIN: 204 No Content
   - `DELETE /api/admin/categories/99999`: 404 Not Found
   - Unauthenticated `GET /api/categories`: 401 Unauthorized

3. **Database Verification:**
   - Exactly the 8 approved categories seeded: `Herb`, `Succulent`, `Flowering`, `Vegetable`, `Tree`, `Shrub`, `Fern`, `Cactus`.

---

## Pending Work

| Milestone | Task                                       | Status  |
|-----------|--------------------------------------------|---------|
| **M4**    | **Plant CRUD & User Ownership Management** | **READY TO START (Awaiting Approval)** |
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