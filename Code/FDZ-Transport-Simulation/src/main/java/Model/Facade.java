package Model;

import Model.Exception.IllegalSetupException;
import Model.Network.NetworkController;
import Model.Station.Station;
import Model.Station.StationHandler;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class Facade {

    private NetworkController networkController;
    private StationHandler stationHandler;
    private Thread connectionThread;

    public Facade(){
        networkController = NetworkController.getInstance();
        stationHandler = StationHandler.getInstance();
    }

/* NETWORK ------------------------------------------------------------------*/

    public void testCommand(String command){
        networkController.testCommand(command);
    }

    public synchronized void connect(byte[] ip, int port){
        if(connectionThread == null){
            connectionThread = new Thread(){
                @Override
                public void run (){
                    try{
                        NetworkController.getInstance().connect(ip, port);
                    }catch(UnknownHostException e){
                    /*TODO Log exception*/
                    }
                }
            };
            connectionThread.start();
        }else{
            connectionThread.interrupt();
            connectionThread = null;
            connect(ip, port);
        }

    }

    public void disconnect(){
        networkController.disconnect();
    }

    public SimpleBooleanProperty connectedProperty(){
        return new SimpleBooleanProperty(true);
        /*TODO*/
    }

    /* STATIONS -----------------------------------------------------------------*/

    public void addStation(String name, String shortCut) throws IllegalSetupException{
        if(name != null && name.length() != 0 && shortCut.length() == 2){
            stationHandler.addStation(name, shortCut);
        }else{
            throw new IllegalSetupException("Name or ShortCut not valid");
        }
    }

    public void deleteStation(String name) throws NullPointerException{
        stationHandler.deleteStation(name);
    }

    public void addPrevStation(String toName, String prevName) throws NullPointerException{
        Station to = stationHandler.getStationByName(toName);
        Station prev = stationHandler.getStationByName(prevName);
        to.addPrevStation(prev);
    }

    public void deletePrevStation(String nameOf, String prevName) throws NullPointerException{
        Station to = stationHandler.getStationByName(nameOf);
        Station prev = stationHandler.getStationByName(prevName);
        to.deletePrevStation(prev);
    }

    public void setHopsToNewCarriage(String stationName, int hops) throws IllegalSetupException, NullPointerException {
        Station station = stationHandler.getStationByName(stationName);
        if (hops < stationHandler.getStationList().size() && hops >= 1){
            station.setHopsToNewCarriage(hops);
        }else{
            throw new IllegalSetupException("hops is either smaller than 1 or too big");
        }
    }

    public void setStationName(String oldName, String newName) throws NullPointerException, IllegalSetupException{
        if(newName.length() != 0 || newName != null) {
            Station station = stationHandler.getStationByName(newName);
            station.setName(newName);
        }else{
            throw new IllegalSetupException("Input Name is invalid");
        }
    }
    public void setStationShortCut(String oldShortCut, String newShortCut) throws NullPointerException, IllegalSetupException{
        if(newShortCut.length() == 2){
            Station station = stationHandler.getStationByShortCut(oldShortCut);
            station.setShortCut(newShortCut);
        }else{
            throw new IllegalSetupException("Input ShortCut is invalid");
        }
    }

    public ArrayList<Integer> getSledsInStation(String name) throws NullPointerException{
        return stationHandler.getStationByName(name).getIdsInStation();
    }

    public SimpleIntegerProperty getStationChangedProperty(String name) throws NullPointerException{
        return stationHandler.getStationByName(name).getStationProperty();
    }
}
