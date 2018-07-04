package Persistance;

import Model.Logger.LoggerInstance;
import View.AbstractStation;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.nio.file.Paths;
import java.util.ArrayList;


public class ConfigurationPersistor extends Persistor{

	public ConfigurationPersistor() {
		super(Paths.get("configuration/stations.txt"),
				Paths.get("configuration/ip.txt"),
				Paths.get("configuration/speedMode.txt"));
	}

	@Override
	public void loadConfiguration(Pane rootPane, ArrayList<AbstractStation> stations, IPAddress ipAddress, BorderPane messagePane) {
		super.loadConfiguration(rootPane, stations, ipAddress, messagePane);
		LoggerInstance.log.info("Configuration loaded.");
	}

	@Override
	public void saveConfiguration(ArrayList<AbstractStation> stations, IPAddress ipAddress){
		super.saveConfiguration(stations, ipAddress);
		LoggerInstance.log.info("Configuration saved.");
	}
}


