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

    public String getIsbn(){return isbn;}
    public String getTitle(){return title;}
    public String getAuthor(){return author;}
    public String getGenre(){return genre;}
}
