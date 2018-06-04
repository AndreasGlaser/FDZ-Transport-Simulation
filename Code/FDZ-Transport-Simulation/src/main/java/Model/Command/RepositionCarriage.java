package Model.Command;

import Model.CongestionException;
import Model.IllegalSetupException;
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
    public void execute() throws IllegalSetupException {
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        int idx = findIDinPos(id);
        if(idx != NOT_FOUND) {
            try {
                Station from = stationList.get(findIDinPos(id));
                Station to = stationList.get(findPosInList(position));
                new PathFinder(from, to);
                /*No Congestion from source to destination*/
                from.driveOutSled(id);
                to.driveInSled(id);
                System.out.println("\t log: reposition carriage " + id + " to "
                        + position);
            }catch(CongestionException e) {
                System.out.println(e.getBlockingStation().getName() + " is blocking");
                /*Congestion from source to destination, carriage must wait*/
                /*first blocked station -> blocking*/
                /*TODO Stau auf weg zu neuer station*/
                System.out.println("\t log: CONGESTION DETECTED\n" +
                        "\t      COULD NOT REPOSITION\n" +
                        "\t      [" + id + "] TO [" + position + "]");
            }catch (IndexOutOfBoundsException e){
                throw new IllegalSetupException("No Stations in Setup");
            }
        }else{
            System.out.println("\t log: ID not found\n" +
                    "\t      COULD NOT REPOSITION\n" +
                    "\t      [" + id + "] TO [" + position + "]");
        }
        super.commandExecuted();
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
}
