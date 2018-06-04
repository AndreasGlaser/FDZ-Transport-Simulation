package Model.Command;

import Model.CongestionException;
import Model.IllegalSetupException;
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

    @Override
    protected void commandExecuted(){
        NetworkController.getInstance().acknowledge2(msgID, true);
    }

    @Override
    public void execute() throws IllegalSetupException{
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        try{
            Station temp = stationList.get(this.findPosInList(position));
            new PathFinder(temp, temp.getHopsToNewCarriage());
            if (this.findPosInList(position) != NOT_FOUND) {
                stationList.get(this.findPosInList(position)).
                        driveInSled(EMPTY_CARRIAGE);
                /*empty sled gets unknown id when received*/
                System.out.println("\t log: requesting empty carriage to "
                        + position);
            }
        }catch(CongestionException e){
            /*TODO Congestion detected*/
            System.out.println( "\t log: CONGESTION DETECTED\n" +
                    "\t      COULD NOT REQUEST\n"+
                    "\t      CARRIAGE TO [" + position + "]");
        }catch(IndexOutOfBoundsException e){
            throw new IllegalSetupException("No Stations in Setup");
        }
        this.commandExecuted();
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
        while (stationList.size() > 0 && stationList.get(idx).getShortCut().
                compareToIgnoreCase(position) != 0) {
            //find idx of requested station
            if(++idx == stationList.size()){return NOT_FOUND;}
        }
        return idx;
    }
}
