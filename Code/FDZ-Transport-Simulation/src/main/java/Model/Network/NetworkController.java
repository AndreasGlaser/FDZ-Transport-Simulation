package Model.Network;

/**
 * @author Dzianis Brysiuk
 */

//import Model.CommandInterpreter;
import Model.CommandInterpreter;
import Model.IllegalCommandException;
import Model.Station;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;


public class NetworkController {

    private static NetworkController ourInstance = new NetworkController();

    private ClientNetwork clientNetwork;
    final private String ACK1_HEAD = "StSTA001";
    final private String ACK2_HEAD = "StSTA002";
    final private String ACK2_CNRD_END = "0002";
    final private String EMPTY_ID = "00";
    final private String CNU_HEAD = "StSTF000";
    final private String ACK1_CNU_CNE_CE_END = "0000";
    final private String CNE_HEAD = "StSTF001";
    final private String CNRD_HEAD = "StSTF002";
    final private String CNRD_END = "0003";
    final private String CE_HEAD = "StSTF999";


    private NetworkController(){
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

    /**
     * Generate and send Acknowledge_001 for received Commands
     * that Interpreter understood
     * @param msgID messageID from received Command
     * @return boolean value that the message was successfully send
     * to adapter
     */
    public boolean acknowledge1 (String msgID){
//        String message = ACK1_HEAD+msgID+ACK1_CNU_CNE_CE_END;
//        clientNetwork.sendMessage(message);
        return true;
    }

    /**
     * Generate and send Error if Interpreter does not understand
     * received Command
     * @param msgID messageID from received Command
     * @return true send sending Error to Adapter finished
     */
    public boolean commandNotUnterstood (String msgID){
//        String message = CNU_HEAD+msgID+ACK1_CNU_CNE_CE_END;
//        clientNetwork.sendMessage(message);
        return true;
    }

    /**
     * Generate and send Error if received Command cant executed
     * @param msgID messageID from received Command
     */
    public void commandNotExecuted (String msgID){
        String message = CNE_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        clientNetwork.sendMessage(message);
    }

    /**
     * Generate and send Error if Carriage not reach destination
     * @param msgID messageID from received Command
     * @param id Carriage ID
     */
    public void notReachDestination (String msgID, int id){
        String message = CNRD_HEAD+msgID+CNRD_END+id;
        clientNetwork.sendMessage(message);
    }

    /**
     * Generate and send Critical Error to Adapter
     * @param msgID messageID from received Command
     */
    public void criticalError (String msgID){
        String message = CE_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        clientNetwork.sendMessage(message);
    }

    /**
     * Generate and send Acknowledge_002 for Commands that does not need Carriage ID
     * and was executed to Adapter
     * @param msgID messageID from received Command
     */
    public void acknowledge2 (String msgID){
        //String message = ACK2_HEAD+msgID+ACK2_CNRD_END;
        //clientNetwork.sendMessage(message);
    }

    /**
     * Generate and send Acknowledge_002 for Command that need Carriage ID
     * and was executed to Adapter
     * @param msgID messageID from received Command
     * @param emptyCarriage Carriage ID number
     */
    public void acknowledge2 (String msgID, boolean emptyCarriage){
        //String message = ACK2_HEAD+msgID+ACK2_CNRD_END+EMPTY_ID;
        //clientNetwork.sendMessage(message);
    }

    /**
     * Create CommandHandler
     * @return CommandHandler
     */
    public static NetworkController getInstance (){

        return ourInstance;
    }

    /*--TESTMETHODEN-------------------------------------------------------------*/
    public void testCommand(String command) {
        invokeInterpreter(command);
    }


    private void invokeInterpreter(String command){
        new CommandInterpreter(command).run();
    }
}

