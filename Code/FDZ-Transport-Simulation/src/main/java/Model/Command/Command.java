package Model.Command;

import Model.Network.NetworkController;

/**
 * @author Dzinais Brysiuk
 * @author Noah Lehmann
 */
public abstract class Command {

    protected String msgID;

    public abstract void execute();

    private void commandUnderstood(){
        NetworkController.getInstance().acknowledge2(msgID);
    }
    private void error(){
        NetworkController.getInstance().commandNotExecuted(msgID);
    }
}
