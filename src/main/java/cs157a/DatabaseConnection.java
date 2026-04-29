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

    //Static block to Load properties
    static{
        try(InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")){
            if(input == null){
                throw new RuntimeException("db.properties not found in resources");
            }
            Properties props = new Properties();
            props.load(input);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");

            //Test the connection
            try(Connection conn = getConnection()){
                System.out.println("Database connection initialized successfully");

            }
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }
    public static Connection getConnection() throws Exception{
        return DriverManager.getConnection(url, user, password);
    }
    //Check if user is in database
    public static String authenticate(String email, String password){
        String sql = "SELECT Status FROM Users WHERE Email = ? AND Password = ?";
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return rs.getString("Status");//Staff/Member
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;//Failed
    }
}
