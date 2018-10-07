package application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GUIControl {
	DatabaseHelper dbHelper;
	static int userId = 0;
	int currentFolder = 0;

	@FXML
	private Label emailAddressLabel;

	@FXML
	private ListView emailListView;

	@FXML
	private TextArea emailTextArea;

	public void initialize(){
		dbHelper=new DatabaseHelper();
		emailAddressLabel.setText(dbHelper.getName(userId)+"\n("+dbHelper.getEmailAddress(userId)+")");
		List list = dbHelper.getEmails(userId,0);
		if(list!=null){
			ObservableList<String> observableList = FXCollections.observableList(list);
			emailListView.setItems(observableList);
		}else{
			ObservableList<String> observableList = FXCollections.observableList(new ArrayList<>());
			emailListView.setItems(observableList);
		}

	}

	public void signOut(ActionEvent event) throws IOException {
		Parent account = FXMLLoader.load(getClass().getResource("Login.fxml"));
		Scene accountscene = new Scene(account);
		
		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow(); 
		
		window.setScene(accountscene);
		window.show(); 
		
	}

	public void composeEmail(ActionEvent evt) throws IOException {
        Parent account = FXMLLoader.load(getClass().getResource("Email.fxml"));
        Scene accountscene = new Scene(account);

        Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

        window.setScene(accountscene);
        window.show();
    }

	public void refreshList(ActionEvent actionEvent) {
		Button bttn = (Button) actionEvent.getSource();
		List list;
		switch (bttn.getId()){
			case "inboxBttn":
				currentFolder =0;
				list = dbHelper.getEmails(userId,0);
				break;
			case "sentBttn":
				currentFolder =1;
				list = dbHelper.getEmails(userId,1);
				break;
			case "draftsBttn":
				currentFolder =2;
				list = dbHelper.getEmails(userId,2);
				break;
			default:
				list = dbHelper.getEmails(userId,0);
				break;
		}
		if(list!=null){
			ObservableList<String> observableList = FXCollections.observableList(list);
			emailListView.setItems(observableList);
		}else{
			ObservableList<String> observableList = FXCollections.observableList(new ArrayList<>());
			emailListView.setItems(observableList);
		}

	}

	public void displayEmail(MouseEvent mouseEvent) throws IOException {
		String[] emailContent;
		switch (currentFolder){
			case 0:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),0);
				break;
			case 1:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),1);
				break;
			case 2:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),2);

				EmailControl.dftEmail=true;
				EmailControl.emailId=Integer.parseInt(emailContent[5]);
				Parent account = FXMLLoader.load(getClass().getResource("Email.fxml"));
				Scene accountscene = new Scene(account);

				Stage window = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

				window.setScene(accountscene);
				window.show();

				return;
			default:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),0);
				break;


		}
		emailTextArea.setText("From: "+emailContent[0]+"    ("+emailContent[1]+")\nSent: "+
				emailContent[2]+"\nSubject: "+emailContent[3]+"\n\n"+emailContent[4]);
	}

	public void directionalEmailMove(KeyEvent keyEvent) throws IOException {
		displayEmail(null);
	}

	public void forwardEmail(ActionEvent actionEvent) throws IOException {
		String[] emailContent;
		switch (currentFolder){
			case 0:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),0);
				break;
			case 1:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),1);
				break;
			default:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),0);
				break;
		}

		EmailControl.fwdEmail=true;
		EmailControl.emailId=Integer.parseInt(emailContent[5]);

		Parent account = FXMLLoader.load(getClass().getResource("Email.fxml"));
		Scene accountscene = new Scene(account);

		Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

		window.setScene(accountscene);
		window.show();
	}

	public void replyEmail(ActionEvent actionEvent) throws IOException {
		String[] emailContent;
		switch (currentFolder){
			case 0:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),0);
				break;
			case 1:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),1);
				break;
			default:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),0);
				break;
		}

		EmailControl.rpyEmail=true;
		EmailControl.emailId=Integer.parseInt(emailContent[5]);

		Parent account = FXMLLoader.load(getClass().getResource("Email.fxml"));
		Scene accountscene = new Scene(account);

		Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

		window.setScene(accountscene);
		window.show();
	}
}
