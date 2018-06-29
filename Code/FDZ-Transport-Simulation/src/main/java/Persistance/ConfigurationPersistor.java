package Persistance;

import java.nio.file.Paths;


public class ConfigurationPersistor extends Persistor{

	public ConfigurationPersistor() {
		super(Paths.get("configuration/stations.txt"),
				Paths.get("configuration/ip.txt"));
	}
}


