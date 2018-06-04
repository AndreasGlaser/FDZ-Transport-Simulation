package persistance;

import GUI.CrossingPane;
import GUI.StationLike;
import GUI.StationPane;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.layout.Pane;


import javax.json.*;
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

	public static void saveConfiguration(ArrayList<StationLike> stations){
		Gson gson = new Gson();
		ArrayList<StationData> stationsData = new ArrayList<>();
		for(StationLike stationLike : stations){
			stationsData.add(stationLike.getData());
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
	public static void saveConfigurationOld(ArrayList<StationPane> stations){
		JsonArrayBuilder configurationObjectBuilder = Json.createArrayBuilder();
		for(StationPane station: stations){

			JsonObjectBuilder stationObjectBuilder = Json.createObjectBuilder()
				.add("name", station.getName())
				.add("shortcut", station.getShortcut())
				.add("xCord", station.getXCord())
				.add("yCord", station.getYCord());

			JsonArrayBuilder reachableStationsArrayBuilder = Json.createArrayBuilder();
			for(String reachableStation: station.getPreviousStationsByName())
				reachableStationsArrayBuilder.add(reachableStation);

			stationObjectBuilder.add("reachableStationsByName", reachableStationsArrayBuilder.build());
			configurationObjectBuilder.add(stationObjectBuilder.build());
		}

		System.out.println(configurationObjectBuilder.build());
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(configurationFile), "utf-8"))) {
			writer.write(configurationObjectBuilder.build().toString());
		} catch (IOException e) {
			e.printStackTrace();//TODO: Exceptionhandling
		}
	}

	public static void loadConfiguration(Pane rootPane, ArrayList<StationLike> stations) {
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
				new StationPane(stationData, rootPane, stations);
			}else if(stationData.getstationType().equals(StationType.CROSSING)){
				new CrossingPane(stationData, rootPane,stations);
			}
		}




	}

	public static void loadConfigurationOld(Pane rootPane, ArrayList<StationLike> stations){
		rootPane.getChildren().clear();
		stations.clear();
		try (JsonReader jsonReader = Json.createReader(new FileReader("configuration.txt")))
		{
			JsonArray jsonArray = jsonReader.readArray();
			jsonReader.close();
			for(int i = 0; i<jsonArray.size(); ++i){
				JsonObject jsonObject = jsonArray.getJsonObject(i);
				StationPane loadedStationPane = new StationPane(new StationData(jsonObject.getString("name"), StationType.STATION), rootPane, stations);
				loadedStationPane.setShortcut(jsonObject.getString("shortcut"));
				loadedStationPane.setXCord(jsonObject.getJsonNumber("xCord").doubleValue());
				loadedStationPane.setYCord(jsonObject.getJsonNumber("yCord").doubleValue());
				for(JsonValue name: jsonObject.getJsonArray("reachableStationsByName")){
							loadedStationPane.getPreviousStationsByName().add(name.toString());
				}

				stations.add(loadedStationPane);
				rootPane.getChildren().add(loadedStationPane.getViewPane());
				loadedStationPane.setXCord(loadedStationPane.getXCord());
				loadedStationPane.setYCord(loadedStationPane.getYCord());



			}
			for(StationLike stationLike: stations){
				stationLike.refreshBelts(rootPane, stations);
			}
			System.out.println("read from file"+jsonArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
