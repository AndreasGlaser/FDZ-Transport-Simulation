package Model.Command;

import Model.Exception.CongestionException;
import Model.Exception.IllegalSetupException;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.util.LinkedList;

import static java.lang.Thread.sleep;

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

/*
    //@Override
    public void execute() throws IllegalSetupException {
        try {
            Station from = StationHandler.getInstance().getStationBySledID(id);
            Station to = StationHandler.getInstance().getStationByShortCut(position);
            System.err.println("trying path");
            new PathFinder(from, to);
            System.err.println("path found");
            //No Congestion from source to destination
            from.driveOutSled();
            System.err.println("drove out");
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
            e.printStackTrace();
            // TODO: 07.06.18 stations not found
        }

    }
*/

    @Override
    public void execute() throws IllegalSetupException{
        Thread t1 = new Thread(()->{
            while(true){
                try{
                    sleep(5000);
                }catch (Exception e){
                    break;
                }
                System.err.println("t1 running");
            }
        });
        t1.start();

        new Thread(() ->{
            Station from = null, to = null;
            try {
                from = StationHandler.getInstance().getStationBySledID(id);
                to = StationHandler.getInstance().getStationByShortCut(position);
            }catch(NullPointerException e){
                super.error();
                e.printStackTrace();
                // TODO: 07.06.18 stations not found
            }catch(IndexOutOfBoundsException e){
                super.error();
            }
            try{
                LinkedList<Station> path = new PathFinder(from, to).getPath();
                path.getFirst().driveOutSled();
                path.stream().filter(s -> (s!=path.getFirst() && s!=path.getLast())).forEachOrdered(station -> {
                    station.driveInSled(id);
                    station.driveOutSled();
                });
                path.getLast().driveInSled(id);
            }catch(CongestionException e){
            }catch(NullPointerException e){
                super.error();
            }catch(IllegalSetupException e){
                super.error();
            }
        }).start();

        t1.interrupt();
        System.err.println("end repo");

    }
}
