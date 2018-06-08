package Model.Station;

import javafx.beans.property.SimpleIntegerProperty;

public class StationProperty {

    private SimpleIntegerProperty changedProperty;

    public StationProperty(){
        changedProperty = new SimpleIntegerProperty(0);
    }

    public SimpleIntegerProperty changedProperty(){
        return changedProperty;
    }

    public void setChanged(){
        System.err.println("changed value");
        changedProperty.set(changedProperty.getValue()+1);
    }
}
