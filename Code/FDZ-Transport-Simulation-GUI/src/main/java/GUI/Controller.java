package GUI;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class Controller {
	ArrayList<StationPane> stations = new ArrayList<>();

	Double dragXTrans;
	Double dragYTrans;
	@FXML
	private Pane optionMenu;
	@FXML
	private Pane stationsPane;

	public Pane getPane(){
		return stationsPane;
	}


	@FXML
	public void openCloseOptions(){
		if(optionMenu.isVisible()) optionMenu.setVisible(false);
		else optionMenu.setVisible(true);

	}

	@FXML
	public void addStation(){
		StationPane station = new StationPane("new Station", stationsPane, stations);
		stationsPane.getChildren().add(station);
		station.setOnMousePressed(e ->{
			StationPane aStation = ((StationPane)e.getTarget());

			aStation.setXCord(e.getSceneX());
			aStation.setYCord(e.getSceneY());

			dragXTrans = aStation.getTranslateX();
			dragYTrans = aStation.getTranslateY();
		});
		station.setOnMouseDragged(e->{
			StationPane aStation = ((StationPane)e.getTarget());
			System.out.println("xcord: "+aStation.getXCord());
			System.out.println("sceneX: "+ e.getSceneX());
			System.out.println("scenex - xcord: "+ (e.getSceneX() - aStation.getXCord())+ dragXTrans);
			aStation.setTranslateX(e.getSceneX() - aStation.getXCord() + dragXTrans);
			aStation.setTranslateY(e.getSceneY() - aStation.getYCord() + dragYTrans);
		});

		stations.add(station);
	}

}
