-- UDIS schema (SRS §3.4). All statements are idempotent via IF NOT EXISTS.

CREATE TABLE IF NOT EXISTS app_user (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role          VARCHAR(20) NOT NULL,
    full_name     VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS student (
    roll_no   VARCHAR(20) PRIMARY KEY,
    name      VARCHAR(100) NOT NULL,
    dob       DATE,
    gender    VARCHAR(10),
    address   VARCHAR(255),
    contact   VARCHAR(20),
    program   VARCHAR(50),
    batch     VARCHAR(10)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS course (
    course_id        VARCHAR(20) PRIMARY KEY,
    course_name      VARCHAR(100) NOT NULL,
    credits          INT NOT NULL,
    semester         INT NOT NULL,
    prerequisite_id  VARCHAR(20) NULL,
    FOREIGN KEY (prerequisite_id) REFERENCES course(course_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS registration (
    reg_id     INT AUTO_INCREMENT PRIMARY KEY,
    roll_no    VARCHAR(20) NOT NULL,
    course_id  VARCHAR(20) NOT NULL,
    semester   INT NOT NULL,
    year       INT NOT NULL,
    status     VARCHAR(15) NOT NULL DEFAULT 'REGISTERED',
    UNIQUE KEY uq_reg (roll_no, course_id, semester, year),
    FOREIGN KEY (roll_no)   REFERENCES student(roll_no) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS grade (
    grade_id     INT AUTO_INCREMENT PRIMARY KEY,
    roll_no      VARCHAR(20) NOT NULL,
    course_id    VARCHAR(20) NOT NULL,
    semester     INT NOT NULL,
    year         INT NOT NULL,
    letter_grade VARCHAR(2) NOT NULL,
    grade_points DECIMAL(4,2) NOT NULL,
    UNIQUE KEY uq_grade (roll_no, course_id, semester, year),
    FOREIGN KEY (roll_no)   REFERENCES student(roll_no) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES course(course_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS inventory_item (
    item_id          INT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    category         VARCHAR(50),
    serial_number    VARCHAR(50) UNIQUE,
    location         VARCHAR(100),
    acquisition_date DATE,
    condition_status VARCHAR(20) DEFAULT 'GOOD'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS txn (
    txn_id      INT AUTO_INCREMENT PRIMARY KEY,
    txn_date    DATE NOT NULL,
    description VARCHAR(255),
    category    VARCHAR(15) NOT NULL,
    amount      DECIMAL(12,2) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS research_project (
    project_id     INT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(200) NOT NULL,
    pi             VARCHAR(100),
    funding_source VARCHAR(100),
    start_date     DATE,
    end_date       DATE,
    status         VARCHAR(20) DEFAULT 'ONGOING'
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS publication (
    pub_id   INT AUTO_INCREMENT PRIMARY KEY,
    title    VARCHAR(200) NOT NULL,
    authors  VARCHAR(255),
    journal  VARCHAR(150),
    year     INT,
    doi      VARCHAR(100)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS audit_log (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    action   VARCHAR(100),
    entity   VARCHAR(50),
    at_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
