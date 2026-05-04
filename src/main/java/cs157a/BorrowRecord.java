package cs157a;

import java.time.LocalDate;

public class BorrowRecord {
    private long paymentID;
    private long borrowRecordId; //Foreign key
    private double paymentAmount;
    private LocalDate paymentDate;

    //Constructor
    public  Payment(){}

    public Payment(long paymentId, long borrowRecordId, double paymentAmount, LocalDate paymentDate){
        this.paymentID = paymentId;
        this.borrowRecordId = borrowRecordId;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
    }

    //Getters and Setters
    public long getPaymentID(){
        return paymentID;
    }

    public void setPaymentID(long paymentId){
        this.paymentID = paymentId;
    }

    public long getBorrowRecordId(){
        return borrowRecordId;
    }

    public void setBorrowRecordId(long borrowRecordId){
        this.borrowRecordId = borrowRecordId;
    }

    public double getPaymentAmount(){
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount){
        this.paymentAmount = paymentAmount;
    }

    public LocalDate getPaymentDate(){
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate){
        this.paymentDate = paymentDate;
    }

}
