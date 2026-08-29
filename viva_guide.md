# 🌱 PlantPal — Final Viva Quick Revision Guide
### Last-Minute Study & Demo Guide for College Viva | Language: Simple ELI5 Tanglish

---

## 1. PROJECT IN ONE MINUTE (WHAT & WHY)
- **Project Name:** PlantPal — A Personal Plant Care, Watering Schedule and Growth Monitoring Platform.
- **Core Concept (ELI5):** Namma veetla irukkura plants-ku oru digital diary madhiri. Eppo thanni oothunom nu record panna, adutha thadava eppo thanni oothanum nu system-e automatic-aa calculate panni live alerts kudukkum.
- **Problem Solved:** Irregular watering (overwatering / underwatering) thaduthu plants saavadha zero aakuradhu.
- **Tech Stack:** Java 21 + Spring Boot 3 + MySQL 8 + Vanilla JS + Glassmorphism CSS.

---

## 2. REAL-TIME DEMO DATA SUITE (COPY-PASTE READY FOR VIVA)

> 💡 **Quick Copy-Paste Tip:** Examiner munnadi viva demo appo indha tables-la irukura values-ah appadiye UI-la copy-paste pannalaam!

### 2.1 Demo Accounts & Login Credentials
| # | Account Type | Full Name | Email (Username) | Password | Role | Notes / Purpose |
|:---:|---|---|---|---|:---:|---|
| 1 | **Default Admin** | System Administrator | `admin@plantpal.local` | `LiveAdminPassword@2026` | `ADMIN` | Pre-seeded admin account; manages categories, users & stats |
| 2 | **Primary User** | Hariharan | `hari@plantpal.local` | `HariPass@2026` | `USER` | Main student account for plant CRUD & care tracking |
| 3 | **Demo User 2** | Ananya Sharma | `ananya@plantpal.local` | `AnanyaPass@2026` | `USER` | Indoor plant lover account for multi-user demo |
| 4 | **Demo User 3** | Rajesh Kumar | `rajesh@plantpal.local` | `RajeshPass@2026` | `USER` | Terrace herb collection account |
| 5 | **Demo User 4** | Priya Dharshini | `priya@plantpal.local` | `PriyaPass@2026` | `USER` | Balcony succulent user for ownership isolation test |

---

### 2.2 Add New Plant Demo Data (8 Plants)
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

### 2.3 Update Care Schedule Demo Data (Modal Fields)
| # | Plant Reference | Watering Interval (Days) * | Last Watered Date (Optional) | Sunlight Needs (Optional) | Fertilizing Interval (Days, Optional) |
|:---:|---|:---:|:---:|---|:---:|
| 1 | **Money Plant** | `7` | `24-08-2026` | `⛅ Partial Sun` (`PARTIAL_SUN`) | `30` |
| 2 | **Snake Plant** | `14` | `20-08-2026` | `☁️ Shade` (`SHADE`) | `60` |
| 3 | **Aloe Vera** | `10` | `10-08-2026` | `☀️ Full Sun` (`FULL_SUN`) | `45` |
| 4 | **Red Rose** | `3` | `25-08-2026` | `☀️ Full Sun` (`FULL_SUN`) | `15` |
| 5 | **Peace Lily** | `5` | `23-08-2026` | `☁️ Shade` (`SHADE`) | `30` |

---

### 2.4 Record Watering Event Demo Data (Modal Fields)
| # | Plant Reference | Watered Date * | Notes (Optional) |
|:---:|---|:---:|---|
| 1 | **Money Plant** | `26-08-2026` | `Regular watering, cleaned leaves with soft wet cloth` |
| 2 | **Red Rose** | `25-08-2026` | `Morning deep soak; added seaweed liquid fertilizer` |
| 3 | **Peace Lily** | `26-08-2026` | `Watered after noticing gentle leaf droop; perked up in 2 hrs` |
| 4 | **Holy Basil (Tulsi)** | `26-08-2026` | `Daily morning watering; sprayed water on foliage` |
| 5 | **Snake Plant** | `20-08-2026` | `Light soak around rim; soil dried completely before watering` |

---

### 2.5 Record Growth Observation Demo Data (Modal Fields)
| # | Plant Reference | Observation Date * | Height (cm) | Leaf Count | Observation Notes |
|:---:|---|:---:|:---:|:---:|---|
| 1 | **Money Plant** | `26-08-2026` | `32.5` | `14` | `Two new vibrant green vines climbing up moss pole` |
| 2 | **Red Rose** | `26-08-2026` | `48.0` | `22` | `Three fresh crimson buds formed at stem apex` |
| 3 | **Peace Lily** | `25-08-2026` | `28.2` | `9` | `White spathe flower bloom fully open and glossy` |
| 4 | **Holy Basil (Tulsi)** | `26-08-2026` | `21.0` | `35` | `Bushy branching after pinching top shoots; fragrant` |
| 5 | **Snake Plant** | `24-08-2026` | `41.5` | `7` | `New pup offset shoot emerging from base soil` |

---

## 3. HOW TO FILL DEMO DATA (STEP-BY-STEP ELI5)

1. Browser open panni `http://localhost:8080` address-ku ponga.
2. Unga **USER** account-la Login pannunga (e.g. `hari@plantpal.local` / `HariPass@2026`).
3. Top navigation bar-la irukkura **"My Plants"** link-ah click pannunga.
4. Top right corner-la irukura green **"+ Add New Plant"** button click pannunga (modal pop-up aagum).
5. Mela irukura table-la irundhu **Plant 1 (Money Plant)** details edunga.
6. **Plant Name** box-la `Money Plant` nu enter pannunga.
7. **Category** dropdown-la `Herb` nu select pannunga.
8. **Health Status** dropdown-la `🌿 Healthy` nu select pannunga.
9. **Watering Interval (Days)** field-la `7` nu podunga.
10. **Last Watered** date picker-la `24-08-2026` nu select pannunga.
11. Optional fields: Species, Location, and Care Notes-ah copy-paste pannunga.
12. **"Add Plant"** button click pannunga; card create aagi toast success message varum.
13. Adhe maari remaining 7 demo plants-ah yum one by one quick-aa add pannunga.

---

## 4. BEST DEMO ORDER & WHY

**Recommended Entry Order:**
1. Money Plant $\rightarrow$ 2. Snake Plant $\rightarrow$ 3. Aloe Vera $\rightarrow$ 4. Rose $\rightarrow$ 5. Tulsi $\rightarrow$ 6. Fern $\rightarrow$ 7. Cactus $\rightarrow$ 8. Peace Lily

**Why this order is best for Examiner:**
- **Category Variety:** Herb, Succulent, Flowering, Fern, Cactus nu ellame cover aagum.
- **Health Status Mix:** 5 Healthy, 2 Needs Attention, 1 Inactive — Dashboard pie/counters visual-aa colorful-aa maarum.
- **Realistic Watering Spread:** Aloe & Fern overdue alert tharum, Rose & Tulsi upcoming alert tharum.
- **Live Search & Filter Test:** Search bar-la "Rose" nu type panna filter aagum; category dropdown-la "Succulent" select panna 2 plants filter aagum.
- **Growth & Care Demo:** Peace Lily or Money Plant-ku poi Quick Water & Growth Observation (e.g. 32.5 cm, 14 leaves) easy-aa kaattalaam.

---

## 5. WHAT TO DEMONSTRATE IN VIVA (CHECKLIST)

| Step | Module | Examiner-kitta Kaatta Vendiya One-Line Demo Action |
|:---:|---|---|
| 1 | **Registration / Login** | User register panni session login panna dynamic navbar and dashboard-ku redirect aaguradha kaatunga. |
| 2 | **Dashboard** | 4 Health counters, Overdue / Due Today alert boxes, and 7-day upcoming care timeline live-aa kaatunga. |
| 3 | **Add Plant** | "+ Add New Plant" modal open panni category, interval, and last watered date pottu card create aavradha kaatunga. |
| 4 | **Search / Filter** | Search box-la plant name type panni, category and status dropdowns maathi instant live filtering kaatunga. |
| 5 | **Plant Details** | "View Details" click panni plant care schedule, sunlight type, and quick health switcher button kaatunga. |
| 6 | **Care Schedule** | "Edit Care Schedule" modal-la interval days maathuna, next watering date automatic-aa recalculate aaguradha kaatunga. |
| 7 | **Watering Record** | "Record Watering" click panni date & notes submit panna history timeline table-la record add aavradha kaatunga. |
| 8 | **Growth Record** | "Log Growth" click panni plant height (cm) & leaf count enter panni growth tracking log-la sync aavradha kaatunga. |
| 9 | **Profile** | User profile page-la plant count counter, full name updater, and BCrypt password change form kaatunga. |
| 10 | **Admin Panel** | Logout panni Admin account-la login panni Categories CRUD, User Directory, and System Statistics metrics kaatunga. |

---

## 6. VIVA ONE-LINE EXPLANATION (TOP 20 QUESTIONS)

1. **What is PlantPal?**
   PlantPal is a full-stack personal plant care platform that helps household plant owners track watering schedules and growth logs easily.
2. **Why did you choose this project?**
   Plant lovers face irregular watering issue (over/underwatering); idhai solve panna automated scheduling assistant theva pattadhu.
3. **What problem does it solve?**
   Plants eppo water pannanum nu marandhu saavadha thaduthu, automated next watering date calculation and health monitoring tharudhu.
4. **What is the existing system?**
   Manual paper diaries, sticky notes, or basic phone reminders; idhula dynamic date calculation or centralized dashboard irukaadhu.
5. **What is the proposed system?**
   Java Spring Boot + MySQL web platform; `lastWatered + interval = nextWateringDate` formula pottu live overdue and upcoming care alerts tharudhu.
6. **What are the main modules?**
   Authentication, Categories, Plant Management (CRUD), Care Schedules, Watering Records, Growth Records, Dashboard, Profile, and Admin Panel.
7. **What technologies are used?**
   Java 21 LTS, Spring Boot 3.2.5, MySQL 8.x, Spring Data JPA/Hibernate, Spring Security 6, HTML5, Glassmorphic CSS3, and Vanilla JavaScript.
8. **Why Spring Boot?**
   Enterprise standard framework; embedded Tomcat server, auto-configuration, and rapid REST API development-ku best.
9. **Why MySQL?**
   Relational plant data, foreign keys, cascading deletions, and ACID transactions-ah structured-aa store panna use aagudhu.
10. **Why JPA/Hibernate?**
    Object-Relational Mapping (ORM); Java entity objects-ah direct-aa MySQL tables-oda connect panni boilerplate SQL JDBC code-ah thavirkka use aagudhu.
11. **What is REST API?**
    Stateless client-server architecture; JSON data format-la standard HTTP methods (GET, POST, PUT, DELETE, PATCH) moolama communicate pannum.
12. **How does frontend communicate with backend?**
    Browser Vanilla JavaScript-la native `fetch()` API use panni Spring Boot controller endpoints-ah call pannudhu.
13. **How is authentication handled?**
    Spring Security session-based auth; login aana secure HTTP-only `JSESSIONID` cookie generate panni session manage pannudhu.
14. **What is CSRF?**
    Cross-Site Request Forgery; unauthorized site namma active session use panni request anuppama irukka, PlantPal `X-XSRF-TOKEN` header enforce pannudhu.
15. **What is BCrypt?**
    One-way salted cryptographic hashing algorithm; user passwords-ah plain text-aa store pannama high-security hash-aa MySQL-la store pannum.
16. **How is user data isolated?**
    Backend-la logged-in user ID vechu dhaan SQL query run aagum; vera user plant ID access panna secure 404 Not Found (anti-enumeration) tharum.
17. **What is the dashboard used for?**
    Displays total plants, health breakdown, overdue care cards, and upcoming 7-day care schedule timeline.
18. **What is the main advantage?**
    100% free, zero external API dependency, offline support, modern Glassmorphism Dark/Light mode, and 92 unit + 30 UI automated tests verified.
19. **What is one limitation?**
    Plant photo file upload innum implement aagala (future scope); ippo plant category badges and status icons use panrom.
20. **What is future enhancement?**
    Plant photo image upload, WhatsApp/Email reminder alerts, and downloadable PDF plant care summary reports.

---

## 7. FINAL 2-MINUTE VIVA REVISION (MEMORIZE THIS!)

| Key Factor | One-Line Viva Answer |
|---|---|
| **Project** | PlantPal — Personal Plant Care, Watering Schedule & Growth Monitoring Platform |
| **Purpose** | Prevent plant mortality through automated watering schedules and growth tracking |
| **Existing** | Manual diaries & sticky notes (easily lost, zero automated calculation) |
| **Proposed** | Full-stack web application with dynamic `lastWatered + interval` arithmetic |
| **Users** | Two Roles: `USER` (manages own plants and logs) and `ADMIN` (categories and system stats) |
| **Main Modules** | Auth, Categories, Plants, Care Schedule, Watering Logs, Growth Logs, Dashboard, Profile, Admin |
| **Frontend** | Vanilla HTML5, modern CSS3 (Glassmorphism, Light/Dark toggle), Vanilla JS (`fetch()` API) |
| **Backend** | Java 21 LTS, Spring Boot 3.2.5, Spring Data JPA / Hibernate, Spring Security 6 |
| **Database** | MySQL 8.x (6 Relational Tables with Foreign Keys and CASCADE DELETE) |
| **Security** | Session Cookies, BCrypt Password Hashing, CSRF (`X-XSRF-TOKEN`), RBAC, Anti-Enumeration (404) |
| **API** | 27 Authoritative REST Endpoints + 1 Health Check Endpoint (`/api/health`) |
| **Testing** | 92 Maven Backend Tests (100% PASS) + 72 Headless Chrome Browser QC Flows (164 Total Tests, 100% PASS) |
| **Final Result** | Production-ready monolith on port 8080 (Release Tag: `v1.1.1`) |

---
**🌱 All the Best for your Viva Voce! Confident-aa answer pannunga!**

---

## 8. AUTOMATION, DATABASE INSPECTION & DEPLOYMENT VIVA Q&A

### Q21: What is `DB.bat` and why did you create it?
**Answer:** `DB.bat` is a lightweight, one-click database inspector batch script. In college viva demonstrations or rapid debugging, opening MySQL Workbench or phpMyAdmin is slow. `DB.bat` connects to the local MySQL server using environment variables and executes non-destructive summary queries (`COUNT(*)`, table listing, and recent user inspection) in milliseconds with zero risk of data mutation.

### Q22: How is PlantPal containerized and deployed to the cloud (Render)?
**Answer:** PlantPal uses a multi-stage Docker build (`Dockerfile`):
1. **Stage 1 (Build):** `maven:3.9.6-eclipse-temurin-21-alpine` compiles the source code and produces a production Spring Boot executable JAR.
2. **Stage 2 (Runtime):** `eclipse-temurin:21-jre-alpine` provides a minimal, secure ~150MB JRE container running on port 8080.
3. **Render Blueprint (`render.yaml`):** Configures the Docker Web Service on Render with automatic `/api/health` polling and environment variable injection for database credentials.

### Q23: How do you prove your system works end-to-end without manual testing?
**Answer:** PlantPal implements a two-tier testing strategy:
1. **Tier 1 (Backend Regression):** 92 JUnit 5 and MockMvc tests covering controllers, RBAC authorization, and service validation.
2. **Tier 2 (Browser QC Automation):** 72 automated Puppeteer flows in real headless Chrome covering theme switching, authentication, Plant CRUD, dynamic schedule recalculation, Admin stats, CSRF protection, and mobile responsiveness.
