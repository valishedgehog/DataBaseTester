package Test;

import Test.Engine.Cli.TestCli;
import Test.Utils.ClientServerHelper;
import Test.Utils.Printer;

import java.io.PrintStream;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        PrintStream original = System.out;
        if (args.length > 0)
            System.setOut(Printer.getEmptyPrintStream());
        Printer.SYSTEM_OUT = System.out;

        boolean result = new TestCli().run(Arrays.copyOfRange(args, 0, args.length));

        if (args.length > 1) {
            System.setOut(original);
            System.out.println(result);
        }

        ClientServerHelper.stopServer();
    }

}
