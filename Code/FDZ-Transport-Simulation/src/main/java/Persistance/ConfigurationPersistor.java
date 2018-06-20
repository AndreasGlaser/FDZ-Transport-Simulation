package Persistance;

import java.nio.file.Path;
import java.nio.file.Paths;


public class ConfigurationPersistor extends Persistor{
	private static Path stationsPath = Paths.get("configuration/stations.txt");
	private static Path ipPath = Paths.get("configuration/ip.txt");

	public ConfigurationPersistor() {
		super(stationsPath, ipPath);
	}
}


