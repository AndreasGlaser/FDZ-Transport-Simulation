package Model.Command;

import java.util.HashSet;

/**@author nlehmann*/

public class ShutdownTransport extends Command {

    private static HashSet<ShutdownObserver> observers = new HashSet<>(2);


    /**
     * @param msgID message ID of message that initiated the Command
     */
    ShutdownTransport(String msgID){
        super.msgID = msgID;
    }

    @Override
    public void execute(){
        System.out.println("\t log: shutting down");
        ShutdownTransport.observers.forEach(shutdownObserver -> shutdownObserver.shutdown());
        super.commandExecuted();
    }

    public static void addObserver(ShutdownObserver observer){
        observers.add(observer);
    }
}