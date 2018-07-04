package Model.Command;

import static Model.Network.NetworkController.getInstance;

/**
 * @author nlehmann
 */
public abstract class Command {

    String msgID;
    private boolean ack1Success = false;

    /**
     * Method to be Overridden in subclasses, executes their Commands
     */
    public abstract void execute();

    /**
     * Signals the Network to send the commandExecuted acknowledgment
     */
    void commandExecuted(){
        getInstance().acknowledge2(msgID);
    }

    /**
     * Signals the Network to send the commandNotExecuted error
     */
    void error(){
        getInstance().commandNotExecuted(msgID);
    }

    /**
     * Getter for Ack1Success flag
     * @return true, if Ack1 was successful
     */
    public boolean getAck1Success(){
        return ack1Success;
    }

    /**
     * Sets the Ack1 Flag to true
     */
    public void confirmAck1Success(){
        ack1Success=true;
    }
}
