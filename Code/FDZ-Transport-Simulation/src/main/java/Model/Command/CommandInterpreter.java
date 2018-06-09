package Model.Command;

/**
 * @author nlehmann
 */

import Model.Exception.IllegalCommandException;
import Model.Network.NetworkController;


public class CommandInterpreter extends Thread {

    /*--MEMBERVARIABLES----------------------------------------------------------*/

    private final String command;
    private String position, messageID;
    private int commandNum = -1, beginMesID = -1;
    private Integer carriageID = null, paramCount = null;
    private final int NO_ID_REQUIRED = -1;

    /*--CONSTRUCTOR--------------------------------------------------------------*/

    /**
     * Invokes a new InterpreterThread, which parses the received Message
     * @param command the full String received by the NetworkController
     */
    public CommandInterpreter(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        try{
            this.parseValues();
            new CommandValidator(command, messageID, position, commandNum, paramCount, carriageID);
        }catch(IllegalCommandException e){
            error(messageID);
        }

        System.out.println("\t log: \n" +
                "\t\tcommandNum = " + commandNum + "\n" +
                "\t\tcarriageID = " + carriageID + "\n" +
                "\t\tposition   = " + position + "\n" +
                "\t\tmessageID  = " + messageID);

        switch (this.commandNum) {
            case 1:
                System.out.println("\t log: " + "interpreted case 1");
                CommandQueue.getInstance().add(new RequestEmptyCarriage(position, messageID));
                acknowledge(messageID);
                break;
            case 2:
                System.out.println("\t log: " + "interpreted case 2");
                CommandQueue.getInstance().add(new ReleaseCarriage(carriageID, messageID));
                acknowledge(messageID);
                break;
            case 3:
                System.out.println("\t log: " + "interpreted case 3");
                CommandQueue.getInstance().add(new RepositionCarriage(carriageID, position, messageID));
                acknowledge(messageID);
                break;
            case 4:
                System.out.println("\t log: " + "interpreted case 4");
                CommandQueue.getInstance().add(new ShutdownTransport(messageID));
                acknowledge(messageID);
                break;
            default:
                System.out.println("\t log: default");
                error(messageID);
        }
    }

    /**
     * Acknowledges the Command for NetworkController to send acknowledge1, waits for response of NetworkController
     * @param messageID Message ID of the Command
     */
    private void acknowledge(String messageID){
        if(NetworkController.getInstance().acknowledge1(messageID)) {
            CommandQueue.getInstance().activate(messageID);
        }
    }

    /**
     * sends an Error 'Command not understood' to the NetworkController, will not wait for response
     * @param messageID Message ID of the Command
     */
    private void error(String messageID){
        NetworkController.getInstance().commandNotUnterstood(messageID);
    }

    /*--PARSER-------------------------------------------------------------------*/

    /**
     * Starts the Sequence of parsing the Command for its values.
     * Does not read any Values, only starts other Methods
     * @throws IllegalCommandException thrown if the Command cannot be parsed due to invalid construction
     */
    private void parseValues() throws IllegalCommandException{
        this.commandNum = parseCommandNum();
        this.paramCount = parseParamCount();
        this.position = parsePosition();
        this.carriageID = parseCarriageID();
        this.messageID = parseMessageID();
    }

    /**
     * Parses the Number of the Command. Each Command is identified by a
     * unique Number in the Message
     * @return CommandNumber
     * @throws IllegalCommandException thrown if the CommandNumber cannot be parsed due to invalid construction of the command
     */
    private int parseCommandNum() throws IllegalCommandException{
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
     * Parses the PayloadSize at position 24 of the command String
     * @return payload size
     */
    private Integer parseParamCount(){
        try{
            return Integer.parseInt(command.substring(24,25));
        }catch (NumberFormatException e){
            return null;
        }
    }

    /**
     * Parses the Message for a Position. If non is required, the
     * returned String will say so. A Position is alway the last parameter
     * in the Message
     * @return StationShortCut
     * @throws IllegalCommandException thrown if the position cannot be parsed due to invalid construction of the command
     */
    private String parsePosition() throws IllegalCommandException{
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
     * @return CarriageID
     * @throws IllegalCommandException thrown if the carriageID cannot be parsed due to invalid construction of the command
     */
    private int parseCarriageID() throws IllegalCommandException{
        /*STStK001|2|3...??..*/
        if(commandNum == 2 || commandNum == 3) {
            /*reposition or release Carriage*/
            int end = command.length() - 1;
            if (paramCount != null && paramCount == 2 && commandNum == 2) {     //release Carriage
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
     * @return MessageID
     */
    private String parseMessageID(){
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

}

