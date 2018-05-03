package Model;

import java.io.IOException;

/**
 *
 * @author therpich
 */
public class FDZNetworkException {
    /**
     * Initializes a FDZNetworkException
     * @param ex
     */
    public FDZNetworkException(IOException ex)
    {
        super(ex);
    }

    /**
     * Initializes a FDZNetworkException
     * @param ex
     */
    public FDZNetworkException(Exception ex)
    {
        super(ex.getLocalizedMessage(), ex);
    }

}
