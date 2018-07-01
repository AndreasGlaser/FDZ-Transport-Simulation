package Controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
/**
 * The Controller for the ask-for-restore-message
 * @author Andreas Glaser
 *
 */
public class AskForRestoreMessageController extends MessageController{
	private final GUIController guiController;

	AskForRestoreMessageController(GUIController guiController, Pane messagePane){
		super(messagePane);
		details = "The application was not closed correct.";
		message = "Do you want to restore the last state?";
		this.guiController = guiController;
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
