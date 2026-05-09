package cs157a;

import java.time.LocalDate;

/**
 * Represents a payment made for a fine in the library system.
 * Can be linked either by borrow record ID (staff view) or user ID (member view).
 */
public class Payment {
    private long paymentId;
    private long borrowRecordId; //Foreign key
    private int userId;
    private double paymentAmount;
    private LocalDate paymentDate;

    //Constructor
    public  Payment() {}

    public Payment(long paymentId, long borrowRecordId, double paymentAmount, LocalDate paymentDate){
        this.paymentId = paymentId;
        this.borrowRecordId = borrowRecordId;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
    }

    public Payment(long paymentId, int userId, double paymentAmount, LocalDate paymentDate) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
    }

    //Getters and Setters
    public long getPaymentId(){
        return paymentId;
    }

    public void setPaymentId(long paymentId){
        this.paymentId = paymentId;
    }

    public long getBorrowRecordId(){
        return borrowRecordId;
    }

    public void setBorrowRecordId(long borrowRecordId){
        this.borrowRecordId = borrowRecordId;
    }

    public int getUserId(){
        return userId;
    }

    public void setUserId(int userId){
        this.userId = userId;
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
