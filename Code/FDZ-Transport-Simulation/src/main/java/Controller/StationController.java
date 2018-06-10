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
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;


public class StationController extends AbstractStation implements StationObserver{

    private Pane parent;
    private ArrayList<AbstractStation> stations;
    private Facade facade = new Facade();

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
            facade.addStation(data.getName(), data.getShortcut());
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
                System.out.println("new hopsBack: "+ newHopsBack);
            } catch (IllegalSetupException e) {
                System.out.println(e.getMessage());//TODO: Log
            }
        });

        abbreviationField.setText(data.getShortcut());
        abbreviationField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.length()>2){
                abbreviationField.setText(oldValue);
                data.setShortcut(oldValue);
            }else {
                data.setShortcut(newValue);
            }
        });

    }

    public void initAfterAllStationLoaded(){
        try {
            facade.setHopsToNewCarriage(data.getName(), data.getHopsBack());
            for(String stationName: data.getPreviousStationsByName()){
                for(AbstractStation station: stations){
                    if(station.getName().equals(stationName)){
                        if(!data.getName().equals(stationName)){
                            addPrevStationInModel(station);
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
            for (AbstractStation i: stations){
                if(i.equals(this))continue;
                CheckBox box = new CheckBox(i.getName());
                getPreviousStationsPane().getChildren().add(box);
                if(data.getPreviousStationsByName().contains(i.getData().getName()))box.setSelected(true);
                else box.setSelected(false);
                box.selectedProperty().addListener((observable2, oldValue, newValue) -> {
                    if(newValue){
                        data.getPreviousStationsByName().add(i.getData().getName());
                        addPrevStationInModel(i);
                    }
                    else {
                        data.getPreviousStationsByName().remove(i.getData().getName());
                        removePrevStationInModel(i);
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
            for(String stationName: station.getPreviousStationsByName()){
                for(AbstractStation station2: stations){
                    if(station2.getName().equals(stationName)){
                        if(!data.getName().equals(stationName)){
                            removePrevStationInModel(station2);
                            System.out.println("removed " +station2.getName());
                        }

                    }
                }
            }
        }
    }

    private void addPrevStationInModel(AbstractStation station) {

        if(station.getData().getstationType().equals(StationType.STATION)){
            new Facade().addPrevStation(data.getName(), station.getName());
        }else{
            for(String stationName: station.getPreviousStationsByName()){
                for(AbstractStation station2: stations){
                    if(station2.getName().equals(stationName)){
                        if(!data.getName().equals(stationName)){
                            addPrevStationInModel(station2);
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

    public void setSledText(String sledString){
        sledText.setText(sledString);
    }

    public Pane getPreviousStationsPane(){
        return previousStationsPane;
    }

    public Pane getstationOptionsPane(){
        return stationOptionsPane;
    }

    public void setHopsBack(int hopsBack) {
        data.setHopsBack(hopsBack);
    }

    @Override
    public void update(Station station) {
        System.out.println("change occurred to station " + station.getName());

        setName(station.getName());
        setShortcut(station.getShortCut());
        setHopsBack(station.getHopsToNewCarriage());
        //TODO übernehmen der Änderungen an den prevstations die in der cli gemacht wurden


        ArrayList<Integer> sleds = new Facade().getSledsInStation(data.getName());
        System.out.println(sleds);
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
