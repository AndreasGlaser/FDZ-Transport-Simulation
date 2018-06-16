package Model.Station;

import com.sun.istack.internal.NotNull;

public class PrevPair {

    private Station prevStation;
    private int pathTime;

    public PrevPair(@NotNull Station aPrevStation, int aPathTime){
        this.setPathTime(aPathTime);
        this.setPrevStation(aPrevStation);
    }

    public void setPrevStation(Station station){this.prevStation = station;}

    public void setPathTime(int aPathTime){this.pathTime = aPathTime;}

    public Station getPrevStation(){return this.prevStation;}

    public int getPathTime(){return this.pathTime;}
}
