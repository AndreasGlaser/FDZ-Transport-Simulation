package Model.Station;

import javafx.beans.property.SimpleBooleanProperty;

public class StationProperty {

    private SimpleBooleanProperty changedProperty;

    public StationProperty(){
        changedProperty = new SimpleBooleanProperty(true);
    }

    public SimpleBooleanProperty getChangedProperty(){
        return changedProperty;
    }
}
