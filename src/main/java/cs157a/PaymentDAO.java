/*package cs157a;

import java.time.LocalDate;


public class PaymentDAO {
    List<Payment> getPaymentHistory();


}
*/

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
    // PaymentDAO.java

    // Record a payment for a specific borrow record (fine payment)
    boolean recordPayment(long borrowRecordId, double amount, LocalDate paymentDate) {
        if (amount <= 0) {
            return false;
        }

        String userSql = "SELECT UserID FROM BorrowRecords WHERE RecordID = ?";
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
        if (amount <= 0) {
            return false;
        }

        String sql = "INSERT INTO PaymentRecords (UserID, PaymentAmount, PaymentDate) VALUES (?, ?, ?)";
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

    // Get all payments made by a user (join with BorrowRecords)
    List<Payment> getPaymentsByUser(int userId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT PaymentID, UserID, PaymentAmount, PaymentDate " +
                "FROM PaymentRecords " +
                "WHERE UserID = ? " +
                "ORDER BY PaymentDate DESC, PaymentID DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    Payment payment = new Payment();
                    payment.setPaymentID(rs.getLong("PaymentID"));
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
