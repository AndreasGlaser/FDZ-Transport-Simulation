package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * The Controller for Error-messages
 * @author Andreas Glaser
 *
 *
 */
public class ErrorMessageController {

	@FXML
	private Label detailsLabel;
	@FXML
	private Label messageLabel;

	private final String details;
	private final String message;
	private final Pane messagePane;


	public ErrorMessageController(Pane messagePane, String details, String message){
		this.details = details;
		this.message = message;
		this.messagePane = messagePane;
	}
	@FXML
	private void initialize(){
		messagePane.setMouseTransparent(false);
		detailsLabel.setText(details);
		messageLabel.setText(message);
		detailsLabel.getStyleClass().add("detailsLabel");
		messageLabel.getStyleClass().add("messageLabel");
	}
	@FXML
	private void okPressed(){
		messagePane.setMouseTransparent(true);
		messagePane.getChildren().clear();
	}
}
