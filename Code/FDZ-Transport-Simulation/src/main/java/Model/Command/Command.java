package Model.Command;

import Model.Exception.IllegalSetupException;

import java.util.HashSet;

import static Model.Network.NetworkController.getInstance;

/**
 * @author nlehmann
 */
abstract class Command {

    protected String msgID;

    /**
     * Method to be Overridden in subclasses, executes their Commands
     * @throws IllegalSetupException if illegal setup is detected in method
     */
    public abstract void execute();

    /**
     * Signals the Network to send the commandExecuted acknowledgment
     */
    protected void commandExecuted(){
        getInstance().acknowledge2(msgID);
    }

    /**
     * Signals the Network to send the commandNotExecuted error
     */
    protected void error(){
        getInstance().commandNotExecuted(msgID);
    }
}
