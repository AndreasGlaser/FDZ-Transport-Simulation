package Model.Command;

import Model.Exception.CongestionException;
import Model.Exception.IllegalSetupException;
import Model.Network.NetworkController;
import Model.Station.Station;
import Model.Station.StationHandler;

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

    @Override
    public void execute() throws IllegalSetupException{
        Station temp;
        try{
            temp = StationHandler.getInstance().getStationByShortCut(position);
            System.err.println("found");
            new PathFinder(temp, temp.getHopsToNewCarriage());
            System.err.println("path found");
            temp.driveInSled(EMPTY_CARRIAGE);
            /*empty sled gets unknown id when received*/
            System.out.println("\t log: requesting empty carriage to "+ position);

            this.commandExecuted();
        }catch(CongestionException e){
            System.err.println("request congestion detected");
            // TODO: 07.06.18 Staubehandlung
            this.commandExecuted();
        }catch(IndexOutOfBoundsException e){
            System.err.println("request indexoutofbounds");
            super.error();
            throw new IllegalSetupException("No Stations in Setup");
        }catch(NullPointerException e){
            System.err.println("request nullpointer");
            super.error();
        }
    }
}
