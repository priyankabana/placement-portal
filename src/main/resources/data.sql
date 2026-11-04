-- ================================================================
--  Sample data for the placement portal.
--
--  Spring Boot runs this file once, right after Hibernate has created
--  the tables. Ids are written by hand so that the foreign keys below
--  stay readable; the ALTER statements at the end push the identity
--  counters ahead so new rows added from the UI never clash with these.
-- ================================================================

-- ------------------------- departments --------------------------
INSERT INTO department (id, name, code, hod_name) VALUES
 (1, 'Computer Science and Engineering', 'CSE',   'Dr. S. R. Kulkarni'),
 (2, 'Information Technology',           'IT',    'Dr. M. A. Deshmukh'),
 (3, 'Electronics and Communication',    'ECE',   'Dr. P. V. Rane'),
 (4, 'Mechanical Engineering',           'MECH',  'Dr. A. K. Joshi'),
 (5, 'Civil Engineering',                'CIVIL', 'Dr. N. B. Pawar');

-- ------------------------- recruiters ---------------------------
INSERT INTO company (id, name, industry, city, website, hr_name, hr_email) VALUES
 (1, 'Infosys Limited',      'IT Services',          'Pune',      'www.infosys.com',    'Anjali Menon',   'campus.pune@infosys.example.com'),
 (2, 'Tata Consultancy Services', 'IT Services',     'Mumbai',    'www.tcs.com',        'Rajesh Iyer',    'hiring.west@tcs.example.com'),
 (3, 'Persistent Systems',   'Product Engineering',  'Pune',      'www.persistent.com', 'Meghana Rao',    'talent@persistent.example.com'),
 (4, 'Barclays Technology',  'Banking Technology',   'Pune',      'www.barclays.in',    'Farhan Qureshi', 'gradhiring@barclays.example.com'),
 (5, 'Zensar Technologies',  'IT Services',          'Pune',      'www.zensar.com',     'Sonal Kelkar',   'freshers@zensar.example.com'),
 (6, 'Bajaj Auto',           'Automobile',           'Pune',      'www.bajajauto.com',  'Deepak Sathe',   'get@bajajauto.example.com'),
 (7, 'L and T Construction', 'Infrastructure',       'Chennai',   'www.lntecc.com',     'Kavita Reddy',   'campus@lntecc.example.com'),
 (8, 'Cognizant',            'IT Consulting',        'Hyderabad', 'www.cognizant.com',  'Nikhil Verma',   'gencnext@cognizant.example.com');

-- --------------------------- staff logins -----------------------------
-- Placement officer and the branch HODs. All of them are STAFF, so they can
-- manage students, recruiters, drives and applications.
-- passwords are BCrypt hashes; the plain demo passwords are listed in docs/login-credentials.txt
INSERT INTO staff (id, name, username, password, designation, email, role, department_id) VALUES
 (1, 'Dr. R. S. Chavan',    'placement.officer', '$2a$10$niQh45nYhqBIn2ZMkXk5eO4XAlLPbGROFbTirrK2u/8GSrvc.mehC',  'Training and Placement Officer', 'tpo@campus.edu.in',      'STAFF',   NULL),
 (2, 'Dr. S. R. Kulkarni',  'cse.hod',           '$2a$10$MH3wN.v5Z8pTGmG39U8ySuI2FpPZPiF9drmUXURo0106gchdwgdhW',   'Head of Department',             'hod.cse@campus.edu.in',  'STAFF',   1),
 (3, 'Dr. M. A. Deshmukh',  'it.hod',            '$2a$10$OY52EjsK2xiYEwWX6VEEF.TnzWJCstp9Q6zlk5zBx23VpTPH2Ze4.',    'Head of Department',             'hod.it@campus.edu.in',   'STAFF',   2),
 (4, 'Dr. P. V. Rane',      'ece.hod',           '$2a$10$Nx9BfAljm0TFeZgcfvFEBuhVUrHSy2pfnt/jEJG174AaizzfAR/V2',   'Head of Department',             'hod.ece@campus.edu.in',  'STAFF',   3),
 (5, 'Dr. A. K. Joshi',     'mech.hod',          '$2a$10$vio4FYZA1oFPWqXoDKnHJus43vQRfyvdDG.8pTbp7xrcqSi1PPvwe',  'Head of Department',             'hod.mech@campus.edu.in', 'STAFF',   4),
 (6, 'Dr. N. B. Pawar',     'civil.hod',         '$2a$10$P7raVxs6fL0jPDOZcTrM/ewiK5H.uwIwt25RT0OAvTMIAsYiHjP/i', 'Head of Department',             'hod.civil@campus.edu.in','STAFF',   5),
 (7, 'Prof. K. D. Sable',   'placement.coord',   '$2a$10$sKZhEM10mUvxvl35ZiQd3O3jQChLkI6YaCJFNzfdUugjKf25d.D9K',    'Placement Coordinator',          'coordinator@campus.edu.in','STAFF', 1),
 (8, 'Prof. A. S. Naik',    'training.coord',    '$2a$10$4Jfmh8n680VUrpTxNnG09ugp1vIZuqanBS5AySTQsWgUzm67oRxQe', 'Training Coordinator',           'training@campus.edu.in', 'STAFF',   2),
 (9, 'Dr. V. M. Sawant',    'principal',         '$2a$10$fbevztlb0yczXEpEm3jaZOXIZaWasKzLjUvSv00i5kSovzZvl5AbO','Principal',                      'principal@campus.edu.in','STAFF',   NULL);

-- --------------------------- students ---------------------------
-- passwords are BCrypt hashes; the plain demo passwords are listed in docs/login-credentials.txt
INSERT INTO student (id, roll_number, first_name, last_name, email, phone, cgpa, batch_year, backlogs, resume_url, password, department_id) VALUES
 (1,  'CS22001', 'Aditi',      'Kulkarni',  'aditi.kulkarni@campus.edu.in',      '9822011001', 8.6, 2026, 0, 'drive.campus.edu.in/cs22001', '$2a$10$cmPZE.GSI.qjBishYrO3vOh85vX75etSis31v6okXQCmD5ddC9C4K', 1),
 (2,  'CS22002', 'Rohan',      'Deshpande', 'rohan.deshpande@campus.edu.in',     '9822011002', 7.9, 2026, 0, 'drive.campus.edu.in/cs22002', '$2a$10$ZsJnECdyZqqvpjXk1b7e6eEv5pOLjpnUNRdpMtpeq2wv1HEyoG7OG', 1),
 (3,  'CS22003', 'Sneha',      'Patil',     'sneha.patil@campus.edu.in',         '9822011003', 9.1, 2026, 0, 'drive.campus.edu.in/cs22003', '$2a$10$KW3WNO.Z.RXCIT/BpGeW1On3XXTD5ZSUNcRRvfpI7oNGrL9S14nsi', 1),
 (4,  'CS22004', 'Karan',      'Mehta',     'karan.mehta@campus.edu.in',         '9822011004', 6.8, 2026, 1, NULL, '$2a$10$OmebS8FqTuYRu8v/Yu3fQ.G9F2lW9ALGu1ZfSkLGBruV5f4YYUc4K', 1),
 (5,  'CS22005', 'Priyanka',   'Bansode',   'priyanka.bansode@campus.edu.in',    '9822011005', 8.2, 2026, 0, 'drive.campus.edu.in/cs22005', '$2a$10$tCVOahy0KpdLcr9KXLuCx.derikrdsLV6/XXorC1fd5aIAJlfKeaa', 1),
 (6,  'CS22006', 'Vishal',     'Chavan',    'vishal.chavan@campus.edu.in',       '9822011006', 6.2, 2026, 2, NULL, '$2a$10$vSfksUXZOTLHbYEdBXv4ZuIT1RHermMc1ES4JXpO.N/rmrdYB/72a', 1),
 (7,  'IT22001', 'Neha',       'Joshi',     'neha.joshi@campus.edu.in',          '9822012001', 8.8, 2026, 0, 'drive.campus.edu.in/it22001', '$2a$10$JUdL43KLDgKLo.Q/wilPQ.Taekxsvdy31t5R7atrMEXBeL6sYI4Ma', 2),
 (8,  'IT22002', 'Sahil',      'Kapoor',    'sahil.kapoor@campus.edu.in',        '9822012002', 7.4, 2026, 0, 'drive.campus.edu.in/it22002', '$2a$10$x.ywoCGXtgzX37boqlk/6.CzaFPqBU7xBRVP6nraMboO7jqep0AWq', 2),
 (9,  'IT22003', 'Ritika',     'Shah',      'ritika.shah@campus.edu.in',         '9822012003', 6.9, 2026, 1, NULL, '$2a$10$2JIwXCxsBWixRMnr18qaSuc4BH9ruPaQlM2N.qhSLqY3Ak9MsSf4G', 2),
 (10, 'IT22004', 'Amit',       'Bhosale',   'amit.bhosale@campus.edu.in',        '9822012004', 7.7, 2026, 0, 'drive.campus.edu.in/it22004', '$2a$10$NCQ2bvPxR8gNVOvBz7/HcORPOJ5v7SDiToMSiINyqWyXkBf4oh3v6', 2),
 (11, 'IT22005', 'Tanvi',      'Rane',      'tanvi.rane@campus.edu.in',          '9822012005', 9.3, 2026, 0, 'drive.campus.edu.in/it22005', '$2a$10$Ha/C2HQN.qn7DeSHZIbu0.LTcKCtE.HhZdi5FuRbwbkPMrS0JPjuu', 2),
 (12, 'EC22001', 'Manish',     'Gupta',     'manish.gupta@campus.edu.in',        '9822013001', 7.1, 2026, 0, NULL, '$2a$10$IlarqmlT8kaFUUfmukHjEerqjanDdQD7D8gxcLd0Gm1OwbOujodBe', 3),
 (13, 'EC22002', 'Pooja',      'Nair',      'pooja.nair@campus.edu.in',          '9822013002', 8.0, 2026, 0, 'drive.campus.edu.in/ec22002', '$2a$10$ZsWL1NMab0/tp1oDZ3KZ2uQxVBXUrFIdTHhkAswxQ1PsqtkLYX.ZS', 3),
 (14, 'EC22003', 'Siddharth',  'Salunkhe',  'siddharth.salunkhe@campus.edu.in',  '9822013003', 6.5, 2026, 1, NULL, '$2a$10$.m775ioiWy8ofRKERU8qUeOY5oXvgLMQgf8/L3.9ueye1.Z6LoRMy', 3),
 (15, 'EC22004', 'Ankita',     'Pandit',    'ankita.pandit@campus.edu.in',       '9822013004', 7.6, 2026, 0, 'drive.campus.edu.in/ec22004', '$2a$10$dZiVwgHOpv0DaOaCCWK53eHvLr09nWF5Wwzd1ZMLxXfJM3gyTIduO', 3),
 (16, 'ME22001', 'Ganesh',     'Shinde',    'ganesh.shinde@campus.edu.in',       '9822014001', 7.2, 2026, 0, NULL, '$2a$10$J6KHZllPn3/OQClZIFAOXuy8ZXErJC19.kbgcJCKd3rKw68a8GWJC', 4),
 (17, 'ME22002', 'Rahul',      'Wagh',      'rahul.wagh@campus.edu.in',          '9822014002', 6.4, 2026, 1, NULL, '$2a$10$dFGSOpbjPl67sKKx4Tr0FuFouku1GQYNsU0xxFfks0iE0v0LGWuYO', 4),
 (18, 'ME22003', 'Snehal',     'Kadam',     'snehal.kadam@campus.edu.in',        '9822014003', 8.1, 2026, 0, 'drive.campus.edu.in/me22003', '$2a$10$0UnXlJkjvISP8WyIJl/p9.QU32yefaKa3CzG2o2vkQ59XgLHMZuDS', 4),
 (19, 'CE22001', 'Akash',      'Bhoir',     'akash.bhoir@campus.edu.in',         '9822015001', 6.7, 2026, 0, NULL, '$2a$10$cKFpdCiD1YHON1ZGJBjHUuYn/gn.pY/4WVrwbv6qI1ZXPyjqZBxj6', 5),
 (20, 'CE22002', 'Vaishnavi',  'More',      'vaishnavi.more@campus.edu.in',      '9822015002', 7.8, 2026, 0, 'drive.campus.edu.in/ce22002', '$2a$10$WtS1amr6Xigpq02bdWQt7uKpnxFsMN.Blqgks5tSbR/Wjon2UKjbW', 5);

-- ---------------------------- drives ----------------------------
INSERT INTO drive (id, company_id, job_role, package_lpa, min_cgpa, max_backlogs, drive_date, location, status, description) VALUES
 (1,  1, 'Systems Engineer',           4.5,  6.0, 1, DATE '2026-08-12', 'Pune',      'CLOSED',
      'Entry level role for the digital services unit. Aptitude test followed by one technical and one HR round.'),
 (2,  2, 'Assistant System Engineer',  3.9,  6.0, 0, DATE '2026-08-20', 'Mumbai',    'CLOSED',
      'National qualifier test, then a technical interview at the Mumbai campus.'),
 (3,  3, 'Software Engineer Trainee',  6.5,  7.0, 0, DATE '2026-09-10', 'Pune',      'OPEN',
      'Core Java and DBMS based written test, one machine coding round and a managerial round.'),
 (4,  4, 'Graduate Analyst',          12.0,  8.0, 0, DATE '2026-09-18', 'Pune',      'OPEN',
      'Highest package of the season. Two coding rounds, a case study discussion and a values interview.'),
 (5,  5, 'Junior Developer',           4.2,  6.5, 1, DATE '2026-09-25', 'Pune',      'OPEN',
      'Open to all circuit branches. Written test on programming basics followed by a technical round.'),
 (6,  2, 'Digital Cadre',              7.0,  7.0, 0, DATE '2026-09-30', 'Pune',      'OPEN',
      'Advanced track of the same qualifier test. Candidates scoring above the digital cut off are called.'),
 (7,  8, 'Programmer Analyst',         4.0,  6.0, 1, DATE '2026-10-05', 'Hyderabad', 'UPCOMING',
      'Bulk hiring drive. Online assessment, communication round and HR discussion.'),
 (8,  6, 'Graduate Engineer Trainee',  5.5,  6.5, 0, DATE '2026-10-14', 'Pune',      'UPCOMING',
      'For mechanical and production students. Technical test on thermodynamics and manufacturing.'),
 (9,  7, 'Site Engineer',              4.8,  6.0, 1, DATE '2026-10-22', 'Chennai',   'UPCOMING',
      'Civil branch drive with a site posting after six months of training.'),
 (10, 1, 'Specialist Programmer',      9.5,  7.5, 0, DATE '2026-11-06', 'Bengaluru', 'UPCOMING',
      'Premium role of the same company. Single coding round of ninety minutes and a systems design interview.');

-- ------------------------- applications -------------------------
-- (student_id, drive_id) is unique, so a student appears only once per drive
INSERT INTO application (id, student_id, drive_id, applied_on, status, remarks) VALUES
 (1,  1,  1, DATE '2026-07-28', 'SELECTED',    'Cleared aptitude and both interview rounds.'),
 (2,  2,  1, DATE '2026-07-28', 'SELECTED',    'Offer letter received on 20 August.'),
 (3,  5,  1, DATE '2026-07-29', 'REJECTED',    'Could not clear the technical round.'),
 (4,  8,  1, DATE '2026-07-29', 'SELECTED',    'Good performance in the HR round.'),
 (5,  10, 1, DATE '2026-07-30', 'SHORTLISTED', 'Waiting for the final result.'),
 (6,  13, 1, DATE '2026-07-30', 'REJECTED',    'Aptitude score below the cut off.'),
 (7,  4,  2, DATE '2026-08-02', 'SELECTED',    'Backlog cleared before the drive date.'),
 (8,  9,  2, DATE '2026-08-02', 'SELECTED',    'Selected in the second slot.'),
 (9,  12, 2, DATE '2026-08-03', 'REJECTED',    'Did not attend the interview.'),
 (10, 16, 2, DATE '2026-08-03', 'SHORTLISTED', 'Shortlisted for the final HR round.'),
 (11, 20, 2, DATE '2026-08-04', 'SELECTED',    'Joining date shared by the company.'),
 (12, 3,  3, DATE '2026-08-25', 'SHORTLISTED', 'Machine coding round scheduled.'),
 (13, 7,  3, DATE '2026-08-25', 'SELECTED',    'Topped the written test.'),
 (14, 11, 3, DATE '2026-08-26', 'APPLIED',     NULL),
 (15, 2,  3, DATE '2026-08-26', 'APPLIED',     'Applied for a better package.'),
 (16, 3,  4, DATE '2026-08-30', 'APPLIED',     NULL),
 (17, 11, 4, DATE '2026-08-30', 'SHORTLISTED', 'Cleared the first coding round.'),
 (18, 7,  4, DATE '2026-08-31', 'APPLIED',     NULL),
 (19, 5,  5, DATE '2026-09-01', 'APPLIED',     NULL),
 (20, 10, 5, DATE '2026-09-01', 'APPLIED',     NULL),
 (21, 15, 5, DATE '2026-09-02', 'APPLIED',     'First drive of this student.'),
 (22, 1,  6, DATE '2026-09-02', 'APPLIED',     'Applied for the digital cadre role.'),
 (23, 13, 6, DATE '2026-09-03', 'APPLIED',     NULL),
 (24, 18, 6, DATE '2026-09-03', 'SELECTED',    'Cross branch selection through the digital test.');

-- Move the identity counters past the seed rows
ALTER TABLE department ALTER COLUMN id RESTART WITH 100;
ALTER TABLE staff      ALTER COLUMN id RESTART WITH 100;
ALTER TABLE company    ALTER COLUMN id RESTART WITH 100;
ALTER TABLE student    ALTER COLUMN id RESTART WITH 100;
ALTER TABLE drive      ALTER COLUMN id RESTART WITH 100;
ALTER TABLE application ALTER COLUMN id RESTART WITH 100;
