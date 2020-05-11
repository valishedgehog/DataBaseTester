package Test.Engine.Cli;

import Client.ClientRunner;
import Test.Engine.Tester;
import Test.Utils.ServerUtils;
import Test.Utils.Printer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TestCliEngine {

    private final Tester tester;

    public TestCliEngine() {
        Printer.printDelimiter();
        Printer.printTask("Initializing Tester instance");

        tester = new Tester();

        Printer.printInfo("Tests folder: " + tester.getConfiguration().getTestsFolder());
        Printer.printDelimiter();
    }

    public boolean createTest(String[] testNames) {
        boolean status = true;

        for (String name : testNames) {
            Printer.printTask("Creating test <" + name + ">");

            String test_dir = tester.getConfiguration().getTestsFolder().concat(name);
            String test_results_dir = test_dir.concat("/results");
            String test_expected_dir = test_dir.concat("/expected");

            new File(test_dir).mkdirs();
            new File(test_results_dir).mkdirs();
            new File(test_expected_dir).mkdirs();

            try {
                new File(test_dir.concat("/test.in")).createNewFile();
                new File(test_expected_dir.concat("/test.expected")).createNewFile();
                Printer.printInfo("Test <" + name + "> created");
            } catch (IOException e) {
                Printer.printCriticalError(e);
                Printer.printError("Test <" + name + "> is not created");
                status = false;
            }
        }

        return status;
    }

    public void listTests() {
        for (String f : Objects.requireNonNull(new File(tester.getConfiguration().getTestsFolder()).list())) {
            Printer.printListElement(f);
        }
    }

    public boolean removeTests(String[] args) {
        boolean status = true;

        for (String name : args) {
            Printer.printTask("Deleting test <" + name + ">");
            File testFolder = new File(tester.getConfiguration().getTestsFolder() + name);
            if (testFolder.exists()) {
                try {
                    Files.walk(Paths.get(String.valueOf(testFolder)))
                            .map(Path::toFile)
                            .sorted((o1, o2) -> -o1.compareTo(o2))
                            .forEach(File::delete);
                    Printer.printInfo("Test <" + name + "> deleted");
                } catch (IOException e) {
                    Printer.printCriticalError(e);
                    status = false;
                }
            } else {
                Printer.printError("Test not found <" + name + ">");
                status = false;
            }
        }

        return status;
    }

    public boolean runTests(String[] args) {
        int countTests = 0;
        int countPassed = 0;

        boolean status = true;

        for (String testName : args) {
            if (tester.getConfiguration().getTestPath(testName) == null) {
                Printer.printError("Test <" + testName + "> does not exist");
                continue;
            }

            countTests++;

            boolean result = tester.test(testName);

            if (result) {
                countPassed++;
            } else {
                status = false;
            }
        }

        if (countTests > 0) {
            Printer.printTestsStatistic(countTests, countPassed, countTests - countPassed);
        }

        return status;
    }

    public boolean runAllTests() {
        Printer.printTask("Running all tests");
        Printer.printDelimiter();

        List<String> folders = Arrays.asList(Objects.requireNonNull(new File(tester.getConfiguration().getTestsFolder()).list()));

        return runTests((String[]) folders.toArray());
    }

    public boolean client() {
        return new ClientRunner().run(ServerUtils.PORT);
    }
}
