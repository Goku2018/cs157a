package cs157a;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // Get all members (Status = 'Member')
    List<User> getAllMembers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE Status = 'Member' ORDER BY FullName";

        // Run a read-only query for member accounts and map each row to a User object.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch members. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch members", e);
        }
        return users;
    }

    // Find user by email (used for login)
    User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE Email = ?";

        // PreparedStatement binds the email entered in the UI.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                // Return the matching user row, or null if no account uses that email.
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user", e);
        }
        return null;
    }

    // Find one user by primary key.
    User getUserById(int userId){
        // Used by panels that only know the UserID from a borrow or payment record.
        try(Connection conn = DatabaseConnection.getConnection()){
            try(PreparedStatement getUser = conn.prepareStatement("SELECT * FROM Users WHERE UserID = ?")){
                getUser.setInt(1, userId);
                try (ResultSet users = getUser.executeQuery()){
                    List<User> matches = convertResultSet(users);
                    if(matches.isEmpty()){
                        return null;
                    }
                    return matches.get(0);
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            while(e.getNextException() != null){
                e.printStackTrace();
            }
            throw new RuntimeException("Failed to fetch user. Database error.", e);
        }
        catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch user", e);
        }
    }

    private List<User> convertResultSet(ResultSet users) throws Exception {
        List<User> userList = new ArrayList<>();
        // Convert every ResultSet row into a User model object.
        while (users.next()) {
            User user = new User();

            user.setUserId(users.getInt("UserID"));
            user.setFullName(users.getString("FullName"));
            user.setPassword(users.getString("Password"));
            user.setStatus(users.getString("Status"));
            user.setEmail(users.getString("Email"));
            user.setPhone(users.getString("Phone"));
            user.setAddress(users.getString("Address"));

            // Handle RegistrationDate (could be null)
            java.sql.Timestamp timestamp = users.getTimestamp("RegistrationDate");
            if (timestamp != null) {
                user.setRegistrationDate(timestamp.toLocalDateTime());
            }

            userList.add(user);
        }
        return userList;
    }

    // Register a new user (staff or member)
    boolean registerUser(User user) {
        // Enforce unique email at the application level before inserting.
        if (getUserByEmail(user.getEmail()) != null) {
            return false;
        }

        String sql = "INSERT INTO Users (FullName, Password, Status, Email, Phone, Address, RegistrationDate) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Insert all user fields with bound parameters to avoid SQL injection.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getStatus());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getAddress());
            stmt.setTimestamp(7, Timestamp.valueOf(user.getRegistrationDate()));
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register user. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user", e);
        }
    }

    // Update user info (name, email, phone, etc.)
    boolean updateUser(User user) {
        String sql = "UPDATE Users SET FullName = ?, Password = ?, Status = ?, Email = ?, Phone = ?, Address = ? WHERE UserID = ?";

        // Update the existing Users row identified by UserID.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getStatus());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            stmt.setString(6, user.getAddress());
            stmt.setInt(7, user.getUserId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    // Delete a user (only if no active borrowings)
    boolean deleteUser(int userId) {
        String activeSql = "SELECT 1 FROM BorrowRecords WHERE UserID = ? AND ReturnDate IS NULL";
        String deleteSql = "DELETE FROM Users WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check for active borrow records before allowing the user row to be deleted.
            try (PreparedStatement activeStmt = conn.prepareStatement(activeSql)) {
                activeStmt.setInt(1, userId);
                try (ResultSet rs = activeStmt.executeQuery()) {
                    if (rs.next()) {
                        return false;
                    }
                }
            }
            // Delete the user only after confirming they have no unreturned books.
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, userId);
                return deleteStmt.executeUpdate() == 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user. Database error.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        // Shared mapper for turning one Users ResultSet row into a User object.
        User user = new User();
        user.setUserId(rs.getInt("UserID"));
        user.setFullName(rs.getString("FullName"));
        user.setPassword(rs.getString("Password"));
        user.setStatus(rs.getString("Status"));
        user.setEmail(rs.getString("Email"));
        user.setPhone(rs.getString("Phone"));
        user.setAddress(rs.getString("Address"));
        Timestamp registrationDate = rs.getTimestamp("RegistrationDate");
        if (registrationDate != null) {
            user.setRegistrationDate(registrationDate.toLocalDateTime());
        }
        return user;
    }
}
