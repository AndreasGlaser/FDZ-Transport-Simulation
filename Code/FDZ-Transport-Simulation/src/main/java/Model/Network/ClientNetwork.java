package Model.Network;

import Model.Command.CommandInterpreter;
import Model.Exception.FDZNetworkException;
import Model.Logger.LoggerInstance;
import java.net.InetAddress;

/**
 * @author Dennis Brysiuk
 */
class ClientNetwork extends ConnectionObservable{

    //instance of class Network
    private Network client;
    //IP Address from Adapter
    private InetAddress ipAddr;
    //Adapter Port Number
    private int port;
    //flag to stop Threads
    private volatile boolean isRunning = false;
    //Received command from Adapter
    private String messageOutgoing;
    //Connection Status
    private boolean connection=false;

    /**
     * Thread that waiting for Commands from Adapter and after receive sends receiving message to CommandInterpreter
     */
    private Thread receiveMsg;

    /**
     * Set IP Address and Port number of Adapter
     * @param ipAddr contains IP Address from Adapter
     * @param port contains Port Number of Adapter for Connection
     */
    public ClientNetwork (InetAddress ipAddr, int port){
        this.ipAddr=ipAddr;
        this.port=port;
    }

    /**
     *Starts Thread that tries to connect to Adapter, after successfully connection starts Thread that
     *wait for messages from Adapter.
     */
     public void connect (){
         //Create new Network Socket with given IP/Port
         if (!isRunning){
             LoggerInstance.log.debug("Create new Network Socket {}:{}",ipAddr.getHostAddress(),port);
             client = new Network(ipAddr, port);
             this.isRunning=true;
        }
        //Change IP/Port for existing Network Socket
        else {
             LoggerInstance.log.debug("Changing IP-Address and Port-Number to {}:{}",ipAddr.getHostAddress(),port);
             client.setSocketAddr(ipAddr, port);
             try {
                 LoggerInstance.log.debug("Close existing Socket");
                 //Need to close Socket because the Socket is already opened
                 client.closeSocket();
             } catch (FDZNetworkException e) {
                 LoggerInstance.log.warn("Cant close existing socket: ", new FDZNetworkException(e));
             }
         }
        //Connect to Adapter Thread
         Thread firstConnection = new Thread() {
             public synchronized void run() {
                 //tries to connect as long as connected to Adapter or User Interrupt the connection
                 do {
                     try {
                         LoggerInstance.log.debug("Trying to open connection to Adapter: {}:{}", ipAddr.getHostAddress(), port);
                         client.openConnection();
                     } catch (FDZNetworkException e) {
                         LoggerInstance.log.warn("Cant open connection to Adapter: {}:{}", ipAddr.getHostAddress(), port);
                         try {
                             LoggerInstance.log.debug("Close existing Socket");
                             client.closeSocket();
                         } catch (FDZNetworkException e1) {
                             LoggerInstance.log.warn("Cant close existing socket: ", new FDZNetworkException(e));
                         }
                     }
                 } while (!client.isConnected() && isRunning);

                 //update connection status and start Thread that waiting all time for Message from Adapter
                 if (isRunning) {
                     setConnection(true);
                     receiveMsg.start();
                     LoggerInstance.log.info("Successfully connected to Adapter: {}:{}", ipAddr.getHostAddress(), port);
                 }

             }
         };

         firstConnection.start();

         receiveMsg = new Thread (){
             public synchronized void run (){

                 String messageIncomming;
                 isRunning=true;

                 while (isRunning) {
                     try {
                         //Wait for Command
                         messageIncomming = client.receiveMessage();
                         LoggerInstance.log.info("Received command: {}",messageIncomming);
                         //Start Interpreter with receiving command
                         new CommandInterpreter(messageIncomming).start();

                     } catch (FDZNetworkException e) {
                         LoggerInstance.log.debug("Connection lost to Adapter: {}:{}",ipAddr.getHostAddress(),port);
                         try {
                             client.closeSocket();
                         } catch (FDZNetworkException e1) {
                             LoggerInstance.log.warn("Cant close existing Socket: ", new FDZNetworkException(e));
                         }
                         //Reconnect if connection lost to Adapter
                         setConnection(false);
                         while (!client.isConnected()&&isRunning) {
                             LoggerInstance.log.info("Trying to reconnect to Adapter: {}:{} ", ipAddr.getHostAddress(),port);
                             try {
                                 LoggerInstance.log.debug("Close existing Socket");
                                 client.closeSocket();
                                 LoggerInstance.log.debug("Trying to connect to Adapter: {}:{}",ipAddr.getHostAddress(),port);
                                 client.openConnection();
                             } catch (FDZNetworkException e1) {
                                 LoggerInstance.log.warn("Cant open connection to Adapter: ", new FDZNetworkException(e));

                             }
                         }
                         if(isRunning){
                             setConnection(true);
                             LoggerInstance.log.info("Successfully connected to Adapter: {}:{}",ipAddr.getHostAddress(),port);
                         }
                     }

                 }
                 close();

             }
         };
    }

    /**
     * Close existing Network connection
     */
    @SuppressWarnings("FinalizeCalledExplicitly")
    private void close(){
        if (client!=null){
            this.client.finalize();
            LoggerInstance.log.info("Successfully disconnected from Adapter");
        }
    }

    /**
     * Starts Thread that tries to send Message to Adapter and stops only if message successfully send.
     * @param message Answer of receiving Command
     */
    public void sendMessage (String message){
        this.messageOutgoing=message;
        final Thread sendMsg = new Thread(() -> {
            try {
                client.sendMessage(messageOutgoing);
            } catch (FDZNetworkException e) {
                LoggerInstance.log.info("Cant send Message: {} to Adapter",messageOutgoing);
                try {
                    LoggerInstance.log.debug("Close existing Socket");
                    client.closeSocket();
                } catch (FDZNetworkException e1) {
                    LoggerInstance.log.warn("Cant close existing socket: ", new FDZNetworkException(e1));
                }
                setConnection(false);
                //Reconnect if connection lost to Adapter and tries to send message again
                while (!client.isConnected()&&isRunning) {
                    try {
                        LoggerInstance.log.debug("Close existing Socket");
                        client.closeSocket();
                        LoggerInstance.log.debug("Trying to connect to Adapter: {}:{}",ipAddr.getHostAddress(),port);
                        client.openConnection();
                        LoggerInstance.log.debug("Resending Message: {}",messageOutgoing);
                        client.sendMessage(messageOutgoing);
                    } catch (FDZNetworkException e1) {
                        LoggerInstance.log.warn("Cant open connection to Adapter: ", new FDZNetworkException(e1));

                    }
                }
                if(isRunning){
                    setConnection(true);
                    LoggerInstance.log.info("Successfully connected to Adapter: {}:{}",ipAddr.getHostAddress(),port);
                }
            }
        });
        sendMsg.start();
    }

    /**
     * Changes connection Status
     * @param connection boolean state of connection Status
     */
    private void setConnection (boolean connection){
        this.connection=connection;
        setChanged();
    }

    /**
     * Connection Status at the moment
     * @return boolean state of connection Status
     */
    public boolean connectionToAdapter (){
        return connection;
    }


    /*------------------------------SETTER/GETTER------------------------------*/
    public void setIpAddr (InetAddress ipAddr){
        this.ipAddr=ipAddr;
    }
    public InetAddress getIpAddr (){
        return this.ipAddr;
    }

    public void setPort (int port){
        this.port=port;
    }
    public int getPort (){
        return this.port;
    }

    /**
     * Stop connection to Adapter and stop all running Threads
     */
    public void setIsRunning (boolean isRunning){
        if(receiveMsg.isAlive()){
            receiveMsg.interrupt();
        }
        this.isRunning=isRunning;
    }

}
