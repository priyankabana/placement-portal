# Placement Portal — Student Placement Management System

[![CI](https://github.com/priyankabana/placement-portal/actions/workflows/ci.yml/badge.svg)](https://github.com/priyankabana/placement-portal/actions/workflows/ci.yml)

> A Spring Boot 3 web portal that replaces a college placement cell's registers: students, recruiters, campus drives, applications with eligibility rules, and placement reports — with role-based sign-in and BCrypt-hashed passwords.

Advanced Java mini-project (**team project**, B.Tech Computer Engineering). Runs out of the box on an in-memory H2 database with one season of sample data.

**Live demo:** https://vu-placement-portal.onrender.com (Render free tier; the exact URL may differ once deployed, and the first request after a quiet period takes about 50 seconds while the service wakes up)  
**Repo:** https://github.com/priyankabana/placement-portal

## Screenshots

| Sign in | Dashboard | Students |
| --- | --- | --- |
| ![Login](docs/screenshots/login.png) | ![Dashboard](docs/screenshots/dashboard.png) | ![Students](docs/screenshots/students.png) |
| **Drive details** | **Applications** | **Reports** |
| ![Drive](docs/screenshots/drive-details.png) | ![Applications](docs/screenshots/applications.png) | ![Reports](docs/screenshots/reports.png) |

> _TODO: re-capture screenshots if the UI changes._

## Features

| Module | What it does |
| --- | --- |
| **Students** | List with search + branch filter, profile with application history, add / edit / delete (Bean Validation on every field) |
| **Companies** | Recruiter master with search; profile lists all drives of that company |
| **Drives** | Announce a drive with eligibility (min CGPA, max backlogs); Upcoming / Open / Closed status; eligible-student list |
| **Applications** | Apply a student to an open drive, move through stages, withdraw, filter by stage. Rules: no duplicate application, eligibility enforced server-side |
| **Reports** | Placement %, highest and average package, department-wise and stage-wise stats, offer list |
| **Sign in** | Two tabs: staff (officer, coordinators, HODs — full rights) and students (own profile, recruiter list, drives, self-apply). Server-side `HandlerInterceptor` guards every URL |
| **JSON API** | `GET /api/drives`, `GET /api/drives?status=OPEN`, `GET /api/stats` (public, read only, aggregate numbers — no student personal data) |
| **Health** | `GET /actuator/health` returns `{"status":"UP"}` (only endpoint exposed by Actuator) |

Security notes: passwords are stored as **BCrypt hashes** (`spring-security-crypto`); the session only holds a small `LoggedInUser` DTO, never the password; wrong user id and wrong password return the same message; students can only apply as themselves (id comes from the session, not the form).

## Architecture

```mermaid
flowchart LR
    B[Browser] -->|HTTP form posts| I[AuthInterceptor<br/>session + role check]
    I --> C[Controllers<br/>Spring MVC + @Valid]
    C --> S[Services<br/>@Transactional business rules<br/>eligibility, duplicates, reports]
    S --> R[Spring Data JPA repositories]
    R --> DB[(H2 in-memory<br/>or MySQL)]
    C --> V[Thymeleaf templates<br/>server-rendered pages]
    S --> E[PasswordEncoder<br/>BCrypt]
    C -.exceptions.-> G[GlobalExceptionHandler]
```

Layering rule: a controller never calls a repository directly, so the officer's screen, the student's own "Apply" button and the JSON API all go through the same checks. `AuthInterceptor` runs before every controller (no session means a redirect to `/login`; a student's URLs are matched against a whitelist, so `/companies` is allowed but `/companies/new` is not). `GlobalExceptionHandler` turns rule failures into a 409 page and unknown ids into a 404 page.

### Data model (6 tables)

| Table | Holds | Key relations |
|---|---|---|
| `department` | branches (CSE, IT, ECE, MECH, CIVIL) | 1 to many students |
| `staff` | officer and faculty sign-in accounts (BCrypt hash) | optional department |
| `company` | recruiters, unique name | 1 to many drives |
| `student` | roll number and email unique, CGPA, backlogs | belongs to a department |
| `drive` | job role, package, `min_cgpa`, `max_backlogs`, date, status | belongs to a company |
| `application` | applied on, stage, remark | junction of student and drive, `UNIQUE(student_id, drive_id)` |

### The placement rules

`ApplicationService.create()` refuses an application unless all of these hold:

1. the drive status is **Open** (Upcoming and Closed drives are refused)
2. student CGPA >= drive `min_cgpa` (the cut-off is inclusive)
3. student backlogs <= drive `max_backlogs`
4. the student has not already applied to that drive (also a unique key in the table)

A student counts as placed when at least one of their applications is `SELECTED`. The placed count uses `COUNT(DISTINCT student)`, so a student with two offers is counted once.

## Tech stack

Java 17 · Spring Boot 3.3 · Spring MVC · Spring Data JPA (Hibernate) · Bean Validation · Thymeleaf · H2 / MySQL · spring-security-crypto (BCrypt) · Spring Boot Actuator · JUnit 5 + MockMvc · Maven (wrapper included) · Docker · GitHub Actions · Render

## Quick start

Prerequisite: JDK 17+ (nothing else — the Maven wrapper downloads Maven, H2 needs no install).

```bash
git clone https://github.com/priyankabana/placement-portal.git
cd placement-portal
./mvnw spring-boot:run          # http://localhost:8080  (H2, sample data)
# or
./run.sh                        # build + run on H2;  ./run.sh 8099 for another port
```

Demo accounts (sample data only — full list in [`docs/login-credentials.txt`](docs/login-credentials.txt)):

| Sign in as | Tab | User id | Password |
| --- | --- | --- | --- |
| Placement Officer | Faculty / Officer | `placement.officer` | `officer@123` |
| CSE HOD | Faculty / Officer | `cse.hod` | `cse@hod123` |
| Student | Student | `CS22001` | `cs22001@2026` |

Student password rule: roll number in lower case + `@` + passing-out year.

On every start the H2 profile loads one sample season: 5 departments, 9 staff accounts, 20 students, 8 companies, 10 drives (4 open) and 24 applications (8 selected). All names, emails (`example.com` / `campus.edu.in`) and phone numbers (`90000xxxxx`) are fictional.

With Docker:

```bash
docker build -t placement-portal .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=h2,prod placement-portal
curl localhost:8080/actuator/health        # {"status":"UP"}
```

### MySQL profile (persistent storage)

```bash
mysql -u root -e "CREATE DATABASE IF NOT EXISTS placement_db;
  CREATE USER IF NOT EXISTS 'placement'@'localhost' IDENTIFIED BY '<your-password>';
  GRANT ALL PRIVILEGES ON placement_db.* TO 'placement'@'localhost';"
cp .env.example .env            # set DB_PASSWORD (and DB_URL / DB_USERNAME if different)
./run-mysql.sh
```

Hibernate creates the tables and `data-mysql.sql` seeds them with `INSERT IGNORE`, so restarts don't duplicate rows. If you have a MySQL database from the pre-BCrypt version, drop and recreate `placement_db` (the password column grew from 40 to 100 chars and the seed passwords are now hashes).

## Environment variables

| Variable | Profile | Default | Purpose |
| --- | --- | --- | --- |
| `DB_URL` | mysql | `jdbc:mysql://localhost:3306/placement_db?...` | JDBC URL |
| `DB_USERNAME` | mysql | `placement` | DB user |
| `DB_PASSWORD` | mysql | — (required) | DB password |
| `H2_CONSOLE_ENABLED` | h2 | `true` | Set `false` on any public deployment (the `prod` profile forces it off) |
| `PORT` | any | `8080` | HTTP port (`server.port=${PORT:8080}`; Render sets it) |
| `SPRING_PROFILES_ACTIVE` | any | `h2` | `h2` or `mysql`, plus `prod` on a server (e.g. `h2,prod`) |

## Project structure

```
src/main/java/com/college/placement/
  config/        AuthInterceptor, WebConfig, PasswordConfig (BCrypt), form converters
  controller/    Dashboard, Student, Company, Drive, Application, Report, Auth, StudentPortal, Api (JSON)
  service/       business rules (eligibility, duplicates, reports, auth)
  repository/    Spring Data JPA interfaces (derived + JPQL queries)
  model/         JPA entities + enums (DriveStatus, ApplicationStatus, UserRole)
  dto/           form objects, dashboard/report DTOs, LoggedInUser
  exception/     custom exceptions + GlobalExceptionHandler
src/main/resources/
  templates/     Thymeleaf pages (layout fragment, CRUD screens, reports, login, error)
  static/        hand-written CSS + small JS
  application*.properties (h2, mysql, prod), data.sql (H2), data-mysql.sql (MySQL)
src/test/java/   JUnit 5 + @SpringBootTest + MockMvc
Dockerfile, .dockerignore, render.yaml, DEPLOY.md   container build and Render blueprint
.github/workflows/ci.yml                         tests + Docker build on every push / PR
docs/            screenshots, presentation, demo credentials
DOCUMENTATION.md full write-up: architecture, DB design & normalisation, roles, test list
```

## Testing

```bash
./mvnw test        # 16 tests, all passing
```

| Test class | Covers |
| --- | --- |
| `StudentCrudTest` (2) | create → read → update → delete, 404 after delete, duplicate roll number refused |
| `ApplicationRuleTest` (7) | duplicate application, low CGPA, too many backlogs, closed and upcoming drives, inclusive cut-off, selection adds to the placed count, eligible list matches the rules |
| `AuthServiceTest` (2) | seeded accounts sign in only with the right password; new students get a BCrypt-hashed default password |
| `WebLayerTest` (5, MockMvc) | health check, `/api/stats` numbers, `/api/drives` with filter and a 400 for a bad status, sign-in redirects, student blocked from officer pages, staff sign-in |

Every test runs in a rolled-back transaction against the sample data. GitHub Actions (`.github/workflows/ci.yml`) runs `./mvnw -B verify` and a `docker build` on every push and pull request.

Measured locally (H2, `prod` profile, 200 sequential requests after warm-up): `/api/drives` p50 2.0 ms, `/api/stats` p50 5.6 ms, dashboard p50 6.3 ms; startup about 5 seconds.

## Deployment

Everything for a free deployment is in the repo:

- `Dockerfile`: two-stage build (Maven, then a JRE 17 image running as a non-root user), heap capped for a 512 MB container, listens on `$PORT`
- `render.yaml`: Render blueprint for a free web service `placement-portal` with `/actuator/health` as the health check and `SPRING_PROFILES_ACTIVE=h2,prod` (self-contained demo, data resets on restart)
- `application-prod.properties`: H2 console off, SQL logging off, cached templates, no stack traces on error pages, HTTP-only session cookie, forwarded headers trusted

On Render: **New > Blueprint**, pick `priyankabana/placement-portal`, apply. Expected URL https://vu-placement-portal.onrender.com (Render adds a suffix if the name is taken). Full steps, the MySQL switch (`mysql,prod` + `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) and other Docker hosts are in [DEPLOY.md](DEPLOY.md). Not suitable for Vercel (server-rendered Java app).

Before a real (non-demo) deployment: rotate/remove the demo accounts and consider the full Spring Security filter chain (CSRF tokens, login throttling).

## Team

Placement Portal is a college team project. Contributors include **Priyanka Bana** and **Krish Oswal**; module ownership is described in `DOCUMENTATION.md` §5.2.

**My contributions** _(TODO — Priyanka to fill in)_:

- [ ] TODO: modules / layers I owned (e.g. entities & repositories, services, controllers, Thymeleaf UI)
- [ ] TODO: business rules or reports I implemented
- [ ] TODO: tests / documentation / presentation work
- [x] Post-submission hardening: BCrypt password hashing, env-based DB credentials, Maven wrapper, Dockerfile
- [x] Deployment readiness: Render blueprint, Actuator health check, prod profile, GitHub Actions CI, MockMvc tests

## What I learned

- Layered Spring Boot design (controller → service → repository) and why business rules belong in `@Transactional` services.
- JPA entity modelling, relationships with cascade/orphan removal, derived queries vs JPQL, and the `open-in-view` trade-off.
- Server-side validation with Bean Validation and a global exception handler that maps domain errors to friendly pages.
- Session-based authentication with an interceptor, role checks, and storing passwords as BCrypt hashes instead of plain text.
- Profile-based configuration (H2 for zero-setup demos, MySQL for persistence) and writing idempotent seed data.
- Integration testing with `@SpringBootTest` + `@Transactional` rollbacks.

## License

MIT © 2026 Priyanka Bana — see [LICENSE](LICENSE).
