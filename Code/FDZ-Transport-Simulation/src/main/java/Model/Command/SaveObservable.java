package Model.Command;

import java.util.ArrayList;

public abstract class SaveObservable {

    private static ArrayList<SaveObserver> observers = new ArrayList<>(2);

    protected void notifyObservers(){
        observers.forEach(saveObserver -> saveObserver.save());
    }

    public static void addObserver(SaveObserver observer){
        observers.add(observer);
    }

}
