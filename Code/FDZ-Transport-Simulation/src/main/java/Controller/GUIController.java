package Controller;

import Model.Facade;
import Model.Logger.*;
import Model.Network.ConnectionObserver;
import Model.Status.StatusObserver;
import Persistance.*;
import View.AbstractStation;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * The Controller for the GUI
 * @author Andreas Glaser
 *
 *
 */
public class GUIController implements ConnectionObserver, StatusObserver{
	private final ArrayList<AbstractStation> stations = new ArrayList<>();
	private final IPAddress ipAddress = new IPAddress(new byte[]{127,0,0,1}, 47331);
	private final Facade facade = new Facade();
	private final ConfigurationPersistor configurationPersistor = new ConfigurationPersistor();

	@FXML
	private Pane optionMenu;
	@FXML
	private Pane stationsPane;
	@FXML
	private ImageView controllerImageView;
	@FXML
	private ImageView simulatorImageView;
	@FXML
	private GridPane controllerGridPane;
	@FXML
	private BorderPane messagePane;
	@FXML
	private TextField portField;
	@FXML
	private TextField ipField4;
	@FXML
	private TextField ipField3;
	@FXML
	private TextField ipField2;
	@FXML
	private TextField ipField1;
	@FXML
	private Polygon controllerConnectionArrow;
	@FXML
	private Pane disconnectedIpPane;
	@FXML
	private Text ipAddressText;
	@FXML
	private Text userIPText;
	@FXML
	private TextArea logTextArea;
	@FXML
	private TextArea statusTextArea;
	@FXML
	private Button optionsButton;
	@FXML
	private CheckBox fastModeCheckBox;
	@FXML
	private ChoiceBox<String> logLevelBox;

	@FXML
	/*this method will be called once the fxml-File is fully loaded and every GUI-Element is available for manipulation*/
	public void initialize(){
		OutputStream LogOutputStream = new LogAreaOutputStream(logTextArea);
		LogOutputStreamAppender.setStaticOutputStream(LogOutputStream);

		try {
			userIPText.setText(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			LoggerInstance.log.warn("IP-Address of user pc could not be read.");
		}
		controllerImageView.fitWidthProperty().bind(Bindings.add(controllerGridPane.widthProperty(), -20));
		controllerImageView.fitHeightProperty().bind(controllerGridPane.heightProperty());
		simulatorImageView.fitWidthProperty().bind(Bindings.add(controllerGridPane.widthProperty(), -20));
		simulatorImageView.fitHeightProperty().bind(controllerGridPane.heightProperty());
		
		ipField1.setOnKeyReleased(event -> {
			if(ipField1.getText().length()>3){
				ipField1.setText(ipField1.getText(0,3));
				ipField1.positionCaret(3);
			}
			try{
				ipAddress.getAddress()[0] = ((byte)Integer.parseInt(ipField1.getText()));
			}catch(NumberFormatException e){
				ipField1.setText("0");
				ipAddress.getAddress()[0] = (byte)0;
			}
		});
		ipField2.setOnKeyReleased(event -> {
			if(ipField2.getText().length()>3){
				ipField2.setText(ipField2.getText(0,3));
				ipField2.positionCaret(3);
			}
			try{
				ipAddress.getAddress()[1] = ((byte)Integer.parseInt(ipField2.getText()));
			}catch(NumberFormatException e){
				ipField2.setText("0");
				ipAddress.getAddress()[1] = (byte)0;
			}
		});
		ipField3.setOnKeyReleased(event -> {
			if(ipField3.getText().length()>3){
				ipField3.setText(ipField3.getText(0,3));
				ipField3.positionCaret(3);
			}
			try{
				ipAddress.getAddress()[2] = ((byte)Integer.parseInt(ipField3.getText()));
			}catch(NumberFormatException e){
				ipField3.setText("0");
				ipAddress.getAddress()[2] = (byte)0;
			}
		});
		ipField4.setOnKeyReleased(event -> {
			if(ipField4.getText().length()>3){
				ipField4.setText(ipField4.getText(0,3));
				ipField4.positionCaret(3);
			}
			try{
				ipAddress.getAddress()[3] = ((byte)Integer.parseInt(ipField4.getText()));
			}catch(NumberFormatException e){
				ipField4.setText("0");
				ipAddress.getAddress()[3] = (byte)0;
			}
		});
		portField.setOnKeyReleased(event -> {
			if(portField.getText().length()>5){
				portField.setText(portField.getText(0,5));
				portField.positionCaret(5);
			}

			try{
				if(Integer.parseInt(portField.getText())<0){
					portField.setText("0");
					ipAddress.setPort(0);
				}
				ipAddress.setPort(Integer.parseInt(portField.getText()));
			}catch(NumberFormatException e){
				portField.setText("0");
				ipAddress.setPort(0);
			}
		});

		facade.addToConnectionObservable(this);
		facade.statusObservable().addObserver(this);
		logLevelBox.getItems().addAll("debug", "info",  "warn", "error");
		logLevelBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			switch(newValue){
				case "info": LoggerInstance.infoLevel();
							break;
				case "debug": LoggerInstance.debugLevel();
							break;
				case "warn": LoggerInstance.warnLevel();
							break;
				case "error": LoggerInstance.errorLevel();
							break;
			}
		});
		logLevelBox.getSelectionModel().select("info");

	}

	@FXML
	public void openCloseOptions(){
		if(optionMenu.isVisible()) optionMenu.setVisible(false);
		else optionMenu.setVisible(true);
	}

	@FXML
	public void addStation(){
		Boolean newStationUnnamed = false;
		for(AbstractStation station: stations){
			if(station.getName().equals("new Station")|station.getShortcut().equals("NS"))newStationUnnamed= true;
		}

		if(!newStationUnnamed){
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/StationPane.fxml"));
			try {
				loader.setControllerFactory(c -> new StationController(new StationData("new Station", StationType.STATION),stationsPane,stations, ipAddress, messagePane));
				loader.load();
			} catch (IOException e) {
				LoggerInstance.log.error("StationPane.fxml could not be loaded");
			}
		}
	}

	@FXML
	public void addCrossing(){
		Boolean newCrossingUnnamed = false;
		for(AbstractStation station: stations){
			if(station.getName().equals("new Crossing"))newCrossingUnnamed= true;
		}
		if(!newCrossingUnnamed) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/CrossingPane.fxml"));
			try {
				loader.setControllerFactory(c -> new CrossingController(new StationData("new Crossing", StationType.CROSSING), stationsPane, stations));
				loader.load();
			} catch (IOException e) {
				LoggerInstance.log.error("CrossingPane.fxml could not be loaded");
			}
		}
	}

	@FXML
	public void saveConfiguration(){
		configurationPersistor.saveConfiguration(stations, ipAddress);
	}

	@FXML
	public void loadConfiguration(){
		configurationPersistor.loadConfiguration(stationsPane, stations, ipAddress, messagePane);
		showIPAddress();
	}
	void loadState(){
		StatePersistor statePersistor = new StatePersistor();
		statePersistor.loadState(stationsPane,stations,ipAddress, messagePane);
		showIPAddress();
	}

	@FXML
	private void connect(){
		new Facade().connect(ipAddress.getAddress(), ipAddress.getPort());
		disconnectedIpPane.setVisible(false);
		ipAddressText.setText(ipAddress.toIPAddress());
		setOptionsActive(false);
	}

	@FXML
	private void disconnect(){
		new Facade().disconnect();
		disconnectedIpPane.setVisible(true);
		setOptionsActive(true);
	}

	@FXML
	private void onFastModeCheckBoxClicked(){
		if(fastModeCheckBox.isSelected()){
			facade.setFastTime(true);
		}else{
			facade.setFastTime(false);
		}
	}

	public void askForSaving(Stage primaryStage){
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AskForSavingMessagePane.fxml"));
		try {
			loader.setControllerFactory(c -> new AskForSavingMessageController(
                    this,
                    primaryStage,
                    messagePane));
			Pane message = loader.load();
			messagePane.setCenter(message);
		} catch (IOException e) {
			LoggerInstance.log.error("AskForSavingMessagePane.fxml could not be loaded");
		}
	}
	public void askForRestore(){
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AskForRestoreMessagePane.fxml"));
		try {
			loader.setControllerFactory(c -> new AskForRestoreMessageController(
					this,
					messagePane));
			Pane message = loader.load();
			messagePane.setCenter(message);
		} catch (IOException e) {
			LoggerInstance.log.error("AskForRestoreMessagePane.fxml could not be loaded");
		}
	}

	public Boolean isConfigurationSaved(){
		return configurationPersistor.isConfigurationSaved(stations,ipAddress);
	}

	/**
	 * Updates the Textfields in the GUI to show the User the loaded IP-Address
	 */
	private void showIPAddress(){
		byte[] address = ipAddress.getAddress();
		ipField1.setText(Integer.toString(Byte.toUnsignedInt(address[0])));
		ipField2.setText(Integer.toString(Byte.toUnsignedInt(address[1])));
		ipField3.setText(Integer.toString(Byte.toUnsignedInt(address[2])));
		ipField4.setText(Integer.toString(Byte.toUnsignedInt(address[3])));
		portField.setText(ipAddress.getPort().toString());
	}

	@Override
	public void update() {
		Platform.runLater(() -> {
			controllerConnectionArrow.getStyleClass().clear();
			if(facade.isConnected()){
				controllerConnectionArrow.getStyleClass().add("green");
				disconnectedIpPane.setVisible(false);
				ipAddressText.setText(ipAddress.toIPAddress());
				setOptionsActive(false);
			}else {
				controllerConnectionArrow.getStyleClass().add("red");
				disconnectedIpPane.setVisible(true);
				setOptionsActive(true);
			}
		});
	}


	/**
	 * deactivates or activates all options that change the configuration,
	 * for persistence reasons this method should be called before receiving commands
	 * @param bool true to activate options, false to deactivate the options
	 */
	private void setOptionsActive(Boolean bool){
		optionsButton.setDisable(!bool);
		if(bool){
			for(AbstractStation station : stations){
				station.setDisableOptionsButton(!bool);
			}
		}else{
			optionMenu.setVisible(bool);
			for(AbstractStation station : stations){
				station.closeOptions();
				station.setDisableOptionsButton(!bool);
			}
		}
	}

	@Override
	public void updateStatus() {
		Platform.runLater(()->statusTextArea.setText(facade.statusObservable().getValue()));

	}
}
