package Test.Utils.Statuses;

import java.util.regex.Pattern;

public class StatusParser {
    public enum Statuses {OK, DB_ERROR, PARSER_ERROR, CONNECTION_LOST, TIMEOUT_EXCEPTION}

    private static final String DB_ERROR_REGEX = "^*ERROR: (\\d)+$";
    private static final String PARSER_ERROR_REGEX = "^syntax error*";
    private static final String CONNECTION_LOST_REGEX = "^Connection lost$";
    private static final String TIMEOUT_EXCEPTION_REGEX = "^TimeoutException$";

    public static Status parse(String command, String answer) {
        Status status = new Status(command, answer);

        if (Pattern.matches(PARSER_ERROR_REGEX, answer)) {
            status.setStatus(Statuses.PARSER_ERROR.toString());
        } else if (Pattern.matches(DB_ERROR_REGEX, answer)) {
            status.setStatus(Statuses.DB_ERROR.toString());
        } else if (Pattern.matches(CONNECTION_LOST_REGEX, answer)) {
            status.setStatus(Statuses.CONNECTION_LOST.toString());
        } else if (Pattern.matches(TIMEOUT_EXCEPTION_REGEX, answer)) {
            status.setStatus(Statuses.TIMEOUT_EXCEPTION.toString());
        } else {
            status.setStatus(Statuses.OK.toString());
        }

        return status;
    }
}

