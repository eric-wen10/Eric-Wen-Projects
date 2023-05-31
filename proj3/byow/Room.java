package byow;

import java.util.ArrayList;
import java.util.Random;

public class Room {

    private static final ArrayList<Room> ALL_ROOMS = new ArrayList<>();
    private final int rLength;
    private final int rWidth;
    private final int x;
    private final int y;
    private final Random rand;
    private Integer[] entry = null;
    World world;


    public Room(int l, int w, int sx, int sy, Random r, int worldwidth, int worldheight, World world) {
        rLength = l;
        rWidth = w;
        x = sx;
        y = sy;
        rand = r;
        this.world = world;

        if (rLength > 4 && rWidth > 4) {
            this.addToLocations(world);
            ALL_ROOMS.add(this);
            entry = new Integer[]{sx + w / 2, sy + l / 2};
            world.noCopyAdd(world.allEntries, entry);
        }


    }


    public static ArrayList<Integer[]> getroomlocations(World world) {
        return world.roomLocations;
    }

    public static ArrayList<Integer[]> getroomperimeter(World world) {
        return world.allRoomPerims;
    }

    public static ArrayList<Integer[]> getAllEntries(World world) {
        return world.allEntries;
    }

    public static ArrayList<Room> getAllRooms() {
        return ALL_ROOMS;
    }

    public static boolean inNoTouch(ArrayList<Integer[]> s, World world) {
        for (Integer[] x : world.notouchzone) {
            for (Integer[] y : s) {
                if (x[0] == y[0] && x[1] == y[1]) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean inRoomLocations(Integer[] s, World world) {
        for (Integer[] x : world.roomLocations) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }
        return false;
    }

    public static boolean inPerims(Integer[] s, World world) {
        for (Integer[] x : world.allRoomPerims) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }
        return false;
    }

    public Integer[] getEntry() {
        return entry;
    }


    public void addToLocations(World world) {

        for (int currY = y; currY < y + rLength; currY++) {
            for (int currX = x; currX < x + rWidth; currX++) {
                Integer[] adder = new Integer[2];
                adder[0] = currX;
                adder[1] = currY;
                world.noCopyAdd(world.roomLocations, adder);

                if (currX == x || currY == y || currX == x + rWidth - 1 || currY == y + rLength - 1) {
                    Integer[] perimeteradder = new Integer[2];
                    perimeteradder[0] = currX;
                    perimeteradder[1] = currY;
                    world.noCopyAdd(world.allRoomPerims, perimeteradder);
                }
            }
        }

        for (int currY = y - 2; currY < y + rLength + 2; currY++) { //creating a no-touch zone of 1 sqaure
            for (int currX = x - 2; currX < x + rWidth + 2; currX++) {
                Integer[] notouchzoneadder = {currX, currY};
                world.noCopyAdd(world.notouchzone, notouchzoneadder);
            }
        }
    }


}