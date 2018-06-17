package Controller;

import Model.Facade;
import Model.Logger.OwnOutputStreamAppender;
import Model.Logger.TextAreaOutputStream;
import Model.Network.ConnectionObserver;
import Persistance.ConfigurationPersistor;
import Persistance.IPAddress;
import Persistance.StationData;
import Persistance.StationType;
import View.AbstractStation;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * The Controller for the GUI
 * @author Andreas Glaser
 *
 *
 */
public class GUIController implements ConnectionObserver{
	private final ArrayList<AbstractStation> stations = new ArrayList<>();
	private final IPAddress ipAddress = new IPAddress(new byte[]{127,0,0,1}, 47331);
	private final Facade facade = new Facade();

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
	private TextArea logTextArea;
	@FXML
	private TextArea statusTextArea;
	@FXML
	private Button optionsButton;
	@FXML
	private CheckBox fastModeCheckBox;

	@FXML
	public void initialize(){

		OutputStream outputStream = new TextAreaOutputStream(logTextArea);
		OwnOutputStreamAppender.setStaticOutputStream(outputStream);




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

		

		//nur zur Demonstration
		String mesID1 = "0000000001";
		String mesID2 = "0000000002";
		String mesID3 = "0000000003";

		statusTextArea.appendText("Connect to 172.68.92.1 \n");
		statusTextArea.appendText("Empty sled ordered to RO \n");
		statusTextArea.appendText("Empty sled arrived at RO \n");
		statusTextArea.appendText("Congestion in station RO \n");
		statusTextArea.appendText("Command not executed Oct 15 00:23:12: SPXSStK001"+mesID2+ "0002aa \n");
		statusTextArea.appendText("Release carriage with ID 23 \n");
		statusTextArea.appendText("Reposition the carriage with id 23 to position IO \n");
		statusTextArea.appendText("Port number changed to 23 \n");
		statusTextArea.appendText("Connection lost to 172.68.92.1 \n");
		statusTextArea.appendText("Empty sled ordered to ST \n");
		statusTextArea.appendText("Empty sled arrived at ST \n");
		statusTextArea.appendText("Congestion in station ST \n");
		statusTextArea.appendText("Command not executed Oct 15 00:41:00: STStK003"+mesID3+ "000423io \n");
		statusTextArea.appendText("Release carriage with ID 23 \n");
		statusTextArea.appendText("Reposition the carriage with id 23 to position IO \n");

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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/StationPane.fxml"));
			try {
				loader.setControllerFactory(c -> new StationController(new StationData("new Station", StationType.STATION),stationsPane,stations));
				loader.load();
			} catch (IOException e) {
				e.printStackTrace();//TODO: exceptionhandling
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
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrossingPane.fxml"));
			try {
				loader.setControllerFactory(c -> new CrossingController(new StationData("new Crossing", StationType.CROSSING), stationsPane, stations));
				loader.load();
			} catch (IOException e) {
				e.printStackTrace();//TODO: exceptionhandling
			}
		}
	}

	@FXML
	public void saveConfiguration(){
		ConfigurationPersistor.saveConfiguration(stations, ipAddress);
	}

	@FXML
	public void loadConfiguration(){
		ConfigurationPersistor configurationPersistor = new ConfigurationPersistor();
		configurationPersistor.loadConfiguration(stationsPane, stations, ipAddress);
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
		messagePane.setMouseTransparent(false);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessagePane.fxml"));
		try {
			loader.setControllerFactory(c -> new MessageController("Save configuration?",
                    "The configuration has not been saved, do you want to save it now?",
                    this,
                    primaryStage,
                    messagePane));
			Pane message = loader.load();
			messagePane.setCenter(message);


		} catch (IOException e) {
			e.printStackTrace();//TODO: exceptionhandling
		}

	}

	public Boolean isConfigurationSaved(){
		return ConfigurationPersistor.isConfigurationSaved(stations,ipAddress);
	}

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
		controllerConnectionArrow.getStyleClass().clear();
		System.out.println("isConnencted() returns "+facade.isConnected());
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
}
