package Model;

public class FDZClientSocket extends Network{
    /**
     * Initializes a ClientSocket.
     * @param aServerAddress Address of the server to connect.
     * @param aServerPort Port of the server to connect.
     * @param aChangeListener is informed, of the state if the socket changes.
     */
    public FDZClientSocket(InetAddress aServerAddress, int aServerPort, ChangeListener aChangeListener)
    {
        this(aServerAddress, aServerPort);
        super.addListener(aChangeListener);
    }

    /**
     * Initializes a ClientSocket.
     * @param aSerAddress Address of the server to connect.
     * @param aServerPort iPort of the server to connect.
     */
    public FDZClientSocket(InetAddress aSerAddress, int aServerPort)
    {
        super(aSerAddress, aServerPort);
    }

    /**
     *@see fdzNetwork.FDZSocket#receive()
     */
    @Override
    public String receive() throws FDZNetworkException
    {
        if (!isConnected())
        {
            this.openConnection();
        }
        String result = null;
        try
        {
            result = super.receive();
        }
        catch(FDZNetworkException e)
        {
            throw e;
        }
        return result;
    }

    /**
     *@see fdzNetwork.FDZSocket#send(java.lang.String)
     */
    @Override
    public void send(String aMessage) throws FDZNetworkException
    {
        if (!isConnected())
        {
            this.openConnection();
        }
        try
        {
            super.send(aMessage);
        }
        catch(FDZNetworkException e)
        {
            //If the first try failed, try a second time
            try
            {
                closeSocket();
                openConnection();
                super.send(aMessage);
            }
            catch(FDZNetworkException ex)
            {
                throw ex;
            }
        }
    }

    /**
     *@see fdzNetwork.Network#openConnection()
     */
    @Override
    public void openConnection() throws FDZNetworkException
    {
        try
        {
            super.openConnection();
        }
        catch(FDZNetworkException e)
        {
            System.err.println("Could not connect");
            throw e;
        }
    }
}
