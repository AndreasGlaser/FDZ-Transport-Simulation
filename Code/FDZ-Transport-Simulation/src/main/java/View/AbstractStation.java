package View;

import Persistance.StationData;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public abstract class AbstractStation {
	protected StationData data;
	protected Pane viewPane;

	//helper Variables
	protected ArrayList<BeltNode> incomingBelts = new ArrayList<>();
	protected Double dragXTrans = .0;
	protected Double dragYTrans = .0;
	protected Double sceneX = .0;
	protected Double sceneY = .0;


	/**
	 * removes all Belts and adds Belts to fit the current configuration
	 * @param parent the Pane the Belts will be displayed in
	 * @param stations the List of stations available in the system
	 */
	public void refreshBelts(Pane parent, ArrayList<AbstractStation> stations) {
		parent.getChildren().removeAll(incomingBelts);
		incomingBelts.clear();
		for(AbstractStation i: stations){
			if(data.getPreviousStationsByName().contains(i.getName())){
				BeltNode belt = new BeltNode(i, this);
				incomingBelts.add(belt);
				parent.getChildren().add(belt);
				belt.toBack();
			}
		}
	}
	public String getName(){
		return data.getName();
	}
	public abstract void setName(String name);
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
	public ArrayList<String> getPreviousStationsByName(){return data.getPreviousStationsByName();}
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
