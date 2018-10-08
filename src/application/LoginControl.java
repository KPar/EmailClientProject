package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

public class LoginControl {
    DatabaseHelper dbHelper;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private TextField showpassword;

    @FXML
    private CheckBox toggle;

    @FXML
    private ComboBox domain;

    public void initialize(){
        domain.getItems().addAll("@cq.edu","@yg.com","@lnb.gov");
        passwordTextField.textProperty().bindBidirectional(showpassword.textProperty());
        showpassword.managedProperty().bind(toggle .selectedProperty());
        showpassword.visibleProperty().bind(toggle.selectedProperty());
        passwordTextField.managedProperty().bind(toggle.selectedProperty().not());
        passwordTextField.visibleProperty().bind(toggle.selectedProperty().not());
    }

    public void domainselect(){
        if(usernameTextField.getText().toLowerCase().contains("@yg.com") || usernameTextField.getText().toLowerCase().contains("@cq.edu") || usernameTextField.getText().toLowerCase().contains("@lnb.gov")) {
            usernameTextField.setText(usernameTextField.getText().substring(0,usernameTextField.getText().indexOf("@")));
            usernameTextField.appendText(domain.getSelectionModel().getSelectedItem().toString());
            return;
        }
        usernameTextField.appendText(domain.getSelectionModel().getSelectedItem().toString());
    }

	public void signIn(ActionEvent event) throws IOException {
        String emailAddress="";
        String password="";
        dbHelper= new DatabaseHelper();

	    //change this to check usernames in the database.
	    if(usernameTextField.getText().isEmpty()){
	        AlertBox("Username Error", "No Username entered.");
	        return;
        }else if(!(usernameTextField.getText().endsWith("@cq.edu") || usernameTextField.getText().endsWith("@yg.com") || usernameTextField.getText().endsWith("@lnb.gov"))){
	        AlertBox("Username Error", "Must have domain at the end.");
	        return;
        }else {
	        emailAddress=usernameTextField.getText();
        }

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
                accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());
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
        accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

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
