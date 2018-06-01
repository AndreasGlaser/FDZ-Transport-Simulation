package Model.Command;

public class RequestEmptyCarriage extends Command {

    private String position, msgID;

    public RequestEmptyCarriage(String position, String msgID){
        this.position = position;
        this.msgID = msgID;
    }

    //@Override
    public void execute(){

    }

}
