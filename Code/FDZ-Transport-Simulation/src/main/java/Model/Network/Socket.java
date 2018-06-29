package Model.Network;

import Model.Exception.FDZNetworkException;

/**
 * @author Dzianis Brysiuk
 */
interface Socket {

    /**
     *This function receives a message from the network.
     * It blocks until a message arrived or an error occurs.
     * @return The Message that was received.
     * @throws FDZNetworkException if an error occurs.
     */
     String receiveMessage() throws FDZNetworkException;

    /**
     * This function sends a message to the network.
     * @param message The message to be sent.
     * @throws FDZNetworkException if an error occurs.
     */
    void sendMessage(String message);

    /**
     * This function checks if the socket is connected.
     * @return true if it is connected, otherwise false
     */
    boolean isConnected();

}
