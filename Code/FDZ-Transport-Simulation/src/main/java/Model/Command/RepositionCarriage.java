package Model.Command;

import Model.Station;
import Model.StationHandler;

import java.util.ArrayList;

/**@author Noah Lehmann*/

public class RepositionCarriage extends Command {

    private int id;
    private String position;
    private final int NOT_FOUND=-2, EMPTY_CARRIAGE=-1;

    public RepositionCarriage(int id, String position, String msgID){
        this.id = id;
        this.position = position;
        super.msgID = msgID;
    }

    //@Override
    public void execute(){
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        int idx = findIDinPos(id);
        if(idx != NOT_FOUND) {
            Station blocking = firstStationInWay(
                    /*from*/stationList.get(idx),
                    /*to*/stationList.get(findPosInList(position)));

            if (blocking == null) {
                /*No Congestion from source to destination*/
                stationList.get(findIDinPos(id)).driveOutSled(id);
                stationList.get(findPosInList(position)).driveInSled(id);
                System.out.println("\t log: reposition carriage " + id + " to "
                        + position);
            } else {
                System.out.println(blocking.getName() + " is blocking");
                /*Congestion from source to destination, carriage must wait*/
                /*first blocked station -> blocking*/
                /*TODO Stau auf weg zu neuer station*/
                System.out.println("\t log: CONGESTION DETECTED\n" +
                        "\t      COULD NOT REPOSITION\n" +
                        "\t      [" + id + "] TO [" + position + "]");
            }
        }else{
            System.out.println("\t log: ID not found\n" +
                    "\t      COULD NOT REPOSITION\n" +
                    "\t      [" + id + "] TO [" + position + "]");
        }
    }

    /**
     * Finds a Positions shortcut in the list of known Stations and
     * returns the Index, if not found -2
     * @param position
     * @return positionIndex
     */
    private int findPosInList(String position) {
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        int idx = 0;
        while (stationList.get(idx).getShortCut().
                compareToIgnoreCase(position) != 0) {
            //find idx of requested station
            if(++idx == stationList.size()){return NOT_FOUND;}
        }
        return idx;
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
            if(idx+1 == stationList.size() && id != EMPTY_CARRIAGE){
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
            }else if(idx+1 == stationList.size() && id == EMPTY_CARRIAGE){
                return NOT_FOUND;
            }
            ++idx;
        }
        return idx;
    }

    /**
     * searches the path between two stations. Returns the first station
     * closest to "from", where a congestion is detected. If no Station
     * blocks, it returns null
     * @param from
     * @param to
     * @return blockingStation
     */
    private Station firstStationInWay(Station from, Station to){
        /*--END RECURSIVE FUNCTION--*/
        if(to == from || to.getPrevStations().size() == 0) {
            return null;
        }
        /*RECURSIVE CALL*/
        Station prev = firstStationInWay(from, to.getPrevStations().get(0));
        /*TODO BACKTRACKING THE RIGHT way from-to if multiple prev-stations*/
        /*WAS PREV-STATION OCCUPIED?*/
        if(prev == null){ /*NO*/
            if(to.isOccupied()) {
                /*AM I OCCUPIED?*/
                return to; //yes return me
            }else{
                return null;//no, no prev Station or me is occupied
            }
        }else{           /*YES, PREV-STATION isOccupied*/
            return prev;
            /*
             *i don't care if i am occupied, prev station is
             *causing trouble already, return prev-station
             */
        }
    }

}
