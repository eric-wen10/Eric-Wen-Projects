package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.World;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 50;
    public static final int HEIGHT = 40;
    private final int fiftypause = 50;
    private final int maxseedlength = 19;
    private final int fifteen = 15;
    private final int thirty = 30;
    private final int hud = 23;
    TERenderer ter = new TERenderer();
    World world = new World(0, HEIGHT, WIDTH); //somehow this is needed to generate the start menus
    String input = "";
    String gamestate = "menu";

    boolean narrowView = false;

    public static void main(String[] args) {
        Engine engine = new Engine();
        //engine.interactWithInputString("n44234324s");
        //World world = new World(423423, HEIGHT, WIDTH);
        //engine.drawGameDisplay(world);
        engine.interactWithKeyboard(); // somehow alway sneeds a world to do
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        if (gamestate.equals("menu")) {
            StdDraw.enableDoubleBuffering();
            drawMenu();
            gamestate = "seedmenu";
        }
        while (gamestate.equals("seedmenu")) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if ((c == 'n' && input.length() < 2) || (c == 'N' && input.length() < 2)) {
                    gamestate = "seedmenu";
                    drawSeedMenu();
                    drawSeedinput(input);
                    input += c;
                } else if ((c == 'l' && input.length() < 2) || (c == 'L' && input.length() < 2)) {
                    gamestate = "avatarmove";
                    input = load();
                    String[] seedAndMovements = loadingstringparser(input);
                    makeGameWorldFromLoading(seedAndMovements[0]);
                    for (char a : seedAndMovements[1].toCharArray()) {
                        if (a == 'w') {
                            world.avatarUp();
                        }
                        if (a == 'a') {
                            world.avatarleft();
                        }
                        if (a == 's') {
                            world.avatardown();
                        }
                        if (a == 'd') {
                            world.avatarright();
                        }
                        if (a == 'q' || a == 'Q') {
                            System.exit(0);
                        }
                        if (a == 't') {
                            world.turnOff();
                        }
                        StdDraw.enableDoubleBuffering();
                        ter.renderFrame(world.getTiles());
                        drawGameDisplay(world);
                        StdDraw.pause(fiftypause);
                    }
                } else if ((c == 'q' && input.length() < 2) || (c == 'Q' && input.length() < 2)) {
                    System.exit(0);
                } else if ((input.charAt(0) == 'n' && c != 's') || (input.charAt(0) == 'N' && c != 's')) {
                    drawSeedMenu();
                    drawSeedinput(input);
                    input += c;
                } else if ((c == 's' && input.length() > 2) || (c == 'S' && input.length() > 2)) {
                    input += c;
                    gamestate = "avatarmove";
                    makeGameWorld(input);
                }
            }
        }
        while (gamestate.equals("avatarmove")) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                movementchecker(c);
                StdDraw.enableDoubleBuffering();
                ter.renderFrame(world.getTiles());
                drawGameDisplay(world);
            } else {
                while (!StdDraw.hasNextKeyTyped()) {
                    StdDraw.enableDoubleBuffering();
                    ter.renderFrame(world.getTiles());
                    drawGameDisplay(world);
                    StdDraw.pause(fiftypause);

                }
            }
        }
    }

    public void movementchecker(Character c) {
        if (c == 'w') { // Character move
            world.avatarUp();
            input += c;
        }
        if (c == 'a') {
            world.avatarleft();
            input += c;
        }

        if (c == 's') {
            world.avatardown();
            input += c;
        }

        if (c == 'd') {
            world.avatarright();
            input += c;
        }
        if (c == ':') {
            save(input);
            input += c;
        }
        if ((c == 'q' || c == 'Q') && input.charAt(input.length() - 1) == ':') {
            System.exit(0);
        }
        if (c == 't') {
            world.turnOff();
            input += c;
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, running both of these:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */

    public TETile[][] interactWithInputString(String inputString) {
        String[] seedAndMovements = loadingstringparser(inputString);
        if (seedAndMovements[0].equals("")) {
            String extraMovementsAfterLoading = inputString.substring(1);
            String savedstring = load();
            String[] newSeedAndMovements = loadingstringparser(savedstring);
            newSeedAndMovements[1] += extraMovementsAfterLoading;
            long i = Long.valueOf(newSeedAndMovements[0]);
            world = new World(i, HEIGHT, WIDTH);
            ter.initialize(WIDTH, HEIGHT);
            for (char a : newSeedAndMovements[1].toCharArray()) {
                if (a == 'w') { // Character move
                    world.avatarUp();
                }
                if (a == 'a') {
                    world.avatarleft();
                }

                if (a == 's') {
                    world.avatardown();
                }

                if (a == 'd') {
                    world.avatarright();
                }
                if (a == ':') {
                    save(inputString);
                }
                /*if (a == 'q' || a == 'Q') {
                    //System.exit(0);
                }*/
                if (a == 't') {
                    world.turnOff();
                }
            }
            StdDraw.enableDoubleBuffering();
            ter.renderFrame(world.getTiles());
            drawGameDisplay(world);
            StdDraw.pause(fiftypause);
        } else if (!seedAndMovements[1].equals("")) { //when you have entire seed
            long i = Long.valueOf(seedAndMovements[0]);
            world = new World(i, HEIGHT, WIDTH);
            ter.initialize(WIDTH, HEIGHT);
            for (char a : seedAndMovements[1].toCharArray()) {
                if (a == 'w') { // Character move
                    world.avatarUp();
                }
                if (a == 'a') {
                    world.avatarleft();
                }

                if (a == 's') {
                    world.avatardown();
                }

                if (a == 'd') {
                    world.avatarright();
                }
                if (a == ':') {
                    save(inputString);
                }

                /*if (a == 'q' || a == 'Q') {
                    //System.exit(0);
                }*/
                if (a == 't') {
                    world.turnOff();
                }
                StdDraw.enableDoubleBuffering();
                ter.renderFrame(world.getTiles());
                drawGameDisplay(world);
                StdDraw.pause(fiftypause);
            }
            StdDraw.enableDoubleBuffering();
            ter.renderFrame(world.getTiles());
            drawGameDisplay(world);
        }
        return world.getTiles();
    }

    public Long makeGameWorld(String s) {
        if (s.charAt(s.length() - 1) == 's') {
            s = s.substring(1, s.length() - 1);
        }
        if (s.length() > maxseedlength) {
            s = s.substring(0, maxseedlength - 1);
        }
        long i = Long.parseLong(s);
        world = new World(i, HEIGHT, WIDTH);
        ter.initialize(WIDTH, HEIGHT);
        StdDraw.enableDoubleBuffering();
        ter.renderFrame(world.getTiles());
        drawGameDisplay(world);
        return i;
    }

    public Long makeGameWorldFromLoading(String s) {
        if (s.charAt(s.length() - 1) == 's') {
            s = s.substring(1, s.length() - 1);
        }
        if (s.length() > maxseedlength) {
            s = s.substring(0, maxseedlength - 1);
        }
        long i = Long.parseLong(s);
        world = new World(i, HEIGHT, WIDTH);
        ter.initialize(WIDTH, HEIGHT);
        StdDraw.enableDoubleBuffering();
        ter.renderFrame(world.getTiles());
        drawGameDisplay(world);
        return i;
    }

    public void drawGameDisplay(World world1) {
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, fifteen);
        StdDraw.setFont(fontBig);

        LocalDate date = LocalDate.now();
        LocalTime t = LocalTime.now();
        DateTimeFormatter correctFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = t.format(correctFormat);

        int x = (int) Math.floor(StdDraw.mouseX());
        int y = (int) Math.floor(StdDraw.mouseY());
        Integer[] location = {x, y};
        String type = "";

        if (world1.isAvatarLoc(location)) {
            type = "Fish -- 鱼";
        } else if (world1.inGetAllEntries(location) && !world1.inFakeIsland(location)) {
            type = "Island";
        } else if (world1.inHallwayLocations(location)) {
            type = "Ocean";
        } else if (world1.inAllPerims(location)) {
            type = "Land";
        } else if (world1.inhallwayWalls(location)) {
            type = "Land";
        } else if (world1.inAllRooms(location)) {
            type = "Ocean";
        } else {
            type = "Nothingness";
        }

        StdDraw.text(hud, HEIGHT - 1, "Current Day: " + date + " and Time: " + time + " You are inspecting: " + type);

        StdDraw.show();

    }

    public void drawMenu() {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, thirty);
        Font fontSmall = new Font("Monaco", Font.BOLD, fifteen);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, 3 * HEIGHT / 4, "Welcome to CS61B Game");
        StdDraw.setFont(fontSmall);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Press N (New World), L (Load World), Q (Quit)");
        StdDraw.show();
    }

    public void drawSeedMenu() {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, thirty);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, 3 * HEIGHT / 4, "Enter a Seed followed by the letter s");
        StdDraw.show();
    }

    public void drawSeedinput(String c) {
        StdDraw.enableDoubleBuffering();
        StdDraw.clear(StdDraw.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, thirty);
        StdDraw.setFont(fontBig);
        StdDraw.text(WIDTH / 2, 3 * HEIGHT / 4, "Enter a Seed followed by the letter s");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, c);
        StdDraw.show();
    }

    public void save(String toSave) {
        //Write file
        Out savedLocation = new Out("load.txt");
        savedLocation.print(toSave);
    }

    public String load() {
        In toLoad = new In("load.txt");
        return toLoad.readLine();
    }

    public String[] loadingstringparser(String string) {
        boolean condition = true;
        int s = 0;
        String seed;
        String movements;

        if (string.charAt(0) == 'l' || string.charAt(0) == 'L') {
            seed = "";
            movements = string.substring(1);
        } else {
            while (condition) {
                if (string.charAt(s) == 's') {
                    condition = false;
                }
                s += 1;
            }
            movements = "";
            if (s < string.length()) {
                movements = string.substring(s);
            }
            seed = string.substring(0, s - 1);

            if (string.charAt(0) == 'n') {
                seed = string.substring(1, s - 1);
            }
            if (seed.length() > maxseedlength) {
                seed = seed.substring(0, maxseedlength - 1);
            }
        }
        return new String[]{seed, movements};

    }


}
