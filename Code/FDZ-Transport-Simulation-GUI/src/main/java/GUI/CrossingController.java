package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import persistance.StationData;

public class CrossingController {

	@FXML
	private Pane crossingOptions;
	@FXML
	private Text nameText;
	@FXML
	private TextField crossingNameTextField;
	@FXML
	private Pane previousStationsPane;

	public void init(StationData data){
		crossingNameTextField.setOnKeyReleased(event -> {
			setName(crossingNameTextField.getText());
			data.setName(crossingNameTextField.getText());
		});
	}

	public void setName(String name){
		nameText.setText(name);
	}

	@FXML
	private void openCloseOptions(){
		if(crossingOptions.isVisible())crossingOptions.setVisible(false);
		else {
			crossingOptions.setVisible(true);
			crossingNameTextField.setText(nameText.getText());
		}
	}
	@FXML
	public void deleteCrossing(){
		//TODO: implement
	}

	@FXML
	private void closeOptions(){
		crossingOptions.setVisible(false);
	}

	public Pane getPreviousStationsPane(){
		return previousStationsPane;
	}

	public Pane getOptionsPane(){
		return crossingOptions;
	}



}
