-- to run this file: mysql -u <user> -p <your database name> < initialize_data.sql
-- =====================================================
-- USE YOUR DATABASE
-- =====================================================
-- USE <your database name>
USE cs157a_library_system;

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
('Mike Nelson', 'pass112', 'Staff', 'mike.nelson@sjsu.edu', '555-0115', '1212 Fig St, Santa Clara, CA'),
('Nora Patel', 'pass113', 'Member', 'nora.patel@sjsu.edu', '555-0116', '1313 Grove St, San Jose, CA'),
('Omar Rivera', 'pass114', 'Member', 'omar.rivera@sjsu.edu', '555-0117', '1414 Hill Ave, Santa Clara, CA'),
('Priya Shah', 'pass115', 'Member', 'priya.shah@sjsu.edu', '555-0118', '1515 Lake Dr, Cupertino, CA'),
('Ryan Chen', 'pass116', 'Member', 'ryan.chen@sjsu.edu', '555-0119', '1616 Mission St, Sunnyvale, CA'),
('Sofia Martinez', 'pass117', 'Member', 'sofia.martinez@sjsu.edu', '555-0120', '1717 Park Ln, Milpitas, CA');

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


INSERT INTO Books (ISBN, Status) VALUES
('9780743273565', 'Available'),
('9780061120084', 'Available'),
('9780452284234', 'Borrowed'),
('9780141439518', 'Available'),
('9780316769480', 'Borrowed'),
('9780142437247', 'Available'),
('9780143035008', 'Available'),
('9780547928227', 'Borrowed'),
('9780143058144', 'Available'),
('9780062502174', 'Borrowed'),
('9780439708180', 'Borrowed'),
('9780385504205', 'Available'),
('9780439023481', 'Borrowed'),
('9780307387899', 'Available'),
('9780307588364', 'Borrowed');

INSERT INTO BorrowRecords (BookID, UserID, BorrowDate, ReturnDate, FineAmount) VALUES
(1, 2, '2025-04-01 10:00:00', '2025-04-14 10:00:00', 0.00),
(2, 5, '2025-04-02 11:00:00', '2025-04-17 11:00:00', 0.25),
(3, 8, '2025-04-03 12:00:00', NULL, 0.00),
(4, 11, '2025-04-04 13:00:00', '2025-04-17 13:00:00', 0.00),
(5, 14, '2025-04-05 14:00:00', NULL, 0.00),
(6, 1, '2025-04-06 09:00:00', '2025-04-21 09:00:00', 0.25),
(7, 3, '2025-04-07 10:00:00', '2025-04-20 10:00:00', 0.00),
(8, 4, '2025-04-08 11:00:00', NULL, 0.00),
(9, 6, '2025-04-09 12:00:00', '2025-04-22 12:00:00', 0.00),
(10, 7, '2025-04-10 13:00:00', NULL, 0.00),
(11, 9, '2025-04-11 14:00:00', NULL, 0.00),
(12, 10, '2025-04-12 09:00:00', '2025-04-25 09:00:00', 0.00),
(13, 12, '2025-04-13 10:00:00', NULL, 0.00),
(14, 13, '2025-04-14 11:00:00', '2025-04-29 11:00:00', 0.25),
(15, 15, '2025-04-15 12:00:00', NULL, 0.00);

INSERT INTO PaymentRecords (UserID, PaymentAmount, PaymentDate) VALUES
(5, 0.25, '2025-04-17 15:00:00'),
(1, 0.25, '2025-04-21 14:00:00'),
(13, 0.25, '2025-04-29 16:00:00'),
(8, 15.00, '2025-05-01 10:30:00'),
(14, 20.00, '2025-05-02 11:45:00'),
(4, 5.00, '2025-05-03 09:15:00'),
(7, 12.50, '2025-05-04 13:20:00'),
(9, 50.00, '2025-05-05 16:10:00'),
(12, 10.00, '2025-05-06 12:00:00'),
(15, 25.00, '2025-05-07 14:35:00'),
(8, 10.00, '2025-05-08 09:25:00'),
(14, 5.00, '2025-05-09 15:40:00'),
(4, 7.50, '2025-05-10 10:05:00'),
(7, 10.00, '2025-05-11 11:30:00'),
(12, 15.00, '2025-05-12 13:50:00');
