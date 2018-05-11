package GUI;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import persistance.ConfigurationPersistor;


import java.util.ArrayList;

public class Controller {
	ArrayList<StationPane> stations = new ArrayList<>();


	@FXML
	private Pane optionMenu;
	@FXML
	private Pane stationsPane;

	public Pane getPane(){
		return stationsPane;
	}


	@FXML
	public void openCloseOptions(){
		if(optionMenu.isVisible()) optionMenu.setVisible(false);
		else optionMenu.setVisible(true);

	}

	@FXML
	public void addStation(){
		StationPane station = new StationPane("new Station", stationsPane, stations);
		stationsPane.getChildren().add(station);


		stations.add(station);


	}

	@FXML
	public void saveConfiguration(){
		ConfigurationPersistor.saveConfiguration(stations);
	}

	@FXML void loadConfiguration(){
		ConfigurationPersistor.loadConfiguration(stationsPane, stations);
	}

}
