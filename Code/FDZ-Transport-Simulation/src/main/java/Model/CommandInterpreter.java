package Model;

public class CommandInterpreter extends Thread {

    private String command;

    public CommandInterpreter(String command){
        this.command = command;
        System.out.println("\t log: "+"invokation succesfull");
    }


    @Override
    public void run(){
        /* TODO Interpret String-Command*/
    }


}
