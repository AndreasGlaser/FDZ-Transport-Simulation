import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class Network implements FDZSocket
{
    //Number of bytes in the packet header
    final static int HEADER_LENGTH = 25;
    //Position of payload-length in packet header
    final static int PAYLOAD_LENGTH_OFFSET = 21;
    final static int PAYLOAD_LENGTH_SIZE = 4;

    //The address used to connect; Contains IP and Port information
    SocketAddress mSocketAddress;

    //The Server and Client socket
    private SocketChannel mClientSocket;
    private ServerSocketChannel mServerSocket;

    //List for registered Listeners
    private EventListenerList mEventList = new EventListenerList();

    //Connection-State for the Change-Events
    private boolean mState = false;

    /**
     * Initializes the Sockets; The server-address will be set to localhost.
     * @param port The port to listen on / to connect to.
     */
    protected Network(int port)
    {
        try
        {
            //Set the IP to localhost
            mSocketAddress = new InetSocketAddress(InetAddress.getLocalHost(), port);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Initializes the Sockets.
     * @param aNetworkAddress The address identifying the remote machine. To init a server use localhost.
     * @param port The port to listen on / to connect to.
     */
    protected Network(InetAddress aNetworkAddress, int port)
    {
        mSocketAddress = new InetSocketAddress(aNetworkAddress, port);
    }

    /**
     * Closes the socket that is used for communication (ClientSocket).
     * @throws FDZNetworkException if an error occurs.
     */
    public synchronized void closeSocket() throws FDZNetworkException
    {
        if (mClientSocket != null)
        {
            try
            {
                mClientSocket.close();
            }
            catch (IOException e)
            {
                throw new FDZNetworkException(e);
            }
            finally
            {
                mClientSocket = null;
                //Inform the Listeners
                triggerStateChange();
            }
        }
    }

    /**
     * Closes the server-socket associated with the server.
     * @throws FDZNetworkException if an error occurs.
     */
    public synchronized void closeServerSocket() throws FDZNetworkException
    {
        if (mServerSocket != null)
        {
            try
            {
                mServerSocket.close();
            }
            catch(IOException e)
            {
                throw new FDZNetworkException(e);
            }
            finally
            {
                mServerSocket = null;
                //Inform the Listeners
                triggerStateChange();
            }
        }
    }

    /**
     * Initializes the server-socket.
     * @throws FDZNetworkException if the server is still connected or an error occurs.
     */
    protected synchronized void initServerSocket() throws FDZNetworkException
    {
        if (mServerSocket != null)
        {
            throw new RuntimeException("ServerSocket still connected upon initServerSocket. Did you forget to call \"closeServerSocket()\"");
        }
        try
        {
            //Open a new ServerSocket and bind it to the IP and Port information.
            mServerSocket = ServerSocketChannel.open();
            //Blocking-Mode true: Waiting for Connections will block until a connection is established.
            mServerSocket.configureBlocking(true);
            mServerSocket.bind(mSocketAddress);
        }
        catch(IOException e)
        {
            throw new FDZNetworkException(e);
        }
    }

    /**
     * Awaits a connection form a client to the server-socket.
     * @throws FDZNetworkException if a client is already connected, the server-socked is not initialized or an error occurs.
     */
    protected synchronized void awaitConnection() throws FDZNetworkException
    {
        if (mClientSocket != null)
        {
            throw new RuntimeException("Still connected upon awaitConnection. Did you forget to call \"closeSocket()\"?");
        }
        if (mServerSocket == null)
        {
            throw new RuntimeException("ServerSocket not initialized upon awaitConnection.");
        }
        try
        {
            //Wait for a Client-Connection
            mClientSocket = mServerSocket.accept();
            //mClientSocket.socket().setSoTimeout(2000);
            triggerStateChange();
        }
        catch(IOException e)
        {
            throw new FDZNetworkException(e);
        }
    }

    /**
     * Tries to connect to the server.
     * @throws FDZNetworkException if an error occurs.
     */
    protected synchronized void openConnection() throws FDZNetworkException
    {
        if (mClientSocket == null)
        {
            connect();
        }
    }

    private synchronized void connect() throws FDZNetworkException
    {
        try
        {
            //Connect to a Server with the given IP and Port
            mClientSocket = SocketChannel.open();
            mClientSocket.configureBlocking(true);
            //Aus Transportcode Network.java
            //mClientSocket.socket().setSoTimeout(2000);
            mClientSocket.connect(mSocketAddress);
            triggerStateChange();
        }
        catch (IOException e)
        {
            throw new FDZNetworkException(e);
        }
    }

    /** Receives a message from the network. Reads header and payload and returns one string.
     *
     */
    @Override
    public String receive() throws FDZNetworkException
    {
        if (mClientSocket == null)
        {
            throw new RuntimeException("Not connected upon receive. Did you forget to call \"openConnection()\" or \"awaitConnection()\"?");
        }
        try
        {
            //read Header
            ByteBuffer header = ByteBuffer.allocate(HEADER_LENGTH);

            //Need to do it in while because the first time the complete message wont be received.
            while(header.hasRemaining())
            {
                //Check if the connection is closed
                if (mClientSocket.read(header) == -1)
                {
                    throw new IOException("End-of-Stream while read()2");
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
            while(payload.hasRemaining())
            {
                //Check if the connection is closed
                if (mClientSocket.read(payload) == -1)
                {
                    throw new IOException("End-of-Stream while read()1");
                }
            }

            //Add header and payload
            return new String(values, StandardCharsets.US_ASCII) + new String(payload.array(), StandardCharsets.US_ASCII);
        }
        catch(IOException e)
        {
            triggerStateChange();
            throw new FDZNetworkException(e);
        }
    }

    /** Sends a message to the network. The string aMessage is encoded as ASCII
     *
     */
    @Override
    public void send(String aMessage) throws FDZNetworkException
    {
        if (mClientSocket == null)
        {
            throw new RuntimeException("Not connected upon receive. Did you forget to call \"openConnection()\" or \"awaitConnection()\"?");
        }

        try
        {
            //Convert aMessage to a byte-array using encoding US_ASCII (1 byte per character)
            byte[] toSend = aMessage.getBytes(StandardCharsets.US_ASCII);
            ByteBuffer buffer = ByteBuffer.allocate(toSend.length);
            buffer.put(toSend);

            //This need to be done
            buffer.flip();
            while(buffer.hasRemaining())
            {
                mClientSocket.write(buffer);
            }
        }
        catch(IOException e)
        {
            triggerStateChange();
            throw new FDZNetworkException(e);
        }
    }

    @Override
    public void finalize()
    {
        try
        {
            closeServerSocket();
        }
        catch(FDZNetworkException e)
        {
            e.printStackTrace();
        }
        try
        {
            closeSocket();
        }
        catch(FDZNetworkException e)
        {
            e.printStackTrace();
        }

        try
        {
            super.finalize();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add a ChangeListener.
     * @param aChangeListener to be added.
     */
    public synchronized void addListener(ChangeListener aChangeListener)
    {
        mEventList.add(ChangeListener.class, aChangeListener);
    }

    private synchronized void triggerStateChange()
    {
        if (mState != isConnected())
        {
            mState = isConnected();
            ChangeEvent changeEvt = new ChangeEvent(this);
            for (ChangeListener listener : mEventList.getListeners(ChangeListener.class))
            {
                listener.stateChanged(changeEvt);
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isConnected()
    {
        return mClientSocket != null && mClientSocket.isConnected();
    }

}
