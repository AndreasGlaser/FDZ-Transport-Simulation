

import Controller.GUIController;
import Persistance.StatePersistor;
import View.CommandLineInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
            guiController.askForRestore();
        }else {
            //load Configuration on Program start
            guiController.loadConfiguration();
        }

        primaryStage.setOnCloseRequest(event -> {
            if(!guiController.isConfigurationSaved()){
                guiController.askForSaving(primaryStage);
                event.consume();
            }
            StatePersistor.deleteFiles();
        });
    }


    public static void main(String[] args) {
        new CommandLineInterface().start();
        launch(args);
    }
}
