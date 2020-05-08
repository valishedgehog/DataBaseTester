package Test.Utils;

import Client.Client;
import Test.Exceptions.ClientDownException;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ClientServerHelper {

    // ServerHelper

    public static String SERVER_PATH = "~/Server";

    private static Process server;

    public static void startServer() {
        try {
            ProcessBuilder builder = new ProcessBuilder(SERVER_PATH);
            builder.directory(new File(getServerWorkingDir()));
            server = builder.start();
            reconnectClients();
        } catch (IOException e) {
            Printer.printCriticalErrorAndExit(e);
        }
    }

    public static void stopServer() {
        disconnectClients();
        server.destroyForcibly();
    }

    public static boolean serverDown() {
        return !server.isAlive();
    }

    public static String getServerWorkingDir() {
        return String.valueOf(new File(SERVER_PATH).toPath().getParent());
    }


    // ClientHelper

    public static int PORT = 18666;

    private static final ArrayList<Client> clients = new ArrayList<Client>();

    public static int registerClient() {
        Client client = new Client(PORT, Printer.getEmptyPrintStream(), Printer.getEmptyPrintStream());

        if (!client.connect()) return -1;

        clients.add(client);

        return clients.size() - 1;
    }

    public static String communicate(int clientId, String msg) {
        String result = "TimeoutException";
        final Duration timeout = Duration.ofSeconds(1);
        ExecutorService executor = Executors.newSingleThreadExecutor();

        final Future<String> handler = executor.submit((Callable) () -> clients.get(clientId).communicate(msg));

        try {
            result = handler.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            handler.cancel(true);
            stopServer();
            startServer();
        } catch (InterruptedException | ExecutionException e) {
            Printer.printError(e);
        }

        executor.shutdownNow();

        return result;
    }

    public static void disconnectClient(int id) {
        clients.get(id).disconnect();
    }

    public static void disconnectClients() {
        for (Client client : clients) {
            client.disconnect();
        }
    }

    public static void reconnectClients() {
        while (serverDown()) {
            try {
                Printer.printInfo("Waiting client-server up...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Printer.printCriticalErrorAndExit(e);
            }
        }

        for (Client client : clients) {
            boolean flag = client.connect();
            if (!flag) {
                Printer.printCriticalErrorAndExit(new ClientDownException());
            }
        }
    }

    public static void clearClients() {
        clients.clear();
    }
}
