package Test.Utils;

import Client.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientServerHelper {
    public static String SERVER_PATH = "~/DBServer";
    public static final String PIDOF_SERVER = "pidof DBServer";

    public static void restartServer() {
        stopServer();
        startServer();
    }

    public static void startServer() {
        final String RUN_SERVER = String.format("%s", SERVER_PATH);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(RUN_SERVER);
            processBuilder.directory(new File(getServerWorkingDir()));
            processBuilder.start();
        } catch (IOException e) {
            Printer.printCriticalError(e);
        }
    }

    public static void stopServer() {
        final String KILL_SERVER = String.format("kill -KILL $(%s)", PIDOF_SERVER);

        try {
            Runtime.getRuntime().exec(KILL_SERVER);
        } catch (IOException e) {
            Printer.printCriticalError(e);
        }
    }

    private static int getServerPID() {
        try {
            Process process = Runtime.getRuntime().exec(PIDOF_SERVER);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (process.isAlive()) ;

            String result = reader.readLine();

            if (result == null || result.trim().isEmpty()) return -1;

            return Integer.parseInt(result);
        } catch (IOException e) {
            Printer.printCriticalError(e);
        }

        return -1;
    }

    public static boolean serverDown() {
        return getServerPID() == -1;
    }

    public static String getServerWorkingDir() {
        return String.valueOf(new File(SERVER_PATH).toPath().getParent());
    }


    // ClientHelper

    public static int PORT = 18666;

    public static Client getClient() {
        Client client = new Client(PORT, Printer.getEmptyPrintStream(), Printer.getEmptyPrintStream());
        return (!client.connect()) ? null : client;
    }
}
