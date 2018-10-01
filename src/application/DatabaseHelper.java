package application;

import java.sql.*;
import java.util.Date;
import java.text.*;

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

    public String getEmailAddress (int userId){
        String emailAddress="";
        String localPart="";
        String domain="";
        String sql = "SELECT localPart, domain FROM UsersTable WHERE id="+userId;

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            if (!rs.isBeforeFirst()){
                return null;
            }else{
                localPart=rs.getString("localPart");
                domain=rs.getString("domain");
                emailAddress=localPart+"@"+domain;
                return emailAddress;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //queries db for email, if it exists, it returns the userId
    public int getUserId(String emailAddress){
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

    //sets up account and returns userId
    public int setupNewAccount(String firstName, String lastName, String emailAddress, String password){
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
        System.out.println("ACCOUNT CREATED: "+ getUserId(emailAddress));
        return getUserId(emailAddress);
    }

    //sets up account and returns userId
    public boolean sendEmail(int senderUserId, String recipient, String subject, String content, int emailStatus){
        int recipientUserId = getUserId(recipient);
        if (recipientUserId==0){
            return false;
        }else{
            Date date = new Date();
            long dateNum = date.getTime();

            SimpleDateFormat ft =
                    new SimpleDateFormat ("MMM dd, yyyy 'at' hh:mm:ss a zzz");

            String dateText= ft.format(date);

            String sql = "INSERT INTO EmailsTable(subject,content,emailStatus,senderId,recipientId,dateText,dateInteger) VALUES(?,?,?,?,?,?,?)";

            try (Connection conn = this.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, subject);
                pstmt.setString(2, content);
                pstmt.setInt(3, emailStatus);
                pstmt.setInt(4, senderUserId);
                pstmt.setInt(5, recipientUserId);
                pstmt.setString(6, dateText);
                pstmt.setLong(7, dateNum);

                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
            if(emailStatus==0){
                System.out.println("Email sent with Subject: "+subject+" and Content: "+content);
            }else{
                System.out.println("Email draft saved with Subject:"+subject+" and Content: "+content);
            }
            return true;
        }

    }
}
