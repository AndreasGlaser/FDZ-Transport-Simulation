

import java.net.InetAddress;

import javax.swing.event.ChangeEvent;

import javax.swing.event.ChangeListener;





public class NetworkClient implements Runnable, Applicationconstants{

   // private FSMessageParser mParser; // current instance of class "FSMessageParser"

    private FDZClientSocket client; // current instance of class "FDZClientSocket"

    private InetAddress host; // name of the server machine

    private int port; // tcp port number

    private boolean isRunning = false; 	//flag to stop the thread, true thread can run

    //false the thread were stopped

    /**

     * Constructor creates a new NetworkClient.

     * @param host host contains the name of the server machine

     * @param port port contains the tcp port number

     * @param mParser mParser contains the current instance of class "FSMessageParser"

     */

    public NetworkClient(InetAddress host, int port) {

        this.host = host;

        this.port = port;

        //this.mParser = mParser;

    }

    /**

     * Function to send any data to the server.

     * @param data data to send to server

     */

    public void send(String data) throws FDZNetworkException {this.client.send(data);}

    /**

     * Function to close an existing network connection.

     */

    public void close() {

        if(this.client != null)

            this.client.finalize();

    }

    /**

     * Set the isRunning-flag to false, so if the thread

     * is running it will be stopped.

     */

    public void stopRunning(){

        isRunning = false;

    }

    /**

     * Function to listen for incomming data.

     */

    synchronized public void run() {

        String incomming;

        //StateTracer tracer = new StateTracer();

        //tracer.setMessageParser(this.mParser);

        this.client = new FDZClientSocket(this.host, this.port);

        this.isRunning = true;



        //try connecting to the server

        do{



            try {

               // this.mParser.sendToHistory(OUT_TRY);

                this.client.openConnection();

            } catch (FDZNetworkException e) {

                System.err.println("Error during connecting to the server: " + e);

                //FSMessageParser.sleep(5000);

            }



        }while(!client.isConnected() && isRunning);


        //if the client is connected start receiving messages

        while (isRunning) {

            try {

                incomming = this.client.receive();
                System.out.println(incomming);

               // this.mParser.messageRecieved(incomming);

            } catch (FDZNetworkException e) {

                System.err.println("Error during receiving a message: " + e);



                while(!client.isConnected() && isRunning){

                    try {

                        //this.mParser.sendToHistory(OUT_TRY);

                        this.client.closeSocket();

                        this.client.openConnection();

                    } catch (FDZNetworkException e1) {

                        System.err.println("Error during reconnecting to the server: " + e1);

                        //FSMessageParser.sleep(5000);

                    }

                }

            }

        }


        this.close();

    }

}

/**

 * <dt>Title:</dt><dd>StateTracer</dd> <dt>Description:</dt> <dd>The class checks if the connection state has been changed.</dd>

 */



