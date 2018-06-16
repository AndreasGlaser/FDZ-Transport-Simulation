package Model.Command;

import Model.Station.PrevPair;
import Model.Station.Station;

public class TimeMode {
    public static boolean fastModeActivated = true;

    public static int findTimeForPath(Station from, Station to){
        for(PrevPair prevPair : to.getPrevStations()){
            if(prevPair.getPrevStation() == from){
                return prevPair.getPathTime();
            }
        }
        return 0;
    }
}
