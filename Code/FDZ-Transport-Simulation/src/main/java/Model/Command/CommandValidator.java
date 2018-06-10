package Model.Command;

import Model.Exception.IllegalCommandException;
import Model.Station.StationHandler;

/**
 * @author nlehmann
 *
 * Validates the Command interpreted and parsed by CommandInterpreter
 */
public class CommandValidator {

    private final Integer commandNum;

    /**
     * Validates the Command and runs without Exceptions, if the Command is valid
     * @param command the full String which was received by the networkController
     * @param messageID a String not null with length 13, 0-9 and 11-12 being Numbers, separated with a ':'
     * @param position a String with length 2 or null, required for commands request and reposition carriage
     * @param commandNum an Integer in range of 1 and 4 and not null
     * @param paramCount an Integer value in set{0,2,4}
     * @param id an Integer value with two digits, bigger than -2, or null
     * @throws IllegalCommandException if the Command is not valid
     */
    public CommandValidator(String command, String messageID, String position, Integer commandNum, Integer paramCount, Integer id) throws IllegalCommandException {
        this.commandNum = commandNum;
        this.validateCommand(command);
        this.validateCommandNum(commandNum);
        this.validateMessageID(messageID);
        this.validateParamCount(paramCount);
        this.validatePosition(position);
        this.validateID(id);
    }

    private void validateCommand(String command) throws IllegalCommandException{
        if(command.substring(0,7).compareTo("STStK00") != 0){
            throw new IllegalCommandException("Command does not begin with STStK00");
        }
    }

    /**
     * Checks whether the commandNum is valid in terms of the specification of the FDZ
     * @param commandNum an Integer in range of 1 and 4 and not null
     * @throws IllegalCommandException if the commandNum does not meet requirements
     */
    private void validateCommandNum(Integer commandNum) throws IllegalCommandException{
        if(commandNum == null){
            throw new IllegalCommandException("CommandNum is null but always required");
        }
        if(!(1 <= commandNum && commandNum <= 4)){
            throw  new IllegalCommandException("CommandNumber not in Range between 1 and 4");
        }
    }

    /**
     * Checks whether the messageID is valid in terms of the specification of the FDZ
     * @param messageID a String with length 13, 0-9 and 11-12 being Numbers, separated with a ':'
     * @throws IllegalCommandException if the messageID does not meet requirements
     */
    private void validateMessageID(String messageID) throws IllegalCommandException{
        if(messageID.length() != 13){
            throw new IllegalCommandException("Invalid length of MessageID");
        }
        if(messageID.substring(10,11).compareTo(":") != 0){
            throw new IllegalCommandException("MessageID Part-Separator not ':'");
        }
        try{
            Integer.parseInt(messageID.substring(0,10));
            Integer.parseInt(messageID.substring(11));
        }catch(NumberFormatException e){
            throw new IllegalCommandException("MessageID contains non-Number Characters");
        }
    }

    /**
     * Checks whether the paramCount is valid in terms of the specification of the FDZ, matching the Command Number
     * @param paramCount an Integer value in set{0,2,4}
     * @throws IllegalCommandException if the paramCount does not meet requirements
     */
    private void validateParamCount(Integer paramCount) throws IllegalCommandException{
        switch(commandNum){
            case 1: if(paramCount.equals(2)){break;}
            case 2: if(paramCount.equals(2)){break;}
            case 3: if(paramCount.equals(4)){break;}
            case 4: if(paramCount.equals(0)){break;}
            default: throw new IllegalCommandException("Parameter Count does not match the required Command");
        }
    }

    /**
     * Checks whether the position is valid in terms of the specification of the FDZ
     * @param position a String with length 2, required for commands request and reposition carriage
     * @throws IllegalCommandException if the position does not meet requirements
     */
    private void validatePosition(String position) throws IllegalCommandException{
        if(position == null && !(commandNum == 4 || commandNum == 2)){
            throw new IllegalCommandException("Position required but not found");
        }
        if(position != null && position.length() != 2){
            throw new IllegalCommandException("Length of position not 2");
        }
        if(StationHandler.getInstance().getStationByShortCut(position) == null){
            throw new IllegalCommandException("Position not known to system");
        }
    }

    /**
     * Checks whether the ID is valid in terms of the specification of the FDZ
     * @param id an Integer value with two digits, bigger than -2
     * @throws IllegalCommandException if the id does not meet requirements
     */
    private void validateID(Integer id) throws IllegalCommandException{
        switch(commandNum){
            case 1: if(id == null){break;}
            case 2: if(-1 <= id && id <= 99){break;}
            case 3: if(-1 <= id && id <= 99){break;}
            case 4: if(id == null){break;}
            default: throw new IllegalCommandException("ID does not meet requirements");
        }
    }


}
