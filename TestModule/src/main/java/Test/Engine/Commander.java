package Test.Engine;

import Client.Client;
import Test.Exceptions.DropDatabaseException;
import Test.Utils.ServerUtils;
import Test.Utils.Printer;
import org.apache.commons.text.StringEscapeUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class Commander {
    public static final String FRAMEWORK_COMMAND_PREFIX = "[@";
    public static final String PRINT_LEVEL_COMMAND = "[@PrintLevel]";
    public static final String PRINT_COMMAND = "[@Print]";
    public static final String CLEAR_COMMAND = "[@Clear]";
    public static final String SLEEP_COMMAND = "[@Sleep]";
    public static final String RESTART_SERVER_COMMAND = "[@RestartServer]";
    public static final String NO_OUTPUT_COMMAND = "[@NoOutput]";
    public static final String ECHO_COMMAND = "[@Echo]";

    public enum PRINT_LEVEL {NONE, MAIN, EXTENDED}

    private PRINT_LEVEL printLevel = PRINT_LEVEL.MAIN;
    private boolean noOutput = false;
    private final Client client;

    private final FileOutputStream outputStream;

    public Commander(FileOutputStream outputStream, Client client) {
        this.outputStream = outputStream;
        this.client = client;
    }

    public boolean isFrameworkCommand(String cmd) {
        return cmd.startsWith(FRAMEWORK_COMMAND_PREFIX);
    }

    public void parseFrameworkCommand(String cmd) {
        if (cmd.startsWith(PRINT_LEVEL_COMMAND)) {
            configPrintLevel(cmd);
        } else if (cmd.startsWith(PRINT_COMMAND)) {
            print(cmd);
        } else if (cmd.startsWith(CLEAR_COMMAND)) {
            clearCommand();
        } else if (cmd.startsWith(SLEEP_COMMAND)) {
            sleepCommand(cmd);
        } else if (cmd.startsWith(RESTART_SERVER_COMMAND)) {
            restartServer(cmd);
        } else if (cmd.startsWith(NO_OUTPUT_COMMAND)) {
            noOutputToggle();
        } else if (cmd.startsWith(ECHO_COMMAND)) {
            echoCommand(cmd);
        }
    }


    // [@Print] command

    private void print(String cmd) {
        try {
            String result = StringEscapeUtils.unescapeJava(cmd.replace(PRINT_COMMAND, "").trim());
            outputStream.write((result + "\n").getBytes());
        } catch (IOException e) {
            Printer.printError(e);
        }
    }


    // [@PrintLevel] command

    public PRINT_LEVEL getPrintLevel() {
        return printLevel;
    }

    public void setPrintLevel(PRINT_LEVEL printLevel) {
        this.printLevel = printLevel;
    }

    private void configPrintLevel(String cmd) {
        String printLevel = cmd.replace(Commander.PRINT_LEVEL_COMMAND, "").trim();
        boolean flag = false;
        for (Commander.PRINT_LEVEL level : Commander.PRINT_LEVEL.values()) {
            if (level.name().equals(printLevel)) {
                setPrintLevel(level);
                flag = true;

                if (getPrintLevel() == Commander.PRINT_LEVEL.NONE) {
                    Printer.offSystemOut();
                } else {
                    Printer.resetSystemOut();
                }

                if (getPrintLevel() == Commander.PRINT_LEVEL.EXTENDED) {
                    Printer.printTestInfo("PrintLevel set to " + printLevel);
                }
            }
        }

        if (!flag) {
            Printer.printTestError("Can not set print level to " + printLevel);
        }
    }


    // [@Echo] command

    private void echoCommand(String cmd) {
        String line = cmd.replace(ECHO_COMMAND, "").trim();
        Printer.printTestInfo(line);
    }


    // [@NoOutput] command

    private void noOutputToggle() {
        noOutput = !noOutput;
    }

    public boolean isNoOutput() {
        return noOutput;
    }


    // [@Clear] command

    private void clearCommand() {
        try {
            String workingDir = ServerUtils.getServerWorkingDir();
            Files.walk(Paths.get(workingDir))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(file -> {
                        if (!file.getName().equals("Server") && !file.getName().equals(workingDir)) {
                            file.delete();
                        }
                    });
        } catch (IOException e) {
            Printer.printCriticalError(e);
            Printer.printCriticalError(new DropDatabaseException());
        }

        if (printLevel == Commander.PRINT_LEVEL.EXTENDED) {
            Printer.printTestInfo("Database dropped");
        }
    }


    // [@Sleep] command

    private void sleepCommand(String cmd) {
        String time = cmd.replace(Commander.SLEEP_COMMAND, "").trim();
        try {
            Thread.sleep(new Integer(time));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // [@RestartServer]

    private void killServer() {
        client.disconnect();
        ServerUtils.stopServer();
    }

    private void startServer() {
        ServerUtils.startServer();
        client.connect();
    }

    private void restartServer(String cmd) {
        cmd = cmd.replace(RESTART_SERVER_COMMAND, "").trim();
        String[] args = cmd.split(" ");
        if (cmd.isEmpty()) {
            restartServer();
        } else {
            int time = Integer.parseInt(args[0]);
            int minTime = args.length == 2 ? Integer.parseInt(args[1]) : 0;
            new Thread(() -> restartServer(time, minTime)).start();
        }
    }

    private void restartServer() {
        if (printLevel == PRINT_LEVEL.EXTENDED) {
            Printer.printInfo("Restarting server");
        }

        killServer();
        startServer();

        if (printLevel == PRINT_LEVEL.EXTENDED) {
            Printer.printInfo("Server restarted");
        }
    }

    private void restartServer(int time, int minTime) {
        int rand = (int) (Math.random() * ((time - minTime) + 1)) + minTime;

        try {
            Thread.sleep(rand);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        restartServer();
    }
}
