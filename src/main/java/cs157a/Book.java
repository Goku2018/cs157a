package cs157a;

public class Book {
    private int bookId;
    private ISBN isbn;
    private String status;

    public Book() {
        isbn = new ISBN("");
    }

    public Book(int bookId, String title, String author, String genre, String isbn, String status) {
        this.bookId = bookId;
        this.isbn = new ISBN(isbn, title, author, genre);
        this.status = status;
    }

    public Book(String title, String author, String genre, String isbn, String status) {
        this.isbn = new ISBN(isbn, title, author, genre);
        this.status = status;
    }

    public Book(int bookId, ISBN isbn, String status) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.status = status;
    }

    // Getters and Setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public String getTitle() { return isbn.getTitle(); }
    public void setTitle(String title) { isbn.setTitle(title); }
    public String getAuthor() { return isbn.getAuthor(); }
    public void setAuthor(String author) { isbn.setAuthor(author); }
    public String getGenre() { return isbn.getGenre(); }
    public void setGenre(String genre) { isbn.setGenre(genre); }
    public String getIsbn() { return isbn.getIsbn(); }
    public void setIsbn(String isbn) {this.isbn.setIsbn(isbn);}
    public void setIsbnObj(ISBN isbn){this.isbn = isbn;}
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
