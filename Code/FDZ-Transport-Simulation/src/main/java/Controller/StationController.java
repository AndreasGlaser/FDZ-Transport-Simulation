package Controller;

import Model.Exception.IllegalSetupException;
import Model.Facade;
import Model.Station.Station;
import Model.Station.StationObserver;
import Persistance.StationData;
import Persistance.StationType;
import View.AbstractStation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.ArrayList;

/**
 * The Controller for Stations
 * @author Andreas Glaser
 *
 *
 */
public class StationController extends AbstractStation implements StationObserver{

    private final Pane parent;
    private final ArrayList<AbstractStation> stations;
    private final Facade facade = new Facade();

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
    @FXML
    private Button stationOptionsButton;

    public StationController(StationData data, Pane parent, ArrayList<AbstractStation> stations){
        this.data = data;
        this.parent = parent;
        this.stations = stations;
        stations.add(this);
        try {
            facade.addStation(data.getName(), data.getShortcut());
        } catch (IllegalSetupException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize(){
        viewPane = rootPane;
        parent.getChildren().add(viewPane);



        sledPane.getStyleClass().clear();
        sledPane.getStyleClass().add("yellow");
        sledText.setText("Empty");
        congestionMenu.getStyleClass().clear();
        congestionMenu.getStyleClass().add("green");
        congestionMenu.setText("no Congestion");

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

        hopsBackBox.getItems().addAll(1,2,3,4,5,6,7,8,9);
        hopsBackBox.getSelectionModel().select(data.getHopsBack());

        hopsBackBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int newHopsBack = hopsBackBox.getItems().get((int)newValue);
            try {
                new Facade().setHopsToNewCarriage(data.getName(), newHopsBack);
                data.setHopsBack(newHopsBack);
            } catch (IllegalSetupException e) {
                System.out.println(e.getMessage());//TODO: Log
            }
        });

        abbreviationField.setText(data.getShortcut());
        abbreviationField.textProperty().addListener((observable, oldValue, newValue) -> {
            abbreviationField.getStyleClass().remove("lightRed");
            if(newValue.length()>2){
                abbreviationField.setText(oldValue);
            }else if(newValue.length()== 2){
                data.setShortcut(newValue);
                try {
                    facade.setStationShortCut(data.getName(), newValue);
                } catch (IllegalSetupException e) {
                    e.printStackTrace();
                    abbreviationField.getStyleClass().add("lightRed");
                }
            }else{
                abbreviationField.getStyleClass().add("lightRed");
            }

        });

        stationNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            stationNameTextField.getStyleClass().remove("lightRed");
            if(newValue.length() > 0){

                try {
                    facade.setStationName(data.getName(), newValue);
                    setName(newValue);
                } catch (IllegalSetupException e) {
                    e.printStackTrace();
                }
            }else {
                stationNameTextField.getStyleClass().add("lightRed");
            }

        });
    }

    public void initAfterAllStationLoaded(){
        try {
            facade.setHopsToNewCarriage(data.getName(), data.getHopsBack());
            for(Pair<String, Integer> stationPair: data.getPreviousStationsByName()){
                for(AbstractStation station: stations){
                    if(station.getName().equals(stationPair.getKey())){
                        if(!data.getName().equals(stationPair.getKey())){
                            addPrevStationInModel(station, stationPair.getValue());
                        }

                    }
                }
            }
            new Facade().addToStationObservable(data.getName(), this);
        } catch (IllegalSetupException e) {
            e.printStackTrace();//TODO log
        }
    }

    @FXML
    private void openCloseStationOptions(){
        if(stationOptionsPane.isVisible())stationOptionsPane.setVisible(false);
        else {
            stationOptionsPane.setVisible(true);
            stationNameTextField.setText(nameText.getText());
            getPreviousStationsPane().getChildren().clear();
            for (AbstractStation station: stations){
                if(station.equals(this))continue;
                BorderPane prevStationBorderPane = new BorderPane();
                getPreviousStationsPane().getChildren().add(prevStationBorderPane);

                CheckBox box = new CheckBox(station.getName());
                prevStationBorderPane.setLeft(box);
                if(prevStationsContains(station.getData().getName()))box.setSelected(true);
                else box.setSelected(false);


                HBox timeBox = new HBox();
                prevStationBorderPane.setRight(timeBox);
                Text prevStationTimeText = new Text("s: ");
                timeBox.getChildren().add(prevStationTimeText);
                TextField prevStationTimeTextField = new TextField("1");
                timeBox.getChildren().add(prevStationTimeTextField);
                prevStationTimeTextField.setMaxWidth(50);
                prevStationTimeTextField.setTooltip(new Tooltip("Time in s to this station"));
                prevStationTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        Integer.parseInt(newValue);
                    }catch (NumberFormatException e){
                        prevStationTimeTextField.setText(oldValue);
                    }



                });
                box.selectedProperty().addListener((observable2, oldValue, newValue) -> {
                    if(newValue){
                        Integer pathTime = Integer.parseInt(prevStationTimeTextField.getText());
                        addPrevStation(new Pair<>(station.getData().getName(),pathTime));
                        addPrevStationInModel(station, pathTime);
                    }
                    else {
                        prevStationRemove(station.getData().getName());
                        removePrevStationInModel(station);
                    }
                    refreshBelts(parent, stations);

                });
            }



        }
    }

    private void removePrevStationInModel(AbstractStation station) {

        if(station.getData().getstationType().equals(StationType.STATION)){
            new Facade().deletePrevStation(data.getName(), station.getName());
        }else{
            for(Pair<String, Integer> stationPair: station.getPreviousStationsByName()){
                for(AbstractStation station2: stations){
                    if(station2.getName().equals(stationPair.getKey())){
                        if(!data.getName().equals(stationPair.getKey())){
                            removePrevStationInModel(station2);
                            System.out.println("removed " +station2.getName());
                        }

                    }
                }
            }
        }
    }

    private void addPrevStationInModel(AbstractStation station, int time) {

        if(station.getData().getstationType().equals(StationType.STATION)){
            new Facade().addPrevStation(data.getName(), station.getName(), time);
        }else{
            for(Pair<String, Integer> stationPair: station.getPreviousStationsByName()){
                for(AbstractStation station2: stations){
                    if(station2.getName().equals(stationPair.getKey())){
                        if(!data.getName().equals(stationPair.getKey())){
                            addPrevStationInModel(station2, time + stationPair.getValue());
                        }

                    }
                }
            }
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

    @Override
    public void closeOptions() {
        stationOptionsPane.setVisible(false);
    }

    @Override
    public void setDisableOptionsButton(Boolean bool) {
        stationOptionsButton.setDisable(bool);
    }

    private void setSledText(String sledString){
        sledText.setText(sledString);
    }

    private Pane getPreviousStationsPane(){
        return previousStationsPane;
    }

    private void setHopsBack(int hopsBack) {
        data.setHopsBack(hopsBack);
    }

    @Override
    public void update(Station station) {

        setName(station.getName());
        setShortcut(station.getShortCut());
        setHopsBack(station.getHopsToNewCarriage());
        //TODO übernehmen der Änderungen an den prevstations die in der cli gemacht wurden


        ArrayList<Integer> sleds = new Facade().getSledsInStation(data.getName());
        congestionMenu.getItems().clear();
        if (sleds.size() == 0 || sleds.get(0) == null){
            sledPane.getStyleClass().clear();
            sledPane.getStyleClass().add("yellow");
            sledText.setText("Empty");
            congestionMenu.getStyleClass().clear();
            congestionMenu.getStyleClass().add("green");
            congestionMenu.setText("no Congestion");
        }else if(sleds.size() > 0){
            for(Integer sledId: sleds){
                if(sledId == null)continue;
                congestionMenu.getItems().add(new MenuItem(sledId.toString()));
            }

            sledPane.getStyleClass().clear();
            if(sleds.get(0) != null){
                if(sleds.get(0).equals(-1)){
                    sledText.setText("Empty Sled");
                    sledPane.getStyleClass().add("blue");
                }else{
                    sledText.setText("Sled with Pallet "+ sleds.get(0));
                    sledPane.getStyleClass().add("green");
                }
            }


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


}
