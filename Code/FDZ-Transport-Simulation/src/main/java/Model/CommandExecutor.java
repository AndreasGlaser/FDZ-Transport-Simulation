package Model;

/*--NOAH LEHMANN-------------------------------------------------------------*/

import java.util.ArrayList;

public class CommandExecutor {
    /*this class only provides function for valid input, which was
     * validated in the CommandInterpreter class*/

    private ArrayList<Station> stationList;

    private CommandExecutor(ArrayList<Station> stationList){
        this.stationList = stationList;
    }

    public CommandExecutor(String position, ArrayList<Station> stationList) {
        /*command requestEmptyCarriage*/
        this(stationList);
        this.requestEmptyCarriage(position);
    }

    public CommandExecutor(int id, ArrayList<Station> stationList) {
        /*command releaseCarriage*/
        this(stationList);
        this.releaseCarriage(id);
    }

    public CommandExecutor(String position, int id, ArrayList<Station> stationList) {
        /*command repositionCarriage*/
        this(stationList);
        this.repositionCarriage(position, id);
    }

    public CommandExecutor() {
        /*command ShutdownTransport*/
        this.shutdownTransport();
    }

    /*--COMMAND CONTROL----------------------------------------------------------*/
    /*--REQUEST EMPTY CARRIAGE---------------------------------------------------*/
    /*  STStK001<Message-ID>0002xx  ---------------------------------------------*/

    private void requestEmptyCarriage(String position) {
        System.out.println("\t log: requesting emtpy carriage to " + position);
        if (this.findPosInList(position) != -1) {
            stationList.get(this.findPosInList(position)).driveInSled(Main.getID());
            /*TODO woher bekommt man die neue ID?*/
        }
    }

    /*--RELEASE CARRIAGE--------------------------------------------------------*/
    /*  STStK002<Message-ID>0002xx  --------------------------------------------*/

    private void releaseCarriage(int id) {
        System.out.println("\t log: releasing carriage with id " + id);
        if(this.findIDinPos(id) != -1){
            stationList.get(findIDinPos(id)).driveOutSled(id);
        } else{
            /*TODO ID ist in keiner Station oder im Stau einer Station*/
        }
    }

    /*--REPOSITION CARRIAGE-----------------------------------------------------*/
    /*  STStK003<Message-ID>0002xxyy  ------------------------------------------*/

    private void repositionCarriage(String position, int id) {
        System.out.println("\t log: reposition carriage " + id + " to " + position);
        if(isWayClear(/*from*/stationList.get(findIDinPos(id)),
                /*to*/stationList.get(findPosInList(position)))) {
            stationList.get(findIDinPos(id)).driveOutSled(id);
            stationList.get(findPosInList(position)).driveInSled(id);
        }else{
            /*TODO Stau auf weg zu neuer station*/
            System.out.println( "\t log: CONGESTION DETECTED\n" +
                                "\t      could not reposition\n"+
                                "\t      " + id + " to " + position);
        }
    }

    /*--SHUTDOWN TRANSPORT SYSTEM-----------------------------------------------*/
    /*  STStK004<Message-ID>0002  ----------------------------------------------*/

    private void shutdownTransport() {
        System.out.println("\t log: shutting down");
        /*TODO*/
    }

    /*--HELPING FUNCTIONS--------------------------------------------------------*/

    private int findPosInList(String position) {
        /*returns -1 if POS not found*/
        int idx = 0;
        while (stationList.get(idx).getShortCut().
                compareToIgnoreCase(position) != 0) {
            //find idx of requested station
            if(++idx == stationList.size()){return -1;}
        }
        return idx;
    }

    private int findIDinPos(int id){
        int idx=0;
        while(stationList.get(idx).getSledInside() != id){
            //in which station is ID
            if(++idx == stationList.size()){return -1;}
        }
        return idx;
    }

    private boolean isWayClear(Station from, Station to){
        /*--SPECIAL CASES--*/
        if(to.getPrevStations().get(0) == from) return true;
        if(to == from) return true;
        if(to.getPrevStations().size() == 0){
            /*TODO throw EmptyException*/
        }
        int i = 0;
        while(to.getPrevStations().get(i) != from){
            if(i == stationList.size()) break; //endless loop detection
            if(to.getPrevStations().get(i++).isOccupied()){
                return false;
            }
        }
        return true;
    }
}