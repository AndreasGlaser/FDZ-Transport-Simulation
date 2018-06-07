package Model.Command;

import Model.Exception.CongestionException;
import Model.Exception.IllegalSetupException;
import Model.Station.Station;
import Model.Station.StationHandler;

/**@author nlehmann*/

public class RepositionCarriage extends Command {

    private int id;
    private String position;
    private final String CONGESTED_ERROR;

    /**
     * @param id id of carriage to reposition
     * @param position shortCut of Station to move id to
     * @param msgID message ID of the incoming message initiating the Command
     */
    RepositionCarriage(int id, String position, String msgID){
        this.id = id;
        this.position = position;
        super.msgID = msgID;
        CONGESTED_ERROR =
                "\t log: CONGESTION DETECTED\n" +
                "\t      COULD NOT REPOSITION\n" +
                "\t      [" + id + "] TO [" + position + "]";

    }

    //@Override
    public void execute() throws IllegalSetupException {
        try {
            Station from = StationHandler.getInstance().getStationBySledID(id);
            Station to = StationHandler.getInstance().getStationByShortCut(position);
            new PathFinder(from, to);
            /*No Congestion from source to destination*/
            from.driveOutSled();

            // TODO: 07.06.18 test if blocked command is stuck here
            to.driveInSled(id);
            System.out.println("\t log: reposition carriage " + id + " to " + position);
            super.commandExecuted();
        }catch(CongestionException e) {
            System.out.println(e.getBlockingStation().getName() + " is blocking");
            System.out.println(CONGESTED_ERROR);
            // TODO: 07.06.18 handle congestion
        }catch (IndexOutOfBoundsException e){
            super.error();
            throw new IllegalSetupException("No Stations in Setup");
        }catch(NullPointerException e){
            super.error();
            // TODO: 07.06.18 stations not found
        }

    }
}
