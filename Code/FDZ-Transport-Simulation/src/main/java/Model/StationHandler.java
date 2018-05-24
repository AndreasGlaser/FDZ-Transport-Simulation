package Model;

/*@author Noah Lehmann ------------------------------------------------------*/

import java.util.ArrayList;

/**
 * Singleton Class for handling the Stations
 * handles: insertions, deletions, saving and loading station-data
 */
public class StationHandler {

    private ArrayList<Station> stationList;
    private static StationHandler handler;

    private StationHandler(){
        stationList = new ArrayList<>();
    }

    public static StationHandler getStationHandler(){
        if(handler != null){
            return handler;
        }
        else{
            handler = new StationHandler();
            return handler;
        }
    }

/* GETTER -------------------------------------------------------------------*/

    public ArrayList<Station> getCurrentStationList(){
        return stationList;
    }

/* SAVING/LOADING DATA ------------------------------------------------------*/

    public boolean saveStations(){
        return true;
    }

    public boolean loadStations(){
        return true;
    }

/* MANIPULATE LIST-----------------------------------------------------------*/

    public void addStation(String name, String shortCut){
        stationList.add(new Station(name, shortCut));
    }

    public void deleteStation(String name){
        int i = 0;
        while(stationList.get(i++).getName().compareTo(name) == 0);
        if(i != stationList.size()){
            stationList.remove(i);
            for (int j=0; j<stationList.size(); j++){
                for(int k=0;
                    k<stationList.get(j).getPrevStations().size();
                    k++){
                    if(stationList.get(j).getPrevStations().get(k) ==
                            stationList.get(i)){
                        stationList.get(j).getPrevStations()
                                .remove(stationList.get(i));
                    }
                }
            }
        }
        //TODO Hilfsfunktionen
    }

}
