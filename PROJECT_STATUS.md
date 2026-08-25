# PROJECT_STATUS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform
## Version 9 — Milestone 3 Strict Contract & Consistency Review Completed

---

## Current Phase

Milestone 3 (Plant Categories Management) COMPLETE & CONTRACT VERIFIED.
Status: MILESTONE 3 VERIFIED AND COMPLETED — Awaiting Approval for Milestone 4
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
- Plant Categories module fully implemented strictly per the approved 27-endpoint API contract:
  - `PlantCategory` entity & `plant_categories` table
  - `PlantCategoryRepository` with case-insensitive uniqueness queries
  - `CategoryService` with full CRUD, duplicate name guard (409 Conflict), and in-use protection
  - `CategoryController` (`GET /api/categories` — authenticated user/admin category listing)
  - `AdminCategoryController` (`GET`, `POST`, `PUT`, `DELETE /api/admin/categories` guarded with `ADMIN` role)
  - 8 approved seed categories initialized idempotently: Herb, Succulent, Flowering, Vegetable, Tree, Shrub, Fern, Cactus
- All unit & integration tests passing (`mvn clean test` 100% success — 24 tests passed).
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

---

## Milestone 3 Verification Summary

1. **Approved API Contract Alignment:**
   - Removed unapproved extra endpoints to match the exact 27-endpoint specification.
   - Verified `GET /api/categories` is available only to authenticated users (USER and ADMIN) and returns 401 for unauthenticated requests.
2. **Unit & Integration Tests (`mvn clean test`):** **SUCCESS (24/24 tests passed, 0 failures, 0 errors)**
   - `CategoryControllerTest.testGetAllCategories_UserRole_Success` (200 OK)
   - `CategoryControllerTest.testGetAllCategories_AdminRole_Success` (200 OK)
   - `CategoryControllerTest.testUnauthenticated_AccessCategories_Unauthorized` (401 Unauthorized)
   - `CategoryControllerTest.testAdminGetAllCategories_Success` (200 OK)
   - `CategoryControllerTest.testAdminCreateCategory_Success` (201 Created)
   - `CategoryControllerTest.testAdminCreateCategory_DuplicateName` (409 Conflict)
   - `CategoryControllerTest.testAdminCreateCategory_ValidationError` (400 Bad Request)
   - `CategoryControllerTest.testAdminUpdateCategory_Success` (200 OK)
   - `CategoryControllerTest.testAdminUpdateCategory_DuplicateName` (409 Conflict)
   - `CategoryControllerTest.testAdminDeleteCategory_Success` (204 No Content)
   - `CategoryControllerTest.testAdminDeleteCategory_NotFound` (404 Not Found)
   - `CategoryControllerTest.testUserRole_AccessAdminEndpoints_Forbidden` (403 Forbidden)
   - All 12 Auth and Health tests continue to pass 100%.

3. **Live REST API Verification (Port 8080):** **SUCCESS**
   - Unauthenticated `GET /api/categories`: 401 Unauthorized
   - Authenticated USER `GET /api/categories`: 200 OK (all 8 approved categories returned)
   - USER attempting `POST /api/admin/categories`: 403 Forbidden
   - Authenticated ADMIN `GET /api/categories`: 200 OK
   - ADMIN `POST /api/admin/categories`: 201 Created
   - ADMIN duplicate creation: 409 Conflict
   - ADMIN `PUT /api/admin/categories/{id}`: 200 OK
   - ADMIN `DELETE /api/admin/categories/{id}`: 204 No Content
   - ADMIN `DELETE /api/admin/categories/99999`: 404 Not Found

4. **Database Verification:**
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