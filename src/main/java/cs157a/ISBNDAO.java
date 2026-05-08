package cs157a;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class ISBNDAO {


    public boolean registerISBN(ISBN isbn){
        try(Connection conn = DatabaseConnection.getConnection()) {
            return registerISBN(conn, isbn);
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to register new ISBN. Database error. ISBN may already be registered.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to register new ISBN", e);
        }
    }

    boolean registerISBN(Connection conn, ISBN isbn) throws SQLException {
        try (PreparedStatement insertISBN = conn.prepareStatement("INSERT INTO ISBNs (ISBN, Title, Author, Genre) VALUES (?, ?, ?, ?)")) {
            insertISBN.setString(1, isbn.getIsbn());
            insertISBN.setString(2, isbn.getTitle());
            insertISBN.setString(3, isbn.getAuthor());
            insertISBN.setString(4, isbn.getGenre());
            return insertISBN.executeUpdate() == 1;
        }
    }

    public boolean updateISBN(ISBN isbn){
        try(Connection conn = DatabaseConnection.getConnection()) {
            return updateISBN(conn, isbn);
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to update ISBN. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to update ISBN", e);
        }
    }

    boolean updateISBN(Connection conn, ISBN isbn) throws SQLException {
        try (PreparedStatement updateISBN = conn.prepareStatement("UPDATE ISBNs SET Title = ?, Author = ?, Genre = ? WHERE ISBN = ?")) {
            updateISBN.setString(1, isbn.getTitle());
            updateISBN.setString(2, isbn.getAuthor());
            updateISBN.setString(3, isbn.getGenre());
            updateISBN.setString(4, isbn.getIsbn());
            return updateISBN.executeUpdate() == 1;
        }
    }

    public List<ISBN> getAllISBNs(){
        List<ISBN> allISBNs = new ArrayList<ISBN>();
        try(Connection conn = DatabaseConnection.getConnection()){
            try(PreparedStatement getISBNs = conn.prepareStatement("SELECT ISBN, Title, Author, Genre FROM ISBNs")){
                try (ResultSet isbns = getISBNs.executeQuery()){
                    while(isbns.next()){
                        String isbn = isbns.getString("ISBN");
                        String title = isbns.getString("Title");
                        if(title == null){title = "";}
                        String author = isbns.getString("Author");
                        if(author == null){author = "";}
                        String genre = isbns.getString("Genre");
                        if(genre == null){genre = "";}
                        allISBNs.add(new ISBN(isbn, title, author, genre));
                    }
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to fetch ISBNs. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch ISBNs", e);
        }
        return allISBNs;
    }

    //returns ISBN object if matching isbn exists in database, or null if not
    public ISBN checkISBNExists(String isbn){
        try(Connection conn = DatabaseConnection.getConnection()){
            return checkISBNExists(conn, isbn);
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to check ISBN. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to check ISBN", e);
        }
    }

    ISBN checkISBNExists(Connection conn, String isbn) throws SQLException {
        try(PreparedStatement getISBN = conn.prepareStatement("SELECT ISBN, Title, Author, Genre FROM ISBNs WHERE ISBN = ?")){
            getISBN.setString(1, isbn);
            try (ResultSet isbns = getISBN.executeQuery()){
                if(isbns.next()){
                    String returnedISBN = isbns.getString("ISBN");
                    String title = isbns.getString("Title");
                    if(title == null){title = "";}
                    String author = isbns.getString("Author");
                    if(author == null){author = "";}
                    String genre = isbns.getString("Genre");
                    if(genre == null){genre = "";}
                    return new ISBN(returnedISBN, title, author, genre);
                }
            }
        }
        return null;
    }

    //no books can be using an ISBN for it to be deleted successfully (will return false if deletion not successful)
    public boolean deleteISBN(ISBN isbn){
        try(Connection conn = DatabaseConnection.getConnection()){
            try (PreparedStatement checkBooks = conn.prepareStatement("SELECT 1 FROM Books WHERE ISBN = ?")) {
                checkBooks.setString(1, isbn.getIsbn());
                try(ResultSet statusSet = checkBooks.executeQuery()){
                    if(statusSet.next()){
                        return false;
                    }
                }
            }
            try(PreparedStatement deleteISBN = conn.prepareStatement("DELETE FROM ISBNs WHERE ISBN = ?")){
                deleteISBN.setString(1, isbn.getIsbn());
                int rowsAffected = deleteISBN.executeUpdate();
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
            throw new RuntimeException("Failed to delete ISBN. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to delete ISBN", e);
        }
    }
}
