package GUI;

import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Pane;
import persistance.StationData;

import java.util.ArrayList;

public interface StationLike {

	public String getName();
	public void setName(String name);
	public void setXCord(Double newX);
	public void setYCord(Double newY);
	public Double getXCord();
	public Double getYCord();
	public ArrayList<String> getPreviousStationsByName();
	public DoubleProperty getTranslateXProperty();
	public DoubleProperty getTranslateYProperty();
	public Pane getViewPane();
	public StationData getData();
	public void setData(StationData data);
	public String getShortcut();
	public void setShortcut(String shortcut);
	public void refreshBelts(Pane rootPane, ArrayList<StationLike> stations);
}
