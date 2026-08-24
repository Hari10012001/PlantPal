# DATABASE_DESIGN.md
## PlantPal - Six-Table Database Design
## Version 2 — Updated after Gate 2 Conditional Approval

---

## 1. Design Principles

- Six tables only. No extras added without approved technical justification.
- All primary keys are BIGINT AUTO_INCREMENT (simple, beginner-readable).
- Foreign keys enforce referential integrity at the database level.
- Timestamps (created_at, updated_at) on every table for audit trail.
- No stored computed fields. Watering status is computed in Java at runtime.
- lastWateredDate is OPTIONAL (NULL) for newly added plants.
- Character set: utf8mb4 (supports all Unicode plant names).
- Collation: utf8mb4_unicode_ci.

---

## 2. Table Overview

| # | Table Name       | Purpose                                    |
|---|------------------|--------------------------------------------|
| 1 | users            | Registered users and admin accounts        |
| 2 | plant_categories | Admin-managed plant categories             |
| 3 | plants           | Core plant records, one per user per plant |
| 4 | care_schedules   | Watering interval and care settings        |
| 5 | watering_records | History of watering events per plant       |
| 6 | growth_records   | History of growth observations per plant   |

NO additional tables. No notification table. No audit table. No user-session table.

---

## 3. Entity Relationship Summary

users             (1) ---- (N) plants
plant_categories  (1) ---- (N) plants
plants            (1) ---- (1) care_schedules    [CASCADE DELETE]
plants            (1) ---- (N) watering_records  [CASCADE DELETE]
plants            (1) ---- (N) growth_records    [CASCADE DELETE]

---

## 4. Table Definitions

---

### TABLE 1: users

Purpose: All registered users. Admin accounts are created at application startup
         by DataInitializer.java — NOT by public self-registration.

| Column        | Type                    | Constraints                    | Notes                        |
|---------------|-------------------------|--------------------------------|------------------------------|
| id            | BIGINT                  | PRIMARY KEY, AUTO_INCREMENT    | Internal user identifier     |
| full_name     | VARCHAR(100)            | NOT NULL                       | Display name                 |
| email         | VARCHAR(150)            | NOT NULL, UNIQUE               | Login identifier             |
| password_hash | VARCHAR(255)            | NOT NULL                       | BCrypt hash ONLY             |
| role          | ENUM('USER','ADMIN')    | NOT NULL, DEFAULT 'USER'       | Role-based access control    |
| created_at    | DATETIME                | NOT NULL, DEFAULT NOW()        | Registration timestamp       |
| updated_at    | DATETIME                | NOT NULL, DEFAULT NOW()        | Last update timestamp        |

SQL:
CREATE TABLE users (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    full_name     VARCHAR(100)   NOT NULL,
    email         VARCHAR(150)   NOT NULL UNIQUE,
    password_hash VARCHAR(255)   NOT NULL,
    role          ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
                                 ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Rules:
- Passwords are NEVER stored as plain text. BCrypt hash ONLY.
- Email is the login identifier. Must be unique.
- ADMIN accounts are created by DataInitializer.java at startup (not data.sql).
- Public registration always assigns role = 'USER'.

---

### TABLE 2: plant_categories

Purpose: Admin-managed list of plant category types.

| Column      | Type         | Constraints                 | Notes                    |
|-------------|--------------|-----------------------------|-----------------------------|
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | Internal identifier      |
| name        | VARCHAR(100) | NOT NULL, UNIQUE            | Category name            |
| description | VARCHAR(500) | NULL                        | Optional description     |
| created_at  | DATETIME     | NOT NULL, DEFAULT NOW()     | Creation timestamp       |

SQL:
CREATE TABLE plant_categories (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500) NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Rules:
- Only ADMIN can create, edit, or delete categories.
- A category cannot be deleted if any plant uses it (RESTRICT foreign key).

Seed Categories (8 categories, seeded by DataInitializer.java):
  1. Herb        — Kitchen and cooking herbs (basil, mint, coriander)
  2. Succulent   — Plants that store water in leaves
  3. Flowering   — Decorative flowering plants
  4. Vegetable   — Edible vegetable plants
  5. Tree        — Small ornamental or fruit trees
  6. Shrub       — Woody perennial plants
  7. Fern        — Shade-loving fern varieties
  8. Cactus      — Spiny desert cacti

Note: "Indoor" and "Outdoor" are NOT plant categories.
      Plant location (Indoor/Outdoor/Balcony/etc.) is captured in the
      plants.location field as an ENUM, not as a category.

---

### TABLE 3: plants

Purpose: Core plant record. One row per plant per user.

| Column      | Type                                           | Constraints                       | Notes                     |
|-------------|------------------------------------------------|-----------------------------------|---------------------------|
| id          | BIGINT                                         | PRIMARY KEY, AUTO_INCREMENT       | Internal identifier       |
| user_id     | BIGINT                                         | NOT NULL, FK -> users.id          | Owner of this plant       |
| category_id | BIGINT                                         | NOT NULL, FK -> plant_categories  | Plant category            |
| name        | VARCHAR(100)                                   | NOT NULL                          | Plant name (e.g., Basil)  |
| location    | ENUM('INDOOR','OUTDOOR','BALCONY','TERRACE','GARDEN') | NULL                   | Where plant is kept       |
| description | VARCHAR(500)                                   | NULL                              | Optional notes            |
| status      | ENUM('HEALTHY','NEEDS_ATTENTION','INACTIVE')   | NOT NULL, DEFAULT 'HEALTHY'       | User-managed status       |
| created_at  | DATETIME                                       | NOT NULL, DEFAULT NOW()           | Added timestamp           |
| updated_at  | DATETIME                                       | NOT NULL, DEFAULT NOW()           | Last edit timestamp       |

SQL:
CREATE TABLE plants (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    category_id BIGINT       NOT NULL,
    name        VARCHAR(100) NOT NULL,
    location    ENUM('INDOOR','OUTDOOR','BALCONY','TERRACE','GARDEN') NULL,
    description VARCHAR(500) NULL,
    status      ENUM('HEALTHY','NEEDS_ATTENTION','INACTIVE') NOT NULL DEFAULT 'HEALTHY',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_plant_user     FOREIGN KEY (user_id)
        REFERENCES users(id)            ON DELETE CASCADE,
    CONSTRAINT fk_plant_category FOREIGN KEY (category_id)
        REFERENCES plant_categories(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Rules:
- user_id is the ownership key. ALL queries filter by user_id = currentUser.id.
- Cascade DELETE on user_id: deleting a user removes all their plants.
- Restrict DELETE on category_id: cannot delete a category with plants.
- Watering interval and last_watered_date live in care_schedules (not here).

---

### TABLE 4: care_schedules

Purpose: Watering interval, last watered date, and optional care settings.
         ONE record per plant (1-to-1 with plants table).

| Column                   | Type                                    | Constraints              | Notes                                   |
|--------------------------|-----------------------------------------|--------------------------|-----------------------------------------|
| id                       | BIGINT                                  | PRIMARY KEY, AUTO_INCREMENT | Internal identifier                  |
| plant_id                 | BIGINT                                  | NOT NULL, UNIQUE, FK -> plants.id | 1-to-1 with plants            |
| watering_interval_days   | INT                                     | NOT NULL, DEFAULT 7      | How often to water (days)               |
| last_watered_date        | DATE                                    | NULL                     | NULL for plants never yet watered       |
| sunlight_needs           | ENUM('FULL_SUN','PARTIAL_SUN','SHADE')  | NULL                     | Optional                                |
| fertilizing_interval_days| INT                                     | NULL                     | Optional                                |
| created_at               | DATETIME                                | NOT NULL, DEFAULT NOW()  | Record creation timestamp               |
| updated_at               | DATETIME                                | NOT NULL, DEFAULT NOW()  | Last update timestamp                   |

SQL:
CREATE TABLE care_schedules (
    id                        BIGINT   NOT NULL AUTO_INCREMENT,
    plant_id                  BIGINT   NOT NULL UNIQUE,
    watering_interval_days    INT      NOT NULL DEFAULT 7,
    last_watered_date         DATE     NULL,
    sunlight_needs            ENUM('FULL_SUN','PARTIAL_SUN','SHADE') NULL,
    fertilizing_interval_days INT      NULL,
    created_at                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
                                       ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_care_plant FOREIGN KEY (plant_id)
        REFERENCES plants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Rules:
- UNIQUE on plant_id enforces the 1-to-1 relationship with plants.
- ON DELETE CASCADE: deleting a plant removes its care schedule.
- last_watered_date IS NULL when a plant has never been watered.
  This is the authoritative, consistent rule across all documents.
- watering_interval_days must be >= 1 (validated in Java).
- A care_schedule record is auto-created when a plant is added.

Computed Fields (Java — NOT stored in DB):

  nextWateringDate:
    if lastWateredDate == null -> null (not calculable)
    else -> lastWateredDate.plusDays(wateringIntervalDays)

  wateringStatus:
    if lastWateredDate == null         -> WateringStatus.NOT_SET
    if nextWateringDate.isBefore(today)-> WateringStatus.WATER_OVERDUE
    if nextWateringDate.isEqual(today) -> WateringStatus.WATER_TODAY
    else                               -> WateringStatus.WATER_UPCOMING

---

### TABLE 5: watering_records

Purpose: History of every watering event for each plant.

| Column       | Type         | Constraints                | Notes                                  |
|--------------|--------------|----------------------------|----------------------------------------|
| id           | BIGINT       | PRIMARY KEY, AUTO_INCREMENT| Internal identifier                    |
| plant_id     | BIGINT       | NOT NULL, FK -> plants.id  | Plant that was watered                 |
| watered_date | DATE         | NOT NULL                   | Date the plant was watered             |
| notes        | VARCHAR(500) | NULL                       | Optional note                          |
| created_at   | DATETIME     | NOT NULL, DEFAULT NOW()    | Record creation timestamp              |

SQL:
CREATE TABLE watering_records (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    plant_id     BIGINT       NOT NULL,
    watered_date DATE         NOT NULL,
    notes        VARCHAR(500) NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_watering_plant FOREIGN KEY (plant_id)
        REFERENCES plants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Rules:
- ON DELETE CASCADE: deleting a plant removes all watering records.
- Recording a watering event creates a row here AND updates
  care_schedules.last_watered_date if watered_date >= current last_watered_date.
- Users can only add/view records for plants they own.
- watered_date must not be in the future (validated in Java with @PastOrPresent).

---

### TABLE 6: growth_records

Purpose: Growth observations for each plant (height, leaves, notes).

| Column      | Type          | Constraints                 | Notes                                |
|-------------|---------------|-----------------------------|--------------------------------------|
| id          | BIGINT        | PRIMARY KEY, AUTO_INCREMENT | Internal identifier                  |
| plant_id    | BIGINT        | NOT NULL, FK -> plants.id   | Owning plant                         |
| record_date | DATE          | NOT NULL                    | Date of the observation              |
| height_cm   | DECIMAL(6,2)  | NULL                        | Plant height in cm (optional)        |
| leaf_count  | INT           | NULL                        | Leaf count (optional)                |
| notes       | VARCHAR(500)  | NULL                        | Observation note (optional)          |
| created_at  | DATETIME      | NOT NULL, DEFAULT NOW()     | Record creation timestamp            |

SQL:
CREATE TABLE growth_records (
    id          BIGINT        NOT NULL AUTO_INCREMENT,
    plant_id    BIGINT        NOT NULL,
    record_date DATE          NOT NULL,
    height_cm   DECIMAL(6,2)  NULL,
    leaf_count  INT           NULL,
    notes       VARCHAR(500)  NULL,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_growth_plant FOREIGN KEY (plant_id)
        REFERENCES plants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Rules:
- ON DELETE CASCADE: deleting a plant removes all growth records.
- height_cm, leaf_count, and notes are all optional individually.
- At least one of the three optional fields should be provided (validated in Java).
- Users can only add/view records for plants they own.

---

## 5. ER Diagram (Text)

users
 id PK | full_name | email UNIQUE | password_hash | role ENUM | created_at | updated_at
   |
   | 1:N (user_id FK, CASCADE DELETE)
   v
plants
 id PK | user_id FK | category_id FK | name | location ENUM | description | status ENUM | created_at | updated_at
   |          |               |
   |1:1       |1:N            |1:N
   |CASCADE   |CASCADE        |CASCADE
   v          v               v
care_schedules    watering_records    growth_records
 id PK             id PK               id PK
 plant_id FK UNIQ  plant_id FK         plant_id FK
 watering_interval watered_date NOT NULL record_date NOT NULL
 last_watered NULL notes NULL          height_cm NULL
 sunlight NULL     created_at          leaf_count NULL
 fertilizing NULL                      notes NULL
 created_at                            created_at
 updated_at

plant_categories
 id PK | name UNIQUE | description | created_at
   ^
   | N:1 (category_id FK, RESTRICT DELETE)
   |
 plants.category_id

---

## 6. Cascade and Constraint Summary

| Action                    | Effect                                                 |
|---------------------------|--------------------------------------------------------|
| Delete user               | Cascades -> deletes plants -> cascades -> all records  |
| Delete plant              | Cascades -> care_schedule, watering_records, growth_records |
| Delete plant_category     | BLOCKED (RESTRICT) if any plant uses this category     |

---

## 7. Admin Account — Startup Initialization

Admin credentials are created at application startup by DataInitializer.java.

Mechanism:
  Application starts
  -> DataInitializer (implements CommandLineRunner) runs
  -> Check: does admin@plantpal.local exist in users table?
  -> If NO: create admin user with BCrypt-hashed password
  -> If YES: skip (idempotent)

Local development credentials (NOT for production):
  Email:    admin@plantpal.local
  Password: Admin@123 (BCrypt hashed at runtime — never stored as plain text)

DataInitializer also seeds plant_categories if the table is empty.

IMPORTANT: data.sql is NOT used for password seeding. BCrypt hashing requires
Java runtime. A SQL file cannot BCrypt a password — it can only insert a
pre-computed hash string, which is fragile and non-transparent.
DataInitializer.java is beginner-readable, self-documenting, and correct.

---

## 8. Indexes

| Table            | Column(s)   | Type   | Purpose                              |
|------------------|-------------|--------|--------------------------------------|
| users            | email       | UNIQUE | Login lookup, uniqueness             |
| plant_categories | name        | UNIQUE | Uniqueness enforcement               |
| plants           | user_id     | INDEX  | Fast "all plants by this user" query |
| plants           | category_id | INDEX  | Fast filter by category              |
| care_schedules   | plant_id    | UNIQUE | 1-to-1 enforcement                   |
| watering_records | plant_id    | INDEX  | Fast watering history lookup         |
| growth_records   | plant_id    | INDEX  | Fast growth history lookup           |

Note: InnoDB automatically creates an index for every FOREIGN KEY constraint.
