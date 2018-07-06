package Model.Network;



import Model.Command.CommandInterpreter;
import Model.Facade;
import Model.Logger.LoggerInstance;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author Dzianis Brysiuk
 */
public class NetworkController extends ConnectionObservable implements ConnectionObserver{

    //Singleton thread safe
    private static final NetworkController ourInstance = new NetworkController();

    //Client network
    private ClientNetwork clientNetwork;

    //Setup message structure
    final private String ACK2_HEAD = "StSTA002";
    final private String ACK1_CNU_CNE_CE_END = "0000";

    //Connection to Adapter
    private boolean isConnected = false;

    //If sending command finished return true
    final private boolean finished = true;

    private NetworkController(){
    }

    /*--NETWORK CONTROLLER-------------------------------------------------------------*/

    /**
     * Initialization of Client Network with given IP Address and Port Number.
     * After that start connection to Adapter
     * @param ip The IP-Address that given from User for connection to Adapter
     * @param port Port-Number that given from User for connection to Adapter
     */
    public void connect (byte[] ip, int port) {

        //init InetAddress
        InetAddress ipAddr = null;
        try {
            ipAddr = InetAddress.getByAddress(ip);
        } catch (UnknownHostException e) {
            LoggerInstance.log.error("Invalid IP-Address: ", new UnknownHostException("Stacktrace"));
        }

        LoggerInstance.log.info("Open connection to Adapter {}:{}",Objects.requireNonNull(ipAddr).getHostAddress(),port);

        //ClientNetwork is already existing. Set new IP/Port and connect to Adapter
        if (clientNetwork!=null){
            clientNetwork.setIpAddr(ipAddr);
            clientNetwork.setPort(port);
            clientNetwork.connect();
        }
        //ClientNetwork is not existing. Set IP/Port, connect to Adapter and add Observer for connection status
        else {
            clientNetwork = new ClientNetwork(ipAddr, port);
            clientNetwork.connect();
            clientNetwork.addObserver(this);
        }

    }

    /**
     * Close connection to Adapter
     */
    public void disconnect (){
        LoggerInstance.log.info("Close connection to Adapter {}:{}",clientNetwork.getIpAddr().getHostAddress(),clientNetwork.getPort());
        if (clientNetwork!=null){
            clientNetwork.setIsRunning(false);
        }
    }

    /**
     * Generate and send Acknowledge_001 for received Command, that Interpreter understood.
     * @param msgID unique messageID from received Command.
     * @return true that the message was successfully send to adapter.
     */
    public boolean acknowledge1 (String msgID){
        final String ACK1_HEAD = "StSTA001";
        String message = ACK1_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        LoggerInstance.log.info("Send ACK01: {} to Adapter",message);
        clientNetwork.sendMessage(message);
        return finished;
    }

    /**
     * Generate and send Error if Interpreter does not understand received Command
     * @param msgID unique messageID from received Command
     * @return true that the message was successfully send to adapter.
     */
    public boolean commandNotUnderstood(String msgID){
        final String CNU_HEAD = "StSTF000";
        String message = CNU_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        if(msgID!=null) {
            LoggerInstance.log.info("Send command not understood: {} to Adapter", message);
            clientNetwork.sendMessage(message);
        }else {
            LoggerInstance.log.error("Illegal command: {}",message);
        }
        return finished;
    }

    /**
     * Generate and send Error if received Command can not execute
     * @param msgID unique messageID from received Command
     */
    public void commandNotExecuted (String msgID){
        final String CNE_HEAD = "0000";
        String message = CNE_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        LoggerInstance.log.info("Send command not Executed: {} to Adapter",message);
        clientNetwork.sendMessage(message);
    }

    /* Not yet supported
     * Generate and send Error if Carriage not reach destination
    public void notReachDestination (String msgID, int id){
        final String CNRD_HEAD = "StSTF002";
        final String CNRD_END = "0003";
        String message = CNRD_HEAD+msgID+CNRD_END+id;
        LoggerInstance.log.info("Send command not reach destination: {} to Adapter",message);
        clientNetwork.sendMessage(message);
    }
    */

    /* Not yet supported
    public void criticalError (String msgID){
        final String CE_HEAD = "StSTF999";
        String message = CE_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        clientNetwork.sendMessage(message);
    }
    */

    /**
     * Generate and send Acknowledge_002 for Commands that executed and do not need value of Carriage ID
     * @param msgID unique messageID from received Command
     */
    public void acknowledge2 (String msgID){
        final String ACK2_END = "0000";
        String message = ACK2_HEAD+msgID+ACK2_END;
        //Update GUI Status
        new Facade().setStatus("Finished");
        LoggerInstance.log.info("Send ACK02: {} to Adapter",message);
        clientNetwork.sendMessage(message);

    }

    /**
     * Generate and send Acknowledge_002 for Command that executed and need value of Carriage ID
     * @param msgID unique messageID from received Command
     * @param emptyCarriage Carriage ID number
     */
    public void acknowledge2 (String msgID, boolean emptyCarriage){
        final String EMPTY_ID = "00";
        String ACK2_CNRD_END = "0002";
        //Update GUI Status
        new Facade().setStatus("Finished");
        String message = ACK2_HEAD+msgID+ ACK2_CNRD_END +EMPTY_ID;
        LoggerInstance.log.info("Send ACK02: {} to Adapter",message);
        clientNetwork.sendMessage(message);
    }

    /**
     * @return Network Controller
     */
    public static NetworkController getInstance (){

        return ourInstance;
    }

    /*--TESTMETHODS FOR CLI-----------------------------------------------------*/
    public void testCommand(String command) {
        invokeInterpreter(command);
    }


    private void invokeInterpreter(String command){
        new Thread(new CommandInterpreter(command)).start();
    }

    /*---------------------------------------------------------------------------*/

    /**
     * Update connection status if connection changes
     */
    @Override
    public void update() {
        isConnected = clientNetwork.connectionToAdapter();
        setChanged();
    }

    /**
     * Status of connection to Adapter
     * @return boolean state of connection status to Adapter
     */
    public boolean isConnected(){
        return isConnected;
    }
}

