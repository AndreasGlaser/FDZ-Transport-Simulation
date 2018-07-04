package Controller;

import Model.Facade;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
/**
 * The Controller for the ask-for-restore-message
 * @author Andreas Glaser
 *
 */
public class AskForRestoreMessageController extends MessageController{
	private final GUIController guiController;
	private Scene scene;

	AskForRestoreMessageController(GUIController guiController, Pane messagePane, Scene scene){
		super(messagePane);
		details = "The application was not closed correct.";
		message = "Do you want to restore the last state? \nIf you chose \"yes\", you need to connect first.";
		this.guiController = guiController;
		this.scene = scene;
	}

	@FXML
	private void noPressed(){
		guiController.loadConfiguration();
		closeThisWindow();
	}
	@FXML
	private void yesPressed(){
		if(new Facade().isConnected()){
			guiController.loadState();
			closeThisWindow();
		}
	}

	private void closeThisWindow(){
		messagePane.setMouseTransparent(true);
		messagePane.getChildren().clear();
		scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			final KeyCombination keyComb = new KeyCodeCombination(KeyCode.S,
					KeyCombination.CONTROL_DOWN);
			public void handle(KeyEvent ke) {
				if (keyComb.match(ke) && !new Facade().isConnected()) {
					guiController.saveConfiguration();
					ke.consume();
				}
			}
		});
	}
}
