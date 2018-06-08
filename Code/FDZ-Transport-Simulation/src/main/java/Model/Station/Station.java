package Model.Station;

import Model.Exception.IllegalSetupException;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**@author nlehmann*/

/**
 * A Station as specified in the Documentation of the FDZ
 */
public class Station{

    private String name, shortCut;
    private Semaphore semaphore;
    private ArrayList<Integer> idsInCongestion;
    private ArrayList<Station> prevStations;
    private Integer sledInside;
    private int hopsBackToNewCarriage;

    /**
     * Basic Constructor for an instance of Station.
     * <p>
     *     Other parameters are filled with default settings and
     *     need to be set separately
     * </p>
     * @param aName String that is not null
     * @param aShortCut String that has the length of 2
     */
    public Station(String aName, String aShortCut) throws IllegalSetupException {
        this.setName(aName);
        this.setShortCut(aShortCut);
        this.hopsBackToNewCarriage = 1;
        this.sledInside = null;
        this.idsInCongestion = new ArrayList<>(3); /*TODO add ids, remove if sledInside is empty*/
        this.prevStations = new ArrayList<>(1);
        this.semaphore = new Semaphore(1,true);
    }

    /*Logic*/

    /**
     * Provides an interface to set the sled, which is in the Station.
     * <p>
     *     Works like a binary Semaphore, can only be finished, if the Semaphore is clear,
     *     in this case, the previous sled has been driven out
     * </p>
     * @param id id of the sled
     */
    public void driveInSled(int id){
        boolean flag = true;
        while(flag) {
            try {
                semaphore.acquire();
                flag = false;
            } catch (InterruptedException e) {
                System.err.println("Station "+getName()+"s semaphore has been interrupted");
                flag = true;
            }
        }/*acquired successfully*/
        this.setSledInside(id);
    }

    /**
     * Provides an interface to empty the Station
     * <p>
     *     Works like a binary Semaphore, releases the Semaphore so that waiting sleds can drive in
     * </p>
     */
    public void driveOutSled(){
        this.setSledInside(null);
        semaphore.release();
    }

    /*Setter*/

    /**
     * Sets the Stations state to changed, so that potential observers notice
     */
    private void setStationChanged(){}

    /**
     * Sets the stations name with the specified parameter
     * @param aName a String with length greater than 0
     * @throws IllegalSetupException throws Exception if parameter does not meet requirements
     */
    public void setName(String aName) throws IllegalSetupException {
        if(aName == null || aName.length() == 0){
            throw new IllegalSetupException("New StationName is invalid");
        }else{
            this.name = aName;
            this.setStationChanged();
        }
    }

    /**
     * Sets the stations shortCut with the specified parameter
     * @param aShortCut a String with length equals 2
     * @throws IllegalSetupException throws Exception if parameter does not meet requirements
     */
    public void setShortCut(String aShortCut) throws IllegalSetupException{
        if(aShortCut.length() == 2){
            this.shortCut = aShortCut;
            this.setStationChanged();
        }else{
            throw new IllegalSetupException("New StationShortCut is invalid");
        }
    }

    /**
     * Setter for the HopsBackToNewCarriage value
     * @param hopsBack an integer greater than 0 but smaller than the amount of Stations in setup
     * @throws IllegalSetupException thrown if the parameter does not meet the requirements
     */
    public void setHopsToNewCarriage(int hopsBack) throws IllegalSetupException{
        if(hopsBack < 1){
            throw new IllegalSetupException("Given HopsBack is too small");
        }
        if(hopsBack >= StationHandler.getInstance().getAmountOfStations()){
            throw new IllegalSetupException("Given HopsBack is too big");
        }
        this.hopsBackToNewCarriage = hopsBack;
        this.setStationChanged();
    }

    /**
     * sets the sled which is inside the station at the moment
     * @param id the id of the sled, which is based in the station at the moment
     */
    private void setSledInside(Integer id){
        this.sledInside = id;
        this.setStationChanged();
    }

    public void addObserver(){
        // TODO: 07.06.18 implement Observer
    }

    public void idFound(int id){
        if(sledInside != null && sledInside == -1){
            setSledInside(id);
        }
    }

    /*Setter PrevStations*/

    /**
     * Adds a station to the Objects prevList
     * @param station a Station which is not null and not equal to one, that is already in prevList
     */
    public void addPrevStation(Station station){
        if(!this.prevStations.contains(station)) {
            prevStations.add(station);
            this.setStationChanged();
        }else if(station == null){
            /* TODO DEBUG not changed */
            System.err.println("NullPointer will not be added to PrevList");
        }else{
            /* TODO DEBUG not changed */
            System.err.println("Station already in PrevList");
        }
    }

    /**
     * Removes a station from the Objects prevList
     * @param station a station which is not null and in the specified prevList
     * @throws NullPointerException thrown if the Station is null or not in the prevList
     */
    public void deletePrevStation(Station station) throws NullPointerException{
        if(this.prevStations.contains(station)){
            prevStations.remove(station);
            this.setStationChanged();
        }else{
            throw new NullPointerException("Station not in used PrevList");
        }
    }

    /*Getter*/

    /**
     * Getter Method for Stations full name
     * @return the Stations full name
     */
    public String getName(){return this.name;}

    /**
     * Getter Method for Stations shortCut
     * @return the Stations shortCut
     */
    public String getShortCut(){return this.shortCut;}

    /**
     * Returns the HopsBackToNewCarriage Value
     * @return hops back in the setup, which need to be taken, if an empty Carriage is requested
     */
    public int getHopsToNewCarriage(){return this.hopsBackToNewCarriage;}

    /**
     * Returns a List of Ids of sleds in the Station
     * @return List of Sleds in Station, where first is inside station and rest is in congestion
     */
    public ArrayList<Integer> getSledsInStation(){
        ArrayList<Integer> list = new ArrayList<>(4);
        list.add(this.sledInside);
        list.addAll(idsInCongestion);
        return list;
    }

    /**
     * Getter for the PrevStations to determine a path
     * @return list of stations directly previous to this station
     */
    public ArrayList<Station> getPrevStations(){
        return prevStations;
    }

    /**
     * Checks whether the station is Congested or not
     * @return true if congested, false if not
     */
    public boolean isCongested(){
        if(sledInside == null && idsInCongestion.size() == 0){
            return false;
        }
        return true;
    }
}