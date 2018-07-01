package Controller;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

/**
 * The Controller for Error-messages
 * @author Andreas Glaser
 *
 *
 */
public class ErrorMessageController extends MessageController{

	ErrorMessageController(Pane messagePane, String details, String message){
		super(messagePane);
		this.details = details;
		this.message = message;

	}
	@FXML
	private void okPressed(){
		messagePane.setMouseTransparent(true);
		messagePane.getChildren().clear();
	}
}
