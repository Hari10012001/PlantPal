# API_DOCUMENTATION.md
## PlantPal - REST API Design
## Version 2 — Updated after Gate 2 Conditional Approval

---

## 1. API Design Principles

- All API endpoints are prefixed with /api
- Authentication: Spring Security session-based authentication with a custom JSON login endpoint
- Session ID stored in server-side HttpSession; browser sends JSESSIONID cookie automatically
- CSRF protection: CookieCsrfTokenRepository strategy (see Section 3)
- All protected endpoints return 401 Unauthorized if session is missing or expired
- All endpoints return 403 Forbidden if user lacks the required role
- All endpoints return 404 Not Found if resource does not exist OR belongs to a different user
- All endpoints return 400 Bad Request with field errors on validation failure
- Successful creates return 201 Created; reads return 200 OK; deletes return 204 No Content
- All request and response bodies are JSON (Content-Type: application/json)
- No HATEOAS. No pagination. Simple lists for MVP.
- User ownership is enforced in the service layer on every plant/care/watering/growth endpoint.

---

## 2. Authentication Mechanism

Mechanism: Spring Security session-based authentication with a custom JSON login endpoint.

This is NOT classic HTML form-login. PlantPal uses a REST controller that:
- Accepts POST /api/auth/login with a JSON body { email, password }
- Authenticates via AuthenticationManager (Spring Security)
- On success: creates an HttpSession and binds the SecurityContext to it
- Returns JSON with user info; browser stores JSESSIONID cookie automatically
- All subsequent requests send the JSESSIONID cookie; Spring Security validates it

Registration flow (corrected — no auto-login):
  POST /api/auth/register
    -> 201 Created
    -> Frontend shows "Registration successful. Please login."
    -> User navigates to /login page manually
    -> POST /api/auth/login
    -> 200 OK + session established
    -> Frontend redirects to /dashboard

Logout flow:
  POST /api/auth/logout
    -> Session invalidated
    -> CSRF token invalidated
    -> 200 OK

---

## 3. CSRF Protection Strategy

### 3.1 Why CSRF Protection Is Required

PlantPal uses HTTP session authentication with a JSESSIONID cookie. Browsers automatically
attach cookies to all requests to the same domain — including cross-origin requests triggered
by malicious pages on other domains. Without CSRF protection, an attacker could construct a
page that silently submits POST/PUT/DELETE requests to PlantPal using the victim's active
session cookie.

Example attack without CSRF:
  User is logged in to PlantPal at localhost:8080.
  User visits a malicious page on another tab.
  That page contains: fetch("http://localhost:8080/api/plants/5", { method:"DELETE" })
  Browser attaches JSESSIONID cookie automatically.
  Plant is deleted without the user's knowledge.

CSRF protection prevents this because the attacker's page cannot read the CSRF token
from the legitimate site (same-origin policy on cookies and DOM).

### 3.2 CSRF Strategy Chosen

Strategy: CookieCsrfTokenRepository with HttpOnly = false

Spring Security sets a cookie named XSRF-TOKEN.
- The XSRF-TOKEN cookie is readable by JavaScript (HttpOnly = false).
- For each state-changing request (POST, PUT, PATCH, DELETE), the frontend reads
  the XSRF-TOKEN cookie value and sends it as the X-XSRF-TOKEN request header.
- Spring Security validates that the X-XSRF-TOKEN header matches the XSRF-TOKEN cookie.
- A cross-origin attacker cannot read the XSRF-TOKEN cookie (same-origin policy),
  so they cannot forge the header, and the request is rejected.

### 3.3 Which Requests Require the CSRF Token

| Method   | CSRF Required? | Reason                                       |
|----------|----------------|----------------------------------------------|
| GET      | No             | Safe method, does not modify state           |
| POST     | Yes (most)     | Modifies state                               |
| PUT      | Yes            | Modifies state                               |
| PATCH    | Yes            | Modifies state                               |
| DELETE   | Yes            | Modifies state                               |

### 3.4 Exemptions from CSRF

The following endpoints are exempted from CSRF because they are called before a session
(and therefore before a CSRF token) exists. An unauthenticated POST cannot be CSRF'd
in the classical sense because there is no session to hijack.

Exempted endpoints:
- POST /api/auth/register  (no session exists yet)
- POST /api/auth/login     (no session exists yet)

All other POST/PUT/PATCH/DELETE endpoints require the X-XSRF-TOKEN header.

### 3.5 How the Frontend Sends the CSRF Token

In api.js (shared JavaScript helper), every state-changing fetch() call reads the
XSRF-TOKEN cookie and adds it as a header:

  function getCsrfToken() {
      const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
      return match ? decodeURIComponent(match[1]) : '';
  }

  function post(url, body) {
      return fetch(url, {
          method: 'POST',
          headers: {
              'Content-Type': 'application/json',
              'X-XSRF-TOKEN': getCsrfToken()
          },
          body: JSON.stringify(body),
          credentials: 'same-origin'
      });
  }

credentials: 'same-origin' ensures cookies (JSESSIONID and XSRF-TOKEN) are sent
automatically with every request to the same origin.

### 3.6 Login and Logout CSRF Handling

POST /api/auth/login: Exempted from CSRF (no session before login).
POST /api/auth/logout: Protected by CSRF. Frontend must send X-XSRF-TOKEN header.

The XSRF-TOKEN cookie is set by Spring Security on the first GET request (e.g., when the
login page loads). By the time the user fills the form and clicks Logout, the token exists.

### 3.7 Spring Security Configuration Summary

SecurityConfig.java:
  http.csrf(csrf -> csrf
      .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
      .ignoringRequestMatchers("/api/auth/register", "/api/auth/login")
  );

---

## 4. Common Response Formats

### Success (single object)
{ "id": 1, "name": "Basil", ... }

### Success (list)
[ { "id": 1, "name": "Basil" }, { "id": 2, "name": "Aloe Vera" } ]

### Validation Error (400)
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "name": "Plant name is required",
    "wateringIntervalDays": "Must be at least 1"
  }
}

### General Error
{ "status": 404, "message": "Plant not found" }

---

## 5. HTTP Status Code Reference

| Code | Meaning               | When Used                                               |
|------|-----------------------|---------------------------------------------------------|
| 200  | OK                    | Successful GET, PUT, PATCH, logout                      |
| 201  | Created               | Successful POST (create)                                |
| 204  | No Content            | Successful DELETE                                       |
| 400  | Bad Request           | Validation failure, malformed request                   |
| 401  | Unauthorized          | Not logged in / session expired                         |
| 403  | Forbidden             | Logged in but wrong role (e.g., USER accessing /admin/) |
| 404  | Not Found             | Resource does not exist OR belongs to another user      |
| 409  | Conflict              | Duplicate email on register, category name duplicate, category in use on delete |
| 500  | Internal Server Error | Unexpected server error                                 |

---

## 6. API Endpoints (27 total)

---

### 6.1 AUTH Endpoints (4 endpoints — Public/Any)

---

POST /api/auth/register                                              [PUBLIC — No CSRF required]
Purpose: Register a new user account.

Request Body:
{
  "fullName": "Hariharan P",
  "email": "hari@gmail.com",
  "password": "pass123",
  "confirmPassword": "pass123"
}

Validation:
- fullName: required, 2-100 chars
- email: required, valid format, unique in users table
- password: required, min 6 chars
- confirmPassword: must match password

Response 201 Created:
{ "message": "Registration successful. Please login." }

Response 400 Bad Request:
{ "status": 400, "message": "Validation failed",
  "errors": { "email": "Email is already registered" } }

---

POST /api/auth/login                                                 [PUBLIC — No CSRF required]
Purpose: Login. Returns user info and establishes HttpSession.

Request Body:
{ "email": "hari@gmail.com", "password": "pass123" }

Response 200 OK:
{
  "message": "Login successful",
  "user": { "id": 1, "fullName": "Hariharan P", "email": "hari@gmail.com", "role": "USER" }
}
(Browser receives Set-Cookie: JSESSIONID=... and XSRF-TOKEN=... automatically)

Response 401 Unauthorized:
{ "status": 401, "message": "Invalid email or password" }

---

POST /api/auth/logout                                                [AUTH required — CSRF required]
Purpose: Logout, invalidate session, invalidate CSRF token.
Request: No body. Must include X-XSRF-TOKEN header.

Response 200 OK:
{ "message": "Logged out successfully" }

---

GET /api/auth/me                                                     [AUTH required]
Purpose: Get the current logged-in user's info.

Response 200 OK:
{ "id": 1, "fullName": "Hariharan P", "email": "hari@gmail.com", "role": "USER", "createdAt": "2026-08-24" }

Response 401: { "status": 401, "message": "Not authenticated" }

---

### 6.2 CATEGORIES Endpoint (1 endpoint — USER or ADMIN)

---

GET /api/categories                                                  [AUTH required]
Purpose: Get all categories. Used to populate the dropdown on Add/Edit Plant forms.
Role: USER or ADMIN

Response 200 OK:
[
  { "id": 1, "name": "Herb",      "description": "Kitchen and cooking herbs" },
  { "id": 2, "name": "Succulent", "description": "Plants that store water"   },
  { "id": 3, "name": "Cactus",    "description": "Spiny desert cacti"        }
]

---

### 6.3 PLANTS Endpoints (6 endpoints — USER, owner-only for specific plant operations)

---

GET /api/plants                                                      [AUTH required — USER]
Purpose: Get all plants for the current user. Supports optional search/filter.

Query Parameters (all optional):
- search: case-insensitive name search
- categoryId: filter by category
- status: filter by plant status (HEALTHY | NEEDS_ATTENTION | INACTIVE)

Example: GET /api/plants?search=basil&status=HEALTHY

Response 200 OK:
[
  {
    "id": 1,
    "name": "Basil",
    "category": { "id": 1, "name": "Herb" },
    "location": "INDOOR",
    "description": "Kitchen basil plant",
    "status": "HEALTHY",
    "createdAt": "2026-08-10",
    "wateringStatus": "WATER_UPCOMING",
    "lastWateredDate": "2026-08-22",
    "nextWateringDate": "2026-08-25",
    "wateringIntervalDays": 3
  }
]

Note on lastWateredDate / wateringStatus:
- If care_schedules.last_watered_date IS NULL:
  lastWateredDate: null, nextWateringDate: null, wateringStatus: "NOT_SET"
- Only plants where plants.user_id = currentUser.id are returned.

---

GET /api/plants/{id}                                                 [AUTH required — USER, owner only]
Purpose: Get full details of a single plant.
Ownership: if plant.user_id != currentUser.id -> return 404 (not 403)

Response 200 OK:
{
  "id": 1, "name": "Basil",
  "category": { "id": 1, "name": "Herb" },
  "location": "INDOOR",
  "description": "Kitchen basil plant",
  "status": "HEALTHY",
  "createdAt": "2026-08-10",
  "updatedAt": "2026-08-22"
}

---

POST /api/plants                                                     [AUTH required — USER — CSRF required]
Purpose: Add a new plant.

Request Body:
{
  "name": "Basil",
  "categoryId": 1,
  "location": "INDOOR",
  "description": "Kitchen basil plant",
  "status": "HEALTHY",
  "wateringIntervalDays": 3,
  "lastWateredDate": "2026-08-22",
  "sunlightNeeds": "PARTIAL_SUN",
  "fertilizingIntervalDays": 30
}

Field rules:
- name:                   REQUIRED — 2 to 100 chars
- categoryId:             REQUIRED — must reference existing category
- location:               OPTIONAL — INDOOR | OUTDOOR | BALCONY | TERRACE | GARDEN
- description:            OPTIONAL — max 500 chars
- status:                 REQUIRED — HEALTHY | NEEDS_ATTENTION | INACTIVE (default HEALTHY)
- wateringIntervalDays:   REQUIRED — integer, min 1, max 365
- lastWateredDate:        OPTIONAL — NULL allowed (plant never yet watered)
                                     If provided: must be today or in the past
- sunlightNeeds:          OPTIONAL — FULL_SUN | PARTIAL_SUN | SHADE
- fertilizingIntervalDays:OPTIONAL — integer, min 1

Behavior: Creates plant row AND auto-creates care_schedule row.

Response 201 Created:
{
  "id": 5, "name": "Basil",
  "category": { "id": 1, "name": "Herb" },
  "location": "INDOOR", "status": "HEALTHY",
  "createdAt": "2026-08-24"
}

---

PUT /api/plants/{id}                                                 [AUTH required — USER, owner only — CSRF required]
Purpose: Update plant details.
Request Body: same structure as POST /api/plants
Ownership: 404 if not owner
Response 200 OK: updated plant object

---

DELETE /api/plants/{id}                                              [AUTH required — USER, owner only — CSRF required]
Purpose: Delete plant and all related records (cascades in DB).
Response 204 No Content
Response 404: not found or not owner

---

PATCH /api/plants/{id}/status                                        [AUTH required — USER, owner only — CSRF required]
Purpose: Quick status update from plant card.
Request Body: { "status": "NEEDS_ATTENTION" }
Response 200 OK: { "id": 1, "status": "NEEDS_ATTENTION" }

---

### 6.4 CARE SCHEDULE Endpoints (2 endpoints — USER, owner only)

---

GET /api/plants/{id}/care                                            [AUTH required — USER, owner only]
Purpose: Get care schedule for a plant.

Response 200 OK:
{
  "id": 1,
  "plantId": 5,
  "wateringIntervalDays": 3,
  "lastWateredDate": "2026-08-22",
  "nextWateringDate": "2026-08-25",
  "wateringStatus": "WATER_UPCOMING",
  "sunlightNeeds": "PARTIAL_SUN",
  "fertilizingIntervalDays": 30,
  "updatedAt": "2026-08-22"
}

If lastWateredDate is null:
{
  "lastWateredDate": null,
  "nextWateringDate": null,
  "wateringStatus": "NOT_SET"
}

---

PUT /api/plants/{id}/care                                            [AUTH required — USER, owner only — CSRF required]
Purpose: Update the care schedule.

Request Body:
{
  "wateringIntervalDays": 5,
  "lastWateredDate": "2026-08-20",
  "sunlightNeeds": "FULL_SUN",
  "fertilizingIntervalDays": 14
}

Validation:
- wateringIntervalDays: required, min 1, max 365
- lastWateredDate: optional, @PastOrPresent if provided

Response 200 OK: updated care schedule object

---

### 6.5 WATERING RECORDS Endpoints (2 endpoints — USER, owner only)

---

GET /api/plants/{id}/watering                                        [AUTH required — USER, owner only]
Purpose: Get watering history for a plant, most recent first.

Response 200 OK:
[
  { "id": 10, "plantId": 5, "wateredDate": "2026-08-22",
    "notes": "Regular morning watering", "createdAt": "2026-08-22T08:30:00" },
  { "id": 7,  "plantId": 5, "wateredDate": "2026-08-19",
    "notes": null, "createdAt": "2026-08-19T09:00:00" }
]

---

POST /api/plants/{id}/watering                                       [AUTH required — USER, owner only — CSRF required]
Purpose: Record a watering event.

Request Body:
{ "wateredDate": "2026-08-24", "notes": "Soil was dry" }

Validation:
- wateredDate: required, @PastOrPresent

Side effect: If wateredDate >= care_schedules.last_watered_date (or last_watered_date is null),
             update care_schedules.last_watered_date = wateredDate.

Response 201 Created:
{ "id": 11, "plantId": 5, "wateredDate": "2026-08-24",
  "notes": "Soil was dry", "createdAt": "2026-08-24T10:00:00" }

---

### 6.6 GROWTH RECORDS Endpoints (2 endpoints — USER, owner only)

---

GET /api/plants/{id}/growth                                          [AUTH required — USER, owner only]
Purpose: Get growth history, most recent first.

Response 200 OK:
[
  { "id": 3, "plantId": 5, "recordDate": "2026-08-24",
    "heightCm": 14.00, "leafCount": 10,
    "notes": "New leaves sprouting", "createdAt": "2026-08-24T11:00:00" }
]

---

POST /api/plants/{id}/growth                                         [AUTH required — USER, owner only — CSRF required]
Purpose: Add a growth observation.

Request Body:
{ "recordDate": "2026-08-24", "heightCm": 14.00, "leafCount": 10, "notes": "New leaves" }

Validation:
- recordDate: required, valid date
- heightCm: optional, if provided > 0
- leafCount: optional, if provided >= 0
- notes: optional, max 500 chars
- At least one of heightCm, leafCount, or notes must be provided

Response 201 Created: growth record object

---

### 6.7 DASHBOARD Endpoint (1 endpoint)

---

GET /api/dashboard                                                   [AUTH required — USER]
Purpose: All stats for the current user's dashboard.

Response 200 OK:
{
  "totalPlants": 8,
  "healthyPlants": 6,
  "needsAttentionPlants": 1,
  "inactivePlants": 1,
  "waterTodayCount": 1,
  "overdueCount": 1,
  "recentPlants": [
    { "id": 5, "name": "Basil", "categoryName": "Herb",
      "status": "HEALTHY", "wateringStatus": "WATER_UPCOMING",
      "lastWateredDate": "2026-08-22", "nextWateringDate": "2026-08-25" }
  ],
  "upcomingCare": [
    { "plantId": 5, "plantName": "Basil",
      "nextWateringDate": "2026-08-24", "wateringStatus": "WATER_TODAY" }
  ]
}

Notes:
- recentPlants: last 5 plants added by the user (created_at DESC)
- upcomingCare: plants whose nextWateringDate is within 7 days including overdue
- Plants with wateringStatus = NOT_SET are excluded from upcomingCare
- All counts are scoped to the current user's plants only

---

### 6.8 PROFILE Endpoints (3 endpoints)

---

GET /api/profile                                                     [AUTH required]
Response 200 OK:
{ "id": 1, "fullName": "Hariharan P", "email": "hari@gmail.com",
  "role": "USER", "createdAt": "2026-08-10", "totalPlants": 8 }

---

PUT /api/profile                                                     [AUTH required — CSRF required]
Request Body: { "fullName": "Hariharan Periyasamy" }
Validation: fullName required, 2-100 chars
Response 200 OK: updated profile object

---

PUT /api/profile/password                                            [AUTH required — CSRF required]
Request Body:
{ "currentPassword": "old", "newPassword": "new123", "confirmNewPassword": "new123" }
Validation:
- currentPassword: required, must match stored BCrypt hash
- newPassword: required, min 6 chars
- confirmNewPassword: must match newPassword
Response 200 OK: { "message": "Password changed successfully" }
Response 400: { "status": 400, "message": "Current password is incorrect" }

---

### 6.9 ADMIN Endpoints (7 endpoints — ADMIN only)

---

GET /api/admin/users                                                 [AUTH required — ADMIN only]
Purpose: View all registered users (READ-ONLY overview — no edit or delete).

Response 200 OK:
[
  { "id": 1, "fullName": "Hariharan P", "email": "hari@gmail.com",
    "role": "USER", "plantCount": 8, "createdAt": "2026-08-10" },
  { "id": 2, "fullName": "Priya S", "email": "priya@gmail.com",
    "role": "USER", "plantCount": 3, "createdAt": "2026-08-15" }
]

Note: This is a read-only user overview. Admin cannot edit or delete users in MVP.
      The page is titled "View Users" — NOT "Manage Users".

---

GET /api/admin/categories                                            [AUTH required — ADMIN only]
Purpose: Get all categories (admin-only view, same data as /api/categories).
Response 200 OK: list of category objects

---

POST /api/admin/categories                                           [AUTH required — ADMIN only — CSRF required]
Request Body: { "name": "Succulent", "description": "Plants that store water" }
Validation: name required, 2-100 chars, unique
Response 201 Created: category object
Response 409 Conflict: { "status": 409, "message": "Category name already exists" }

---

PUT /api/admin/categories/{id}                                       [AUTH required — ADMIN only — CSRF required]
Request Body: { "name": "Succulents", "description": "Updated" }
Response 200 OK: updated category object
Response 404: category not found

---

DELETE /api/admin/categories/{id}                                    [AUTH required — ADMIN only — CSRF required]
Response 204 No Content: success
Response 409 Conflict: { "status": 409, "message": "Cannot delete: plants are using this category" }

---

GET /api/admin/stats                                                 [AUTH required — ADMIN only]
Purpose: System-wide statistics.

Response 200 OK:
{
  "totalUsers": 12, "totalPlants": 45,
  "totalWateringRecords": 130, "totalGrowthRecords": 60,
  "totalCategories": 8,
  "plantsByStatus": { "HEALTHY": 32, "NEEDS_ATTENTION": 10, "INACTIVE": 3 }
}

---

## 7. Complete Endpoint Count — 27 Endpoints

| #  | Method | Endpoint                        | Auth  | Role        |
|----|--------|---------------------------------|-------|-------------|
| 1  | POST   | /api/auth/register              | No    | Public      |
| 2  | POST   | /api/auth/login                 | No    | Public      |
| 3  | POST   | /api/auth/logout                | Yes   | Any         |
| 4  | GET    | /api/auth/me                    | Yes   | Any         |
| 5  | GET    | /api/categories                 | Yes   | Any         |
| 6  | GET    | /api/plants                     | Yes   | USER        |
| 7  | GET    | /api/plants/{id}                | Yes   | USER (owner)|
| 8  | POST   | /api/plants                     | Yes   | USER        |
| 9  | PUT    | /api/plants/{id}                | Yes   | USER (owner)|
| 10 | DELETE | /api/plants/{id}                | Yes   | USER (owner)|
| 11 | PATCH  | /api/plants/{id}/status         | Yes   | USER (owner)|
| 12 | GET    | /api/plants/{id}/care           | Yes   | USER (owner)|
| 13 | PUT    | /api/plants/{id}/care           | Yes   | USER (owner)|
| 14 | GET    | /api/plants/{id}/watering       | Yes   | USER (owner)|
| 15 | POST   | /api/plants/{id}/watering       | Yes   | USER (owner)|
| 16 | GET    | /api/plants/{id}/growth         | Yes   | USER (owner)|
| 17 | POST   | /api/plants/{id}/growth         | Yes   | USER (owner)|
| 18 | GET    | /api/dashboard                  | Yes   | USER        |
| 19 | GET    | /api/profile                    | Yes   | Any         |
| 20 | PUT    | /api/profile                    | Yes   | Any         |
| 21 | PUT    | /api/profile/password           | Yes   | Any         |
| 22 | GET    | /api/admin/users                | Yes   | ADMIN       |
| 23 | GET    | /api/admin/categories           | Yes   | ADMIN       |
| 24 | POST   | /api/admin/categories           | Yes   | ADMIN       |
| 25 | PUT    | /api/admin/categories/{id}      | Yes   | ADMIN       |
| 26 | DELETE | /api/admin/categories/{id}      | Yes   | ADMIN       |
| 27 | GET    | /api/admin/stats                | Yes   | ADMIN       |

TOTAL: 27 endpoints. This is the single authoritative count across all documents.

---

## 8. Validation Strategy

### Backend (Authoritative)
| Annotation        | Applied To                                               |
|-------------------|----------------------------------------------------------|
| @NotBlank         | Required string fields (name, email, fullName)           |
| @NotNull          | Required non-string fields (categoryId, status, interval)|
| @Size(min, max)   | String length bounds                                     |
| @Email            | Email format                                             |
| @Min(1)           | wateringIntervalDays, heightCm (if provided)             |
| @Max(365)         | wateringIntervalDays                                     |
| @PastOrPresent    | lastWateredDate, wateredDate, recordDate                 |

Custom service-layer checks:
- password == confirmPassword (not an annotation)
- email uniqueness before save
- categoryId exists before save
- currentPassword matches BCrypt hash (profile password change)
- At least one of heightCm/leafCount/notes provided (growth record)
- category has no plants before delete

### Frontend (Convenience Only)
HTML5 attributes: required, minlength, maxlength, min, max, type="email", type="date"
JavaScript: password confirmation match check
Frontend validation is UX convenience. Backend is the enforcement layer.

---

## 9. Exception Handling Strategy

GlobalExceptionHandler (@ControllerAdvice):

| Exception                       | Status | Response                              |
|---------------------------------|--------|---------------------------------------|
| MethodArgumentNotValidException | 400    | { "status":400, "errors":{ field:msg}}|
| ResourceNotFoundException       | 404    | { "status":404, "message":"..." }     |
| DuplicateResourceException      | 409    | { "status":409, "message":"..." }     |
| BadCredentialsException         | 401    | { "status":401, "message":"..." }     |
| AccessDeniedException           | 403    | { "status":403, "message":"..." }     |
| Exception (catch-all)           | 500    | { "status":500, "message":"..." }     |

Custom exceptions:
  ResourceNotFoundException  extends RuntimeException
  DuplicateResourceException extends RuntimeException
  (Simple, beginner-readable, no complex hierarchy)

---

## 10. Security Rules Summary

| Rule                                    | Where Enforced              |
|-----------------------------------------|-----------------------------|
| Passwords BCrypt hashed                 | AuthService + PasswordEncoder bean |
| Session-based auth (JSON login)         | SecurityConfig + AuthController |
| CSRF: CookieCsrfTokenRepository         | SecurityConfig              |
| CSRF exemptions: register + login       | SecurityConfig .ignoringRequestMatchers |
| 401 for unauthenticated requests        | SecurityConfig              |
| 403 for wrong role                      | SecurityConfig / @PreAuthorize |
| 404 for wrong owner (not 403)           | Service layer (ownership check) |
| password_hash never in API response     | ResponseDTOs never include password fields |
| Admin endpoints blocked for USER role   | SecurityConfig / @PreAuthorize |
