package Persistance;

import Controller.StationController;
import Model.Command.Command;
import Model.Command.CommandQueue;
import Model.Facade;
import Model.Logger.LoggerInstance;
import View.AbstractStation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    private static final Path speedModePath = Paths.get("state/speedMode.txt");
    private static final Path commandsPath = Paths.get("state/commands.txt");
    private static final File commandsFile = commandsPath.toFile();
    private static final Path toBeValidatedCommandPath = Paths.get("state/toBeValidatedCommand.txt");
    private static final File toBeValidatedCommandFile = toBeValidatedCommandPath.toFile();
    private static final Path activatedCommandsPath = Paths.get("state/activatedCommands.txt");
    private static final File activatedCommandsFile = activatedCommandsPath.toFile();
    private static final File stationsFile = stationsPath.toFile();
    private static final File ipFile = ipPath.toFile();
    private static final Path sledsPath = Paths.get("state/sleds.txt");
    private static final File sledsFile = sledsPath.toFile();
    private static Gson gson;
    private static Type commandsType = new TypeToken<ArrayList<Command>>(){}.getType();
    private static Type toBeValididatedCommandsType = new TypeToken<Command>(){}.getType();
    private static Type activatedCommandsType = new TypeToken<LinkedList<String>>(){}.getType();

    public StatePersistor() {
        super(stationsPath, ipPath, speedModePath);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Command.class, new CommandSerializer());
        gson = gsonBuilder.create();
    }

    public static Boolean isFilesExist(){
        return commandsFile.exists() && stationsFile.exists() && ipFile.exists() && toBeValidatedCommandFile.exists() && activatedCommandsFile.exists();
    }

    /**
     * deletes the save-files for the state, if this method is not called the application will know the application was not exited correct the last time.
     */
    public static void deleteFiles(){
        commandsFile.delete();
        activatedCommandsFile.delete();
        toBeValidatedCommandFile.delete();
        stationsFile.delete();
        ipFile.delete();
        sledsFile.delete();
        speedModePath.toFile().delete();
    }

    private void createFilesIfNotExist(){
        if(!commandsFile.exists()|| !activatedCommandsFile.exists() || !toBeValidatedCommandFile.exists()) try {
            commandsFile.getParentFile().mkdirs();
            commandsFile.createNewFile();
            activatedCommandsFile.createNewFile();
            toBeValidatedCommandFile.createNewFile();
        } catch (IOException e) {
            LoggerInstance.log.warn("Files to save the state could not be created.");
        }
    }

    public void saveState(ArrayList<AbstractStation> stations, IPAddress ipAddress){
        saveConfiguration(stations, ipAddress);
        createFilesIfNotExist();
        saveSleds(stations);
        saveCommands();
        saveActivatedCommands();
        saveToBeValidatedCommand();
        LoggerInstance.log.debug("saved state.");
    }

    private void saveSleds(ArrayList<AbstractStation> stations) {
        String sledsJson = sledsToJSON(stations);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(sledsFile), "utf-8"))) {
            writer.write(sledsJson);
        } catch (IOException e) {
            LoggerInstance.log.warn("state could not be saved.");
        }
    }

    private void saveToBeValidatedCommand() {
        String toBeValidatedCommandJson = toBeValidatedCommandToJSON();
        System.out.println(toBeValidatedCommandJson);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(toBeValidatedCommandFile), "utf-8"))) {
            writer.write(toBeValidatedCommandJson);
        } catch (IOException e) {
            LoggerInstance.log.warn("state could not be saved.");
        }
    }

    private void saveActivatedCommands() {
        String activatedCommandsJson = activatedCommandsToJSON();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(activatedCommandsFile), "utf-8"))) {
            writer.write(activatedCommandsJson);
        } catch (IOException e) {
            LoggerInstance.log.warn("state could not be saved.");
        }
    }

    private void saveCommands() {
        String commandsJson = commandsToJSON();
        System.out.println(commandsJson);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(commandsFile), "utf-8"))) {
            writer.write(commandsJson);
        } catch (IOException e) {
            LoggerInstance.log.warn("state could not be saved.");
        }
    }

    public void loadState(Pane rootPane, ArrayList<AbstractStation> stations, IPAddress ipAddress, BorderPane messagePane){
        loadConfiguration(rootPane, stations, ipAddress, messagePane);

        String sledsJson = readJSONFromFile(sledsPath);
        Type collectionType = new TypeToken<Map<String,Collection<Integer>>>(){}.getType();
        Map<String, ArrayList<Integer>> sledsFromJson = gson.fromJson(sledsJson, collectionType);
        for(Map.Entry<String, ArrayList<Integer>> sleds: sledsFromJson.entrySet()){
            new Facade().setSledsInStation(sleds.getKey(), sleds.getValue());
        }

        //load commands
        String commandsJson = readJSONFromFile(commandsPath);
        String toBeValidatedCommandsJson = readJSONFromFile(toBeValidatedCommandPath);
        String activatedCommandsJson = readJSONFromFile(activatedCommandsPath);

        Thread loadCommandsThread = new Thread(()->{
            //the commands must be convertet back to an LinkedList, because it must be saved as ArrayList
            LinkedList<Command> commands = new LinkedList<>(gson.fromJson(commandsJson, commandsType));
            CommandQueue.getInstance().setQueueContent(
                    commands,
                    gson.fromJson(toBeValidatedCommandsJson, toBeValididatedCommandsType),
                    gson.fromJson(activatedCommandsJson, activatedCommandsType));
        });
        loadCommandsThread.start();

    }

    public void loadOnlyIPAddress(IPAddress ipAddress){
        String ipJson = readJSONFromFile(ipPath);
        IPAddress jsonIpAddress = gson.fromJson(ipJson, IPAddress.class);
        ipAddress.setAddress(jsonIpAddress.getAddress());
        ipAddress.setPort(jsonIpAddress.getPort());
    }

    private static String commandsToJSON(){
        //the LinkedList for the commands must be convertet to a ArrayList because gson cant serialize LinkedLists
        ArrayList<Command> commands = new ArrayList<>(CommandQueue.getInstance().getCommandQueue());
        return gson.toJson(commands, commandsType);
    }
    private String toBeValidatedCommandToJSON() {
        return gson.toJson((CommandQueue.getInstance().getToBeValidated()), toBeValididatedCommandsType);
    }
    private String activatedCommandsToJSON() {
        return gson.toJson(CommandQueue.getInstance().getActivatedList(), activatedCommandsType);
    }
    private static String sledsToJSON(ArrayList<AbstractStation> stations){
        Gson gson = new Gson();
        HashMap<String,ArrayList<Integer>> sleds = new HashMap<>();
        stations.stream().filter(station -> station.getData().getStationType().equals(StationType.STATION)).forEach(station -> {
            ArrayList<Integer> sled = new ArrayList<>();
            if(((StationController)station).getSleds().size() >0) sled.add(((StationController)station).getSleds().get(0));
            sleds.put(station.getName(),sled);
        });
        return gson.toJson(sleds);
    }

}

