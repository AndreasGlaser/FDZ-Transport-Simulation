package Model;

import java.util.ArrayList;
import java.util.Scanner;

public class CommandListener {

    private ArrayList<Station> stationList;

    public CommandListener(ArrayList<Station> currentStationList){
        this.stationList = currentStationList;
    }

    /* TODO
     *  -Netzwerkanbindung
     */

    /*--TESTMETHODEN-------------------------------------------------------------*/
    public void testCommand(String command) {
        invokeInterpreter(command);
        System.out.println("\t log: "+"testing command >>"+command+"<<");
    }


    private void invokeInterpreter(String command){
        new CommandInterpreter(command, stationList).run();
        System.out.println("\t log: "+"invoked Interpreter");
    }
}

