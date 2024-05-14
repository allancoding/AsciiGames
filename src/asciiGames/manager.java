package asciiGames;

import org.fusesource.jansi.AnsiConsole;
import java.io.File;
import java.util.ArrayList;
import java.util.jar.JarFile;
import java.lang.reflect.Method;
import java.net.URL;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class manager {
    public static boolean gameRunning = false;
    private static final String packagePath = "asciiGames";
    private static ArrayList<Object[]> gameS = new ArrayList<>();
    private static Thread shutdownHook;
    public static void main(String[] args) throws Exception {
        AnsiConsole.systemInstall();
        if (AnsiConsole.getTerminalWidth() < 80) {
            ascii.println("Terminal width must be at least 80 characters wide.");
            System.exit(1);
        }
        ascii.wait(300);
        ctrC();
        setup();
        start(true, "");
    }

    public static void setup() throws Exception {
        URL gamespath = manager.class.getResource("games/");
        if (gamespath.toString().startsWith("jar:") || gamespath.toString().startsWith("rsrc:")) {
            System.out.println("Jar file detected.");
            String jarPath = manager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            JarFile jarFile = new JarFile(jarPath);
            ArrayList<String> gameClasses = new ArrayList<String>();
            jarFile.stream().forEach(entry -> {
                if (entry.getName().startsWith("asciiGames/games/") && !entry.isDirectory()) {
                    if (entry.getName().replace(".class", "").endsWith("$Game")) {
                        gameClasses.add(entry.getName().replace(".class", "").replace("/", "."));
                    }
                }
            });
            for (String gameClassName : gameClasses) {
                Class<?> gameClass = Class.forName(gameClassName);
                Field[] fields = gameClass.getFields();
                String[] fieldValue = new String[fields.length];
                int i = 0;
                for (Field field : fields) {
                    if (field.getType().equals(String.class)) {
                        fieldValue[i] = (String) field.get(null);
                        i++;
                    }
                }
                gameS.add(new Object[]{gameClassName.replace("asciiGames.games.", ""), fieldValue[0], fieldValue[1]});
            }
            jarFile.close();
        } else {
            File folder = new File(gamespath.getPath());
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null && listOfFiles.length > 0) {
                ArrayList<String> gameClasses = new ArrayList<String>();
                for (File file : listOfFiles) {
                    if (file.isFile() && file.getName().endsWith(".class")) {
                        if (file.getName().replace(".class", "").endsWith("$Game")) {
                            gameClasses.add(file.getName().replace(".class", ""));
                        }
                    }
                }
                for (String gameClassName : gameClasses) {
                    Class<?> gameClass = Class.forName(packagePath + ".games." + gameClassName);
                    Field[] fields = gameClass.getFields();
                    String[] fieldValue = new String[fields.length];
                    int i = 0;
                    for (Field field : fields) {
                        if (field.getType().equals(String.class)) {
                            fieldValue[i] = (String) field.get(null);
                            i++;
                        }
                    }
                    gameS.add(new Object[]{gameClassName, fieldValue[0], fieldValue[1]});
                }
            }
        }
    }

    public static void start(boolean animate, String error) throws Exception {
        if (animate) {
            animation.slidein();
        }
        animation.show();
        ascii.printRepeated(ascii.color.ANSI_PURPLE + "-" + ascii.color.ANSI_RESET, 80, true);
        ascii.println(ascii.color.ANSI_RED + "Welcome to ASCII Games!" + ascii.color.ANSI_RESET);
        ascii.printRepeated(ascii.color.ANSI_YELLOW + "=" + ascii.color.ANSI_RESET, 24, true);
        ascii.println(ascii.color.ANSI_GREEN + "List of Games:" + ascii.color.ANSI_RESET);
        ascii.printRepeated(ascii.color.ANSI_CYAN + "~" + ascii.color.ANSI_RESET, 15, true);
        Object[][] array = new Object[gameS.size()][3];
        array = gameS.toArray(array);
        int i = 1;
        for (Object[] obj : array) {
            System.out.println(ascii.color.ANSI_BLUE + i + ". " + obj[1] + " - " + obj[2] + ascii.color.ANSI_RESET);
            i++;
        }
        ascii.printRepeated(ascii.color.ANSI_PURPLE +"-" + ascii.color.ANSI_RESET, 20, true);
        if (error != "") {
            ascii.println(ascii.color.ANSI_RED + error + ascii.color.ANSI_RESET);
        }
        int start = 0;
        try {
            ascii.print("Enter a Game: ");
            start = Integer.parseInt(System.console().readLine());
        } catch (Exception e) {
            start = 0;
            return;
        }
        if (start < 1 || start > gameS.size()) {
            start(false, "Not a valid game.");
            return;
        }
        try {
            System.out.println("Starting game: " + array[start - 1][0]);
            Class<?> clazz = Class.forName(packagePath + ".games." + array[start - 1][0]+"");
            Method method = clazz.getMethod("start", boolean.class, boolean.class);
            ascii.clear();
            method.invoke(null, true, false);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        start(false, "");
    }

    public static void ctrC() {
        shutdownHook = new Thread(() -> {
            ascii.clear();
            ascii.println(ascii.color.ANSI_RED + "Goodbye!");
            ascii.println(ascii.color.ANSI_GREEN + "Thank you for playing ASCII Games!" + ascii.color.ANSI_RESET);
        });
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }
}