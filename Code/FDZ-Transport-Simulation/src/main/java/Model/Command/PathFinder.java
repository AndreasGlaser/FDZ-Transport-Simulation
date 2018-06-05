package Model.Command;

import Model.Exception.CongestionException;
import Model.Exception.IllegalSetupException;
import Model.Station.Station;

import java.util.ArrayList;
import java.util.LinkedList;

/**@author Noah Lehmann*/

public class PathFinder {

    private Station blocking;
    private LinkedList<Station> path = new LinkedList<>();


    public PathFinder(Station from, Station to) throws CongestionException {
        blocking = checkPath(from, to);
        if(blocking != null) {
            throw new CongestionException("Found congestion at " + blocking.getName(), blocking);
        }
    }
    public PathFinder(Station station, int hops) throws CongestionException, IllegalSetupException{
        blocking = checkPath(hops, station);
        if(blocking != null) {
            throw new CongestionException("Found congestion at " + blocking.getName(), blocking);
        }
    }

    public LinkedList<Station> getPath(){
        return path;
    }

    private Station checkPath(Station from, Station to) {
        findRightPathFor(to, from, to);
        for (int i = 0; i < path.size(); i++) {
            Station station =  path.get(i);
            if (station.isCongested()){
                return station;
            }
        }
        return null;
    }


    private Station checkPath(int hops, Station to) throws IllegalSetupException {
        /*returns null if no congestion on way or
         *first station, which was congested in way */
        if(hops == 1 || to.getPrevStations().size() == 0){
            /*END RECURSIVE FUNCTION*/
            if(to.isOccupied()){
                /*am i congested? 1 more hop to go*/
                path.addLast(to);
                return to;
            }else{
                /*1 hop back is ok, if i'm not congested*/
                path.addLast(to);
                return null;
            }
        }
        /*RECURSIVE CALL*/
        Station prev = checkPath(hops-1, findRightNextHopFor(to));
        /*WAS PREV-STATION OCCUPIED?*/
        return isOccupied(prev, to);
    }

/* HELPING FUNCTIONS --------------------------------------------------------*/

    private Station findRightNextHopFor(Station station)throws IllegalSetupException {
        ArrayList<Station> list = station.getPrevStations();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).getHopsToNewCarriage() ==
                    station.getHopsToNewCarriage()-1) return list.get(i);
        }
        throw new IllegalSetupException("Station "+station.getName()+"has no Prev Station with"+
                                        "Hops-1 == "+(station.getHopsToNewCarriage()-1));
    }

    private void findRightPathFor(Station init, Station from, Station to){
        if(to == from) {
            path.addFirst(to);
            return;
        }
        if(from == to && to == init && to.getPrevStations().isEmpty()){
            return; /*Loop detected*/
        }
        for(int i=0; i<to.getPrevStations().size();++i){
            if(path.isEmpty() && to.getPrevStations().get(i)!= init){
                findRightPathFor(init, from, to.getPrevStations().get(i));
            }
        }
        if(!path.isEmpty()){
            path.addLast(to);
        }
    }

    private Station isOccupied(Station ifYes, Station ifNo){
        if(ifYes == null){ /*NO*/
            if(ifNo.isOccupied()) {
                /*AM I OCCUPIED?*/
                return ifNo; //yes return me
            }else{
                return null;//no, no prev Station or me is occupied
            }
        }else{           /*YES, PREV-STATION isOccupied*/
            return ifYes;
            /*
             *i don't care if i am occupied, prev station is
             *causing trouble already, return prev-station
             */
        }
    }

}
