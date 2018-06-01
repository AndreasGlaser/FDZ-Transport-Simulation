package Model.Command;

/**
 * @author Dzinais Brysiuk
 * @author Noah Lehmann
 */
public abstract class Command {

    protected String msgID;

    public abstract void execute();

    private void commandUnderstood(){

    }
    private void error(){

    }
}
