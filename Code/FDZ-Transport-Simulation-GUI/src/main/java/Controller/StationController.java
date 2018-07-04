package Controller;

import View.AbstractStation;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import Persistance.StationData;

import java.util.ArrayList;


public class StationController extends AbstractStation {

    private Pane parent;
    private ArrayList<AbstractStation> stations;

    @FXML
    private Pane rootPane;
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
    @FXML
    private Polygon controllerConnectionArrow;
    @FXML
    private ChoiceBox<Integer> hopsBackBox;
    @FXML
    private TextField abbreviationField;

    public StationController(StationData data, Pane parent, ArrayList<AbstractStation> stations){
        this.data = data;
        this.parent = parent;
        this.stations = stations;
        stations.add(this);
    }

    @FXML
    public void initialize(){
        viewPane = rootPane;
        parent.getChildren().add(viewPane);
        stationNameTextField.setOnKeyReleased(event -> {
            setName(stationNameTextField.getText());
            data.setName(stationNameTextField.getText());
        });
        sledText.getStyleClass().add("yellow");
        refreshBelts(parent, stations);

        setData(data);
        setName(data.getName());
        setSledText("Empty");

        //make Dragable
        viewPane.setOnMousePressed(e ->{
            sceneX = e.getSceneX();
            sceneY = e.getSceneY();

            dragXTrans = viewPane.getTranslateX();
            dragYTrans = viewPane.getTranslateY();
        });
        viewPane.setOnMouseDragged(e->{
            setXCord(e.getSceneX()  - sceneX + dragXTrans);
            setYCord(e.getSceneY() - sceneY + dragYTrans);
        });

        hopsBackBox.getItems().addAll(0,1,2,3,4,5,6,7,8,9);
        hopsBackBox.getSelectionModel().select(data.getHopsBack());

        hopsBackBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            data.setHopsBack(hopsBackBox.getItems().get((Integer)newValue));
            System.out.println(data.getHopsBack());
        });
    }

    @FXML
    private void openCloseStationOptions(){
        if(stationOptionsPane.isVisible())stationOptionsPane.setVisible(false);
        else {
            stationOptionsPane.setVisible(true);
            stationNameTextField.setText(nameText.getText());
            getPreviousStationsPane().getChildren().clear();
            for (AbstractStation i: stations){
                if(i.equals(this))continue;
                CheckBox box = new CheckBox(i.getName());
                getPreviousStationsPane().getChildren().add(box);
                if(data.getPreviousStationsByName().contains(i.getData().getName()))box.setSelected(true);
                else box.setSelected(false);
                box.selectedProperty().addListener((observable2, oldValue, newValue) -> {
                    if(newValue) data.getPreviousStationsByName().add(i.getData().getName());
                    else data.getPreviousStationsByName().remove(i.getData().getName());
                    refreshBelts(parent, stations);

                });
            }


        }
    }

    @FXML
    public void deleteStation(){
        parent.getChildren().remove(viewPane);
        stations.remove(this);
        parent.getChildren().removeAll(incomingBelts);
    }

    @FXML
    private void closeStationOptions(){
        stationOptionsPane.setVisible(false);
    }

    public void setName(String name){
        nameText.setText(name);
        data.setName(name);
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
