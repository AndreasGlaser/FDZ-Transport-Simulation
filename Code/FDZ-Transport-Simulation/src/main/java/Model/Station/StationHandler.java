package Model.Station;

/**@author Noah Lehmann*/

import java.util.ArrayList;

/**
 * Singleton Class for handling the Stations
 * handles: insertions, deletions, saving and loading station-data
 */
public class StationHandler {

    private ArrayList<Station> stationList;
    private static StationHandler handler = new StationHandler();

    private StationHandler(){
        stationList = new ArrayList<>(3);
    }

    public static StationHandler getInstance(){
        return handler;
    }

/* GETTER -------------------------------------------------------------------*/

    public ArrayList<Station> getStationList(){
        return stationList;
    }

    public Station getStationByShortCut(String shortCut){
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        int idx = 0;
        while (stationList.get(idx).getShortCut().
                compareToIgnoreCase(shortCut) != 0) {
            //find idx of requested station
            if(++idx == stationList.size()){return null;}
        }
        return stationList.get(idx);
    }
    public Station getStationByName(String name){
        ArrayList<Station> stationList = StationHandler.getInstance().getStationList();
        int idx = 0;
        while (stationList.get(idx).getName().
                compareToIgnoreCase(name) != 0) {
            //find idx of requested station
            if(++idx == stationList.size()){return null;}
        }
        return stationList.get(idx);
    }

/* SAVING/LOADING DATA ------------------------------------------------------*/

    public boolean saveStations(){
        return true;
    }

    public boolean loadStations(){
        return true;
    }

/* MANIPULATE LIST-----------------------------------------------------------*/

    /**
     * adds a Station to the stationList, station will not have any
     * connections until the user configures them
     * @param name
     * @param shortCut
     */
    public void addStation(String name, String shortCut){
        /*TODO if connection is closed*/
        stationList.add(new Station(name, shortCut));
    }

    /**
     * deletes the Station with the specified name or throws a
     * nullpointer Exception if Station does not exist
     * @param name
     * @throws NullPointerException
     */
    public void deleteStation(String name) throws NullPointerException {
        /*TODO if connection is closed*/
        Station station = findNameInStations(name);
        if(station != null) {
            while(stationList.remove(station))
                removeFromPrevList(station);
        }else{
            throw new NullPointerException("Name of Station not in stationList");
        }
    }

/* HELPER FUNCTIONS ---------------------------------------------------------*/

    /**
     * removes specified Station from all Stations prevList in stationList
     * @param station
     */
    private void removeFromPrevList(Station station){
        for (int j=0; j<stationList.size(); j++){
            for(int k=0;k<stationList.get(j).getPrevStations().size();k++){
                if(stationList.get(j).getPrevStations().get(k) == station){
                    System.out.println(
                            "removing station: "+
                                    stationList.get(j).getPrevStations().remove(station));
                    removeFromPrevList(station);
                    /*second last element deleted would increment k, size() decremented,
                    * last element would be skipped if it is also (for which reason ever)
                    * the station to be deleted*/
                }
            }
        }
    }

    /**
     * Finds index of Station when only name is known, returns the index
     * or -1, if Station could not be found
     * @param name
     * @return idx
     */
    private Station findNameInStations(String name){
        for(int i=0; i<stationList.size();++i){
            if(stationList.get(i).getName().compareTo(name) == 0){
                return stationList.get(i);
            }
        }
        return null;
    }

}
