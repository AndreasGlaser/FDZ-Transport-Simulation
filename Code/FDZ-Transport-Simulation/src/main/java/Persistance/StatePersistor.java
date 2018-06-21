package Persistance;

import Controller.StationController;
import Model.Facade;
import View.AbstractStation;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.layout.Pane;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class StatePersistor extends Persistor{
    private static Path stationsPath = Paths.get("state/stations.txt");
    private static Path ipPath = Paths.get("state/ip.txt");
    private static Path sledsPath = Paths.get("state/sleds.txt");
    private static File sledsFile = sledsPath.toFile();
    private static File stationsFile = stationsPath.toFile();
    private static File ipFile = ipPath.toFile();

    public StatePersistor() {
        super(stationsPath, ipPath);
    }

    public static Boolean isFilesExist(){
        return sledsFile.exists() && stationsFile.exists() && ipFile.exists();
    }
    public static void deleteFiles(){
        System.out.println("deleting files");
        sledsFile.delete();
        stationsFile.delete();
        ipFile.delete();
    }


    public Boolean isStateFileExisting(){
        return true;//TODO: überprüfen ob alle drei Dateien vorhanden sind
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
            e.printStackTrace();//TODO: Exceptionhandling
        }
    }

    public void loadState(Pane rootPane, ArrayList<AbstractStation> stations, IPAddress ipAddress){
        loadConfiguration(rootPane, stations, ipAddress);
        String json = readJSONFromFile(sledsPath);
        System.out.println("loaded"+ json);
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
        stations.stream().filter(station -> station.getData().getstationType().equals(StationType.STATION)).forEach(station -> {
           sleds.put(station.getName(), ((StationController)station).getSleds());
        });
        return gson.toJson(sleds);
    }


}

