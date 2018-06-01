package Model.Network;

/**
 * @author Dzianis Brysiuk
 */

//import Model.CommandInterpreter;
import Model.IllegalCommandException;
import Model.Station;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class NetworkController {

    private static NetworkController networkController = new NetworkController();
    private ArrayList<Station> stationList;
    public ClientNetwork clientNetwork;

    public NetworkController(){

    }

    public NetworkController(ArrayList<Station> currentStationList){
        this.stationList = currentStationList;
    }

    /*--NETWORK------------------------------------------------------------------*/
    public void connect (byte[] ip, int port) throws UnknownHostException {
        InetAddress ipAddr = InetAddress.getByAddress(ip);

        if (clientNetwork!=null){
            clientNetwork.setIpAddr(ipAddr);
            clientNetwork.setPort(port);
            clientNetwork.connect();
        }else {
            clientNetwork = new ClientNetwork(ipAddr, port);
            clientNetwork.connect();
        }

    }

    public void disconnect (){
        if (clientNetwork!=null){
            clientNetwork.setIsRunning(false);
        }
    }

    public void sendCommandAnswer (String answer) throws FDZNetworkException {
        clientNetwork.sendMessage(answer);
    }

    /**
     * Create CommandHandler
     * @return CommandHandler
     */
    public static NetworkController getInstance (){

        return NetworkController.networkController;
    }

    /*--TESTMETHODEN-------------------------------------------------------------*/
    public void testCommand(String command) {
        invokeInterpreter(command);
    }


    private void invokeInterpreter(String command){
        /*
        try{
            new CommandInterpreter(command, stationList).run();
        }catch(IllegalCommandException e){
            System.out.println(e.getMessage());
        }
        */
    }
}

