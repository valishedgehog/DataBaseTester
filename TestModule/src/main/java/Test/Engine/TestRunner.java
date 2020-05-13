package Test.Engine;

import Client.Client;
import Test.Exceptions.ClientDownException;
import Test.Utils.ServerUtils;
import Test.Utils.Printer;
import Test.Utils.Statuses.Status;
import Test.Utils.Statuses.StatusCounter;
import Test.Utils.Statuses.StatusParser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.*;

public class TestRunner {
    private final Client client;
    private final Test test;
    private final StatusCounter statusCounter;
    private Commander commands;

    public TestRunner(Test test) {
        this.test = test;
        client = ServerUtils.getClient();

        if (client == null) {
            Printer.printCriticalErrorAndExit(new ClientDownException());
        }

        statusCounter = new StatusCounter();
    }

    public String getTestName() {
        return test.getName();
    }

    public void run() {
        FileOutputStream statusStream;
        FileOutputStream resultStream;

        try {
            statusStream = new FileOutputStream(test.getStatusFile());
            resultStream = new FileOutputStream(test.getResultFile());
        } catch (FileNotFoundException e) {
            Printer.printError(e);
            return;
        }

        commands = new Commander(resultStream, client);

        for (String query : test.getQueries()) {
            if (commands.isFrameworkCommand(query)) {
                commands.parseFrameworkCommand(query);
                continue;
            }

            if (ServerUtils.serverDown()) {
                commands.parseFrameworkCommand(Commander.RESTART_SERVER_COMMAND);

                while (ServerUtils.serverDown()) {
                    Printer.printTestInfo("Waiting client-server up...");

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            String answer = communicate(query).trim();
            Status status = StatusParser.parse(query, answer);
            statusCounter.parse(status);

            if (commands.getPrintLevel() == Commander.PRINT_LEVEL.EXTENDED) {
                Printer.printTestQuery(query, answer.isEmpty() ? "empty" : answer);
            }

            if (!commands.isNoOutput()) {
                try {
                    statusStream.write(status.toString().concat("\n").getBytes());
                    resultStream.write((answer + "\n").getBytes());
                } catch (IOException e) {
                    Printer.printError(e);
                }
            }
        }

        client.disconnect();
    }

    public StatusCounter getStatusCounter() {
        return statusCounter;
    }

    private String communicate(String msg) {
        String result = "TimeoutException";
        final Duration timeout = Duration.ofSeconds(20);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        final Future<String> handler = executor.submit(() -> client.communicate(msg));

        try {
            result = handler.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            handler.cancel(true);
        } catch (InterruptedException | ExecutionException e) {
            Printer.printError(e);
        } finally {
            executor.shutdownNow();
        }

        if (result.equals("Connection lost") || result.equals("TimeoutException")) {
            commands.parseFrameworkCommand(Commander.RESTART_SERVER_COMMAND);
        }

        return result;
    }
}
