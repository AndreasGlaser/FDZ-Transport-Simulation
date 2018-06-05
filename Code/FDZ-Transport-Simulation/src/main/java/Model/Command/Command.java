package Model.Command;

import Model.Exception.IllegalSetupException;

import static Model.Network.NetworkController.getInstance;

/**
 * @author Dzinais Brysiuk
 * @author Noah Lehmann
 */
public abstract class Command {

    protected String msgID;

    public abstract void execute() throws IllegalSetupException;

    protected void commandExecuted(){
        getInstance().acknowledge2(msgID);
    }
    protected void error(){
        getInstance().commandNotExecuted(msgID);
    }
}
