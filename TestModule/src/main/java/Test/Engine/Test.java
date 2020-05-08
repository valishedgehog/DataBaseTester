package Test.Engine;

import java.io.File;
import java.util.ArrayList;

public class Test {
    private final String name;
    private final ArrayList<String> queries;
    private final File result;
    private final File status;
    private final File expected;

    public Test(ArrayList<String> queries, File result, File status, File expected) {
        name = result.getName().replace(".out", "");

        this.queries = queries;
        this.result = result;
        this.status = status;
        this.expected = expected;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getQueries() {
        return queries;
    }

    public File getResultFile() {
        return result;
    }

    public File getStatusFile() {
        return status;
    }

    public File getExpectedFile() {
        return expected;
    }
}
