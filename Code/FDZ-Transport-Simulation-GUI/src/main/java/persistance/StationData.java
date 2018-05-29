package persistance;

import java.util.ArrayList;

public class StationData {
	private String name = "";
	private String shortcut = "";
	private Double xCord = 0.;
	private Double yCord = 0.;
	private ArrayList<String> reachableStationsByName = new ArrayList<>();

	public StationData(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getShortcut(){return shortcut;}
	public void setShortcut(String shortcut){this.shortcut = shortcut;}
	public void setXCord(Double newX){
		xCord = newX;
	}
	public void setYCord(Double newY){
		yCord = newY;
	}
	public Double getXCord(){
		return xCord;
	}
	public Double getYCord(){
		return yCord;
	}
	public ArrayList<String> getReachableStationsByName(){return reachableStationsByName;}
}
