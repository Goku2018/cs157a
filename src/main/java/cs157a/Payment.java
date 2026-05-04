package cs157a;

import java.time.LocalDate;

public class BorrowRecord {
    private long recordId;
    private int bookId;
    private int userId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;


    //Constructor
    public  BorrowRecord(){}

    public BorrowRecord(long recordId, int bookId, int userId, LocalDate borrowDate,LocalDate dueDate, LocalDate returnDate, double fineAmount){
        this.recordId = recordId;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fineAmount = fineAmount;
    }

    //Getters and Setters

    public long getRecordId(){
        return recordId;
    }
    public void setRecordId(long recordId){
        this.recordId = recordId;
    }

    public int getBookId(){
        return bookId;
    }

    public void setBookId(int bookId){
        this.bookId = bookId;
    }

    public int getUserId(){
        return userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
    }

    public LocalDate getBorrowDate(){
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate){
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate(){
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate){
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate(){
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate){
        this.returnDate = returnDate;
    }

    public double getFineAmount(){
        return fineAmount;
    }

    public void setFineAmount(double fineAmount){
        this.fineAmount = fineAmount;
    }

}
