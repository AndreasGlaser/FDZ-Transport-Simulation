package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The Controller for the ask-for-saving-message
 * @author Andreas Glaser
 *
 */
public class AskForSavingMessageController {

	@FXML
	private Label detailsLabel;
	@FXML
	private Label messageLabel;

	private final String details;
	private final String message;
	private final GUIController GUIController;
	private final Stage primaryStage;
	private final Pane messagePane;


	AskForSavingMessageController(GUIController GUIController, Stage primaryStage, Pane messagePane){
		this.details = "Save configuration?";
		this.message = "The configuration has not been saved, do you want to save it now?";
		this.GUIController = GUIController;
		this.primaryStage = primaryStage;
		this.messagePane = messagePane;
	}

	@FXML
	/*this method will be called once the fxml-File is fully loaded and every GUI-Element is available for manipulation*/
	private void initialize(){
		messagePane.setMouseTransparent(false);
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
