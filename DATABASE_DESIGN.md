# DATABASE_DESIGN.md
## PlantPal - Six-Table Database Design

---

## 1. Design Principles

- Six tables only. No extras added without technical justification.
- All primary keys are BIGINT AUTO_INCREMENT (simple, beginner-readable).
- Foreign keys enforce referential integrity at the database level.
- Timestamps (created_at, updated_at) on every table for audit trail.
- No stored computed fields. Watering status is computed in Java at runtime.
- Character set: utf8mb4 (supports all Unicode characters, including plant names).
- Collation: utf8mb4_unicode_ci.

---

## 2. Table Overview

| # | Table Name       | Purpose                                    | Rows (estimated) |
|---|------------------|--------------------------------------------|------------------|
| 1 | users            | Stores registered users and admin accounts | Small            |
| 2 | plant_categories | Admin-managed plant categories             | Small (~10-20)   |
| 3 | plants           | Core plant records per user                | Medium           |
| 4 | care_schedules   | Watering interval and care settings        | 1 per plant      |
| 5 | watering_records | History of watering events per plant       | Many per plant   |
| 6 | growth_records   | History of growth observations per plant   | Many per plant   |

---

## 3. Entity Relationship Summary

users          (1) ──────── (N) plants
plant_categories (1) ──────── (N) plants
plants         (1) ──────── (1) care_schedules
plants         (1) ──────── (N) watering_records
plants         (1) ──────── (N) growth_records

---

## 4. Table Definitions

---

### Table 1: users

Purpose: Stores all registered users. Admin accounts are pre-seeded, not self-registered.

| Column       | Type                      | Constraints                  | Notes                          |
|--------------|---------------------------|------------------------------|--------------------------------|
| id           | BIGINT                    | PRIMARY KEY, AUTO_INCREMENT  | Internal user identifier       |
| full_name    | VARCHAR(100)              | NOT NULL                     | User's display name            |
| email        | VARCHAR(150)              | NOT NULL, UNIQUE             | Used as login username         |
| password_hash| VARCHAR(255)              | NOT NULL                     | BCrypt hashed password         |
| role         | ENUM('USER','ADMIN')      | NOT NULL, DEFAULT 'USER'     | Role-based access control      |
| created_at   | DATETIME                  | NOT NULL, DEFAULT NOW()      | Account creation timestamp     |
| updated_at   | DATETIME                  | NOT NULL, DEFAULT NOW()      | Last profile update timestamp  |

SQL:
CREATE TABLE users (
    id            BIGINT         NOT NULL AUTO_INCREMENT,
    full_name     VARCHAR(100)   NOT NULL,
    email         VARCHAR(150)   NOT NULL UNIQUE,
    password_hash VARCHAR(255)   NOT NULL,
    role          ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Notes:
- Passwords are NEVER stored as plain text. BCrypt hash only.
- Email is the login identifier. Must be unique.
- ADMIN role accounts are created via data.sql seed, not public registration.
- USER is the default role for all public registrations.

---

### Table 2: plant_categories

Purpose: Admin-managed list of plant categories (e.g., Herb, Succulent, Flowering).

| Column      | Type         | Constraints                 | Notes                           |
|-------------|--------------|-----------------------------|---------------------------------|
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT | Internal category identifier    |
| name        | VARCHAR(100) | NOT NULL, UNIQUE            | Category name (e.g., Herb)      |
| description | VARCHAR(500) | NULL                        | Optional description            |
| created_at  | DATETIME     | NOT NULL, DEFAULT NOW()     | Category creation timestamp     |

SQL:
CREATE TABLE plant_categories (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500) NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Notes:
- Only ADMIN can create, edit, or delete categories.
- A category cannot be deleted if plants are assigned to it.
- Seed data will include common categories: Herb, Succulent, Flowering, Vegetable, Tree, Shrub, Indoor, Outdoor.

---

### Table 3: plants

Purpose: Core plant record. One row per plant per user.

| Column                | Type                                          | Constraints                 | Notes                              |
|-----------------------|-----------------------------------------------|-----------------------------|------------------------------------|
| id                    | BIGINT                                        | PRIMARY KEY, AUTO_INCREMENT | Internal plant identifier          |
| user_id               | BIGINT                                        | NOT NULL, FK -> users.id    | Owner of this plant record         |
| category_id           | BIGINT                                        | NOT NULL, FK -> plant_categories.id | Plant category           |
| name                  | VARCHAR(100)                                  | NOT NULL                    | Plant name (e.g., Basil)           |
| location              | ENUM('INDOOR','OUTDOOR','BALCONY','TERRACE','GARDEN') | NULL           | Where plant is kept                |
| description           | VARCHAR(500)                                  | NULL                        | Optional notes about the plant     |
| status                | ENUM('HEALTHY','NEEDS_ATTENTION','INACTIVE')  | NOT NULL, DEFAULT 'HEALTHY' | User-managed plant status          |
| created_at            | DATETIME                                      | NOT NULL, DEFAULT NOW()     | Plant added timestamp              |
| updated_at            | DATETIME                                      | NOT NULL, DEFAULT NOW()     | Last edit timestamp                |

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
    CONSTRAINT fk_plant_user     FOREIGN KEY (user_id)     REFERENCES users(id)            ON DELETE CASCADE,
    CONSTRAINT fk_plant_category FOREIGN KEY (category_id) REFERENCES plant_categories(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Notes:
- user_id is the ownership key. All queries filter by user_id = current logged-in user.
- ON DELETE CASCADE on user_id: deleting a user removes all their plants (cascade).
- ON DELETE RESTRICT on category_id: cannot delete a category with plants assigned.
- Watering interval and last watered date are in care_schedules (1-to-1 with this table).
- status is managed by the user manually. Not auto-calculated.

---

### Table 4: care_schedules

Purpose: Watering interval, last watered date, and optional care settings for each plant.
One care_schedule record exists per plant (1-to-1 relationship).

| Column                   | Type         | Constraints                     | Notes                               |
|--------------------------|--------------|---------------------------------|-------------------------------------|
| id                       | BIGINT       | PRIMARY KEY, AUTO_INCREMENT     | Internal identifier                 |
| plant_id                 | BIGINT       | NOT NULL, UNIQUE, FK -> plants.id | Owning plant (1-to-1)             |
| watering_interval_days   | INT          | NOT NULL, DEFAULT 7             | How often to water (in days)        |
| last_watered_date        | DATE         | NULL                            | Last date the plant was watered     |
| sunlight_needs           | ENUM('FULL_SUN','PARTIAL_SUN','SHADE') | NULL       | Optional sunlight requirement       |
| fertilizing_interval_days| INT          | NULL                            | Optional fertilizing interval       |
| created_at               | DATETIME     | NOT NULL, DEFAULT NOW()         | Record creation timestamp           |
| updated_at               | DATETIME     | NOT NULL, DEFAULT NOW()         | Last update timestamp               |

SQL:
CREATE TABLE care_schedules (
    id                        BIGINT    NOT NULL AUTO_INCREMENT,
    plant_id                  BIGINT    NOT NULL UNIQUE,
    watering_interval_days    INT       NOT NULL DEFAULT 7,
    last_watered_date         DATE      NULL,
    sunlight_needs            ENUM('FULL_SUN','PARTIAL_SUN','SHADE') NULL,
    fertilizing_interval_days INT       NULL,
    created_at                DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_care_plant FOREIGN KEY (plant_id) REFERENCES plants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Notes:
- UNIQUE on plant_id enforces 1-to-1 relationship with plants table.
- ON DELETE CASCADE: deleting a plant deletes its care schedule.
- last_watered_date is NULL when a plant has never been watered.
- next_watering_date = last_watered_date + watering_interval_days (computed in Java, not stored).
- watering_interval_days must be >= 1.
- Created automatically when a plant is added (with default values).

Computed fields (in Java, not stored):
- nextWateringDate = lastWateredDate.plusDays(wateringIntervalDays)
- wateringStatus:
    if lastWateredDate == null     -> "NOT_SET"
    if nextWateringDate < today    -> "WATER_OVERDUE"
    if nextWateringDate == today   -> "WATER_TODAY"
    if nextWateringDate > today    -> "WATER_UPCOMING"

---

### Table 5: watering_records

Purpose: History of every watering event for each plant.

| Column      | Type         | Constraints                  | Notes                               |
|-------------|--------------|------------------------------|-------------------------------------|
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT  | Internal identifier                 |
| plant_id    | BIGINT       | NOT NULL, FK -> plants.id    | The plant that was watered          |
| watered_date| DATE         | NOT NULL                     | Date the plant was watered          |
| notes       | VARCHAR(500) | NULL                         | Optional note about this watering   |
| created_at  | DATETIME     | NOT NULL, DEFAULT NOW()      | When this record was created        |

SQL:
CREATE TABLE watering_records (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    plant_id     BIGINT       NOT NULL,
    watered_date DATE         NOT NULL,
    notes        VARCHAR(500) NULL,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_watering_plant FOREIGN KEY (plant_id) REFERENCES plants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Notes:
- ON DELETE CASCADE: deleting a plant removes all its watering records.
- When a user records a watering event, this table gets a new row AND
  care_schedules.last_watered_date is updated to watered_date (most recent).
- Users can only add/view records for plants they own (user_id check via JOIN).
- No DELETE on individual watering records for MVP (history is immutable).

---

### Table 6: growth_records

Purpose: History of growth observations for each plant (height, leaf count, notes).

| Column      | Type         | Constraints                  | Notes                               |
|-------------|--------------|------------------------------|-------------------------------------|
| id          | BIGINT       | PRIMARY KEY, AUTO_INCREMENT  | Internal identifier                 |
| plant_id    | BIGINT       | NOT NULL, FK -> plants.id    | The plant this record belongs to    |
| record_date | DATE         | NOT NULL                     | Date of the growth observation      |
| height_cm   | DECIMAL(6,2) | NULL                         | Plant height in centimeters         |
| leaf_count  | INT          | NULL                         | Optional leaf count                 |
| notes       | VARCHAR(500) | NULL                         | Optional growth observation note    |
| created_at  | DATETIME     | NOT NULL DEFAULT NOW()       | When this record was created        |

SQL:
CREATE TABLE growth_records (
    id          BIGINT         NOT NULL AUTO_INCREMENT,
    plant_id    BIGINT         NOT NULL,
    record_date DATE           NOT NULL,
    height_cm   DECIMAL(6,2)   NULL,
    leaf_count  INT            NULL,
    notes       VARCHAR(500)   NULL,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_growth_plant FOREIGN KEY (plant_id) REFERENCES plants(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

Notes:
- ON DELETE CASCADE: deleting a plant removes all its growth records.
- height_cm and leaf_count are both optional; at least one meaningful field should be entered.
- Users can only add/view records for plants they own.
- DECIMAL(6,2) supports heights up to 9999.99 cm.

---

## 5. Entity Relationship Diagram (Text)

users
  id PK
  full_name
  email (UNIQUE)
  password_hash
  role (USER|ADMIN)
  created_at
  updated_at
    |
    | 1:N
    v
plants
  id PK
  user_id FK -> users.id      [CASCADE DELETE]
  category_id FK -> plant_categories.id  [RESTRICT DELETE]
  name
  location
  description
  status (HEALTHY|NEEDS_ATTENTION|INACTIVE)
  created_at
  updated_at
    |            |             |
    | 1:1        | 1:N         | 1:N
    v            v             v
care_schedules  watering_records  growth_records
  id PK           id PK             id PK
  plant_id FK     plant_id FK       plant_id FK
  watering_       watered_date      record_date
  interval_days   notes             height_cm
  last_watered_   created_at        leaf_count
  date                              notes
  sunlight_needs                    created_at
  fertilizing_
  interval_days
  created_at
  updated_at

plant_categories
  id PK
  name (UNIQUE)
  description
  created_at
    ^
    | N:1
    |
  plants.category_id

---

## 6. Cascade and Constraint Rules

| Action                        | Effect                                          |
|-------------------------------|-------------------------------------------------|
| Delete user                   | Deletes all plants -> cascades to all records   |
| Delete plant                  | Deletes care_schedule, watering_records, growth_records |
| Delete plant_category         | BLOCKED if plants exist with that category      |
| Delete watering_record        | Not supported in MVP (history is immutable)     |
| Delete growth_record          | Not supported in MVP (history is immutable)     |

---

## 7. Indexes

| Table            | Index Column   | Type    | Reason                                  |
|------------------|----------------|---------|-----------------------------------------|
| users            | email          | UNIQUE  | Login lookup, uniqueness enforcement    |
| plant_categories | name           | UNIQUE  | Uniqueness enforcement                  |
| plants           | user_id        | INDEX   | Fast lookup of all plants by owner      |
| plants           | category_id    | INDEX   | Fast lookup of plants by category       |
| care_schedules   | plant_id       | UNIQUE  | 1-to-1 enforcement                      |
| watering_records | plant_id       | INDEX   | Fast lookup of watering history         |
| growth_records   | plant_id       | INDEX   | Fast lookup of growth history           |

FK constraints automatically create indexes in InnoDB.

---

## 8. Seed Data (data.sql)

Admin user (pre-created, not self-registered):
  email: admin@plantpal.com
  password: admin123 (BCrypt hashed at application startup)
  role: ADMIN

Seed categories:
  Herb, Succulent, Flowering, Vegetable, Tree, Shrub, Indoor, Outdoor
