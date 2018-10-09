package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EmailControl extends TextField {

    static int emailId;
    static Boolean dftEmail=false;
    static Boolean fwdEmail=false;
    static Boolean rpyEmail=false;


    Boolean draftEmail=false;
    Boolean forwardEmail=false;
    Boolean replyEmail=false;

    DatabaseHelper dbHelper;

    @FXML
    private TextField recipientTextField;

    @FXML
    private TextField subjectTextField;

    @FXML
    private TextArea contentTextField;

    @FXML
    private Button discardButton;

    public void initialize(){
        dbHelper= new DatabaseHelper();
        if(dftEmail){
            draftEmail=true;
            String[] emailContent=dbHelper.getEmailContent(emailId);
            recipientTextField.setText(emailContent[1]);
            subjectTextField.setText(emailContent[5]);
            contentTextField.setText(emailContent[6]);

            dftEmail=false;
        }
        if(fwdEmail){
            forwardEmail=true;
            String[] emailContent=dbHelper.getEmailContent(emailId);
            subjectTextField.setText("Fwd: "+emailContent[5]);
            contentTextField.setText("\n\n--------------Forwarding------------------\nFrom: "
                    +emailContent[2]+" ("+emailContent[3]+")\n"
                    +"Sent: "+emailContent[4]+"\nSubject: "+emailContent[5]+"\n\n"+emailContent[6]);

            fwdEmail=false;
        }

        if(rpyEmail){
            replyEmail=true;
            String[] emailContent=dbHelper.getEmailContent(emailId);
            recipientTextField.setText(emailContent[3]);
            subjectTextField.setText("Re: "+emailContent[5]);
            contentTextField.setText("\n\n------------Replying To-----------------\nFrom: "
                    +emailContent[2]+" ("+emailContent[3]+")\n"
                    +"Sent: "+emailContent[4]+"\nSubject: "+emailContent[5]+"\n\n"+emailContent[6]);

            rpyEmail=false;
        }


    }

    public void send(ActionEvent evt) throws IOException{

        String recipient = recipientTextField.getText();
        String subject = subjectTextField.getText();
        String content = contentTextField.getText();
        //String[] j = (String[]) Arrays.stream(recipient.split(" ")).distinct().toArray();
        if(draftEmail){
            //;recipient.split(" ")
            for (String retval: Arrays.stream(recipient.split(" +")).distinct().collect(Collectors.toList())) {
                if(!dbHelper.updateEmail(emailId, retval, subject, content, 0)){
                    AlertBox("Email Error", "Invalid Email");
                    return;
                }
            }
            Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
            Scene accountscene = new Scene(account);
            accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

            Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

            window.setScene(accountscene);
            window.show();
        } else {
            for (String retval:  Arrays.stream(recipient.split(" +")).distinct().collect(Collectors.toList())) {
                if(!dbHelper.sendEmail(GUIControl.userId,retval,subject, content, 0)){
                    AlertBox("Email Error", "Invalid Email");
                    return;
                }
            }
                Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
                Scene accountscene = new Scene(account);
                accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

                Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

                window.setScene(accountscene);
                window.show();

        }

    }

    public void draft(ActionEvent evt) throws IOException{

        String recipient = recipientTextField.getText();
        String subject = subjectTextField.getText();
        String content = contentTextField.getText();
        if(draftEmail){
            if(dbHelper.updateEmail(emailId,recipient,subject, content, 1)){
                Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
                Scene accountscene = new Scene(account);
                accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

                Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

                window.setScene(accountscene);
                window.show();
            }else{
                AlertBox("Email Error", "Invalid Email");
            }
        }else{
            if(dbHelper.sendEmail(GUIControl.userId,recipient,subject, content, 1)){
                Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
                Scene accountscene = new Scene(account);
                accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

                Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

                window.setScene(accountscene);
                window.show();
            }else{
                AlertBox("Email Error", "Invalid Email");
            }
        }

    }

    public void discard(ActionEvent evt) throws IOException{
        System.out.println("Discard email.");
        if(draftEmail){
            dbHelper.deleteEmail(emailId,1);
        }
            Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
            Scene accountscene = new Scene(account);
            accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

            Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

            window.setScene(accountscene);
            window.show();

    }

    private void AlertBox(String title, String message){
        Stage window = new Stage();

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setWidth(200);

        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("OK");
        closeButton.setOnAction(e -> window.close());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

    }


}

