package cs157a;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {
    // BorrowRecordDAO.java

    private static final double DAILY_FINE_RATE = 0.25;
    private static final double MAX_FINE_AMOUNT = 50.00;

    // Checkout a book (create borrow record, update book status)
    //boolean checkoutBook(int bookId, int userId, LocalDate dueDate);
    boolean checkoutBook(int bookId, int userId, LocalDate dueDate) {
        String insertSql = "INSERT INTO BorrowRecords (BookID, UserID, BorrowDate, DueDate, FineAmount) VALUES (?, ?, NOW(), ?, 0.00)";
        String updateSql = "UPDATE Books SET Status = 'Borrowed' WHERE BookID = ? AND Status = 'Available'";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateBook = conn.prepareStatement(updateSql);
                 PreparedStatement insertRecord = conn.prepareStatement(insertSql)) {
                updateBook.setInt(1, bookId);
                if (updateBook.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }

                insertRecord.setInt(1, bookId);
                insertRecord.setInt(2, userId);
                insertRecord.setDate(3, Date.valueOf(dueDate));
                boolean success = insertRecord.executeUpdate() == 1;
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                return success;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check out book. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check out book", e);
        }
    }

    // Return a book (calculate fine, update return date and book status)
    boolean returnBook(long recordId){
        String findSql = "SELECT BookID FROM BorrowRecords WHERE RecordID = ?";
        String returnSql = "UPDATE BorrowRecords SET ReturnDate = NOW(), FineAmount = ? WHERE RecordID = ?";
        String bookSql = "UPDATE Books SET Status = ? WHERE BookID = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement findStmt = conn.prepareStatement(findSql);
                 PreparedStatement returnStmt = conn.prepareStatement(returnSql);
                 PreparedStatement bookStmt = conn.prepareStatement(bookSql)) {
                findStmt.setLong(1, recordId);
                int bookId;
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    bookId = rs.getInt("BookID");
                }

                double fine = calculateFine(recordId);
                returnStmt.setLong(2, recordId);
                returnStmt.setDouble(1, fine);
                bookStmt.setString(1, fine >= MAX_FINE_AMOUNT ? "Lost" : "Available");
                bookStmt.setInt(2, bookId);
                boolean success = returnStmt.executeUpdate() == 1 && bookStmt.executeUpdate() == 1;
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                return success;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to return book. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to return book", e);
        }
    }

    // Get all currently borrowed books (ReturnDate IS NULL)
    List<BorrowRecord> getActiveBorrowings(){
        return new ArrayList<>();
    }

    // Get borrowing history for a specific user
    List<BorrowRecord> getBorrowingsByUser(int userId){
        return new ArrayList<>();
    }

    // Calculate fine for a borrow record (based on due date and return date)
    double calculateFine(long recordId){
        String sql = "SELECT BookID, DueDate, ReturnDate FROM BorrowRecords WHERE RecordID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return 0.0;
                }

                int bookId = rs.getInt("BookID");
                Date dueDate = rs.getDate("DueDate");
                Timestamp returnDate = rs.getTimestamp("ReturnDate");
                if (dueDate == null) {
                    return 0.0;
                }

                LocalDate endDate = returnDate == null ? LocalDate.now() : returnDate.toLocalDateTime().toLocalDate();
                long overdueDays = ChronoUnit.DAYS.between(dueDate.toLocalDate(), endDate);
                if (overdueDays <= 0) {
                    return 0.0;
                }
                double fine = Math.min(overdueDays * DAILY_FINE_RATE, MAX_FINE_AMOUNT);
                if (fine >= MAX_FINE_AMOUNT && returnDate == null) {
                    markBookLost(bookId);
                }
                return fine;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate fine. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate fine", e);
        }
    }

    private void markBookLost(int bookId) {
        String sql = "UPDATE Books SET Status = 'Lost' WHERE BookID = ? AND Status = 'Borrowed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark book as lost. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark book as lost", e);
        }
    }

    BorrowRecord getBorrowRecordById(long recordId) {
        String sql = "SELECT 1 FROM BorrowRecords WHERE RecordID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapBorrowRecord(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find borrow record. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find borrow record", e);
        }
    }



    private BorrowRecord mapBorrowRecord(ResultSet rs) throws SQLException {
        BorrowRecord record = new BorrowRecord();
        record.setRecordId(rs.getLong("RecordID"));
        record.setBookId(rs.getInt("BookID"));
        record.setUserId(rs.getInt("UserID"));

        Timestamp borrowDate = rs.getTimestamp("BorrowDate");
        Timestamp dueDate = rs.getTimestamp("DueDate");
        Timestamp returnDate = rs.getTimestamp("ReturnDate");

        if (borrowDate != null) {
            record.setBorrowDate(LocalDate.from(borrowDate.toLocalDateTime()));
        }
        if (dueDate != null) {
            record.setDueDate(LocalDate.from(dueDate.toLocalDateTime()));
        }
        if (returnDate != null) {
            record.setReturnDate(LocalDate.from(returnDate.toLocalDateTime()));
        }

        record.setFineAmount(rs.getDouble("FineAmount"));
        return record;
    }


}


/*
package cs157a;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {
    // BorrowRecordDAO.java
    private static final double DAILY_FINE_RATE = 0.25;
    private static final double MAX_FINE_AMOUNT = 50.00;

    // Checkout a book (create borrow record, update book status)
    boolean checkoutBook(int bookId, int userId, LocalDate dueDate) {
        String insertSql = "INSERT INTO BorrowRecords (BookID, UserID, BorrowDate, DueDate, FineAmount) VALUES (?, ?, NOW(), ?, 0.00)";
        String updateSql = "UPDATE Books SET Status = 'Borrowed' WHERE BookID = ? AND Status = 'Available'";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement updateBook = conn.prepareStatement(updateSql);
                 PreparedStatement insertRecord = conn.prepareStatement(insertSql)) {
                updateBook.setInt(1, bookId);
                if (updateBook.executeUpdate() != 1) {
                    conn.rollback();
                    return false;
                }

                insertRecord.setInt(1, bookId);
                insertRecord.setInt(2, userId);
                insertRecord.setDate(3, Date.valueOf(dueDate));
                boolean success = insertRecord.executeUpdate() == 1;
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                return success;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check out book. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to check out book", e);
        }
    }

    // Return a book (calculate fine, update return date and book status)
    boolean returnBook(long recordId) {
        String findSql = "SELECT BookID FROM BorrowRecords WHERE RecordID = ?";
        String returnSql = "UPDATE BorrowRecords SET ReturnDate = NOW(), FineAmount = ? WHERE RecordID = ?";
        String bookSql = "UPDATE Books SET Status = ? WHERE BookID = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement findStmt = conn.prepareStatement(findSql);
                 PreparedStatement returnStmt = conn.prepareStatement(returnSql);
                 PreparedStatement bookStmt = conn.prepareStatement(bookSql)) {
                findStmt.setLong(1, recordId);
                int bookId;
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    bookId = rs.getInt("BookID");
                }

                double fine = calculateFine(recordId);
                returnStmt.setLong(2, recordId);
                returnStmt.setDouble(1, fine);
                bookStmt.setString(1, fine >= MAX_FINE_AMOUNT ? "Lost" : "Available");
                bookStmt.setInt(2, bookId);
                boolean success = returnStmt.executeUpdate() == 1 && bookStmt.executeUpdate() == 1;
                if (success) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
                return success;
            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to return book. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to return book", e);
        }
    }

    // Get all currently borrowed books (ReturnDate IS NULL)
    List<BorrowRecord> getActiveBorrowings() {
        return new ArrayList<>();
    }

    // Get borrowing history for a specific user
    List<BorrowRecord> getBorrowingsByUser(int userId) {
        return new ArrayList<>();
    }

    // Calculate fine for a borrow record (based on due date and return date)
    double calculateFine(long recordId) {
        String sql = "SELECT BookID, DueDate, ReturnDate FROM BorrowRecords WHERE RecordID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return 0.0;
                }

                int bookId = rs.getInt("BookID");
                Date dueDate = rs.getDate("DueDate");
                Timestamp returnDate = rs.getTimestamp("ReturnDate");
                if (dueDate == null) {
                    return 0.0;
                }

                LocalDate endDate = returnDate == null ? LocalDate.now() : returnDate.toLocalDateTime().toLocalDate();
                long overdueDays = ChronoUnit.DAYS.between(dueDate.toLocalDate(), endDate);
                if (overdueDays <= 0) {
                    return 0.0;
                }
                double fine = Math.min(overdueDays * DAILY_FINE_RATE, MAX_FINE_AMOUNT);
                if (fine >= MAX_FINE_AMOUNT && returnDate == null) {
                    markBookLost(bookId);
                }
                return fine;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate fine. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate fine", e);
        }
    }

    private void markBookLost(int bookId) {
        String sql = "UPDATE Books SET Status = 'Lost' WHERE BookID = ? AND Status = 'Borrowed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark book as lost. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark book as lost", e);
        }
    }

    boolean borrowRecordExists(long recordId) {
        String sql = "SELECT 1 FROM BorrowRecords WHERE RecordID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find borrow record. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find borrow record", e);
        }
    }
}
 */