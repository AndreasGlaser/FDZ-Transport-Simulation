package Model.Command;

import java.util.LinkedList;

/**
 * @author Dzianis Brysiuk
 */
public class CommandQueue {

    //Queue for saved Commands
    private LinkedList<Command> commandQueue;
    //List save MessageID that activated not in queue
    private LinkedList<String> activatedList;

    private static CommandQueue ourInstance = new CommandQueue();

    private void enqueue (Command command){
        commandQueue.addLast(command);
    }

    private void insert (int pos, Command command){
        commandQueue.add(pos, command);

    }

    private void dequeue (){
        commandQueue.pollFirst().execute();
        if (!activatedList.isEmpty()){
            for (int i=0; i<=activatedList.size();i++){
                if (activatedList.get(i).compareTo(commandQueue.peekFirst().msgID)==0){
                    dequeue();
                }
            }
        }

    }

    private String top (){
        return commandQueue.peekFirst().msgID;
    }

    public void add (Command command){
        validate(command);
    }

    private void validate (Command command) {
        if (commandQueue.isEmpty()){
            enqueue(command);
        }else {

                switch (commandQueue.peekLast().msgID.compareTo(command.msgID)){
                    case 1:     findPos(command);
                                break;
                    case -1:    enqueue(command);
                                break;
                    default:    break;
                }
        }
    }

    private void findPos (Command command){
        for (int i = 0; i<=commandQueue.size(); i++){
            if (command.msgID.compareTo(commandQueue.get(i).msgID)==-1){
                insert (i, command);
                break;
            }

        }
    }

    private void findPos (String msgID){
        for (int i = 0; i<=commandQueue.size(); i++){
            if (msgID.compareTo(commandQueue.get(i).msgID)==0){
                activatedList.addLast (msgID);
                break;
            }

        }
    }

    public void activate (String mesID){
        if (!commandQueue.isEmpty()){
            if (top().compareTo(mesID)==0){
                dequeue();
            }else {
                findPos(mesID);
            }
        }

    }



    public static CommandQueue getInstance() {
        return ourInstance;
    }

    private CommandQueue() {
        commandQueue = new LinkedList<>();
        activatedList = new LinkedList<>();
    }
}
