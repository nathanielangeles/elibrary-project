-- E-Library Database Schema
-- MySQL Database Creation Script

CREATE DATABASE IF NOT EXISTS elibrary_db;
USE elibrary_db;

-- STUDENT Table
CREATE TABLE IF NOT EXISTS STUDENT (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    lrn VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    grade_level INT NOT NULL,
    section VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_lrn (lrn)
);

-- CATEGORY Table
CREATE TABLE IF NOT EXISTS CATEGORY (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- BOOK Table
CREATE TABLE IF NOT EXISTS BOOK (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    year_published INT,
    description TEXT,
    file_path VARCHAR(500) NOT NULL,
    cover_image_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES CATEGORY(category_id) ON DELETE RESTRICT,
    INDEX idx_title (title),
    INDEX idx_author (author),
    INDEX idx_category (category_id)
);

-- ACCESS_LOG Table
CREATE TABLE IF NOT EXISTS ACCESS_LOG (
    access_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    book_id INT NOT NULL,
    access_type ENUM('VIEW', 'DOWNLOAD') NOT NULL,
    access_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES STUDENT(student_id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES BOOK(book_id) ON DELETE CASCADE,
    INDEX idx_student (student_id),
    INDEX idx_book (book_id),
    INDEX idx_access_type (access_type),
    INDEX idx_access_date (access_date)
);

-- ADMIN Table
CREATE TABLE IF NOT EXISTS ADMIN (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
);

-- Insert default admin account (password: admin123 - hashed)
INSERT INTO ADMIN (first_name, last_name, username, password) 
VALUES ('Admin', 'User', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

-- Insert sample categories
INSERT INTO CATEGORY (category_name) VALUES 
    ('Fiction'),
    ('Non-Fiction'),
    ('Science'),
    ('Mathematics'),
    ('History'),
    ('Literature'),
    ('Technology'),
    ('Arts'),
    ('Biography'),
    ('Reference');

-- Insert sample students
INSERT INTO STUDENT (lrn, first_name, last_name, grade_level, section) VALUES
    ('123456789012', 'Juan', 'Dela Cruz', 11, 'A'),
    ('123456789013', 'Maria', 'Santos', 11, 'B'),
    ('123456789014', 'Pedro', 'Reyes', 12, 'A');
