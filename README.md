# UDIS - University Department Information System

A minimal Java Swing desktop application implementing the UDIS SRS for the
Software Engineering course at IIIT Ranchi. Covers all twelve features
(F-01 to F-12): student profiles, course catalogue, semester registration,
grading with automatic GPA/CGPA, inventory, cash book, research /
publications, student-info query, RBAC, and audit logging.

## Tech Stack

- Java 17, Swing (FlatLaf look-and-feel)
- Maven
- MySQL 8 with plain JDBC
- `jbcrypt` for password hashing

## Prerequisites

1. JDK 17+
2. Maven 3.8+
3. A running MySQL 8 instance on `localhost:3306`
4. `mysqldump` on `PATH` (only for the optional "Backup Database" menu item)

## Setup

1. Ensure MySQL is running. The app uses `createDatabaseIfNotExist=true`, so
   the `udis` database is created automatically on first launch.

2. Edit `src/main/resources/config.properties` to match your MySQL credentials:

   ```
   jdbc.url=jdbc:mysql://localhost:3306/udis?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true
   jdbc.user=root
   jdbc.password=root
   maintenance=false
   ```

3. Build and run:

   ```bash
   mvn clean package
   java -jar target/udis.jar
   ```

   Or directly with Maven:

   ```bash
   mvn compile exec:java -Dexec.mainClass=com.udis.Main
   ```

On first run, schema and demo data are loaded automatically.

## Demo Credentials

| Role      | Username    | Password       | Capability                                         |
|-----------|-------------|----------------|----------------------------------------------------|
| Secretary | `secretary` | `secretary123` | Full write access to all academic + admin modules  |
| HOD       | `hod`       | `hod123`       | Read-only on everything; reports & queries         |
| Faculty   | `faculty`   | `faculty123`   | Read-only on students, courses, grades             |
| Admin     | `admin`     | `admin123`     | Access to the Audit Log module                     |

## Suggested Demo Flow (5 minutes)

1. Log in as `secretary`.
2. **Students** tab - add a new student (e.g. roll `2023UG1099`).
3. **Courses** tab - note the CS101 -> CS102 prerequisite chain in the seed data.
4. **Registration** tab - roll `2023UG1063`, Sem 3 / Year 2024, click
   *Load Profile*. Completed (CS101) and back-log (CS102) are shown. Try
   registering for `CS201` - it will be rejected because CS102 hasn't been
   cleared.
5. **Grades** tab - roll `2023UG1049`, Sem 3 / Year 2024; enter letter grades,
   *Save Grades*, then *Print Grade Sheet* for the HTML transcript.
6. **Finance** tab - running balance is shown at the top. Click
   *Financial Summary...* for date-range totals.
7. **Inventory** tab - use the search box to filter and try *Mark Disposed*.
8. **Student Query** tab - roll `2023UG1049` to see the read-only 360 view.
9. Log out, sign in as `admin` -> **Audit Log** tab shows the activity we
   just generated.

## Module / SRS Feature Mapping

| SRS Feature                          | Implementation                                  |
|--------------------------------------|-------------------------------------------------|
| F-01 Student Profile Management      | `StudentPanel` + `StudentDao`                   |
| F-02 Course Catalogue                | `CoursePanel` + `CourseDao`                     |
| F-03 Semester Registration           | `RegistrationPanel` + `RegistrationService`     |
| F-04 Grade Entry & GPA/CGPA          | `GradePanel` + `GpaService`                     |
| F-05 Grade Sheet Printing            | `JEditorPane.print()` in `GradePanel`           |
| F-06 Inventory Register              | `InventoryPanel` + `InventoryDao`               |
| F-07 Cash Book Management            | `FinancePanel` + `CashBookService`              |
| F-08 Financial Reporting             | "Financial Summary..." dialog in `FinancePanel` |
| F-09 Research & Publications         | `ResearchPanel` with two sub-tabs               |
| F-10 Student Info Query              | `QueryPanel` - read-only 360 view               |
| F-11 Role-Based Access Control       | `AuthService.canWrite / canAccess`              |
| F-12 Audit Logging                   | `AuditService` + `AuditPanel` (admin only)      |

## MVP Simplifications (vs. full SRS)

- Plain JDBC, no ORM.
- `Database.java` single-connection singleton instead of a pool.
- Automated 24h backup replaced with a manual *File > Backup Database...*
  menu item that invokes `mysqldump`.
- Maintenance Mode is the `maintenance=true` flag in `config.properties`;
  when set, only the Admin role may log in.
- Letter-grade scale is fixed in `GpaService` (A=10, A-=9, B+=8.5, ..., F=0)
  rather than configurable per semester.
- Audit log records logins and insert/update/delete events only (no
  field-level diffs).

## Project Phases and Roadmap

Total effort is measured in calendar weeks for a 4-member team working on
the course timeline. Effort estimates are indicative.

### Completed phases

#### Phase 0 - Requirements & Planning (1 week) - DONE

- SRS v1.0 authored and approved.
- Tech-stack decisions (Java 17 + Swing + MySQL + plain JDBC + Maven).
- Schema design (10 tables, SRS Section 3.4).
- MVP scope frozen: all 12 features at a demo-worthy level.

#### Phase 1 - MVP Implementation (2 weeks) - DONE

Current state of the codebase. Deliverables:

- Maven project with fat-jar packaging via `maven-shade-plugin`.
- MySQL schema + seed data auto-loaded on first launch.
- Login with BCrypt-hashed credentials and four seeded roles.
- All twelve SRS features (F-01 to F-12) implemented as independent
  Swing panels behind a single `JTabbedPane`.
- GPA / CGPA computation, prerequisite checking, running cash balance,
  printable HTML grade sheet, searchable inventory, finance date-range
  summary, audit trail.
- FlatLaf look-and-feel, role-gated UI, manual DB backup via
  `mysqldump`, README with demo script.

### Upcoming phases

#### Phase 2 - Hardening & UX Polish (1.5 weeks)

Goal: raise the MVP to "production-ish" quality for the final viva.

- **Date pickers** (JCalendar or equivalent) instead of free-text
  `YYYY-MM-DD` fields - 1 day.
- **Stricter input validation** across all forms with inline error
  hints rather than modal dialogs - 2 days.
- **Connection pooling** with HikariCP, replacing the singleton
  `Connection` in `Database.java` - 1 day.
- **Transactional grade entry**: wrap the multi-row "Save Grades"
  operation in a single JDBC transaction so partial failures roll back
  - 1 day.
- **Unit tests** for `GpaService`, `RegistrationService`, and
  `CashBookService` using JUnit 5 - 2 days.
- **Keyboard shortcuts** on main actions (Ctrl-S save, Ctrl-F search,
  Esc to clear form) - 0.5 day.
- **App icon & splash screen** - 0.5 day.

#### Phase 3 - SRS Compliance Uplift (2 weeks)

Close the remaining gaps between the MVP shortcuts and the letter of
the SRS.

- **Automated 24h backup scheduler** using
  `ScheduledExecutorService` instead of the manual menu item - 1 day.
- **Proper Maintenance Mode** toggled at runtime by Admin (with a
  system-wide banner) rather than a config file flag - 1 day.
- **Configurable grading scale** stored in a `grade_scheme` table so
  the department can adjust letter->point mappings per batch - 2 days.
- **Admin User Management UI** for creating / disabling users and
  resetting passwords - 2 days.
- **Field-level audit diffs** (before/after values) with a richer
  `audit_log` schema - 2 days.
- **PDF grade sheet export** via Apache PDFBox in addition to direct
  printing - 2 days.
- **Report export to CSV/Excel** for Finance and Inventory - 1 day.

#### Phase 4 - Scale-out & Extension (3-4 weeks)

Optional, out of course scope but listed per SRS Section 3.6
("Scalability") guidance.

- **Multi-department support**: add a `department_id` discriminator
  to all core tables and a department-scoped login - 1 week.
- **Migration to Spring Boot + JPA/Hibernate** while keeping the
  current Swing client - 1 week.
- **Web front-end** (React or Thymeleaf) served by the same Spring
  backend, satisfying the SRS Section 2.1 "web-based deployment"
  alternative - 2 weeks.
- **Role-based dashboards** (HOD sees financial KPIs, Faculty sees
  taught-course grade distributions) - 3 days.
- **Email notifications** for grade publication and registration
  confirmation via JavaMail - 2 days.

### Summary timeline

| Phase                           | Status      | Effort       |
|---------------------------------|-------------|--------------|
| 0. Requirements & Planning      | Done        | 1 week       |
| 1. MVP Implementation           | Done        | 2 weeks      |
| 2. Hardening & UX Polish        | Upcoming    | 1.5 weeks    |
| 3. SRS Compliance Uplift        | Upcoming    | 2 weeks      |
| 4. Scale-out & Extension        | Stretch     | 3-4 weeks    |

## Project Layout

```
udis/
|-- pom.xml
|-- README.md
`-- src/main/
    |-- java/com/udis/
    |   |-- Main.java
    |   |-- db/Database.java
    |   |-- model/   (9 POJOs)
    |   |-- dao/     (9 DAOs)
    |   |-- service/ (AuthService, GpaService, RegistrationService,
    |   |             CashBookService, AuditService)
    |   `-- ui/      (LoginFrame, MainFrame + 9 module panels, UiUtils)
    `-- resources/
        |-- schema.sql
        |-- seed.sql
        `-- config.properties
```

## Team

- Aditya Sharma (2023UG1063)
- Aishwary Dixit (2023UG1049)
- Vijit Vishnoi (2023UG1064)
- Abhinav Kumar Srivastava (2023UG1070)

Instructor: Dr. Jayadeep Pati, IIIT Ranchi.
