package Test.Exceptions;

public class ServerDownException extends Exception {
    public ServerDownException(String message) {
        super(message);
    }
}
