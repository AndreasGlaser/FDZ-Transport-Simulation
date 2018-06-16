package Model.Network;

/**
 * @author Dzianis Brysiuk
 */

import Model.Command.CommandInterpreter;
import Model.Logger.LoggerInstance;

import java.net.InetAddress;

public class ClientNetwork extends ConnectionObservable{

    //instance of class Network
    private Network client;
    //IP Adresse from Adapter
    private InetAddress ipAddr;
    //Adapter Port Nummer
    private int port;
    //flag to stop Thread
    private volatile boolean isRunning = false;
    private String messageOutgoing;
    private boolean connection=false;

    /**
     * Thread is waiting for Command from connected Adapter and send receiving message to CommandInterpreter
     */
    Thread receiveMsg;
    Thread firstConnect;

    /**
     * Creates a new Thread for CleintNetwork
     * @param ipAddr contains IP Address from Adapter
     * @param port contains Port Nummer of Adapter for Connection
     */
    public ClientNetwork (InetAddress ipAddr, int port){
        this.ipAddr=ipAddr;
        this.port=port;
    }

    /**
     * Message Listener for incomming Message from Adapter
     */
     public void connect (){
         if (!isRunning){
             LoggerInstance.log.debug("Create new Network socket {}:{}",ipAddr.getAddress(),port);
            client = new Network(ipAddr, port);
             this.isRunning=true;
        }else {
             LoggerInstance.log.debug("Changing IP-Address and Port-Number to {}:{}",ipAddr.getAddress(),port);
             client.setSocketAddr(ipAddr, port);
             try {
                 LoggerInstance.log.debug("Close existing Socket");
                 client.closeSocket();
             } catch (FDZNetworkException e) {
                 LoggerInstance.log.warn("Cant close existing socket: ", new FDZNetworkException(e));
             }
         }
        //Connect to Adapter
        firstConnect = new Thread (){
             public synchronized void run (){
                 do{
                     try {
                         LoggerInstance.log.debug("Trying to open connection to Adapter: {}:{}",ipAddr.getAddress(),port);
                         client.openConnection();
                     }catch (FDZNetworkException e){
                         LoggerInstance.log.warn("Cant open connection to Adapter: {}:{}",ipAddr.getAddress(),port);
                         try {
                             LoggerInstance.log.debug("Close existing Socket");
                             client.closeSocket();
                         } catch (FDZNetworkException e1) {
                             LoggerInstance.log.warn("Cant close existing socket: ", new FDZNetworkException(e));
                         }
                     }
                 }while (!client.isConnected() && isRunning);
                 if (isRunning){
                     setConnection(true);
                 }

                 if(isRunning){
                     LoggerInstance.log.info("Succefull connected to Adapter: {}:{}",ipAddr.getAddress(),port);
                 }


                 if(isRunning){
                     receiveMsg.start();
                 }
             }
        };
         firstConnect.start();



         receiveMsg = new Thread (){
             public synchronized void run (){

                 String messageIncomming;
                 isRunning=true;

                 //wait and receive messages from Adapter
                 while (isRunning) {
                     try {

                         messageIncomming = client.receiveMessage();
                         LoggerInstance.log.info("Received command: {}",messageIncomming);
                         new CommandInterpreter(messageIncomming).start();

                     } catch (FDZNetworkException e) {
                         LoggerInstance.log.info("Connection lost to Adapter: {}:{}",ipAddr.getAddress(),port);
                         LoggerInstance.log.debug("Connection error: ", new FDZNetworkException(e));
                         try {
                             client.closeSocket();
                         } catch (FDZNetworkException e1) {
                             LoggerInstance.log.warn("Cant close existing Socket: ", new FDZNetworkException(e));
                         }
                         //Reconnect if connection lost to Adapter
                         setConnection(false);
                         while (!client.isConnected()&&isRunning) {
                             LoggerInstance.log.info("Trying to reconnect to Adapter: {}:{} ", ipAddr.getAddress(),port);
                             try {
                                 LoggerInstance.log.debug("Close existing Socket");
                                 client.closeSocket();
                                 LoggerInstance.log.debug("Trying to connect to Adapter: {}:{}",ipAddr.getAddress(),port);
                                 client.openConnection();
                             } catch (FDZNetworkException e1) {
                                 LoggerInstance.log.warn("Cant open connection to Adapter: ", new FDZNetworkException(e));

                             }
                         }
                         if(isRunning){
                             setConnection(true);
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
    public void close (){
        if (client!=null){
            this.client.finalize();
        }
    }

    public void sendMessage (String message){
        this.messageOutgoing=message;
        final Thread sendMsg = new Thread (){
            @Override
            public void run() {
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
                    //Reconnect if connection lost to Adapter
                    while (!client.isConnected()&&isRunning) {
                        LoggerInstance.log.info("Trying to reconnect to Adapter: {}:{} ", ipAddr.getAddress(),port);
                        try {
                            LoggerInstance.log.debug("Close existing Socket");
                            client.closeSocket();
                            LoggerInstance.log.debug("Trying to connect to Adapter: {}:{}",ipAddr.getAddress(),port);
                            client.openConnection();
                            LoggerInstance.log.debug("Resending Message: {}",messageOutgoing);
                            client.sendMessage(messageOutgoing);
                        } catch (FDZNetworkException e1) {
                            LoggerInstance.log.warn("Cant open connection to Adapter: ", new FDZNetworkException(e1));

                        }
                    }
                    if(isRunning){
                        setConnection(true);
                    }
                }
            }
        };
        sendMsg.start();
    }

    private void setConnection (boolean connection){
        this.connection=connection;
        setChanged();
    }

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
     * Thread will be stopped
     */
    public void setIsRunning (boolean isRunning){
        if(receiveMsg.isAlive()){
            receiveMsg.interrupt();
        }
        this.isRunning=isRunning;
    }

}
