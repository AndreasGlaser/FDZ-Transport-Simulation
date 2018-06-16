package Persistance;

import javafx.util.Pair;

import java.util.ArrayList;

public class StationData {
	private String name = "";
	private String shortcut = "";
	private Double xCord = 0.;
	private Double yCord = 0.;
	private ArrayList<Pair<String, Integer>> reachableStationsByName = new ArrayList<>();
	private StationType stationType;
	private Integer hopsBack = 1;


	public StationData(String name, StationType type){
		setName(name);
		setstationType(type);
		if(type.equals(StationType.STATION))setShortcut("NS");
		else setShortcut("");
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
	public ArrayList<Pair<String, Integer>> getPreviousStationsByName(){return reachableStationsByName;}

	public void setstationType(StationType type){ this.stationType = type;}
	public StationType getstationType(){return stationType;}
	public void setHopsBack(Integer hops){
		hopsBack= hops;
	}
	public Integer getHopsBack(){
		return hopsBack;
	}
}
