package Model.Station;

import Model.Exception.IllegalSetupException;
import Model.Logger.LoggerInstance;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * A Station as specified in the Documentation of the FDZ
 *
 * @author nlehmann
 */
public class Station{

    private String name, shortCut;
    private Semaphore semaphore;
    private ArrayList<Integer> idsInCongestion;
    private ArrayList<PrevPair> prevStations;
    private Integer sledInside;
    private int hopsBackToNewCarriage;
    private final HashSet<StationObserver> observers;

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
        this.observers = new HashSet<>(2);
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
    public void driveInSled(Integer id){
        if(id == null)return;
        while(true) {
            try {
                if(!idsInCongestion.contains(id)){
                    idsInCongestion.add(id);
                    setChanged();
                }// id in congestion until it acquires semaphore
                semaphore.acquire();
                idsInCongestion.remove(id);
                setChanged();
                break;
            } catch (InterruptedException e) {
                LoggerInstance.log.warn("Semaphore in Station {} interrupted", name);
            }
        }/*acquired successfully*/
        this.setSledInside(id);
        LoggerInstance.log.debug("New Sled in Station {} is {}", name, id);
    }

    /**
     * Provides an interface to empty the Station
     * <p>
     *     Works like a binary Semaphore, releases the Semaphore so that waiting sleds can drive in
     * </p>
     */
    public void driveOutSled(){
        if(semaphore.availablePermits()==0 && sledInside != null) {
            System.err.println("driving out");
            this.setSledInside(null);
            semaphore.release();
        }
        LoggerInstance.log.debug("No more Sled in Station {}", name);
    }

    /*Setter*/

    /**
     * Sets the stations name with the specified parameter
     * @param aName a String with length greater than 0
     * @throws IllegalSetupException throws Exception if parameter does not meet requirements
     */
    public void setName(String aName) throws IllegalSetupException {
        if(aName == null || aName.length() == 0){
            LoggerInstance.log.warn("Illegal Input in setStationName()");
            throw new IllegalSetupException("New StationName is invalid");
        }else{
            LoggerInstance.log.debug("Station {}s name has been changed to {}", name, aName);
            this.name = aName;
            this.setChanged();
        }
    }

    /**
     * Sets the stations shortCut with the specified parameter
     * @param aShortCut a String with length equals 2
     * @throws IllegalSetupException throws Exception if parameter does not meet requirements
     */
    public void setShortCut(String aShortCut) throws IllegalSetupException{
        if(aShortCut != null && aShortCut.length() == 2){
            LoggerInstance.log.debug("Station {}s shortCut has been changed from {} to {}",name, shortCut, aShortCut);
            this.shortCut = aShortCut;
            this.setChanged();
        }else{
            LoggerInstance.log.warn("Illegal Input in setStationShortCut()");
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
            LoggerInstance.log.warn("Illegal Input in setStationsHopsToNewCarriage(), too small");
            throw new IllegalSetupException("Given HopsBack is too small");
        }
        if(hopsBack >= StationHandler.getInstance().getAmountOfStations()){
            LoggerInstance.log.warn("Illegal Input in setStationsHopsToNewCarriage(), too big");
            throw new IllegalSetupException("Given HopsBack is too big");
        }
        LoggerInstance.log.debug("Set HopsBack of Station {} from {} to {}", name, hopsBackToNewCarriage, hopsBack);
        this.hopsBackToNewCarriage = hopsBack;
        this.setChanged();
    }

    /**
     * sets the sled which is inside the station at the moment
     * @param id the id of the sled, which is based in the station at the moment
     */
    private void setSledInside(Integer id){
        this.sledInside = id;
        this.setChanged();
    }

    /**
     * Sets the IDs in the Station, used for loading new configs
     * @param list list of ids in station
     */
    public void setSledsInStation(@NotNull List<Integer> list){
        if(list != null && !list.isEmpty()) {
            this.driveOutSled();
            this.driveInSled(list.remove(0));
            if(list.size() > 1){
                list.forEach(sledID -> driveInSled(sledID));
            }
            LoggerInstance.log.debug("New State of Station set for {}", name);
        }
    }

    /**
     * Setter for the PathTime of a PrevStation to the Station
     * @param prev PrevStation for which new PathTime is to be set
     * @param time PathTime in Seconds
     * @throws NullPointerException thrown if PrevStation is not in List of the Stations PrevList
     */
    public void setPathTime(Station prev, int time) throws NullPointerException{
        for(PrevPair pair : prevStations){
            if(pair.getPrevStation().equals(prev)){
                pair.setPathTime(time);
                return;
            }
        }
        LoggerInstance.log.warn("No Station such Station in setPathTime for Station {}", name);
        throw new NullPointerException("PrevStation not in List of PrevStations");
    }

    /**
     * Method to Call, if an EmptySled has been found and Command with unknown ID has been received
     * @param id Unknown ID, which will replace the EmptySled in Station
     */
    void idFound(int id){
        if( sledInside != null && sledInside == -1){
            LoggerInstance.log.debug("Found Empty Sled with new ID {}", id);
            setSledInside(id);
        }
    }

    /*Setter PrevStations*/

    /**
     * Adds a station to the Objects prevList
     * @param station a Station which is not null and not equal to one, that is already in prevList
     * @param pathTime time in seconds which sled need from prev to station, default value is 1s, if input is <1
     */
    public void addPrevStation(Station station, int pathTime){
        if(station == null){
            LoggerInstance.log.warn("Null will not be added to Station {}s PrevList", name);
        }else if(this.prevStations.stream().noneMatch(prevPair -> prevPair.getPrevStation() == station) && pathTime >= 1) {
            LoggerInstance.log.info("Adding Station {} to {}s PrevList with PathTime {}", station.getName(), name, pathTime);
            prevStations.add(new PrevPair(station,pathTime));
            this.setChanged();
        }else{
            LoggerInstance.log.warn("Station already in PrevList of Station {}", name);
        }
    }

    /**
     * Removes a station from the Objects prevList
     * @param station a station which is not null and in the specified prevList
     * @throws NullPointerException thrown if the Station is null or not in the prevList
     */
    public void deletePrevStation(Station station){
        for(PrevPair prev: this.prevStations){
            if (prev.getPrevStation() == station){
                LoggerInstance.log.info("Removing {} from Station {}s PrevList", station.getName(), name);
                this.prevStations.remove(prev);
                this.setChanged();
                return;
            }
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
     * @return list of stations directly previous to this station paired with time
     */
    public ArrayList<PrevPair> getPrevStations(){
        return prevStations;
    }

    /**
     * Checks whether the station is Congested or not
     * @return true if congested, false if not
     */
    public boolean isCongested(){
        return (idsInCongestion.size() != 0);
    }

    /*Observing*/

    /**
     * Notifies all StationObservers for changes
     */
    private void setChanged(){
        if(!observers.isEmpty()){
            LoggerInstance.log.debug("Notifying all Observers of {}", name);
            observers.forEach(observer -> observer.update(this));
        }
    }

    /**
     * adds a new StationObserver to the list
     * @param o a class implementing StationObserver
     */
    public void addObserver(StationObserver o) {
        observers.add(o);
        LoggerInstance.log.debug("Adding Observer to {}s List", name);
        o.update(this);
    }
}