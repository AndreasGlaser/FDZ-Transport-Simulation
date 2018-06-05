package Model.Command;

import Model.IllegalSetupException;

import java.sql.SQLOutput;
import java.util.LinkedList;

/**
 * @author Dzianis Brysiuk
 */
public class CommandQueue {

    //Queue for saved Commands
    private LinkedList<Command> commandQueue;
    //List save MessageID that activated not in queue
    private LinkedList<String> activatedList;

    //Thread safe Singleton
    private static CommandQueue ourInstance = new CommandQueue();

    /**
     * Add Command Object to Queue on the end
     * @param command
     */
    private void enqueue (Command command){
        commandQueue.addLast(command);
    }

    /**
     * Add Command Object to Queue on some Position
     * @param pos position in Queue
     * @param command
     */
    private void insert (int pos, Command command){
        commandQueue.add(pos, command);

    }

    /**
     * Remove and start first Command Object and proof if there Commands in waiting List
     */
    private void dequeue (){
        try{
            commandQueue.pollFirst().execute();
        }catch(IllegalSetupException e){
            System.err.println("WRONG SETUP!!!\n"+e.getMessage());
        }
        if (!activatedList.isEmpty()){
            for (int i=0; i<=activatedList.size();i++){
                if (activatedList.get(i).compareTo(commandQueue.peekFirst().msgID)==0){
                    dequeue();
                }
            }
        }

    }

    /**
     * Returns of fist Command in Queue the Message ID
     * @return Message ID String
     */
    private String top (){
        return commandQueue.peekFirst().msgID;
    }
    /**
     * Adding Command to Queue
     * @param command Object
     */
    public void add (Command command){
        validate(command);
    }

    /**
     * Validate where to Add the Command Object in Queue
     * @param command Object
     */
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

    /**
     * Find the position where to add the Command in Queue if the Message ID is greater than last in Queue
     * @param command
     */
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

    /**
     * Activate the Command Object from Queue with specific Message ID
     * @param msgID Message ID String
     */
    public void activate (String msgID){
        try {
            if (!commandQueue.isEmpty()) {
                if (top().compareTo(msgID) == 0) {
                    dequeue();
                } else {
                    findPos(msgID);
                }
            }
        }catch (NullPointerException e){
            System.err.println("COMMAND NOT IN QUEUE, NULLPOINTER");
            System.err.println("INFO :: PRE ACTIVATED COMMANDS WAS NULL");
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
