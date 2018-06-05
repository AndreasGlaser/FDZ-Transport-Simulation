package Model.Command;

/**@author Noah Lehmann*/

import Model.Command.*;
import Model.Exception.IllegalCommandException;
import Model.Network.NetworkController;
import Model.Station.Station;
import Model.Station.StationHandler;

import java.util.ArrayList;

public class CommandInterpreter extends Thread {

/*--MEMBERVARIABLES----------------------------------------------------------*/

    private final String command;
    private final ArrayList<Station> stationList;
    private String position, messageID;
    private int commandNum = -1, carriageID = -1, paramCount = -1,
                beginMesID = -1;
    private final char POS = '2', ID = '2', POS_ID = '4', NO = '0';
    private final int NO_ID_REQUIRED = -1;

/*--CONSTRUCTOR--------------------------------------------------------------*/

    /**
     * Invokes a new InterpreterThread, which parses the received Message
     * @param command
     * @throws IllegalCommandException
     */
    public CommandInterpreter(String command) {
        this.command = command;
        this.stationList = StationHandler.getInstance().getStationList();
    }

    @Override
    public void run() {
        try{
            this.parseValues();
            this.validateValues();
        }catch(IllegalCommandException e){
            NetworkController.getInstance().commandNotUnterstood(messageID);
            /*TODO ERROR in Network*/
        }

        System.out.println("\t log: \n" +
                "\t\tcommandNum = " + commandNum + "\n" +
                "\t\tcarriageID = " + carriageID + "\n" +
                "\t\tposition   = " + position + "\n" +
                "\t\tmessageID  = " + messageID);

        /*TODO sendAcknowledge1(msgID)*/
        switch (this.commandNum) {
            case 1:
                System.out.println("\t log: " + "interpreted case 1");
                CommandQueue.getInstance().add(new RequestEmptyCarriage(position, messageID));
                acknowledge(messageID);
                break;
            case 2:
                System.out.println("\t log: " + "interpreted case 2");
                CommandQueue.getInstance().add(
                        new ReleaseCarriage(carriageID, messageID));
                acknowledge(messageID);
            case 3:
                System.out.println("\t log: " + "interpreted case 3");
                CommandQueue.getInstance().add(
                        new RepositionCarriage(carriageID, position, messageID));
                acknowledge(messageID);
                break;
            case 4:
                System.out.println("\t log: " + "interpreted case 4");
                CommandQueue.getInstance().add(new ShutdownTransport(messageID));
                acknowledge(messageID);
                break;
            default:
                System.out.println("\t log: default");
                /* TODO Command not recognized Network ERROR*/
                NetworkController.getInstance().commandNotUnterstood(messageID);
        }
        System.out.println("\t log: Command successfully Recognized");
    }

    private void acknowledge(String messageID){
        if(NetworkController.getInstance().acknowledge1(messageID)) {
            CommandQueue.getInstance().activate(messageID);
        }
    }

/*--PARSER-------------------------------------------------------------------*/

    /**
     * Starts the Sequence of parsing the Command for its values.
     * Does not read any Values, only starts other Methods
     * @throws IllegalCommandException
     */
    private void parseValues() throws IllegalCommandException{
        this.commandNum = parseCommandNum(command);
        if( validateParamCount(command)                //comNum = expected Params
                & validateCommand(command)) {       //other input mistakes
            this.position = parsePosition(command);
            this.carriageID = parseCarriageID(command);
            this.messageID = parseMessageID(command);
        }else{
            String mes = "\t\t\tCommandInterpreter at: parseValues(); \n" +
                         "\t\t\tvalidateParamCount && validateCommand not true!\n" +
                         "\t\t\tprobably commandNum and paramCount Values\n" +
                         "\t\t\tdon't match";
            throw new IllegalCommandException(mes);      //invalid command
        }
    }

    /**
     * Parses the Number of the Command. Each Command is identified by a
     * unique Number in the Message
     * @param command
     * @return CommandNumber
     * @throws IllegalCommandException
     */
    private int parseCommandNum(String command) throws IllegalCommandException{
        /*throws Exception, if char at commandNum position is NaN*/
        /*STStK00?...*/
        int cntIndex=0;
        char[] chars = command.toCharArray();
        try {
            while (chars[cntIndex] != '0') {
                cntIndex++;                     //find first set of numbers
            }
            while (chars[cntIndex] == '0') {
                cntIndex++;                     //find commandNum
            }
        }catch(ArrayIndexOutOfBoundsException e){
            String mes = "\t\t\tCommandInterpreter at: parseCommandNum(); \n" +
                         "\t\t\tUnable to read Command (empty)";
            throw new IllegalCommandException(mes);
        }
        try{
            this.beginMesID = cntIndex+1;   //messageID begins
            return Integer.parseInt(Character.toString(chars[cntIndex]));
        }catch(NumberFormatException e){
            String mes = "\t\t\tCommandInterpreter at: parseCommandNum(); \n" +
                         "\t\t\tNo Number at Position where CommandNum\n" +
                         "\t\t\tis expected";
            throw new IllegalCommandException(mes);      //invalid command
        }catch(ArrayIndexOutOfBoundsException aEx){
            String mes = "\t\t\tCommandInterpreter at: parseCommandNum(); \n" +
                         "\t\t\tUnable to read Command (empty)";
            throw new IllegalCommandException(mes);
        }
    }

    /**
     * Parses the Message for a Position. If non is required, the
     * returned String will say so. A Position is alway the last parameter
     * in the Message
     * @param command
     * @return StationShortCut
     * @throws IllegalCommandException
     */
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

    /**
     * Parses the Message for a CarriageID, if one is required,
     * else it returns -2
     * @param command
     * @return CarriageID
     * @throws IllegalCommandException
     */
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

    /**
     * Parses the middle section of a message for an Identifier
     * made of the time, the message was sent
     * @param command
     * @return MessageID
     */
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
    }

/*--VALIDATOR----------------------------------------------------------------*/

    /**
     * Validates if the message is built correctly
     * @param command
     * @return isValid
     * @throws IllegalCommandException
     */
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

    /**
     * Validates wether the found parameter Count equals the expected
     * parameters for the given Command
     * @param command
     * @return isParamCountEqualToCommand
     */
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

    /**
     * Validates the form of the values. Checks wether they are within
     * their domains
     * @throws IllegalCommandException
     */
    private void validateValues() throws IllegalCommandException{
        /*--INT VALUES--------------*/
        if(paramCount < 0){
            String mes = "\t\t\tCommandInterpreter at: validateValues(); \n" +
                         "\t\t\tNumber of Params in Command not as Expected";
            throw new IllegalCommandException(mes);
        }
        if(carriageID < 0 &&              //carriageID necessary
                (commandNum == 2 || commandNum == 3)){
            String mes = "\t\t\tCommandInterpreter at: validateValues(); \n" +
                         "\t\t\tcarriageID not set, though needed for\n" +
                         "\t\t\tprocessing Command, or negative, which is\n" +
                         "\t\t\tnot allowed";
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
        /*--IS POSITION IN LIST OF STATIONS*/
        if(commandNum == 1 || commandNum == 3){
            validatePosition();
        }
    }

    /**
     * Checks, wether the ShortCut for the Position is known
     * to the System
     * @return isCorrect
     * @throws IllegalCommandException
     */
    private boolean validatePosition() throws IllegalCommandException{
        for(int i=0; i<stationList.size(); i++){
            if(stationList.get(i).getShortCut()
                    .compareToIgnoreCase(position) == 0){
                return true;
            }
        }
        String mes = "\t\t\tCommandInterpreter at: validatePosition(); \n" +
                     "\t\t\tGiven Position not found in List of\n" +
                     "\t\t\tcurrent Stations (search was not Case-Sensitive)";
        throw new IllegalCommandException(mes);
    }
}

