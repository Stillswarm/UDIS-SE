-- Demo seed data. Users are seeded programmatically (BCrypt hashes).

INSERT INTO course (course_id, course_name, credits, semester, prerequisite_id) VALUES
  ('CS101', 'Introduction to Programming',    4, 1, NULL),
  ('CS102', 'Data Structures',                4, 2, 'CS101'),
  ('CS201', 'Algorithms',                     4, 3, 'CS102'),
  ('CS202', 'Database Systems',               3, 3, 'CS102'),
  ('CS301', 'Operating Systems',              4, 4, 'CS201'),
  ('CS302', 'Software Engineering',           3, 4, 'CS202');

INSERT INTO student (roll_no, name, dob, gender, address, contact, program, batch) VALUES
  ('2023UG1049', 'Aishwary Dixit',            '2005-05-14', 'M', 'IIIT Ranchi Hostel', '9000000049', 'B.Tech CSE', '2023'),
  ('2023UG1063', 'Aditya Sharma',             '2005-02-11', 'M', 'IIIT Ranchi Hostel', '9000000063', 'B.Tech CSE', '2023'),
  ('2023UG1064', 'Vijit Vishnoi',             '2005-07-22', 'M', 'IIIT Ranchi Hostel', '9000000064', 'B.Tech CSE', '2023'),
  ('2023UG1070', 'Abhinav Kumar Srivastava',  '2005-09-03', 'M', 'IIIT Ranchi Hostel', '9000000070', 'B.Tech CSE', '2023');

-- Registrations: roll 1049 has completed sem 1 & 2, currently in sem 3.
-- Roll 1063 completed sem 1, failed CS102 in sem 2 (backlog), currently retaking.
INSERT INTO registration (roll_no, course_id, semester, year, status) VALUES
  ('2023UG1049', 'CS101', 1, 2023, 'COMPLETED'),
  ('2023UG1049', 'CS102', 2, 2024, 'COMPLETED'),
  ('2023UG1049', 'CS201', 3, 2024, 'REGISTERED'),
  ('2023UG1049', 'CS202', 3, 2024, 'REGISTERED'),
  ('2023UG1063', 'CS101', 1, 2023, 'COMPLETED'),
  ('2023UG1063', 'CS102', 2, 2024, 'BACKLOG'),
  ('2023UG1063', 'CS102', 3, 2024, 'REGISTERED'),
  ('2023UG1064', 'CS101', 1, 2023, 'COMPLETED'),
  ('2023UG1064', 'CS102', 2, 2024, 'COMPLETED');

INSERT INTO grade (roll_no, course_id, semester, year, letter_grade, grade_points) VALUES
  ('2023UG1049', 'CS101', 1, 2023, 'A',  10.00),
  ('2023UG1049', 'CS102', 2, 2024, 'A-',  9.00),
  ('2023UG1063', 'CS101', 1, 2023, 'B',   8.00),
  ('2023UG1063', 'CS102', 2, 2024, 'F',   0.00),
  ('2023UG1064', 'CS101', 1, 2023, 'A',  10.00),
  ('2023UG1064', 'CS102', 2, 2024, 'B+',  8.50);

INSERT INTO inventory_item (name, category, serial_number, location, acquisition_date, condition_status) VALUES
  ('Dell OptiPlex 7090',  'Computer',  'SN-DLL-001', 'Lab A - Seat 01', '2023-08-12', 'GOOD'),
  ('Projector Epson X41', 'AV',        'SN-EPS-014', 'Seminar Hall',    '2022-11-03', 'GOOD'),
  ('Whiteboard 6x4',      'Furniture', 'SN-WB-0027', 'Room 204',        '2021-07-19', 'FAIR'),
  ('HP LaserJet Pro',     'Printer',   'SN-HP-1102', 'Secretariat',     '2024-01-09', 'GOOD');

INSERT INTO txn (txn_date, description, category, amount) VALUES
  ('2024-04-01', 'Annual University Grant',      'INCOME',      500000.00),
  ('2024-05-12', 'Consultancy - TCS',            'INCOME',       75000.00),
  ('2024-06-03', 'Lab Computers (3 units)',      'EXPENDITURE', 180000.00),
  ('2024-07-20', 'Books and Journals',           'EXPENDITURE',  42000.00),
  ('2024-08-15', 'Stationery and Printing',      'EXPENDITURE',   8500.00);

INSERT INTO research_project (title, pi, funding_source, start_date, end_date, status) VALUES
  ('Privacy-Preserving ML for Edge Devices', 'Dr. J. Pati',   'SERB',   '2024-01-15', NULL,         'ONGOING'),
  ('IoT Sensor Networks for Smart Campus',    'Dr. A. Verma',  'DST',    '2023-06-01', '2025-05-31', 'ONGOING'),
  ('Graph Algorithms for Social Networks',    'Dr. R. Kumar',  'IIIT-R', '2022-09-10', '2024-03-15', 'COMPLETED');

INSERT INTO publication (title, authors, journal, year, doi) VALUES
  ('Federated Learning Survey',             'J. Pati, A. Verma', 'IEEE Access',                2024, '10.1109/ACCESS.2024.0001'),
  ('Efficient Graph Partitioning',          'R. Kumar',          'ACM Trans. on Algorithms',   2023, '10.1145/3581234'),
  ('IoT Anomaly Detection with LSTMs',      'A. Verma, S. Singh','Elsevier IoT Journal',       2024, '10.1016/j.iot.2024.100123');
