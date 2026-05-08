<<<<<<< HEAD:src/main/resources/sql tables.txt
USE librarydb;
=======
--to run this file: mysql -u <user> -p <your database name> < create_schema.sql

-- =====================================================
-- USE YOUR DATABASE
-- =====================================================
-- USE <your database name>
>>>>>>> 502e65c0f9102f735f8b0337347bbd25f04570ab:src/main/resources/create_schema.sql

-- =====================================================
-- DROP EXISTING TABLES (in correct order due to foreign keys)
-- =====================================================
DROP TABLE IF EXISTS PaymentRecords;
DROP TABLE IF EXISTS BorrowRecords;
DROP TABLE IF EXISTS Books;
DROP TABLE IF EXISTS ISBNs;
DROP TABLE IF EXISTS Users;

-- =====================================================
-- TABLE 1: USERS
-- =====================================================
CREATE TABLE Users
(
  UserID INT unsigned AUTO_INCREMENT,
  FullName VARCHAR(150) NOT NULL,
  Password VARCHAR(30) NOT NULL,
  Status VARCHAR(30) NOT NULL,
  Email VARCHAR(150),
  Phone VARCHAR(20),
  Address VARCHAR(150),
  RegistrationDate DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (UserID)
);


INSERT INTO Users (FullName, Password, Status, Email, Phone, Address) VALUES
('John Smith', 'pass123', 'Member', 'john.smith@sjsu.edu', '555-0101', '123 Main St, San Jose, CA'),
('Jane Doe', 'pass456', 'Member', 'jane.doe@sjsu.edu', '555-0102', '456 Oak Ave, Santa Clara, CA'),
('Bob Johnson', 'pass789', 'Member', 'bob.johnson@sjsu.edu', '555-0103', '789 Pine Rd, Cupertino, CA'),
('Alice Brown', 'pass101', 'Member', 'alice.brown@sjsu.edu', '555-0104', '101 Elm St, Sunnyvale, CA'),
('Charlie Davis', 'pass202', 'Member', 'charlie.davis@sjsu.edu', '555-0105', '202 Maple Dr, Milpitas, CA'),
('Diana Evans', 'pass303', 'Member', 'diana.evans@sjsu.edu', '555-0106', '303 Cedar Ln, Fremont, CA'),
('Edward Ford', 'pass404', 'Member', 'edward.ford@sjsu.edu', '555-0107', '404 Birch Way, San Jose, CA'),
('Fiona Green', 'pass505', 'Member', 'fiona.green@sjsu.edu', '555-0108', '505 Spruce Ct, Santa Clara, CA'),
('George Harris', 'pass606', 'Member', 'george.harris@sjsu.edu', '555-0109', '606 Willow St, Cupertino, CA'),
('Hannah Ingram', 'pass707', 'Member', 'hannah.ingram@sjsu.edu', '555-0110', '707 Ash Ave, Sunnyvale, CA'),
('Ian Jones', 'pass808', 'Staff', 'ian.jones@sjsu.edu', '555-0111', '808 Beech St, San Jose, CA'),
('Julia Kelly', 'pass909', 'Staff', 'julia.kelly@sjsu.edu', '555-0112', '909 Cedar Ave, Milpitas, CA'),
('Kevin Lewis', 'pass010', 'Staff', 'kevin.lewis@sjsu.edu', '555-0113', '1010 Date Dr, Fremont, CA'),
('Laura Miller', 'pass111', 'Staff', 'laura.miller@sjsu.edu', '555-0114', '1111 Elder Ln, San Jose, CA'),
('Mike Nelson', 'pass112', 'Staff', 'mike.nelson@sjsu.edu', '555-0115', '1212 Fig St, Santa Clara, CA');

-- =====================================================
-- TABLE 2: ISBNS
-- =====================================================
CREATE TABLE ISBNs
(
  ISBN VARCHAR(13),
  Title VARCHAR(150),
  Author VARCHAR(150),
  Genre VARCHAR(30),
  PRIMARY KEY (ISBN)
);


INSERT INTO ISBNs (ISBN, Title, Author, Genre) VALUES
('9780743273565', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Fiction'),
('9780061120084', 'To Kill a Mockingbird', 'Harper Lee', 'Classic'),
('9780452284234', '1984', 'George Orwell', 'Dystopian'),
('9780141439518', 'Pride and Prejudice', 'Jane Austen', 'Romance'),
('9780316769480', 'The Catcher in the Rye', 'J.D. Salinger', 'Fiction'),
('9780142437247', 'Moby Dick', 'Herman Melville', 'Adventure'),
('9780143035008', 'War and Peace', 'Leo Tolstoy', 'Historical'),
('9780547928227', 'The Hobbit', 'J.R.R. Tolkien', 'Fantasy'),
('9780143058144', 'Crime and Punishment', 'Fyodor Dostoevsky', 'Philosophical'),
('9780062502174', 'The Alchemist', 'Paulo Coelho', 'Fiction'),
('9780439708180', 'Harry Potter', 'J.K. Rowling', 'Fantasy'),
('9780385504205', 'The Da Vinci Code', 'Dan Brown', 'Mystery'),
('9780439023481', 'The Hunger Games', 'Suzanne Collins', 'Sci-Fi'),
('9780307387899', 'The Road', 'Cormac McCarthy', 'Post-apocalyptic'),
('9780307588364', 'Gone Girl', 'Gillian Flynn', 'Thriller');

-- =====================================================
-- TABLE 3: BOOKS (FIXED)
-- =====================================================
<<<<<<< HEAD:src/main/resources/sql tables.txt
CREATE TABLE Books (
  BookID INT unsigned NOT NULL AUTO_INCREMENT,
  ISBN VARCHAR(13) DEFAULT NULL,
  Status VARCHAR(100) NOT NULL,
  PRIMARY KEY (BookID),
  KEY idx_isbn (ISBN),
  CONSTRAINT fk_books_isbn FOREIGN KEY (ISBN) REFERENCES ISBNs (ISBN)
);

=======
CREATE TABLE Books
(
  BookID INT unsigned AUTO_INCREMENT,
  ISBN VARCHAR(13),
  CONSTRAINT isbn FOREIGN KEY (ISBN) REFERENCES ISBNs(ISBN) ON UPDATE CASCADE,
  Status VARCHAR(100) NOT NULL,
  PRIMARY KEY (BookID)
);


>>>>>>> 502e65c0f9102f735f8b0337347bbd25f04570ab:src/main/resources/create_schema.sql
INSERT INTO Books (ISBN, Status) VALUES
('9780743273565', 'Available'),
('9780061120084', 'Borrowed'),
('9780452284234', 'Available'),
('9780141439518', 'Available'),
('9780316769480', 'Borrowed'),
('9780142437247', 'Available'),
('9780143035008', 'Available'),
('9780547928227', 'Borrowed'),
('9780143058144', 'Available'),
('9780062502174', 'Available'),
('9780439708180', 'Borrowed'),
('9780385504205', 'Available'),
('9780439023481', 'Available'),
('9780307387899', 'Borrowed'),
('9780307588364', 'Available');

-- =====================================================
-- TABLE 4: BORROWRECORDS (FIXED)
-- =====================================================
<<<<<<< HEAD:src/main/resources/sql tables.txt
CREATE TABLE BorrowRecords (
  RecordID BIGINT unsigned NOT NULL AUTO_INCREMENT,
  BookID INT unsigned DEFAULT NULL,
  UserID INT unsigned DEFAULT NULL,
  BorrowDate DATETIME DEFAULT NULL,
  DueDate DATETIME NOT NULL,
  ReturnDate DATETIME DEFAULT NULL,
  FineAmount DECIMAL(6,2) DEFAULT NULL,
  PRIMARY KEY (RecordID),
  KEY idx_book (BookID),
  KEY idx_user (UserID),
  CONSTRAINT fk_borrow_book FOREIGN KEY (BookID) REFERENCES Books (BookID),
  CONSTRAINT fk_borrow_user FOREIGN KEY (UserID) REFERENCES Users (UserID)
);

INSERT INTO BorrowRecords (BookID, UserID, BorrowDate, DueDate, ReturnDate, FineAmount) VALUES
(1, 2, '2025-04-01 10:00:00', '2025-04-15 10:00:00', '2025-04-14 10:00:00', 0.00),
(2, 5, '2025-04-02 11:00:00', '2025-04-16 11:00:00', '2025-04-17 11:00:00', 0.25),
(3, 8, '2025-04-03 12:00:00', '2025-04-17 12:00:00', NULL, 0.00),
(4, 11, '2025-04-04 13:00:00', '2025-04-18 13:00:00', '2025-04-17 13:00:00', 0.00),
(5, 14, '2025-04-05 14:00:00', '2025-04-19 14:00:00', NULL, 0.00),
(6, 1, '2025-04-06 09:00:00', '2025-04-20 09:00:00', '2025-04-21 09:00:00', 0.25),
(7, 3, '2025-04-07 10:00:00', '2025-04-21 10:00:00', '2025-04-20 10:00:00', 0.00),
(8, 4, '2025-04-08 11:00:00', '2025-04-22 11:00:00', NULL, 0.00),
(9, 6, '2025-04-09 12:00:00', '2025-04-23 12:00:00', '2025-04-22 12:00:00', 0.00),
(10, 7, '2025-04-10 13:00:00', '2025-04-24 13:00:00', NULL, 0.00),
(11, 9, '2025-04-11 14:00:00', '2025-04-25 14:00:00', NULL, 0.00),
(12, 10, '2025-04-12 09:00:00', '2025-04-26 09:00:00', '2025-04-25 09:00:00', 0.00),
(13, 12, '2025-04-13 10:00:00', '2025-04-27 10:00:00', NULL, 0.00),
(14, 13, '2025-04-14 11:00:00', '2025-04-28 11:00:00', '2025-04-29 11:00:00', 0.25),
(15, 15, '2025-04-15 12:00:00', '2025-04-29 12:00:00', NULL, 0.00);
=======
CREATE TABLE BorrowRecords
(
  RecordID BIGINT unsigned AUTO_INCREMENT,
  BookID INT unsigned,
  CONSTRAINT bookid FOREIGN KEY (BookID) REFERENCES Books(BookID) ON UPDATE CASCADE ON DELETE SET NULL,
  UserID INT unsigned,
  CONSTRAINT userid FOREIGN KEY (UserID) REFERENCES Users(UserID) ON UPDATE CASCADE ON DELETE SET NULL,
  BorrowDate DATETIME,
  ReturnDate DATETIME,
  FineAmount DECIMAL(5,2),
  PRIMARY KEY (RecordID)
);


INSERT INTO BorrowRecords (BookID, UserID, BorrowDate, ReturnDate, FineAmount) VALUES
(2, 1, '2025-04-01 10:00:00', '2025-04-14 10:00:00', 0.00),
(5, 2, '2025-04-02 11:00:00', '2025-04-17 11:00:00', 0.25),
(8, 3, '2025-04-03 12:00:00', NULL, 0.00),
(11, 4, '2025-04-04 13:00:00', '2025-04-17 13:00:00', 0.00),
(14, 5, '2025-04-05 14:00:00', NULL, 0.00),
(1, 6, '2025-04-06 09:00:00', '2025-04-21 09:00:00', 0.25),
(3, 7, '2025-04-07 10:00:00', '2025-04-20 10:00:00', 0.00),
(4, 8, '2025-04-08 11:00:00', NULL, 0.00),
(6, 9, '2025-04-09 12:00:00', '2025-04-22 12:00:00', 0.00),
(7, 10, '2025-04-10 13:00:00', NULL, 0.00),
(9, 1, '2025-04-11 14:00:00', NULL, 0.00),
(10, 2, '2025-04-12 09:00:00', '2025-04-25 09:00:00', 0.00),
(12, 3, '2025-04-13 10:00:00', NULL, 0.00),
(13, 4, '2025-04-14 11:00:00', '2025-04-29 11:00:00', 0.25),
(15, 5, '2025-04-15 12:00:00', NULL, 0.00);
>>>>>>> 502e65c0f9102f735f8b0337347bbd25f04570ab:src/main/resources/create_schema.sql

-- =====================================================
-- TABLE 5: PAYMENTRECORDS
-- =====================================================
<<<<<<< HEAD:src/main/resources/sql tables.txt
CREATE TABLE PaymentRecords (
  PaymentID BIGINT unsigned NOT NULL AUTO_INCREMENT,
  BorrowRecordID BIGINT unsigned NOT NULL,
  PaymentAmount DECIMAL(6,2) DEFAULT NULL,
  PaymentDate DATETIME DEFAULT NULL,
  PRIMARY KEY (PaymentID),
  KEY idx_borrowrecord (BorrowRecordID),
  CONSTRAINT fk_payment_borrowrecord FOREIGN KEY (BorrowRecordID) REFERENCES BorrowRecords (RecordID)
=======
CREATE TABLE PaymentRecords
(
  PaymentID BIGINT unsigned AUTO_INCREMENT,
  UserID INT unsigned,
  CONSTRAINT useridpr FOREIGN KEY (UserID) REFERENCES Users(UserID) ON UPDATE CASCADE ON DELETE SET NULL,
  PaymentAmount DECIMAL(6,2),
  PaymentDate DATETIME,
  PRIMARY KEY (PaymentID)
>>>>>>> 502e65c0f9102f735f8b0337347bbd25f04570ab:src/main/resources/create_schema.sql
);


INSERT INTO PaymentRecords (UserID, PaymentAmount, PaymentDate) VALUES
(2, 0.25, '2025-04-17 15:00:00'),
(6, 0.25, '2025-04-21 14:00:00'),
(14, 0.25, '2025-04-29 16:00:00');

-- =====================================================
-- VERIFY ALL TABLES
-- =====================================================
SHOW TABLES;

-- Check counts
SELECT 'Users' AS TableName, COUNT(*) AS RowCount FROM Users
UNION ALL
SELECT 'ISBNs', COUNT(*) FROM ISBNs
<<<<<<< HEAD:src/main/resources/sql tables.txt
UNION ALL
SELECT 'Books', COUNT(*) FROM Books
UNION ALL
=======
UNION
SELECT 'Books', COUNT(*) FROM Books
UNION
>>>>>>> 502e65c0f9102f735f8b0337347bbd25f04570ab:src/main/resources/create_schema.sql
SELECT 'BorrowRecords', COUNT(*) FROM BorrowRecords
UNION ALL
SELECT 'PaymentRecords', COUNT(*) FROM PaymentRecords;