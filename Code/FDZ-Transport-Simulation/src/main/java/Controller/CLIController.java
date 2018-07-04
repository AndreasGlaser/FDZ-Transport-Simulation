package Controller;

import Model.Facade;

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
            facade.connect(ip, port);
            return true;
            /*TODO boolean unused*/
        }else{
            return false;
        }
    }

    public void disconnect(){
        facade.disconnect();
    }



    /* STATIONS -----------------------------------------------------------------*/

   /* public boolean addStation(String name, String shortCut){
        if(name != null && shortCut != null){
            facade.addStation(name,shortCut);
            return true;
        }else {
            return false;
        }
    }

    /*public boolean deleteStation(String name){
        if(name != null){
           return facade.deleteStation(name);
        }else{
            return false;
        }
    }

    /*public boolean addPrevStation(String toName, String prevName){
        if(toName != null && prevName != null){
            return facade.addPrevStation(toName, prevName);
        }else{
            return false;
        }
    }

   /* public boolean setHopsToNewCarriage(String stationName, int hops){
        if(stationName != null && hops >= 0){
            return facade.setHopsToNewCarriage(stationName, hops);
        }else{
            return false;
        }
    }

   /* public List<Station> getStationList(){
        return Collections.unmodifiableList(facade.getStationList());
    }
*/

}
