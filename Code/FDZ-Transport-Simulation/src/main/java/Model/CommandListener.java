package Model;

import java.util.Scanner;

public class CommandListener {

    /* TODO
     *  -Netzwerkanbindung
     */
    /*--TESTMETHODEN-------------------------------------------------------------*/
    public void testCommand(String command) {
        invokeInterpreter(command);
        System.out.println("\t log: "+"testing command >>"+command+"<<");
    }


    private void invokeInterpreter(String command){
        new CommandInterpreter(command).run();
        System.out.println("\t log: "+"invoked Interpreter");
    }
}

