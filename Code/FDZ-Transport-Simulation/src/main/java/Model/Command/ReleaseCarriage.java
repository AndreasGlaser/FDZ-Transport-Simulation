package Model.Command;

import Model.Logger.LoggerInstance;
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
        LoggerInstance.log.debug("Creating new ReleaseCarriage Command for ID " +id);
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
            LoggerInstance.log.info("Releasing carriage with ID " + id);
            super.commandExecuted();
        }catch(IndexOutOfBoundsException | NullPointerException e){
            LoggerInstance.log.error("ID not in List", e);
            super.error();
        }
    }
}
