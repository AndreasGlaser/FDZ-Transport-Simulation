package Model.Network;

import java.util.HashSet;

/**
 * @author nlehmann
 */
public abstract class ConnectionObservable {

    private HashSet<ConnectionObserver> observers;

    ConnectionObservable(){
        observers = new HashSet<>(2);
    }

    void setChanged(){
        if(!observers.isEmpty()){
            observers.forEach(ConnectionObserver::update);
        }
    }

    public void addObserver(ConnectionObserver observer){
        observers.add(observer);
        observer.update();
    }

}
