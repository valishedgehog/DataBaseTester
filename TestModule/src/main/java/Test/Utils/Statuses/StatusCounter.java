package Test.Utils.Statuses;

public class StatusCounter {
    private int DB_ERROR_COUNT = 0;
    private int PARSER_ERROR_COUNT = 0;
    private int OK_COUNT = 0;

    public void parse(Status status) {
        switch (status.getStatus()) {
            case "DB_ERROR":
                DB_ERROR_COUNT++;
                break;
            case "PARSER_ERROR":
                PARSER_ERROR_COUNT++;
                break;
            case "OK":
                OK_COUNT++;
                break;
        }
    }

    @Override
    public String toString() {
        return "Statuses statistic" + "\n" +
                "DB_ERROR_COUNT=" + DB_ERROR_COUNT + "\n" +
                "PARSER_ERROR_COUNT=" + PARSER_ERROR_COUNT + "\n" +
                "OK_COUNT=" + OK_COUNT + "\n";
    }
}
