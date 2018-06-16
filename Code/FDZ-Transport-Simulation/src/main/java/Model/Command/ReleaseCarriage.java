package Model.Command;

import Model.Station.Station;
import Model.Station.StationHandler;

/**@author nlehmann*/

public class ReleaseCarriage extends Command {

    private final int id;

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
    public void execute() {
        try {
            Station station = StationHandler.getInstance().getStationBySledID(id);
            station.driveOutSled();
            System.out.println("\t log: releasing carriage with id " + id);
            super.commandExecuted();
        }catch(IndexOutOfBoundsException | NullPointerException e){
            super.error();
            // TODO: 16.06.18 log illegal setup
        }
    }
}
