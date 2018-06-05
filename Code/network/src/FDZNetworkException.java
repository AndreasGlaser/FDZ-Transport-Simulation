

import java.io.IOException;

public class FDZNetworkException extends IOException
{

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
