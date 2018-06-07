package Model.Command;

import Model.Exception.CongestionException;
import Model.Exception.IllegalSetupException;
import Model.Network.NetworkController;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.util.ArrayList;

/**@author Noah Lehmann*/

public class RequestEmptyCarriage extends Command {

    private String position;
    private final int EMPTY_CARRIAGE=-1;

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
        Station temp;
        try{
            temp = stationList.get(this.findPosInList(position));
            new PathFinder(temp, temp.getHopsToNewCarriage());
            temp.driveInSled(EMPTY_CARRIAGE);
            /*empty sled gets unknown id when received*/
            System.out.println("\t log: requesting empty carriage to "
                        + position);
            this.commandExecuted();
        }catch(CongestionException e){
            Station blocking = e.getBlockingStation();
            blocking.driveInSled(EMPTY_CARRIAGE);
            System.out.println( "\t log: CONGESTION DETECTED\n" +
                    "\t      COULD NOT REQUEST\n"+
                    "\t      CARRIAGE TO [" + position + "]"+
                    "\t      Carriage blocked in "+blocking.getName());
            this.commandExecuted();
        }catch(IndexOutOfBoundsException e){
            super.error();
            throw new IllegalSetupException("No Stations in Setup");
        }catch(NullPointerException e){
            super.error();
        }
    }

    /**
     * Finds a Positions shortcut in the list of known Stations and
     * returns the Index, if not found -2
     * @param position
     * @return positionIndex
     */
    private int findPosInList(String position) throws NullPointerException{
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        int idx = 0;
        while (stationList.size() > 0 && stationList.get(idx).getShortCut().
                compareToIgnoreCase(position) != 0) {
            //find idx of requested station
            if(++idx == stationList.size()){throw new NullPointerException("Station Name not in List");}
        }
        return idx;
    }
}
