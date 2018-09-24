package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import javax.swing.*;
import java.io.IOException;

public class SignupControl extends TextField{

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private TextField confirmpassword;

    @FXML
    private TextField firstname;

    @FXML
    private TextField lastname;

    public void signIn(ActionEvent event) throws IOException {
        Parent account = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene accountscene = new Scene(account);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(accountscene);
        window.show();

    }

    //Username field
    public void username(ActionEvent event){
        if(username.getText().isEmpty()){
            AlertBox("Username Error", "No username entered.");
            return;
        }
        //checks for multiple '@'
        int total=0;
        for(int i=0; i<username.getText().length(); i++) {
            if(username.getText().charAt(i) == '@') {
                total++;
            }
            if(total>1) {
                AlertBox("Username Error", "Cannot Use More Than One '@'");
                return;
            }
        }

        if(!username.getText().endsWith("@yg.com")){
            AlertBox("Username Error", "must have @yg.com at the end.");
            return;
        }

        if(username.getText().substring(0, username.getText().indexOf("@")).isEmpty()){
            AlertBox("Username Error", "No username entered before '@yg.com.'");
        }

        if(this.username.getText().length() > 20){
            AlertBox("Username Error", "Username Cannot be more than 20 characters.");
        }

        this.username.setText(this.username.getText());
    }

    //password field
    public void password(ActionEvent event){
        if(this.password.getText().length() < 4 || this.password.getText().length() > 12){
            AlertBox("Password Error", "Password has to be between 4-12 characters.");
        }
        this.password.setText(this.password.getText());
    }

    //Confirmpassword field
    public void confirmpassword(ActionEvent event){
        if(!password.getText().equals(confirmpassword.getText())) {
            AlertBox("Confirm Password Error", "Does not match password");
        }
    }

    //checks all fields
    public void signup(ActionEvent evt){

        //gets first and last name
        if(firstname.getText().isEmpty()){
            AlertBox("First name Error", "No first name entered.");
            return;
        }
        firstname.setText(firstname.getText());
        if(lastname.getText().isEmpty()){
            AlertBox("Last name Error", "No last name entered.");
            return;
        }
        lastname.setText(lastname.getText());

        //username check
        if(username.getText().isEmpty()){
            AlertBox("Username Error", "No username entered.");
            return;
        }
        if(!username.getText().endsWith("@yg.com")){
            AlertBox("Username Error", "must have @yg.com at the end.");
            return;
        }
        if(username.getText().substring(0, username.getText().indexOf("@")).isEmpty()){
            AlertBox("Username Error", "No username entered before '@yg.com.'");
        }
        if(this.username.getText().length() > 20){
            AlertBox("Username Error", "Username Cannot be more than 20 characters.");
        }

        //password check
        if(this.password.getText().length() < 4 || this.password.getText().length() > 12){
            AlertBox("Password Error", "Password has to be between 4-12 characters.");
        }

        //confirmpassword check
        if(!password.getText().equals(confirmpassword.getText())) {
            AlertBox("Confirm Password Error", "Does not match password");
        }
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
