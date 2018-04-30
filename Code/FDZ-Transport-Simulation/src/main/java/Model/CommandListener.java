package Model;

/*--NOAH LEHMANN-------------------------------------------------------------*/

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
    }


    private void invokeInterpreter(String command){
        try{
            new CommandInterpreter(command, stationList).run();
        }catch(IllegalCommandException e){
            System.out.println(e.getMessage());
        }
    }
}

