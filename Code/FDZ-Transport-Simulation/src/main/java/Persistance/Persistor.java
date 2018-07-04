package Persistance;

import Controller.CrossingController;
import Controller.StationController;
import Model.Facade;
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
    private final File speedModeFile;
    private final Path speedModePath;
    private static Gson gson = new Gson();

    Persistor(Path stationsPath, Path ipPath, Path speedModePath){
        this.stationsPath = stationsPath;
        this.ipPath = ipPath;
        this.stationFile = stationsPath.toFile();
        this.ipFile = ipPath.toFile();
        this.speedModePath = speedModePath;
        this.speedModeFile = speedModePath.toFile();
    }

    public void saveConfiguration(ArrayList<AbstractStation> stations, IPAddress ipAddress){
        String json = toJSON(stations);
        String ipJson = toJSON(ipAddress);
        createFilesIfNotExist();
        writeToFile(stationsPath, json);
        writeToFile(ipPath, ipJson);
        writeToFile(speedModePath, gson.toJson(new Facade().isFastTime()));
    }

    private Boolean createFilesIfNotExist() {
        if(!stationFile.exists()||!ipFile.exists()|| !speedModeFile.exists()){
            try {
                stationFile.getParentFile().mkdirs();
                stationFile.createNewFile();
                ipFile.createNewFile();
                speedModeFile.createNewFile();
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

        String speedModeJson = readJSONFromFile(speedModePath);
        new Facade().setFastTime(gson.fromJson(speedModeJson, Boolean.class));
    }
    public Boolean isConfigurationSaved(ArrayList<AbstractStation> stations, IPAddress ipAddress){
        return readJSONFromFile(stationsPath).equals(toJSON(stations)) &&
                readJSONFromFile(ipPath).equals(toJSON(ipAddress));
    }

    private void writeToFile(Path file, String content){
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file.toFile()), "utf-8"))) {
            writer.write(content);
        } catch (IOException e) {
            LoggerInstance.log.error("writing in File: "+ file +" not possible.");
        }
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
        return gson.toJson(ipAddress);
    }
    private static String toJSON(ArrayList<AbstractStation> stations){
        ArrayList<StationData> stationsData = new ArrayList<>();
        for(AbstractStation abstractStation : stations){
            stationsData.add(abstractStation.getData());
        }

        return gson.toJson(stationsData);
    }
}
