package Model.Command;

import Model.Logger.LoggerInstance;
import Model.Network.NetworkController;

import java.util.LinkedList;

/**
 * @author Dzianis Brysiuk
 */
public class CommandQueue extends SaveObservable{

    //Queue for saved Commands
    private LinkedList<Command> commandQueue;
    //List for Commands to be validated
    private Command toBeValidated;
    //List delete MessageID that activated not in queue
    private LinkedList<String> activatedList;

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
        Command command = commandQueue.peekFirst();
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
    public synchronized void add (Command command){
        toBeValidated = command;
        notifyObservers();
        validate(command);
        notifyObservers();
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
                Integer compareResult = commandQueue.peekLast().msgID.compareTo(command.msgID);
                if(compareResult > 0) {
                    //Last command message ID is greater than new command message ID
                    //find the place in queue where to add the command
                    findPos(command);
                }else if(compareResult < 0) {
                    //Last command message ID is smaller than new command message ID
                    enqueue(command);

                }else {
                    LoggerInstance.log.warn("Command with id: "+ command.msgID + " already received");
                }
            }catch(NullPointerException e){
                LoggerInstance.log.warn("COMMAND NOT IN QUEUE ");
            }
        }
        toBeValidated = null;
    }

    /**
     * Find the position where to add the Command in Queue if the Message ID is greater than last in Queue
     * @param command Object Command
     */
    private void findPos (Command command){
        int msgIDisSmaller = -1;
        for (int i = 0; i<=commandQueue.size(); i++){
            //find index where command message ID in queue smaller than new command message ID
            if (command.msgID.compareTo(commandQueue.get(i).msgID)<=-msgIDisSmaller){
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
                }else if(commandQueue.peekFirst().getActivated()){
                    int cnt=0;
                    while(cnt < commandQueue.size() && commandQueue.get(cnt++).getActivated());
                    if (commandQueue.get(cnt-1).msgID.compareTo(msgID)==0){
                        commandQueue.get(cnt-1).execute();
                    }
                }else {
                    findPos(msgID);
                }
            }
        }catch (NullPointerException e){
            LoggerInstance.log.warn("COMMAND NOT IN QUEUE or PRE ACTIVATED COMMAND WAS NULL");
        }

    }

    /**
     * Sets the CommandQueue to its state before saving
     * @param queue LinkedList which was read from getCommandQueue
     * @param toBeValidated Command which was read from getToBeValidated
     * @param activated LinkedList which was read from getActivatedList
     */
    public void setQueueContent(LinkedList<Command> queue, Command toBeValidated, LinkedList<String> activated){
        this.commandQueue = queue;
        this.toBeValidated = toBeValidated;
        this.activatedList = activated;
        /*checks if a command was to be validated a system breakdown*/
        if(toBeValidated != null){
            this.add(toBeValidated);
        }
        try{
            if(commandQueue.peekFirst().getAck1Success()){
                activate(commandQueue.peekFirst().msgID);
            }
        }catch(NullPointerException e){
            LoggerInstance.log.debug("EmptyCommandQueue");
        }
        for (Command command : commandQueue) {
            new Thread(()->{
                if(command.getAck1Success()){
                    activate(command.msgID);
                }else{
                    NetworkController.getInstance().acknowledge1(command.msgID);
                    activate(command.msgID);
                }
            }).start();
        }
    }

    void delete(Command command){
        commandQueue.remove(command);
        notifyObservers();
    }

    void save(){
        notifyObservers();
    }

    public LinkedList<Command> getCommandQueue(){return this.commandQueue;}
    public LinkedList<String> getActivatedList(){return activatedList;}
    public Command getToBeValidated(){return toBeValidated;}

    public static CommandQueue getInstance() {
        return ourInstance;
    }

    private CommandQueue() {
        commandQueue = new LinkedList<>();
        toBeValidated = null;
        activatedList = new LinkedList<>();
    }
}

