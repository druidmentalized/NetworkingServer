package utils;

import java.io.PrintWriter;

public class Utility {
    private Utility() {} //no instantiation possible

    public static void clearConsole(PrintWriter output) {
        for (int i = 0; i < 50; i++) {
            output.println();
        }
    }
}
