package GUI;

import com.google.gson.reflect.TypeToken;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import persistance.StationData;
import persistance.StationType;

import java.io.IOException;
import java.util.ArrayList;

public class CrossingPane implements StationLike{
	private Pane viewPane;
	private StationData data = new StationData("", StationType.CROSSING);

	//helper Variables
	private CrossingController controller;
	private ArrayList<BeltNode> outgoingBelts = new ArrayList<>();
	private Double dragXTrans = .0;
	private Double dragYTrans = .0;
	private Double sceneX = .0;
	private Double sceneY = .0;

	public CrossingPane(StationData data, Pane parent, ArrayList<StationLike> stations) {

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
				for (StationLike i: stations){
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
	/**
	 * removes all Belts and adds Belts to fit the current configuration
	 * @param parent the Pane the Belts will be displayed in
	 * @param stations the List of stations available in the system
	 */
	public void refreshBelts(Pane parent, ArrayList<StationLike> stations) {
		parent.getChildren().removeAll(outgoingBelts);
		outgoingBelts.clear();
		for(StationLike i: stations){
			if(data.getPreviousStationsByName().contains(i.getName())){
				BeltNode belt = new BeltNode(this, i);
				outgoingBelts.add(belt);
				parent.getChildren().add(belt);
				belt.toBack();
			}
		}
	}


	public String getName(){
		return data.getName();
	}
	public void setName(String name){
		data.setName(name);
		controller.setName(name);
	}
	public void setXCord(Double newX){
		data.setXCord(newX);
		viewPane.setTranslateX(newX);
	}
	public void setYCord(Double newY){
		data.setYCord(newY);
		viewPane.setTranslateY(newY);
	}
	public Double getXCord(){
		return data.getXCord();
	}
	public Double getYCord(){
		return data.getYCord();
	}
	public ArrayList<String> getPreviousStationsByName(){return data.getPreviousStationsByName();}
	public StationData getData(){return data;}
	public void setData(StationData data){
		this.data = data;
		viewPane.setTranslateX(data.getXCord());
		viewPane.setTranslateY(data.getYCord());
	}

	@Override
	public String getShortcut() {
		return null;
	}

	@Override
	public void setShortcut(String shortcut) {

	}

	public DoubleProperty getTranslateXProperty(){
		return viewPane.translateXProperty();
	}
	public DoubleProperty getTranslateYProperty(){
		return viewPane.translateYProperty();
	}
	public Pane getViewPane(){
		return viewPane;
	}
}
