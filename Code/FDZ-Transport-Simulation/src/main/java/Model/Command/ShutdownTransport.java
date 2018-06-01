package Model.Command;

/**@author Noah Lehmann*/

public class ShutdownTransport extends Command {

    public ShutdownTransport(String msgID){
        super.msgID = msgID;
    }

    /**
     * The Method, which executes the Command SHUTDOWN_TRANSPORT
     */
    //@Override
    public void execute(){
        System.out.println("\t log: shutting down");
        /*TODO*/
    }
}