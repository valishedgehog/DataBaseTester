package Test.Engine;

import Test.Exceptions.ClientDownException;
import Test.Utils.ClientServerHelper;
import Test.Utils.Printer;
import Test.Utils.Statuses.Status;
import Test.Utils.Statuses.StatusCounter;
import Test.Utils.Statuses.StatusParser;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TesterRunner extends Thread {
    private final int clientId;
    private final Test test;
    private final StatusCounter statusCounter;

    public TesterRunner(Test test) {
        this.test = test;
        clientId = ClientServerHelper.registerClient();

        if (clientId == -1) {
            Printer.printCriticalErrorAndExit(new ClientDownException());
        }

        statusCounter = new StatusCounter();
    }

    public String getTestName() {
        return test.getName();
    }

    @Override
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

        Commander commands = new Commander(resultStream);

        for (String query : test.getQueries()) {
            if (commands.isFrameworkCommand(query)) {
                commands.parseFrameworkCommand(query);
                continue;
            }

            while (ClientServerHelper.serverDown()) {
                Printer.printTestInfo("Waiting client-server up...");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String answer = ClientServerHelper.communicate(clientId, query).trim();
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

        ClientServerHelper.disconnectClient(clientId);
    }

    public StatusCounter getStatusCounter() {
        return statusCounter;
    }
}
