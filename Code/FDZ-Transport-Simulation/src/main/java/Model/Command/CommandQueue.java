package Model.Command;

import Model.Logger.LoggerInstance;
import java.util.LinkedList;

/**
 * @author Dzianis Brysiuk
 */
public class CommandQueue {

    //Queue for saved Commands
    private final LinkedList<Command> commandQueue;
    //List save MessageID that activated not in queue
    private final LinkedList<String> activatedList;

    //Thread safe Singleton
    private static final CommandQueue ourInstance = new CommandQueue();

    /**
     * Add Command Object to Queue on the end
     * @param command Object Command
     */
    private void enqueue (Command command){
        commandQueue.addLast(command);
    }

    /**
     * Add Command Object to Queue on some Position
     * @param pos position in Queue
     * @param command Object Command
     */
    private void insert (int pos, Command command){
        commandQueue.add(pos, command);

    }

    /**
     * Remove and start first Command Object and proof if there Commands in waiting List
     */
    private void dequeue (){
        Command command = commandQueue.pollFirst();
        if (command!=null){
            command.execute();
        }else{
            LoggerInstance.log.warn("No command to execute available");
        }
        //check if already some commands has been activated
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
     * @param command Object command
     */
    public void add (Command command){
        validate(command);
    }

    /**
     * Find right place where to Add the Command Object in Queue
     * @param command Object Command
     */
    private void validate (Command command) {
        if (commandQueue.isEmpty()){
            enqueue(command);
        }else {
            try {
                //compare last command message ID with new Command message ID
                switch (commandQueue.peekLast().msgID.compareTo(command.msgID)) {
                    //Last command message ID is greater than new command message ID
                    case 1:
                        //find the place in queue where to add the command
                        findPos(command);
                        break;
                    //Last command message ID is smaller than new command message ID
                    case -1:
                        //Add new Command on the end of queue
                        enqueue(command);
                        break;
                    default:
                        break;
                }
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Find the position where to add the Command in Queue if the Message ID is greater than last in Queue
     * @param command Object Command
     */
    private void findPos (Command command){
        int msgIDisSmaller = -1;
        for (int i = 0; i<=commandQueue.size(); i++){
            //find index where command message ID in queue smaller than new command message ID
            if (command.msgID.compareTo(commandQueue.get(i).msgID)==-msgIDisSmaller){
                //add new command in queue on index i
                insert (i, command);
                break;
            }

        }
    }

    /**
     * If activated command not on first place in queue, add the message ID of Command in Activated command list
     * @param msgID Message ID
     */
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
     * @param msgID Message ID
     */
    public void activate (String msgID){
        try {
            if (!commandQueue.isEmpty()) {
                //Message ID is same as first message ID in queue
                if (top().compareTo(msgID) == 0) {
                    dequeue();
                //Message ID is not same as the first message ID in queue
                } else {
                    findPos(msgID);
                }
            }
        }catch (NullPointerException e){
            System.err.println("COMMAND NOT IN QUEUE, NULL POINTER");
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

