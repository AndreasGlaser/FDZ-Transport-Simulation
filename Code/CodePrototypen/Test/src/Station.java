/*--NOAH LEHMANN-------------------------------------------------------------*/

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Station {

    private String name;
    private ArrayList<Station> prevStations;
    private Semaphore sem;
    private boolean isOccupied = false;
    private int sledInside = -1; //no sled inside

    public Station(String name){
        this.setName(name);

        sem = new Semaphore(1);
        prevStations = new ArrayList<Station>(5);
    }

    public Station(String name, Station prevStation){
        this(name);
        addPrevStation(prevStation);
    }

/*--LOGIC--------------------------------------------------------------------*/

    public synchronized void driveInSled(int id){
        try {
            sem.acquire();
        } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("\t log: "+id+" acquired "+this.name+"'s Semaphore >>in >>");

        if(isOccupied()){   //Stau
            /* TODO Staubehandlung */
        }else{
            setOccupied(true);
            setSledInside(id);
        }

        sem.release();
        System.out.println("\t log: "+id+" released "+this.name+"'s Semaphore >>in >>");
    }

    public synchronized void driveOutSled(int id){
        try {
            sem.acquire();
        } catch (InterruptedException e) { e.printStackTrace(); }

        System.out.println("\t log: "+id+" acquired "+this.name+"'s Semaphore <<out<<");

        if(!isOccupied()){   //no sled inside
            /* TODO Nichts freizugeben */
        }else{
            setOccupied(false);
            setSledInside(-1); //TODO was bei Stau?
        }

        sem.release();
        System.out.println("\t log: "+id+" released "+this.name+"'s Semaphore <<out<<");
    }

/*--SETTER-------------------------------------------------------------------*/

    public void setName(String aName){
        this.name = aName;
    }

    public void setSledInside(int sledId) {
        this.sledInside = sledId;
    }

    private void setOccupied(boolean occupied){
        isOccupied=occupied;
    }

/*--GETTER-------------------------------------------------------------------*/

    public boolean isOccupied() {
        return isOccupied;
    }
    public String getName() {
        return name;
    }

    /*--LIST---------------------------------------------------------------------*/

    public boolean addPrevStation(Station aStation){
        return prevStations.add(aStation);
    }
    public boolean removePrevStation(Station aStation){
        return prevStations.remove(aStation);
    }

}
