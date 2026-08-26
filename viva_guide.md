# 🌱 PlantPal — Final Viva Quick Revision Guide
### Last-Minute Study & Demo Guide for College Viva | Language: Simple ELI5 Tanglish

---

## 1. PROJECT IN ONE MINUTE (WHAT & WHY)
- **Project Name:** PlantPal — A Personal Plant Care, Watering Schedule and Growth Monitoring Platform.
- **Core Concept (ELI5):** Namma veetla irukkura plants-ku oru digital diary madhiri. Eppo thanni oothunom nu record panna, adutha thadava eppo thanni oothanum nu system-e automatic-aa calculate panni live alerts kudukkum.
- **Problem Solved:** Irregular watering (overwatering / underwatering) thaduthu plants saavadha zero aakuradhu.
- **Tech Stack:** Java 21 + Spring Boot 3 + MySQL 8 + Vanilla JS + Glassmorphism CSS.

---

## 2. DEMO PLANT DATA (FOR VIVA DEMONSTRATION)

> **Note for Student:** Indha 8 sample plants-ah viva demo appo neenga browser UI-la manual-aa add panna use pannunga. Idhu real PlantPal categories and fields-oda 100% match aagum!

### Demo Plant 1 – Money Plant
| Field | Demo Value |
|---|---|
| Plant Name | Money Plant |
| Category | Herb |
| Health Status | HEALTHY |
| Watering Interval (Days) | 7 |
| Last Watered | 24-08-2026 |
| Species / Scientific Name | Epipremnum aureum |
| Location | Living Room Shelf |
| Description / Care Notes | Keep near indirect sunlight and avoid overwatering |

### Demo Plant 2 – Snake Plant
| Field | Demo Value |
|---|---|
| Plant Name | Snake Plant |
| Category | Succulent |
| Health Status | HEALTHY |
| Watering Interval (Days) | 14 |
| Last Watered | 20-08-2026 |
| Species / Scientific Name | Sansevieria trifasciata |
| Location | Bedroom Corner |
| Description / Care Notes | Drought-tolerant air purifier; water only when soil is dry |

### Demo Plant 3 – Aloe Vera
| Field | Demo Value |
|---|---|
| Plant Name | Aloe Vera |
| Category | Succulent |
| Health Status | NEEDS_ATTENTION |
| Watering Interval (Days) | 10 |
| Last Watered | 10-08-2026 |
| Species / Scientific Name | Aloe barbadensis miller |
| Location | Sunny Balcony Sill |
| Description / Care Notes | Lower leaves slightly yellow; reduce watering and check drainage |

### Demo Plant 4 – Red Rose
| Field | Demo Value |
|---|---|
| Plant Name | Red Rose |
| Category | Flowering |
| Health Status | HEALTHY |
| Watering Interval (Days) | 3 |
| Last Watered | 25-08-2026 |
| Species / Scientific Name | Rosa rubiginosa |
| Location | Terrace Garden |
| Description / Care Notes | Needs 5-6 hours direct morning sun; prune faded buds |

### Demo Plant 5 – Tulsi
| Field | Demo Value |
|---|---|
| Plant Name | Holy Basil (Tulsi) |
| Category | Herb |
| Health Status | HEALTHY |
| Watering Interval (Days) | 2 |
| Last Watered | 26-08-2026 |
| Species / Scientific Name | Ocimum tenuiflorum |
| Location | Front Courtyard |
| Description / Care Notes | Sacred medicinal plant; requires daily moisture and warm sunlight |

### Demo Plant 6 – Boston Fern
| Field | Demo Value |
|---|---|
| Plant Name | Boston Fern |
| Category | Fern |
| Health Status | NEEDS_ATTENTION |
| Watering Interval (Days) | 3 |
| Last Watered | 21-08-2026 |
| Species / Scientific Name | Nephrolepis exaltata |
| Location | Bathroom Window Shade |
| Description / Care Notes | Fronds drying due to low humidity; mist leaves with water spray |

### Demo Plant 7 – Golden Barrel Cactus
| Field | Demo Value |
|---|---|
| Plant Name | Golden Barrel Cactus |
| Category | Cactus |
| Health Status | INACTIVE |
| Watering Interval (Days) | 30 |
| Last Watered | 01-08-2026 |
| Species / Scientific Name | Echinocactus grusonii |
| Location | South Facing Ledge |
| Description / Care Notes | Plant is in winter dormancy; no active watering required |

### Demo Plant 8 – Peace Lily
| Field | Demo Value |
|---|---|
| Plant Name | Peace Lily |
| Category | Flowering |
| Health Status | HEALTHY |
| Watering Interval (Days) | 5 |
| Last Watered | 23-08-2026 |
| Species / Scientific Name | Spathiphyllum wallisii |
| Location | Office Study Desk |
| Description / Care Notes | Glossy dark leaves; droops gently when soil needs watering |

---

## 3. HOW TO FILL DEMO DATA (STEP-BY-STEP ELI5)

1. Chrome browser open panni `http://localhost:8080` address-ku ponga.
2. Unga **USER** account-la Login pannunga (register panna id and password).
3. Top navigation bar-la irukkura **"My Plants"** link-ah click pannunga.
4. Top right corner-la irukura green **"+ Add New Plant"** button click pannunga (modal pop-up aagum).
5. Mela irukura demo table-la irundhu **Plant 1 (Money Plant)** details edunga.
6. **Plant Name** box-la `Money Plant` nu enter pannunga.
7. **Category** dropdown-la `Herb` nu select pannunga.
8. **Health Status** dropdown-la `🌿 Healthy` nu select pannunga.
9. **Watering Interval (Days)** field-la `7` nu podunga.
10. **Last Watered** date picker-la `24-08-2026` nu select pannunga.
11. Optional fields: Species, Location, and Care Notes-ah copy-paste pannunga.
12. **"Add Plant"** button click pannunga; card create aagi toast success message varum.
13. Adhe maari remaining 7 demo plants-ah yum one by one quick-aa add pannunga.

> 💡 **Important:** Indha data pure demo/viva testing data dhaan. Idhu source code-la hardcode aagala; live database-la dynamic-aa store aagudhu.

---

## 4. BEST DEMO ORDER & WHY

**Recommended Entry Order:**
1. Money Plant $\rightarrow$ 2. Snake Plant $\rightarrow$ 3. Aloe Vera $\rightarrow$ 4. Rose $\rightarrow$ 5. Tulsi $\rightarrow$ 6. Fern $\rightarrow$ 7. Cactus $\rightarrow$ 8. Peace Lily

**Why this order is best for Examiner:**
- **Category Variety:** Herb, Succulent, Flowering, Fern, Cactus nu ellame cover aagum.
- **Health Status Mix:** 5 Healthy, 2 Needs Attention, 1 Inactive — Dashboard pie/counters visual-aa colorful-aa maarum.
- **Realistic Watering Spread:** Aloe & Fern overdue alert tharum, Rose & Tulsi upcoming alert tharum.
- **Live Search & Filter Test:** Search bar-la "Rose" nu type panna filter aagum; category dropdown-la "Succulent" select panna 2 plants filter aagum.
- **Growth & Care Demo:** Peace Lily or Money Plant-ku poi Quick Water & Growth Observation (e.g. 25 cm, 6 leaves) easy-aa kaattalaam.

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
    Total plant counts, health status breakdown, overdue care alerts, and upcoming 7-day care timeline-ah single glance-la monitor panna.
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
| **API** | 27 Authoritative REST Endpoints + 1 Health Check Endpoint |
| **Testing** | 92 Maven Backend Tests (100% PASS) + 30 Headless Chrome Puppeteer UI Flows (100% PASS) |
| **Final Result** | Production-ready localhost monolith on port 8080 (Release Tag: `v1.1.0`) |

---
**🌱 All the Best for your Viva Voce! Confident-aa answer pannunga!**
