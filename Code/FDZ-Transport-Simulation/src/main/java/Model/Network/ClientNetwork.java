package Model.Network;

/**
 * @author Dzianis Brysiuk
 */

import Model.CommandInterpreter;

import java.net.InetAddress;

public class ClientNetwork {

    //instance of class Network
    private Network client;
    //IP Adresse from Adapter
    private InetAddress ipAddr;
    //Adapter Port Nummer
    private int port;
    //flag to stop Thread
    private boolean isRunning = false;
    private String messageOutgoing;

    private CommandInterpreter commandInterpreter;

    /**
     * Thread is waiting for Command from connected Adapter and send receiving message to CommandInterpreter
     */
    Thread receiveMsg;

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
            client = new Network(ipAddr, port);
             this.isRunning=true;
        }else {
             client.setSocketAddr(ipAddr, port);
             try {
                 client.closeSocket();
             } catch (FDZNetworkException e) {
                 e.printStackTrace();
             }
         }

        //Connect to Adapter
        do{
            try {
                this.client.openConnection();
            }catch (FDZNetworkException e){
                e.printStackTrace();
            }

        }while (!client.isConnected() && isRunning);

         receiveMsg = new Thread (){
             public synchronized void run (){

                 String messageIncomming;
                 isRunning=true;

                 //wait and receive messages from Adapter
                 while (isRunning) {
                     try {
                         System.out.println("1");
                         messageIncomming = client.receiveMessage();
                         commandInterpreter = new CommandInterpreter(messageIncomming);

                         System.out.println("3");
                     } catch (FDZNetworkException e) {
                         e.printStackTrace();
                         try {
                             client.closeSocket();
                         } catch (FDZNetworkException e1) {
                             e1.printStackTrace();
                         }
                         //Reconnect if connection lost to Adapter
                         while (!client.isConnected()&&isRunning) {
                             try {
                                 client.closeSocket();
                                 client.openConnection();
                             } catch (FDZNetworkException e1) {
                                 e1.printStackTrace();

                             }
                         }
                     }
                     System.out.println("4");
                 }
                 close();

             }
         };
        receiveMsg.start();
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
                    e.printStackTrace();
                    try {
                        client.closeSocket();
                    } catch (FDZNetworkException e1) {
                        e1.printStackTrace();
                    }

                    //Reconnect if connection lost to Adapter
                    while (!client.isConnected()&&isRunning) {
                        try {
                            client.closeSocket();
                            client.openConnection();
                            client.sendMessage(messageOutgoing);
                        } catch (FDZNetworkException e1) {
                            e1.printStackTrace();

                        }
                    }
                }
            }
        };
        sendMsg.start();
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
        receiveMsg.interrupt();
        this.isRunning=isRunning;
        try {
            receiveMsg.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
