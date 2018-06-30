package Model.Status;

import java.util.HashSet;

/**
 * @author Andreas Glaser
 */
public class StatusObservable {
    private static final StatusObservable instance = new StatusObservable();
    private String value = "";

    private final HashSet<StatusObserver> observers;
    public static StatusObservable getInstance(){
        return instance;
    }
    private StatusObservable(){
        observers = new HashSet<>(2);
    }

    private void setChanged(){
        if(!observers.isEmpty()){
            observers.forEach(StatusObserver::updateStatus);
        }
    }

    public void setValue(String value){
        this.value = value;
        setChanged();
    }
    public String getValue(){
        return value;
    }

    public void addObserver(StatusObserver observer){
        observers.add(observer);
        observer.updateStatus();
    }

}
