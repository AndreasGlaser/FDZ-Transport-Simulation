package Model.Command;

public abstract class Command {

    protected String msgID;

    public abstract void execute();

    private void commandUnderstood(){

    }
    private void error(){

    }
}
