package Model;


import java.net.InetAddress;
import java.util.regex.Matcher;

public class ClientNetwork {

    //instance of class Network
    private Network client;
    //IP Adresse from Adapter
    private InetAddress ipAddr;
    //Adapter Port Nummer
    private int port;
    //flag to stop Thread
    private boolean isRunning = false;
    public String message;

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
        }else {
            client.setSocketAddr(ipAddr, port);
             try {
                 client.closeSocket();
             } catch (FDZNetworkException e) {
                 e.printStackTrace();
             }
         }

        this.isRunning=true;

        //Connect to Adapter
        do{
            try {
                this.client.openConnection();
            }catch (FDZNetworkException e){
                e.printStackTrace();
            }

        }while (!client.isConnected() && isRunning);

        final Thread out = new Thread(){

            public void run (){

                String message;
                isRunning=true;

                //wait and receive messages from Adapter
                while (isRunning) {
                    try {
                        System.out.println("1");
                        message = client.receiveMessage();
                        System.out.println("2");
                        System.out.println(message);

                        System.out.println("3");
                    } catch (FDZNetworkException e) {
                        e.printStackTrace();
                        //Reconnect if connection lost to Adapter
                        while (!client.isConnected() && isRunning) {
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
                //close();

            }
        };
        out.start();
        


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
        this.message=message;
        final Thread in = new Thread (){
            @Override
            public void run() {
                try {
                    client.sendMessage(message);
                } catch (FDZNetworkException e) {
                    e.printStackTrace();
                }
            }
        };
        in.start();
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
        this.isRunning=isRunning;
    }
    public boolean getIsRunning (){
        return this.isRunning;
    }

    public Network getNetwork () {
        return this.client;
    }
}
