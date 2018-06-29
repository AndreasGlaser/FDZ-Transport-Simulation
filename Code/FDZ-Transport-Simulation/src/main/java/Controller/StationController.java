package Controller;

import Model.Exception.IllegalSetupException;
import Model.Facade;
import Model.Logger.LoggerInstance;
import Model.Station.Station;
import Model.Station.StationObserver;
import Persistance.IPAddress;
import Persistance.StatePersistor;
import Persistance.StationData;
import Persistance.StationType;
import View.AbstractStation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The Controller for Stations
 * @author Andreas Glaser
 *
 *
 */
public class StationController extends AbstractStation implements StationObserver{

    private final Pane parent;
    private final BorderPane messagePane;
    private final ArrayList<AbstractStation> stations;
    private final Facade facade = new Facade();
    private ArrayList<Integer> sleds = new ArrayList<>();
    private final StatePersistor statePersistor = new StatePersistor();
    private final IPAddress ipAddress;
    private String modelName;

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

    public StationController(StationData data, Pane parent, ArrayList<AbstractStation> stations, IPAddress ipAddress, BorderPane messagePane){
        this.data = data;
        this.parent = parent;
        this.stations = stations;
        this.ipAddress = ipAddress;
        this.messagePane = messagePane;
        stations.add(this);
        try {
            facade.addStation(data.getName(), data.getShortcut());
        } catch (IllegalSetupException e) {
            LoggerInstance.log.warn("Station: "+ data.getName()+" could not be added.");
        }
    }

    @FXML
    /*this method will be called once the fxml-File is fully loaded and every GUI-Element is available for manipulation*/
    public void initialize(){
        super.initialize(rootPane, parent);

        sledPane.getStyleClass().add("yellow");
        sledText.setText("Empty");
        congestionMenu.getStyleClass().add("green");
        congestionMenu.setText("no Congestion");

        refreshBelts(parent, stations);

        setData(data);
        setName(data.getName());
        modelName = data.getName();
        sledText.setText("Empty");

        hopsBackBox.getItems().addAll(1,2,3,4,5,6,7,8,9);
        hopsBackBox.getSelectionModel().select(data.getHopsBack());

        hopsBackBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            int newHopsBack = hopsBackBox.getItems().get((int)newValue);
            try {
                new Facade().setHopsToNewCarriage(data.getName(), newHopsBack);
                data.setHopsBack(newHopsBack);
            } catch (IllegalSetupException e) {
                LoggerInstance.log.warn("HopsBack could not be set to: "+ newHopsBack+ " the value is either smaller than 0 or greater than the number of stations.");
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
            Boolean nameGiven = false;
            for(AbstractStation station: stations){
                if(station.equals(this))continue;
                if(station.getName().equals(newValue)){
                    nameGiven = true;
                }
            }
            if(newValue.length() > 0 && !nameGiven){
                setName(newValue);
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
            LoggerInstance.log.warn("HopsBack could not be set to: "+ data.getHopsBack()+ " the value is either smaller than 0 or greater than the number of stations.");
        }
    }

    @FXML
    private void openCloseStationOptions(){
        if(stationOptionsPane.isVisible())closeOptions();
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
                Integer time = 1;
                for(Pair<String, Integer> pair: data.getPreviousStationsByName()){
                    if(pair.getKey().equals(station.getName())) time=pair.getValue();
                }
                TextField prevStationTimeTextField = new TextField(time.toString());
                timeBox.getChildren().add(prevStationTimeTextField);
                prevStationTimeTextField.setMaxWidth(50);
                prevStationTimeTextField.setTooltip(new Tooltip("Time in s to this station"));
                prevStationTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        prevStationTimeTextField.getStyleClass().remove("red");
                        prevStationTimeTextField.getStyleClass().add("green");
                        for(Pair<String, Integer> pair: data.getPreviousStationsByName()){
                            if(pair.getKey().equals(station.getName())){
                                addPrevStation(new Pair<>(pair.getKey(), Integer.parseInt(newValue)));
                                updatePrevStationTimeInModel(station, Integer.parseInt(newValue));
                                break;
                            }
                        }
                    }catch (NumberFormatException e){
                        prevStationTimeTextField.getStyleClass().remove("green");
                        prevStationTimeTextField.getStyleClass().add("red");

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
        if(station.getData().getStationType().equals(StationType.STATION)){
            try {
                new Facade().deletePrevStation(data.getName(), station.getName());
            }catch (NullPointerException e){
                //no action is needed, this only means that there are more than on way to the prevStation that should be deleted
            }
        }else{
            for(Pair<String, Integer> stationPair: station.getPreviousStationsByName()){
                for(AbstractStation station2: stations){
                    if(station2.getName().equals(stationPair.getKey())){
                        if(!data.getName().equals(stationPair.getKey())){
                            removePrevStationInModel(station2);
                        }

                    }
                }
            }
        }
    }

    private void addPrevStationInModel(AbstractStation station, int time) {
        if(station.getData().getStationType().equals(StationType.STATION)){
            try {
                new Facade().addPrevStation(data.getName(), station.getName(), time);
            }catch (NullPointerException e){
                LoggerInstance.log.warn("PrevStation: "+station.getName() +"could not be added, because Station: "+data.getName()+"dos not exist.");
            }
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
    private void updatePrevStationTimeInModel(AbstractStation station, int time) {

        if(station.getData().getStationType().equals(StationType.STATION)){
            new Facade().setPathTime(data.getName(), station.getName(), time);
            System.out.println("GUI updated: "+station.getName()+" as prevStation with time: "+ time + " to Station: "+ getName());
        }else{
            for(Pair<String, Integer> stationPair: station.getPreviousStationsByName()){
                for(AbstractStation station2: stations){
                    if(station2.getName().equals(stationPair.getKey())){
                        if(!data.getName().equals(stationPair.getKey())){
                            updatePrevStationTimeInModel(station2, time + stationPair.getValue());
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

    private void setNameInModel() {
        try {
            facade.setStationName(modelName, getName());
            modelName = getName();
        } catch (IllegalSetupException e) {
            setName(modelName);
            showInvalidNameMessage();
        }
    }

    private void showInvalidNameMessage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AskForSavingMessagePane.fxml"));
        try {
            loader.setControllerFactory(c -> new ErrorMessageController(
                    messagePane,
                    "This name is invalid.",
                    "Please chose a different one."
                    ));
            Pane message = loader.load();
            messagePane.setCenter(message);
        } catch (IOException e) {
            LoggerInstance.log.warn("AskForSavingMessagePane.fxml could not be loaded.");
        }
    }

    public void setName(String name){
        nameText.setText(name);
        data.setName(name);
    }
    public ArrayList<Integer> getSleds(){
        return sleds;
    }

    @Override
    public void closeOptions() {
        stationOptionsPane.setVisible(false);
        setNameInModel();//TODO: die anderen werte auch nur zu diesen zeitpunkten aktualisieren
    }

    @Override
    public void setDisableOptionsButton(Boolean bool) {
        stationOptionsButton.setDisable(bool);
    }

    private Pane getPreviousStationsPane(){
        return previousStationsPane;
    }

    private void setHopsBack(int hopsBack) {
        data.setHopsBack(hopsBack);
    }

    @Override
    public void update(Station station) {
        Platform.runLater(() -> {
            statePersistor.saveState(stations, ipAddress);
            setName(station.getName());
            setShortcut(station.getShortCut());
            setHopsBack(station.getHopsToNewCarriage());

            sleds = new Facade().getSledsInStation(data.getName());
            congestionMenu.getStyleClass().remove("red");
            congestionMenu.getStyleClass().remove("green");
            congestionMenu.getItems().clear();
            sledPane.getStyleClass().remove("yellow");
            sledPane.getStyleClass().remove("blue");
            sledPane.getStyleClass().remove("green");
            if (sleds.size() == 0 || sleds.get(0) == null){
                sledPane.getStyleClass().add("yellow");
                sledText.setText("Empty");
                congestionMenu.getStyleClass().add("green");
                congestionMenu.setText("no Congestion");
            }else if(sleds.size() > 0){
                for(Integer sledId: sleds){
                    if(sledId == null)continue;
                    congestionMenu.getItems().add(new MenuItem(sledId.toString()));
                }

                if(sleds.get(0) != null){
                    if(sleds.get(0).equals(-1)){
                        sledText.setText("Empty Sled");
                        sledPane.getStyleClass().add("blue");
                    }else{
                        sledText.setText("Sled with Pallet "+ sleds.get(0));
                        sledPane.getStyleClass().add("green");
                    }
                }
                if(sleds.size()>1){
                    congestionMenu.getStyleClass().add("red");
                    congestionMenu.setText("Congestion");
                }else {
                    congestionMenu.getStyleClass().add("green");
                    congestionMenu.setText("no Congestion");
                }
            }
        });
    }

    void updatePrevStations(AbstractStation station, Integer time) {
        updatePrevStationTimeInModel(station, time);
    }
}
