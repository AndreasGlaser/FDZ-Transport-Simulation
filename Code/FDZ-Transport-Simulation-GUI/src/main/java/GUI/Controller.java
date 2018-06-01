package GUI;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import persistance.ConfigurationPersistor;
import persistance.StationData;


import java.util.ArrayList;

public class Controller {
	ArrayList<StationPane> stations = new ArrayList<>();


	@FXML
	private Pane optionMenu;
	@FXML
	private Pane stationsPane;


	@FXML
	public void openCloseOptions(){
		if(optionMenu.isVisible()) optionMenu.setVisible(false);
		else optionMenu.setVisible(true);

	}

	@FXML
	public void addStation(){
		new StationPane(new StationData("new Station"), stationsPane, stations);
	}

	@FXML
	public void saveConfiguration(){
		ConfigurationPersistor.saveConfiguration(stations);
	}

	@FXML void loadConfiguration(){
		ConfigurationPersistor.loadConfiguration(stationsPane, stations);
	}

}
