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

public class Controller implements ConnectionObserver{
	private ArrayList<AbstractStation> stations = new ArrayList<>();
	private IPAddress ipAddress = new IPAddress(new byte[]{127,0,0,1}, 47331);
	private Facade facade = new Facade();

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
	private Polygon controllerConnectionArrow;
	@FXML
	private Pane disconnectedIpPane;
	@FXML
	private Text ipAddressText;

	@FXML
	private TextArea textArea;

	@FXML
	public void initialize(){
		TextArea textArea = new TextArea();//TODO: entfernen, nur bis es in der view eingebaut ist
		OutputStream outputStream = new TextAreaOutputStream(textArea);
		OwnOutputStreamAppender.setStaticOutputStream(outputStream);
		textArea.setText("sdaf");
		logPane.getChildren().add(textArea);




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
		new Facade().connect(ipAddress.getAddress(), ipAddress.getPort());
		disconnectedIpPane.setVisible(false);
		ipAddressText.setText(ipAddress.toIPAddress());
	}

	@FXML
	private void disconnect(){
		new Facade().disconnect();
		disconnectedIpPane.setVisible(true);
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
		byte[] address = ipAddress.getAddress();
		ipField1.setText(Integer.toString(Byte.toUnsignedInt(address[0])));
		ipField2.setText(Integer.toString(Byte.toUnsignedInt(address[1])));
		ipField3.setText(Integer.toString(Byte.toUnsignedInt(address[2])));
		ipField4.setText(Integer.toString(Byte.toUnsignedInt(address[3])));
		portField.setText(ipAddress.getPort().toString());
	}

	@Override
	public void update() {
		//TODO: change GUI depending on connection state
		controllerConnectionArrow.getStyleClass().clear();
		System.out.println("isConnencted() returns "+facade.isConnected());
		if(facade.isConnected()){
			controllerConnectionArrow.getStyleClass().add("green");
			disconnectedIpPane.setVisible(false);
			ipAddressText.setText(ipAddress.toIPAddress());
		}else {
			controllerConnectionArrow.getStyleClass().add("red");
			disconnectedIpPane.setVisible(true);
		}
	}
}
