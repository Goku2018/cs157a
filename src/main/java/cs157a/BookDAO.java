package cs157a;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class BookDAO {

    // BookDAO.java
    private ISBNDAO isbnDAO;
    private List<Book> convertResultSet(ResultSet books) throws Exception{
        List<Book> bookList = new ArrayList<Book>();
        try(Connection conn = DatabaseConnection.getConnection()) {
            while (books.next()) {
                int bookid = books.getInt("BookID");
                String status = books.getString("Status");
                String isbn = books.getString("ISBN");
                ISBN isbnObj;
                if(isbn == null){
                    isbnObj = new ISBN("");
                }
                else{
                    isbnObj = isbnDAO.checkISBNExists(isbn);
                }
                bookList.add(new Book(bookid, isbnObj, status));
            }
        }
        return bookList;
    }

    // Get all books from database
    List<Book> getAllBooks(){
        List<Book> allBooks = new ArrayList<Book>();
        try(Connection conn = DatabaseConnection.getConnection()){
            try(PreparedStatement getBooks = conn.prepareStatement("SELECT * FROM Books")){
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

    // Search books by title, author, or genre
    List<Book> searchBooks(String keyword, String searchType){
        List<Book> searchResults = new ArrayList<Book>();
        try(Connection conn = DatabaseConnection.getConnection()) {
            Set<String> allowedColumns = Set.of("Title", "Author", "Genre");
            if(allowedColumns.contains(searchType)) {
                String query = "SELECT b.* FROM Books b JOIN ISBNs i ON b.ISBN = i.ISBN WHERE " + searchType + " LIKE ?";
                try (PreparedStatement getResults = conn.prepareStatement(query)) {
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
            ISBN isbn = isbnDAO.checkISBNExists(book.getIsbn());
            if(isbn == null){
                isbn = new ISBN(book.getIsbn(), book.getTitle(), book.getAuthor(), book.getGenre()));
                if(!isbnDAO.registerISBN(isbn)){
                    return false;
                }
                else{
                    book.setIsbnObj(isbn);
                }
            }
            try (PreparedStatement insertBook = conn.prepareStatement("INSERT INTO Books (ISBN, Status) VALUES ('?', '?')")) {
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

    // Update an existing book (will fail if updating ISBN to nonexistent one)
    boolean updateBook(Book book){
        try(Connection conn = DatabaseConnection.getConnection()){
            ISBN isbn = isbnDAO.checkISBNExists(book.getIsbn());
            if(isbn == null){//book doesn't have valid isbn; must register one
                return false;
            }
            try (PreparedStatement updateBook = conn.prepareStatement("UPDATE Books SET ISBN = ?, Status = ? WHERE BookID = ?")) {
                updateBook.setString(1, book.getIsbn());
                updateBook.setString(2, book.getStatus());
                updateBook.setInt(3, book.getBookId());
                int rowsAffected = updateBook.executeUpdate();
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
