package application;
	
import javafx.application.Application;
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application{

	
	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception {
		
		Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
		primaryStage.setTitle("Snail Mail");
		Scene accountscene = new Scene(root, 600, 500);
		accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());
		primaryStage.setScene(accountscene);
		primaryStage.show();
	}
	
}

