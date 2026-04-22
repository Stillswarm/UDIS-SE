# UDIS - Mermaid Diagrams (SmartDraw-compatible)

**University Department Information System**

Each block below is adapted for SmartDraw's mermaid importer:

- `graph LR` replaced with `flowchart LR`
- Emojis removed (SmartDraw often can't render them)
- `\n` inside node labels replaced with `<br/>`
- `subgraph` names with spaces rewritten in `id [Label]` form
- Unicode math (Sigma / x / not-equal) replaced with ASCII
- All labels with punctuation quoted

If you ever want the prettier emoji / Unicode versions again, use
`UDIS_Mermaid_Codes.md` at mermaid.live.

---

## 1. DFD Level 0 - Context Diagram

```mermaid
flowchart LR
    SEC["Department Secretary"]
    HOD["HOD"]
    FAC["Faculty / Instructor"]
    ADM["System Administrator"]

    UDIS(["UDIS<br/>University Department<br/>Information System"])

    SEC -->|"student + course + finance data"| UDIS
    UDIS -->|"confirmations, grade sheets, reports"| SEC

    HOD -->|"report and query requests"| UDIS
    UDIS -->|"financial and academic reports"| HOD

    FAC -->|"grade submissions"| UDIS
    UDIS -->|"course / grade view"| FAC

    ADM -->|"backup / maintenance commands"| UDIS
    UDIS -->|"audit and system reports"| ADM
```

---

## 2. DFD Level 1 - Process Decomposition

```mermaid
flowchart TD
    SEC["Secretary"]
    HOD["HOD / Secretary"]

    P1(["1.0<br/>Manage Students"])
    P2(["2.0<br/>Register Courses"])
    P3(["3.0<br/>Enter Grades"])
    P4(["4.0<br/>Manage Inventory"])
    P5(["5.0<br/>Manage Finance"])
    P6(["6.0<br/>Reporting and Query"])

    DS1[("D1: Student")]
    DS2[("D2: Course")]
    DS3[("D3: Registration")]
    DS4[("D4: Grade")]
    DS5[("D5: Inventory")]
    DS6[("D6: Transaction")]
    DS9[("D9: Audit Log")]

    SEC -->|"student data"| P1
    SEC -->|"registration request"| P2
    SEC -->|"grade submission"| P3
    SEC -->|"inventory data"| P4
    SEC -->|"finance transaction"| P5

    P1 <-->|"read / write"| DS1
    DS1 -->|"student lookup"| P2
    DS2 <-->|"course details"| P2
    P2 -->|"write registration"| DS3
    DS3 -->|"registration record"| P3
    DS2 -->|"course credits"| P3
    P3 -->|"write grade"| DS4
    P4 <-->|"read / write"| DS5
    P5 -->|"write transaction"| DS6

    DS4 -->|"grade data"| P6
    DS6 -->|"transaction data"| P6
    P6 -->|"audit entry"| DS9
    P6 -->|"reports / results"| HOD
    HOD -->|"report / query request"| P6
```

---

## 3. DFD Level 2 - Sub-process Decomposition

### 3a. Process 1.0 - Manage Students

```mermaid
flowchart LR
    SEC["Secretary"]

    P11(["1.1<br/>Validate Roll Number"])
    P12(["1.2<br/>Create Student Record"])
    P13(["1.3<br/>Search Student"])
    P14(["1.4<br/>Update Student Profile"])

    DS1[("D1: Student")]

    SEC -->|"new student data"| P11
    P11 -->|"validated data<br/>no duplicate"| P12
    P12 -->|"write new record"| DS1

    SEC -->|"roll_no / name"| P13
    P13 -->|"lookup query"| DS1
    DS1 -->|"student record"| P13
    P13 -->|"retrieved record"| P14
    SEC -->|"updated fields"| P14
    P14 -->|"updated record"| DS1
```

---

### 3b. Process 2.0 - Register Courses

```mermaid
flowchart LR
    SEC["Secretary"]

    P21(["2.1<br/>Check Prerequisite"])
    P22(["2.2<br/>Check Duplicate<br/>Registration"])
    P23(["2.3<br/>Create Registration"])
    P24(["2.4<br/>Flag Backlog"])

    DS2[("D2: Course")]
    DS3[("D3: Registration")]
    DS4[("D4: Grade")]

    SEC -->|"roll_no + course_id"| P21
    DS2 -->|"prerequisite_id"| P21
    DS4 -->|"completed grades"| P21
    P21 -->|"prerequisite OK"| P22
    P21 -->|"rejection: not met"| SEC

    DS3 -->|"existing registrations"| P22
    P22 -->|"no duplicate confirmed"| P23
    P22 -->|"rejection: already registered"| SEC

    P23 -->|"write ENROLLED"| DS3

    DS4 -->|"grade = F"| P24
    P24 -->|"update status = BACKLOG"| DS3
```

---

### 3c. Process 3.0 - Enter Grades

```mermaid
flowchart LR
    FAC["Faculty"]

    P31(["3.1<br/>Receive Grade Input"])
    P32(["3.2<br/>Compute Grade Points"])
    P33(["3.3<br/>Compute Semester GPA"])
    P34(["3.4<br/>Compute CGPA"])
    P35(["3.5<br/>Generate Grade Sheet"])

    DS3[("D3: Registration")]
    DS4[("D4: Grade")]

    FAC -->|"letter_grade per course"| P31
    P31 -->|"raw grade"| P32
    P32 -->|"write grade_points"| DS4

    DS4 -->|"grades for sem / year"| P33
    DS3 -->|"credits for enrolled courses"| P33
    P33 -->|"semester GPA"| P34

    DS4 -->|"all historical grades"| P34
    P34 -->|"update CGPA"| DS4

    DS4 -->|"grades + GPA / CGPA"| P35
    P35 -->|"formatted grade sheet"| FAC
```

---

### 3d. Process 4.0 - Manage Inventory

```mermaid
flowchart LR
    SEC["Secretary"]

    P41(["4.1<br/>Add Item"])
    P42(["4.2<br/>Search / Filter Items"])
    P43(["4.3<br/>Update Item Details"])
    P44(["4.4<br/>Mark Item Disposed"])

    DS5[("D5: Inventory")]

    SEC -->|"item details"| P41
    P41 -->|"write new item"| DS5

    SEC -->|"filter criteria"| P42
    DS5 -->|"matching items"| P42
    P42 -->|"search results"| SEC
    P42 -->|"selected record"| P43
    SEC -->|"updated fields"| P43
    P43 -->|"update record"| DS5

    P42 -->|"selected item"| P44
    P44 -->|"condition_status = DISPOSED"| DS5
```

---

### 3e. Process 5.0 - Manage Finance

```mermaid
flowchart LR
    SEC["Secretary"]

    P51(["5.1<br/>Record Transaction"])
    P52(["5.2<br/>Compute Running Balance"])
    P53(["5.3<br/>Filter by Date Range"])
    P54(["5.4<br/>Generate Financial Summary"])

    DS6[("D6: Transaction")]

    SEC -->|"date, description,<br/>category, amount"| P51
    P51 -->|"write transaction"| DS6

    DS6 -->|"all transactions"| P52
    P52 -->|"update running balance"| DS6

    SEC -->|"start_date + end_date"| P53
    DS6 -->|"filtered transactions"| P53
    P53 -->|"filtered set"| P54
    P54 -->|"total income, expenditure,<br/>net balance"| SEC
```

---

## 4. UML Diagrams

---

### 4a. Use Case Diagram

```mermaid
flowchart LR
    SEC(["Department Secretary"])
    HOD(["HOD"])
    FAC(["Faculty / Instructor"])
    ADM(["System Administrator"])

    subgraph udis [UDIS System]
        LOGIN["Login / Authenticate"]
        UC1["F-01: Manage Student Profile"]
        UC2["F-02: Manage Course Catalogue"]
        UC3["F-03: Register Courses"]
        UC4["F-04: Enter Grades / GPA"]
        UC5["F-05: Print Grade Sheet"]
        UC6["F-06: Manage Inventory"]
        UC7["F-07: Manage Cash Book"]
        UC8["F-08: Generate Financial Report"]
        UC9["F-09: Track Research and Publications"]
        UC10["F-10: Student Information Query"]
        UC11["F-11: Manage User Roles"]
        UC12["F-12: View Audit Log"]
    end

    SEC --- LOGIN
    HOD --- LOGIN
    FAC --- LOGIN
    ADM --- LOGIN

    SEC --- UC1
    SEC --- UC2
    SEC --- UC3
    SEC --- UC4
    SEC --- UC5
    SEC --- UC6
    SEC --- UC7
    SEC --- UC8
    SEC --- UC9

    HOD --- UC5
    HOD --- UC8
    HOD --- UC10

    FAC --- UC4
    FAC --- UC5

    ADM --- UC11
    ADM --- UC12
```

---

### 4b. Class Diagram

```mermaid
classDiagram
    class Student {
        +String roll_no
        +String name
        +Date dob
        +String gender
        +String program
        +int batch
        +String address
        +String contact
    }

    class Course {
        +String course_id
        +String course_name
        +int credits
        +int semester
        +String prerequisite_id
    }

    class Registration {
        +int reg_id
        +String roll_no
        +String course_id
        +int semester
        +int year
        +String status
    }

    class Grade {
        +int grade_id
        +String roll_no
        +String course_id
        +int semester
        +int year
        +String letter_grade
        +double grade_points
    }

    class InventoryItem {
        +int item_id
        +String name
        +String category
        +String serial_number
        +String location
        +Date acquisition_date
        +String condition_status
    }

    class Transaction {
        +int txn_id
        +Date txn_date
        +String description
        +String category
        +double amount
    }

    class ResearchProject {
        +int project_id
        +String title
        +String PI
        +String funding_source
        +Date start_date
        +Date end_date
        +String status
    }

    class Publication {
        +int pub_id
        +String title
        +String authors
        +String journal
        +int year
        +String doi
    }

    class AppUser {
        +int user_id
        +String username
        +String password_hash
        +String role
        +String full_name
    }

    class AuditLog {
        +int id
        +String username
        +String action
        +String entity
        +DateTime at_time
    }

    class AuthService {
        +login(username, password) AppUser
        +canAccess(module) boolean
        +canWrite(module) boolean
    }

    class RegistrationService {
        +register(rollNo, courseId, sem, year) Registration
        +checkPrerequisite(rollNo, courseId) boolean
        +checkDuplicate(rollNo, courseId, sem, year) boolean
    }

    class GpaService {
        +computeSemesterGPA(rollNo, sem, year) double
        +computeCGPA(rollNo) double
        +letterToPoints(letterGrade) double
    }

    class CashBookService {
        +addTransaction(txn) void
        +getRunningBalance() double
        +getSummary(from, to) FinancialReport
    }

    class AuditService {
        +log(username, action, entity) void
    }

    Student "1" --> "0..*" Registration : enrolled in
    Course "1" --> "0..*" Registration : registered for
    Student "1" --> "0..*" Grade : receives
    Course "1" --> "0..*" Grade : assessed by
    Course "0..1" --> "0..*" Course : prerequisite of

    AppUser ..> AuthService : uses
    RegistrationService ..> Registration : creates
    RegistrationService ..> Grade : reads
    GpaService ..> Grade : reads
    CashBookService ..> Transaction : manages
    AuditService ..> AuditLog : writes
```

---

### 4c. Sequence Diagram - Course Registration Flow

```mermaid
sequenceDiagram
    actor Secretary
    participant UI as RegistrationPanel
    participant RS as RegistrationService
    participant AS as AuditService
    participant DB as MySQL

    Secretary->>UI: enter roll_no + course_id + sem + year
    UI->>RS: register(rollNo, courseId, sem, year)

    RS->>DB: SELECT grade WHERE roll_no = ? AND course_id = prerequisite_id
    DB-->>RS: grade record or null

    alt Prerequisite not completed
        RS-->>UI: PrerequisiteException
        UI-->>Secretary: "Prerequisite not completed"
    else Prerequisite OK
        RS->>DB: SELECT registration WHERE roll_no / course_id / sem / year
        DB-->>RS: existing record or null

        alt Duplicate registration
            RS-->>UI: DuplicateRegistrationException
            UI-->>Secretary: "Already registered"
        else No duplicate
            RS->>DB: INSERT INTO registration (status = 'ENROLLED')
            DB-->>RS: reg_id
            RS->>AS: log(user, REGISTER, reg_id)
            AS->>DB: INSERT INTO audit_log
            RS-->>UI: Registration
            UI-->>Secretary: "Registration successful"
        end
    end
```

---

### 4d. Sequence Diagram - Grade Entry and GPA Computation

```mermaid
sequenceDiagram
    actor Faculty
    participant UI as GradePanel
    participant GS as GpaService
    participant AS as AuditService
    participant DB as MySQL

    Faculty->>UI: submit letter_grade for roll_no / course_id / sem / year
    UI->>GS: enterGrade(rollNo, courseId, sem, year, letterGrade)

    GS->>GS: points = letterToPoints(letterGrade)
    GS->>DB: INSERT INTO grade (letter_grade, grade_points)
    DB-->>GS: grade_id

    GS->>DB: SELECT grade_points, credits for (roll_no, semester, year)
    DB-->>GS: semester grades list
    GS->>GS: semGPA = Sum(points * credits) / Sum(credits)

    GS->>DB: SELECT grade_points, credits for roll_no (all time)
    DB-->>GS: all historical grades
    GS->>GS: cgpa = Sum(points * credits) / Sum(credits)

    alt letterGrade is F
        GS->>DB: UPDATE registration SET status = 'BACKLOG'
    end

    GS->>AS: log(user, GRADE_ENTRY, grade_id)
    AS->>DB: INSERT INTO audit_log
    GS-->>UI: semGPA, cgpa
    UI-->>Faculty: updated grade sheet with GPA / CGPA
```

---

### 4e. State Diagram - Registration Lifecycle

```mermaid
stateDiagram-v2
    [*] --> ENROLLED : register<br/>prerequisite OK
    ENROLLED --> COMPLETED : grade entered<br/>grade not F
    ENROLLED --> BACKLOG : grade entered<br/>grade = F
    BACKLOG --> ENROLLED : re-register<br/>in later semester
    COMPLETED --> [*]
```

> If SmartDraw rejects `stateDiagram-v2`, change the first line to `stateDiagram`.

---

### 4f. Activity Diagram - Login and Access Control

```mermaid
flowchart TD
    A([Start]) --> B["User enters username + password"]
    B --> C{"Maintenance<br/>mode active?"}

    C -->|Yes| D{"Role = ADMIN?"}
    D -->|Yes| E["Grant admin access"]
    D -->|No| F["Deny - maintenance in progress"]
    F --> Z([Stop])

    C -->|No| G["AuthService.login(username, password)"]
    G --> H{"BCrypt<br/>match?"}

    H -->|No| I["Log failed attempt to audit_log"]
    I --> J["Show 'Invalid credentials'"]
    J --> Z

    H -->|Yes| K["Log successful login to audit_log"]
    K --> L["Load role permissions"]
    L --> M["Render MainFrame with allowed modules"]
    M --> N["Disable write buttons for read-only roles"]
    N --> O["User interacts with modules"]
    O --> P["Every write operation -> audit_log entry"]
    P --> Z2([Stop])
```

---

## Quick Reference

| Diagram                      | Mermaid type used    |
|------------------------------|----------------------|
| DFD Level 0, 1, 2            | `flowchart`          |
| Use Case                     | `flowchart LR`       |
| Class Diagram                | `classDiagram`       |
| Sequence Diagrams            | `sequenceDiagram`    |
| State Diagram                | `stateDiagram-v2`    |
| Activity Diagram             | `flowchart TD`       |

## If SmartDraw still complains

Usual culprits, in order of likelihood:

1. **Copy-paste ate the indentation.** SmartDraw's parser is whitespace-sensitive inside `subgraph` / `alt / else / end`. Preserve leading spaces.
2. **Extra trailing spaces or a BOM character.** Paste into a plain-text editor first.
3. **`stateDiagram-v2` not recognized.** Fall back to `stateDiagram`.
4. **Curly braces `{ ... }` in class method arguments.** Simplify signatures.
5. **Relationship labels with `:` inside them.** Wrap the whole label in quotes: `A "1" --> "0..*" B : "label"`.
6. **`subgraph` with spaces in its name.** Use the `id [Label]` form (already applied above).
