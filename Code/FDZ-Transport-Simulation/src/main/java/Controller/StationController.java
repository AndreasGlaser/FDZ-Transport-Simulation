package Controller;

import Model.Exception.IllegalSetupException;
import Model.Facade;
import Persistance.StationData;
import Persistance.StationType;
import View.AbstractStation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

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
    private Pane sledPane;
    @FXML
    private TextField stationNameTextField;
    @FXML
    private Pane previousStationsPane;
    @FXML
    private ChoiceBox<Integer> hopsBackBox;
    @FXML
    private TextField abbreviationField;
    @FXML
    private MenuButton congestionMenu;

    public StationController(StationData data, Pane parent, ArrayList<AbstractStation> stations){
        this.data = data;
        this.parent = parent;
        this.stations = stations;
        stations.add(this);
        try {
            new Facade().addStation(data.getName(), data.getShortcut());
        } catch (IllegalSetupException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(){
        viewPane = rootPane;
        parent.getChildren().add(viewPane);
        stationNameTextField.setOnKeyReleased(event -> {
            setName(stationNameTextField.getText());
            data.setName(stationNameTextField.getText());
        });
        abbreviationField.setOnKeyReleased(event ->{
            data.setShortcut(abbreviationField.getText());
        });

        /*new Facade().getStationChangedProperty(data.getName()).addListener((observable, oldValue, newValue) -> {
            if(newValue){
                ArrayList<Integer> sleds = new Facade().getSledsInStation(data.getName());
                if (sleds.size() == 0){
                    sledPane.getStyleClass().clear();
                    sledPane.getStyleClass().add("yellow");
                    sledText.setText("Empty");
                }else{
                    for(Integer sledId: sleds){
                        congestionMenu.getItems().add(new MenuItem(sledId.toString()));
                    }
                    sledText.setText("Sled with Pallet "+ sleds.get(0));
                    sledPane.getStyleClass().clear();
                    sledPane.getStyleClass().add("green");
                    congestionMenu.getStyleClass().clear();
                    if(sleds.size()>1){
                        congestionMenu.getStyleClass().add("red");
                        congestionMenu.setText("Congestion");
                    }else {
                        congestionMenu.getStyleClass().add("green");
                        congestionMenu.setText("no Congestion");
                    }
                }
            }
        });*/

         //TODO auf Property lauschen

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
                    if(newValue){
                        data.getPreviousStationsByName().add(i.getData().getName());
                        if(i.getData().getstationType().equals(StationType.STATION)){
                            new Facade().addPrevStation(data.getName(), i.getName());
                        }

                    }
                    else {
                        data.getPreviousStationsByName().remove(i.getData().getName());
                        if(i.getData().getstationType().equals(StationType.STATION)){
                            new Facade().deletePrevStation(data.getName(), i.getName());
                        }

                    }
                    refreshBelts(parent, stations);

                });
            }
            abbreviationField.setText(data.getShortcut());
            abbreviationField.textProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue.length()>2){
                    abbreviationField.setText(oldValue);
                    data.setShortcut(oldValue);
                }else data.setShortcut(newValue);
            });


        }
    }

    @FXML
    public void deleteStation(){
        parent.getChildren().remove(viewPane);
        stations.remove(this);
        parent.getChildren().removeAll(incomingBelts);
        for(AbstractStation station: stations){
            station.refreshBelts(parent, stations);
        }
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
