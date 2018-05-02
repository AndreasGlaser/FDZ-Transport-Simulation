package Model;

/*--NOAH LEHMANN-------------------------------------------------------------*/

import java.util.ArrayList;

class CommandExecutor {
    /*this class only provides function for valid input, which was
     * validated in the CommandInterpreter class*/

    private ArrayList<Station> stationList;

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

    private void requestEmptyCarriage(String position) {
        Station temp = stationList.get(this.findPosInList(position));
        Station blocking = firstStationInWay(temp.getHopsToNewCarriage(), temp);
        if(blocking == null) {
            if (this.findPosInList(position) != -1) {
                stationList.get(this.findPosInList(position)).
                        driveInSled(-1); //@Andreas leerer Schlitten
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

    private void releaseCarriage(int id) {
        System.out.println("\t log: releasing carriage with id " + id);
        if(this.findIDinPos(id) != -2){
            stationList.get(findIDinPos(id)).driveOutSled(id);
        } else{
            /*TODO ID ist in keiner Station oder im Stau einer Station*/
        }
    }

/*--REPOSITION CARRIAGE-----------------------------------------------------*/
/*  STStK003<Message-ID>0002xxyy  ------------------------------------------*/

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
        // returns -2 if id is not found in pos
        int idx=0;
        while(stationList.get(idx).getSledInside() != id){
            //in which station is ID
            if(++idx == stationList.size()){
                /*id unknown*/
                idx = findIDinPos(-1);
                /*finds first empty carriage, expects only one empty carriage*/
                if(idx != -2){ //find empty sled
                    stationList.get(idx).setSledInside(id);
                    /*empty carriage gets unknown id*/
                    return idx;
                    /*empty carriage found, give unknown id to carriage*/
                }else{
                    return -2;
                    /*no empty sled found, return -2*/
                }
            }
            /*TODO merke nicht erkannte id == leerer schlitten*/
        }
        return idx;
    }

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

    private Station firstStationInWay(int hopsBack, Station to){
        /*returns null if no congestion on way or
         *first station, which was congested in way */
        /*TODO rekursiver aufruf, prüfung erst im Rücklauf wie oben*/
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