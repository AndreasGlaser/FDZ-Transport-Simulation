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
import java.util.ArrayList;
import java.util.Collection;

public abstract class Persistor {
    protected Path stationsPath;
    protected Path ipPath;

    public Persistor(Path stationsPath, Path ipPath){
        this.stationsPath = stationsPath;
        this.ipPath = ipPath;
    }

    public void saveConfiguration(ArrayList<AbstractStation> stations, IPAddress ipAddress){
        String json = toJSON(stations);
        String ipJson = toJSON(ipAddress);
        File stationFile = stationsPath.toFile();
        File ipFile = ipPath.toFile();
        if(!stationFile.exists()) try {
            stationFile.getParentFile().mkdirs();
            stationFile.createNewFile();
            ipFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(stationsPath.toFile()), "utf-8"))) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();//TODO: Exceptionhandling
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(ipPath.toFile()), "utf-8"))) {
            writer.write(ipJson);
        } catch (IOException e) {
            e.printStackTrace();//TODO: Exceptionhandling
        }
    }
    public void loadConfiguration(Pane rootPane, ArrayList<AbstractStation> stations, IPAddress ipAddress) {
        rootPane.getChildren().clear();
        stations.clear();
        String json = readJSONFromFile(stationsPath);
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<StationData>>(){}.getType();
        ArrayList<StationData> stationsFromJson = gson.fromJson(json, collectionType);

        for(StationData stationData: stationsFromJson){
            if(stationData.getstationType().equals(StationType.STATION)){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/StationPane.fxml"));
                try {
                    loader.setControllerFactory(c ->{
                        return new StationController(stationData,rootPane,stations, ipAddress);
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
        if(readJSONFromFile(stationsPath).equals(toJSON(stations)) &&
                readJSONFromFile(ipPath).equals(toJSON(ipAddress)))return true;
        else return false;
    }

    protected String readJSONFromFile(Path file) {
        StringBuilder json = new StringBuilder();
        try  {
            Files.readAllLines(file, StandardCharsets.UTF_8).forEach(line -> json.append(line));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
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
