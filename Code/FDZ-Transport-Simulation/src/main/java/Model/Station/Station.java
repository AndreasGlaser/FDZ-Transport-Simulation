package Model.Station;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**@author Noah Lehmann*/

public class Station {

    private String name;
    private String shortCut;
    private ArrayList<Station> prevStations;
    private Semaphore sem;
    private boolean isOccupied = false;
    private int sledInside = -2; //no sled inside
    private int hopsToNewCarriage = 1;
    private StationProperty property;
    private ArrayList<Integer> congestionList;

    public Station(String name, String shortCut){
        this.property = new StationProperty();
        this.congestionList = new ArrayList<>(3);
        sem = new Semaphore(1);
        prevStations = new ArrayList<>(5);

        this.setName(name);
        this.setShortCut(shortCut);
    }

    /*--LOGIC--------------------------------------------------------------------*/

    public synchronized void driveInSled(int id){
        //if id == -1 -> empty sled gets first unknown id
        try {
            sem.acquire();
        } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("\t log: "+id+" acquired "+this.name+"'s Semaphore >>in >>");

        this.congestionList.add(id);
        this.property.getChangedProperty().add(1);

        try{
            setSledInside(congestionList.get(0));
        }catch(Exception e){
            setSledInside(-2);
        }

        sem.release();
        System.out.println("\t log: "+id+" released "+this.name+"'s Semaphore >>in >>");
    }

    public synchronized void driveOutSled(int id){
        try {
            sem.acquire();
        } catch (InterruptedException e) { e.printStackTrace(); }

        System.out.println("\t log: "+id+" acquired "+this.name+"'s Semaphore <<out<<");

        if(congestionList.get(0) != id){   //no sled inside
            /* TODO Nichts freizugeben */
            System.out.println("Cannot drive out Sled "+id+" because its stuck in congestion at "+this.getName());
        }else if(congestionList.get(0) == id){
            congestionList.remove(0);
            this.property.getChangedProperty().add(1);
        }
        try{
            setSledInside(congestionList.get(0));
        }catch(Exception e){
            setSledInside(-2);
        }

        sem.release();
        System.out.println("\t log: "+id+" released "+this.name+"'s Semaphore <<out<<");
    }

    /*--SETTER-------------------------------------------------------------------*/

    public void setName(String aName){
        this.name = aName;
        this.property.getChangedProperty().add(1);
    }
    public void setShortCut(String aShortCut){ this.shortCut = aShortCut;
        this.property.getChangedProperty().add(1);}
    public void setSledInside(int sledId) {
        /*TODO Threadsafe*/
        try{
            this.congestionList.set(0, sledId);
            sledInside = congestionList.get(0);
        }catch(IndexOutOfBoundsException e){
            this.congestionList.add(sledId);
        }
        this.property.getChangedProperty().add(1);
    }
    public void setHopsToNewCarriage(int hops){
        this.property.getChangedProperty().add(1);
    }
    private void setOccupied(boolean occupied){
        isOccupied=occupied;
    }

    /*--GETTER-------------------------------------------------------------------*/

    public boolean isOccupied() {
        return isOccupied;
    }
    public boolean isCongested(){ return (congestionList.size() != 1); /*TODO*/}
    public String getName() {
        return name;
    }
    public String getShortCut(){return shortCut;}
    public int getSledInside(){return sledInside;}
    public ArrayList<Station> getPrevStations(){return prevStations;}
    public int getHopsToNewCarriage(){ return hopsToNewCarriage; }
    public StationProperty getStationProperty(){ return property; }
    public ArrayList<Integer> getIdsInStation(){ return congestionList;}

    /*--LIST---------------------------------------------------------------------*/

    public void addPrevStation(Station aStation) {
        for (int i = 0; i < prevStations.size(); i++) {
            if(prevStations.get(i).getName().compareTo(aStation.getName()) == 0){
                return;
            }
        }
        prevStations.add(aStation);
        this.property.getChangedProperty().add(1);
    }
    public void deletePrevStation(Station aStation) throws NullPointerException {
        prevStations.remove(aStation);
        this.property.getChangedProperty().add(1);
    }

}
