# API_DOCUMENTATION.md
## PlantPal - REST API Design

---

## 1. API Design Principles

- All API endpoints are prefixed with /api
- Authentication uses Spring Security HTTP session (cookie-based)
- All protected endpoints return 401 Unauthorized if not logged in
- All endpoints return 403 Forbidden if the user lacks the required role
- All endpoints return 404 Not Found if the resource does not exist
- All endpoints return 400 Bad Request with field errors if validation fails
- Successful responses use standard HTTP status codes (200, 201, 204)
- All responses are JSON (Content-Type: application/json)
- Request bodies are JSON (Content-Type: application/json)
- No HATEOAS, no pagination for MVP (simple lists)
- User ownership is enforced server-side on every plant/care/watering/growth endpoint

---

## 2. Authentication Strategy

Spring Security form login with HTTP session:
- Login: POST /api/auth/login (JSON body)
- Logout: POST /api/auth/logout
- Session stored server-side. Session ID in browser cookie (JSESSIONID).
- Register: POST /api/auth/register -> 201 Created -> user must then login separately.
- No auto-login after register.

Registration flow:
  POST /api/auth/register
    -> 201 Created (success)
    -> redirect to /login page (frontend handles)

Login flow:
  POST /api/auth/login
    -> 200 OK + session cookie set
    -> frontend stores session via cookie (automatic)

Logout flow:
  POST /api/auth/logout
    -> 200 OK + session invalidated

---

## 3. Common Response Formats

### Success Response (single object)
{
  "id": 1,
  "name": "Basil",
  ...
}

### Success Response (list)
[
  { "id": 1, "name": "Basil" },
  { "id": 2, "name": "Aloe Vera" }
]

### Error Response (validation)
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "name": "Plant name is required",
    "wateringIntervalDays": "Watering interval must be at least 1 day"
  }
}

### Error Response (general)
{
  "status": 404,
  "message": "Plant not found"
}

---

## 4. HTTP Status Code Reference

| Code | Meaning              | When Used                                            |
|------|----------------------|------------------------------------------------------|
| 200  | OK                   | Successful GET, PUT, DELETE, logout                  |
| 201  | Created              | Successful POST (create)                             |
| 204  | No Content           | Successful DELETE (no body returned)                 |
| 400  | Bad Request          | Validation errors, malformed request                 |
| 401  | Unauthorized         | Not logged in                                        |
| 403  | Forbidden            | Logged in but wrong role, or not owner of resource   |
| 404  | Not Found            | Resource does not exist                              |
| 409  | Conflict             | Duplicate email on register, category in use         |
| 500  | Internal Server Error| Unexpected server error                              |

---

## 5. API Endpoints

---

### 5.1 AUTH Endpoints (Public — No Login Required)

---

POST /api/auth/register
Purpose: Register a new user account.
Auth: Public (no login required)
Role: N/A

Request Body:
{
  "fullName": "Hariharan P",
  "email": "hari@gmail.com",
  "password": "mypassword",
  "confirmPassword": "mypassword"
}

Validation:
- fullName: required, 2-100 chars
- email: required, valid email format, unique
- password: required, min 6 chars
- confirmPassword: must match password

Response 201 Created:
{
  "message": "Registration successful. Please login."
}

Response 400 Bad Request (validation):
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "email": "Email is already registered",
    "password": "Password must be at least 6 characters"
  }
}

---

POST /api/auth/login
Purpose: Login with email and password.
Auth: Public (no login required)
Role: N/A

Request Body:
{
  "email": "hari@gmail.com",
  "password": "mypassword"
}

Validation:
- email: required
- password: required

Response 200 OK:
{
  "message": "Login successful",
  "user": {
    "id": 1,
    "fullName": "Hariharan P",
    "email": "hari@gmail.com",
    "role": "USER"
  }
}

Response 401 Unauthorized:
{
  "status": 401,
  "message": "Invalid email or password"
}

Note: Browser receives Set-Cookie: JSESSIONID=... automatically.

---

POST /api/auth/logout
Purpose: Logout and invalidate the current session.
Auth: Required (logged in)
Role: USER or ADMIN

Request: No body

Response 200 OK:
{
  "message": "Logged out successfully"
}

---

GET /api/auth/me
Purpose: Get the currently logged-in user's info.
Auth: Required
Role: USER or ADMIN

Response 200 OK:
{
  "id": 1,
  "fullName": "Hariharan P",
  "email": "hari@gmail.com",
  "role": "USER",
  "createdAt": "2026-08-24"
}

Response 401 Unauthorized:
{
  "status": 401,
  "message": "Not authenticated"
}

---

### 5.2 CATEGORIES Endpoints

---

GET /api/categories
Purpose: Get all plant categories (used to populate the dropdown in Add/Edit Plant form).
Auth: Required
Role: USER or ADMIN

Response 200 OK:
[
  { "id": 1, "name": "Herb",      "description": "Kitchen herbs"    },
  { "id": 2, "name": "Succulent", "description": "Desert plants"    },
  { "id": 3, "name": "Flowering", "description": "Decorative flowers"}
]

---

POST /api/admin/categories
Purpose: Add a new plant category.
Auth: Required
Role: ADMIN only

Request Body:
{
  "name": "Succulent",
  "description": "Plants that store water in their leaves"
}

Validation:
- name: required, 2-100 chars, unique

Response 201 Created:
{
  "id": 5,
  "name": "Succulent",
  "description": "Plants that store water in their leaves",
  "createdAt": "2026-08-24T12:00:00"
}

Response 409 Conflict:
{
  "status": 409,
  "message": "Category name already exists"
}

---

PUT /api/admin/categories/{id}
Purpose: Update an existing category.
Auth: Required
Role: ADMIN only

Request Body:
{
  "name": "Succulents and Cacti",
  "description": "Updated description"
}

Response 200 OK: (updated category object)
Response 404 Not Found: (if category id does not exist)

---

DELETE /api/admin/categories/{id}
Purpose: Delete a category.
Auth: Required
Role: ADMIN only

Response 204 No Content: (success)

Response 409 Conflict:
{
  "status": 409,
  "message": "Cannot delete category. Plants are assigned to this category."
}

---

### 5.3 PLANTS Endpoints (User owns their plants)

---

GET /api/plants
Purpose: Get all plants owned by the currently logged-in user.
Auth: Required
Role: USER

Query Parameters (all optional):
- search: string (searches plant name, case-insensitive)
- categoryId: long (filter by category)
- status: string (filter by plant status: HEALTHY, NEEDS_ATTENTION, INACTIVE)

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

Note:
- wateringStatus, lastWateredDate, nextWateringDate, wateringIntervalDays are computed/fetched
  from care_schedules and included in the plant list response for convenience.
- Only plants where plants.user_id = current user's id are returned.

---

GET /api/plants/{id}
Purpose: Get full details of a single plant.
Auth: Required
Role: USER (owner only)

Response 200 OK:
{
  "id": 1,
  "name": "Basil",
  "category": { "id": 1, "name": "Herb" },
  "location": "INDOOR",
  "description": "Kitchen basil plant",
  "status": "HEALTHY",
  "createdAt": "2026-08-10",
  "updatedAt": "2026-08-22"
}

Response 404 Not Found: (plant does not exist or belongs to another user)

Note: Ownership check: if plant.user_id != currentUser.id -> return 404 (not 403, to avoid
information disclosure about which plant IDs exist).

---

POST /api/plants
Purpose: Add a new plant for the logged-in user.
Auth: Required
Role: USER

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

Note:
- sunlightNeeds and fertilizingIntervalDays are optional.
- lastWateredDate is optional (can be null if plant has never been watered).
- categoryId must reference an existing category.
- After creating the plant, a care_schedule record is automatically created.

Validation:
- name: required, 2-100 chars
- categoryId: required, must exist
- status: required, must be HEALTHY | NEEDS_ATTENTION | INACTIVE
- wateringIntervalDays: required, min 1, max 365
- location: optional, must be INDOOR | OUTDOOR | BALCONY | TERRACE | GARDEN if provided

Response 201 Created:
{
  "id": 5,
  "name": "Basil",
  "category": { "id": 1, "name": "Herb" },
  "location": "INDOOR",
  "description": "Kitchen basil plant",
  "status": "HEALTHY",
  "createdAt": "2026-08-24"
}

---

PUT /api/plants/{id}
Purpose: Update an existing plant.
Auth: Required
Role: USER (owner only)

Request Body: Same structure as POST /api/plants

Response 200 OK: (updated plant object)
Response 404 Not Found: (not found or not owner)
Response 400 Bad Request: (validation errors)

---

DELETE /api/plants/{id}
Purpose: Delete a plant and all its related records (care schedule, watering, growth).
Auth: Required
Role: USER (owner only)

Response 204 No Content: (success, cascades automatically via FK)
Response 404 Not Found: (not found or not owner)

---

PATCH /api/plants/{id}/status
Purpose: Update only the plant status (quick status change from plant card).
Auth: Required
Role: USER (owner only)

Request Body:
{
  "status": "NEEDS_ATTENTION"
}

Response 200 OK:
{
  "id": 1,
  "status": "NEEDS_ATTENTION"
}

---

### 5.4 CARE SCHEDULE Endpoints

---

GET /api/plants/{id}/care
Purpose: Get care schedule for a plant.
Auth: Required
Role: USER (owner only)

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

Note: nextWateringDate and wateringStatus are computed in Java, not stored in DB.

---

PUT /api/plants/{id}/care
Purpose: Update care schedule for a plant.
Auth: Required
Role: USER (owner only)

Request Body:
{
  "wateringIntervalDays": 5,
  "lastWateredDate": "2026-08-20",
  "sunlightNeeds": "FULL_SUN",
  "fertilizingIntervalDays": 14
}

Validation:
- wateringIntervalDays: required, min 1, max 365
- lastWateredDate: optional, must be a valid date, cannot be in the future

Response 200 OK: (updated care schedule object)
Response 404 Not Found: (plant not found or not owner)

---

### 5.5 WATERING RECORDS Endpoints

---

GET /api/plants/{id}/watering
Purpose: Get watering history for a plant.
Auth: Required
Role: USER (owner only)

Response 200 OK:
[
  {
    "id": 10,
    "plantId": 5,
    "wateredDate": "2026-08-22",
    "notes": "Regular morning watering",
    "createdAt": "2026-08-22T08:30:00"
  },
  {
    "id": 7,
    "plantId": 5,
    "wateredDate": "2026-08-19",
    "notes": null,
    "createdAt": "2026-08-19T09:00:00"
  }
]

Note: Ordered by wateredDate DESC (most recent first).

---

POST /api/plants/{id}/watering
Purpose: Record a new watering event for a plant.
Auth: Required
Role: USER (owner only)

Request Body:
{
  "wateredDate": "2026-08-24",
  "notes": "Watered after the soil was dry"
}

Validation:
- wateredDate: required, valid date, cannot be in the future

Side Effect: Updates care_schedules.last_watered_date to the wateredDate if it is more recent
             than the current last_watered_date.

Response 201 Created:
{
  "id": 11,
  "plantId": 5,
  "wateredDate": "2026-08-24",
  "notes": "Watered after the soil was dry",
  "createdAt": "2026-08-24T10:00:00"
}

---

### 5.6 GROWTH RECORDS Endpoints

---

GET /api/plants/{id}/growth
Purpose: Get growth history for a plant.
Auth: Required
Role: USER (owner only)

Response 200 OK:
[
  {
    "id": 3,
    "plantId": 5,
    "recordDate": "2026-08-24",
    "heightCm": 14.00,
    "leafCount": 10,
    "notes": "New leaves sprouting",
    "createdAt": "2026-08-24T11:00:00"
  },
  {
    "id": 1,
    "plantId": 5,
    "recordDate": "2026-08-10",
    "heightCm": 8.00,
    "leafCount": 5,
    "notes": "Planted today",
    "createdAt": "2026-08-10T09:00:00"
  }
]

Note: Ordered by recordDate DESC.

---

POST /api/plants/{id}/growth
Purpose: Add a growth observation record for a plant.
Auth: Required
Role: USER (owner only)

Request Body:
{
  "recordDate": "2026-08-24",
  "heightCm": 14.00,
  "leafCount": 10,
  "notes": "New leaves sprouting"
}

Validation:
- recordDate: required, valid date
- heightCm: optional, if provided must be > 0
- leafCount: optional, if provided must be >= 0
- notes: optional, max 500 chars
- At least one of heightCm, leafCount, or notes must be provided

Response 201 Created:
{
  "id": 3,
  "plantId": 5,
  "recordDate": "2026-08-24",
  "heightCm": 14.00,
  "leafCount": 10,
  "notes": "New leaves sprouting",
  "createdAt": "2026-08-24T11:00:00"
}

---

### 5.7 DASHBOARD Endpoint

---

GET /api/dashboard
Purpose: Get dashboard statistics for the currently logged-in user.
Auth: Required
Role: USER

Response 200 OK:
{
  "totalPlants": 8,
  "healthyPlants": 6,
  "needsAttentionPlants": 1,
  "inactivePlants": 1,
  "waterTodayCount": 1,
  "overdueCount": 1,
  "recentPlants": [
    {
      "id": 8,
      "name": "Jasmine",
      "categoryName": "Flowering",
      "status": "NEEDS_ATTENTION",
      "wateringStatus": "WATER_OVERDUE",
      "lastWateredDate": "2026-08-16",
      "nextWateringDate": "2026-08-19"
    },
    ...
  ],
  "upcomingCare": [
    {
      "plantId": 5,
      "plantName": "Basil",
      "nextWateringDate": "2026-08-24",
      "wateringStatus": "WATER_TODAY"
    },
    ...
  ]
}

Note:
- recentPlants: last 5 plants added by the user (ordered by created_at DESC)
- upcomingCare: plants whose nextWateringDate is within the next 7 days (including overdue)
- All counts are for the logged-in user's plants only

---

### 5.8 ADMIN Endpoints

---

GET /api/admin/users
Purpose: Get list of all registered users.
Auth: Required
Role: ADMIN only

Response 200 OK:
[
  {
    "id": 1,
    "fullName": "Hariharan P",
    "email": "hari@gmail.com",
    "role": "USER",
    "plantCount": 8,
    "createdAt": "2026-08-10"
  },
  {
    "id": 2,
    "fullName": "Priya S",
    "email": "priya@gmail.com",
    "role": "USER",
    "plantCount": 3,
    "createdAt": "2026-08-15"
  }
]

Note: Admin sees all users. plantCount is the number of plants owned by each user.

---

GET /api/admin/stats
Purpose: Get system-wide statistics.
Auth: Required
Role: ADMIN only

Response 200 OK:
{
  "totalUsers": 12,
  "totalPlants": 45,
  "totalWateringRecords": 130,
  "totalGrowthRecords": 60,
  "totalCategories": 8,
  "plantsByStatus": {
    "HEALTHY": 32,
    "NEEDS_ATTENTION": 10,
    "INACTIVE": 3
  }
}

---

GET /api/admin/categories  (alias)
GET /api/categories        (same data, accessible by all logged-in users)
Purpose: Get all categories (admin version has same data)
Auth: Required
Role: ADMIN only (for /api/admin/categories)
      USER or ADMIN (for /api/categories)

---

## 6. User Profile Endpoints

---

GET /api/profile
Purpose: Get the current user's profile.
Auth: Required
Role: USER or ADMIN

Response 200 OK:
{
  "id": 1,
  "fullName": "Hariharan P",
  "email": "hari@gmail.com",
  "role": "USER",
  "createdAt": "2026-08-10",
  "totalPlants": 8
}

---

PUT /api/profile
Purpose: Update the current user's profile (name only for MVP).
Auth: Required
Role: USER or ADMIN

Request Body:
{
  "fullName": "Hariharan Periyasamy"
}

Validation:
- fullName: required, 2-100 chars

Response 200 OK: (updated profile object)

---

PUT /api/profile/password
Purpose: Change the current user's password.
Auth: Required
Role: USER or ADMIN

Request Body:
{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword",
  "confirmNewPassword": "newpassword"
}

Validation:
- currentPassword: required, must match stored hash
- newPassword: required, min 6 chars
- confirmNewPassword: must match newPassword

Response 200 OK:
{
  "message": "Password changed successfully"
}

Response 400 Bad Request:
{
  "status": 400,
  "message": "Current password is incorrect"
}

---

## 7. Endpoint Summary Table

| Method | Endpoint                          | Auth | Role        | Purpose                         |
|--------|-----------------------------------|------|-------------|---------------------------------|
| POST   | /api/auth/register                | No   | Public      | Register new user               |
| POST   | /api/auth/login                   | No   | Public      | Login                           |
| POST   | /api/auth/logout                  | Yes  | Any         | Logout                          |
| GET    | /api/auth/me                      | Yes  | Any         | Get current user info           |
| GET    | /api/categories                   | Yes  | Any         | Get all categories (dropdown)   |
| GET    | /api/plants                       | Yes  | USER        | Get my plants (search/filter)   |
| GET    | /api/plants/{id}                  | Yes  | USER (owner)| Get plant details               |
| POST   | /api/plants                       | Yes  | USER        | Add new plant                   |
| PUT    | /api/plants/{id}                  | Yes  | USER (owner)| Edit plant                      |
| DELETE | /api/plants/{id}                  | Yes  | USER (owner)| Delete plant                    |
| PATCH  | /api/plants/{id}/status           | Yes  | USER (owner)| Update plant status             |
| GET    | /api/plants/{id}/care             | Yes  | USER (owner)| Get care schedule               |
| PUT    | /api/plants/{id}/care             | Yes  | USER (owner)| Update care schedule            |
| GET    | /api/plants/{id}/watering         | Yes  | USER (owner)| Get watering history            |
| POST   | /api/plants/{id}/watering         | Yes  | USER (owner)| Record watering event           |
| GET    | /api/plants/{id}/growth           | Yes  | USER (owner)| Get growth history              |
| POST   | /api/plants/{id}/growth           | Yes  | USER (owner)| Add growth record               |
| GET    | /api/dashboard                    | Yes  | USER        | Get dashboard stats             |
| GET    | /api/profile                      | Yes  | Any         | Get my profile                  |
| PUT    | /api/profile                      | Yes  | Any         | Update my profile               |
| PUT    | /api/profile/password             | Yes  | Any         | Change my password              |
| GET    | /api/admin/users                  | Yes  | ADMIN       | Get all users                   |
| GET    | /api/admin/categories             | Yes  | ADMIN       | Get all categories (admin view) |
| POST   | /api/admin/categories             | Yes  | ADMIN       | Add category                    |
| PUT    | /api/admin/categories/{id}        | Yes  | ADMIN       | Edit category                   |
| DELETE | /api/admin/categories/{id}        | Yes  | ADMIN       | Delete category                 |
| GET    | /api/admin/stats                  | Yes  | ADMIN       | System statistics               |

Total endpoints: 26

---

## 8. Validation Strategy

### Backend Validation (Spring Validation / javax.validation)
- All request DTOs use @NotBlank, @NotNull, @Size, @Min, @Max, @Email annotations
- @Valid annotation on @RequestBody in all controller methods
- MethodArgumentNotValidException is caught by GlobalExceptionHandler
- Returns 400 with field-level error map

### Frontend Validation (HTML5 + JavaScript)
- HTML5 required, minlength, maxlength, min, max attributes on all form fields
- JavaScript checks: password confirmation match, email format
- Frontend validation is UX convenience only; backend validation is the enforcement layer

### Ownership Validation
- Every plant endpoint extracts current user from Spring Security context
- Queries always include AND plants.user_id = :currentUserId
- If plant not found OR belongs to different user -> return 404 (not 403)
  Reason: returning 403 reveals that the plant ID exists; 404 is safer

---

## 9. Exception / Error Handling Strategy

### GlobalExceptionHandler (@ControllerAdvice)

| Exception                          | HTTP Status | Response                              |
|------------------------------------|-------------|---------------------------------------|
| MethodArgumentNotValidException    | 400         | Field-level validation error map      |
| ResourceNotFoundException          | 404         | { "status": 404, "message": "..." }  |
| AccessDeniedException              | 403         | { "status": 403, "message": "..." }  |
| DuplicateResourceException         | 409         | { "status": 409, "message": "..." }  |
| AuthenticationException            | 401         | { "status": 401, "message": "..." }  |
| Exception (catch-all)              | 500         | { "status": 500, "message": "..." }  |

Custom exceptions (simple, beginner-readable):
- ResourceNotFoundException extends RuntimeException
- DuplicateResourceException extends RuntimeException

---

## 10. Security Rules Summary

| Rule | Implementation |
|------|---------------|
| Passwords hashed with BCrypt | PasswordEncoder bean, BCryptPasswordEncoder |
| Session-based auth | Spring Security HttpSession |
| Unauthenticated request -> 401 | Spring Security configuration |
| USER cannot access ADMIN endpoints -> 403 | @PreAuthorize("hasRole('ADMIN')") or configure in SecurityConfig |
| USER can only access own plants | user_id check in service layer before returning data |
| No plain-text passwords in API responses | password_hash never included in response DTOs |
| No sensitive data exposed | ResponseDTO never contains password fields |
