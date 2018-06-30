package Persistance;

import Controller.StationController;
import Model.Facade;
import Model.Logger.LoggerInstance;
import View.AbstractStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StatePersistor extends Persistor{
    private static final Path stationsPath = Paths.get("state/stations.txt");
    private static final Path ipPath = Paths.get("state/ip.txt");
    private static final Path sledsPath = Paths.get("state/sleds.txt");
    private static final File sledsFile = sledsPath.toFile();
    private static final File stationsFile = stationsPath.toFile();
    private static final File ipFile = ipPath.toFile();

    public StatePersistor() {
        super(stationsPath, ipPath);
    }

    public static Boolean isFilesExist(){
        return sledsFile.exists() && stationsFile.exists() && ipFile.exists();
    }

    /**
     * deletes the save-files for the state, if this method is not called the application will know the application was not exited correct the last time.
     */
    public static void deleteFiles(){
        sledsFile.delete();
        stationsFile.delete();
        ipFile.delete();
    }

    public void saveState(ArrayList<AbstractStation> stations, IPAddress ipAddress){
        saveConfiguration(stations, ipAddress);
        String sledsJson = sledsToJSON(stations);
        File sledsFile = sledsPath.toFile();
        if(!sledsFile.exists()) try {
            sledsFile.getParentFile().mkdirs();
            sledsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(sledsFile), "utf-8"))) {
            writer.write(sledsJson);
        } catch (IOException e) {
            LoggerInstance.log.warn("state could not be saved.");
        }
    }

    public void loadState(Pane rootPane, ArrayList<AbstractStation> stations, IPAddress ipAddress, BorderPane messagePane){
        loadConfiguration(rootPane, stations, ipAddress, messagePane);
        String json = readJSONFromFile(sledsPath);
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Map<String,Collection<Integer>>>(){}.getType();
        Map<String, ArrayList<Integer>> sledsFromJson = gson.fromJson(json, collectionType);
        for(Map.Entry<String, ArrayList<Integer>> sleds: sledsFromJson.entrySet()){
            new Facade().setSledsInStation(sleds.getKey(), sleds.getValue());
        }
    }

    private static String sledsToJSON(ArrayList<AbstractStation> stations){
        Gson gson = new Gson();
        HashMap<String,ArrayList<Integer>> sleds = new HashMap<>();
        stations.stream().filter(station -> station.getData().getStationType().equals(StationType.STATION)).forEach(station -> sleds.put(station.getName(), ((StationController)station).getSleds()));
        return gson.toJson(sleds);
    }


}

