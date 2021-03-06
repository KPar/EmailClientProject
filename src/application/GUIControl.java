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
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class GUIControl {
	DatabaseHelper dbHelper;
	static int userId = 0;
	int currentFolder = 0;

	@FXML
	private Label emailAddressLabel;

	@FXML
	private ListView<String> emailListView;

	@FXML
	private TextArea emailTextArea;

	@FXML
	private Button refreshBttn;


	public void initialize(){
		dbHelper=new DatabaseHelper();
		emailAddressLabel.setText(dbHelper.getName(userId)+"\n("+dbHelper.getEmailAddress(userId)+")");



		List list = dbHelper.getEmails(userId,0);
		if(list!=null){
			ObservableList<String> observableList = FXCollections.observableList(list);
			emailListView.setItems(observableList);
			emailListView.setCellFactory(lv -> new ListCell<String>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null) {
						setText(null);
						setStyle(null);
					} else {
						setText(item);
						//setTextFill(Paint.valueOf("#F2F4F4"));

						//setStyle("-fx-control-inner-background: #2C3E50;");
					}
				}
			});
		}else{
			ObservableList<String> observableList = FXCollections.observableList(new ArrayList<>());
			emailListView.setItems(observableList);
		}

	}

	public void signOut(ActionEvent event) throws IOException {
		Parent account = FXMLLoader.load(getClass().getResource("Login.fxml"));
		Scene accountscene = new Scene(account);
		accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

		Stage window = (Stage)((Node)event.getSource()).getScene().getWindow(); 
		
		window.setScene(accountscene);
		window.show(); 
		
	}

	public void composeEmail(ActionEvent evt) throws IOException {
        Parent account = FXMLLoader.load(getClass().getResource("Email.fxml"));
        Scene accountscene = new Scene(account);
		accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

        Stage window = (Stage)((Node)evt.getSource()).getScene().getWindow();

        window.setScene(accountscene);
        window.show();
    }

	public void refreshList(ActionEvent actionEvent) {
		Button bttn = (Button) actionEvent.getSource();
		List list = null;
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
			case "refreshBttn":
			    switch (currentFolder) {
                    case 0:
                        list = dbHelper.getEmails(userId, 0);
                        break;
                    case 1:
                        list = dbHelper.getEmails(userId, 1);
                        break;
                    case 2:
                        list = dbHelper.getEmails(userId, 2);
                        break;
                }
                break;
			case "deleteBttn":
				switch (currentFolder) {
					case 0:
						deleteEmail();
						list = dbHelper.getEmails(userId, 0);
						break;
					case 1:
						deleteEmail();

						list = dbHelper.getEmails(userId, 1);
						break;
					case 2:
						deleteEmail();

						list = dbHelper.getEmails(userId, 2);
						break;
				}
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
				if(emailContent==null){
					return;
				}
				break;
			case 1:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),1);
				if(emailContent==null){
					return;
				}
				break;
			case 2:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),2);
				if(emailContent==null){
					return;
				}
				EmailControl.dftEmail=true;
				EmailControl.emailId=Integer.parseInt(emailContent[5]);
				Parent account = FXMLLoader.load(getClass().getResource("Email.fxml"));
				Scene accountscene = new Scene(account);
				accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

				Stage window = (Stage)((Node)mouseEvent.getSource()).getScene().getWindow();

				window.setScene(accountscene);
				window.show();

				return;
			default:
				emailContent=dbHelper.getEmailContent(userId,emailListView.getSelectionModel().getSelectedIndex(),0);
				if(emailContent==null){
					return;
				}
				break;


		}
		emailTextArea.setText("From: "+emailContent[0]+"    ("+emailContent[1]+")\nSent: "+
				emailContent[2]+"\nSubject: "+emailContent[3]+"\n\n"+emailContent[4]);
	}

	public void directionalEmailMove(KeyEvent keyEvent) throws IOException {
		displayEmail(null);
	}

	public void deleteEmail() {
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
		if(emailContent==null){
			return;
		}else{
			switch (currentFolder){
				case 0:
					dbHelper.deleteEmail(Integer.parseInt(emailContent[5]), 0);
					break;
				case 1:
					dbHelper.deleteEmail(Integer.parseInt(emailContent[5]), 1);
					break;
				default:
					dbHelper.deleteEmail(Integer.parseInt(emailContent[5]), 1);
					break;
			}
		}
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
		if(emailContent==null){
			return;
		}
		EmailControl.fwdEmail=true;
		EmailControl.emailId=Integer.parseInt(emailContent[5]);

		Parent account = FXMLLoader.load(getClass().getResource("Email.fxml"));
		Scene accountscene = new Scene(account);
		accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

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
if(emailContent==null){
	return;
}
		EmailControl.rpyEmail=true;
		EmailControl.emailId=Integer.parseInt(emailContent[5]);

		Parent account = FXMLLoader.load(getClass().getResource("Email.fxml"));
		Scene accountscene = new Scene(account);
		accountscene.getStylesheets().add(GUIControl.class.getResource("style.css").toExternalForm());

		Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();

		window.setScene(accountscene);
		window.show();
	}
}
