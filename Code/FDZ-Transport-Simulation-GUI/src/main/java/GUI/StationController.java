package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;


public class StationController {



    @FXML
    private Pane stationOptionsPane;

    @FXML
    private Text nameText;

    @FXML
    private Text sledText;

    @FXML
    private TextField stationNameTextField;

    @FXML
    private Pane previousStationsPane;

    public void init(){
        stationNameTextField.setOnKeyReleased(event -> {
            setName(stationNameTextField.getText());
        });
        sledText.getStyleClass().add("yellow");
    }

    @FXML
    private void openCloseStationOptions(){
        if(stationOptionsPane.isVisible())stationOptionsPane.setVisible(false);
        else {
            stationOptionsPane.setVisible(true);
            stationNameTextField.setText(nameText.getText());
        }
    }

    @FXML
    private void closeStationOptions(){
        stationOptionsPane.setVisible(false);
    }

    public void setName(String name){
        nameText.setText(name);
    }

    public void setSledText(String sledString){
        sledText.setText(sledString);
    }

    public Pane getPreviousStationsPane(){
        return previousStationsPane;
    }

    public Pane getstationOptionsPane(){
        return stationOptionsPane;
    }
}
