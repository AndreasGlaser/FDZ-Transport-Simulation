package Persistance;

import Model.Command.Command;
import Model.Command.CommandQueue;
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
    private static final Path commandsPath = Paths.get("state/commands.txt");
    private static final File commandsFile = commandsPath.toFile();
    private static final Path toBeValidatedCommandPath = Paths.get("state/toBeValidatedCommand.txt");
    private static final File toBeValidatedCommandFile = toBeValidatedCommandPath.toFile();
    private static final Path activatedCommandsPath = Paths.get("state/activatedCommands.txt");
    private static final File activatedCommandsFile = activatedCommandsPath.toFile();
    private static final File stationsFile = stationsPath.toFile();
    private static final File ipFile = ipPath.toFile();
    private static Gson gson;
    private static Type commandsType = new TypeToken<LinkedList<Command>>(){}.getType();
    private static Type toBeValididatedCommandsType = new TypeToken<Command>(){}.getType();
    private static Type activatedCommandsType = new TypeToken<LinkedList<String>>(){}.getType();

    public StatePersistor() {
        super(stationsPath, ipPath);
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
        saveCommands();
        saveActivatedCommands();
        saveToBeValidatedCommand();
        LoggerInstance.log.debug("saved state.");
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
        String commandsJson = readJSONFromFile(commandsPath);
        String toBeValididatedCommandsJson = readJSONFromFile(toBeValidatedCommandPath);
        String activatedCommandsJson = readJSONFromFile(activatedCommandsPath);

        CommandQueue.getInstance().setQueueContent(
                gson.fromJson(commandsJson, commandsType),
                gson.fromJson(toBeValididatedCommandsJson, toBeValididatedCommandsType),
                gson.fromJson(activatedCommandsJson, activatedCommandsType));

    }

    private static String commandsToJSON(){
        return gson.toJson(CommandQueue.getInstance().getCommandQueue(), commandsType);
    }
    private String toBeValidatedCommandToJSON() {
        return gson.toJson((CommandQueue.getInstance().getToBeValidated()), toBeValididatedCommandsType);
    }
    private String activatedCommandsToJSON() {
        return gson.toJson(CommandQueue.getInstance().getActivatedList(), activatedCommandsType);
    }


}

