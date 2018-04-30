package Model;

/*--NOAH LEHMANN-------------------------------------------------------------*/

import java.util.ArrayList;

class CommandInterpreter extends Thread {

/*--MEMBERVARIABLES----------------------------------------------------------*/

    private final String command;
    private final ArrayList<Station> stationList;
    private String position, messageID;
    private int commandNum = -1, carriageID = -1, paramCount = -1,
                beginMesID = -1;
    private final char POS = '2', ID = '2', POS_ID = '4', NO = '0';
    private final int NO_ID_REQUIRED = -1;

/*--CONSTRUCTOR--------------------------------------------------------------*/

    public CommandInterpreter(String command, ArrayList<Station> currentStationList)
                                                        throws IllegalCommandException{
        this.command = command;
        this.stationList = currentStationList;
        this.parseValues();
        this.validateValues();
    }


    @Override
    public void run() {
        switch(this.commandNum){
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
                        /* TODO Command not recognized*/
        }
        System.out.println("\t log: Command Succesfully Recognized");
        /*TODO Reply command recognized*/
    }

/*--PARSER-------------------------------------------------------------------*/

    private void parseValues() throws IllegalCommandException{
        this.commandNum = parseCommandNum(command);
        if( validateParamCount(command)                //comNum = expected Params
                & validateCommand(command)) {       //other input mistakes
            this.position = parsePosition(command);
            this.carriageID = parseCarriageID(command);
            this.messageID = parseMessageID(command);

            System.out.println("\t log: \n" +
                    "\t\tcommandNum = " + commandNum + "\n" +
                    "\t\tcarriageID = " + carriageID + "\n" +
                    "\t\tposition   = " + position + "\n" +
                    "\t\tmessageID  = " + messageID);
        }else{
            String mes = "\t\t\tCommandInterpreter at: parseValues(); \n" +
                         "\t\t\tvalidateParamCount && validateCommand not true!\n" +
                         "\t\t\tprobably commandNum and paramCount Values\n" +
                         "\t\t\tdon't match";
            throw new IllegalCommandException(mes);      //invalid command
        }
    }

    private int parseCommandNum(String command) throws IllegalCommandException{
        /*throws Exception, if char at commandNum position is NaN*/
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
            String mes = "\t\t\tCommandInterpreter at: parseCommandNum(); \n" +
                         "\t\t\tNo Number at Position where CommandNum\n" +
                         "\t\t\tis expected";
            throw new IllegalCommandException(mes);      //invalid command
        }
    }

    private String parsePosition(String command) throws IllegalCommandException{
        /*STStK003....id??*/
        if(commandNum == 1 || commandNum == 3) {
            /*reposition or request carriage*/
            int end = command.length() - 1;
            String sub = command.substring(end - 1, end + 1);
            try {
                Integer.parseInt(sub);
                String mes = "\t\t\tCommandInterpreter at: parsePosition(); \n" +
                             "\t\t\tNumber found, where Position expected";
                throw new IllegalCommandException(mes);      //no number expected
            } catch (NumberFormatException e) {
                return sub;
            }
        }
        else{
            return "NO_POS_REQUIRED";
        }
    }

    private int parseCarriageID(String command) throws IllegalCommandException{
        /*STStK001|2|3...??..*/
        if(commandNum == 2 || commandNum == 3) {
            /*reposition or release Carriage*/
            int end = command.length() - 1;
            if (paramCount == 2 && commandNum == 2) {     //release Carriage
                String sub = command.substring(end - 1, end + 1);
                try {
                    return Integer.parseInt(sub);   //number expected
                } catch (NumberFormatException e) {
                    String mes = "\t\t\tCommandInterpreter at: parseCarriageID();\n" +
                            "\t\t\tNo ID found, where expected [at last 2]";
                    throw new IllegalCommandException(mes);    //no id found
                }
            } else if (paramCount == 4 && commandNum == 3) {
                String sub = command.substring(end - 3, end - 1);
                try {
                    return Integer.parseInt(sub);   //number expected
                } catch (NumberFormatException e) {
                    String mes = "\t\t\tCommandInterpreter at: parseCarriageID();\n" +
                            "\t\t\tNo ID found, where expected\n" +
                            "\t\t\t[at 3rd+4th last]";
                    throw new IllegalCommandException(mes);    //no id found
                }
            }
            String mes = "\t\t\tCommandInterpreter at: parseCarriageID(); \n" +
                    "\t\t\tNo ID found, where expected";
            throw new IllegalCommandException(mes);    //no id found
        }else{
            return NO_ID_REQUIRED;
        }
    }

    private String parseMessageID(String command){
        /*STStK00.<??>..*/
        int end = command.length();
        if(commandNum == 1 || commandNum == 2){
            try {
                return command.substring(beginMesID, end-6);
            }catch(StringIndexOutOfBoundsException sEx){
                System.out.println(sEx.getMessage());
                return null;
            }
        }
        if(commandNum == 3){
            try {
                return command.substring(beginMesID, end-8);
            }catch(StringIndexOutOfBoundsException sEx){
                System.out.println(sEx.getMessage());
                return null;
            }
        }
        if(commandNum == 4){
            try {
                return command.substring(beginMesID, end-4);
            }catch(StringIndexOutOfBoundsException sEx){
                System.out.println(sEx.getMessage());
                return null;
            }
        }
        return null;
        /*TODO Exception Handling, Fragen was ID beinhaltet*/
    }

/*--VALIDATOR----------------------------------------------------------------*/

    private boolean validateCommand(String command) throws IllegalCommandException{
        /*Special Cases:*/
        if(command == null){
            String mes = "\t\t\tCommandInterpreter at: validateCommand(); \n" +
                         "\t\t\tEither no command was given to Interpreter\n" +
                         "\t\t\tor NULL was referenced to Command\n";
            throw new IllegalCommandException(mes);
        }
        if(command.length() < 12){          //More chars expected
            String mes = "\t\t\tCommandInterpreter at: validateCommand(); \n" +
                         "\t\t\tCommand referenced to Interpreter does\n" +
                         "\t\t\tnot match the expected length\n";
            throw new IllegalCommandException(mes);
        }
        if (command.substring(0,7).compareTo("STStK00") != 0){
            String mes = "\t\t\tCommandInterpreter at: validateCommand();\n" +
                         "\t\t\tCommand given to Interpreter can not be\n" +
                         "\t\t\trecognized;\n" +
                         "\t\t\tDoes not start with \"STStK00\"";
            throw new IllegalCommandException(mes);
        }
        return true;
    }

    private boolean validateParamCount(String command){
        char [] chars = command.toCharArray();
        if(commandNum == 1 & chars[command.length()-3] == POS){
            //requestEmptyCarriage
            this.paramCount = 2;
            return true;
        }
        if(commandNum == 2 & chars[command.length()-3] == ID){
            //releaseCarriage
            this.paramCount = 2;
            return true;
        }
        if(commandNum == 3 & chars[command.length()-5] == POS_ID){
            //repositionCarriage
            this.paramCount = 4;
            return true;
        }
        if(commandNum == 4 & chars[command.length()-1] == NO){
            //shutdownTransport
            this.paramCount = 0;
            return true;
        }
        this.paramCount = -1;
        return false;
    }

    private void validateValues() throws IllegalCommandException{
        /*--INT VALUES--------------*/
        if(paramCount < 0){
            String mes = "\t\t\tCommandInterpreter at: validateValues(); \n" +
                         "\t\t\tNumber of Params in Command not as Expected";
            throw new IllegalCommandException(mes);
        }
        if(carriageID == -1 &&              //carriageID necessary
                (commandNum == 2 || commandNum == 3)){
            String mes = "\t\t\tCommandInterpreter at: validateValues(); \n" +
                         "\t\t\tcarriageID not set, though needed for\n" +
                         "\t\t\tprocessing Command";
            throw new IllegalCommandException(mes);
        }
        if(0 > commandNum || commandNum > 4 ){
            String mes = "\t\t\tCommandInterpreter at: validateValues(); \n" +
                         "\t\t\tNumber identifying Command not listed in known\n" +
                         "\t\t\tcommands";
            throw new IllegalCommandException(mes);
        }
        if(beginMesID == -1){
            String mes = "\t\t\tCommandInterpreter at: validateValues(); \n" +
                         "\t\t\tMessageID could not be located in Command";
            throw new IllegalCommandException(mes);
        }
        /*--STRING VALUES-----------------------*/
        if(position == null){
            String mes = "\t\t\tCommandInterpreter at: validateValues(); \n" +
                         "\t\t\tNo Position for carriage found, but\n" +
                         "\t\t\tneeded to process Command";
            throw new IllegalCommandException(mes);
        }
        if(messageID == null){
            String mes = "\t\t\tCommandInterpreter at: validateValues(); \n" +
                         "\t\t\tNo Message ID set";
            throw new IllegalCommandException(mes);
        }
        /*--OBJECT VALUES-----------------------*/
        if(stationList == null || stationList.size() == 0){
            String mes = "\t\t\tCommandInterpreter at: validateValues(); \n" +
                         "\t\t\tList of currently used Station is either\n" +
                         "\t\t\tnot set or empty";
            throw new IllegalCommandException(mes);
        }
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

