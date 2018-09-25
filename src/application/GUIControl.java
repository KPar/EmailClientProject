package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class GUIControl {
	DatabaseHelper dbHelper;
	static int userId=0;

	@FXML
	private Label emailAddressLabel;

	public void initialize(){
		dbHelper=new DatabaseHelper();
		System.out.println("startup");
		emailAddressLabel.setText(dbHelper.getEmailAddress(userId));

	}

	public void signOut(ActionEvent event) throws IOException {
		System.out.println("this user: "+userId);
		Parent account = FXMLLoader.load(getClass().getResource("Login.fxml"));
		Scene accountscene = new Scene(account);
		
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow(); 
		
		window.setScene(accountscene);
		window.show(); 
		
	}
}
