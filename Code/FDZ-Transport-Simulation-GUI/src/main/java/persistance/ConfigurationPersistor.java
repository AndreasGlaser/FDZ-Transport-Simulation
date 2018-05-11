package persistance;

import GUI.StationPane;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import javafx.scene.layout.Pane;


import javax.json.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ConfigurationPersistor {
	private static File configurationFile = new File("configuration.txt"); //Todo: test

	public static void saveConfiguration(ArrayList<StationPane> stations){
		JsonArrayBuilder configurationObjectBuilder = Json.createArrayBuilder();
		for(StationPane station: stations){
			configurationObjectBuilder.add(
					Json.createObjectBuilder()
							.add("name", station.getName())
							.add("shortcut", station.getShortcut())
							.add("xCord", station.getXCord())
							.add("yCord", station.getYCord())
							.build());
		}

		System.out.println(configurationObjectBuilder.build());
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(configurationFile), "utf-8"))) {
			writer.write(configurationObjectBuilder.build().toString());
		} catch (IOException e) {
			e.printStackTrace();//TODO: Exceptionhandling
		}
	}

	public static void loadConfiguration(Pane rootPane, ArrayList<StationPane> stations){
		rootPane.getChildren().clear();
		stations.clear();
		try (JsonReader jsonReader = Json.createReader(new FileReader("configuration.txt")))
		{
			JsonArray jsonArray = jsonReader.readArray();
			jsonReader.close();
			for(int i = 0; i<jsonArray.size(); ++i){
				JsonObject jsonObject = jsonArray.getJsonObject(i);
				StationPane loadedStationPane = new StationPane(jsonObject.getString("name"), rootPane, stations);
				loadedStationPane.setShortcut(jsonObject.getString("shortcut"));
				loadedStationPane.setXCord(jsonObject.getJsonNumber("xCord").doubleValue());
				loadedStationPane.setYCord(jsonObject.getJsonNumber("yCord").doubleValue());
				stations.add(loadedStationPane);
				rootPane.getChildren().add(loadedStationPane);
				loadedStationPane.setXCord(loadedStationPane.getXCord());
				loadedStationPane.setYCord(loadedStationPane.getYCord());


			}
			System.out.println("read from file"+jsonArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
