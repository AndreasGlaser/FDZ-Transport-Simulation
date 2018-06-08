package Model.Command;

import Model.Exception.IllegalSetupException;
import Model.Station.Station;
import Model.Station.StationHandler;

/**@author nlehmann*/

public class ReleaseCarriage extends Command {

    private int id;

    /**
     * @param id id of sled to release
     * @param msgID message id of incoming message initiating the Command
     */
    ReleaseCarriage(int id, String msgID){
        this.id = id;
        super.msgID = msgID;
    }

    /**
     * The Method, which executes the Command RELEASE_CARRIAGE
     */
    @Override
    public void execute() throws IllegalSetupException {
        try {
            Station station = StationHandler.getInstance().getStationBySledID(id);
            station.driveOutSled();
            System.out.println("\t log: releasing carriage with id " + id);
            super.commandExecuted();
        }catch(IndexOutOfBoundsException e){
            throw new IllegalSetupException("No Stations in Setup");
        }catch(NullPointerException e){
            // TODO: 07.06.18 log error id not found
        }
    }
}
