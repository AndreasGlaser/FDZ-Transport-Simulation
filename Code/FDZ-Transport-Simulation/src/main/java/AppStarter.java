

import Controller.GUIController;
import Model.Facade;
import Model.Station.StationHandler;
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
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("FDZ-Transport-Simulation");
        Scene scene = new Scene(root, 1600, 1000);
        scene.getStylesheets().add("style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(700);
        primaryStage.getIcons().add(new Image("/images/FDZLogo.png"));

        //load Configuration on Program start
        GUIController GUIController = ((GUIController) loader.getController());
        GUIController.loadConfiguration();

        primaryStage.setOnCloseRequest(event -> {
            if(!GUIController.isConfigurationSaved()){
                GUIController.askForSaving(primaryStage);
                event.consume();
            }
        });

    }


    public static void main(String[] args) {
        new CommandLineInterface().start();
        launch(args);
    }
}
