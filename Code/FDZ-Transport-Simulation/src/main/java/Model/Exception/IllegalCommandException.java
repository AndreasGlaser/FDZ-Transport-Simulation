package Model.Exception;

public class IllegalCommandException extends Exception{

    public IllegalCommandException(String problem){
        super("\tlog: ERROR\n" +
                "\t\tIllegalCommandException:\n" +
                problem);
    }

}
