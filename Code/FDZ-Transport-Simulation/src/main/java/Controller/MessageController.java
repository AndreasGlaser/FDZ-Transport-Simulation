package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MessageController {

	@FXML
	private Label detailsLabel;
	@FXML
	private Label messageLabel;
	@FXML
	private Button noButton;
	@FXML
	private Button yesButton;

	private String details;
	private String message;
	private GUIController GUIController;
	private Stage primaryStage;
	private Pane messagePane;

	public MessageController(String details, String message, GUIController GUIController, Stage primaryStage, Pane messagePane){
		this.details = details;
		this.message = message;
		this.GUIController = GUIController;
		this.primaryStage = primaryStage;
		this.messagePane = messagePane;
	}

	@FXML
	private void initialize(){
		detailsLabel.setText(details);
		messageLabel.setText(message);
		detailsLabel.getStyleClass().add("detailsLabel");
		messageLabel.getStyleClass().add("messageLabel");
	}

	@FXML
	private void noPressed(){
		primaryStage.close();
	}
	@FXML
	private void yesPressed(){
		GUIController.saveConfiguration();
		primaryStage.close();
	}
	@FXML
	private void cancelPressed(){
		messagePane.setMouseTransparent(true);
		messagePane.getChildren().clear();
	}
}
