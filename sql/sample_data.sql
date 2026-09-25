-- Optional sample data for trying the application.
-- Run after schema.sql:  mysql -u root -p < sql/sample_data.sql

USE student_management;

INSERT INTO students (name, email, phone) VALUES
    ('Thabo Mokoena',   'thabo.mokoena@example.com',   '0712345678'),
    ('Lerato Ndlovu',   'lerato.ndlovu@example.com',   '0723456789'),
    ('Sipho Dlamini',   'sipho.dlamini@example.com',   '0734567890');

INSERT INTO courses (course_name, course_code) VALUES
    ('Introduction to Programming', 'PRG101'),
    ('Database Systems',            'DBS201'),
    ('Software Engineering',        'SEN301');

INSERT INTO registrations (student_id, course_id) VALUES
    (1, 1),
    (1, 2),
    (2, 2),
    (3, 3);
