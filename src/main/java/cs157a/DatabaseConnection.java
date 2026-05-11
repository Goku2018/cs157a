package cs157a;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class DatabaseConnection {
    private static String url;
    private static String user;
    private static String password;

    // Load database connection settings once when the class is first used.
    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("db.properties not found in resources");
            }

            // Read JDBC URL, username, and password from the resources folder.
            Properties props = new Properties();
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            // Open and immediately close a test connection to catch setup errors early.
            try (Connection conn = getConnection()) {
                System.out.println("Database connection initialized successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    // Creates a new JDBC connection for each DAO operation.
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, password);
    }

    // Authenticates a login attempt and returns the full user row for dashboard routing.
    public static User authenticate(String email, String password) {
        String sql = "SELECT UserID, FullName, Password, Status, Email, Phone, Address, RegistrationDate FROM Users WHERE Email = ? AND Password = ?";

        // PreparedStatement safely binds user input into the login query.
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            // If the query returns one row, copy that ResultSet row into a User object.
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("UserID"));
                    user.setFullName(rs.getString("FullName"));
                    user.setPassword(rs.getString("Password"));
                    user.setStatus(rs.getString("Status"));
                    user.setEmail(rs.getString("Email"));
                    user.setPhone(rs.getString("Phone"));
                    user.setAddress(rs.getString("Address"));
                    java.sql.Timestamp registrationDate = rs.getTimestamp("RegistrationDate");
                    if (registrationDate != null) {
                        user.setRegistrationDate(registrationDate.toLocalDateTime());
                    }
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
