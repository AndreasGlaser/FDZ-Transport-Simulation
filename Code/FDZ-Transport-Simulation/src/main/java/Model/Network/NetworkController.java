package Model.Network;

/**
 * @author Dzianis Brysiuk
 */

import Model.Command.CommandInterpreter;
import Model.Logger.LoggerInstance;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class NetworkController extends ConnectionObservable implements ConnectionObserver{

    private static NetworkController ourInstance = new NetworkController();

    private ClientNetwork clientNetwork;

    final private String ACK2_HEAD = "StSTA002";
    final private String ACK2_CNRD_END = "0002";
    final private String ACK1_CNU_CNE_CE_END = "0000";
    private boolean isConnected;

    private NetworkController(){
        isConnected = false;
    }

    /*--NETWORK------------------------------------------------------------------*/

    /**
     * Init clientNetwork with given IP/Port and start connection to Adapter
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

        LoggerInstance.log.info("Open connection to Adapter {}:{}",ipAddr.getAddress(),port);

        //ClientNetwork is already existing. Set new IP/Port and connect to Adapter
        if (clientNetwork!=null){
            clientNetwork.setIpAddr(ipAddr);
            clientNetwork.setPort(port);
            clientNetwork.connect();
        }
        //ClientNetwork is not existing. Set IP/Port, connect to Adapter and add Observer for connection
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
        LoggerInstance.log.info("Close connection to Adapter {}:{}",clientNetwork.getIpAddr().getAddress(),clientNetwork.getPort());
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
        final String ACK1_HEAD = "StSTA001";
        String message = ACK1_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        LoggerInstance.log.info("Send ACK01: {} to Adapter",message);
        clientNetwork.sendMessage(message);
        return true;
    }

    /**
     * Generate and send Error if Interpreter does not understand
     * received Command
     * @param msgID messageID from received Command
     * @return true send sending Error to Adapter finished
     */
    public boolean commandNotUnterstood (String msgID){
        final String CNU_HEAD = "StSTF000";
        String message = CNU_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        LoggerInstance.log.info("Send command not understood: {} to Adapter",message);
        clientNetwork.sendMessage(message);
        return true;
    }

    /**
     * Generate and send Error if received Command cant executed
     * @param msgID messageID from received Command
     */
    public void commandNotExecuted (String msgID){
        final String CNE_HEAD = "StSTF001";
        String message = CNE_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        LoggerInstance.log.info("Send command not Executed: {} to Adapter",message);
        clientNetwork.sendMessage(message);
    }

    /**
     * Generate and send Error if Carriage not reach destination
     * @param msgID messageID from received Command
     * @param id Carriage ID number
     */
    public void notReachDestination (String msgID, int id){
        final String CNRD_HEAD = "StSTF002";
        final String CNRD_END = "0003";
        String message = CNRD_HEAD+msgID+CNRD_END+id;
        LoggerInstance.log.info("Send command not reach destination: {} to Adapter",message);
        clientNetwork.sendMessage(message);
    }

    /**
     * Generate and send Critical Error to Adapter
     * @param msgID messageID from received Command
     */
    public void criticalError (String msgID){
        final String CE_HEAD = "StSTF999";
        String message = CE_HEAD+msgID+ACK1_CNU_CNE_CE_END;
        clientNetwork.sendMessage(message);
    }

    /**
     * Generate and send Acknowledge_002 for Commands that does not need Carriage ID
     * and was executed to Adapter
     * @param msgID messageID from received Command
     */
    public void acknowledge2 (String msgID){
        String message = ACK2_HEAD+msgID+ACK2_CNRD_END;
        LoggerInstance.log.info("Send ACK02: {} to Adapter",message);
        clientNetwork.sendMessage(message);
    }

    /**
     * Generate and send Acknowledge_002 for Command that need Carriage ID
     * and was executed to Adapter
     * @param msgID messageID from received Command
     * @param emptyCarriage Carriage ID number
     */
    public void acknowledge2 (String msgID, boolean emptyCarriage){
        final String EMPTY_ID = "00";
        String message = ACK2_HEAD+msgID+ACK2_CNRD_END+EMPTY_ID;
        LoggerInstance.log.info("Send ACK02: {} to Adapter",message);
        clientNetwork.sendMessage(message);
    }

    /**
     * @return CommandHandler
     */
    public static NetworkController getInstance (){

        return ourInstance;
    }

    /*--TESTMETHODS FOR CLI-----------------------------------------------------*/
    public void testCommand(String command) {
        invokeInterpreter(command);
    }


    private void invokeInterpreter(String command){
        new CommandInterpreter(command).run();
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
     * @return boolean state of connection status
     */
    public boolean isConnected(){
        return isConnected;
    }
}

