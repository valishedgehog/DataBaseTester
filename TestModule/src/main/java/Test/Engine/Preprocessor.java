package Test.Engine;

import java.util.ArrayList;

public class Preprocessor {
    public static final String FRAMEWORK_COMMAND_PREFIX = "[@";
    public static final String PREPROCESSOR_COMMAND_PREFIX = "[#";

    public static final String REPEAT_COMMAND = "[#Repeat]";

    public boolean isPreprocessorCommand(String cmd) {
        return cmd.startsWith(PREPROCESSOR_COMMAND_PREFIX);
    }

    public boolean isFrameworkCommand(String cmd) {
        return cmd.startsWith(FRAMEWORK_COMMAND_PREFIX);
    }

    public ArrayList<String> parsePreprocessorCommand(String cmd, String query) {
        if (cmd.startsWith(REPEAT_COMMAND)) {
            return repeatQuery(cmd, query);
        }

        return new ArrayList<>();
    }

    // #########################
    // [#Repeat] command
    // #########################

    public ArrayList<String> repeatQuery(String cmd, String query) {
        ArrayList<String> queries = new ArrayList<>();

        int n = Integer.parseInt(cmd.split(" ")[1]);

        for (int i = 1; i <= n; i++) {
            queries.add(query.replaceAll("\\$i", String.valueOf(i)));
        }

        return queries;
    }
}
