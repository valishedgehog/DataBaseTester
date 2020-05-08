package Test.Exceptions;

public class ClientDownException extends Exception {
    public ClientDownException() {
        super("Client is down");
    }
}
