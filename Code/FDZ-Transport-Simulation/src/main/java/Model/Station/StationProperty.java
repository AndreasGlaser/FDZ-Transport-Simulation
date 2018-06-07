package Model.Station;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class StationProperty {

    private SimpleIntegerProperty changedProperty;

    public StationProperty(){
        changedProperty = new SimpleIntegerProperty(0);
    }

    public SimpleIntegerProperty getChangedProperty(){
        return changedProperty;
    }
}
