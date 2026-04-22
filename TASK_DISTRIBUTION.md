# UDIS - Work Distribution

Division of the already-delivered MVP v1.0 codebase across the four-member
team. Allocation is by **module cohesion**: each engineer owns a vertical
slice (model + DAO + service + UI) so they can speak to their modules end
to end, plus one cross-cutting responsibility.

Total footprint: ~4,300 lines of Java + ~1,000 lines of SQL / config /
docs. Distribution is balanced within roughly 10%.

## Summary

| Member                         | Role              | Owns SRS Features                    | Lines |
|--------------------------------|-------------------|--------------------------------------|-------|
| Aditya Sharma (2023UG1063)     | Team Lead         | F-11, F-12 + infrastructure          | ~1,200 |
| Aishwary Dixit (2023UG1049)    | Academic Core     | F-01, F-02, F-03                     | ~950  |
| Vijit Vishnoi (2023UG1064)     | Grading and Research | F-04, F-05, F-09                 | ~920  |
| Abhinav Kumar Srivastava (2023UG1070) | Operations | F-06, F-07, F-08, F-10              | ~1,230 |

## 1. Aditya Sharma - Team Lead, Infrastructure and RBAC

**Scope: F-11 Role-Based Access Control, F-12 Audit Logging, project
infrastructure, authentication, and navigation shell.**

### Code ownership

| File | Purpose |
|---|---|
| [pom.xml](pom.xml) | Maven build, dependencies, shade plugin |
| [src/main/resources/config.properties](src/main/resources/config.properties) | Runtime configuration |
| [src/main/resources/schema.sql](src/main/resources/schema.sql) | Complete database DDL (co-owned with Aishwary for student/course DDL) |
| [src/main/resources/seed.sql](src/main/resources/seed.sql) | Bootstrap seed data coordination |
| [src/main/java/com/udis/Main.java](src/main/java/com/udis/Main.java) | Application entry point, FlatLaf, DB init |
| [src/main/java/com/udis/db/Database.java](src/main/java/com/udis/db/Database.java) | Connection singleton, schema/seed loader, user seeding |
| [src/main/java/com/udis/model/User.java](src/main/java/com/udis/model/User.java) | User POJO |
| [src/main/java/com/udis/model/AuditEntry.java](src/main/java/com/udis/model/AuditEntry.java) | Audit log POJO |
| [src/main/java/com/udis/dao/UserDao.java](src/main/java/com/udis/dao/UserDao.java) | User lookup |
| [src/main/java/com/udis/dao/AuditDao.java](src/main/java/com/udis/dao/AuditDao.java) | Audit log reader |
| [src/main/java/com/udis/service/AuthService.java](src/main/java/com/udis/service/AuthService.java) | BCrypt login, RBAC matrix |
| [src/main/java/com/udis/service/AuditService.java](src/main/java/com/udis/service/AuditService.java) | Audit write API |
| [src/main/java/com/udis/ui/LoginFrame.java](src/main/java/com/udis/ui/LoginFrame.java) | Login screen, maintenance-mode check |
| [src/main/java/com/udis/ui/MainFrame.java](src/main/java/com/udis/ui/MainFrame.java) | Main window, JTabbedPane, menu, backup action |
| [src/main/java/com/udis/ui/UiUtils.java](src/main/java/com/udis/ui/UiUtils.java) | Shared Swing helpers |
| [src/main/java/com/udis/ui/AuditPanel.java](src/main/java/com/udis/ui/AuditPanel.java) | Admin-only audit log viewer |

### Cross-cutting responsibilities

- Branch policy, code-review sign-off, release tagging.
- `pom.xml` dependency updates and fat-jar packaging.
- README structure and roadmap ownership.
- Database backup workflow (`mysqldump` integration in `MainFrame`).
- Maintenance-mode behavior and rollout.

### Demo talking points

Authentication flow, RBAC matrix, audit trail, maintenance mode,
architecture overview.

## 2. Aishwary Dixit - Academic Core

**Scope: F-01 Student Profile, F-02 Course Catalogue, F-03 Semester
Registration with prerequisite validation.**

### Code ownership

| File | Purpose |
|---|---|
| [src/main/java/com/udis/model/Student.java](src/main/java/com/udis/model/Student.java) | Student POJO |
| [src/main/java/com/udis/model/Course.java](src/main/java/com/udis/model/Course.java) | Course POJO |
| [src/main/java/com/udis/model/Registration.java](src/main/java/com/udis/model/Registration.java) | Registration POJO |
| [src/main/java/com/udis/dao/StudentDao.java](src/main/java/com/udis/dao/StudentDao.java) | Student CRUD |
| [src/main/java/com/udis/dao/CourseDao.java](src/main/java/com/udis/dao/CourseDao.java) | Course CRUD |
| [src/main/java/com/udis/dao/RegistrationDao.java](src/main/java/com/udis/dao/RegistrationDao.java) | Registration queries and status updates |
| [src/main/java/com/udis/service/RegistrationService.java](src/main/java/com/udis/service/RegistrationService.java) | Prerequisite and duplicate checks |
| [src/main/java/com/udis/ui/StudentPanel.java](src/main/java/com/udis/ui/StudentPanel.java) | Student CRUD UI |
| [src/main/java/com/udis/ui/CoursePanel.java](src/main/java/com/udis/ui/CoursePanel.java) | Course CRUD UI with prerequisite dropdown |
| [src/main/java/com/udis/ui/RegistrationPanel.java](src/main/java/com/udis/ui/RegistrationPanel.java) | Profile lookup, completed/backlog lists, multi-course confirm |

### Cross-cutting responsibilities

- Student / Course / Registration portions of [schema.sql](src/main/resources/schema.sql) (FKs, cascade rules).
- Seed data for students, courses, and prior registrations in [seed.sql](src/main/resources/seed.sql).
- Mermaid diagrams owned:
  - DFD Level 2 - Process 1.0 Manage Students (section 3a)
  - DFD Level 2 - Process 2.0 Register Courses (section 3b)
  - Sequence Diagram - Course Registration Flow (section 4c)
  - State Diagram - Registration Lifecycle (section 4e)

### Demo talking points

Duplicate-roll-number detection, prerequisite graph via `course.prerequisite_id`, backlog flagging, the three-column Registration screen (Completed / Backlog / Available).

## 3. Vijit Vishnoi - Grading, Transcripts, and Research

**Scope: F-04 Grade Entry and GPA/CGPA, F-05 Grade Sheet Printing, F-09
Research Projects and Publications.**

### Code ownership

| File | Purpose |
|---|---|
| [src/main/java/com/udis/model/Grade.java](src/main/java/com/udis/model/Grade.java) | Grade POJO (joined with credits) |
| [src/main/java/com/udis/model/ResearchProject.java](src/main/java/com/udis/model/ResearchProject.java) | Research project POJO |
| [src/main/java/com/udis/model/Publication.java](src/main/java/com/udis/model/Publication.java) | Publication POJO |
| [src/main/java/com/udis/dao/GradeDao.java](src/main/java/com/udis/dao/GradeDao.java) | Grade upsert, semester/student queries |
| [src/main/java/com/udis/dao/ResearchDao.java](src/main/java/com/udis/dao/ResearchDao.java) | Research project CRUD |
| [src/main/java/com/udis/dao/PublicationDao.java](src/main/java/com/udis/dao/PublicationDao.java) | Publication CRUD |
| [src/main/java/com/udis/service/GpaService.java](src/main/java/com/udis/service/GpaService.java) | Letter-to-points map, GPA, CGPA, semester history |
| [src/main/java/com/udis/ui/GradePanel.java](src/main/java/com/udis/ui/GradePanel.java) | Grade entry, auto GPA/CGPA, HTML grade-sheet printing |
| [src/main/java/com/udis/ui/ResearchPanel.java](src/main/java/com/udis/ui/ResearchPanel.java) | Two-tab research/publication CRUD |

### Cross-cutting responsibilities

- Grade-scale policy (`LETTER_TO_POINTS` in `GpaService`).
- Grade-sheet HTML template in `GradePanel.printSheet()`.
- Grade / research / publication DDL in [schema.sql](src/main/resources/schema.sql).
- Seed data for grades, research projects, and publications.
- Mermaid diagrams owned:
  - DFD Level 2 - Process 3.0 Enter Grades (section 3c)
  - Sequence Diagram - Grade Entry and GPA Computation (section 4d)

### Demo talking points

GPA / CGPA formula (SRS Section 3.2.3), automatic COMPLETED / BACKLOG
transition on grade entry, printable transcript, research project
lifecycle.

## 4. Abhinav Kumar Srivastava - Operations, Finance, and Reporting

**Scope: F-06 Inventory, F-07 Cash Book, F-08 Financial Reporting, F-10
Student Information Query, plus presentation tooling.**

### Code ownership

| File | Purpose |
|---|---|
| [src/main/java/com/udis/model/InventoryItem.java](src/main/java/com/udis/model/InventoryItem.java) | Inventory POJO |
| [src/main/java/com/udis/model/Transaction.java](src/main/java/com/udis/model/Transaction.java) | Transaction POJO |
| [src/main/java/com/udis/dao/InventoryDao.java](src/main/java/com/udis/dao/InventoryDao.java) | Inventory CRUD + status update |
| [src/main/java/com/udis/dao/TransactionDao.java](src/main/java/com/udis/dao/TransactionDao.java) | Transaction CRUD, sum / balance queries |
| [src/main/java/com/udis/service/CashBookService.java](src/main/java/com/udis/service/CashBookService.java) | Running balance and date-range summary |
| [src/main/java/com/udis/ui/InventoryPanel.java](src/main/java/com/udis/ui/InventoryPanel.java) | Inventory CRUD, TableRowSorter search, Mark Disposed |
| [src/main/java/com/udis/ui/FinancePanel.java](src/main/java/com/udis/ui/FinancePanel.java) | Transaction table, live balance, summary dialog |
| [src/main/java/com/udis/ui/QueryPanel.java](src/main/java/com/udis/ui/QueryPanel.java) | Read-only student 360 (profile + grades + GPA history + backlogs) |

### Cross-cutting responsibilities

- Inventory and transaction DDL in [schema.sql](src/main/resources/schema.sql).
- Seed data for transactions and inventory items in [seed.sql](src/main/resources/seed.sql).
- Presentation generator [tools/build_pptx.py](tools/build_pptx.py) and artifact [UDIS-Project-Presentation.pptx](UDIS-Project-Presentation.pptx).
- SmartDraw-compatible mermaid file [UDIS_Mermaid_SmartDraw.md](UDIS_Mermaid_SmartDraw.md).
- Mermaid diagrams owned:
  - DFD Level 2 - Process 4.0 Manage Inventory (section 3d)
  - DFD Level 2 - Process 5.0 Manage Finance (section 3e)
  - Activity Diagram - Login and Access Control (section 4f)

### Demo talking points

Inline inventory search, running-balance recomputation, date-range
financial summary, student 360 query, Cash Book formula
(SRS Section 3.2.5).

## Collaboration Matrix

Some responsibilities naturally cut across modules. The table below
disambiguates primary vs. supporting owners.

| Artifact                     | Primary         | Supporting                      |
|------------------------------|-----------------|---------------------------------|
| `schema.sql`                 | Aditya          | Aishwary, Vijit, Abhinav (own tables) |
| `seed.sql`                   | Aditya          | Aishwary, Vijit, Abhinav (own rows) |
| RBAC matrix changes          | Aditya          | All (consumers)                 |
| Audit log keys / actions     | Aditya          | All (callers)                   |
| README                       | Aditya          | All (section reviews)           |
| Mermaid documentation        | Abhinav         | All (own diagrams)              |
| PPTX presentation            | Abhinav         | All (content review)            |
| Unit tests (Phase 2)         | Each owns tests for their services |            |

## Pull-Request Conventions

- One PR per feature or fix; link to the corresponding SRS feature ID
  in the title (for example: `F-03: stricter prerequisite validation`).
- Target branch: `main`.
- Reviewers: at least one peer plus Aditya for any change touching
  `Database`, `AuthService`, `schema.sql`, `seed.sql`, or `pom.xml`.
- Tests for service-layer changes are required once Phase 2 JUnit
  scaffolding lands.

## Phase 2 Task Pre-Allocation

Forward-looking split for the next 1.5 weeks of work, keeping the
ownership lines above stable.

| Phase 2 Work Item                                  | Owner      |
|----------------------------------------------------|------------|
| Date-picker component and swap in every form       | Abhinav    |
| Inline per-field validation                        | Aishwary   |
| HikariCP connection pool replacing singleton       | Aditya     |
| Transactional "Save Grades" commit                 | Vijit      |
| JUnit 5 tests for `GpaService`                     | Vijit      |
| JUnit 5 tests for `RegistrationService`            | Aishwary   |
| JUnit 5 tests for `CashBookService`                | Abhinav    |
| JUnit 5 tests for `AuthService` and maintenance    | Aditya     |
| Keyboard shortcuts across panels                   | Aishwary   |
| Application icon and splash screen                 | Aditya     |
