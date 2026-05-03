package cs157a;

public class BookDAO {

    // BookDAO.java

    // Get all books from database
    List<Book> getAllBooks();

    // Search books by title, author, or genre
    List<Book> searchBooks(String keyword, String searchType);

    // Add a new book (and ISBN if needed)
    boolean addBook(Book book);

    // Update an existing book
    boolean updateBook(Book book);

    // Delete a book (only if not borrowed)
    boolean deleteBook(int bookId);

    // Change book status (Available/Borrowed/Damaged/Lost)
    boolean updateBookStatus(int bookId, String status);
}
