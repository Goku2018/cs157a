package cs157a;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BorrowRecordDAO {
    // BorrowRecordDAO.java

    private static final double DAILY_FINE_RATE = 0.25;
    private static final double MAX_FINE_AMOUNT = 50.00;

    void updateActiveFines() {
        String sql = "UPDATE BorrowRecords " +
                "SET FineAmount = LEAST(" +
                "GREATEST(DATEDIFF(CURDATE(), DATE_ADD(DATE(BorrowDate), INTERVAL 14 DAY)), 0) * ?, " +
                "?) " +
                "WHERE ReturnDate IS NULL";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, DAILY_FINE_RATE);
            stmt.setDouble(2, MAX_FINE_AMOUNT);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update active fines. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update active fines", e);
        }
    }

    // Checkout a book (create borrow record, update book status)
    boolean checkoutBook(int bookId, int userId) {
        String insertSql = "INSERT INTO BorrowRecords (BookID, UserID, BorrowDate, FineAmount) VALUES (?, ?, NOW(), 0.00)";
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
        String findSql = "SELECT BookID, ReturnDate FROM BorrowRecords WHERE RecordID = ?";
        String returnSql = "UPDATE BorrowRecords SET ReturnDate = NOW(), FineAmount = ? WHERE RecordID = ? AND ReturnDate IS NULL";
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
                    if(rs.getTimestamp("ReturnDate") != null){
                        conn.rollback();
                        return false;
                    }
                }

                double fine = calculateFine(recordId);
                returnStmt.setLong(2, recordId);
                returnStmt.setDouble(1, fine);
                bookStmt.setString(1, fine >= MAX_FINE_AMOUNT ? "Lost" : "Available");
                bookStmt.setInt(2, bookId);
                boolean success = returnStmt.executeUpdate() == 1;
                if (success) {
                    bookStmt.executeUpdate();
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
        return getBorrowRecords(null, null, true);
    }

    // Get borrowing history for a specific user
    List<BorrowRecord> getBorrowingsByUser(int userId){
        return getBorrowRecords(userId, null, false);
    }

    List<BorrowRecord> getBorrowRecords(Integer userId, Integer bookId, boolean activeOnly){
        updateActiveFines();
        List<BorrowRecord> records = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT RecordID, BookID, UserID, BorrowDate, ReturnDate, FineAmount FROM BorrowRecords WHERE 1 = 1");
        List<Integer> params = new ArrayList<>();

        if(userId != null){
            sql.append(" AND UserID = ?");
            params.add(userId);
        }
        if(bookId != null){
            sql.append(" AND BookID = ?");
            params.add(bookId);
        }
        if(activeOnly){
            sql.append(" AND ReturnDate IS NULL");
        }
        sql.append(" ORDER BY BorrowDate DESC, RecordID DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for(int i = 0; i < params.size(); i++){
                stmt.setInt(i + 1, params.get(i));
            }
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    records.add(mapBorrowRecord(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch borrow records. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch borrow records", e);
        }
        return records;
    }

    // Calculate fine for a borrow record (based on due date and return date)
    double calculateFine(long recordId){
        String sql = "SELECT BookID, BorrowDate, ReturnDate FROM BorrowRecords WHERE RecordID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, recordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return 0.0;
                }

                int bookId = rs.getInt("BookID");
                Timestamp borrowDate = rs.getTimestamp("BorrowDate");
                Timestamp returnDate = rs.getTimestamp("ReturnDate");
                if (borrowDate == null) {
                    return 0.0;
                }

                LocalDate dueDate = borrowDate.toLocalDateTime().toLocalDate().plusDays(14);
                LocalDate endDate = returnDate == null ? LocalDate.now() : returnDate.toLocalDateTime().toLocalDate();
                long overdueDays = ChronoUnit.DAYS.between(dueDate, endDate);
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
        updateActiveFines();
        String sql = "SELECT RecordID, BookID, UserID, BorrowDate, ReturnDate, FineAmount FROM BorrowRecords WHERE RecordID = ?";
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
        Timestamp returnDate = rs.getTimestamp("ReturnDate");

        if (borrowDate != null) {
            LocalDate borrowLocalDate = borrowDate.toLocalDateTime().toLocalDate();
            record.setBorrowDate(borrowLocalDate);
            record.setDueDate(borrowLocalDate.plusDays(14));
        }
        if (returnDate != null) {
            record.setReturnDate(returnDate.toLocalDateTime().toLocalDate());
        }

        record.setFineAmount(rs.getDouble("FineAmount"));
        return record;
    }
    // Placeholder for getUnpaidFines - returns empty list for now
    // Partner will implement real version with database query
    List<BorrowRecord> getUnpaidFines() {
        updateActiveFines();
        System.out.println("WARNING: getUnpaidFines() placeholder called - returns empty list");
        return new ArrayList<>();  // Returns empty list for now
    }

    Map<Integer, Double> getFineTotalsByUser() {
        updateActiveFines();
        Map<Integer, Double> fineTotals = new LinkedHashMap<>();
        String sql = "SELECT UserID, COALESCE(SUM(FineAmount), 0) AS TotalFines " +
                "FROM BorrowRecords " +
                "WHERE UserID IS NOT NULL AND FineAmount > 0 " +
                "GROUP BY UserID " +
                "ORDER BY UserID";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                fineTotals.put(rs.getInt("UserID"), rs.getDouble("TotalFines"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch fine totals. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch fine totals", e);
        }
        return fineTotals;
    }

}
