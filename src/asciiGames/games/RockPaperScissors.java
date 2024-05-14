package asciiGames.games;

import org.fusesource.jansi.AnsiConsole;
import asciiGames.ascii;

public class RockPaperScissors {
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        Game.start(true, true);
    }

    public static class Game {
        public static final String Name = "Rock Paper Scissors";
        public static final String Description = "A really simple game of Rock Paper Scissors.";
        public static boolean shutdownHookAdded = false;

        public static void start(boolean instructions, boolean main) {
            System.setProperty("file.encoding", "UTF-8");
            if (!shutdownHookAdded && main) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(200);
                            ascii.println("Exiting Rock Paper Scissors...");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                shutdownHookAdded = true;
            }
            ascii.clear();
            ascii.println("You have been challenged!");
            ascii.println("0) Rock");
            ascii.println("1) Paper");
            ascii.println("2) Scissors");
            System.out.print("Enter a number for you the play: ");
            Integer guess = Integer.parseInt(System.console().readLine());
            int random = (int) (Math.random() * 3 + 1);
            random = random - 1;
            ascii.println(" ");
            ascii.println("The Computer played: " + random);
            if (guess == random) {
                ascii.println("You tided...");
            } else if (guess == 0 && random == 1) {
                ascii.println("You won... You beat the computer humanity will survive!");
            } else if (guess == 0 && random == 2) {
                ascii.println("You lost... Computers will now take over the world!");
            } else if (guess == 1 && random == 2) {
                ascii.println("You won... You beat the computer humanity will survive!");
            } else if (guess == 1 && random == 0) {
                ascii.println("You lost... Computers will now take over the world!");
            } else if (guess == 2 && random == 0) {
                ascii.println("You won... You beat the computer humanity will survive!");
            } else if (guess == 2 && random == 1) {
                ascii.println("You lost... Computers will now take over the world!");
            } else {
                ascii.println("You failed! The number " + guess + " is not a option!");
            }
            ascii.waitForEnter("Press enter to continue to the main menu...");
        }
    }
}