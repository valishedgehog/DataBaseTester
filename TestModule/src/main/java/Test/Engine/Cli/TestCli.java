package Test.Engine.Cli;

import Test.Exceptions.WrongParametersException;
import Test.Utils.Printer;

import java.util.Arrays;
import java.util.Scanner;

public class TestCli {
    private final TestCliEngine engine;

    public TestCli() {
        engine = new TestCliEngine();
    }

    public boolean run(String[] args) {
        if (args.length > 0) {
            return parseCommand(args);
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            Printer.printShellPrompt();
            String[] command = scanner.nextLine().split(" ");

            if (command.length == 0) continue;

            if (isExitCommand(command)) return true;

            parseCommand(command);
        }
    }

    private boolean parseCommand(String[] command) {
        String[] args = Arrays.copyOfRange(command, 1, command.length);
        switch (command[0]) {
            case "run":
                if (!checkArgsLen(command.length, 2)) return false;

                boolean run_all_flag = false;
                for (String arg : args) {
                    if (arg.equals("all")) {
                        run_all_flag = true;
                        break;
                    }
                }

                if (run_all_flag) {
                    return engine.runAllTests();
                }

                return engine.runTests(args);
            case "list":
            case "lst":
            case "dir":
            case "ls":
                if (!checkArgsLen(command.length, 1, 1)) return false;

                engine.listTests();
                break;
            case "create":
            case "new":
                if (!checkArgsLen(command.length, 2)) return false;

                return engine.createTest(args);
            case "delete":
            case "remove":
            case "del":
            case "rm":
                if (!checkArgsLen(command.length, 2)) return false;
                return engine.removeTests(args);
            case "cls":
            case "clear":
                if (!checkArgsLen(command.length, 1, 1)) return false;

                Printer.clear();
                break;
            case "client":
                if (!checkArgsLen(command.length, 1, 1)) return false;

                return engine.client();
            default:
                Printer.printError("Unknown command");
        }

        return true;
    }

    private boolean isExitCommand(String[] command) {
        return command[0].equals("exit") || command[0].equals("quit");
    }

    private boolean checkArgsLen(int len, int min) {
        boolean result = len >= min;
        if (!result) Printer.printError(new WrongParametersException());
        return result;
    }

    private boolean checkArgsLen(int len, int min, int max) {
        boolean result = len >= min && len <= max;
        if (!result) Printer.printError(new WrongParametersException());
        return result;
    }
}
