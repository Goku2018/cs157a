package cs157a;

public class UserDAO {
// UserDAO.java

    // Get all members (Status = 'Member')
    List<User> getAllMembers();

    // Find user by email (used for login)
    User getUserByEmail(String email);

    // Register a new user (staff or member)
    boolean registerUser(User user);

    // Update user info (name, email, phone, etc.)
    boolean updateUser(User user);

    // Delete a user (only if no active borrowings)
    boolean deleteUser(int userId);



}
