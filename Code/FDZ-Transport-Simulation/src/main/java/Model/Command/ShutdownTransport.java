package Model.Command;

/**@author nlehmann*/

public class ShutdownTransport extends Command {

    /**
     * @param msgID message ID of message that initiated the Command
     */
    ShutdownTransport(String msgID){
        super.msgID = msgID;
    }

    @Override
    public void execute(){
        System.out.println("\t log: shutting down");
        /*TODO*/
        super.commandExecuted();
    }
}