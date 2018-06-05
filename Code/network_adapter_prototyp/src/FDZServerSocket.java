/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.InetAddress;
import javax.swing.event.ChangeListener;

/**
 *
 * @author therpich
 */
public class FDZServerSocket extends Network
{

    /**
     * Initializes a ServerSocket.
     * @param aServerPort The port to listen on.
     * @param aChangeListener is informed, if the state of the socket changes.
     */
    public FDZServerSocket(int aServerPort, ChangeListener aChangeListener)
    {
        this(aServerPort);
        super.addListener(aChangeListener);
    }

    /**
     * Initializes a serverSocket.
     * @param aServerPort The port to listen on.
     */
    public FDZServerSocket(int aServerPort)
    {
        super(aServerPort);

        try
        {
            super.initServerSocket();
        }
        catch(FDZNetworkException e)
        {
            System.err.println("Could not init ServerSocket");
            e.printStackTrace();
        }
    }

    public FDZServerSocket(InetAddress address, int port)
    {
        super(address, port);
        try
        {
            super.initServerSocket();
        }
        catch(FDZNetworkException e)
        {
            System.err.println("Could not init ServerSocket");
            e.printStackTrace();
        }
    }

    /**
     *@see fdzNetwork.FDZSocket#receive()
     */
    @Override
    public String receive() throws FDZNetworkException
    {
        //Wait until a client connected
        if (!isConnected())
        {
            awaitConnection();
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
        //Wait until a client connected
        if (!isConnected())
        {
            awaitConnection();
        }
        try
        {
            super.send(aMessage);
        }
        catch(FDZNetworkException e)
        {
            closeSocket();
            awaitConnection();
            try
            {
                super.send(aMessage);
            }
            catch(FDZNetworkException ex)
            {
                throw ex;
            }
        }
    }

    @Override
    public void awaitConnection() throws FDZNetworkException
    {
        super.awaitConnection();
    }
}
