package Model.Command;

import Model.Exception.IllegalSetupException;
import Model.Logger.LoggerInstance;
import Model.Station.Station;
import sun.rmi.runtime.Log;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Noah Lehmann
 *
 * Class to find Paths in Setup for different occasions
 */
public class PathFinder {

    private final LinkedList<Station> path = new LinkedList<>();

    /**
     * The Constructor to find a Path between two Stations
     * @param from Beginning of Path
     * @param to End of Path
     * @throws IllegalSetupException thrown if an Illegal Setup is detected
     * @throws NullPointerException thrown if the stations are not known or null
     */
    public PathFinder(Station from, Station to) throws  IllegalSetupException, NullPointerException {
        LoggerInstance.log.debug("Trying to find Path for Reposition Command");
        checkPath(from, to);
    }

    /**
     * The Constructor to find a Path if only the destination and the distance is known
     * @param station End of Path
     * @param hops distance to destination
     * @throws IllegalSetupException thrown if an Illegal Setup is detected
     * @throws NullPointerException thrown if the stations are not known or null
     */
    public PathFinder(Station station, int hops) throws  IllegalSetupException, NullPointerException{
        LoggerInstance.log.debug("Trying to find Path for RequestEmptyCarriage Command");
        checkPath(hops, station);
    }

    /**
     * Getter for final Path that has been found
     * @return Path requested in one of the Constructors
     */
    public LinkedList<Station> getPath(){
        final StringBuilder builder = new StringBuilder();
        path.stream().forEachOrdered(station ->  builder.append("|"+station.getName()+"|"));
        LoggerInstance.log.debug("Returning Path: " + builder.toString());
        return path;
    }

    /**
     * checks the Stations and calls findRightPathFor(...)
     * @param from Beginning of Path
     * @param to End of Path
     * @throws IllegalSetupException thrown if Illegal Setup has been detected
     * @throws NullPointerException thrown if Inout Stations are null
     */
    private void checkPath(Station from, Station to) throws IllegalSetupException, NullPointerException {
        if(from == null || to == null){
            LoggerInstance.log.warn("Given Station is null (PathFinder)");
            throw new NullPointerException("Station is null");
        }
        findRightPathFor(to, from, to);
        if(path.isEmpty()){
            LoggerInstance.log.warn("No possible Path between "+from.getName()+ " and "+to.getName());
            throw new IllegalSetupException("No possible path");
        }
    }

    /**
     * Checks the given Parameters and finds Path
     * @param hops distance of Path
     * @param to End of Path
     * @throws IllegalSetupException thrown if Illegal Setup has been detected
     * @throws NullPointerException thrown if Station is null
     */
    private void checkPath(int hops, Station to) throws IllegalSetupException, NullPointerException {
        /*returns null if no congestion on way or
         *first station, which was congested in way */
        if(to == null){
            LoggerInstance.log.warn("Given Station is null (PathFinder)");
            throw new NullPointerException("Station is null");
        }
        if(hops == 1 || to.getPrevStations().size() == 0){
            /*END RECURSIVE FUNCTION*/
            path.addLast(to);
            return;
        }
        /*RECURSIVE CALL*/
        checkPath(hops-1, findRightNextHopFor(to));
        path.addLast(to);
    }

    /* HELPING FUNCTIONS --------------------------------------------------------*/

    /**
     * Finds right next hop station back for recursive call of checkPath(int hops, Station to)
     * @param aStation Station fpr which next hop is needed
     * @return next hopBack station
     * @throws IllegalSetupException thrown if Illegal setup has been detected
     */
    private Station findRightNextHopFor(Station aStation)throws IllegalSetupException {
        ArrayList<Station> list = new ArrayList<>(aStation.getPrevStations().size());
        aStation.getPrevStations().forEach(prevPair -> list.add(prevPair.getPrevStation()));
        for (Station station : list) {
            if(station.getHopsToNewCarriage() == aStation.getHopsToNewCarriage()-1) return station;
        }
        IllegalSetupException e = new IllegalSetupException("Station "+aStation.getName()+"has no Prev Station with"+
                "Hops-1 == "+(aStation.getHopsToNewCarriage()-1));
        LoggerInstance.log.error("Illegal Setup found by PathFinder", e);
        throw e;
    }

    /**
     * Finds Right Path for 2 given Stations
     * @param init Destination Station, never changed in recursive call
     * @param from Beginning of Path
     * @param to End of Path, changed in recursive Call
     */
    private void findRightPathFor(Station init, Station from, Station to) {
        if(from == to && to == init && to.getPrevStations().isEmpty()){
            return; /*Loop detected*/
        }
        if(to == from) {
            path.addFirst(to);
            return;
        }
        for(int i=0; i<to.getPrevStations().size();++i){
            if(path.isEmpty() && to.getPrevStations().get(i).getPrevStation() != init){
                findRightPathFor(init, from, to.getPrevStations().get(i).getPrevStation());
            }
        }

        if(!path.isEmpty()){
            path.addLast(to);
        }
    }
}
