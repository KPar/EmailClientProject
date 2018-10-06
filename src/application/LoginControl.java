package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class LoginControl {
    DatabaseHelper dbHelper;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;


	public void signIn(ActionEvent event) throws IOException {
        String emailAddress="";
        String password="";
        dbHelper= new DatabaseHelper();
	    //change this to check usernames in the database.
	    if(usernameTextField.getText().isEmpty()){
	        AlertBox("Username Error", "No Username entered.");
	        return;
        }else if(!usernameTextField.getText().endsWith("@yg.com")){
	        AlertBox("Username Error", "Must have '@yg.com' at the end.");
	        return;
        }else {
	        emailAddress=usernameTextField.getText();
        }

        //change this to check passwords.
        if(passwordTextField.getText().isEmpty()){
            AlertBox("Password Error", "No password entered.");
            return;
        }else{
            password=passwordTextField.getText();
        }
        if(dbHelper.getUserId(emailAddress).length!=0){
        int userId = dbHelper.getUserId(emailAddress)[0];

            if(dbHelper.checkPassword(userId,password)){
                GUIControl.userId=userId;

                Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
                Scene accountscene = new Scene(account);

                Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

                window.setScene(accountscene);
                window.show();
            }else{
                AlertBox("Password Error", "Incorrect Password.");
                return;
            }
        }else{
            AlertBox("Email Address Error", "Email does not exist.");
            return;
        }


		
	}

    public void signUp(ActionEvent event) throws IOException {
        Parent account = FXMLLoader.load(getClass().getResource("Sign-up.fxml"));
        Scene accountscene = new Scene(account);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(accountscene);
        window.show();

    }

    private void AlertBox(String title, String message){
        Stage window = new Stage();

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(250);

        javafx.scene.control.Label label = new javafx.scene.control.Label();
        label.setText(message);
        javafx.scene.control.Button closeButton = new javafx.scene.control.Button("OK");
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
