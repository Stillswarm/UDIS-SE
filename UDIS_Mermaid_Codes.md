# UDIS – Mermaid Diagram Codes
**University Department Information System**

Paste any block below at [https://mermaid.live](https://mermaid.live) to preview instantly.

---

## 1. DFD Level 0 – Context Diagram
*(Represented as a flowchart — Mermaid has no native DFD type)*

```mermaid
flowchart LR
    SEC["👤 Department Secretary"]
    HOD["👤 HOD"]
    FAC["👤 Faculty / Instructor"]
    ADM["👤 System Administrator"]

    UDIS(["🖥️ UDIS\nUniversity Department\nInformation System"])

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

## 2. DFD Level 1 – Process Decomposition

```mermaid
flowchart TD
    SEC["👤 Secretary"]
    HOD["👤 HOD / Secretary"]

    P1(["1.0\nManage Students"])
    P2(["2.0\nRegister Courses"])
    P3(["3.0\nEnter Grades"])
    P4(["4.0\nManage Inventory"])
    P5(["5.0\nManage Finance"])
    P6(["6.0\nReporting & Query"])

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

    P1 <-->|"read/write"| DS1
    DS1 -->|"student lookup"| P2
    DS2 <-->|"course details"| P2
    P2 -->|"write registration"| DS3
    DS3 -->|"registration record"| P3
    DS2 -->|"course credits"| P3
    P3 -->|"write grade"| DS4
    P4 <-->|"read/write"| DS5
    P5 -->|"write transaction"| DS6

    DS4 -->|"grade data"| P6
    DS6 -->|"transaction data"| P6
    P6 -->|"audit entry"| DS9
    P6 -->|"reports / results"| HOD
    HOD -->|"report / query request"| P6
```

---

## 3. DFD Level 2 – Sub-process Decomposition

### 3a. Process 1.0 – Manage Students

```mermaid
flowchart LR
    SEC["👤 Secretary"]

    P11(["1.1\nValidate Roll Number"])
    P12(["1.2\nCreate Student Record"])
    P13(["1.3\nSearch Student"])
    P14(["1.4\nUpdate Student Profile"])

    DS1[("D1: Student")]

    SEC -->|"new student data"| P11
    P11 -->|"validated data\n(no duplicate)"| P12
    P12 -->|"write new record"| DS1

    SEC -->|"roll_no / name"| P13
    P13 -->|"lookup query"| DS1
    DS1 -->|"student record"| P13
    P13 -->|"retrieved record"| P14
    SEC -->|"updated fields"| P14
    P14 -->|"updated record"| DS1
```

---

### 3b. Process 2.0 – Register Courses

```mermaid
flowchart LR
    SEC["👤 Secretary"]

    P21(["2.1\nCheck Prerequisite"])
    P22(["2.2\nCheck Duplicate\nRegistration"])
    P23(["2.3\nCreate Registration"])
    P24(["2.4\nFlag Backlog"])

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

### 3c. Process 3.0 – Enter Grades

```mermaid
flowchart LR
    FAC["👤 Faculty"]

    P31(["3.1\nReceive Grade Input"])
    P32(["3.2\nCompute Grade Points"])
    P33(["3.3\nCompute Semester GPA"])
    P34(["3.4\nCompute CGPA"])
    P35(["3.5\nGenerate Grade Sheet"])

    DS3[("D3: Registration")]
    DS4[("D4: Grade")]

    FAC -->|"letter_grade per course"| P31
    P31 -->|"raw grade"| P32
    P32 -->|"write grade_points"| DS4

    DS4 -->|"grades for sem/year"| P33
    DS3 -->|"credits for enrolled courses"| P33
    P33 -->|"semester GPA"| P34

    DS4 -->|"all historical grades"| P34
    P34 -->|"update CGPA"| DS4

    DS4 -->|"grades + GPA/CGPA"| P35
    P35 -->|"formatted grade sheet"| FAC
```

---

### 3d. Process 4.0 – Manage Inventory

```mermaid
flowchart LR
    SEC["👤 Secretary"]

    P41(["4.1\nAdd Item"])
    P42(["4.2\nSearch / Filter Items"])
    P43(["4.3\nUpdate Item Details"])
    P44(["4.4\nMark Item Disposed"])

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

### 3e. Process 5.0 – Manage Finance

```mermaid
flowchart LR
    SEC["👤 Secretary"]

    P51(["5.1\nRecord Transaction"])
    P52(["5.2\nCompute Running Balance"])
    P53(["5.3\nFilter by Date Range"])
    P54(["5.4\nGenerate Financial Summary"])

    DS6[("D6: Transaction")]

    SEC -->|"date, description,\ncategory, amount"| P51
    P51 -->|"write transaction"| DS6

    DS6 -->|"all transactions"| P52
    P52 -->|"update running balance"| DS6

    SEC -->|"start_date + end_date"| P53
    DS6 -->|"filtered transactions"| P53
    P53 -->|"filtered set"| P54
    P54 -->|"total income, expenditure,\nnet balance"| SEC
```

---

## 4. UML Diagrams

---

### 4a. Use Case Diagram

```mermaid
graph LR
    SEC("👤 Department Secretary")
    HOD("👤 HOD")
    FAC("👤 Faculty / Instructor")
    ADM("👤 System Administrator")

    subgraph UDIS System
        LOGIN["Login / Authenticate"]
        UC1["F-01: Manage Student Profile"]
        UC2["F-02: Manage Course Catalogue"]
        UC3["F-03: Register Courses"]
        UC4["F-04: Enter Grades / GPA"]
        UC5["F-05: Print Grade Sheet"]
        UC6["F-06: Manage Inventory"]
        UC7["F-07: Manage Cash Book"]
        UC8["F-08: Generate Financial Report"]
        UC9["F-09: Track Research & Publications"]
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
        +String roll_no PK
        +String name
        +Date dob
        +String gender
        +String program
        +int batch
        +String address
        +String contact
    }

    class Course {
        +String course_id PK
        +String course_name
        +int credits
        +int semester
        +String prerequisite_id FK
    }

    class Registration {
        +int reg_id PK
        +String roll_no FK
        +String course_id FK
        +int semester
        +int year
        +String status
    }

    class Grade {
        +int grade_id PK
        +String roll_no FK
        +String course_id FK
        +int semester
        +int year
        +String letter_grade
        +double grade_points
    }

    class InventoryItem {
        +int item_id PK
        +String name
        +String category
        +String serial_number
        +String location
        +Date acquisition_date
        +String condition_status
    }

    class Transaction {
        +int txn_id PK
        +Date txn_date
        +String description
        +String category
        +double amount
    }

    class ResearchProject {
        +int project_id PK
        +String title
        +String PI
        +String funding_source
        +Date start_date
        +Date end_date
        +String status
    }

    class Publication {
        +int pub_id PK
        +String title
        +String authors
        +String journal
        +int year
        +String doi
    }

    class AppUser {
        +int user_id PK
        +String username
        +String password_hash
        +String role
        +String full_name
    }

    class AuditLog {
        +int id PK
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

### 4c. Sequence Diagram – Course Registration Flow

```mermaid
sequenceDiagram
    actor Secretary as Secretary
    participant UI as RegistrationPanel (UI)
    participant RS as RegistrationService
    participant AS as AuditService
    participant DB as MySQL DB

    Secretary->>UI: enter roll_no + course_id + sem + year
    UI->>RS: register(rollNo, courseId, sem, year)

    RS->>DB: SELECT grade WHERE roll_no=? AND course_id=prerequisite_id
    DB-->>RS: grade record (or null)

    alt Prerequisite not completed
        RS-->>UI: PrerequisiteException
        UI-->>Secretary: "Prerequisite not completed"
    else Prerequisite OK
        RS->>DB: SELECT * FROM registration WHERE roll_no=? AND course_id=? AND sem=? AND year=?
        DB-->>RS: existing record (or null)

        alt Duplicate registration
            RS-->>UI: DuplicateRegistrationException
            UI-->>Secretary: "Already registered"
        else No duplicate
            RS->>DB: INSERT INTO registration (status='ENROLLED')
            DB-->>RS: reg_id
            RS->>AS: log(username, "REGISTER", "registration:"+reg_id)
            AS->>DB: INSERT INTO audit_log
            RS-->>UI: Registration object
            UI-->>Secretary: "Registration successful"
        end
    end
```

---

### 4d. Sequence Diagram – Grade Entry & GPA Computation

```mermaid
sequenceDiagram
    actor Faculty as Faculty
    participant UI as GradePanel (UI)
    participant GS as GpaService
    participant AS as AuditService
    participant DB as MySQL DB

    Faculty->>UI: submit letter_grade for (roll_no, course_id, sem, year)
    UI->>GS: enterGrade(rollNo, courseId, sem, year, letterGrade)

    GS->>GS: points = letterToPoints(letterGrade)
    GS->>DB: INSERT INTO grade (letter_grade, grade_points)
    DB-->>GS: grade_id

    GS->>DB: SELECT grade_points, credits WHERE roll_no=? AND semester=? AND year=?
    DB-->>GS: semester grades list
    GS->>GS: semGPA = Σ(points×credits) / Σcredits

    GS->>DB: SELECT grade_points, credits WHERE roll_no=? (all time)
    DB-->>GS: all historical grades
    GS->>GS: cgpa = Σ(points×credits) / Σcredits

    alt letterGrade = 'F'
        GS->>DB: UPDATE registration SET status='BACKLOG'
    end

    GS->>AS: log(username, "GRADE_ENTRY", "grade:"+grade_id)
    AS->>DB: INSERT INTO audit_log
    GS-->>UI: semGPA, cgpa
    UI-->>Faculty: updated grade sheet with GPA / CGPA
```

---

### 4e. State Diagram – Registration Lifecycle

```mermaid
stateDiagram-v2
    [*] --> ENROLLED : register()\nprerequisite OK, no duplicate

    ENROLLED --> COMPLETED : grade entered\ngrade ≠ F
    ENROLLED --> BACKLOG : grade entered\ngrade = F

    BACKLOG --> ENROLLED : re-register\nin later semester
    COMPLETED --> [*]
```

---

### 4f. Activity Diagram – Login & Access Control

```mermaid
flowchart TD
    A([Start]) --> B[User enters username + password]
    B --> C{Maintenance\nMode active?}

    C -->|Yes| D{Role = ADMIN?}
    D -->|Yes| E[Grant admin access]
    D -->|No| F[Deny — maintenance in progress]
    F --> Z([Stop])

    C -->|No| G[AuthService.login username + password]
    G --> H{BCrypt\nmatch?}

    H -->|No| I[Log failed attempt → audit_log]
    I --> J[Show 'Invalid credentials']
    J --> Z

    H -->|Yes| K[Log successful login → audit_log]
    K --> L[Load role permissions]
    L --> M[Render MainFrame with allowed modules]
    M --> N[Disable write buttons for read-only roles]
    N --> O[User interacts with modules]
    O --> P[Every write operation → audit_log entry]
    P --> Z2([Stop])
```

---

## Quick Reference

| Diagram | Mermaid type used |
|---|---|
| DFD Level 0, 1, 2 | `flowchart` |
| Use Case | `graph LR` |
| Class Diagram | `classDiagram` |
| Sequence Diagrams | `sequenceDiagram` |
| State Diagram | `stateDiagram-v2` |
| Activity Diagram | `flowchart TD` |

**Preview any block instantly:** paste at [https://mermaid.live](https://mermaid.live)
