package Model.Command;

public class RequestEmptyCarriage extends Command {

    private String position;

    public RequestEmptyCarriage(String position, String msgID){
        this.position = position;
        .msgID = msgID;
    }

    //@Override
    public void execute(){

    }

}
