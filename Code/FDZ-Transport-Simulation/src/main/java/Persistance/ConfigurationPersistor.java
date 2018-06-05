package Persistance;

import Controller.CrossingController;
import Controller.StationController;
import View.AbstractStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

public class ConfigurationPersistor {
	private static File configurationFile = new File("configuration.txt"); //TODO: Veraltert wird nur für die alte Variante benötigt
	private static Path path = Paths.get("configuration.txt");

	public static void saveConfiguration(ArrayList<AbstractStation> stations){
		Gson gson = new Gson();
		ArrayList<StationData> stationsData = new ArrayList<>();
		for(AbstractStation abstractStation : stations){
			stationsData.add(abstractStation.getData());
		}

		String json = gson.toJson(stationsData);

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(configurationFile), "utf-8"))) {
			writer.write(json);
		} catch (IOException e) {
			e.printStackTrace();//TODO: Exceptionhandling
		}
		System.out.println(json);
	}

	public void loadConfiguration(Pane rootPane, ArrayList<AbstractStation> stations) {
		rootPane.getChildren().clear();
		stations.clear();
		StringBuilder json = new StringBuilder();
		try  {
			Files.readAllLines(path, StandardCharsets.UTF_8).forEach(line -> json.append(line));
			System.out.println(json.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<StationData>>(){}.getType();
		ArrayList<StationData> stationsFromJson = gson.fromJson(json.toString(), collectionType);

		for(StationData stationData: stationsFromJson){
			if(stationData.getstationType().equals(StationType.STATION)){
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/StationPane.fxml"));
				try {
					loader.setControllerFactory(c ->{
						return new StationController(stationData,rootPane,stations);
					});
					loader.load();

				} catch (IOException e) {
					e.printStackTrace();//TODO: exceptionhandling
				}
			}else if(stationData.getstationType().equals(StationType.CROSSING)){
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrossingPane.fxml"));
				try {
					loader.setControllerFactory(c ->{
						return new CrossingController(stationData,rootPane,stations);
					});
					loader.load();

				} catch (IOException e) {
					e.printStackTrace();//TODO: exceptionhandling
				}
			}
		}

		for (AbstractStation station: stations) {
			station.refreshBelts(rootPane, stations);
		}




	}
}


