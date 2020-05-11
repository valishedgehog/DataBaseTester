package Test.Utils;

import Client.Client;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerUtils {
    public static String SERVER_PATH = "~/Server";
    public static int PORT = 18666;

    public static void startServer() {
        final String RUN_SERVER = String.format("%s", SERVER_PATH);

        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            ProcessBuilder processBuilder = new ProcessBuilder(RUN_SERVER);
            processBuilder.directory(new File(getServerWorkingDir()));
            processBuilder.redirectOutput(new File("/home/anton/logs/Server.log." + dtf.format(now)));
            processBuilder.redirectError(new File("/home/anton/logs/Server.err." + dtf.format(now)));
            processBuilder.start();

            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            Printer.printCriticalError(e);
        }
    }

    public static void stopServer() {
        final String[] KILL_SERVER = new String[] {"kill", "-KILL", String.valueOf(getServerPID())};

        try {
            Runtime.getRuntime().exec(KILL_SERVER);
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            Printer.printCriticalError(e);
        }
    }

    private static int getServerPID() {
        try {
            Process process = Runtime.getRuntime().exec(new String[] {"pidof", "Server"});
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

    public static Client getClient() {
        Client client = new Client(PORT, Printer.getEmptyPrintStream(), Printer.getEmptyPrintStream());
        return (!client.connect()) ? null : client;
    }
}
