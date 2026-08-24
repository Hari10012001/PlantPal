# REQUIREMENTS.md
## PlantPal - Personal Plant Care, Watering Schedule and Growth Monitoring Platform

---

## 1. Project Overview

PlantPal is a simple web application that helps users manage their household, indoor, balcony,
terrace, or small-garden plants. It enables plant lovers to register their plants, set care
schedules, track watering history, record growth observations, and monitor the health status
of each plant through a clean dashboard.

---

## 2. Problem Statement

Plant owners often forget to water their plants on time or lose track of when a plant was last
watered. Without a system to record and remind, plants suffer from overwatering or underwatering.
There is no lightweight, free, locally-run tool for personal plant care management that a beginner
developer can build and use.

---

## 3. Existing System

Most plant care tools today are:
- Mobile apps with subscriptions
- AI-powered plant disease detection apps
- Smart IoT irrigation systems
- Cloud-hosted services requiring sign-up
- Apps that depend on paid external plant databases

### Limitations of Existing Systems
- Require internet connectivity
- Require subscriptions or payments
- Too complex for personal household use
- Cannot run locally on a basic laptop
- Overkill for a single user managing a small plant collection

---

## 4. Proposed System

PlantPal is a local-only, free, web-based plant care management platform built with Java Spring Boot
and MySQL that allows a user to:
- Register and log in securely
- Add plants with basic details
- Organize plants by category
- Set watering intervals
- Record when a plant was last watered
- Calculate the next watering date automatically
- Record growth measurements over time
- View a dashboard with plant health and care statistics

---

## 5. Objectives

1. Build a fully functional plant care management system as a final-year Java Full Stack project
2. Demonstrate Java OOP, Spring Boot, REST APIs, JPA, MySQL, authentication, and role-based access
3. Keep the system simple enough for a beginner to understand, explain, and maintain
4. Run entirely on localhost with zero cost
5. Produce clean, professional, portfolio-quality code and UI

---

## 6. Target Users

- Plant enthusiasts managing household plants
- Students building a Java Full Stack portfolio project
- Beginner developers demonstrating Spring Boot and MySQL skills

---

## 7. Scope

### 7.1 In Scope (MVP)

- User registration and login
- Role-based access (USER, ADMIN)
- Plant categories management (Admin)
- Plant CRUD (User)
- Watering interval and schedule
- Watering records history
- Growth records history
- Dashboard statistics
- Search and filter plants
- Plant status management (HEALTHY, NEEDS_ATTENTION, INACTIVE)
- Watering status calculation (WATER_TODAY, WATER_UPCOMING, WATER_OVERDUE)
- Admin panel: user list, category management, statistics

### 7.2 Future Scope

- Local plant photo upload
- PDF report export
- Dark mode toggle
- Watering reminder notes
- Multiple watering interval types (days, weeks)

### 7.3 Explicitly Out of Scope

- AI or ML of any kind
- Plant disease detection
- Computer vision or image recognition
- Smart irrigation or IoT
- Weather API integration
- Email or SMS notifications
- Cloud hosting or deployment
- Payment or subscription
- External plant databases or APIs
- React or complex frontend frameworks
- Microservices or distributed systems

---

## 8. Functional Requirements

### FR-AUTH: Authentication
- FR-AUTH-01: Users must be able to register with name, email, and password
- FR-AUTH-02: Passwords must be securely hashed (BCrypt)
- FR-AUTH-03: Users must be able to log in with email and password
- FR-AUTH-04: Users must be able to log out
- FR-AUTH-05: Authenticated sessions must expire properly
- FR-AUTH-06: Unauthenticated users must not access protected pages

### FR-CAT: Plant Categories
- FR-CAT-01: Admin must be able to add plant categories
- FR-CAT-02: Admin must be able to edit plant categories
- FR-CAT-03: Admin must be able to delete unused plant categories
- FR-CAT-04: Users must be able to view available categories when adding a plant

### FR-PLANT: Plant Management
- FR-PLANT-01: Users must be able to add a plant with name, category, description, and location
- FR-PLANT-02: Users must be able to view all their plants
- FR-PLANT-03: Users must be able to edit plant details
- FR-PLANT-04: Users must be able to delete a plant and its records
- FR-PLANT-05: Users must be able to search plants by name
- FR-PLANT-06: Users must be able to filter plants by category
- FR-PLANT-07: Users must be able to filter plants by status
- FR-PLANT-08: Users must only see and manage their own plants

### FR-CARE: Care Schedules
- FR-CARE-01: Users must be able to set a watering interval in days for each plant
- FR-CARE-02: System must store the last watered date
- FR-CARE-03: System must calculate next watering date as lastWateredDate + wateringIntervalDays
- FR-CARE-04: Users must be able to update the care schedule

### FR-WATER: Watering Records
- FR-WATER-01: Users must be able to record a watering event for a plant
- FR-WATER-02: Recording a watering event must update the last watered date
- FR-WATER-03: System must calculate watering status: WATER_TODAY, WATER_UPCOMING, WATER_OVERDUE
- FR-WATER-04: Users must be able to view the full watering history for a plant

### FR-GROWTH: Growth Records
- FR-GROWTH-01: Users must be able to add a growth record with date, height, notes, optional leaf count
- FR-GROWTH-02: Users must be able to view all growth records for a plant

### FR-DASH: Dashboard
- FR-DASH-01: Dashboard must show total plant count
- FR-DASH-02: Dashboard must show count of healthy plants
- FR-DASH-03: Dashboard must show count of plants needing watering today
- FR-DASH-04: Dashboard must show count of overdue plants
- FR-DASH-05: Dashboard must show recent plants added
- FR-DASH-06: Dashboard must show upcoming care alerts

### FR-ADMIN: Admin Panel
- FR-ADMIN-01: Admin must be able to view list of all registered users
- FR-ADMIN-02: Admin must be able to manage plant categories
- FR-ADMIN-03: Admin must be able to view system statistics

---

## 9. Non-Functional Requirements

- NFR-01: Application must run on a 4 GB RAM laptop
- NFR-02: Application must respond within 3 seconds for standard operations
- NFR-03: Passwords must be stored as BCrypt hashes
- NFR-04: Users must not be able to access other users data
- NFR-05: Code must be beginner-readable with clear naming
- NFR-06: UI must be responsive on desktop and tablet
- NFR-07: Application must run entirely on localhost
- NFR-08: Zero cost to run - no paid APIs or services
- NFR-09: Must run with standard Maven build: mvn spring-boot:run

---

## 10. User Roles

### USER
- Register and login
- Manage own plants (CRUD)
- Set and update care schedules
- Record watering events
- Record growth observations
- View dashboard and plant history
- Search and filter own plants

### ADMIN
- Login (no public admin registration)
- View all users
- Manage plant categories
- View system statistics

---

## 11. Core User Workflow

Register or Login
  -> Dashboard
  -> Add Plant
  -> Set Care Schedule
  -> View Plant
  -> Record Watering
  -> Next Watering calculated automatically
  -> Record Growth
  -> View Plant History
  -> Monitor Upcoming Care via Dashboard

---

## 12. Watering Status Logic

IF nextWateringDate < today  -> status = WATER_OVERDUE
IF nextWateringDate == today -> status = WATER_TODAY
IF nextWateringDate > today  -> status = WATER_UPCOMING

Where: nextWateringDate = lastWateredDate + wateringIntervalDays

---

## 13. Plant Status Values

- HEALTHY: Plant is being cared for on schedule
- NEEDS_ATTENTION: Plant is overdue for watering or flagged manually
- INACTIVE: Plant is no longer being actively tracked

---

## 14. MVP Features Classification

MUST HAVE:
- Authentication (register, login, logout)
- Plant CRUD
- Plant categories (admin managed)
- Care schedule (watering interval)
- Watering records
- Watering status calculation
- Growth records
- Dashboard statistics
- Search by name
- Filter by category and status
- Admin panel (basic)

SHOULD HAVE:
- Input validation (frontend + backend)
- Meaningful error messages
- Empty states on all list pages
- Good responsive layout
- Clean consistent UI

COULD HAVE:
- Local plant image upload
- PDF or CSV export
- Dark mode

OUT OF SCOPE:
- AI, ML, IoT, weather API, email, SMS, cloud, payment, computer vision
