package asciiGames.games;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import org.fusesource.jansi.AnsiConsole;
import asciiGames.ascii;

public class Battleship {
    private static int BOARD_SIZE = 11;
    private static int[][] human = new int[10][10];
    private static boolean allhumanshipsplaced = false;
    private static int[][] cpu = new int[10][10];
    private static String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
    private static boolean continuePlacement = true;
    private static int gamelevel = 1;
    private static boolean gamestart = false;
    private static boolean allhumanshipshavebeensunk = false;
    private static boolean allcpushipshavebeensunk = false;
    private static double hitsWeight = 0.36;
    private static double missesWeight = -0.05;
    private static double turnsWeight = -0.01;
    private static double shipsSunkWeight = 0.7;
    private static char hit = 0x2593;
    private static char sunk = 0x2592;
    private static char miss = 0x2248;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        Game.start(true, true);
    }

    public static class Game {
        public static final String Name = "Battleship";
        public static final String Description = "The classic game of Battleship.";
        public static boolean shutdownHookAdded = false;

        public static void start(boolean instructions, boolean main) {
            System.setProperty("file.encoding", "UTF-8");
            if (!shutdownHookAdded && main) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(200);
                            ascii.println("Exiting Battleship...");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                shutdownHookAdded = true;
            }
            if (instructions == true) {
                printInstructions();
            }
            cpurandomships();
            level("");
            humansetships(5, "");
            checkshipplacement();
            while (!allhumanshipshavebeensunk && !allcpushipshavebeensunk) {
                if (!allhumanshipshavebeensunk && !allcpushipshavebeensunk) {
                    humanhit("");
                }
                if (!allhumanshipshavebeensunk && !allcpushipshavebeensunk) {
                    cpuhit();
                }
            }
            end();
            ascii.waitForEnter("Press enter to continue to the main menu...");
        }
    
        public static void cheat() {
            String workingDirectory = System.getProperty("user.dir");
            String fileName = "cheat.txt";
            String filePath = workingDirectory + File.separator + fileName;
            try {
                try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
                    printcheatBoard(writer);
                }
            } catch (IOException e) {
                System.err.println("Error creating/writing the file: " + e.getMessage());
            }
        }
    
        public static void printInstructions() {
            ascii.clear();
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_CYAN_BACKGROUND + "Welcome to Battleship!" + ascii.color.ANSI_RESET + " ");
            printBoardLine(22, 3);
            ascii.println(ascii.color.ANSI_PURPLE_BACKGROUND + ascii.color.ANSI_BLACK + "Instructions:" + ascii.color.ANSI_RESET + " ");
            printBoardLine(11, 3);
            ascii.print(ascii.color.ANSI_WHITE);
            ascii.println("- The game is played on a 10x10 board.");
            ascii.println("- The goal of the game is to sink all of the enemy ships before they sink yours.");
            ascii.println("- You will choose where it put your ships with the starting coordinate and the direction you want it to go.");
            ascii.println("- You will have 5 ships to place on the board. They are 1x5, 1x4, 1x3, 1x3, and 1x2.");
            ascii.println("- You will play against a 10x10 board with 5 randomly placed ships on it.");
            ascii.println("  ^ You will play against the computer & it is not AI :( - I didn't have enough time, lol");
            ascii.println("- You will take turns guessing where the enemy ships are by entering the coordinate you want to hit.");
            ascii.println("- If you hit a ship, it will be marked with a " + hit + ".");
            ascii.println("- If you sink a ship, it will be marked with a " + sunk + ".");
            ascii.println("- If you miss a ship, it will be marked with a " + miss + ".");
            ascii.println("- Same goes for the computer. If they hit your ship, it will be marked with a " + hit + ", etc...");
            ascii.println("- A ships will be colors depending on what ship it is.");
            ascii.println("- The Aircraft Carrier will be " + ascii.color.ANSI_PURPLE + "purple" + ascii.color.ANSI_WHITE + ". " + ascii.color.ANSI_PURPLE + ascii.box + " " + ascii.box + " " + ascii.box + " " + ascii.box + " " + ascii.box + " " + ascii.color.ANSI_WHITE + " ");
            ascii.println("- The Battleship will be " + ascii.color.ANSI_CYAN + "cyan" + ascii.color.ANSI_WHITE + ". " + ascii.color.ANSI_CYAN + ascii.box + " " + ascii.box + " " + ascii.box + " " + ascii.box + " " + ascii.color.ANSI_WHITE + " ");
            ascii.println("- The Cruiser will be " + ascii.color.ANSI_YELLOW + "yellow" + ascii.color.ANSI_WHITE + ". " + ascii.color.ANSI_YELLOW + ascii.box + " " + ascii.box + " " + ascii.box + " " + ascii.color.ANSI_WHITE + " ");
            ascii.println("- The Submarine will be " + ascii.color.ANSI_GREEN + "green" + ascii.color.ANSI_WHITE + ". " + ascii.color.ANSI_GREEN + ascii.box + " " + ascii.box + " " + ascii.box + " " + ascii.color.ANSI_WHITE + " ");
            ascii.println("- The Destroyer will be " + ascii.color.ANSI_RED + "red" + ascii.color.ANSI_WHITE + ". " + ascii.color.ANSI_RED + ascii.box + " " + ascii.box + " " + ascii.color.ANSI_WHITE + " ");
            ascii.println("- If you hit all of the ships before the computer hits yours, you win!");
            printBoardLine(11, 3);
            ascii.println(ascii.color.ANSI_GREEN_BACKGROUND + ascii.color.ANSI_BLACK + "Good luck!" + ascii.color.ANSI_RESET + " ");
            ascii.waitForEnter(ascii.color.ANSI_YELLOW_BACKGROUND + ascii.color.ANSI_BLACK + "Press enter to continue..." + ascii.color.ANSI_RESET + " ");
        }
    
        public static void end() {
            if (allcpushipshavebeensunk) {
                ascii.clear();
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "You won!" + ascii.color.ANSI_RESET + " ");
                printBoardLine(22, 3);
                int nmisses = 0;
                int nhits = 0;
                int nhitsleft = 0;
                int nturns = 0;
                int[] nships = { 0, 0, 0, 0, 0 };
                int cpunhits = 0;
                int cpunmisses = 0;
                for (int i = 0; i < human.length; i++) {
                    for (int j = 0; j < human[i].length; j++) {
                        if (cpu[i][j] == 4) {
                            nmisses++;
                            nturns++;
                        } else if (cpu[i][j] >= 10) {
                            nhits++;
                            nturns++;
                        }
                        if (human[i][j] == 4) {
                            cpunmisses++;
                        } else if (human[i][j] >= 5 && human[i][j] <= 9) {
                            nhitsleft++;
                        } else if (human[i][j] >= 10 && human[i][j] <= 14) {
                            cpunhits++;
                        } else if (human[i][j] >= 15 && human[i][j] <= 19) {
                            nships[(human[i][j] - 15)] = 1;
                        }
                    }
                }
                int snships = 0;
                for (int i = 0; i < nships.length; i++) {
                    if (nships[i] == 1) {
                        snships++;
                    }
                }
                String hitstr = (nhits == 1) ? "hit" : "hits";
                String missstr = (nmisses == 1) ? "miss" : "misses";
                String shipstr = (snships == 1) ? "ship" : "ships";
                String turnstr = (nturns == 1) ? "turn" : "turns";
                String cpunhitstr = (cpunhits == 1) ? "hit" : "hits";
                int cpunturns = (int) ((double) nhitsleft / ((double) cpunhits / (double) (cpunhits + cpunmisses)));
                String cputurnstr = (cpunturns == 1) ? "turn" : "turns";
                double score = (((nhits * hitsWeight) + (nmisses * missesWeight) + (nturns * turnsWeight)
                        + (5 * shipsSunkWeight))
                        / ((17 * hitsWeight) + (0 * missesWeight) + (17 * turnsWeight) + (5 * shipsSunkWeight)) * 100);
                double rscore = Math.round(score * 100.0) / 100.0;
                double accuracy = ((double) nhits / (double) (nhits + nmisses)) * 100;
                double raccuracy = Math.round(accuracy * 100.0) / 100.0;
                ascii.println(ascii.color.ANSI_WHITE + "Your score was: " + rscore + "%" + ascii.color.ANSI_RESET + " ");
                ascii.println(ascii.color.ANSI_WHITE + "You had " + nmisses + " " + missstr + " and had " + nhits + " " + hitstr
                        + ", which is a accuracy of " + raccuracy + "%" + ascii.color.ANSI_RESET + " ");
                ascii.println(ascii.color.ANSI_WHITE + "You had " + nturns + " " + turnstr + ascii.color.ANSI_RESET + " ");
                ascii.println(ascii.color.ANSI_WHITE + "The computer had " + nhitsleft + " " + cpunhitstr
                        + " left to win, which would have taken about " + cpunturns + " " + cputurnstr + ascii.color.ANSI_RESET + " ");
                ascii.println(ascii.color.ANSI_WHITE + "The computer sunk " + snships + " " + shipstr + ascii.color.ANSI_RESET + " ");
                printBoardLine(22, 3);
                ascii.waitForEnter(ascii.color.ANSI_YELLOW_BACKGROUND + ascii.color.ANSI_BLACK + "Press enter to continue..." + ascii.color.ANSI_RESET + " ");
                playagain("");
            } else if (allhumanshipshavebeensunk) {
                ascii.clear();
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "You lost!" + ascii.color.ANSI_RESET + " ");
                printBoardLine(22, 3);
                int nmisses = 0;
                int nhits = 0;
                int nhitsleft = 0;
                int nturns = 0;
                int[] nships = { 0, 0, 0, 0, 0 };
                for (int i = 0; i < cpu.length; i++) {
                    for (int j = 0; j < cpu[i].length; j++) {
                        if (cpu[i][j] == 4) {
                            nmisses++;
                            nturns++;
                        } else if (cpu[i][j] >= 10) {
                            if (cpu[i][j] >= 15 && cpu[i][j] <= 19) {
                                nships[(cpu[i][j] - 15)] = 1;
                            }
                            nhits++;
                            nturns++;
                        } else if (cpu[i][j] >= 5 && cpu[i][j] <= 9) {
                            nhitsleft++;
                        }
                    }
                }
                int snships = 0;
                for (int i = 0; i < nships.length; i++) {
                    if (nships[i] == 1) {
                        snships++;
                    }
                }
                String hitstr = (nhits == 1) ? "hit" : "hits";
                String missstr = (nmisses == 1) ? "miss" : "misses";
                String shipstr = (snships == 1) ? "ship" : "ships";
                String turnstr = (nturns == 1) ? "turn" : "turns";
                int turnsleft = (int) ((double) nhitsleft / ((double) nhits / (double) (nhits + nmisses)));
                String turnsleftstr = (turnsleft == 1) ? "turn" : "turns";
                double score = (((nhits * hitsWeight) + (nmisses * missesWeight) + (nturns * turnsWeight)
                        + (snships * shipsSunkWeight))
                        / ((17 * hitsWeight) + (0 * missesWeight) + (17 * turnsWeight) + (5 * shipsSunkWeight)) * 100);
                double rscore = Math.round(score * 100.0) / 100.0;
                double accuracy = ((double) nhits / (double) (nhits + nmisses)) * 100;
                double raccuracy = Math.round(accuracy * 100.0) / 100.0;
                ascii.println(ascii.color.ANSI_WHITE + "Your score was: " + rscore + "%" + ascii.color.ANSI_RESET + " ");
                ascii.println(ascii.color.ANSI_WHITE + "You had " + nmisses + " " + missstr + " and had " + nhits + " " + hitstr
                        + ", which is a accuracy of " + raccuracy + "%" + ascii.color.ANSI_RESET + " ");
                ascii.println(ascii.color.ANSI_WHITE + "You had " + nturns + " " + turnstr + ascii.color.ANSI_RESET + " ");
                ascii.println(ascii.color.ANSI_WHITE + "You had " + nhitsleft + " " + hitstr
                        + " left to win, which would have taken you about " + turnsleft + " " + turnsleftstr + ascii.color.ANSI_RESET
                        + " ");
                ascii.println(ascii.color.ANSI_WHITE + "You sunk " + snships + " " + shipstr + ascii.color.ANSI_RESET + " ");
                printBoardLine(22, 3);
                ascii.waitForEnter(ascii.color.ANSI_YELLOW_BACKGROUND + ascii.color.ANSI_BLACK + "Press enter to continue..." + ascii.color.ANSI_RESET + " ");
                playagain("");
            }
        }
    
        public static void playagain(String error) {
            ascii.clear();
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_PURPLE_BACKGROUND + "Do you want to play again?" + ascii.color.ANSI_RESET + " ");
            printBoardLine(22, 3);
            ascii.println(ascii.color.ANSI_GREEN_BACKGROUND + ascii.color.ANSI_BLACK + "1. Yes " + ascii.color.ANSI_RESET + " ");
            ascii.println(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "2. No " + ascii.color.ANSI_RESET + " ");
            printBoardLine(11, 3);
            if (error != "") {
                ascii.println(error);
            }
            int start = 0;
            try {
                start = Integer.parseInt(System.console().readLine());
            } catch (Exception e) {
                start = 0;
            }
            if (start == 1) {
                ascii.clear();
                reset();
                start(false, true);
            } else if (start == 2) {
                ascii.clear();
                ascii.println(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Bye Bye..." + ascii.color.ANSI_RESET + " ");
                System.exit(0);
            } else {
                playagain(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Invalid input. Try again." + ascii.color.ANSI_RESET + " ");
            }
        }
    
        public static void reset() {
            gamestart = false;
            allhumanshipsplaced = false;
            allhumanshipshavebeensunk = false;
            allcpushipshavebeensunk = false;
            human = new int[10][10];
            cpu = new int[10][10];
            for (int i = 0; i < human.length; i++) {
                for (int j = 0; j < human[i].length; j++) {
                    human[i][j] = 0;
                    cpu[i][j] = 0;
                }
            }
        }
    
        public static void cpuhit() {
            printBoard("human");
            ascii.println(ascii.color.ANSI_BLACK +  ascii.color.ANSI_PURPLE_BACKGROUND + "The computer is thinking..." + ascii.color.ANSI_RESET + " ");
            try {
                Thread.sleep((int) (Math.random() * 1500) + 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            printBoardLine(22, 3);
            int hitrate = -1;
            if (gamelevel == 1) {
                hitrate = (Math.random() < 0.10) ? 1 : 0;
            } else if (gamelevel == 2) {
                hitrate = (Math.random() < 0.25) ? 1 : 0;
            } else if (gamelevel == 3) {
                hitrate = (Math.random() < 0.40) ? 1 : 0;
            } else if (gamelevel == 4) {
                hitrate = (Math.random() < 0.95) ? 1 : 0;
            }
            int shipcore = 0;
            int typerow = 0;
            int typecol = 0;
            if (hitrate == 0) {
                int row = (int) (Math.random() * 10);
                int col = (int) (Math.random() * 10);
                while (human[row][col] != 0) {
                    row = (int) (Math.random() * 10);
                    col = (int) (Math.random() * 10);
                }
                shipcore = human[row][col];
                typerow = row;
                typecol = col;
            } else if (hitrate == 1) {
                int row = (int) (Math.random() * 10);
                int col = (int) (Math.random() * 10);
                while (!(human[row][col] >= 5 && human[row][col] <= 9)) {
                    row = (int) (Math.random() * 10);
                    col = (int) (Math.random() * 10);
                }
                shipcore = human[row][col];
                typerow = row;
                typecol = col;
            }
            int type = 0;
            if (shipcore == 0) {
                type = 1;
                human[typerow][typecol] = 4;
            } else if (shipcore == 4) {
                type = 10;
                human[typerow][typecol] = 11;
            } else if (shipcore == 5) {
                type = 9;
                human[typerow][typecol] = 10;
                if (checksunk(5, "human") == true) {
                    type = 3;
                    sinkship(15, "human");
                }
            } else if (shipcore == 6) {
                type = 10;
                human[typerow][typecol] = 11;
                if (checksunk(6, "human") == true) {
                    type = 4;
                    sinkship(16, "human");
                }
            } else if (shipcore == 7) {
                type = 11;
                human[typerow][typecol] = 12;
                if (checksunk(7, "human") == true) {
                    type = 5;
                    sinkship(17, "human");
                }
            } else if (shipcore == 8) {
                type = 12;
                human[typerow][typecol] = 13;
                if (checksunk(8, "human") == true) {
                    type = 6;
                    sinkship(18, "human");
                }
            } else if (shipcore == 9) {
                type = 13;
                human[typerow][typecol] = 14;
                if (checksunk(9, "human") == true) {
                    type = 7;
                    sinkship(19, "human");
                }
            }
            if (checksunk(5, "human") == true && checksunk(6, "human") == true && checksunk(7, "human") == true
                    && checksunk(8, "human") == true && checksunk(9, "human") == true) {
                allhumanshipshavebeensunk = true;
                type = 8;
            }
            printBoard("human");
            String cpucore = letters[typecol] + (typerow + 1);
            if (type == 1) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "The computer missed at " + cpucore + "!" + ascii.color.ANSI_RESET + " ");
            } else if (type == 3) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer sunk the Aircraft Carrier at " + cpucore + "!"
                        + ascii.color.ANSI_RESET + " ");
            } else if (type == 4) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer sunk the Battleship at " + cpucore + "!"
                        + ascii.color.ANSI_RESET + " ");
            } else if (type == 5) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer sunk the Cruiser at " + cpucore + "!" + ascii.color.ANSI_RESET
                        + " ");
            } else if (type == 6) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer sunk the Submarine at " + cpucore + "!"
                        + ascii.color.ANSI_RESET + " ");
            } else if (type == 7) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer sunk the Destroyer at " + cpucore + "!"
                        + ascii.color.ANSI_RESET + " ");
            } else if (type == 8) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer sunk all of the ships!" + ascii.color.ANSI_RESET + " ");
            } else if (type == 9) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer hit your Aircraft Carrier at " + cpucore + "!"
                        + ascii.color.ANSI_RESET + " ");
            } else if (type == 10) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer hit your Battleship at " + cpucore + "!"
                        + ascii.color.ANSI_RESET + " ");
            } else if (type == 11) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer hit your Cruiser at " + cpucore + "!" + ascii.color.ANSI_RESET
                        + " ");
            } else if (type == 12) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer hit your Submarine at " + cpucore + "!"
                        + ascii.color.ANSI_RESET + " ");
            } else if (type == 13) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "The computer hit your Destroyer at " + cpucore + "!"
                        + ascii.color.ANSI_RESET + " ");
            }
            ascii.waitForEnter(ascii.color.ANSI_YELLOW_BACKGROUND + ascii.color.ANSI_BLACK + "Press enter to continue..." + ascii.color.ANSI_RESET + " ");
        }
    
        public static void humanhit(String error) {
            printBoard("cpu", true);
            ascii.println(ascii.color.ANSI_BLACK +  ascii.color.ANSI_PURPLE_BACKGROUND + "Where do you want to hit?" + ascii.color.ANSI_RESET + " ");
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "Enter the coordinate you want to hit (ex. A1):" + ascii.color.ANSI_RESET
                    + " ");
            printBoardLine(11, 3);
            if (error != "") {
                ascii.println(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + error + ascii.color.ANSI_RESET + " ");
            }
            String coordinate = System.console().readLine();
            if (!(coordinate.length() == 2 || (coordinate.length() == 3 && coordinate.substring(1, 3).equals("10")))) {
                humanhit("Coordinate does not exist. Try again.");
                return;
            }
            String colPart = coordinate.substring(0, 1).toUpperCase();
            int row = 0;
            int col = 0;
            try {
                row = Integer.parseInt(coordinate.substring(1));
            } catch (NumberFormatException e) {
                humanhit("Invalid Number Coordinate. Try again.");
                return;
            }
            boolean validColChar = false;
            for (int i = 0; i < letters.length; i++) {
                if (letters[i].equals(colPart)) {
                    col = i;
                    validColChar = true;
                    break;
                }
            }
            if (!validColChar) {
                humanhit("Invalid Letter Coordinate. Try again.");
                return;
            }
            if (row < 1 || row > 10) {
                humanhit("Invalid Coordinate. Try again.");
                return;
            }
            row -= 1;
            int shipcore = cpu[row][col];
            int type = 0;
            if (shipcore == 0) {
                cpu[row][col] = 4;
                type = 1;
            } else if (shipcore == 2) {
                type = 9;
            } else if (shipcore == 4) {
                type = 10;
            } else if (shipcore == 5) {
                cpu[row][col] = 10;
                type = 2;
                if (checksunk(5, "cpu") == true) {
                    type = 3;
                    sinkship(15, "cpu");
                }
            } else if (shipcore == 6) {
                cpu[row][col] = 11;
                type = 2;
                if (checksunk(6, "cpu") == true) {
                    type = 4;
                    sinkship(16, "cpu");
                }
            } else if (shipcore == 7) {
                cpu[row][col] = 12;
                type = 2;
                if (checksunk(7, "cpu") == true) {
                    type = 5;
                    sinkship(17, "cpu");
                }
            } else if (shipcore == 8) {
                cpu[row][col] = 13;
                type = 2;
                if (checksunk(8, "cpu") == true) {
                    type = 6;
                    sinkship(18, "cpu");
                }
            } else if (shipcore == 9) {
                cpu[row][col] = 14;
                type = 2;
                if (checksunk(9, "cpu") == true) {
                    type = 7;
                    sinkship(19, "cpu");
                }
            } else {
                type = 9;
            }
            if (checksunk(5, "cpu") == true && checksunk(6, "cpu") == true && checksunk(7, "cpu") == true
                    && checksunk(8, "cpu") == true && checksunk(9, "cpu") == true) {
                allcpushipshavebeensunk = true;
                type = 8;
            }
            printBoard("cpu", true);
            String humancore = letters[col] + (row + 1);
            if (type == 1) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "You missed at " + humancore + "!" + ascii.color.ANSI_RESET + " ");
            } else if (type == 2) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "You hit a ship at " + humancore + "!" + ascii.color.ANSI_RESET + " ");
            } else if (type == 3) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "You sunk the Aircraft Carrier at " + humancore + "!"
                        + ascii.color.ANSI_RESET + " ");
            } else if (type == 4) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "You sunk the Battleship at " + humancore + "!" + ascii.color.ANSI_RESET
                        + " ");
            } else if (type == 5) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "You sunk the Cruiser at " + humancore + "!" + ascii.color.ANSI_RESET
                        + " ");
            } else if (type == 6) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "You sunk the Submarine at " + humancore + "!" + ascii.color.ANSI_RESET
                        + " ");
            } else if (type == 7) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "You sunk the Destroyer at " + humancore + "!" + ascii.color.ANSI_RESET
                        + " ");
            } else if (type == 8) {
                ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "You sunk all of the computers ships!" + ascii.color.ANSI_RESET + " ");
            } else if (type == 9) {
                humanhit(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "You already hit that ship at " + humancore + "!" + ascii.color.ANSI_RESET
                        + " ");
            } else if (type == 10) {
                humanhit(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "You already missed at " + humancore + "!" + ascii.color.ANSI_RESET + " ");
            }
            ascii.waitForEnter(ascii.color.ANSI_YELLOW_BACKGROUND + ascii.color.ANSI_BLACK + "Press enter to continue..." + ascii.color.ANSI_RESET + " ");
        }
    
        public static void sinkship(int type, String player) {
            int[][] arrays;
            if (player.equals("human")) {
                arrays = human;
            } else if (player.equals("cpu")) {
                arrays = cpu;
            } else {
                return;
            }
            int shiptype = type - 5;
            for (int i = 0; i < arrays.length; i++) {
                for (int j = 0; j < arrays[i].length; j++) {
                    if (arrays[i][j] == shiptype) {
                        arrays[i][j] = type;
                    }
                }
            }
        }
    
        public static boolean checksunk(int type, String player) {
            int[][] arrays;
            if (player.equals("human")) {
                arrays = human;
            } else if (player.equals("cpu")) {
                arrays = cpu;
            } else {
                return false;
            }
            for (int i = 0; i < arrays.length; i++) {
                for (int j = 0; j < arrays[i].length; j++) {
                    if (arrays[i][j] == type) {
                        return false;
                    }
                }
            }
            return true;
        }
    
        public static void startgame(String error, int start) {
            ascii.clear();
            ascii.println(ascii.color.ANSI_BLACK +  ascii.color.ANSI_PURPLE_BACKGROUND + "Are you ready to start?" + ascii.color.ANSI_RESET + " ");
            printBoardLine(22, 3);
            if (gamelevel == 1) {
                ascii.println(ascii.color.ANSI_CYAN_BACKGROUND + ascii.color.ANSI_BLACK + "Difficulty:" + ascii.color.ANSI_RESET + " " + ascii.color.ANSI_BLACK
                        + ascii.color.ANSI_GREEN_BACKGROUND + "Easy" + ascii.color.ANSI_RESET);
            } else if (gamelevel == 2) {
                ascii.println(ascii.color.ANSI_CYAN_BACKGROUND + ascii.color.ANSI_BLACK + "Difficulty:" + ascii.color.ANSI_RESET + " " + ascii.color.ANSI_BLACK
                        + ascii.color.ANSI_BLUE_BACKGROUND + "Medium" + ascii.color.ANSI_RESET);
            } else if (gamelevel == 3) {
                ascii.println(ascii.color.ANSI_CYAN_BACKGROUND + ascii.color.ANSI_BLACK + "Difficulty:" + ascii.color.ANSI_RESET + " " + ascii.color.ANSI_BLACK
                        + ascii.color.ANSI_YELLOW_BACKGROUND + "Hard" + ascii.color.ANSI_RESET);
            } else if (gamelevel == 4) {
                ascii.println(ascii.color.ANSI_CYAN_BACKGROUND + ascii.color.ANSI_BLACK + "Difficulty:" + ascii.color.ANSI_RESET + " " + ascii.color.ANSI_BLACK
                        + ascii.color.ANSI_RED_BACKGROUND + "Impossible" + ascii.color.ANSI_RESET);
            }
            printBoardLine(11, 3);
            ascii.println(ascii.color.ANSI_GREEN_BACKGROUND + ascii.color.ANSI_BLACK + "1. Yes " + ascii.color.ANSI_RESET + " ");
            ascii.println(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "2. No " + ascii.color.ANSI_RESET + " ");
            printBoardLine(11, 3);
            if (error != "") {
                ascii.println(error);
            }
            if (start != 2) {
                try {
                    start = Integer.parseInt(System.console().readLine());
                } catch (Exception e) {
                    start = 0;
                }
            }
            if (start == 1) {
                ascii.println(ascii.color.ANSI_GREEN_BACKGROUND + ascii.color.ANSI_BLACK + "Let's go!" + ascii.color.ANSI_RESET + " ");
            } else if (start == 2) {
                ascii.clear();
                ascii.println( ascii.color.ANSI_PURPLE_BACKGROUND + ascii.color.ANSI_BLACK + "Do you want to change the level?" + ascii.color.ANSI_RESET + " ");
                printBoardLine(22, 3);
                ascii.println(ascii.color.ANSI_GREEN_BACKGROUND + ascii.color.ANSI_BLACK + "1. Yes " + ascii.color.ANSI_RESET + " ");
                ascii.println(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "2. No " + ascii.color.ANSI_RESET + " ");
                printBoardLine(11, 3);
                if (error != "") {
                    ascii.println(error);
                }
                int start2;
                try {
                    start2 = Integer.parseInt(System.console().readLine());
                } catch (Exception e) {
                    start2 = 0;
                }
                if (start2 == 1) {
                    level("");
                } else if (start2 == 2) {
                    ascii.clear();
                    ascii.println(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Bye Bye..." + ascii.color.ANSI_RESET + " ");
                    System.exit(0);
                } else {
                    startgame(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Invalid input. Try again." + ascii.color.ANSI_RESET + " ", 2);
                }
            } else {
                startgame(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Invalid input. Try again." + ascii.color.ANSI_RESET + " ", 0);
            }
            if (gamestart == false) {
                gamestart = true;
                ascii.waitForEnter(ascii.color.ANSI_YELLOW_BACKGROUND + ascii.color.ANSI_BLACK + "Press enter to continue..." + ascii.color.ANSI_RESET + " ");
            }
        }
    
        public static void level(String error) {
            ascii.clear();
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_CYAN_BACKGROUND + "Please choose a level:" + ascii.color.ANSI_RESET + " ");
            printBoardLine(22, 3);
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "1. Easy " + ascii.color.ANSI_RESET + " ");
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_BLUE_BACKGROUND + "2. Medium " + ascii.color.ANSI_RESET + " ");
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_YELLOW_BACKGROUND + "3. Hard " + ascii.color.ANSI_RESET + " ");
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_RED_BACKGROUND + "4. Impossible " + ascii.color.ANSI_RESET + " ");
            printBoardLine(11, 3);
            if (error != "") {
                ascii.println(error);
            }
            String start = "";
            start = System.console().readLine();
            int startinput = 0;
            try {
                if (start.endsWith(";")) {
                    try {
                        startinput = Integer.parseInt(start.substring(0, start.length() - 1));
                    } catch (Exception e) {
                        level(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Invalid input. Try again." + ascii.color.ANSI_RESET + " ");
                    }
                    if (startinput != 0) {
                        gamelevel = startinput;
                        cheat();
                        startgame("", 0);
                    }
                } else {
                    try {
                        startinput = Integer.parseInt(start);
                    } catch (Exception e) {
                        level(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Invalid input. Try again." + ascii.color.ANSI_RESET + " ");
                    }
                    if (startinput != 0) {
                        gamelevel = startinput;
                        startgame("", 0);
                    }
                }
            } catch (Exception e) {
                level(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Invalid input. Try again." + ascii.color.ANSI_RESET + " ");
            }
        }
    
        public static void humansetships(int type, String error) {
            if (!allhumanshipsplaced) {
                String start = "";
                String dir = "";
                printBoard("human");
                ascii.printerror(error);
                if (type == 9) {
                    continuePlacement = false;
                }
                if (continuePlacement) {
                    if (type == 5) {
                        ascii.println(ascii.color.ANSI_PURPLE_BACKGROUND + ascii.color.ANSI_BLACK + "Aircraft Carrier" + ascii.color.ANSI_RESET + " ");
                        ascii.println("Enter the starting coordinate of the ship (ex. A1):");
                        start = System.console().readLine();
                        ascii.println("Enter the direction of the ship (ex. up, down, left, right):");
                        dir = System.console().readLine();
                        setships(start, dir, 5, type);
                        printBoard("human");
                        humansetships(6, "");
                    } else if (type == 6) {
                        ascii.println(ascii.color.ANSI_CYAN_BACKGROUND + ascii.color.ANSI_BLACK + "Battleship" + ascii.color.ANSI_RESET + " ");
                        ascii.println("Enter the starting coordinate of the ship (ex. A1):");
                        start = System.console().readLine();
                        ascii.println("Enter the direction of the ship (ex. up, down, left, right):");
                        dir = System.console().readLine();
                        setships(start, dir, 4, type);
                        printBoard("human");
                        humansetships(7, "");
                    } else if (type == 7) {
                        ascii.println(ascii.color.ANSI_YELLOW_BACKGROUND + ascii.color.ANSI_BLACK + "Cruiser" + ascii.color.ANSI_RESET + " ");
                        ascii.println("Enter the starting coordinate of the ship (ex. A1):");
                        start = System.console().readLine();
                        ascii.println("Enter the direction of the ship (ex. up, down, left, right):");
                        dir = System.console().readLine();
                        setships(start, dir, 3, type);
                        printBoard("human");
                        humansetships(8, "");
                    } else if (type == 8) {
                        ascii.println(ascii.color.ANSI_GREEN_BACKGROUND + ascii.color.ANSI_BLACK + "Submarine" + ascii.color.ANSI_RESET + " ");
                        ascii.println("Enter the starting coordinate of the ship (ex. A1):");
                        start = System.console().readLine();
                        ascii.println("Enter the direction of the ship (ex. up, down, left, right):");
                        dir = System.console().readLine();
                        setships(start, dir, 3, type);
                        printBoard("human");
                        humansetships(9, "");
                    }
                }
    
                if (type == 9) {
                    ascii.println(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Destroyer" + ascii.color.ANSI_RESET + " ");
                    ascii.println("Enter the starting coordinate of the ship (ex. A1):");
                    start = System.console().readLine();
                    ascii.println("Enter the direction of the ship (ex. up, down, left, right):");
                    dir = System.console().readLine();
                    setships(start, dir, 2, type);
                    printBoard("human");
                    allhumanshipsplaced = true;
                }
            }
        }
    
        public static void checkshipplacement() {
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_GREEN_BACKGROUND + "You have placed all of your ships!" + ascii.color.ANSI_RESET + " ");
            printBoardLine(22, 3);
            ascii.println(ascii.color.ANSI_BLACK + ascii.color.ANSI_PURPLE_BACKGROUND + "Do you want to start the game?" + ascii.color.ANSI_RESET + " ");
            printBoardLine(22, 3);
            ascii.println(ascii.color.ANSI_GREEN_BACKGROUND + ascii.color.ANSI_BLACK + "1. Yes " + ascii.color.ANSI_RESET + " ");
            ascii.println(ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "2. No (Reset)" + ascii.color.ANSI_RESET + " ");
            printBoardLine(11, 3);
            int start = 0;
            try {
                start = Integer.parseInt(System.console().readLine());
            } catch (Exception e) {
                start = 0;
            }
            if (start == 2) {
                ascii.clear();
                reset();
                start(false, true);
            } else {
                humansetships(10, ascii.color.ANSI_RED_BACKGROUND + ascii.color.ANSI_BLACK + "Invalid input. Try again." + ascii.color.ANSI_RESET + " ");
            }
        }
    
        public static void setships(String start, String dirr, int length, int type) {
            if (!allhumanshipsplaced) {
                if (!(start.length() == 2 || (start.length() == 3 && start.substring(1, 3).equals("10")))) {
                    humansetships(type, "Coordinate does not exist. Try again.");
                    return;
                }
                if (!dirr.equalsIgnoreCase("up") && !dirr.equalsIgnoreCase("down") &&
                        !dirr.equalsIgnoreCase("left") && !dirr.equalsIgnoreCase("right")) {
                    humansetships(type, "Invalid Direction. Try again.");
                    return;
                }
                dirr = dirr.toLowerCase();
                String colPart = start.substring(0, 1).toUpperCase();
                int row = 0;
                int col = 0;
                try {
                    row = Integer.parseInt(start.substring(1));
                } catch (NumberFormatException e) {
                    humansetships(type, "Invalid Number Coordinate. Try again.");
                    return;
                }
                boolean validColChar = false;
                for (int i = 0; i < letters.length; i++) {
                    if (letters[i].equals(colPart)) {
                        col = i;
                        validColChar = true;
                        break;
                    }
                }
                if (!validColChar) {
                    humansetships(type, "Invalid Letter Coordinate. Try again.");
                    return;
                }
                if (row < 1 || row > 10) {
                    humansetships(type, "Invalid Coordinate. Try again.");
                    return;
                }
                row -= 1;
                int dir = 0;
                if (dirr.equals("up")) {
                    dir = 0;
                } else if (dirr.equals("down")) {
                    dir = 2;
                } else if (dirr.equals("left")) {
                    dir = 3;
                } else if (dirr.equals("right")) {
                    dir = 1;
                }
                boolean placed = false;
                while (!placed) {
                    boolean canPlace = true;
                    for (int j = 0; j < length; j++) {
                        int newRow = row, newCol = col;
                        if (dir == 0)
                            newRow -= j;
                        else if (dir == 1)
                            newCol += j;
                        else if (dir == 2)
                            newRow += j;
                        else if (dir == 3)
                            newCol -= j;
                        if (newRow < 0 || newRow >= 10 || newCol < 0 || newCol >= 10 || human[newRow][newCol] != 0) {
                            canPlace = false;
                            break;
                        }
                    }
                    if (canPlace) {
                        for (int j = 0; j < length; j++) {
                            int newRow = row, newCol = col;
                            if (dir == 0)
                                newRow -= j;
                            else if (dir == 1)
                                newCol += j;
                            else if (dir == 2)
                                newRow += j;
                            else if (dir == 3)
                                newCol -= j;
                            human[newRow][newCol] = type;
                        }
                        placed = true;
                    } else {
                        humansetships(type, "Invalid Placement. Try again.");
                        return;
                    }
                }
            }
        }
    
        public static void cpurandomships() {
            int[] ships = { 5, 4, 3, 3, 2 };
            for (int i = 0; i < ships.length; i++) {
                boolean placed = false;
                while (!placed) {
                    int row = (int) (Math.random() * 10);
                    int col = (int) (Math.random() * 10);
                    int dir = (int) (Math.random() * 4);
                    boolean canPlace = true;
                    for (int j = 0; j < ships[i]; j++) {
                        int newRow = row, newCol = col;
                        if (dir == 0)
                            newRow -= j;
                        else if (dir == 1)
                            newCol += j;
                        else if (dir == 2)
                            newRow += j;
                        else if (dir == 3)
                            newCol -= j;
                        if (newRow < 0 || newRow >= 10 || newCol < 0 || newCol >= 10 || cpu[newRow][newCol] != 0) {
                            canPlace = false;
                            break;
                        }
                    }
                    if (canPlace) {
                        for (int j = 0; j < ships[i]; j++) {
                            int newRow = row, newCol = col;
                            if (dir == 0)
                                newRow -= j;
                            else if (dir == 1)
                                newCol += j;
                            else if (dir == 2)
                                newRow += j;
                            else if (dir == 3)
                                newCol -= j;
                            cpu[newRow][newCol] = i + 5;
                        }
                        placed = true;
                    }
                }
            }
        }
    
        public static void printBoardLine(int n, int type) {
            ascii.print(ascii.color.ANSI_BLACK);
            for (int i = 0; i < n; i++) {
                if (type == 0) {
                    if (i == 0) {
                        ascii.print(ascii.table.topCornerLeft + "" + ascii.emDash + "" + ascii.emDash);
                    }
                    if (i == (n - 1)) {
                        ascii.println(ascii.emDash + "" + ascii.emDash + "" + ascii.table.topCornerRight);
                    } else {
                        ascii.print(ascii.emDash + "" + ascii.emDash + "" + ascii.table.topT + "" + ascii.emDash);
                    }
                } else if (type == 1) {
                    if (i == 0) {
                        ascii.print(ascii.table.leftT + "" + ascii.emDash + "" + ascii.emDash);
                    }
                    if (i == (n - 1)) {
                        ascii.println(ascii.emDash + "" + ascii.emDash + "" + ascii.table.rightT);
                    } else {
                        ascii.print(ascii.emDash + "" + ascii.emDash + "" + ascii.table.cross + "" + ascii.emDash);
                    }
                } else if (type == 2) {
                    if (i == 0) {
                        ascii.print(ascii.table.bottomCornerLeft + "" + ascii.emDash + "" + ascii.emDash);
                    }
                    if (i == (n - 1)) {
                        ascii.println(ascii.emDash + "" + ascii.emDash + "" + ascii.table.bottomCornerRight);
                    } else {
                        ascii.print(ascii.emDash + "" + ascii.emDash + "" + ascii.table.bottomT + "" + ascii.emDash);
                    }
                } else if (type == 3) {
                    ascii.print(ascii.color.ANSI_WHITE);
                    if (i == (n - 1)) {
                        ascii.println(ascii.emDash + "" + ascii.emDash);
                    } else {
                        ascii.print(ascii.emDash + "" + ascii.emDash);
                    }
                }
            }
        }
    
        private static void printData(int row, int col, String player, boolean hidden) {
            if (row == 0) {
                if (col == 0) {
                    ascii.print("  ");
                } else {
                    ascii.print(letters[col - 1]);
                }
            } else if (col == 0) {
                if (row == 0) {
                    ascii.print(" ");
                } else if (row == 10) {
                    ascii.print(row + "");
                } else {
                    ascii.print(" " + row);
                }
            } else {
                printChar(row, col, player, hidden);
            }
        }
    
        private static void printChar(int row, int col, String player, boolean hidden) {
            int[][] arrays;
            if (player == "human") {
                arrays = human;
            } else {
                arrays = cpu;
            }
            String shipout = "";
            if (arrays[row - 1][col - 1] == 0) {
                shipout = " ";
            } else if (arrays[row - 1][col - 1] == 1) {
                if (hidden == true) {
                    shipout = " ";
                } else {
                    shipout = ascii.color.ANSI_BLACK + ascii.rectangle;
                }
            } else if (arrays[row - 1][col - 1] == 2) {
                shipout = hit + "";
            } else if (arrays[row - 1][col - 1] == 3) {
                shipout = sunk + "";
            } else if (arrays[row - 1][col - 1] == 4) {
                shipout = miss + "";
            } else if (arrays[row - 1][col - 1] == 5) {
                if (hidden == true) {
                    shipout = " ";
                } else {
                    shipout = ascii.color.ANSI_PURPLE + ascii.rectangle;
                }
            } else if (arrays[row - 1][col - 1] == 6) {
                if (hidden == true) {
                    shipout = " ";
                } else {
                    shipout = ascii.color.ANSI_CYAN + ascii.rectangle;
                }
            } else if (arrays[row - 1][col - 1] == 7) {
                if (hidden == true) {
                    shipout = " ";
                } else {
                    shipout = ascii.color.ANSI_YELLOW + ascii.rectangle;
                }
            } else if (arrays[row - 1][col - 1] == 8) {
                if (hidden == true) {
                    shipout = " ";
                } else {
                    shipout = ascii.color.ANSI_GREEN + ascii.rectangle;
                }
            } else if (arrays[row - 1][col - 1] == 9) {
                if (hidden == true) {
                    shipout = " ";
                } else {
                    shipout = ascii.color.ANSI_RED + ascii.rectangle;
                }
            } else if (arrays[row - 1][col - 1] == 10) {
                if (hidden == true) {
                    shipout = hit + "";
                } else {
                    shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_PURPLE + hit + ascii.color.ANSI_BLUE_BACKGROUND;
                }
            } else if (arrays[row - 1][col - 1] == 11) {
                if (hidden == true) {
                    shipout = hit + "";
                } else {
                    shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_CYAN + hit + ascii.color.ANSI_BLUE_BACKGROUND;
                }
            } else if (arrays[row - 1][col - 1] == 12) {
                if (hidden == true) {
                    shipout = hit + "";
                } else {
                    shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_YELLOW + hit + ascii.color.ANSI_BLUE_BACKGROUND;
                }
            } else if (arrays[row - 1][col - 1] == 13) {
                if (hidden == true) {
                    shipout = hit + "";
                } else {
                    shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_GREEN + hit + ascii.color.ANSI_BLUE_BACKGROUND;
                }
            } else if (arrays[row - 1][col - 1] == 14) {
                if (hidden == true) {
                    shipout = hit + "";
                } else {
                    shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_RED + hit + ascii.color.ANSI_BLUE_BACKGROUND;
                }
            } else if (arrays[row - 1][col - 1] == 15) {
                shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_PURPLE + sunk + ascii.color.ANSI_BLUE_BACKGROUND;
            } else if (arrays[row - 1][col - 1] == 16) {
                shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_CYAN + sunk + ascii.color.ANSI_BLUE_BACKGROUND;
            } else if (arrays[row - 1][col - 1] == 17) {
                shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_YELLOW + sunk + ascii.color.ANSI_BLUE_BACKGROUND;
            } else if (arrays[row - 1][col - 1] == 18) {
                shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_GREEN + sunk + ascii.color.ANSI_BLUE_BACKGROUND;
            } else if (arrays[row - 1][col - 1] == 19) {
                shipout = ascii.color.ANSI_RESET + ascii.color.ANSI_RED + sunk + ascii.color.ANSI_BLUE_BACKGROUND;
            }
            ascii.print(shipout + ascii.color.ANSI_BLACK);
        }
    
        private static void printBoard(String player, boolean... hidden) {
            ascii.clear();
            boolean hide = false;
            for (boolean h : hidden) {
                if (h) {
                    hide = true;
                    break;
                }
            }
            if (player == "human") {
                ascii.println(ascii.color.ANSI_GREEN + "Your Board:" + ascii.color.ANSI_RESET);
            } else if (player == "cpu") {
                ascii.println(ascii.color.ANSI_RED + "Computer's Board:" + ascii.color.ANSI_RESET);
            }
            ascii.print(ascii.color.ANSI_BLUE_BACKGROUND);
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (i == 0) {
                    printBoardLine(11, 0);
                }
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (j == 0) {
                        ascii.print(ascii.table.side + " ");
                    } else {
                        ascii.print(" " + ascii.table.side + " ");
                    }
                    printData(i, j, player, hide);
                    if (j == (BOARD_SIZE - 1)) {
                        ascii.print(" " + ascii.table.side);
                    }
                }
                ascii.println("");
                if (i == (BOARD_SIZE - 1)) {
                    printBoardLine(11, 2);
                } else if (i < BOARD_SIZE) {
                    printBoardLine(11, 1);
                }
            }
            ascii.print(ascii.color.ANSI_RESET);
        }
    
        private static void printcheatBoard(PrintWriter writer) {
            ascii.clear();
            writer.println("This is the cheat file for Battleship!");
            printcheatBoardLine(22, 3, writer);
            writer.println("Key:");
            printcheatBoardLine(3, 3, writer);
            writer.println("A = Aircraft Carrier");
            writer.println("B = Battleship");
            writer.println("C = Cruiser");
            writer.println("S = Submarine");
            writer.println("D = Destroyer");
            printcheatBoardLine(11, 3, writer);
            writer.println("Computer's Board:");
            String difficulty = "";
            if (gamelevel == 1) {
                difficulty = "Easy";
            } else if (gamelevel == 2) {
                difficulty = "Medium";
            } else if (gamelevel == 3) {
                difficulty = "Hard";
            } else if (gamelevel == 4) {
                difficulty = "Impossible";
            }
            writer.println("Difficulty: " + difficulty);
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (i == 0) {
                    printcheatBoardLine(11, 0, writer);
                }
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (j == 0) {
                        writer.print("\u2502 ");
                    } else {
                        writer.print(" \u2502 ");
                    }
                    printcheatData(i, j, writer);
                    if (j == (BOARD_SIZE - 1)) {
                        writer.print(" \u2502");
                    }
                }
                writer.println("");
                if (i == (BOARD_SIZE - 1)) {
                    printcheatBoardLine(11, 2, writer);
                } else if (i < BOARD_SIZE) {
                    printcheatBoardLine(11, 1, writer);
                }
            }
        }
    
        private static void printcheatData(int row, int col, PrintWriter writer) {
            if (row == 0) {
                if (col == 0) {
                    writer.print("  ");
                } else {
                    writer.print(letters[col - 1]);
                }
            } else if (col == 0) {
                if (row == 0) {
                    writer.print(" ");
                } else if (row == 10) {
                    writer.print(row + "");
                } else {
                    writer.print(" " + row);
                }
            } else {
                if (cpu[row - 1][col - 1] == 0) {
                    writer.print(" ");
                } else if (cpu[row - 1][col - 1] == 5) {
                    writer.print("A");
                } else if (cpu[row - 1][col - 1] == 6) {
                    writer.print("B");
                } else if (cpu[row - 1][col - 1] == 7) {
                    writer.print("C");
                } else if (cpu[row - 1][col - 1] == 8) {
                    writer.print("S");
                } else if (cpu[row - 1][col - 1] == 9) {
                    writer.print("D");
                }
            }
        }
    
        public static void printcheatBoardLine(int n, int type, PrintWriter writer) {
            for (int i = 0; i < n; i++) {
                if (type == 0) {
                    if (i == 0) {
                        writer.print("┌──");
                    }
                    if (i == (n - 1)) {
                        writer.println("──┐");
                    } else {
                        writer.print("──┬─");
                    }
                } else if (type == 1) {
                    if (i == 0) {
                        writer.print("├──");
                    }
                    if (i == (n - 1)) {
                        writer.println("──┤");
                    } else {
                        writer.print("──┼─");
                    }
                } else if (type == 2) {
                    if (i == 0) {
                        writer.print("└──");
                    }
                    if (i == (n - 1)) {
                        writer.println("──┘");
                    } else {
                        writer.print("──┴─");
                    }
                } else if (type == 3) {
                    if (i == (n - 1)) {
                        writer.println("──");
                    } else {
                        writer.print("──");
                    }
                }
            }
        }
    }
}
