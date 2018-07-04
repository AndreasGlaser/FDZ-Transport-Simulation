package Model.Command;

import Model.Facade;
import Model.Logger.LoggerInstance;

import java.util.HashSet;

/**@author nlehmann*/

public class ShutdownTransport extends Command {

    private static final HashSet<ShutdownObserver> observers = new HashSet<>(2);


    /**
     * @param msgID message ID of message that initiated the Command
     */
    ShutdownTransport(String msgID){
        LoggerInstance.log.debug("Creating new ShutdownTransport Command");
        new Facade().setStatus("Shut down system");
        super.msgID = msgID;
    }

    @Override
    public void execute(){
        ShutdownTransport.observers.forEach(ShutdownObserver::shutdown);
        LoggerInstance.log.info("Informed all ShutdownObservers");
        super.commandExecuted();
    }

    public static void addObserver(ShutdownObserver observer){
        observers.add(observer);
    }
}