package Model.Command;

import Model.Exception.IllegalCommandException;
import Model.Logger.LoggerInstance;
import Model.Network.NetworkController;

import java.util.Date;

/**
 * @author nlehmann
 *
 * ThisClass interpretes the Command received by the NetworkController
 */
public class CommandInterpreter extends SaveObservable implements Runnable {

    /*--MEMBER VARIABLES----------------------------------------------------------*/

    private final String command;
    private String position, messageID;
    private int commandNum = -1;
    private Integer carriageID = null, paramCount = null;

    /*--CONSTRUCTOR--------------------------------------------------------------*/

    /**
     * Invokes a new InterpreterThread, which parses the received Message
     * @param command the full String received by the NetworkController
     */
    public CommandInterpreter(String command) {
        LoggerInstance.log.debug("Interpreter started");
        this.command = this.removeWhiteSpaces(command);
    }

    /**
     * Removes WhiteSpaces from InputCommand
      * @param command the command from Network
     * @return command without WhiteSpaces
     */
    private String removeWhiteSpaces(String command) {
        StringBuilder withOut = new StringBuilder();
        for (char c : command.toCharArray()) {
            if(!Character.isWhitespace(c)){
                withOut.append(c);
            }
        }
        return withOut.toString();
    }

    @Override
    public void run() {
        try{
            this.parseValues();
            System.err.println("parsed");
            new CommandValidator(command, messageID, position, commandNum, paramCount, carriageID);
            System.err.println("validated");
        }catch(IllegalCommandException e){
            LoggerInstance.log.warn("Interpreter detected Illegal Command Structure!");
            error(messageID);
            return;
        }

        Command command;
        switch (this.commandNum) {
            case 1:
                LoggerInstance.log.info("Interpreted RequestEmptyCarriage to " + position +" Command");
                command = new RequestEmptyCarriage(position, messageID);
                CommandQueue.getInstance().add(command);
                LoggerInstance.log.debug("Added new RequestEmptyCarriage Command to Queue");
                acknowledge(messageID, command);
                break;
            case 2:
                LoggerInstance.log.info("Interpreted ReleaseCarriage " + carriageID + " Command");
                command = new ReleaseCarriage(carriageID, messageID);
                CommandQueue.getInstance().add(command);
                LoggerInstance.log.debug("Added new ReleaseCarriage Command to Queue");
                acknowledge(messageID, command);
                break;
            case 3:
                LoggerInstance.log.info("Interpreted RepositionCarriage " + carriageID +" to " + position + " Command");
                command = new RepositionCarriage(carriageID, position, messageID);
                CommandQueue.getInstance().add(command);
                LoggerInstance.log.debug("Adding new RepositionCarriage Command to Queue");
                acknowledge(messageID, command);
                break;
            case 4:
                LoggerInstance.log.info("Interpreted ShutdownTransportCommand at "+ new Date(System.currentTimeMillis()).toString());
                command = new ShutdownTransport(messageID);
                CommandQueue.getInstance().add(command);
                LoggerInstance.log.debug("Adding new ShutdownTransport Command to Queue");
                acknowledge(messageID, command);
                break;
            default:
                error(messageID);
        }
    }

    /**
     * Acknowledges the Command for NetworkController to send acknowledge1, waits for response of NetworkController
     * @param messageID Message ID of the Command
     */
    private void acknowledge(String messageID, Command command){
        if(NetworkController.getInstance().acknowledge1(messageID)) {
            command.confirmAck1Success();
            notifyObservers();
            CommandQueue.getInstance().activate(messageID);
            LoggerInstance.log.debug("Activated Command with MessageID "+ messageID);
        }
    }

    /**
     * sends an Error 'Command not understood' to the NetworkController, will not wait for response
     * @param messageID Message ID of the Command
     */
    private void error(String messageID){
        LoggerInstance.log.warn("Interpreter couldn't find right Command for input Message!");
        NetworkController.getInstance().commandNotUnderstood(messageID);
    }

    /*--PARSER-------------------------------------------------------------------*/

    /**
     * Starts the Sequence of parsing the Command for its values.
     * Does not read any Values, only starts other Methods
     * @throws IllegalCommandException thrown if the Command cannot be parsed due to invalid construction
     */
    private void parseValues() throws IllegalCommandException{
        this.commandNum = parseCommandNum();
        this.messageID = parseMessageID();
        this.paramCount = parseParamCount();
        this.position = parsePosition();
        this.carriageID = parseCarriageID();
        LoggerInstance.log.debug("Done Parsing Values of Message "+ messageID);
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
            LoggerInstance.log.error("Unable to read Command (empty): ", e);
            String mes = "CommandInterpreter at: parseCommandNum(). Unable to read Command (empty)";
            throw new IllegalCommandException(mes);
        }
        try{
            return Integer.parseInt(Character.toString(chars[cntIndex]));
        }catch(NumberFormatException e){
            LoggerInstance.log.error("No Number at Position where CommandNum is expected", e);
            String mes = "CommandInterpreter at: parseCommandNum(). No Number at Position where CommandNum is expected";
            throw new IllegalCommandException(mes);      //invalid command
        }catch(ArrayIndexOutOfBoundsException e){
            LoggerInstance.log.error("Unable to read Command (empty)", e);
            String mes = "CommandInterpreter at: parseCommandNum(). Unable to read Command (empty)";
            throw new IllegalCommandException(mes);
        }
    }

    /**
     * Parses the PayloadSize at position 24 of the command String
     * @return payload size
     * @throws IllegalCommandException if paramCount cannot be parsed
     */
    private Integer parseParamCount() throws IllegalCommandException{
        try{
            return Integer.parseInt(command.substring(24,25));
        }catch (NumberFormatException | IndexOutOfBoundsException e){
            LoggerInstance.log.error("Parameter Count is null", e);
            throw new IllegalCommandException("Parameter Count is null");
        }
    }

    /**
     * Parses the Message for a Position. If non is required, the
     * returned String will say so. A Position is always the last parameter
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
                String mes = "CommandInterpreter at: parsePosition(). Number found, where Position expected";
                IllegalCommandException e = new IllegalCommandException(mes);
                LoggerInstance.log.error("Number found, where Position expected", e);
                throw e;      //no number expected
            } catch (NumberFormatException e) {
                return sub;
            }
        }
        else{
            return null;
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
                    LoggerInstance.log.error("No ID found, where expected [at last 2]", e);
                    String mes = "CommandInterpreter at: parseCarriageID(). No ID found, where expected [at last 2]";
                    throw new IllegalCommandException(mes);    //no id found
                }
            } else if (paramCount != null && paramCount == 4 && commandNum == 3) {
                String sub = command.substring(end - 3, end - 1);
                try {
                    return Integer.parseInt(sub);   //number expected
                } catch (NumberFormatException e) {
                    LoggerInstance.log.error("No ID found, where expected [at 3rd+4th last]", e);
                    String mes = "CommandInterpreter at: parseCarriageID(). No ID found, where expected [at 3rd+4th last]";
                    throw new IllegalCommandException(mes);    //no id found
                }
            }
            String mes = "CommandInterpreter at: parseCarriageID(); No ID found, where expected";
            IllegalCommandException e = new IllegalCommandException(mes);    //no id found
            LoggerInstance.log.error("No ID found, where expected", e);
            throw e;
        }else{
            return -1;
        }
    }

    /**
     * Parses the middle section of a message for an Identifier
     * made of the time, the message was sent
     * @return MessageID
     */
    private String parseMessageID(){
        /*STStK00.<??>..*/
        try{
            int beginMesID = 8;
            return command.substring(beginMesID, beginMesID +13);
        }catch(StringIndexOutOfBoundsException e){
            return null;
        }
    }
}

