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
