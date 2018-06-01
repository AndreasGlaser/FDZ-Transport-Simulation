package Model;

/**@author Noah Lehmann*/

import java.util.ArrayList;

class CommandExecutor {
    /*this class only provides function for valid input, which was
     * validated in the CommandInterpreter class*/

    private ArrayList<Station> stationList;
    private final int NOT_FOUND = -2, EMPTY_CARRIAGE = -1;


    private CommandExecutor(ArrayList<Station> stationList){
        this.stationList = stationList;
    }

    CommandExecutor(String position, ArrayList<Station> stationList) {
        /*command requestEmptyCarriage*/
        this(stationList);
        this.requestEmptyCarriage(position);
    }

    CommandExecutor(int id, ArrayList<Station> stationList) {
        /*command releaseCarriage*/
        this(stationList);
        this.releaseCarriage(id);
    }

    CommandExecutor(String position, int id, ArrayList<Station> stationList) {
        /*command repositionCarriage*/
        this(stationList);
        this.repositionCarriage(position, id);
    }

    CommandExecutor() {
        /*command ShutdownTransport*/
        this.shutdownTransport();
    }

/*--COMMAND CONTROL----------------------------------------------------------*/
/*--REQUEST EMPTY CARRIAGE---------------------------------------------------*/
/*  STStK001<Message-ID>0002xx  ---------------------------------------------*/

    /**
     * The Method, which executes the Command REQUEST_EMPTY_CARRIAGE
     * @param position
     */
    private void requestEmptyCarriage(String position) {
        Station temp = stationList.get(this.findPosInList(position));
        Station blocking = firstStationInWay(temp.getHopsToNewCarriage(), temp);
        if(blocking == null) {
            if (this.findPosInList(position) != NOT_FOUND) {
                stationList.get(this.findPosInList(position)).
                        driveInSled(EMPTY_CARRIAGE); //@Andreas leerer Schlitten
                    /*empty sled gets unknown id when received*/
                System.out.println("\t log: requesting empty carriage to "
                                    + position);
            }
        }else{
            /*TODO Congestion detected*/
            /*first station which is congested -> blocking*/
            System.out.println( "\t log: CONGESTION DETECTED\n" +
                    "\t      COULD NOT REQUEST\n"+
                    "\t      CARRIAGE TO [" + position + "]");
        }
    }

/*--RELEASE CARRIAGE--------------------------------------------------------*/
/*  STStK002<Message-ID>0002xx  --------------------------------------------*/

    /**
     * The Method, which executes the Command RELEASE_CARRIAGE
     * @param id
     */
    private void releaseCarriage(int id) {
        System.out.println("\t log: releasing carriage with id " + id);
        if(this.findIDinPos(id) != NOT_FOUND){
            stationList.get(findIDinPos(id)).driveOutSled(id);
        } else{
            /*TODO ID ist in keiner Station oder im Stau einer Station*/
        }
    }

/*--REPOSITION CARRIAGE-----------------------------------------------------*/
/*  STStK003<Message-ID>0002xxyy  ------------------------------------------*/

    /**
     * The Method, which executes the Command REPOSITION_CARRIAGE
     * @param position
     * @param id
     */
    private void repositionCarriage(String position, int id) {
        Station blocking = firstStationInWay(
                /*from*/stationList.get(findIDinPos(id)),
                /*to*/stationList.get(findPosInList(position)));
        if(blocking == null) {
            /*No Congestion from source to destination*/
            stationList.get(findIDinPos(id)).driveOutSled(id);
            stationList.get(findPosInList(position)).driveInSled(id);
            System.out.println("\t log: reposition carriage " + id + " to "
                    + position);
        }else{
            /*Congestion from source to destination, carriage must wait*/
            /*first blocked station -> blocking*/
            /*TODO Stau auf weg zu neuer station*/
            System.out.println( "\t log: CONGESTION DETECTED\n" +
                                "\t      COULD NOT REPOSITION\n["+
                                "\t      " + id + "] TO [" + position +"]");
        }
    }

/*--SHUTDOWN TRANSPORT SYSTEM-----------------------------------------------*/
/*  STStK004<Message-ID>0002  ----------------------------------------------*/

    /**
     * The Method, which executes the Command SHUTDOWN_TRANSPORT
     */
    private void shutdownTransport() {
        System.out.println("\t log: shutting down");
        /*TODO*/
    }

/*--HELPING FUNCTIONS--------------------------------------------------------*/

    /**
     * Finds a Positions shortcut in the list of known Stations and
     * returns the Index, if not found -2
     * @param position
     * @return positionIndex
     */
    private int findPosInList(String position) {
        /*returns -2 if POS not found*/
        int idx = 0;
        while (stationList.get(idx).getShortCut().
                compareToIgnoreCase(position) != 0) {
            //find idx of requested station
            if(++idx == stationList.size()){return NOT_FOUND;}
        }
        return idx;
    }

    /**
     * Finds an Sled-ID in the Stations known to the System.
     * returns te index if found, else it searches for an empty sled.
     * sets the sleds id to the unknown id or only returns -2 if no empty
     * sled is found.
     * @param id
     * @return positionIndex
     */
    private int findIDinPos(int id){
        // returns -2 if id is not found in pos
        int idx=0;
        while(stationList.get(idx).getSledInside() != id){
            //in which station is ID
            if(++idx == stationList.size()){
                /*id unknown*/
                idx = findIDinPos(EMPTY_CARRIAGE);//find empty sled
                /*finds first empty carriage, expects only one empty carriage*/
                if(idx != NOT_FOUND){
                    stationList.get(idx).setSledInside(id);
                    /*empty carriage gets unknown id*/
                    return idx;
                    /*empty carriage found, give unknown id to carriage*/
                }else{
                    return NOT_FOUND;
                    /*no empty sled found, return -2*/
                }
            }
        }
        return idx;
    }

    /**
     * searches the path between two stations. Returns the first station
     * closest to "from", where a congestion is detected. If no Station
     * blocks, it returns null
     * @param from
     * @param to
     * @return blockingStation
     */
    private Station firstStationInWay(Station from, Station to){
        /*returns null if no Stations on Way are congested
         *or first station, which is congested on way */
        /*--END RECURSIVE FUNCTION--*/
        if(to == from || to.getPrevStations().size() == 0) {
            return null;
        }
        /*RECURSIVE CALL*/
        Station prev = firstStationInWay(from, to.getPrevStations().get(0));
        /*TODO BACKTRACKING THE RIGHT way from-to if multiple prev-stations*/
        /*WAS PREV-STATION OCCUPIED?*/
        if(prev == null){ /*NO*/
            if(to.isOccupied()) {
                /*AM I OCCUPIED?*/
                return to; //yes return me
            }else{
                return null;//no, no prev Station or me is occupied
            }
        }else{           /*YES, PREV-STATION isOccupied*/
            return prev;
            /*
             *i don't care if i am occupied, prev station is
             *causing trouble already, return prev-station
             */
        }
    }

    /**
     * Searches for a blocking station on path from hops back=0
     * to Station. Recursive lookback until hopsBack is 0. Returns
     * first blocking station with the smallest hopsBack, or returns
     * null if no station is blocking
     * @param hopsBack
     * @param to
     * @return blockingStation
     */
    private Station firstStationInWay(int hopsBack, Station to){
        /*returns null if no congestion on way or
         *first station, which was congested in way */
        if(hopsBack == 1 || to.getPrevStations().size() == 0){
            /*END RECURSIVE FUNCTION*/
            if(to.isOccupied()){
                /*am i congested? 1 more hop to go*/
                return to;
            }else{
                /*1 hop back is ok, if i'm not congested*/
                return null;
            }
        }
        /*RECURSIVE CALL*/
        Station prev = firstStationInWay(hopsBack-1,
                                        to.getPrevStations().get(0));
        /*TODO BACKTRACKING THE RIGHT way from-to if multiple prev-stations*/
        /*WAS PREV-STATION OCCUPIED?*/
        if(prev == null){ /*NO*/
            if(to.isOccupied()) {
                /*AM I OCCUPIED?*/
                return to; //yes return me
            }else{
                return null;//no, no prev Station or me is occupied
            }
        }else{           /*YES, PREV-STATION isOccupied*/
            return prev;
            /*
             *i don't care if i am occupied, prev station is
             *causing trouble already, return prev-station
             */
        }
    }
}