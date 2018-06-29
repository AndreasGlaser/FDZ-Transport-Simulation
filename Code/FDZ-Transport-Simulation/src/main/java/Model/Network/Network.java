package Model.Network;

import Model.Exception.FDZNetworkException;
import Model.Logger.LoggerInstance;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author Dzianis Brysiuk
 */
public class Network implements Socket {

    //Number of bytes in the packet header
    private final static int HEADER_LENGTH = 25;

    //Position of payload-length in packet header
    private final static int PAYLOAD_LENGTH_OFFSET = 21;
    private final static int PAYLOAD_LENGTH_SIZE = 4;

    //The address used to connect; Contains IP and Port information
    private SocketAddress socketAddr;

    //Client socket
    private SocketChannel clientSocket;

    /**
     * Initializes the Sockets.
     * @param ipAddr contains IP Address from Adapter
     * @param port contains Port Number of Adapter for Connection.
     */
    Network(InetAddress ipAddr, int port) {

        socketAddr = new InetSocketAddress(ipAddr, port);

    }

    /**
     * Closes the socket that is used for communication.
     * @throws FDZNetworkException if an error occurs.
     */
    public synchronized void closeSocket() throws FDZNetworkException {

        if (clientSocket != null) {

            try {
                clientSocket.close();
            }
            catch (IOException e) {
                throw new FDZNetworkException(e);
            }
            finally {
                clientSocket = null;
            }
        }
    }

    /**
     * Tries to connect to the Adapter.
     * @throws FDZNetworkException if an error occurs.
     */
    synchronized void openConnection() throws FDZNetworkException {

        if (clientSocket == null) {
            connect();
        }
    }

    private synchronized void connect() throws FDZNetworkException {
        try {
            //Connect to a Server with the given IP and Port
            clientSocket = SocketChannel.open();
            clientSocket.configureBlocking(true);
            clientSocket.connect(socketAddr);
        }
        catch (IOException e) {
            throw new FDZNetworkException(e);
        }
    }

    /**
     *Receives a message from the Adapter. Reads header and payload and returns one string.
     */
    @Override
    public String receiveMessage() throws FDZNetworkException {

        if (clientSocket == null) {
            LoggerInstance.log.warn("Cant receive message: ",new RuntimeException("Not connected upon receive"));
        }

        try {
            //read Header
            ByteBuffer header = ByteBuffer.allocate(HEADER_LENGTH);

            //Need to do it in while because the first time the complete message wont be received.
            while(header.hasRemaining()) {
                //Check if the connection is closed
                if (clientSocket.read(header) == -1) {
                    LoggerInstance.log.warn("End of Stream while read(): ", new IOException());
                }
            }

            byte[] values = header.array();

            //Get the length of payload
            //Length has PAYLOAD_LENGTH_SIZE digits starting at position PAYLOAD_LENGTH_OFFSET in header
            //Convert the bytes to a String using US_ASCII encoding
            String length = new String(values, PAYLOAD_LENGTH_OFFSET, PAYLOAD_LENGTH_SIZE, StandardCharsets.US_ASCII);
            int payloadLength = Integer.parseInt(length);

            //Read payload
            ByteBuffer payload = ByteBuffer.allocate(payloadLength);
            while(payload.hasRemaining()) {
                //Check if the connection is closed
                if (clientSocket.read(payload) == -1) {
                    LoggerInstance.log.warn("End of Stream while read(): ", new IOException());
                }
            }

            //Add header and payload
            return new String(values, StandardCharsets.US_ASCII) + new String(payload.array(), StandardCharsets.US_ASCII);
        }
        catch(IOException e) {
            throw new FDZNetworkException(e);
        }
    }

    /**
     *Sends a message to the Adapter. The string message is encoded as ASCII
     */
    @Override
    public void sendMessage(String message) {
        if (clientSocket == null) {
            LoggerInstance.log.warn("Cant receive message: ",new RuntimeException("Not connected upon receive"));
        }

        try {
            //Convert message to a byte-array using encoding US_ASCII (1 byte per character)
            byte[] toSend = message.getBytes(StandardCharsets.US_ASCII);
            ByteBuffer buffer = ByteBuffer.allocate(toSend.length);
            buffer.put(toSend);

            buffer.flip();
            while(buffer.hasRemaining()) {
                clientSocket.write(buffer);
            }
        }
        catch(IOException e) {
            LoggerInstance.log.warn("Message convert fails: ",new FDZNetworkException(e));
        }
    }

    @Override
    public void finalize() {

        try {
            closeSocket();
        }
        catch(FDZNetworkException e) {
            e.printStackTrace();
        }

        try {
            super.finalize();
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**Status of Network connection to Adapter
     * @return true or false
     */
    @Override
    public boolean isConnected() {

        return clientSocket != null && clientSocket.isConnected();

    }


    /*-----------SET NEW IP AND PORT -------------------------------------------------*/
    public void setSocketAddr (InetAddress ipAddr, int port){
        socketAddr = new InetSocketAddress(ipAddr, port);
    }

}
