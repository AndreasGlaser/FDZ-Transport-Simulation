package Model.Command;

import Model.Facade;

import static Model.Network.NetworkController.getInstance;

/**
 * @author nlehmann
 */
abstract class Command {

    String msgID;

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
}
