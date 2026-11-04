# Student Placement Management System - Project Documentation

Mini project for the Advanced Java / Spring Boot course.
Everything the placement cell of a college keeps on paper - student records, visiting
companies, campus drives and who finally got selected - is stored in one web application
with proper CRUD screens and reports.

- **Language / platform:** Java 17, Spring Boot 3.3.4, Maven
- **Frontend:** Thymeleaf server side templates + hand written CSS (no framework)
- **Persistence:** Spring Data JPA (Hibernate ORM). Two Spring profiles - `h2` (in memory, default) and `mysql` (permanent). The Java code is identical for both, only the profile file changes.
- **Process model followed:** Waterfall (requirement -> design -> implementation -> testing -> deployment -> maintenance)
- **Tables:** 6 (department, staff, company, student, drive, application)
- **Sign in:** two tabs - Faculty / Officer and Student - two access levels (STAFF, STUDENT), nine staff accounts

---

## 1. How to run

```bash
cd student-placement-system

./run.sh                # H2 in memory (default) - nothing to install
./run-mysql.sh          # MySQL - needs the server running

# the same thing without the scripts
mvn spring-boot:run
mvn clean package && java -jar target/placement-portal.jar
java -jar target/placement-portal.jar --spring.profiles.active=mysql
```

### Switching the database

| | `h2` profile (default) | `mysql` profile |
|---|---|---|
| Where the data lives | RAM, gone when the app stops | `placement_db` on the MySQL server |
| Setup needed | none | `brew services start mysql` + create the database and user once |
| Seed file | `data.sql` (plain INSERT) | `data-mysql.sql` (INSERT IGNORE, so a restart does not duplicate) |
| Console | `/h2-console` | `mysql -u placement -p placement_db` |
| Switch by | `spring.profiles.active=h2` | `--spring.profiles.active=mysql` |

Only the three properties files differ - not a single Java class or query had to change,
because JPQL is written against the entities and Hibernate generates the SQL for whichever
database is connected. That is the main advantage of using JPA over plain JDBC.

Then open **http://localhost:8080**

If port 8080 is busy on your machine, start it on another port:
`java -jar target/placement-portal.jar --server.port=8099`

The tables are created by Hibernate at start up and `data.sql` fills them with sample data
(20 students, 8 companies, 10 drives, 24 applications). Live tables can be browsed at
**http://localhost:8080/h2-console** (JDBC URL `jdbc:h2:mem:placementdb`, user `sa`, no password).

Tests: `./mvnw test` - 7 tests covering CRUD, the placement rules and BCrypt sign in.

---

## 2. Pages in the portal

| Page | URL | What it does |
|---|---|---|
| Sign in | `/login` | Entry point - every other URL redirects here without a session |
| Dashboard | `/` | Six summary tiles, latest applications, branch wise bars, drives lined up |
| Students | `/students` | List with search by name/roll number and a branch filter |
| Student profile | `/students/{id}` | Details + full application history of that student |
| Student form | `/students/new`, `/students/{id}/edit` | Register or edit a student |
| Companies | `/companies` | Recruiter list with search on name or industry |
| Company profile | `/companies/{id}` | HR details + every drive of that company |
| Drives | `/drives` | All drives, one click status switch (Upcoming / Open / Closed) |
| Drive details | `/drives/{id}` | Eligibility, applicants received, **eligible students who have not applied** |
| Applications | `/applications` | Filter by stage, change stage and remark inline, withdraw |
| New application | `/applications/new` | Apply a student to an open drive (rules checked here) |
| Reports | `/reports` | Placement %, highest/average package, stage wise bars, confirmed offers |
| Sign in | `/login` | Two tabs - Faculty / Officer and Student |
| My profile | `/my-profile` | Student's own page - details, applications, and open drives with an Apply button |

---

## 3. Folder structure

```
student-placement-system/
├── pom.xml                                  Maven build file, Spring Boot parent + 6 dependencies
├── README.md                                short run instructions
├── DOCUMENTATION.md                         this file
├── docs/
│   ├── login-credentials.txt                every user id and password
│   ├── presentation.html                    source of the presentation
│   ├── Placement_System_Presentation.pdf    PDF to submit / present
│   └── screenshots/                         7 screenshots of the running portal
└── src
    ├── main
    │   ├── java/com/college/placement/
    │   │   ├── PlacementPortalApplication.java   main() - starts embedded Tomcat
    │   │   │
    │   │   ├── model/                            @Entity classes = the 5 tables
    │   │   │   ├── Department.java               branch master (CSE, IT, ...)
    │   │   │   ├── Student.java                  student record, FK -> department
    │   │   │   ├── Company.java                  recruiter master
    │   │   │   ├── Drive.java                    job opening, FK -> company
    │   │   │   ├── Application.java              junction table (student + drive)
    │   │   │   ├── Staff.java                    officer / faculty login accounts
    │   │   │   ├── UserRole.java                 enum ADMIN / FACULTY / STUDENT
    │   │   │   ├── DriveStatus.java              enum UPCOMING / OPEN / CLOSED
    │   │   │   └── ApplicationStatus.java        enum APPLIED / SHORTLISTED / SELECTED / REJECTED
    │   │   │
    │   │   ├── repository/                       Spring Data JPA interfaces (no SQL written by hand)
    │   │   │   ├── DepartmentRepository.java
    │   │   │   ├── StaffRepository.java          findByUsernameIgnoreCase for the login
    │   │   │   ├── StudentRepository.java        search(), findEligibleStudents()
    │   │   │   ├── CompanyRepository.java        derived query on name / industry
    │   │   │   ├── DriveRepository.java          join fetch + upcoming drives
    │   │   │   └── ApplicationRepository.java    offers join, placed counts, max/avg package
    │   │   │
    │   │   ├── service/                          business rules + @Transactional boundaries
    │   │   │   ├── AuthService.java               staff and student sign in, default password
    │   │   │   ├── DepartmentService.java
    │   │   │   ├── StudentService.java           duplicate roll/email checks, safe update
    │   │   │   ├── CompanyService.java
    │   │   │   ├── DriveService.java             status change, eligible student list
    │   │   │   ├── ApplicationService.java       eligibility + duplicate application rules
    │   │   │   └── ReportService.java            dashboard and report numbers
    │   │   │
    │   │   ├── controller/                       @Controller - request handling only
    │   │   │   ├── AuthController.java           /login and /logout
    │   │   │   ├── StudentPortalController.java  /my-profile (student's own page)
    │   │   │   ├── DashboardController.java      / (dashboard)
    │   │   │   ├── StudentController.java        full CRUD
    │   │   │   ├── CompanyController.java        full CRUD
    │   │   │   ├── DriveController.java          CRUD + status switch
    │   │   │   ├── ApplicationController.java    create, stage update, withdraw, filter
    │   │   │   └── ReportController.java         read only reports
    │   │   │
    │   │   ├── dto/
    │   │   │   ├── LoggedInUser.java             what is kept in the session after sign in
    │   │   │   ├── DashboardStats.java           record - six calculated tiles
    │   │   │   ├── DepartmentPlacementStat.java  record - one row of the branch report
    │   │   │   └── ApplicationForm.java          form backing object of the apply screen
    │   │   │
    │   │   ├── config/
    │   │   │   ├── AuthInterceptor.java               who may open which page
    │   │   │   ├── WebConfig.java                     registers the interceptor
    │   │   │   ├── PasswordConfig.java                BCrypt PasswordEncoder bean
    │   │   │   ├── StringToDepartmentConverter.java   dropdown id -> Department object
    │   │   │   └── StringToCompanyConverter.java      dropdown id -> Company object
    │   │   │
    │   │   └── exception/
    │   │       ├── BusinessRuleException.java     parent of the rule failures
    │   │       ├── DuplicateRecordException.java  repeated roll number / company / application
    │   │       ├── EligibilityNotMetException.java CGPA, backlog or closed drive failure
    │   │       ├── ResourceNotFoundException.java  bad id in the URL
    │   │       └── GlobalExceptionHandler.java     @ControllerAdvice -> error page
    │   │
    │   └── resources/
    │       ├── application.properties        common settings + which profile is active
    │       ├── application-h2.properties     H2 in memory datasource (default profile)
    │       ├── application-mysql.properties  MySQL datasource
    │       ├── data.sql                      sample data for H2
    │       ├── data-mysql.sql                same data with INSERT IGNORE for MySQL
    │       ├── static/
    │       │   ├── css/app.css               complete stylesheet, written by hand
    │       │   └── js/app.js                 delete confirmation, auto submit filters, flash fade
    │       └── templates/                    Thymeleaf pages
    │           ├── fragments/layout.html     head, sidebar, alert box, footer (reused everywhere)
    │           ├── login.html                sign in page with the two tabs
    │           ├── index.html                dashboard
    │           ├── reports.html              reports
    │           ├── portal/my-profile.html    the only page a student can open
    │           ├── error.html                error / 404 page
    │           ├── students/    list.html, form.html, view.html
    │           ├── companies/   list.html, form.html, view.html
    │           ├── drives/      list.html, form.html, view.html
    │           └── applications/list.html, form.html
    └── test/java/com/college/placement/
        ├── StudentCrudTest.java              create -> update -> delete + duplicate roll number
        ├── ApplicationRuleTest.java          duplicate application, low CGPA, placed count
        └── AuthServiceTest.java              BCrypt sign in + hashed default password
```

Total: 45 Java classes (plus 3 test classes), 17 Thymeleaf templates, 6 tables.

---

## 4. Architecture / system design

### 4.1 Layered (MVC) architecture

```
                    ┌──────────────────────────────────────────┐
                    │   BROWSER  (student placement officer)   │
                    └──────────────────┬───────────────────────┘
                                       │  HTTP request  (GET / POST form)
                                       ▼
   ┌───────────────────────────────────────────────────────────────────────┐
   │  PRESENTATION LAYER                                                   │
   │  Thymeleaf templates  +  static CSS / JS                              │
   │  templates/students/form.html, index.html, reports.html ...           │
   └──────────────────┬────────────────────────────────▲───────────────────┘
                      │ model attributes              │ rendered HTML
                      ▼                               │
   ┌───────────────────────────────────────────────────────────────────────┐
   │  CONTROLLER LAYER   (@Controller)                                     │
   │  StudentController, CompanyController, DriveController,               │
   │  ApplicationController, ReportController, DashboardController         │
   │  - reads the form, runs @Valid, picks the next page                   │
   └──────────────────┬────────────────────────────────────────────────────┘
                      │ calls one service method
                      ▼
   ┌───────────────────────────────────────────────────────────────────────┐
   │  SERVICE LAYER   (@Service, @Transactional)                           │
   │  StudentService, CompanyService, DriveService,                        │
   │  ApplicationService, ReportService, DepartmentService                 │
   │  - college rules: duplicate check, eligibility, stage change          │
   └──────────────────┬────────────────────────────────────────────────────┘
                      │ repository method
                      ▼
   ┌───────────────────────────────────────────────────────────────────────┐
   │  REPOSITORY LAYER   (Spring Data JPA interfaces)                      │
   │  StudentRepository, DriveRepository, ApplicationRepository ...        │
   │  - derived queries + JPQL, Hibernate generates the SQL                │
   └──────────────────┬────────────────────────────────────────────────────┘
                      │ JDBC
                      ▼
   ┌───────────────────────────────────────────────────────────────────────┐
   │  DATABASE                                                             │
   │  department · company · student · drive · application                 │
   └───────────────────────────────────────────────────────────────────────┘
```

Rules kept while coding:

- a controller never touches a repository directly, it always goes through a service
- a service never returns HTML and never reads the HTTP request
- entities are the only classes that know about table and column names

### 4.2 Request flow of one real action

*Saving a new student (`POST /students/save`)*

```
1. Browser posts the form
2. StudentController.saveStudent()
      - StringToDepartmentConverter turns "1" into a Department object
      - @Valid runs the annotations on Student (@NotBlank, @Email, @DecimalMax ...)
      - errors ? -> return students/form with the messages
3. StudentService.create()
      - checkRollNumberIsFree()  -> DuplicateRecordException if taken
      - checkEmailIsFree()
      - studentRepository.save()
4. Hibernate fires: insert into student (...) values (...)
5. Controller adds a flash message and redirects to /students/{id}
      (redirect after post, so a page refresh does not insert twice)
```

### 4.3 Waterfall model

The project was built phase by phase, each phase finished before the next one started.

```
   ┌──────────────────────────┐
   │ 1. REQUIREMENT GATHERING │  what the placement cell records today
   └────────────┬─────────────┘
                ▼
        ┌──────────────────────────┐
        │ 2. SYSTEM DESIGN         │  ER design in 3NF, layers, page flow
        └────────────┬─────────────┘
                     ▼
             ┌──────────────────────────┐
             │ 3. IMPLEMENTATION        │  entities -> repositories -> services
             └────────────┬─────────────┘     -> controllers -> templates
                          ▼
                  ┌──────────────────────────┐
                  │ 4. TESTING               │  JUnit + manual test of every screen
                  └────────────┬─────────────┘
                               ▼
                       ┌──────────────────────────┐
                       │ 5. DEPLOYMENT            │  single runnable jar, embedded Tomcat
                       └────────────┬─────────────┘
                                    ▼
                            ┌──────────────────────────┐
                            │ 6. MAINTENANCE           │  data.sql, docs, future scope
                            └──────────────────────────┘
```

| Phase | Deliverable of this project |
|---|---|
| Requirement | Module list, actors (placement officer / coordinator), 12 screens, report list |
| Design | 5 table ER design, layered package structure, URL plan, wireframe of the pages |
| Implementation | 44 Java classes and 17 templates, module by module (student -> company -> drive -> application -> reports) |
| Testing | 7 automated tests, plus every CRUD path checked in the browser |
| Deployment | `mvn clean package` -> `target/placement-portal.jar` -> `java -jar` |
| Maintenance | Sample data, documentation, screenshots, list of future enhancements |

Waterfall was chosen because the requirement of a placement cell is fixed and well known
before coding starts, and every phase has one clear output that the next phase uses.

---

## 5. Roles and responsibilities

### 5.1 Roles inside the application

| Role | Signs in with | Can do |
|---|---|---|
| `STAFF` - placement officer, coordinators, HODs, principal | staff user id (`placement.officer`, `cse.hod`, `placement.coord` ...) | Everything - add / edit / delete students, recruiters, drives and applications, open or close a drive, move an application through the stages, read every report |
| `STUDENT` | roll number (`CS22001`) | Own profile with the application history, the recruiter list, the drive list, and **applying to an open drive** for themselves |

Student self apply goes through the same `ApplicationService.create()` that the officer's
screen uses, so the drive status, the CGPA cut off, the backlog limit and the duplicate
check are applied to a student's own application as well - nothing is trusted from the browser.

Nine staff accounts are seeded, one for every person involved in a real placement
cell - Training and Placement Officer, Placement Coordinator, Training Coordinator,
the HOD of each of the five branches, and the Principal. They share the `STAFF`
access level because all of them work on the same records; the designation column
of the `staff` table is what tells them apart on screen.

The rule is written once in `config/AuthInterceptor.java`, which runs before every
controller, so a new page is protected automatically:

```java
if (user.isStaff()) return true;                      // officer, coordinator, HOD
return "POST".equals(method)                          // student
        ? STUDENT_ACTIONS.matcher(path).matches()     // only /my-applications/apply
        : STUDENT_PAGES.matcher(path).matches();      // own page, companies, drives
```

A student's URLs are matched with a **pattern**, not with "starts with" - otherwise
`/companies` would also let `/companies/new` and `/companies/save` through and a student
could add records. Buttons are hidden in the pages with `th:if="${session.user.staff}"`,
so a student never sees an Edit or Delete button in the first place. Even if the URL is typed by
hand, the interceptor stops the request - the check is on the server, not in the browser.

### Login and passwords

| Account type | User id | Password |
|---|---|---|
| Officer / faculty | `username` column of the `staff` table | `password` column of the same row |
| Student | roll number | roll number in small letters + `@` + passing out year, e.g. `cs22001@2026` |

- The two tabs of the login page post a `loginType`, and `AuthService` checks only the
  matching table - a student roll number typed on the faculty tab is simply "invalid".
- A wrong user id and a wrong password give the **same** message, so nobody can find out
  which user ids exist.
- A student registered later from the portal gets the same kind of password automatically
  (`StudentService.create()` calls `AuthService.defaultPasswordFor()`).
- The complete list is in **`docs/login-credentials.txt`**.
- Passwords are stored as **BCrypt hashes** (`spring-security-crypto`, `PasswordConfig`).
  `AuthService` checks them with `PasswordEncoder.matches`, `StudentService.create()` hashes
  the generated default password, and the seed files contain only hashes. The plain demo
  passwords live in `docs/login-credentials.txt`.

### 5.2 Module ownership in the team

| Member | Responsibility | Files owned |
|---|---|---|
| Member 1 | Database design, entities and repositories | `model/*`, `repository/*`, `data.sql` |
| Member 2 | Service layer and business rules, JUnit tests | `service/*`, `exception/*`, `src/test/*` |
| Member 3 | Controllers, form binding and validation | `controller/*`, `dto/*`, `config/*` |
| Member 4 | Thymeleaf pages, CSS, documentation and presentation | `templates/*`, `static/*`, `docs/*` |

Integration was done after every phase so that no member waited for another to finish.

---

## 6. Database description

### 6.1 ER diagram

```
   ┌────────────────────┐                     ┌────────────────────┐
   │     DEPARTMENT     │                     │      COMPANY       │
   ├────────────────────┤                     ├────────────────────┤
   │ PK id              │                     │ PK id              │
   │    name            │                     │    name  (unique)  │
   │    code  (unique)  │                     │    industry, city  │
   │    hod_name        │                     │    website         │
   └─────────┬──────────┘                     │    hr_name         │
             │ 1                              │    hr_email        │
             │                                └─────────┬──────────┘
             │                                          │ 1
             │ N                                        │ N
   ┌─────────▼──────────┐                     ┌─────────▼──────────┐
   │      STUDENT       │                     │       DRIVE        │
   ├────────────────────┤                     ├────────────────────┤
   │ PK id              │                     │ PK id              │
   │    roll_number (U) │                     │ FK company_id      │
   │    first_name      │                     │    job_role        │
   │    last_name       │                     │    package_lpa     │
   │    email (U)       │                     │    min_cgpa        │
   │    phone           │                     │    max_backlogs    │
   │    cgpa            │                     │    drive_date      │
   │    batch_year      │                     │    location        │
   │    backlogs        │                     │    description     │
   │    resume_url      │                     │    status          │
   │ FK department_id   │                     └─────────┬──────────┘
   └─────────┬──────────┘                               │ 1
             │ 1                                        │
             │                ┌────────────────────┐    │
             │       N        │    APPLICATION     │    │  N
             └───────────────►├────────────────────┤◄───┘
                              │ PK id              │
                              │ FK student_id      │
                              │ FK drive_id        │
                              │    applied_on      │
                              │    status          │
                              │    remarks         │
                              │ UNIQUE(student_id, │
                              │        drive_id)   │
                              └────────────────────┘

   Student  N ── 1  Department          (many students in one branch)
   Drive    N ── 1  Company             (one company, many drives)
   Student  M ── N  Drive               resolved by APPLICATION, which also
                                        stores applied_on, status and remarks
```

### 6.2 Table description

**department** - master list of branches

| Column | Type | Constraint |
|---|---|---|
| id | BIGINT | PK, identity |
| name | VARCHAR(60) | NOT NULL |
| code | VARCHAR(10) | NOT NULL, UNIQUE |
| hod_name | VARCHAR(60) | |

**staff** - login accounts of the college side users

| Column | Type | Constraint |
|---|---|---|
| id | BIGINT | PK, identity |
| name | VARCHAR(60) | NOT NULL |
| username | VARCHAR(30) | NOT NULL, UNIQUE |
| password | VARCHAR(100) | NOT NULL - BCrypt hash |
| designation | VARCHAR(60) | |
| email | VARCHAR(80) | |
| role | VARCHAR(10) | NOT NULL - STAFF / STUDENT (staff rows are always STAFF) |
| department_id | BIGINT | FK -> department(id), NULL for the officer |

**company** - recruiters that visit the campus

| Column | Type | Constraint |
|---|---|---|
| id | BIGINT | PK, identity |
| name | VARCHAR(80) | NOT NULL, UNIQUE |
| industry | VARCHAR(50) | NOT NULL |
| city | VARCHAR(40) | |
| website | VARCHAR(100) | |
| hr_name | VARCHAR(60) | NOT NULL |
| hr_email | VARCHAR(80) | NOT NULL |

**student** - one row per registered student

| Column | Type | Constraint |
|---|---|---|
| id | BIGINT | PK, identity |
| roll_number | VARCHAR(15) | NOT NULL, UNIQUE |
| first_name / last_name | VARCHAR(40) | NOT NULL |
| email | VARCHAR(80) | NOT NULL, UNIQUE |
| phone | VARCHAR(10) | 10 digits (checked by @Pattern) |
| cgpa | DOUBLE | NOT NULL, 0 to 10 |
| batch_year | INT | NOT NULL |
| backlogs | INT | NOT NULL, >= 0 |
| resume_url | VARCHAR(150) | |
| department_id | BIGINT | NOT NULL, FK -> department(id) |

**drive** - a job opening announced by a company

| Column | Type | Constraint |
|---|---|---|
| id | BIGINT | PK, identity |
| company_id | BIGINT | NOT NULL, FK -> company(id) |
| job_role | VARCHAR(70) | NOT NULL |
| package_lpa | DOUBLE | NOT NULL |
| min_cgpa | DOUBLE | NOT NULL |
| max_backlogs | INT | NOT NULL |
| drive_date | DATE | NOT NULL |
| location | VARCHAR(60) | |
| description | VARCHAR(500) | |
| status | VARCHAR(15) | NOT NULL - UPCOMING / OPEN / CLOSED |

**application** - junction table between student and drive

| Column | Type | Constraint |
|---|---|---|
| id | BIGINT | PK, identity |
| student_id | BIGINT | NOT NULL, FK -> student(id) |
| drive_id | BIGINT | NOT NULL, FK -> drive(id) |
| applied_on | DATE | NOT NULL |
| status | VARCHAR(15) | NOT NULL - APPLIED / SHORTLISTED / SELECTED / REJECTED |
| remarks | VARCHAR(200) | |
| | | UNIQUE (student_id, drive_id) |

### 6.3 Normalisation

**Unnormalised form.** A single register kept by the placement cell would look like:

```
roll_no, student_name, branch, hod, cgpa, company, hr_name, hr_email,
role, package, drive_date, status, remark
```

**First normal form (1NF)** - every column holds one atomic value and there is no repeating
group. `student_name` was split into `first_name` and `last_name`. A student applying to
three companies is not stored as `company1, company2, company3`; each application becomes
its own row in the `application` table.

**Second normal form (2NF)** - in 1NF the key of the register is (roll_no, company).
`student_name`, `cgpa` and `branch` depend only on `roll_no`, and `hr_name`, `hr_email`
depend only on `company` - these are partial dependencies. They were removed by splitting
the register into `student`, `company` and `drive`, each with its own single column key,
and `application` keeps only the facts that need both keys (`applied_on`, `status`, `remarks`).

**Third normal form (3NF)** - `hod` depended on `branch`, not on the roll number, which is a
transitive dependency (roll_no -> branch -> hod). Branch data was moved into the
`department` table and `student` now stores only `department_id`. In the same way the drive
does not repeat the company address or HR details, it stores `company_id`.

Result of normalisation:

- no repeated text - a company name is stored once, not once per drive
- no update anomaly - changing an HR email is a one row update
- no insert anomaly - a company can be added before it announces any drive
- no delete anomaly - deleting one application does not lose the student or the drive
- controlled redundancy - only the foreign key values repeat

### 6.4 Joins used in the project

**a) Student with its department (inner join, 2 tables)** - used by the student list

```sql
SELECT s.roll_number, s.first_name, s.last_name, s.cgpa, d.code
FROM student s
JOIN department d ON d.id = s.department_id
ORDER BY s.roll_number;
```
JPQL in `StudentRepository.search()`:
```java
select s from Student s join s.department d
where lower(s.rollNumber) like lower(concat('%', :keyword, '%'))
  and (:departmentId is null or d.id = :departmentId)
```

**b) Confirmed offers (3 table join)** - used by the reports page

```sql
SELECT s.roll_number, s.first_name, c.name AS company, dr.job_role, dr.package_lpa
FROM application a
JOIN student s  ON s.id  = a.student_id
JOIN drive   dr ON dr.id = a.drive_id
JOIN company c  ON c.id  = dr.company_id
WHERE a.status = 'SELECTED'
ORDER BY dr.package_lpa DESC;
```
JPQL in `ApplicationRepository.findOffers()`:
```java
select a from Application a
join fetch a.student s
join fetch a.drive d
join fetch d.company c
where a.status = :status
order by d.packageLpa desc
```
`JOIN FETCH` loads all four tables in one select instead of firing a query per row (avoids
the N+1 problem).

**c) Branch wise placement (join + aggregate + group)** - reports page

```sql
SELECT d.code,
       COUNT(DISTINCT s.id) AS total,
       COUNT(DISTINCT CASE WHEN a.status = 'SELECTED' THEN s.id END) AS placed
FROM department d
LEFT JOIN student s     ON s.department_id = d.id
LEFT JOIN application a ON a.student_id = s.id
GROUP BY d.code
ORDER BY d.code;
```
A `LEFT JOIN` is needed here so that a branch with zero placements (ECE in the sample data)
still appears in the report with 0%.

**d) Eligible students (join with a NOT IN sub query)** - drive details page

```sql
SELECT s.* FROM student s
WHERE s.cgpa >= 7.0 AND s.backlogs <= 0
  AND s.id NOT IN (SELECT a.student_id FROM application a WHERE a.drive_id = 3)
ORDER BY s.cgpa DESC;
```

**e) Distinct placed count** - dashboard tile

```sql
SELECT COUNT(DISTINCT a.student_id) FROM application a WHERE a.status = 'SELECTED';
```
`DISTINCT` matters because a student selected by two companies must be counted once.

---

## 7. CRUD summary

| Entity | Create | Read | Update | Delete |
|---|---|---|---|---|
| Student | `POST /students/save` | `/students`, `/students/{id}` | `POST /students/save` with id | `POST /students/{id}/delete` |
| Company | `POST /companies/save` | `/companies`, `/companies/{id}` | `POST /companies/save` with id | `POST /companies/{id}/delete` |
| Drive | `POST /drives/save` | `/drives`, `/drives/{id}` | `POST /drives/save`, `POST /drives/{id}/status` | `POST /drives/{id}/delete` |
| Application | `POST /applications/save` | `/applications`, `/applications?status=` | `POST /applications/{id}/status` | `POST /applications/{id}/delete` |
| Department | seeded in `data.sql` | dropdown + report | - | - |

Deleting a student removes that student's applications, and deleting a company removes its
drives, through `cascade = ALL` with `orphanRemoval = true`. Every delete is a POST with a
JavaScript confirmation, never a plain link, so a crawler or a refresh cannot delete a row.

---

## 8. Validation and business rules

Field level (Bean Validation on the entity, checked by `@Valid` in the controller):

- roll number, names, email, CGPA, batch year and backlogs are compulsory
- email must be a valid address, phone must be exactly 10 digits
- CGPA between 0 and 10, batch year between 2015 and 2035, backlogs not negative
- description of a drive limited to 500 characters, remark to 200

Rule level (service layer, so the rule also holds if the form is bypassed):

1. roll number and email of a student must be unique - `DuplicateRecordException`
2. company name must be unique
3. a drive accepts applications only in the **Open** state - `EligibilityNotMetException`
4. student CGPA >= drive `min_cgpa`
5. student backlogs <= drive `max_backlogs`
6. one student cannot apply twice to the same drive (also a UNIQUE key in the table)
7. an unknown id in the URL gives a proper 404 page - `ResourceNotFoundException`

---

## 9. Coding practices followed

- **Naming:** classes in PascalCase (`ApplicationService`), methods and variables in
  camelCase (`findEligibleStudents`), constants in UPPER_SNAKE (`DISPLAY_DATE`),
  table and column names in snake_case (`roll_number`), packages in lower case.
- **One job per class:** controller = request handling, service = rules, repository = data,
  entity = table mapping, DTO = data carried to the view.
- **Constructor injection** everywhere instead of `@Autowired` on fields, so a class cannot
  be created without its dependencies and can be unit tested easily.
- **No SQL inside a controller** and no HTML inside a service.
- **`@Transactional(readOnly = true)`** on the service class, plain `@Transactional` on the
  write methods, so a failed save rolls back completely.
- **Redirect after POST** with flash messages, which stops duplicate inserts on refresh.
- **Update by copy:** the update methods copy the form fields on to the row loaded from the
  database instead of saving the detached form object, which protects the child rows.
- **Comments** explain *why* a line exists (for example why `DISTINCT` is needed), not what
  the syntax does.
- Consistent URL pattern for every module: `list -> /new -> /{id} -> /{id}/edit -> /save -> /{id}/delete`.

---

## 10. Testing

`src/test/java/com/college/placement/`

| Test | Checks |
|---|---|
| `StudentCrudTest.studentCrudWorks` | insert, read, update (CGPA + branch change), delete, then a 404 for the deleted id |
| `StudentCrudTest.duplicateRollNumberIsRefused` | `DuplicateRecordException` for an existing roll number |
| `ApplicationRuleTest.duplicateApplicationIsRejected` | same student cannot apply twice to one drive |
| `ApplicationRuleTest.lowCgpaApplicationIsRejected` | 6.2 CGPA student refused for a 7.0 cut off drive |
| `ApplicationRuleTest.placedCountMatchesSelectedApplications` | placed count of the report equals the SELECTED rows |
| `AuthServiceTest.seededAccountsUseHashedPasswords` | seeded staff/student accounts sign in with the right password and are refused with a wrong one |
| `AuthServiceTest.newStudentPasswordIsHashed` | a newly registered student's default password is stored as a BCrypt hash and still works |

Result: `Tests run: 7, Failures: 0, Errors: 0`.

Manual test list used during the demo: create/edit/delete on all four modules, search and
filter, status switch of a drive, applying from the eligible list, wrong URL, empty form
submit, duplicate entry, ineligible application.

---

## 11. Screenshots

`docs/screenshots/` - dashboard, students, student-profile, student-form, drive-details,
applications, reports.

---

## 12. Future scope

- Replace the session based login with the full Spring Security filter chain (passwords are already BCrypt hashed)
- Let a student apply on their own instead of the coordinator entering it
- A "change password" screen, and a forced password change on the first sign in
- Resume upload as a file instead of a link
- Email or SMS alert when a drive opens or a result is declared
- Export of the report tables to Excel or PDF
- Add Flyway or Liquibase so the schema itself is versioned instead of relying on
  Hibernate's `ddl-auto=update`
