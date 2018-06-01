package GUI;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import persistance.StationData;

import java.io.IOException;
import java.util.ArrayList;

public class StationPane{

	private StationData data = new StationData("");
	private Pane viewPane;

	//helper Variables
	private ArrayList<BeltNode> outgoingBelts = new ArrayList<>();
	private Double dragXTrans = .0;
	private Double dragYTrans = .0;
	private Double sceneX = .0;
	private Double sceneY = .0;
	private StationController controller;

	public StationPane(StationData data, Pane parent, ArrayList<StationPane> stations){

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/StationPane.fxml"));
		try {
			viewPane = loader.load();
			parent.getChildren().add(viewPane);
			System.out.println(viewPane);
		} catch (IOException e) {
			e.printStackTrace();//TODO: exceptionhandling
		}
		controller = (StationController)loader.getController();
		controller.init();
		setData(data);
		stations.add(this);
		refreshBelts(parent, stations);



		setName(data.getName());
		controller.setSledText("Empty");


		controller.getstationOptionsPane().visibleProperty().addListener((observable, oldVal, newVal) -> {
			if(newVal){
				controller.getPreviousStationsPane().getChildren().clear();
				for (StationPane i: stations){
					if(i.equals(this))continue;
					CheckBox box = new CheckBox(i.getName());
					controller.getPreviousStationsPane().getChildren().add(box);
					if(data.getReachableStationsByName().contains(i.getData().getName()))box.setSelected(true);
					else box.setSelected(false);
					box.selectedProperty().addListener((observable2, oldValue, newValue) -> {
						if(newValue) data.getReachableStationsByName().add(i.getData().getName());
						else data.getReachableStationsByName().remove(i.getData().getName());
						refreshBelts(parent, stations);

					});

				}
			}

		});






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



	}

	/**
	 * removes all Belts and adds Belts to fit the current configuration
	 * @param parent the Pane the Belts will be displayed in
	 * @param stations the List of stations available in the system
	 */
	public void refreshBelts(Pane parent, ArrayList<StationPane> stations) {
		parent.getChildren().removeAll(outgoingBelts);
		for(StationPane i: stations){
			if(data.getReachableStationsByName().contains(i.getName())){
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
	public String getShortcut(){return data.getShortcut();}
	public void setShortcut(String shortcut){data.setShortcut(shortcut);}
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
	public ArrayList<String> getReachableStationsByName(){return data.getReachableStationsByName();}
	public StationData getData(){return data;}
	public void setData(StationData data){
		this.data = data;
		viewPane.setTranslateX(data.getXCord());
		viewPane.setTranslateY(data.getYCord());
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
