package Test.Exceptions;

public class DropDatabaseException extends Exception {
    public DropDatabaseException() {
        super("Error occurred while dropping database");
    }
}
