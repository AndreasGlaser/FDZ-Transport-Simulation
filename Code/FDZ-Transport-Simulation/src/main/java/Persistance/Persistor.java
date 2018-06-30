package Persistance;

import Controller.CrossingController;
import Controller.StationController;
import Model.Logger.LoggerInstance;
import View.AbstractStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public abstract class Persistor {
    private final Path stationsPath;
    private final Path ipPath;
    private final File stationFile;
    private final File ipFile;

    Persistor(Path stationsPath, Path ipPath){
        this.stationsPath = stationsPath;
        this.ipPath = ipPath;
        this.stationFile = stationsPath.toFile();
        this.ipFile = ipPath.toFile();
    }

    public void saveConfiguration(ArrayList<AbstractStation> stations, IPAddress ipAddress){
        String json = toJSON(stations);
        String ipJson = toJSON(ipAddress);
        createFilesIfNotExist();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(stationFile), "utf-8"))) {
            writer.write(json);
        } catch (IOException e) {
            LoggerInstance.log.error("writing in File: "+ stationsPath +" not possible.");
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(ipFile), "utf-8"))) {
            writer.write(ipJson);
        } catch (IOException e) {
            LoggerInstance.log.error("writing in File: "+ ipPath +" not possible.");
        }
    }

    private Boolean createFilesIfNotExist() {
        if(!stationFile.exists()||!ipFile.exists()){
            try {
                stationFile.getParentFile().mkdirs();
                stationFile.createNewFile();
                ipFile.createNewFile();
            }catch (IOException e) {
                LoggerInstance.log.error("Save files could not be created.");
            }
            return true;
        }
        else return false;
    }

    public void loadConfiguration(Pane rootPane, ArrayList<AbstractStation> stations, IPAddress ipAddress, BorderPane messagePane) {
        rootPane.getChildren().clear();
        stations.clear();
        if(createFilesIfNotExist())saveConfiguration(stations, ipAddress);
        String json = readJSONFromFile(stationsPath);
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<StationData>>(){}.getType();
        ArrayList<StationData> stationsFromJson = gson.fromJson(json, collectionType);

        for(StationData stationData: stationsFromJson){
            if(stationData.getStationType().equals(StationType.STATION)){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/StationPane.fxml"));
                try {
                    loader.setControllerFactory(c -> new StationController(stationData,rootPane,stations, ipAddress, messagePane));
                    loader.load();
                } catch (IOException e) {
                    LoggerInstance.log.error("StationPane.fxml could not be loaded.");
                }
            }else if(stationData.getStationType().equals(StationType.CROSSING)){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/CrossingPane.fxml"));
                try {
                    loader.setControllerFactory(c -> new CrossingController(stationData,rootPane,stations));
                    loader.load();

                } catch (IOException e) {
                   LoggerInstance.log.error("CrossingPane.fxml could not be loaded.");
                }
            }
        }

        for (AbstractStation station: stations) {
            station.initAfterAllStationLoaded();
            station.refreshBelts(rootPane, stations);
        }

        //load IPAddress
        String ipJson = readJSONFromFile(ipPath);
        IPAddress jsonIpAddress = gson.fromJson(ipJson, IPAddress.class);
        ipAddress.setAddress(jsonIpAddress.getAddress());
        ipAddress.setPort(jsonIpAddress.getPort());


    }
    public Boolean isConfigurationSaved(ArrayList<AbstractStation> stations, IPAddress ipAddress){
        return readJSONFromFile(stationsPath).equals(toJSON(stations)) &&
                readJSONFromFile(ipPath).equals(toJSON(ipAddress));
    }

    String readJSONFromFile(Path file) {
        StringBuilder json = new StringBuilder();
        try  {
            Files.readAllLines(file, StandardCharsets.UTF_8).forEach(json::append);
        } catch (IOException e) {
            LoggerInstance.log.error("reading from File: "+ file +" not possible.");
            return "";
        }
        return json.toString();
    }
    private static String toJSON(IPAddress ipAddress) {
        Gson gson = new Gson();
        return gson.toJson(ipAddress);
    }
    private static String toJSON(ArrayList<AbstractStation> stations){
        Gson gson = new Gson();
        ArrayList<StationData> stationsData = new ArrayList<>();
        for(AbstractStation abstractStation : stations){
            stationsData.add(abstractStation.getData());
        }

        return gson.toJson(stationsData);
    }
}
