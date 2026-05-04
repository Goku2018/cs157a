/*
package cs157a;

public class UserDAO {
// UserDAO.java

    // Get all members (Status = 'Member')
    List<User> getAllMembers(){
    }

    // Find user by email (used for login)
    User getUserByEmail(String email);

    // Register a new user (staff or member)
    boolean registerUser(User user);

    // Update user info (name, email, phone, etc.)
    boolean updateUser(User user);

    // Delete a user (only if no active borrowings)
    boolean deleteUser(int userId);



}
*/

package cs157a;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // UserDAO.java

    // Get all members (Status = 'Member')
    List<User> getAllMembers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE Status = 'Member' ORDER BY FullName";
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
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
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

    // Register a new user (staff or member)
    boolean registerUser(User user) {
        if (getUserByEmail(user.getEmail()) != null) {
            return false;
        }

        String sql = "INSERT INTO Users (FullName, Password, Status, Email, Phone, Address, RegistrationDate) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
            try (PreparedStatement activeStmt = conn.prepareStatement(activeSql)) {
                activeStmt.setInt(1, userId);
                try (ResultSet rs = activeStmt.executeQuery()) {
                    if (rs.next()) {
                        return false;
                    }
                }
            }
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