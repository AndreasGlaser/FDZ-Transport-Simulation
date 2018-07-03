package Model.Command;

import Model.Exception.IllegalSetupException;
import Model.Facade;
import Model.Logger.LoggerInstance;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.util.LinkedList;

import static java.lang.Thread.sleep;

/**@author nlehmann*/

public class RepositionCarriage extends Command {

    private final int id;
    private final String position;
    private Station lastUsed = null;

    /**
     * @param id id of carriage to reposition
     * @param position shortCut of Station to move id to
     * @param msgID message ID of the incoming message initiating the Command
     */
    RepositionCarriage(int id, String position, String msgID){
        LoggerInstance.log.debug("Creating new Reposition Carriage Command of ID "+id +" to "+position);
        new Facade().setStatus("Repositioning carriage with ID: "+id+" to station: "+position);
        this.id = id;
        this.position = position;
        super.msgID = msgID;
    }

    // TODO: 16.06.18 javadoc und ack2
    @Override
    public void execute(){

        Thread execute = new Thread(() ->{
            Station from = null, to = null;
            try {
                if (this.getAck1Success()){
                    from = StationHandler.getInstance().getStationBySledID(id);
                    lastUsed = from;
                }else{
                    try{
                        from = lastUsed;
                    }catch(NullPointerException e){

                    }
                }
                to = StationHandler.getInstance().getStationByShortCut(position);
            }catch(NullPointerException e){
                LoggerInstance.log.warn("Station or ID does not exist (RepositionCarriage)");
                super.error();
            }catch(IndexOutOfBoundsException e){
                LoggerInstance.log.error("IndexOutOfBounds, Illegal Setup found", e);
                super.error();
            }
            try {
                LinkedList<Station> path = new PathFinder(from, to).getPath();
                if(TimeMode.fastModeActivated) {
                    path.getFirst().driveOutSled();
                    path.stream().filter(s -> (s != path.getFirst() && s != path.getLast())).forEachOrdered(station -> {
                        station.driveInSled(id);
                        lastUsed = station;
                        station.driveOutSled();
                    });
                    path.getLast().driveInSled(id);
                    lastUsed = path.getLast();
                    LoggerInstance.log.info("Done Repositioning in FastMode");
                }else{
                    path.getFirst().driveOutSled();
                    try {
                        sleep(TimeMode.findTimeForPath(path.getFirst(), path.get(1))*1000);
                    }catch (InterruptedException | IndexOutOfBoundsException e){
                        LoggerInstance.log.warn("Interruption in Repositioning in SlowMode");
                    }
                    for (int i=1; i<path.size()-1; i++){
                        try{
                            path.get(i).driveInSled(id);
                            path.get(i).driveOutSled();
                            sleep(TimeMode.findTimeForPath(path.get(i), path.get(i+1))*1000);
                        }catch(InterruptedException e){
                            LoggerInstance.log.warn("Interruption in Repositioning in SlowMode");
                        }
                    }
                    path.getLast().driveInSled(id);
                    LoggerInstance.log.info("Done Repositioning in SlowMode");
                }
                super.commandExecuted();
            }catch(NullPointerException | IllegalSetupException e){
                super.error();
                LoggerInstance.log.error("Illegal Setup detected in Repositioning Carriage");
            }
        });
        execute.start();
        try {
            execute.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
