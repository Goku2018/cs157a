package cs157a;

public class BorrowRecordDAO {
    // BorrowRecordDAO.java

    // Checkout a book (create borrow record, update book status)
    boolean checkoutBook(int bookId, int userId, LocalDate dueDate);

    // Return a book (calculate fine, update return date and book status)
    boolean returnBook(long recordId);

    // Get all currently borrowed books (ReturnDate IS NULL)
    List<BorrowRecord> getActiveBorrowings();

    // Get borrowing history for a specific user
    List<BorrowRecord> getBorrowingsByUser(int userId);

    // Calculate fine for a borrow record (based on due date and return date)
    double calculateFine(long recordId);
}
