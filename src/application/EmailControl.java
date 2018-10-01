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

public class EmailControl extends TextField {

    DatabaseHelper dbHelper;

    @FXML
    private TextField recipientTextField;

    @FXML
    private TextField subjectTextField;

    @FXML
    private TextArea contentTextField;

    public void initialize(){
        dbHelper= new DatabaseHelper();
    }

    public void send(ActionEvent evt) throws IOException{

        String recipient = recipientTextField.getText();
        String subject = subjectTextField.getText();
        String content = contentTextField.getText();
        if(dbHelper.sendEmail(GUIControl.userId,recipient,subject, content, 0)){
            Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
            Scene accountscene = new Scene(account);

            Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

            window.setScene(accountscene);
            window.show();
        }else{
            AlertBox("Email Error", "Invalid Email");
        }
    }

    public void draft(ActionEvent evt) throws IOException{

        String recipient = recipientTextField.getText();
        String subject = subjectTextField.getText();
        String content = contentTextField.getText();
        if(dbHelper.sendEmail(GUIControl.userId,recipient,subject, content, 1)){
            Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
            Scene accountscene = new Scene(account);

            Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

            window.setScene(accountscene);
            window.show();
        }else{
            AlertBox("Email Error", "Invalid Email");
        }
    }

    public void discard(ActionEvent evt) throws IOException{
        System.out.println("Discard email.");
        Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        Scene accountscene = new Scene(account);

        Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

        window.setScene(accountscene);
        window.show();
    }

    private void AlertBox(String title, String message){
        Stage window = new Stage();

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

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

