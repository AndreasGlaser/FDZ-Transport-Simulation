package Model.Command;

import Model.Exception.IllegalSetupException;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.util.LinkedList;

import static java.lang.Thread.sleep;

/**@author nlehmann*/

public class RepositionCarriage extends Command {

    private final int id;
    private final String position;

    /**
     * @param id id of carriage to reposition
     * @param position shortCut of Station to move id to
     * @param msgID message ID of the incoming message initiating the Command
     */
    RepositionCarriage(int id, String position, String msgID){
        this.id = id;
        this.position = position;
        super.msgID = msgID;
    }

    // TODO: 16.06.18 javadoc und ack2
    @Override
    public void execute(){

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
            try {
                LinkedList<Station> path = new PathFinder(from, to).getPath();
                if(TimeMode.fastModeActivated) {
                    path.getFirst().driveOutSled();
                    path.stream().filter(s -> (s != path.getFirst() && s != path.getLast())).forEachOrdered(station -> {
                        station.driveInSled(id);
                        station.driveOutSled();
                    });
                }else{
                    try {
                        sleep(TimeMode.findTimeForPath(path.getFirst(), path.get(1)));
                    }catch (InterruptedException | IndexOutOfBoundsException e){
                        // TODO: 16.06.18 debug interruption
                    }
                    for (int i=1; i<path.size()-1; i++){
                        try{
                            path.get(i).driveInSled(-1);
                            path.get(i).driveOutSled();
                            sleep(TimeMode.findTimeForPath(path.get(i), path.get(i+1)));
                        }catch(InterruptedException e){
                            // TODO: 16.06.18 debug interruption
                        }
                    }
                }
                path.getLast().driveInSled(id);
                super.commandExecuted();
            }catch(NullPointerException | IllegalSetupException e){
                super.error();
            }
        }).start();

    }
}
