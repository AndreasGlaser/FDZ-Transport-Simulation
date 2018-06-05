package Model;

import Model.Network.NetworkController;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class Facade {

    private NetworkController networkController;
    private StationHandler stationHandler;

    public Facade(){
        networkController = NetworkController.getInstance();
        stationHandler = StationHandler.getInstance();
    }

/* NETWORK ------------------------------------------------------------------*/

    public void testCommand(String command){
        networkController.testCommand(command);
    }

    public boolean connect(byte[] ip, int port){
        try{
            networkController.connect(ip, port);
            return true;
        }catch(UnknownHostException e){
            return false;
        }
    }

    public void disconnect(){
        networkController.disconnect();
    }

    public boolean isConnected(){
        /*TODO*/
        return false;
    }

    /* STATIONS -----------------------------------------------------------------*/

    public void addStation(String name, String shortCut){
        stationHandler.addStation(name, shortCut);
    }

    public boolean deleteStation(String name){
        try{
            stationHandler.deleteStation(name);
            return true;
        }catch(NullPointerException e){
            return false;
        }
    }

    public boolean addPrevStation(String toName, String prevName){
        Station to = stationHandler.getStationByName(toName);
        Station prev = stationHandler.getStationByName(prevName);
        try{
            return to.addPrevStation(prev);
        }catch(NullPointerException e){
            return false;
        }
    }

    public boolean setHopsToNewCarriage(String stationName, int hops){
        Station station = stationHandler.getStationByName(stationName);
        try {
            if (hops < stationHandler.getStationList().size()){
                station.setHopsToNewCarriage(hops);
                return true;
            }else{
                return false;
            }
        }catch(NullPointerException e){
            return false;
        }
    }

    public ArrayList<Station> getStationList(){
        return stationHandler.getStationList();
    }
}
