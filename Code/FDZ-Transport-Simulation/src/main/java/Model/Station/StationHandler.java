package Model.Station;

import Model.Exception.IllegalSetupException;

import java.util.ArrayList;

/**@author nlehmann*/

/**
 * Singleton Class initialized at system start, because of threadsafe implementation
 */
public class StationHandler{

    private ArrayList<Station> stations;
    private final int EMPTY_CARRIAGE = -1;
    private static StationHandler stationHandler = new StationHandler();

    /**
     * private Constructor
     */
    private StationHandler(){
        stations = new ArrayList<>(3);
    }

    /*Getter*/

    /**
     * Getter for the Singleton StationHandler
     * @return Only Instance of StationHandler
     */
    public static StationHandler getInstance(){
        return stationHandler;
    }

    public int getAmountOfStations(){
        return stations.size();
    }

    /**
     * Finds and returns a Station by its name
     * @param name String which is not null and one of the existing stations name
     * @return Station with specified name
     * @throws NullPointerException thrown if the name is null or no Station has specified name
     */
    public Station getStationByName(String name) throws NullPointerException{
        if(name == null){
            throw new NullPointerException("specified name is null");
        }
        for (int i = 0; i < stations.size(); i++) {
            Station station =  stations.get(i);
            if(station.getName().compareTo(name) == 0){
                return station;
            }
        }
        throw new NullPointerException("Name not in List of Stations");
    }

    /**
     * Finds and returns a Station by its shortCut
     * @param shortCut String which is not null and one of the existing stations shortCut
     * @return Station with specified name
     * @throws NullPointerException thrown if the shortCut is null or no Station has specified shortCut
     */
    public Station getStationByShortCut(String shortCut) throws NullPointerException{
        if(shortCut == null){
            throw new NullPointerException("specified shortCut is null");
        }
        for (int i = 0; i < stations.size(); i++) {
            Station station =  stations.get(i);
            if(station.getShortCut().compareTo(shortCut) == 0){
                return station;
            }
        }
        throw new NullPointerException("ShortCut not in List of Stations");
    }

    /**
     * Finds and returns a Station by its shortCut
     * @param id int which is not null and one of the existing stations sledInside
     * @return Station with specified sledInside
     * @throws NullPointerException thrown if the sledInside is null or no Station has specified sledInside
     */
    public Station getStationBySledID(int id) throws NullPointerException{
        if (id<EMPTY_CARRIAGE){
            throw new NullPointerException("Invalid SledID queried");
        }
        for (int i = 0; i < stations.size(); i++) {
            Integer sledInside =  stations.get(i).getSledsInStation().get(0);
            if(sledInside != null && sledInside == id){
                return stations.get(i);
            }
        }
        throw new NullPointerException("Id in none of the Stations");
    }

    /*Manipulate List*/

    /**
     * Adds a station to the list of Stations in the setup
     * @param station instance of station which is not null and not already in stationList
     * @throws IllegalSetupException thrown if station is already in stationList
     */
    public void addStation(Station station) throws IllegalSetupException {
        if(station == null){
            System.err.println("Null will not be added to stationList");
        }else if(listContains(station)){
            /*TODO debug*/
            throw new IllegalSetupException("Station already in stationList");
        }else{
            stations.add(station);
        }
    }

    /**
     * Removes a Station from the list of Stations in the setup
     * @param station instance that is not null and contained in the stationList
     * @throws NullPointerException thrown if station is not in the List
     */
    public void deleteStation(Station station) throws NullPointerException{
        if(station == null){
            System.err.println("Null will not be removed from stationList");
        }else if(listContains(station)){
            stations.remove(station);
        }else{
            throw new NullPointerException("Station not in stationList");
        }
    }


    /*Helpers*/
    private boolean listContains(Station station){
        for (int i=0; i<stations.size(); ++i){
            if(stations.get(i).equals(station)) {
                return true;
            }
        }
        return false;
    }

    // TODO: 07.06.18 entfernen!!
    public ArrayList<Station> getStationList(){
        return stations;
    }
}