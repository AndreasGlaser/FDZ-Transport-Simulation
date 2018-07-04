

import Controller.GUIController;
import Model.Facade;
import Persistance.StatePersistor;
import View.CommandLineInterface;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


public class AppStarter extends Application {

	@Override
    public void start(Stage primaryStage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/GUI.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("FDZ-Transport-Simulation");
        Scene scene = new Scene(root, 1600, 1000);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.getIcons().add(new Image("/images/FDZLogo.png"));
        GUIController guiController = loader.getController();
        if(StatePersistor.isFilesExist()){
            guiController.askForRestore(scene);
        }else {
            //load Configuration on Program start
            guiController.loadConfiguration();
            scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                final KeyCombination keyComb = new KeyCodeCombination(KeyCode.S,
                        KeyCombination.CONTROL_DOWN);
                public void handle(KeyEvent ke) {
                    if (keyComb.match(ke) && !new Facade().isConnected()) {
                        guiController.saveConfiguration();
                        ke.consume();
                    }
                }
            });
        }


        primaryStage.setOnCloseRequest(event -> {
            if(!guiController.isConfigurationSaved()){
                guiController.askForSaving(primaryStage);
                event.consume();
            }else {
                StatePersistor.deleteFiles();
                Facade facade = new Facade();
                if(facade.isConnected()){
                    new Facade().disconnect();
                }
            }

        });
    }


    public static void main(String[] args) {
        new CommandLineInterface().start();
        launch(args);
    }
}
