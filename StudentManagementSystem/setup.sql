-- ============================================================
--  Student Management System – Database Setup Script
--  Run this in psql or pgAdmin ONCE before launching the app.
-- ============================================================

-- 1. Create the database (run as superuser if needed)
CREATE DATABASE studentdb;

-- 2. Connect to the new database, then run the rest:
\c studentdb

-- 3. Create the students table
CREATE TABLE IF NOT EXISTS students (
    id         SERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    course     VARCHAR(100) NOT NULL,
    year_level VARCHAR(20)  NOT NULL
);

-- 4. (Optional) Seed some sample data
INSERT INTO students (name, course, year_level) VALUES
    ('Juan dela Cruz',   'BS Computer Science',      '1st Year'),
    ('Maria Santos',     'BS Information Technology','2nd Year'),
    ('Pedro Reyes',      'BS Computer Engineering',  '3rd Year'),
    ('Ana Gonzales',     'BS Information Systems',   '4th Year');
