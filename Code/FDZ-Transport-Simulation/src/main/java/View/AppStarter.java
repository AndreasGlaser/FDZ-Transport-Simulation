package View;

import Controller.Controller;
import Model.Network.NetworkController;
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
        controller.init();



        /*CommandLineInterface cli = new CommandLineInterface();
        cli.run();
        while(cli.isAlive()) {
            try {
                cli.join();
            } catch (InterruptedException e) {
                continue;
            }
        }*/
    }


    public static void main(String[] args) {
        launch(args);
    }
}
