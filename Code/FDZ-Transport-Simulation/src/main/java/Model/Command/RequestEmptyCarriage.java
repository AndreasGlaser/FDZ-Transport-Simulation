package Model.Command;

import Model.Exception.CongestionException;
import Model.Exception.IllegalSetupException;
import Model.Network.NetworkController;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.util.LinkedList;

import static java.lang.Thread.sleep;

/**@author Noah Lehmann*/

public class RequestEmptyCarriage extends Command {

    private String position;
    private final int EMPTY_CARRIAGE=-1;

    /**
     *
     * @param position shortCut of Station which requests an empty carriage
     * @param msgID message ID of the incoming message that initiated the Command
     */
    RequestEmptyCarriage(String position, String msgID){
        this.position = position;
        super.msgID = msgID;
    }

    /**
     * Special acknowledgment 1 for the requestEmptyCarriage Command
     */
    @Override
    protected void commandExecuted(){
        NetworkController.getInstance().acknowledge2(msgID, true);
    }

    // TODO: 16.06.18 doc und ack2 
    @Override
    public void execute() throws IllegalSetupException{
        
        new Thread(() ->{
            Station temp;
            try {
                temp = StationHandler.getInstance().getStationByShortCut(position);
                LinkedList<Station> path = new PathFinder(temp, temp.getHopsToNewCarriage()).getPath();
                path.stream().filter(station -> station != path.getLast()).forEachOrdered(station-> {
                    station.driveInSled(-1);
                    station.driveOutSled();
                });
                path.getLast().driveInSled(-1);
                commandExecuted();
            }catch(CongestionException e){
                System.err.println(e.getMessage());
            }catch(IllegalSetupException e){
                System.err.println(e.getMessage());
                error();
            }}).start();
        
    }
}
