package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public abstract class MessageController {

	@FXML
	private Label detailsLabel;
	@FXML
	private Label messageLabel;

	String details;
	String message;
	protected final Pane messagePane;


	MessageController(Pane messagePane){
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
}
