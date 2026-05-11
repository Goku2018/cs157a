package cs157a;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class BookDAO {

    private ISBNDAO isbnDAO = new ISBNDAO();

    // Base query used by all book read operations. Books are joined to ISBNs
    // because title, author, and genre live in the normalized ISBNs table.
    private static final String BOOK_SELECT =
            "SELECT b.BookID, b.ISBN, b.Status, i.Title, i.Author, i.Genre " +
                    "FROM Books b LEFT JOIN ISBNs i ON b.ISBN = i.ISBN";

    private String blankIfNull(String value) {
        return value == null ? "" : value;
    }

    private List<Book> convertResultSet(ResultSet books) throws SQLException{
        List<Book> bookList = new ArrayList<Book>();

        // Convert each ResultSet row from the joined query into a Book object.
        while (books.next()) {
            int bookid = books.getInt("BookID");
            String status = books.getString("Status");
            String isbn = blankIfNull(books.getString("ISBN"));
            String title = blankIfNull(books.getString("Title"));
            String author = blankIfNull(books.getString("Author"));
            String genre = blankIfNull(books.getString("Genre"));

            bookList.add(new Book(bookid, title, author, genre, isbn, status));
        }
        return bookList;
    }

    // Get all books from database
    List<Book> getAllBooks(){
        List<Book> allBooks = new ArrayList<Book>();

        // Open a connection, run the shared select query, and map the returned rows.
        try(Connection conn = DatabaseConnection.getConnection()){
            try(PreparedStatement getBooks = conn.prepareStatement(BOOK_SELECT + " ORDER BY b.BookID")){
                try (ResultSet books = getBooks.executeQuery()){
                    allBooks = convertResultSet(books);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to fetch books. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch books", e);
        }
        return allBooks;
    }

    Book getBookById(int bookId){
        // PreparedStatement binds BookID so the lookup is safe and exact.
        try(Connection conn = DatabaseConnection.getConnection()){
            try(PreparedStatement getBook = conn.prepareStatement(BOOK_SELECT + " WHERE b.BookID = ?")){
                getBook.setInt(1, bookId);
                try (ResultSet books = getBook.executeQuery()){
                    List<Book> matches = convertResultSet(books);
                    if(matches.isEmpty()){
                        return null;
                    }
                    return matches.get(0);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to fetch book. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch book", e);
        }
    }


    // Search books by title, author, or genre
    List<Book> searchBooks(String keyword, String searchType){
        List<Book> searchResults = new ArrayList<Book>();
        try(Connection conn = DatabaseConnection.getConnection()) {
            // Whitelist the searchable columns before adding the column name to SQL.
            Set<String> allowedColumns = Set.of("Title", "Author", "Genre");
            if(allowedColumns.contains(searchType)) {
                String query = BOOK_SELECT + " WHERE i." + searchType + " LIKE ? ORDER BY b.BookID";
                try (PreparedStatement getResults = conn.prepareStatement(query)) {
                    // Bind the keyword with wildcards so the database performs a contains search.
                    getResults.setString(1, "%" + keyword + "%");
                    try (ResultSet books = getResults.executeQuery()) {
                        searchResults = convertResultSet(books);
                    }
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to fetch books. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch books", e);
        }
        return searchResults;
    }

    // Add a new book (and ISBN if needed)
    boolean addBook(Book book){
        try(Connection conn = DatabaseConnection.getConnection()){
            // Books reference ISBNs, so create the ISBN row before inserting the book copy.
            ISBN isbn = isbnDAO.checkISBNExists(book.getIsbn());
            if(isbn == null){
                isbn = new ISBN(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre());
                if(!isbnDAO.registerISBN(isbn)){
                    return false;
                }
                else{
                    book.setIsbnObj(isbn);
                }
            }
            else{
                book.setIsbnObj(isbn);
            }
            // Insert the physical book copy after the referenced ISBN is guaranteed to exist.
            try (PreparedStatement insertBook = conn.prepareStatement("INSERT INTO Books (ISBN, Status) VALUES (?, ?)")) {
                insertBook.setString(1, book.getIsbn());
                insertBook.setString(2, book.getStatus());
                insertBook.executeUpdate();
                return true;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to add book. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to add book", e);
        }
    }

    // Update an existing book copy and its ISBN metadata.
    boolean updateBook(Book book){
        try(Connection conn = DatabaseConnection.getConnection()){
            // The ISBN metadata update and Books row update must succeed or fail together.
            conn.setAutoCommit(false);
            try{
                // Reuse the same connection inside ISBNDAO so all related writes stay in one transaction.
                ISBN isbn = isbnDAO.checkISBNExists(conn, book.getIsbn());
                if(isbn == null){
                    isbn = new ISBN(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre());
                    if(!isbnDAO.registerISBN(conn, isbn)){
                        conn.rollback();
                        return false;
                    }
                    book.setIsbnObj(isbn);
                }
                else{
                    isbn = new ISBN(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre());
                    if(!isbnDAO.updateISBN(conn, isbn)){
                        conn.rollback();
                        return false;
                    }
                    book.setIsbnObj(isbn);
                }

                // Update the book copy's ISBN and circulation status.
                try (PreparedStatement updateBook = conn.prepareStatement("UPDATE Books SET ISBN = ?, Status = ? WHERE BookID = ?")) {
                    updateBook.setString(1, isbn.getIsbn());
                    updateBook.setString(2, book.getStatus());
                    updateBook.setInt(3, book.getBookId());
                    int rowsAffected = updateBook.executeUpdate();
                    if(rowsAffected == 1){
                        conn.commit();
                        return true;
                    }
                    else{
                        conn.rollback();
                        return false;
                    }
                }
            }
            catch(Exception e){
                conn.rollback();
                throw e;
            }
            finally{
                conn.setAutoCommit(true);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to update book. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to update book", e);
        }
    }

    // Delete a book (only if not borrowed)
    boolean deleteBook(int bookId){
        try(Connection conn = DatabaseConnection.getConnection()){
            // First check status so an actively borrowed book cannot be deleted.
            try (PreparedStatement checkStatus = conn.prepareStatement("SELECT Status FROM Books WHERE BookID = ?")) {
                checkStatus.setInt(1, bookId);
                try(ResultSet statusSet = checkStatus.executeQuery()){
                    if(statusSet.next()){
                        String status = statusSet.getString("Status");
                        if(status.equals("Borrowed")){
                            return false;
                        }
                    }
                }
            }
            // Delete only the Books row; ISBN metadata remains unless cleaned up separately.
            try(PreparedStatement deleteBook = conn.prepareStatement("DELETE FROM Books WHERE BookID = ?")){
                deleteBook.setInt(1, bookId);
                int rowsAffected = deleteBook.executeUpdate();
                if(rowsAffected == 1){
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to delete book. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to delete book", e);
        }
    }
}
