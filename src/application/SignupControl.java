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

import java.io.IOException;

public class SignupControl extends TextField{

    DatabaseHelper dbHelper;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

    @FXML
    private TextField confirmPasswordTextField;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    public void signIn(ActionEvent event) throws IOException {
        Parent account = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene accountscene = new Scene(account);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(accountscene);
        window.show();

    }

    //Username field
    public void username(ActionEvent event){
        if(usernameTextField.getText().isEmpty()){
            AlertBox("Username Error", "No username entered.");
            return;
        }
        //checks for multiple '@'
        int total=0;
        for(int i = 0; i< usernameTextField.getText().length(); i++) {
            if(usernameTextField.getText().charAt(i) == '@') {
                total++;
            }
            if(total>1) {
                AlertBox("Username Error", "Cannot Use More Than One '@'");
                return;
            }
        }

        if(!usernameTextField.getText().endsWith("@yg.com")){
            AlertBox("Username Error", "must have @yg.com at the end.");
            return;
        }

        if(usernameTextField.getText().substring(0, usernameTextField.getText().indexOf("@")).isEmpty()){
            AlertBox("Username Error", "No username entered before '@yg.com.'");
            return;
        }

        if(this.usernameTextField.getText().length() > 20){
            AlertBox("Username Error", "Username Cannot be more than 20 characters.");
            return;
        }
    }

    //password field
    public void password(ActionEvent event){
        if(this.passwordTextField.getText().length() < 4 || this.passwordTextField.getText().length() > 12){
            AlertBox("Password Error", "Password has to be between 4-12 characters.");
            return;
        }
    }

    //Confirmpassword field
    public void confirmpassword(ActionEvent event){
        if(!passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
            AlertBox("Confirm Password Error", "Does not match password");
            return;
        }
    }

    //checks all fields
    public void signup(ActionEvent event) throws IOException{
        String firstName="";
        String lastName="";
        String emailAddress="";
        String password="";

        //gets first
        if(firstNameTextField.getText().isEmpty()){
            AlertBox("First name Error", "No first name entered.");
            return;
        }else{
            firstName=firstNameTextField.getText();
        }

        //get last name
        if(lastNameTextField.getText().isEmpty()){
            AlertBox("Last name Error", "No last name entered.");
            return;
        }else{
            lastName=lastNameTextField.getText();
        }

        //username check
        if(usernameTextField.getText().isEmpty()){
            AlertBox("Username Error", "No username entered.");
            return;
        }
        //checks for multiple '@'
        int total=0;
        for(int i = 0; i< usernameTextField.getText().length(); i++) {
            if(usernameTextField.getText().charAt(i) == '@') {
                total++;
            }
            if(total>1) {
                AlertBox("Username Error", "Cannot Use More Than One '@'");
                return;
            }
        }

        if(!usernameTextField.getText().endsWith("@yg.com")){
            AlertBox("Username Error", "must have @yg.com at the end.");
            return;
        }else if(usernameTextField.getText().substring(0, usernameTextField.getText().indexOf("@")).isEmpty()){
            AlertBox("Username Error", "No username entered before '@yg.com.'");
            return;
        }else if(this.usernameTextField.getText().length() > 20){
            AlertBox("Username Error", "Username Cannot be more than 20 characters.");
            return;
        }else{
            emailAddress=usernameTextField.getText();
        }

        //password check
        if(this.passwordTextField.getText().length() < 4 || this.passwordTextField.getText().length() > 12){
            AlertBox("Password Error", "Password has to be between 4-12 characters.");
            return;
        }

        //confirmpassword check
        if(!passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
            AlertBox("Confirm Password Error", "Does not match password");
            return;
        }else{
            password=passwordTextField.getText();
        }

        dbHelper = new DatabaseHelper();
        if(dbHelper.getUserId(emailAddress).length!=0){
            AlertBox("Email Address Error ", "Email already exists. Choose a different address or login");
            return;
        }else{
            GUIControl.userId=dbHelper.setupNewAccount(firstName,lastName,emailAddress,password);  //sets the userId in GUIControl to associate it with account
        }

        Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
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
