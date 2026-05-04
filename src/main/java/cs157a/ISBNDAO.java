package cs157a;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class ISBNDAO {
    public boolean registerISBN(ISBN isbn){
        try(Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement insertISBN = conn.prepareStatement("INSERT INTO ISBNs (ISBN, Title, Author, Genre) VALUES (?, ?, ?, ?)")) {
                insertISBN.setString(1, isbn.getIsbn());
                insertISBN.setString(2, isbn.getTitle());
                insertISBN.setString(3, isbn.getAuthor());
                insertISBN.setString(4, isbn.getGenre());
                insertISBN.executeUpdate();
                return true;
            }
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

    public List<ISBN> getAllISBNs(){
        List<ISBN> allISBNs = new ArrayList<ISBN>();
        try(Connection conn = DatabaseConnection.getConnection()){
            try(PreparedStatement getISBNs = conn.prepareStatement("SELECT * FROM ISBNs")){
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
        ISBN match = null;
        try(Connection conn = DatabaseConnection.getConnection()){
            try(PreparedStatement getISBN = conn.prepareStatement("SELECT * FROM ISBNs WHERE ISBN = ?")){
                getISBN.setString(1, isbn);
                try (ResultSet isbns = getISBN.executeQuery()){
                    while(isbns.next()){
                        String returnedISBN = isbns.getString("ISBN");
                        String title = isbns.getString("Title");
                        if(title == null){title = "";}
                        String author = isbns.getString("Author");
                        if(author == null){author = "";}
                        String genre = isbns.getString("Genre");
                        if(genre == null){genre = "";}
                        match = new ISBN(returnedISBN, title, author, genre);
                    }
                }
            }
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
        return match;
    }

    //no books can be using an ISBN for it to be deleted successfully (will return false if deletion not successful)
    public boolean deleteISBN(ISBN isbn){
        try(Connection conn = DatabaseConnection.getConnection()){
            try (PreparedStatement checkBooks = conn.prepareStatement("SELECT * FROM Books WHERE ISBN = ?")) {
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
