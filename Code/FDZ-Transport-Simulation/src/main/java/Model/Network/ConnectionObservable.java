package Model.Network;

import java.util.HashSet;

/**
 * @author nlehmann
 */
public abstract class ConnectionObservable {

    private HashSet<ConnectionObserver> observers;

    public ConnectionObservable(){
        observers = new HashSet<>(2);
    }

    protected void setChanged(){
        if(!observers.isEmpty()){
            observers.forEach(observer -> observer.update());
        }
    }

    public void addObserver(ConnectionObserver observer){
        observers.add(observer);
        observer.update();
    }

}
