package Model;

/**This interface describes, how to interact with the FDZSocket;
 *
 * @author therpich
 */

public interface FDZSocket {
    /**
     *This function receives a message from the network.
     * It blocks until a message arrived or an error occurs.
     * @return The Message that was received.
     * @throws FDZNetworkException if an error occurs.
     */
    public String receive() throws FDZNetworkException;

    /**
     * This function sends a message to the network.
     * @param aMessage The message to be sent.
     * @throws FDZNetworkException if an error occurs.
     */
    public void send(String aMessage) throws FDZNetworkException;

    /**
     * This function checks if the socket is connected.
     * @return true if it is connected, otherwise false
     */
    public boolean isConnected();
}
