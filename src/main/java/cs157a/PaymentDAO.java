package cs157a;

public class PaymentDAO {
    // PaymentDAO.java

    // Record a payment for a specific borrow record (fine payment)
    boolean recordPayment(long borrowRecordId, double amount, LocalDate paymentDate);

    // Get all payments made by a user (join with BorrowRecords)
    List<Payment> getPaymentsByUser(int userId);
}
