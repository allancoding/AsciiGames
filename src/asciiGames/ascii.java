package asciiGames;
import java.io.Console;

public class ascii {
    public static final char emDash = 0x2500;
    public static final char rectangle = 0x2588;
    public static final char box = 0x25A0;

    public static class color {
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";
        public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
        public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
        public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
        public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
        public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
        public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
        public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
        public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    }

    public static class table {
        public static final char topCornerLeft = 0x250C;
        public static final char topCornerRight = 0x2510;
        public static final char bottomCornerLeft = 0x2514;
        public static final char bottomCornerRight = 0x2518;
        public static final char side = 0x2502;
        public static final char topT = 0x252C;
        public static final char bottomT = 0x2534;
        public static final char leftT = 0x251C;
        public static final char rightT = 0x2524;
        public static final char cross = 0x253C;
        public static final char horizontal = 0x2500;
        public static final char vertical = 0x2502;
    }

    public static void clear() {
        print("\033[H\033[2J");
    }

    public static void print(String message) {
        System.out.print(message);
    }

    public static void println(String message) {
        System.out.println(message);
    }

    public static void printerror(String error) {
        if (error != "") {
            println(color.ANSI_RED_BACKGROUND + color.ANSI_BLACK + error + color.ANSI_RESET + " ");
        }
    }

    public static void printRepeated(String character, int times, boolean newline) {
        for (int i = 0; i < times; i++) {
            if (newline && i == times - 1) {
                println(character);
            } else {
                print(character);
            }
        }
    }

    public static void wait(int milliseconds) throws InterruptedException{
        Thread.sleep(milliseconds);
    }

    public static void waitForEnter(String message, Object... args) {
        Console c = System.console();
        if (c != null) {
            if (message != null) {
                c.format(message, args);
            }
            c.format("\n");
            c.readLine();
        }
    }

    public static String toS(char string) {
        return string + "";
    }
}
    
