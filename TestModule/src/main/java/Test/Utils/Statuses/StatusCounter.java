package Test.Utils.Statuses;

public class StatusCounter {
    private int DB_ERROR_COUNT = 0;
    private int PARSER_ERROR_COUNT = 0;
    private int OK_COUNT = 0;
    private int CONNECTION_LOST_COUNT = 0;
    private int TIMEOUT_COUNT = 0;

    public void parse(Status status) {
        switch (status.getStatus()) {
            case "DB_ERROR":
                DB_ERROR_COUNT++;
                break;
            case "PARSER_ERROR":
                PARSER_ERROR_COUNT++;
                break;
            case "CONNECTION_LOST":
                CONNECTION_LOST_COUNT++;
                break;
            case "TIMEOUT_EXCEPTION":
                TIMEOUT_COUNT++;
                break;
            case "OK":
                OK_COUNT++;
                break;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Statuses statistic").append("\n");

        if (DB_ERROR_COUNT != 0) {
            builder.append("Database errors = ").append(DB_ERROR_COUNT).append("\n");
        }

        if (PARSER_ERROR_COUNT != 0) {
            builder.append("Parser errors = ").append(PARSER_ERROR_COUNT).append("\n");
        }

        if (OK_COUNT != 0) {
            builder.append("OK = ").append(OK_COUNT).append("\n");
        }

        if (CONNECTION_LOST_COUNT != 0) {
            builder.append("Connection lost = ").append(CONNECTION_LOST_COUNT).append("\n");
        }

        if (TIMEOUT_COUNT != 0) {
            builder.append("Timeout = ").append(TIMEOUT_COUNT).append("\n");
        }


        return builder.toString();
    }
}
