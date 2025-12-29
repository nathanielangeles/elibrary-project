-- Add Sample Books for Testing
-- Run this after schema.sql to populate the library with test data

USE elibrary_db;

-- Insert sample books (make sure the file paths exist or will be created)
INSERT INTO BOOK (category_id, title, author, year_published, description, file_path, cover_image_path) VALUES
(1, 'The Great Adventure', 'John Smith', 2020, 'An exciting tale of adventure and discovery in uncharted lands.', 'library/books/sample1.pdf', NULL),
(1, 'Mystery at Midnight', 'Jane Doe', 2019, 'A thrilling mystery that will keep you guessing until the very end.', 'library/books/sample2.pdf', NULL),
(3, 'Introduction to Physics', 'Dr. Robert Johnson', 2021, 'A comprehensive guide to understanding the fundamentals of physics.', 'library/books/sample3.pdf', NULL),
(4, 'Calculus Made Easy', 'Prof. Mary Williams', 2022, 'Learn calculus with clear explanations and practical examples.', 'library/books/sample4.pdf', NULL),
(6, 'Classic Poetry Collection', 'Various Authors', 2018, 'A beautiful collection of timeless poems from renowned poets.', 'library/books/sample5.pdf', NULL),
(7, 'Modern Web Development', 'Alex Turner', 2023, 'Master the latest web technologies including HTML5, CSS3, and JavaScript.', 'library/books/sample6.pdf', NULL),
(2, 'World History: A Brief Overview', 'Dr. Sarah Brown', 2020, 'Explore the major events that shaped our world from ancient times to today.', 'library/books/sample7.pdf', NULL),
(5, 'Ancient Civilizations', 'Michael Davis', 2019, 'Discover the fascinating cultures and achievements of ancient peoples.', 'library/books/sample8.pdf', NULL),
(8, 'The Art of Painting', 'Emily Wilson', 2021, 'Learn painting techniques from basic to advanced levels.', 'library/books/sample9.pdf', NULL),
(9, 'Steve Jobs: A Biography', 'Walter Isaacson', 2011, 'The authorized biography of the Apple co-founder.', 'library/books/sample10.pdf', NULL),
(10, 'English Grammar Guide', 'Prof. David Lee', 2022, 'A complete reference guide for English grammar and usage.', 'library/books/sample11.pdf', NULL),
(3, 'Chemistry Fundamentals', 'Dr. Lisa Chen', 2023, 'Essential chemistry concepts for students and enthusiasts.', 'library/books/sample12.pdf', NULL);

-- Verify the data was inserted
SELECT COUNT(*) as 'Total Books' FROM BOOK;
SELECT b.title, c.category_name, b.author, b.year_published 
FROM BOOK b 
JOIN CATEGORY c ON b.category_id = c.category_id 
ORDER BY b.title;

-- Create some sample access logs
INSERT INTO ACCESS_LOG (student_id, book_id, access_type) VALUES
(1, 1, 'VIEW'),
(1, 1, 'DOWNLOAD'),
(1, 3, 'VIEW'),
(2, 1, 'VIEW'),
(2, 2, 'VIEW'),
(2, 2, 'DOWNLOAD'),
(3, 6, 'VIEW'),
(3, 6, 'DOWNLOAD');

SELECT 'Sample data inserted successfully!' as Status;
