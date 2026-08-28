# 🌱 PlantPal — Student Learning & Viva Preparation Guide
### Complete Reference Manual for College Viva | Language: Simple ELI5 Tanglish

---

## 1. PROJECT IN ONE MINUTE (PROJECT OVERVIEW)

- **What is it?** PlantPal is a full-stack personal plant care and growth monitoring web application.
- **ELI5 Analogy:** Namma veetla valarkura chedi-kodigaluku oru smart digital dairy madhiri. Ovvoru plant-ukum eppo thanni oothunom nu note panni, adutha thadava eppo thanni oothanum nu automatic-aa alert pannum.
- **Why this project?** Veetla irukkura plants saavadharuku mukkiya kaaranam **irregular watering** (marandhu poi underwatering panradhu, illana adhigama oothi overwatering panradhu). Idhai prevent panna PlantPal dynamic date calculation tharudhu.
- **Core Formula:** 
  $$\text{Next Watering Date} = \text{Last Watered Date} + \text{Watering Interval (Days)}$$

---

## 2. SYSTEM ARCHITECTURE & 6 DATABASE TABLES

PlantPal is built as a clean **Monolithic MVC Architecture** with zero cloud cost running 100% on localhost:

$$\text{Browser (Vanilla JS + HTML5)} \longleftrightarrow \text{Spring Boot 3 REST Controllers} \longleftrightarrow \text{Service Layer} \longleftrightarrow \text{Spring Data JPA} \longleftrightarrow \text{MySQL 8}$$

### The 6 Database Tables
1. **`users`**: User login credentials, BCrypt hashed passwords, roles (`USER`, `ADMIN`).
2. **`plant_categories`**: Admin-managed plant categories (e.g. Herb, Succulent, Flowering, Fern, Cactus).
3. **`plants`**: Plant profile (name, species, location, health status, foreign key to user and category).
4. **`care_schedules`**: 1-to-1 link with plant; stores watering interval in days, last watered date, sunlight needs.
5. **`watering_records`**: 1-to-many historical log of watering events with dates and notes.
6. **`growth_records`**: 1-to-many historical log of plant growth observations (height in cm, leaf count, notes).

> 💡 **Cascade Deletion:** Oru plant delete aana, adhoda care schedule, watering records, and growth records ellame database-la automatic-aa clean aagidum (Foreign Key `ON DELETE CASCADE`).

---

## 3. SECURITY & AUTHENTICATION MECHANISMS

- **Session Authentication:** Login aana server memory-la session create aagi, browser-ku secure HTTP-only `JSESSIONID` cookie send aagum.
- **BCrypt Password Hashing:** User passwords plain text-aa store aagadhu. Salted one-way hash moolama encrypt aagi store aagum.
- **Strict CSRF Protection:** Mutating requests (POST, PUT, DELETE, PATCH) execute panna Spring Security `CookieCsrfTokenRepository` moolama `XSRF-TOKEN` cookie generate panni, browser JavaScript `X-XSRF-TOKEN` HTTP header-la anuppanum.
- **Ownership Isolation & Anti-Enumeration:** Oru user innoru user-oda plant ID-ah URL-la potta, system 403 kudukama **404 Not Found** return pannum. Idhanala hacker-ku andha plant ID irukka illayanu kandupidikka mudiyadhu (Anti-Enumeration).

---

## 4. REAL-TIME DEMO DATA SUITE (COPY-PASTE READY FOR VIVA)

> 💡 **Student Tip:** Examiner munnadi viva demo appo indha tables-la irukura real-time sample data-va use panni quick-aa copy-paste pannalaam!

### 4.1 Demo Accounts & Login Credentials
| # | Account Type | Full Name | Email (Username) | Password | Role | Notes / Purpose |
|:---:|---|---|---|---|:---:|---|
| 1 | **Default Admin** | System Administrator | `admin@plantpal.local` | `LiveAdminPassword@2026` | `ADMIN` | Pre-seeded admin account; manages categories, users & stats |
| 2 | **Primary User** | Hariharan | `hari@plantpal.local` | `HariPass@2026` | `USER` | Main student account for plant CRUD & care tracking |
| 3 | **Demo User 2** | Ananya Sharma | `ananya@plantpal.local` | `AnanyaPass@2026` | `USER` | Indoor plant lover account for multi-user demo |
| 4 | **Demo User 3** | Rajesh Kumar | `rajesh@plantpal.local` | `RajeshPass@2026` | `USER` | Terrace herb collection account |
| 5 | **Demo User 4** | Priya Dharshini | `priya@plantpal.local` | `PriyaPass@2026` | `USER` | Balcony succulent user for ownership isolation test |

---

### 4.2 Add New Plant Demo Data (8 Plants)
| Field | Plant 1 (Money Plant) | Plant 2 (Snake Plant) | Plant 3 (Aloe Vera) | Plant 4 (Red Rose) |
|---|---|---|---|---|
| **Plant Name** | Money Plant | Snake Plant | Aloe Vera | Red Rose |
| **Category** | Herb | Succulent | Succulent | Flowering |
| **Health Status** | HEALTHY | HEALTHY | NEEDS_ATTENTION | HEALTHY |
| **Interval (Days)** | 7 | 14 | 10 | 3 |
| **Last Watered** | 24-08-2026 | 20-08-2026 | 10-08-2026 | 25-08-2026 |
| **Species** | Epipremnum aureum | Sansevieria trifasciata | Aloe barbadensis miller | Rosa rubiginosa |
| **Location** | Living Room Shelf | Bedroom Corner | Sunny Balcony Sill | Terrace Garden |
| **Care Notes** | Indirect sunlight | Drought-tolerant air purifier | Lower leaves yellow; check drainage | Needs direct morning sun |

| Field | Plant 5 (Tulsi) | Plant 6 (Boston Fern) | Plant 7 (Cactus) | Plant 8 (Peace Lily) |
|---|---|---|---|---|
| **Plant Name** | Holy Basil (Tulsi) | Boston Fern | Golden Barrel Cactus | Peace Lily |
| **Category** | Herb | Fern | Cactus | Flowering |
| **Health Status** | HEALTHY | NEEDS_ATTENTION | INACTIVE | HEALTHY |
| **Interval (Days)** | 2 | 3 | 30 | 5 |
| **Last Watered** | 26-08-2026 | 21-08-2026 | 01-08-2026 | 23-08-2026 |
| **Species** | Ocimum tenuiflorum | Nephrolepis exaltata | Echinocactus grusonii | Spathiphyllum wallisii |
| **Location** | Front Courtyard | Bathroom Window Shade | South Facing Ledge | Office Study Desk |
| **Care Notes** | Daily moisture & sun | Mist leaves; low humidity | Winter dormancy; no water | Glossy dark leaves; droops when dry |

---

### 4.3 Update Care Schedule Demo Data (Modal Fields)
| # | Plant Reference | Watering Interval (Days) * | Last Watered Date (Optional) | Sunlight Needs (Optional) | Fertilizing Interval (Days, Optional) |
|:---:|---|:---:|:---:|---|:---:|
| 1 | **Money Plant** | `7` | `24-08-2026` | `⛅ Partial Sun` (`PARTIAL_SUN`) | `30` |
| 2 | **Snake Plant** | `14` | `20-08-2026` | `☁️ Shade` (`SHADE`) | `60` |
| 3 | **Aloe Vera** | `10` | `10-08-2026` | `☀️ Full Sun` (`FULL_SUN`) | `45` |
| 4 | **Red Rose** | `3` | `25-08-2026` | `☀️ Full Sun` (`FULL_SUN`) | `15` |
| 5 | **Peace Lily** | `5` | `23-08-2026` | `☁️ Shade` (`SHADE`) | `30` |

---

### 4.4 Record Watering Event Demo Data (Modal Fields)
| # | Plant Reference | Watered Date * | Notes (Optional) |
|:---:|---|:---:|---|
| 1 | **Money Plant** | `26-08-2026` | `Regular watering, cleaned leaves with soft wet cloth` |
| 2 | **Red Rose** | `25-08-2026` | `Morning deep soak; added seaweed liquid fertilizer` |
| 3 | **Peace Lily** | `26-08-2026` | `Watered after noticing gentle leaf droop; perked up in 2 hrs` |
| 4 | **Holy Basil (Tulsi)** | `26-08-2026` | `Daily morning watering; sprayed water on foliage` |
| 5 | **Snake Plant** | `20-08-2026` | `Light soak around rim; soil dried completely before watering` |

---

### 4.5 Record Growth Observation Demo Data (Modal Fields)
| # | Plant Reference | Observation Date * | Height (cm) | Leaf Count | Observation Notes |
|:---:|---|:---:|:---:|:---:|---|
| 1 | **Money Plant** | `26-08-2026` | `32.5` | `14` | `Two new vibrant green vines climbing up moss pole` |
| 2 | **Red Rose** | `26-08-2026` | `48.0` | `22` | `Three fresh crimson buds formed at stem apex` |
| 3 | **Peace Lily** | `25-08-2026` | `28.2` | `9` | `White spathe flower bloom fully open and glossy` |
| 4 | **Holy Basil (Tulsi)** | `26-08-2026` | `21.0` | `35` | `Bushy branching after pinching top shoots; fragrant` |
| 5 | **Snake Plant** | `24-08-2026` | `41.5` | `7` | `New pup offset shoot emerging from base soil` |

---

## 5. HOW TO FILL DEMO DATA (STEP-BY-STEP)

1. Browser open panni `http://localhost:8080` URL-ku ponga.
2. Unga registered **USER** account details kuduthu Login pannunga (`hari@plantpal.local` / `HariPass@2026`).
3. Top navigation bar-la irukkura **"My Plants"** click pannunga.
4. Top right corner-la irukura green **"+ Add New Plant"** button click pannunga (modal form open aagum).
5. Mela irukura table-la irundhu **Plant 1 (Money Plant)** details edunga.
6. **Plant Name** field-la `Money Plant` nu enter pannunga.
7. **Category** dropdown-la `Herb` select pannunga.
8. **Health Status** dropdown-la `🌿 Healthy` select pannunga.
9. **Watering Interval (Days)** field-la `7` nu enter pannunga.
10. **Last Watered** date picker-la `24-08-2026` select pannunga.
11. Optional fields: Species, Location, and Care Notes-ah fill pannunga.
12. **"Add Plant"** button click pannunga; plant card instant-aa catalog-la add aagidum.
13. Adhe maari remaining demo plants-ah yum one by one add pannunga.

---

## 6. BEST DEMO ORDER & EXAMINER PRESENTATION

**Recommended Order:**
1. Money Plant $\rightarrow$ 2. Snake Plant $\rightarrow$ 3. Aloe Vera $\rightarrow$ 4. Rose $\rightarrow$ 5. Tulsi $\rightarrow$ 6. Fern $\rightarrow$ 7. Cactus $\rightarrow$ 8. Peace Lily

**Why this order is best:**
- **Category Variety:** Herb, Succulent, Flowering, Fern, Cactus nu multiple categories test aagum.
- **Health Statuses:** 5 Healthy, 2 Needs Attention, 1 Inactive — Dashboard stats cards colorful-aa differ aagi visually super-aa theriyum.
- **Watering Alerts:** Aloe & Fern overdue alert tharum; Rose & Tulsi upcoming timeline-la kaatum.
- **Search & Filter:** Search bar-la "Rose" nu type panna filter aagum; Category dropdown-la "Succulent" select panna aloe & snake plant filter aagum.
- **Full Tracking Flow:** Peace Lily click panni "Quick Water" and "Log Growth Observation" (e.g. 28.2 cm, 9 leaves) easy-aa live-aa demonstrate pannalaam.

---

## 7. WHAT TO DEMONSTRATE IN VIVA (CHECKLIST)

| Step | Feature | One-Line Explanation for Examiner |
|:---:|---|---|
| 1 | **Registration & Login** | User register panni secure session login moolama dashboard-ku redirect aaguradha kaatunga. |
| 2 | **Dashboard Overview** | Total plant counters, health status breakdown, overdue alerts, and 7-day care timeline kaatunga. |
| 3 | **Add New Plant** | "+ Add New Plant" modal open panni category, interval, and date pottu dynamic plant card create aavradha kaatunga. |
| 4 | **Search & Filter** | Live search box-la type panni, category and status dropdowns maathi instant catalog filtering kaatunga. |
| 5 | **Plant Detail Page** | Plant card-la "View Details" click panni metadata, sunlight requirements, and quick health switcher kaatunga. |
| 6 | **Care Schedule Editor** | Care schedule modal-la interval days update panni next watering date instant-aa recalculate aaguradha kaatunga. |
| 7 | **Watering History** | "Record Watering" modal moolama new watering log add panni timeline table-la record sync aavradha kaatunga. |
| 8 | **Growth Tracking** | "Log Growth" modal moolama plant height (cm) & leaf count enter panni observation history kaatunga. |
| 9 | **Profile & Security** | User profile page-la plant count counter, full name update, and BCrypt password change security form kaatunga. |
| 10 | **Admin Panel** | Admin (`admin@plantpal.local`) login panni Categories CRUD, registered User directory, and System stats kaatunga. |

---

## 8. VIVA ONE-LINE EXPLANATION (TOP 20 Q&A)

1. **What is PlantPal?**
   Plant care platform that tracks watering schedules and growth history to keep household plants healthy.
2. **Why did you choose this project?**
   People often forget to water plants on time; automated care schedule prevents plant mortality.
3. **What problem does it solve?**
   Solves irregular watering by computing exact next watering dates and providing overdue alerts.
4. **What is the existing system?**
   Manual paper diaries and sticky notes without dynamic calculation or centralized dashboard.
5. **What is the proposed system?**
   Java Spring Boot + MySQL platform with dynamic date arithmetic (`lastWatered + interval = nextWateringDate`).
6. **What are the main modules?**
   Auth, Categories, Plants, Care Schedules, Watering Records, Growth Records, Dashboard, Profile, and Admin Panel.
7. **What technologies are used?**
   Java 21, Spring Boot 3.2.5, MySQL 8, Spring Data JPA, Spring Security 6, HTML5, CSS3 Glassmorphism, and Vanilla JS.
8. **Why Spring Boot?**
   Fast development, auto-configuration, built-in Tomcat server, and enterprise REST API support.
9. **Why MySQL?**
   ACID compliance, relational table schema, foreign key constraints, and cascade delete capabilities.
10. **Why JPA/Hibernate?**
    Object-Relational Mapping (ORM) to interact with MySQL using clean Java entities without raw SQL boilerplate.
11. **What is REST API?**
    Stateless HTTP interface transferring structured JSON data using standard methods (GET, POST, PUT, DELETE, PATCH).
12. **How does frontend communicate with backend?**
    Vanilla JavaScript uses native `fetch()` API calls to consume Spring Boot REST controllers asynchronously.
13. **How is authentication handled?**
    Spring Security session-based auth with secure HTTP-only `JSESSIONID` cookies.
14. **What is CSRF?**
    Cross-Site Request Forgery attack; PlantPal blocks it by enforcing `X-XSRF-TOKEN` headers on all mutating calls.
15. **What is BCrypt?**
    Cryptographic one-way salted password hashing algorithm to safely store passwords in MySQL.
16. **How is user data isolated?**
    Service layer queries by logged-in user ID; cross-user ID access returns secure 404 Not Found (anti-enumeration).
17. **What is the dashboard used for?**
    Displays total plants, health breakdown, overdue care cards, and upcoming 7-day care schedule timeline.
18. **What is the main advantage?**
    100% free, runs offline on localhost, zero external dependencies, Glassmorphic Light/Dark mode, 92 tests passing.
19. **What is one limitation?**
    Multi-part plant image binary upload is not yet included (future scope); currently uses botanical badge icons.
20. **What is future enhancement?**
    Plant photo uploads, WhatsApp/Email notification alerts, and PDF plant care summary reports.

---

## 9. HOW TO RUN & STOP THE PROJECT

- **To Start:** Project root folder-la irukkura **`start.bat`** double click pannunga (environment variables set panni port 8080-la app run aagum).
- **To Access:** Browser-la [**http://localhost:8080**](http://localhost:8080) open pannunga.
- **To Stop:** Root folder-la irukkura **`stop.bat`** double click pannunga (port 8080 process gracefully terminate aagidum).

---

## 10. FINAL 2-MINUTE VIVA REVISION (KEY SUMMARY)

| Topic | Viva Answer Summary |
|---|---|
| **Project** | PlantPal – Personal Plant Care, Watering Schedule & Growth Monitoring Platform |
| **Purpose** | Prevent household plant mortality through automated watering schedules & growth tracking |
| **Existing** | Manual diaries & sticky notes (no auto-calculation, easily lost or forgotten) |
| **Proposed** | Centralized full-stack web platform with dynamic `lastWatered + interval` date arithmetic |
| **Users** | Two Roles: `USER` (manages own plants and logs) and `ADMIN` (manages categories & system stats) |
| **Main Modules** | Auth, Categories, Plants, Care Schedule, Watering Records, Growth Records, Dashboard, Profile, Admin |
| **Frontend** | Vanilla HTML5, modern CSS3 (Glassmorphism, Light/Dark toggle), Vanilla JS (`fetch()` API) |
| **Backend** | Java 21 LTS, Spring Boot 3.2.5, Spring Data JPA / Hibernate, Spring Security 6 |
| **Database** | MySQL 8.x (6 Relational Tables with Foreign Keys and CASCADE DELETE) |
| **Security** | Session Cookies, BCrypt Password Hashing, CSRF (`X-XSRF-TOKEN`), RBAC, Anti-Enumeration (404) |
| **API** | 27 Authoritative REST Endpoints + 1 Health Check Endpoint (`/api/health`) |
| **Testing** | 92 Maven Backend Tests (100% PASS) + 30 Headless Chrome Puppeteer UI Flows (100% PASS) |
| **Final Result** | Production-ready localhost monolith on port 8080 (Release Tag: `v1.1.0`) |

---
**🌱 All the Best for your College Viva Voce! You are fully prepared.**

---

## 5. ONE-CLICK LAUNCHERS & UTILITIES (HOW TO DEMO)

| Script | Purpose | ELI5 Explanation (Tanglish) |
|---|---|---|
| **`start.bat`** | 1-Click Server Launcher | Spring Boot server-ah background window-la start panni, port 8080 health check ready aana udaney browser-la automatic-aa `http://localhost:8080` open pannum. |
| **`stop.bat`** | 1-Click Server Stopper | Port 8080-la run aagura Java backend process-ah safe-aa terminate pannum. |
| **`DB.bat`** | 1-Click Database Inspector | Local MySQL database-la irukkura 6 tables, user count, plant count, and recent records-ah instant-aa terminal-la safe-aa display pannum without running destructive queries. |

### How to use `DB.bat`:
- `.\DB.bat` : Overall table counts and recent registered users display pannum.
- `.\DB.bat tables` : All 6 database tables list pannum.
- `.\DB.bat data` : Active plants and care schedules preview pannum.
