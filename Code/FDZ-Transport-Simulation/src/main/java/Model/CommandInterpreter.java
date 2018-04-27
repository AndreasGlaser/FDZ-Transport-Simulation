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
        if (command.substring(0,6).compareTo("STStK00") != 0){
            /* TODO Command not recognized */
        }else{
            char commandNum = command.substring(7,7).toCharArray()[0];
            switch(commandNum){
                case '1' : requestEmptyCarriage( stringToInteger( parseLastChars(command,2)));

                case '2' : releaseCarriage( stringToInteger( parseLastChars(command,2)));

                case '3' : repositionCarriage(
                        stringToInteger(
                                parseLastChars(command, 4).substring(0,1)) ,
                        parseLastChars(command,2));

                case '4' : shutdownTransport();

                default: /* TODO Command not recognized*/;
            }
        }
    }

/*--PARSER-------------------------------------------------------------------*/

    private String parseLastChars(String aString, int number) {
        return aString.substring(aString.length()-number, aString.length()-1);
    }

    private int stringToInteger(String string){
        /*
        * returns -1 if String is not a Number or String is
        * longer than 2, which is not a legit id in FDZ
        */
        if(! (string.length()>2)){ //numbers in commands go from 0-99 (2digit)
            try{
                int i = Integer.parseInt(string);
                return i;
            }catch(NumberFormatException e){return -1;}
        }
        return -1;
    }

/*--COMMAND CONTROL----------------------------------------------------------*/
/*--REQUEST EMPTY CARRIAGE---------------------------------------------------*/
/*  STStK001<Message-ID>0002xx  ---------------------------------------------*/

    private void requestEmptyCarriage(int newId){

    }

/*--RELEASE CARRIAGE--------------------------------------------------------*/
/*  STStK002<Message-ID>0002xx  --------------------------------------------*/

    private void releaseCarriage(int id){

    }

/*--REPOSITION CARRIAGE-----------------------------------------------------*/
/*  STStK003<Message-ID>0002xxyy  ------------------------------------------*/

    private void repositionCarriage(int id, String position){

    }

/*--SHUTDOWN TRANSPORT SYSTEM-----------------------------------------------*/
/*  STStK004<Message-ID>0002  ----------------------------------------------*/

    private void shutdownTransport(){

    }

}

