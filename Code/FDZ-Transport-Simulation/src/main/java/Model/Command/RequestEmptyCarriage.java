package Model.Command;

import Model.Network.NetworkController;
import Model.Station;
import Model.StationHandler;

import java.util.ArrayList;

/**@author Noah Lehmann*/

public class RequestEmptyCarriage extends Command {

    private String position;
    private final int EMPTY_CARRIAGE=-1, NOT_FOUND=-2;

    public RequestEmptyCarriage(String position, String msgID){
        this.position = position;
        super.msgID = msgID;
    }

    /*Overrides super class method*/
    private void commandUnderstood(){
        NetworkController.getInstance().acknowledge2(msgID, true);
    }

    @Override
    public void execute(){
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        Station temp = stationList.get(this.findPosInList(position));
        Station blocking = firstStationInWay(temp.getHopsToNewCarriage(), temp);
        if(blocking == null) {
            if (this.findPosInList(position) != NOT_FOUND) {
                stationList.get(this.findPosInList(position)).
                        driveInSled(EMPTY_CARRIAGE); //@Andreas leerer Schlitten
                /*empty sled gets unknown id when received*/
                System.out.println("\t log: requesting empty carriage to "
                        + position);
            }
        }else{
            /*TODO Congestion detected*/
            /*first station which is congested -> blocking*/
            System.out.println( "\t log: CONGESTION DETECTED\n" +
                    "\t      COULD NOT REQUEST\n"+
                    "\t      CARRIAGE TO [" + position + "]");
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
     * Searches for a blocking station on path from hops back=0
     * to Station. Recursive lookback until hopsBack is 0. Returns
     * first blocking station with the smallest hopsBack, or returns
     * null if no station is blocking
     * @param hopsBack
     * @param to
     * @return blockingStation
     */
    private Station firstStationInWay(int hopsBack, Station to){
        /*returns null if no congestion on way or
         *first station, which was congested in way */
        if(hopsBack == 1 || to.getPrevStations().size() == 0){
            /*END RECURSIVE FUNCTION*/
            if(to.isOccupied()){
                /*am i congested? 1 more hop to go*/
                return to;
            }else{
                /*1 hop back is ok, if i'm not congested*/
                return null;
            }
        }
        /*RECURSIVE CALL*/
        Station prev = firstStationInWay(hopsBack-1,
                to.getPrevStations().get(0));
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
