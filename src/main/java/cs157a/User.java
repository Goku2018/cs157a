package cs157a;

import java.time.LocalDateTime;

/**
 * Represents a user in the library system.
 * Can be either a Staff member or a Library Member.
 * Stores personal information and login credentials.
 */
public class User {
    private int userId;
    private String fullName;
    private String password;
    private String status;   // "Staff" or "Member"
    private String email;
    private String phone;
    private String address;
    private LocalDateTime registrationDate;

    public User() {}

    /**
     * Constructor for creating a user with all fields.
     * Registration date is automatically set to current time.
     * @param userId The user's unique identifier
     * @param fullName The user's full name
     * @param email The user's email address (used for login)
     * @param password The user's password
     * @param status "Staff" or "Member"
     * @param phone The user's phone number
     * @param address The user's address
     */
    public User(int userId, String fullName, String password, String status, String email, String phone, String address) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.phone = phone;
        this.address = address;
        this.registrationDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
}


