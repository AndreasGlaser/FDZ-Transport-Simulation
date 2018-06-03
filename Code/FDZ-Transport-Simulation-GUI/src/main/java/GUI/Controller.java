package GUI;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import persistance.ConfigurationPersistor;
import persistance.StationData;
import java.util.ArrayList;

public class Controller {
	ArrayList<StationPane> stations = new ArrayList<>();


	@FXML
	private Pane optionMenu;
	@FXML
	private Pane stationsPane;

	@FXML
	private ImageView controllerImageView;
	@FXML
	private ImageView simulatorImageView;
	@FXML
	private GridPane controllerGridPane;

	public void init(){
		controllerImageView.fitWidthProperty().bind(controllerGridPane.widthProperty());
		controllerImageView.fitHeightProperty().bind(controllerGridPane.heightProperty());
		simulatorImageView.fitWidthProperty().bind(controllerGridPane.widthProperty());
		simulatorImageView.fitHeightProperty().bind(controllerGridPane.heightProperty());
	}


	@FXML
	public void openCloseOptions(){
		if(optionMenu.isVisible()) optionMenu.setVisible(false);
		else optionMenu.setVisible(true);

	}

	@FXML
	public void addStation(){
		new StationPane(new StationData("new Station"), stationsPane, stations);
	}

	@FXML
	public void saveConfiguration(){
		ConfigurationPersistor.saveConfiguration(stations);
	}

	@FXML void loadConfiguration(){
		ConfigurationPersistor.loadConfiguration(stationsPane, stations);
	}

}
