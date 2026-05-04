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

        String sql = "INSERT INTO PaymentRecords (BorrowRecordID, PaymentAmount, PaymentDate) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, borrowRecordId);
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
        String sql = "SELECT COALESCE(SUM(PaymentAmount), 0) AS TotalPaid FROM PaymentRecords WHERE BorrowRecordID = ?";
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

    // Get all payments made by a user (join with BorrowRecords)
    List<Payment> getPaymentsByUser(int userId) {
        return new ArrayList<>();
    }
}
