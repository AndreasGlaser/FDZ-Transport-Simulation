package Model;

import java.util.ArrayList;

public class CommandInterpreter extends Thread {

/*--MEMBERVARIABLES----------------------------------------------------------*/

    private String command;
    private ArrayList<Station> stationList;
    private String position, messageID;
    private int commandNum, carriageID, beginMesID, endMesID;

/*--CONSTRUCTOR--------------------------------------------------------------*/

    public CommandInterpreter(String command, ArrayList<Station> currentStationList){
        this.command = command;
        this.parseValues();
        this.stationList = currentStationList;
    }


    @Override
    public void run(){
        if (command.substring(0,7).compareTo("STStK00") != 0){
            System.out.println("\t log: "+"Command not recognized");
            /* TODO Command not recognized */
        }else{
            switch(commandNum){
                case 1 :    System.out.println("\t log: "+"interpreted case 1");
                            requestEmptyCarriage( position);
                            break;
                case 2 :    System.out.println("\t log: "+"interpreted case 2");
                            releaseCarriage( carriageID);
                            break;
                case 3 :    System.out.println("\t log: "+"interpreted case 3");
                            repositionCarriage(carriageID, position);
                            break;
                case 4 :    System.out.println("\t log: "+"interpreted case 4");
                            shutdownTransport();
                            break;
                default:
                            System.out.println("\t log: default");
                    /* TODO Command not recognized*/;
            }
        }
    }

/*--PARSER-------------------------------------------------------------------*/

    private void parseValues(){
        this.commandNum = parseCommandNum(command);
        this.position = parsePosition(command);
        this.carriageID = parseCarriageID(command);
        this.messageID = parseMessageID(command);

        System.out.println( "\t log: \n"+
                "\t\tcommandNum = "+commandNum+ "\n" +
                "\t\tcarriageID = "+carriageID+ "\n" +
                "\t\tposition   = "+position+   "\n" +
                "\t\tmessageID  = "+messageID);
    }

    private int parseCommandNum(String command){
        /*Returns -1, if char at commandNum position is not a number*/
        /*STStK00?...*/
        int cntIndex=0;
        char[] chars = command.toCharArray();
        while(chars[cntIndex] != '0'){
            cntIndex++;                     //find first set of numbers
        }
        while(chars[cntIndex] == '0'){
            cntIndex++;                     //find commandNum
        }
        try{
            this.beginMesID = cntIndex+1;   //messageID begins
            return Integer.parseInt(Character.toString(chars[cntIndex]));
        }catch(NumberFormatException e){
            return -1;                      //invalid command
        }
    }

    private String parsePosition(String command){
        /*STStK003....id??*/
        int end = command.length()-1;
        String sub = command.substring(end-1, end+1);
        try{
            Integer.parseInt(sub);
            return null;                    //no number expected
        }catch(NumberFormatException e) {
            return sub;
        }
    }

    private int parseCarriageID(String command){
        /*STStK001|2|3...??..*/
        int end = command.length()-1;
        for(int i=0;i<=1;i++) {                 //allow 1 repetition
            String sub = command.substring(end-1, end+1);
            try {
                this.endMesID = end-2;
                return Integer.parseInt(sub);   //number expected
            } catch (NumberFormatException e) {
                end -= 2;                       //pos found, try 2 before
            }
        }
        this.endMesID = end+2;                  //end of messageId
        return -1;                              //no id found
    }

    private String parseMessageID(String command){
        /*STStK00.<??>..*/
        return command.substring(beginMesID, endMesID+1);
    }

/*--COMMAND CONTROL----------------------------------------------------------*/
/*--REQUEST EMPTY CARRIAGE---------------------------------------------------*/
/*  STStK001<Message-ID>0002xx  ---------------------------------------------*/

    private void requestEmptyCarriage(String position){
        System.out.println("\t log: requesting emtpy carriage to "+ position);
        //TODO position null dann fehler
    }

/*--RELEASE CARRIAGE--------------------------------------------------------*/
/*  STStK002<Message-ID>0002xx  --------------------------------------------*/

    private void releaseCarriage(int id){
        System.out.println("\t log: releasing carriage with id "+ id);
        //TODO Abfangen, ob id -1 ist, dann fehler
    }

/*--REPOSITION CARRIAGE-----------------------------------------------------*/
/*  STStK003<Message-ID>0002xxyy  ------------------------------------------*/

    private void repositionCarriage(int id, String position){
        System.out.println("\t log: reposition carriage "+id+" to "+position);
        //TODO Abfangen, ob id -1 ist, dann fehler
    }

/*--SHUTDOWN TRANSPORT SYSTEM-----------------------------------------------*/
/*  STStK004<Message-ID>0002  ----------------------------------------------*/

    private void shutdownTransport(){
        System.out.println("\t log: shutting down");
    }

}

