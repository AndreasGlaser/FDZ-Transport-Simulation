package Model.Network;

import java.io.IOException;

/**
 * @author Dzianis Brysiuk
 */
public class FDZNetworkException extends IOException {

    /**
     * Initializes a FDZNetworkException
     * @param e
     */
    public FDZNetworkException(Exception e) {

        super(e.getLocalizedMessage(), e);
    }

    /**
     * Initializes a FDZNetworkException
     * @param e
     */
    public FDZNetworkException(IOException e) {

        super(e);
    }

}
