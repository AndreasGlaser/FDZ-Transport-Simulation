package Model.Command;

import Model.Exception.IllegalSetupException;
import Model.Facade;
import Model.Logger.LoggerInstance;
import Model.Network.NetworkController;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.util.LinkedList;

import static java.lang.Thread.sleep;

/**@author Noah Lehmann*/

public class RequestEmptyCarriage extends Command {

    private final String position;
    private String lastUsed;

    /**
     *
     * @param position shortCut of Station which requests an empty carriage
     * @param msgID message ID of the incoming message that initiated the Command
     */
    RequestEmptyCarriage(String position, String msgID){
        LoggerInstance.log.debug("Creating new RequestEmptyCarriage Command to "+position);
        new Facade().setStatus("Moving empty carriage to station: "+position);
        this.position = position;
        super.msgID = msgID;
    }

    /**
     * Special acknowledgment 1 for the requestEmptyCarriage Command
     */
    @Override
    void commandExecuted(){
        NetworkController.getInstance().acknowledge2(msgID, true);
    }

    // TODO: 16.06.18 doc und ack2 
    @Override
    public void execute(){
        Thread execute = new Thread(() ->{
            Station temp;
            try {
                temp = StationHandler.getInstance().getStationByShortCut(position);
                lastUsed = temp.getName();
                LinkedList<Station> path;
                if(!this.getActivated()){
                    path = new PathFinder(temp, temp.getHopsToNewCarriage()).getPath();
                }else{
                    Station last = StationHandler.getInstance().getStationByName(lastUsed);
                    path = new PathFinder(last, last.getHopsToNewCarriage()).getPath();
                }
                this.confirmActivation();
                if(TimeMode.fastModeActivated) {
                    path.stream().filter(station -> station != path.getLast()).forEachOrdered(station -> {
                        station.driveInSled(-1);
                        lastUsed = station.getName();
                        station.driveOutSled();
                    });
                    lastUsed = path.getLast().getName();
                    path.getLast().driveInSled(-1);
                    LoggerInstance.log.info("Done Requesting Carriage in FastMode");
                }else{
                    for (int i=0; i<path.size()-1; i++){
                        path.get(i).driveInSled(-1);
                        lastUsed = path.get(i).getName();
                        path.get(i).driveOutSled();
                        try{
                            sleep(TimeMode.findTimeForPath(path.get(i), path.get(i+1))*1000);
                        }catch(InterruptedException e){
                            // TODO: 16.06.18 debug interruption
                        }
                    }
                    lastUsed = path.getLast().getName();
                    path.getLast().driveInSled(-1);
                    LoggerInstance.log.info("Done Requesting Carriage in SlowMode");
                }
                this.commandExecuted();
            }catch(IllegalSetupException | NullPointerException e){
                e.printStackTrace();
                LoggerInstance.log.error("Illegal Setup Detected in RequestEmptyCarriage", e);
                super.error();
            }
            CommandQueue.getInstance().delete(this);
        });
        execute.start();
    }
}
