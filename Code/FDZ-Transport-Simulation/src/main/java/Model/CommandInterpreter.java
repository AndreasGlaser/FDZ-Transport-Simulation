package Model;

import java.util.ArrayList;

public class CommandInterpreter extends Thread {

    private String command;
    private ArrayList<Station> stationList;

    public CommandInterpreter(String command, ArrayList<Station> currentStationList){
        this.command = command;
        this.stationList = currentStationList;
        System.out.println("\t log: "+"invocation successful");
    }


    @Override
    public void run(){
        /* TODO Interpret String-Command */
    }


}

