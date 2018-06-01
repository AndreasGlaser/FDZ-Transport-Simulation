package Model.Command;

import Model.Station;
import Model.StationHandler;

import java.util.ArrayList;

/**@author Noah Lehmann*/

public class ReleaseCarriage extends Command {

    private int id;
    private final int NOT_FOUND=-2, EMPTY_CARRIAGE=-1;

    public ReleaseCarriage(int id, String msgID){

    }

    /**
     * The Method, which executes the Command RELEASE_CARRIAGE
     */
    //@Override
    public void execute(){
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        System.out.println("\t log: releasing carriage with id " + id);
        if(this.findIDinPos(id) != NOT_FOUND){
            stationList.get(findIDinPos(id)).driveOutSled(id);
        } else{
            /*TODO ID ist in keiner Station oder im Stau einer Station*/
        }
    }

    /**
     * Finds an Sled-ID in the Stations known to the System.
     * returns te index if found, else it searches for an empty sled.
     * sets the sleds id to the unknown id or only returns -2 if no empty
     * sled is found.
     * @param id
     * @return positionIndex
     */
    private int findIDinPos(int id){
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        int idx=0;
        while(stationList.get(idx).getSledInside() != id){
            //in which station is ID
            if(++idx == stationList.size()){
                /*id unknown*/
                idx = findIDinPos(EMPTY_CARRIAGE);//find empty sled
                /*finds first empty carriage, expects only one empty carriage*/
                if(idx != NOT_FOUND){
                    stationList.get(idx).setSledInside(id);
                    /*empty carriage gets unknown id*/
                    return idx;
                    /*empty carriage found, give unknown id to carriage*/
                }else{
                    return NOT_FOUND;
                    /*no empty sled found, return -2*/
                }
            }
        }
        return idx;
    }
}
