package cs157a;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    // Record a payment when the UI still supplies a borrow record ID.
    boolean recordPayment(long borrowRecordId, double amount, LocalDate paymentDate) {
        if (amount <= 0) {
            return false;
        }

        String userSql = "SELECT UserID FROM BorrowRecords WHERE RecordID = ?";

        // Convert borrow record ID to UserID because PaymentRecords stores payments by user.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(userSql)) {
            stmt.setLong(1, borrowRecordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return recordPaymentForUser(rs.getInt("UserID"), amount, paymentDate);
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to record payment. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to record payment", e);
        }
    }

    boolean recordPaymentForUser(int userId, double amount, LocalDate paymentDate) {
        // Reject zero or negative payments before opening a database connection.
        if (amount <= 0) {
            return false;
        }

        String sql = "INSERT INTO PaymentRecords (UserID, PaymentAmount, PaymentDate) VALUES (?, ?, ?)";

        // Insert one payment row for the user's overall fine balance.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setDouble(2, amount);
            stmt.setDate(3, Date.valueOf(paymentDate));
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to record payment. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to record payment", e);
        }
    }

    double getTotalPaidForBorrowRecord(long borrowRecordId) {
        String sql = "SELECT COALESCE(SUM(p.PaymentAmount), 0) AS TotalPaid " +
                "FROM BorrowRecords b JOIN PaymentRecords p ON b.UserID = p.UserID " +
                "WHERE b.RecordID = ?";

        // Join through BorrowRecords so older panels can ask by RecordID.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, borrowRecordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TotalPaid");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch paid amount. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch paid amount", e);
        }
        return 0.0;
    }

    double getTotalPaidForUser(int userId) {
        String sql = "SELECT COALESCE(SUM(PaymentAmount), 0) AS TotalPaid FROM PaymentRecords WHERE UserID = ?";

        // Sum all payments for a user to calculate remaining fine balance.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TotalPaid");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch paid amount. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch paid amount", e);
        }
        return 0.0;
    }

    // Get all payments made by a user.
    List<Payment> getPaymentsByUser(int userId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT PaymentID, UserID, PaymentAmount, PaymentDate " +
                "FROM PaymentRecords " +
                "WHERE UserID = ? " +
                "ORDER BY PaymentDate DESC, PaymentID DESC";

        // Fetch the user's payment history newest-first for the Payment History panel.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try(ResultSet rs = stmt.executeQuery()){
                // Map each payment row into a Payment object for the table model.
                while(rs.next()){
                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getLong("PaymentID"));
                    payment.setUserId(rs.getInt("UserID"));
                    payment.setPaymentAmount(rs.getDouble("PaymentAmount"));
                    Date paymentDate = rs.getDate("PaymentDate");
                    if(paymentDate != null){
                        payment.setPaymentDate(paymentDate.toLocalDate());
                    }
                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch payments. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch payments", e);
        }
        return payments;
    }
}
