package cs157a;


public class ISBN {
    private String isbn;
    private String title;
    private String author;
    private String genre;

    public ISBN(String isbn, String title, String author, String genre){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
    }

    public ISBN(String isbn){
        this.isbn = isbn;
        this.title = "";
        this.author = "";
        this.genre = "";
    }

    public String getIsbn(){return isbn;}
    public void setIsbn(String isbn){this.isbn = isbn;}
    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}
    public String getAuthor(){return author;}
    public void setAuthor(String author){this.author = author;}
    public String getGenre(){return genre;}
    public void setGenre(String genre){this.genre = genre;}
}
