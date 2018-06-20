package Model;

import Model.Command.ShutdownObserver;
import Model.Command.ShutdownTransport;
import Model.Command.TimeMode;
import Model.Exception.IllegalSetupException;
import Model.Network.ConnectionObserver;
import Model.Network.NetworkController;
import Model.Station.Station;
import Model.Station.StationHandler;
import Model.Station.StationObserver;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Facade {

    private final NetworkController networkController;
    private final StationHandler stationHandler;
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
            connectionThread = new Thread(() -> {
                NetworkController.getInstance().connect(ip, port);
            });
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

    public boolean isConnected(){
        return NetworkController.getInstance().isConnected();
    }

    /* STATIONS -----------------------------------------------------------------*/

    public void addStation(String name, String shortCut) throws IllegalSetupException{
        if(name != null && name.length() != 0 && shortCut.length() == 2){
            stationHandler.addStation(new Station(name, shortCut));
        }else{
            throw new IllegalSetupException("Name or ShortCut not valid");
        }
    }

    public void deleteStation(String name) throws NullPointerException{
        Station station = stationHandler.getStationByName(name);
        stationHandler.deleteStation(station);
    }

    public void addPrevStation(String toName, String prevName, int pathTime) throws NullPointerException{
        Station to = stationHandler.getStationByName(toName);
        Station prev = stationHandler.getStationByName(prevName);
        to.addPrevStation(prev, pathTime);
    }

    public void deletePrevStation(String nameOf, String prevName) throws NullPointerException{
        Station to = stationHandler.getStationByName(nameOf);
        Station prev = stationHandler.getStationByName(prevName);
        to.deletePrevStation(prev);
    }

    public void setHopsToNewCarriage(String stationName, int hops) throws IllegalSetupException, NullPointerException {
        Station station = stationHandler.getStationByName(stationName);
        if (hops < stationHandler.getAmountOfStations() && hops >= 1){
            station.setHopsToNewCarriage(hops);
        }else{
            throw new IllegalSetupException("hops is either smaller than 1 or too big");
        }
    }

    public void setStationName(String oldName, String newName) throws NullPointerException, IllegalSetupException{
        if(newName != null && newName.length() != 0) {
            Station station = stationHandler.getStationByName(oldName);
            station.setName(newName);
        }else{
            throw new IllegalSetupException("Input Name is invalid");
        }
    }
    public void setStationShortCut(String name, String newShortCut) throws NullPointerException, IllegalSetupException{
        if(newShortCut.length() == 2){
            Station station = stationHandler.getStationByName(name);
            station.setShortCut(newShortCut);
        }else{
            throw new IllegalSetupException("Input ShortCut is invalid");
        }
    }

    public ArrayList<Integer> getSledsInStation(String name) throws NullPointerException{
        return stationHandler.getStationByName(name).getSledsInStation();
    }

    public void setSledsInStation(@NotNull String stationName, @NotNull List<Integer> ids) throws NullPointerException{
        stationHandler.getStationByName(stationName).setSledsInStation(ids);
    }

    public void addToStationObservable(String name, StationObserver observer) throws NullPointerException{
        stationHandler.getStationByName(name).addObserver(observer);
    }

    public void addToConnectionObservable(ConnectionObserver observer){
        networkController.addObserver(observer);
    }

    public void addToShutdownObservable(ShutdownObserver observer){ShutdownTransport.addObserver(observer);}

    public void setFastTime(boolean activated){
        TimeMode.fastModeActivated = activated;
        System.out.println(activated +" timeMode");
    }

    public void setPathTime(String stationName, String prevName, int time) throws NullPointerException {
        Station station = stationHandler.getStationByName(stationName);
        Station prev = stationHandler.getStationByName(prevName);
        station.setPathTime(prev, time);
    }


}
