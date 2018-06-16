package Model.Exception;

import java.io.IOException;

/**
 * @author Dzianis Brysiuk
 */
public class FDZNetworkException extends IOException {

    /**
     * Initializes a FDZNetworkException
     * @param e
     */
    public FDZNetworkException(IOException e) {

        super(e);
    }

}
