package View;

import Persistance.StationData;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.util.ArrayList;

public abstract class AbstractStation {
	protected StationData data;
	protected Pane viewPane;

	//helper Variables
	protected final ArrayList<BeltNode> incomingBelts = new ArrayList<>();
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
			if(prevStationsContains(i.getName())){
				BeltNode belt = new BeltNode(i, this);
				incomingBelts.add(belt);
				parent.getChildren().add(belt);
				belt.toBack();
			}
		}
	}
	public abstract void initAfterAllStationLoaded();
	public String getName(){
		return data.getName();
	}
	public abstract void setName(String name);
	public String getShortcut(){return data.getShortcut();}
	public void setShortcut(String shortcut){data.setShortcut(shortcut);}
	protected void setXCord(Double newX){
		data.setXCord(newX);
		viewPane.setTranslateX(newX);
	}
	protected void setYCord(Double newY){
		data.setYCord(newY);
		viewPane.setTranslateY(newY);
	}
	Double getXCord(){
		return data.getXCord();
	}
	Double getYCord(){
		return data.getYCord();
	}
	public ArrayList<Pair<String, Integer>> getPreviousStationsByName(){return data.getPreviousStationsByName();}
	public StationData getData(){return data;}
	protected void setData(StationData data){
		this.data = data;
		viewPane.setTranslateX(data.getXCord());
		viewPane.setTranslateY(data.getYCord());
	}
	DoubleProperty getTranslateXProperty(){
		return viewPane.translateXProperty();
	}
	DoubleProperty getTranslateYProperty(){
		return viewPane.translateYProperty();
	}
	public abstract void closeOptions();
	public abstract void setDisableOptionsButton(Boolean bool);
	protected Boolean prevStationsContains(String name){
		for(Pair<String, Integer> stationPair: data.getPreviousStationsByName()){
			if(stationPair.getKey().equals(name))return true;
		}
		return false;
	}
	protected void prevStationRemove(String name){
		ArrayList<Pair<String, Integer>> toRemove = new ArrayList<>();
		for(Pair<String, Integer> stationPair: data.getPreviousStationsByName()){
			if(stationPair.getKey().equals(name))toRemove.add(stationPair);
		}
		for(Pair<String, Integer> stationPair: toRemove){
			data.getPreviousStationsByName().remove(stationPair);
		}

	}
	protected void addPrevStation(Pair<String, Integer> pair){
		if(prevStationsContains(pair.getKey())) return;//TODO wenn vorhanen ersetzen mit neuem
		data.getPreviousStationsByName().add(pair);
	}
}
