package application;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.*;
import java.util.List;

public class DatabaseHelper {

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:SnailMailDatabase.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);

            Statement stmt = conn.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS EmailsTable ( " +
                    "id INTEGER PRIMARY KEY, subject TEXT, " +
                    "content TEXT, emailStatus INTEGER, senderId INTEGER, recipientId INTEGER, " +
                    "dateText TEXT, dateInteger INTEGER" +
                    ");";
            stmt.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS UsersTable (" +
                    "id INTEGER PRIMARY KEY, password TEXT, firstName TEXT, " +
                    "lastName TEXT, localPart TEXT, domain TEXT" +
                    ");";
            stmt.execute(sql);

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

    public String getName (int userId){
        String sql = "SELECT firstName, lastName FROM UsersTable WHERE id="+userId;

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            if (!rs.isBeforeFirst()){
                return null;
            }else{
                String name=rs.getString("firstName")+" "+rs.getString("lastName");
                return name;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //queries db for email, if it exists, it returns the userId
    public int[] getUserId(String emailAddress){
        int[] result = new int[0];

        if(!emailAddress.contains("@")){
            return result;
        }
        int atSplit= emailAddress.indexOf('@');
        String localPart=emailAddress.substring(0,atSplit);
        String domain=emailAddress.substring(atSplit+1);
        String sql = "SELECT id, password, localPart, domain FROM UsersTable WHERE localPart='"+localPart+"' AND domain='"+domain+"'";

        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            if (!rs.isBeforeFirst()){
                return result;
            }else{
                result = new int[1];
                result[0]=rs.getInt("id");
                return result;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return result;
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
        return getUserId(emailAddress)[0];
    }

    public boolean sendEmail(int senderUserId, String recipient, String subject, String content, int emailStatus){
        int recipientUserId;
        if(getUserId(recipient).length!=0){
            recipientUserId = getUserId(recipient)[0];
        }else{
            return false;
        }

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
                System.out.println("Email sent to: "+recipient+" from "+getEmailAddress(senderUserId)+" with Subject: "+subject+" and Content: "+content);
            }else{
                System.out.println("Email draft saved to: "+recipient+" from "+getEmailAddress(senderUserId)+"  with Subject:"+subject+" and Content: "+content);
            }
            return true;
    }

    public boolean updateEmail(int emailId, String emailAddress, String subject, String content, int emailStatus){

        if(getUserId(emailAddress).length==0) {
            return false;
        }
        Date date = new Date();
        long dateNum = date.getTime();

        SimpleDateFormat ft =
                new SimpleDateFormat ("MMM dd, yyyy 'at' hh:mm:ss a zzz");

        String dateText= ft.format(date);
        String sql = "UPDATE EmailsTable SET recipientId = ? , "
                + "subject = ? , "
                + "content = ? , "
                + "emailStatus = ? , "
                + "dateText = ? , "
                + "dateInteger = ? "
                + "WHERE id = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, getUserId(emailAddress)[0]);
            pstmt.setString(2, subject);
            pstmt.setString(3, content);
            pstmt.setInt(4, emailStatus);
            pstmt.setString(5, dateText);
            pstmt.setLong(6, dateNum);
            pstmt.setInt(7, emailId);



            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public List getEmails(int userId, int currentFolder){
        String sql;
        switch (currentFolder){
            case 0:
                sql = "SELECT * FROM EmailsTable WHERE recipientId="+userId+" AND emailStatus="
                        +0+" ORDER BY dateInteger DESC";
                break;
            case 1:
                sql = "SELECT * FROM EmailsTable WHERE senderId="+userId+" AND emailStatus="
                        +0+" ORDER BY dateInteger DESC";
                break;
            case 2:
                sql = "SELECT * FROM EmailsTable WHERE senderId="+userId+" AND emailStatus="
                        +1+" ORDER BY dateInteger DESC";
                break;
            default:
                sql = "SELECT * FROM EmailsTable WHERE recipientId="+userId+" AND emailStatus="
                        +0+" ORDER BY dateInteger DESC";
                break;
        }

        List<String> list = new ArrayList<>();
        String data ="";
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            if (!rs.isBeforeFirst()){
                return null;
            }else{
                while((rs.next())){
                    data=getName(rs.getInt("senderId"))+"\n"+rs.getString("dateText")+"\n"
                            +rs.getString("subject");
                    list.add(data);
                }
                return list;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String[] getEmailContent(int userId, int emailPosition, int currentFolder){
        String sql;
        switch (currentFolder){
            case 0:
                sql = "SELECT * FROM EmailsTable WHERE recipientId="+userId+" AND emailStatus="
                        +0+" ORDER BY dateInteger DESC LIMIT "+emailPosition+",1";
                break;
            case 1:
                sql = "SELECT * FROM EmailsTable WHERE senderId="+userId+" AND emailStatus="
                        +0+" ORDER BY dateInteger DESC LIMIT "+emailPosition+",1";
                break;
            case 2:
                sql = "SELECT * FROM EmailsTable WHERE senderId="+userId+" AND emailStatus="
                        +1+" ORDER BY dateInteger DESC LIMIT "+emailPosition+",1";
                break;
            default:
                sql = "SELECT * FROM EmailsTable WHERE recipientId="+userId+" AND emailStatus="
                        +0+" ORDER BY dateInteger DESC LIMIT "+emailPosition+",1";
                break;
        }

        String[] emailContentArray = new String[6];
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            if (!rs.isBeforeFirst()){
                return null;
            }else{
                    emailContentArray[0]=getName(rs.getInt("senderId"));
                    emailContentArray[1]=getEmailAddress(rs.getInt("senderId"));
                    emailContentArray[2]=rs.getString("dateText");
                    emailContentArray[3]=rs.getString("subject");
                    emailContentArray[4]=rs.getString("content");
                    emailContentArray[5]=rs.getString("id");

                return emailContentArray;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String[] getEmailContent(int emailId){
        String sql = "SELECT * FROM EmailsTable WHERE id="+emailId;

        String[] emailContentArray = new String[7];
        try (Connection conn = this.connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            if (!rs.isBeforeFirst()){
                return null;
            }else{
                emailContentArray[0]=getName(rs.getInt("recipientId"));
                emailContentArray[1]=getEmailAddress(rs.getInt("recipientId"));
                emailContentArray[2]=getName(rs.getInt("senderId"));
                emailContentArray[3]=getEmailAddress(rs.getInt("senderId"));
                emailContentArray[4]=rs.getString("dateText");
                emailContentArray[5]=rs.getString("subject");
                emailContentArray[6]=rs.getString("content");
                return emailContentArray;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
