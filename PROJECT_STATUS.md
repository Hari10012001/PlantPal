# PROJECT_STATUS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform
## Version 16 — Milestone 9 Completed

---

## Current Phase

Milestone 9 (Admin Panel & User Profile Management) COMPLETE.
Status: ALL BACKEND MILESTONES (M1–M9) VERIFIED AND COMPLETED — Ready for Milestone 10 (Frontend & UI Polish)
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
- Dashboard & Care Summary Statistics module fully implemented and hardened (N+1 query eliminated with bulk fetch, database-level limit on recent plants).
- **Admin Panel & User Profile Management module fully implemented:**
  - `ProfileResponse`, `UpdateProfileRequest`, `ChangePasswordRequest`, `AdminUserResponse`, `AdminStatsResponse`
  - `UserService` managing profile fetch, profile update, and BCrypt-validated password changes
  - `AdminService` providing read-only user overview with plant counts and system-wide statistics (users, plants, watering records, growth records, categories, plantsByStatus)
  - `ProfileController` (`GET /api/profile`, `PUT /api/profile`, `PUT /api/profile/password`)
  - `AdminController` (`GET /api/admin/users`, `GET /api/admin/stats`)
  - Strict RBAC: normal USER denied from `/api/admin/**` (403 Forbidden); unauthenticated requests rejected (401 Unauthorized)
- **ALL 27 AUTHORITATIVE REST API ENDPOINTS ARE FULLY IMPLEMENTED AND VERIFIED.**
- All unit & integration tests passing (`mvn clean test` 100% success — 90 tests passed).
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
| **Milestone 9: Admin Panel & User Profile**    | **COMPLETED & VERIFIED** | 2026-08-25 |

---

## Complete 27-Endpoint Matrix Status

| # | Method | URI Path | Access / Role | CSRF | Implemented & Verified |
|---|--------|----------|---------------|------|------------------------|
| 1 | POST | `/api/auth/register` | Public | No | ✅ M2 Verified |
| 2 | POST | `/api/auth/login` | Public | No | ✅ M2 Verified |
| 3 | POST | `/api/auth/logout` | Authenticated | Yes | ✅ M2 Verified |
| 4 | GET | `/api/auth/me` | Authenticated | No | ✅ M2 Verified |
| 5 | GET | `/api/categories` | Authenticated | No | ✅ M3 Verified |
| 6 | GET | `/api/plants` | USER (Owner) | No | ✅ M4 Verified |
| 7 | POST | `/api/plants` | USER (Owner) | Yes | ✅ M4 Verified |
| 8 | GET | `/api/plants/{id}` | USER (Owner) | No | ✅ M4 Verified |
| 9 | PUT | `/api/plants/{id}` | USER (Owner) | Yes | ✅ M4 Verified |
| 10 | DELETE | `/api/plants/{id}` | USER (Owner) | Yes | ✅ M4 Verified |
| 11 | PATCH | `/api/plants/{id}/status` | USER (Owner) | Yes | ✅ M4 Verified |
| 12 | GET | `/api/plants/{id}/care` | USER (Owner) | No | ✅ M5 Verified |
| 13 | PUT | `/api/plants/{id}/care` | USER (Owner) | Yes | ✅ M5 Verified |
| 14 | GET | `/api/plants/{id}/watering` | USER (Owner) | No | ✅ M6 Verified |
| 15 | POST | `/api/plants/{id}/watering` | USER (Owner) | Yes | ✅ M6 Verified |
| 16 | GET | `/api/plants/{id}/growth` | USER (Owner) | No | ✅ M7 Verified |
| 17 | POST | `/api/plants/{id}/growth` | USER (Owner) | Yes | ✅ M7 Verified |
| 18 | GET | `/api/dashboard` | USER | No | ✅ M8 Verified |
| 19 | GET | `/api/profile` | Authenticated | No | ✅ M9 Verified |
| 20 | PUT | `/api/profile` | Authenticated | Yes | ✅ M9 Verified |
| 21 | PUT | `/api/profile/password` | Authenticated | Yes | ✅ M9 Verified |
| 22 | GET | `/api/admin/users` | ADMIN | No | ✅ M9 Verified |
| 23 | GET | `/api/admin/categories` | ADMIN | No | ✅ M3 Verified |
| 24 | POST | `/api/admin/categories` | ADMIN | Yes | ✅ M3 Verified |
| 25 | PUT | `/api/admin/categories/{id}` | ADMIN | Yes | ✅ M3 Verified |
| 26 | DELETE | `/api/admin/categories/{id}` | ADMIN | Yes | ✅ M3 Verified |
| 27 | GET | `/api/admin/stats` | ADMIN | No | ✅ M9 Verified |

---

## Milestone 9 Verification Summary

1. **Unit & Integration Tests (`mvn clean test`):** **SUCCESS (90/90 tests passed, 0 failures, 0 errors, 0 skipped)**
   - `ProfileControllerTest`: 8 tests (Get profile with totalPlants, update fullName, change password with BCrypt verification, incorrect current password 400, mismatched confirm password 400, short password 400, unauthenticated 401).
   - `AdminControllerTest`: 5 tests (Get users list for ADMIN, USER access to admin users 403 Forbidden, get system stats for ADMIN, USER access to admin stats 403 Forbidden, unauthenticated 401).
   - All 77 tests from Milestones 1 through 8 continue to pass 100%.

2. **Live REST API Verification (Port 8080):** **SUCCESS**
   - User fetches profile (`GET /api/profile`) -> 200 OK.
   - User updates name (`PUT /api/profile`) -> 200 OK.
   - User changes password (`PUT /api/profile/password`) -> 200 OK.
   - Re-login with new password verified -> 200 OK.
   - Wrong current password and mismatched new password return 400 Bad Request.
   - Normal user attempting `/api/admin/users` and `/api/admin/stats` returns 403 Forbidden.
   - Admin accesses `/api/admin/users` (read-only list with plant counts) and `/api/admin/stats` (counts and status map) -> 200 OK.
   - Unauthenticated requests return 401 Unauthorized.

---

## Pending Work

| Milestone | Task                                       | Status  |
|-----------|--------------------------------------------|---------|
| **M10**   | **Frontend Web Interface & UI Polish**     | **READY TO START (Awaiting Approval)** |
| —         | Viva Q&A Preparation                       | BLOCKED |
| —         | FINAL_STATUS.md                            | BLOCKED |

---

## Known Issues / Blockers

None.