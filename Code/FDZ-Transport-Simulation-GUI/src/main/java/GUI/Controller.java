package GUI;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import persistance.ConfigurationPersistor;
import persistance.StationData;
import persistance.StationType;

import java.util.ArrayList;

public class Controller {
	ArrayList<AbstractStation> stations = new ArrayList<>();


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

	public void init(){
		controllerImageView.fitWidthProperty().bind(Bindings.add(controllerGridPane.widthProperty(), -20));
		controllerImageView.fitHeightProperty().bind(controllerGridPane.heightProperty());
		simulatorImageView.fitWidthProperty().bind(Bindings.add(controllerGridPane.widthProperty(), -20));
		simulatorImageView.fitHeightProperty().bind(controllerGridPane.heightProperty());

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
		new StationPane(new StationData("new Station", StationType.STATION), stationsPane, stations);
	}

	@FXML
	public void deleteStaion(){
		//TODO: implement
	}

	@FXML
	public void addCrossing(){
		new CrossingPane(new StationData("Crossing",StationType.CROSSING), stationsPane, stations);
	}
	@FXML
	public void deleteCrossing(){
		//TODO: implement
	}

	@FXML
	public void saveConfiguration(){
		ConfigurationPersistor.saveConfiguration(stations);
	}

	@FXML void loadConfiguration(){
		ConfigurationPersistor.loadConfiguration(stationsPane, stations);
	}

}
