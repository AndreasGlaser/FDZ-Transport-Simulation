package Controller;

import Persistance.StatePersistor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class AskForRestoreMessageController {
	@FXML
	private Label detailsLabel;
	@FXML
	private Label messageLabel;

	private final String details;
	private final String message;
	private final GUIController guiController;
	private final Pane messagePane;


	public AskForRestoreMessageController(GUIController guiController, Pane messagePane){
		this.details = "The application was not closed correct.";
		this.message = "Do you want to restore the last state?";
		this.guiController = guiController;
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
		guiController.loadConfiguration();
		closeThisWindow();
	}
	@FXML
	private void yesPressed(){
		guiController.loadState();
		closeThisWindow();
	}

	private void closeThisWindow(){
		messagePane.setMouseTransparent(true);
		messagePane.getChildren().clear();
	}
}
