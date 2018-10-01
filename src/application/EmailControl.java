package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.io.IOException;

public class EmailControl extends TextField {

    @FXML
    private TextField to;

    @FXML
    private TextField from;

    @FXML
    private TextArea content;

    public void send(ActionEvent evt){
        System.out.println("Sends email.");
    }

    public void discard(ActionEvent evt) throws IOException{
        System.out.println("Discard email.");
        Parent account = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        Scene accountscene = new Scene(account);

        Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

        window.setScene(accountscene);
        window.show();
    }


}

