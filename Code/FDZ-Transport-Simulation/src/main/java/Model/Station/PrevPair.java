package Model.Station;

import Model.Logger.LoggerInstance;
import org.jetbrains.annotations.NotNull;


public class PrevPair {

    private final Station prevStation;
    private int pathTime;

    PrevPair(@NotNull Station aPrevStation, int aPathTime){
        prevStation = aPrevStation;
        pathTime = aPathTime;
    }

    void setPathTime(int aPathTime){
        LoggerInstance.log.debug("Changed PrevPairs PathTime");
        this.pathTime = aPathTime;
    }

    public Station getPrevStation(){return this.prevStation;}

    public int getPathTime(){return this.pathTime;}
}
