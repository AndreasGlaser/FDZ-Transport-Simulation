package GUI;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import persistance.StationData;

import java.io.IOException;
import java.util.ArrayList;

public class CrossingPane extends AbstractStation {
	private CrossingController controller;

	public CrossingPane(StationData data, Pane parent, ArrayList<AbstractStation> stations) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrossingPane.fxml"));
		try {
			viewPane = loader.load();
			parent.getChildren().add(viewPane);System.out.println(viewPane);
		} catch (IOException e) {
			e.printStackTrace();//TODO: exceptionhandling
		}
		controller = loader.getController();
		controller.init(data);
		setData(data);
		stations.add(this);

		setName(data.getName());

		refreshBelts(parent, stations);

		//make Dragable
		viewPane.setOnMousePressed(e ->{
			sceneX = e.getSceneX();
			sceneY = e.getSceneY();

			dragXTrans = viewPane.getTranslateX();
			dragYTrans = viewPane.getTranslateY();
		});
		viewPane.setOnMouseDragged(e->{
			setXCord(e.getSceneX()  - sceneX + dragXTrans);
			setYCord(e.getSceneY() - sceneY + dragYTrans);
		});

		controller.getOptionsPane().visibleProperty().addListener((observable, oldVal, newVal) -> {
			if(newVal){
				controller.getPreviousStationsPane().getChildren().clear();
				for (AbstractStation i: stations){
					if(i.equals(this))continue;
					CheckBox box = new CheckBox(i.getName());
					controller.getPreviousStationsPane().getChildren().add(box);
					if(data.getPreviousStationsByName().contains(i.getData().getName()))box.setSelected(true);
					else box.setSelected(false);
					box.selectedProperty().addListener((observable2, oldValue, newValue) -> {
						if(newValue) data.getPreviousStationsByName().add(i.getData().getName());
						else data.getPreviousStationsByName().remove(i.getData().getName());
						refreshBelts(parent, stations);

					});

				}
			}

		});
	}


	public void setName(String name){
		data.setName(name);
		controller.setName(name);
	}

}
