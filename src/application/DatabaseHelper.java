package application;

import java.sql.*;

public class DatabaseHelper {

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:sqlite/db/EmailClientDatabase.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    //queries db for email, if it exists, it returns the userId
    public int getEmailAccount (String emailAddress){
        int atSplit= emailAddress.indexOf('@');
        String localPart=emailAddress.substring(0,atSplit);
        String domain=emailAddress.substring(atSplit+1);
        String sql = "SELECT id, password, localPart, domain FROM UsersTable WHERE localPart='"+localPart+"' AND domain='"+domain+"'";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            if (!rs.isBeforeFirst()){
                return 0;
            }else{
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
    }

    //queries db for password entered for userId to see if they match
    public Boolean checkPassword(int userId, String password) {
        String sql = "SELECT id, password FROM UsersTable WHERE id="+userId+" AND password='"+password+"'";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            if (!rs.isBeforeFirst()){
                return false;
            }else{
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void setupNewAccount(String firstName, String lastName, String emailAddress, String password){
        int atSplit= emailAddress.indexOf('@');
        String localPart=emailAddress.substring(0,atSplit);
        String domain=emailAddress.substring(atSplit+1);

        String sql = "INSERT INTO UsersTable(firstName,lastName,localPart,domain,password) VALUES(?,?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, localPart);
            pstmt.setString(4, domain);
            pstmt.setString(5, password);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}