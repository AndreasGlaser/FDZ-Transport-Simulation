package Controller;

import Model.Facade;
import Model.Station.Station;

import java.util.Collections;
import java.util.List;

public class CLIController {

    Facade facade;

    public CLIController(){
        facade = new Facade();
    }

    public void testCommand(String command){
        facade.testCommand(command);
    }

    public boolean connect(byte[] ip, int port){
        if(port>=0 && ip.length == 4){
            return facade.connect(ip, port);
        }else{
            return false;
        }
    }

    public void disconnect(){
        facade.disconnect();
    }

    public boolean isConnected(){
        return facade.isConnected();
    }

    /* STATIONS -----------------------------------------------------------------*/

    public boolean addStation(String name, String shortCut){
        if(name != null && shortCut != null){
            facade.addStation(name,shortCut);
            return true;
        }else {
            return false;
        }
    }

    public boolean deleteStation(String name){
        if(name != null){
            return facade.deleteStation(name);
        }else{
            return false;
        }
    }

    public boolean addPrevStation(String toName, String prevName){
        if(toName != null && prevName != null){
            return facade.addPrevStation(toName, prevName);
        }else{
            return false;
        }
    }

    public boolean setHopsToNewCarriage(String stationName, int hops){
        if(stationName != null && hops >= 0){
            return facade.setHopsToNewCarriage(stationName, hops);
        }else{
            return false;
        }
    }

    public List<Station> getStationList(){
        return Collections.unmodifiableList(facade.getStationList());
    }


}
