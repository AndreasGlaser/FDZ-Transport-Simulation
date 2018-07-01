package Controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The Controller for the ask-for-saving-message
 * @author Andreas Glaser
 *
 */
public class AskForSavingMessageController extends MessageController{

	private final GUIController guiController;
	private final Stage primaryStage;

	AskForSavingMessageController(GUIController GUIController, Stage primaryStage, Pane messagePane){
		super(messagePane);
		details = "Save configuration?";
		message = "The configuration has not been saved, do you want to save it now?";
		this.guiController = GUIController;
		this.primaryStage = primaryStage;
	}

	@FXML
	private void noPressed(){
		primaryStage.close();
	}
	@FXML
	private void yesPressed(){
		guiController.saveConfiguration();
		primaryStage.close();
	}
	@FXML
	private void cancelPressed(){
		messagePane.setMouseTransparent(true);
		messagePane.getChildren().clear();
	}
}
