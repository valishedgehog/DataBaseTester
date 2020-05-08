package Test.Exceptions;

public class BadConfigException extends Exception {
    public BadConfigException() {
        super("Something wrong with your config");
    }
}
