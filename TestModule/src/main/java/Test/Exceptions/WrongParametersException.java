package Test.Exceptions;

public class WrongParametersException extends Exception{
    public WrongParametersException() {
        super("Wrong parameters for command");
    }
}
