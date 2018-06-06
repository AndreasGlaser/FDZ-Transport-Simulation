package Controller;

import Model.Facade;
import Model.Network.NetworkController;
import Persistance.ConfigurationPersistor;
import Persistance.IPAddress;
import Persistance.StationData;
import Persistance.StationType;
import View.AbstractStation;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Controller {
	private ArrayList<AbstractStation> stations = new ArrayList<>();
	private IPAddress ipAddress = new IPAddress(new byte[]{127,0,0,1}, 47331);

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
	private Pane logPane;
	@FXML
	private Pane statusPane;
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
	public void initialize(){
		controllerImageView.fitWidthProperty().bind(Bindings.add(controllerGridPane.widthProperty(), -20));
		controllerImageView.fitHeightProperty().bind(controllerGridPane.heightProperty());
		simulatorImageView.fitWidthProperty().bind(Bindings.add(controllerGridPane.widthProperty(), -20));
		simulatorImageView.fitHeightProperty().bind(controllerGridPane.heightProperty());

		byte[] address = ipAddress.getAdress();
		ipField1.setOnKeyReleased(event -> {
			if(ipField1.getText().length()>3){
				ipField1.setText(ipField1.getText(0,3));
				ipField1.positionCaret(3);
			}
			try{
				address[0] = ((byte)Integer.parseInt(ipField1.getText()));
			}catch(NumberFormatException e){
				ipField1.setText("0");
				address[0] = (byte)0;
			}
		});
		ipField2.setOnKeyReleased(event -> {
			if(ipField2.getText().length()>3){
				ipField2.setText(ipField2.getText(0,3));
				ipField2.positionCaret(3);
			}
			try{
				address[1] = ((byte)Integer.parseInt(ipField2.getText()));
			}catch(NumberFormatException e){
				ipField2.setText("0");
				address[1] = (byte)0;
			}
		});
		ipField3.setOnKeyReleased(event -> {
			if(ipField3.getText().length()>3){
				ipField3.setText(ipField3.getText(0,3));
				ipField3.positionCaret(3);
			}
			try{
				address[2] = ((byte)Integer.parseInt(ipField3.getText()));
			}catch(NumberFormatException e){
				ipField3.setText("0");
				address[2] = (byte)0;
			}
		});
		ipField4.setOnKeyReleased(event -> {
			if(ipField4.getText().length()>3){
				ipField4.setText(ipField4.getText(0,3));
				ipField4.positionCaret(3);
			}
			try{
				address[3] = ((byte)Integer.parseInt(ipField4.getText()));
			}catch(NumberFormatException e){
				ipField4.setText("0");
				address[3] = (byte)0;
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

		//nur zur Demonstration
		String mesID1 = "0000000001";
		String mesID2 = "0000000002";
		String mesID3 = "0000000003";
		logPane.getChildren().add(new Text("Oct 15 00:14:14: STStK001"+mesID1+ "0002ro"));
		logPane.getChildren().add(new Text("Oct 15 00:14:15: StSTA001"+mesID1+ "0002"));
		logPane.getChildren().add(new Text("Oct 15 00:15:34: STStK001"+mesID2+ "0003ro"));
		logPane.getChildren().add(new Text("Oct 15 00:16:30: StSTF001"+mesID2+ "0002xx"));
		logPane.getChildren().add(new Text("Oct 15 00:23:12: SPXSStK001"+mesID2+ "0002aa"));
		logPane.getChildren().add(new Text("Oct 15 00:40:54: StSTF000"+mesID3+ "0000"));
		logPane.getChildren().add(new Text("Oct 15 00:41:00: STStK003"+mesID3+ "000423io"));


		statusPane.getChildren().add(new Text("Connect to 172.68.92.1"));
		statusPane.getChildren().add(new Text("Empty sled ordered to RO"));
		statusPane.getChildren().add(new Text("Empty sled arrived at RO"));
		statusPane.getChildren().add(new Text("Congestion in station RO"));
		statusPane.getChildren().add(new Text("Command not executed Oct 15 00:23:12: SPXSStK001"+mesID2+ "0002aa"));
		statusPane.getChildren().add(new Text("Release carriage with ID 23"));
		statusPane.getChildren().add(new Text("Reposition the carriage with id 23 to position IO"));
		statusPane.getChildren().add(new Text("Port number changed to 23"));
		statusPane.getChildren().add(new Text("Connection lost to 172.68.92.1"));
		statusPane.getChildren().add(new Text("Empty sled ordered to ST"));
		statusPane.getChildren().add(new Text("Empty sled arrived at ST"));
		statusPane.getChildren().add(new Text("Congestion in station ST"));
		statusPane.getChildren().add(new Text("Command not executed Oct 15 00:41:00: STStK003"+mesID3+ "000423io"));
		statusPane.getChildren().add(new Text("Release carriage with ID 23"));
		statusPane.getChildren().add(new Text("Reposition the carriage with id 23 to position IO"));

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
				loader.setControllerFactory(c ->{
					return new StationController(new StationData("new Station", StationType.STATION),stationsPane,stations);
				});
				loader.load();
			} catch (IOException e) {
				e.printStackTrace();//TODO: exceptionhandling
			}
		}

		//TODO: Facade.addStation();


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
				loader.setControllerFactory(c -> {
					return new CrossingController(new StationData("new Crossing", StationType.CROSSING), stationsPane, stations);
				});
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
		new Facade().connect(ipAddress.getAdress(), ipAddress.getPort());
	}

	@FXML
	private void disconnect(){
		new Facade().disconnect();
	}

	public void askForSaving(Stage primaryStage){
		messagePane.setMouseTransparent(false);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessagePane.fxml"));
		try {
			loader.setControllerFactory(c -> {
				return new MessageController("Save configuration?",
						"The configuration has not been saved, do you want to save it now?",
						this,
						primaryStage,
						messagePane);
			});
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
		byte[] address = ipAddress.getAdress();
		ipField1.setText(Integer.toString(Byte.toUnsignedInt(address[0])));
		ipField2.setText(Integer.toString(Byte.toUnsignedInt(address[1])));
		ipField3.setText(Integer.toString(Byte.toUnsignedInt(address[2])));
		ipField4.setText(Integer.toString(Byte.toUnsignedInt(address[3])));
		portField.setText(ipAddress.getPort().toString());
	}
}
