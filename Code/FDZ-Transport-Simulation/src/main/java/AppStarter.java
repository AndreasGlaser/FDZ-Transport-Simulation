

import Controller.Controller;
import Model.Facade;
import Model.Network.NetworkController;
import Model.Station.StationHandler;
import View.CommandLineInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.UnknownHostException;

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
        Controller controller = ((Controller) loader.getController());
        controller.loadConfiguration();

        primaryStage.setOnCloseRequest(event -> {
            if(!controller.isConfigurationSaved()){
                controller.askForSaving(primaryStage);
                event.consume();
            }
        });

        if (StationHandler.getInstance().getStationByName("Storage").getPrevStations().size() == 0){
            new Facade().addPrevStation("Storage", "Robot");
            new Facade().addPrevStation("Storage", "I/O");
            new Facade().addPrevStation("Robot", "Storage");
            new Facade().addPrevStation("I/O", "Robot");
            try {
                new Facade().setHopsToNewCarriage("Robot", 2);
                new Facade().setHopsToNewCarriage("Storage", 1);
                new Facade().setHopsToNewCarriage("I/O", 1);
            }catch(Exception e){}
        }

    }


    public static void main(String[] args) {
        new CommandLineInterface().start();
        launch(args);
    }
}
