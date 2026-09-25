-- =============================================================
-- Student Management System - database schema
-- Run with:  mysql -u root -p < sql/schema.sql
-- =============================================================

DROP DATABASE IF EXISTS student_management;
CREATE DATABASE student_management;

USE student_management;

CREATE TABLE students (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) UNIQUE NOT NULL,
    phone      VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE courses (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) UNIQUE NOT NULL
);

CREATE TABLE registrations (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    student_id        INT NOT NULL,
    course_id         INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- a student can only be registered for the same course once
    UNIQUE KEY uq_student_course (student_id, course_id),

    FOREIGN KEY (student_id) REFERENCES students(id)
        ON DELETE CASCADE,

    FOREIGN KEY (course_id) REFERENCES courses(id)
        ON DELETE CASCADE
);
