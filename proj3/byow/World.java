package byow;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class World {

    private final long seed;

    private final int height;

    private final int width;

    private final Random r;

    ArrayList<Integer[]> roomLocations = new ArrayList<Integer[]>();
    ArrayList<Integer[]> allRoomPerims = new ArrayList<Integer[]>();
    ArrayList<Integer[]> notouchzone = new ArrayList<Integer[]>();
    ArrayList<Integer[]> allEntries = new ArrayList<>();
    Integer[] avatarloc = new Integer[2];
    ArrayList<Integer[]> hallwayWalls = new ArrayList<>();
    ArrayList<Integer[]> hallwayLocations = new ArrayList<Integer[]>();
    TETile[][] tiles;
    TERenderer ter;
    ArrayList<Integer[]> colorOne = new ArrayList<>();
    ArrayList<Integer[]> colorTwo = new ArrayList<>();
    ArrayList<Integer[]> fakeIslands = new ArrayList<>();
    boolean lightOn = false;
    int constant = 20;

    public World(long s, int h, int w) {
        seed = s;
        height = h;
        width = w;
        r = new Random(seed);

        // make renderer
        ter = new TERenderer();
        ter.initialize(width, height);
        tiles = new TETile[width][height];

        // Make floor
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        int number = r.nextInt(width * height / ((width + height) * 2)) + (height + width) / constant;
        this.makeRooms(number);
        this.newConnect();
        this.generatePlayerStartPosition();
    }

    public static void main(String[] args) {
        int width = 60;
        int height = 60;
        long seed = 13498;

        World world = new World(seed, height, width);

        TERenderer ter = new TERenderer();
        ter.initialize(width, height);
        ter.renderFrame(world.getTiles());


    }

    public void colorHallway() { //drawthe hallways

        for (Integer[] pos : hallwayLocations) {
            tiles[pos[0]][pos[1]] = Tileset.OCEAN;
            if (pos[0] != 0 && tiles[pos[0] - 1][pos[1]] != Tileset.OCEAN) {
                tiles[pos[0] - 1][pos[1]] = Tileset.LAND;
                noCopyAdd(hallwayWalls, new Integer[]{pos[0] - 1, pos[1]});

            }
            if (pos[0] != width && tiles[pos[0] + 1][pos[1]] != Tileset.OCEAN) {
                tiles[pos[0] + 1][pos[1]] = Tileset.LAND;
                noCopyAdd(hallwayWalls,new Integer[]{pos[0] + 1, pos[1]});
            }
            if (pos[1] + 1 != height && tiles[pos[0]][pos[1] + 1] != Tileset.OCEAN) {
                tiles[pos[0]][pos[1] + 1] = Tileset.LAND;
                noCopyAdd(hallwayWalls, new Integer[]{pos[0], pos[1] + 1});

            }
            if (pos[1] != 0 && tiles[pos[0]][pos[1] - 1] != Tileset.OCEAN) {
                tiles[pos[0]][pos[1] - 1] = Tileset.LAND;
                noCopyAdd(hallwayWalls, new Integer[]{pos[0], pos[1] - 1});
            }
        }

        for (Integer[] pos : hallwayLocations) {
            tiles[pos[0]][pos[1]] = Tileset.OCEAN;
            if (pos[1] + 1 != height && tiles[pos[0]][pos[1] + 1] != Tileset.OCEAN) {
                tiles[pos[0]][pos[1] + 1] = Tileset.LAND;
                noCopyAdd(hallwayWalls, new Integer[]{pos[0], pos[1] + 1});

            }
            if (pos[1] != 0 && tiles[pos[0]][pos[1] - 1] != Tileset.OCEAN) {
                tiles[pos[0]][pos[1] - 1] = Tileset.LAND;
                noCopyAdd(hallwayWalls, new Integer[]{pos[0], pos[1] - 1});

            }
            if (pos[0] != 0 && tiles[pos[0] - 1][pos[1]] != Tileset.OCEAN) {
                tiles[pos[0] - 1][pos[1]] = Tileset.LAND;
                noCopyAdd(hallwayWalls, new Integer[]{pos[0] - 1, pos[1]});

            }
            if (pos[0] != width && tiles[pos[0] + 1][pos[1]] != Tileset.OCEAN) {
                tiles[pos[0] + 1][pos[1]] = Tileset.LAND;
                noCopyAdd(hallwayWalls, new Integer[]{pos[0] + 1, pos[1]});
            }
        }

        colorLights();
    }


    public void colorLights() {
        fakeIslands = new ArrayList<>();
        for (Integer[] pos : Room.getAllEntries(this)) {
            if (isAvatarLoc(pos)) {
                generatePlayerStartPosition();
            }
            tiles[pos[0]][pos[1]] = Tileset.LIGHT;


            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    Integer[] curr = {pos[0] + x, pos[1] + y};
                    if (!inGetAllEntries(curr) && !inAllPerims(curr) && !isAvatarLoc(curr) && (inAllRooms(curr) || inHallwayLocations(curr))) {
                        tiles[curr[0]][curr[1]] = Tileset.C1;
                        noCopyAdd(colorOne, curr);
                    }
                }
            }
            for (int x = -2; x < 3; x++) {
                for (int y = -2; y < 3; y++) {
                    Integer[] curr = {pos[0] + x, pos[1] + y};
                    if (!inGetAllEntries(curr) && !inAllPerims(curr) && !isAvatarLoc(curr) && !inColorOne(curr) && (inAllRooms(curr) || inHallwayLocations(curr))) {
                        tiles[curr[0]][curr[1]] = Tileset.C2;
                        noCopyAdd(colorTwo, curr);
                    }
                }
            }
        }
    }

    public void uncolorLight() {
        fakeIslands = new ArrayList<>();
        Integer[] pos = Room.getAllEntries(this).get(r.nextInt(Room.getAllEntries(this).size()));
        for (int x = -2; x < 3; x++) {
            for (int y = -2; y < 3; y++) {
                Integer[] curr = {pos[0] + x, pos[1] + y};
                if ((inColorOne(curr) || inGetAllEntries(curr) || inColorTwo(curr)) && !isAvatarLoc(curr)) {
                    tiles[curr[0]][curr[1]] = Tileset.OCEAN;
                }
                noCopyAdd(fakeIslands, curr);
            }
        }
    }

    public void makeRooms(int howmanyrooms) {
        //int howmanyrooms = r.nextInt(number) + 6; //number should be smaller than a certain number 8 or 10 maybe

        for (int currR = 0; currR < howmanyrooms; currR++) {

            int l = r.nextInt(8,12); // 10 used as arbitrary limit
            int w = r.nextInt(8,12);
            int sx = r.nextInt(width - w - 3) + 4;
            int sy = r.nextInt(height - l - 3) + 4;

            while (Room.inNoTouch(findClose(l, w, sx, sy), this)) {
                l = r.nextInt(6) + 4; // 10 used as arbitrary limit
                w = r.nextInt(6) + 4;
                sx = r.nextInt(width - w);
                sy = r.nextInt(height - l);
            }
            Room nextroom = new Room(l, w, sx, sy, r, width, height, this);
        }

        drawRooms();

    }

    public ArrayList<Integer[]> findClose(int l, int w, int sx, int sy) {

        ArrayList<Integer[]> corners = new ArrayList<Integer[]>();

        Integer[] bottomRightCorner = {sx + w - 1, sy};
        noCopyAdd(corners, bottomRightCorner);

        Integer[] bottomLeftCorner = {sx, sy};
        noCopyAdd(corners, bottomLeftCorner);

        Integer[] topLeft = {sx, sy + l - 1};
        noCopyAdd(corners, topLeft);

        Integer[] topRight = {sx + w - 1, sy + l - 1};
        noCopyAdd(corners, topRight);

        return corners;

    }

    public void drawRooms() {

        for (Integer[] coordinates : Room.getroomlocations(this)) {
            int currX = coordinates[0];
            int currY = coordinates[1];
            tiles[currX][currY] = Tileset.OCEAN;
        }
        for (Integer[] perimcoord : Room.getroomperimeter(this)) {
            int X = perimcoord[0];
            int Y = perimcoord[1];
            tiles[X][Y] = Tileset.LAND;
        }
    }

    public void newConnect() {
        Iterator<Room> rooms = Room.getAllRooms().iterator();

        Room one = rooms.next();

        Room smallerX;
        Room smallerY;
        Room biggerX;
        Room biggerY;

        while (rooms.hasNext()) {
            Room two = rooms.next();

            //assign smaller + bigger values:
            if (one.getEntry()[0] < two.getEntry()[0]) {
                smallerX = one;
                biggerX = two;
            } else {
                smallerX = two;
                biggerX = one;
            }

            if (one.getEntry()[1] < two.getEntry()[1]) {
                smallerY = one;
                biggerY = two;
            } else {
                smallerY = two;
                biggerY = one;
            }

            //find values needed to move somewhere

            int currW = 0;
            Integer[] posX = {smallerX.getEntry()[0], smallerX.getEntry()[1]};

            while (smallerX.getEntry()[0] + currW != biggerX.getEntry()[0]) {
                currW++;
                posX = new Integer[]{smallerX.getEntry()[0] + currW, smallerX.getEntry()[1]};
            }

            int currL = 0;
            Integer[] posY = {smallerX.getEntry()[0], smallerX.getEntry()[1]};

            while (smallerY.getEntry()[1] + currL != biggerY.getEntry()[1] + 1) {
                currL++;
                posY = new Integer[]{smallerX.getEntry()[0] + currW, smallerY.getEntry()[1] + currL};
            }

            int startingX = smallerX.getEntry()[0];
            int startingY = smallerX.getEntry()[1];

            //adjust x starting point -> if we add one to a corner and add plus we are in room, so adjust
            Integer[] toRight = new Integer[]{smallerX.getEntry()[0] + 1, smallerX.getEntry()[1]};
            if (Room.inRoomLocations(toRight, this) && !Room.inPerims(toRight, this)) {
                startingX = toRight[0];
                startingY = toRight[1];
            }


            XHallway firstHall = new XHallway(currW, r.nextInt(1) + 1, startingX, startingY, this);
            YHallway secondHall = new YHallway(currL, r.nextInt(1) + 1, smallerX.getEntry()[0] + currW, smallerY.getEntry()[1], this);

        }

        editPerims();

        colorHallway();

    }

    public void editPerims() {
        ArrayList<Integer[]> toBeDeleted = new ArrayList<>();
        for (Integer[] perim : this.allRoomPerims) {
            for (Integer[] entry : hallwayLocations) {
                if (perim[0] == entry[0] && perim[1] == entry[1]) {
                    toBeDeleted.add(perim);
                }
            }
        }
        for (Integer[] d : toBeDeleted) {
            this.allRoomPerims.remove(d);
        }
    }



    public TETile[][] getTiles() {
        return tiles;
    }

    public boolean inAllRooms(Integer[] s) {
        for (Integer[] x : this.roomLocations) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }

        return false;
    }

    public boolean inAllPerims(Integer[] s) {

        for (Integer[] x : this.allRoomPerims) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }

        return false;
    }

    public boolean inHallwayLocations(Integer[] s) {
        for (Integer[] x : hallwayLocations) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }

        return false;
    }

    public boolean inhallwayWalls(Integer[] s) {
        for (Integer[] x : hallwayWalls) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }
        return false;
    }

    public boolean inGetAllEntries(Integer[] s) {
        for (Integer[] x : allEntries) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }
        return false;
    }

    public boolean inFakeIsland(Integer[] s) {
        for (Integer[] x : fakeIslands) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }
        return false;
    }

    public boolean inColorOne(Integer[] s) {
        for (Integer[] x : colorOne) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }
        return false;
    }


    public boolean inColorTwo(Integer[] s) {
        for (Integer[] x : colorTwo) {
            if (x[0] == s[0] && x[1] == s[1]) {
                return true;
            }
        }
        return false;
    }

    public void generatePlayerStartPosition() {
        int coordinateselector = 10;
        boolean condition = true;
        while (condition) {
            Integer[] coordpair = roomLocations.get(coordinateselector);
            int playerX = coordpair[0];
            int playerY = coordpair[1];
            if (tiles[playerX][playerY] == Tileset.OCEAN || tiles[playerX][playerY] == Tileset.C1 || tiles[playerX][playerY] == Tileset.C2) {
                tiles[playerX][playerY] = Tileset.AVATAR;
                avatarloc[0] = playerX;
                avatarloc[1] = playerY;
                condition = false;
            }
            else{
                coordinateselector++;
            }
        }
    }

    public void avatarUp() {
        if (tiles[avatarloc[0]][avatarloc[1] + 1] == Tileset.OCEAN || tiles[avatarloc[0]][avatarloc[1] + 1] == Tileset.C1 || tiles[avatarloc[0]][avatarloc[1] + 1] == Tileset.C2) {
            Integer[] currLoc = {avatarloc[0], avatarloc[1]};
            if (inColorOne(currLoc) && !inFakeIsland(currLoc)) {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.C1;
            } else if (inColorTwo(currLoc) && !inFakeIsland(currLoc)) {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.C2;
            } else {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.OCEAN;
            }
            avatarloc[1] += 1;
            tiles[avatarloc[0]][avatarloc[1]] = Tileset.AVATAR;
        }
    }

    public void avatarleft() {
        if (tiles[avatarloc[0] - 1][avatarloc[1]] == Tileset.OCEAN || tiles[avatarloc[0] - 1][avatarloc[1]] == Tileset.C1 || tiles[avatarloc[0] - 1][avatarloc[1]] == Tileset.C2) {
            Integer[] currLoc = {avatarloc[0], avatarloc[1]};
            if (inColorOne(currLoc) && !inFakeIsland(currLoc)) {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.C1;
            } else if (inColorTwo(currLoc) && !inFakeIsland(currLoc)) {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.C2;
            } else {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.OCEAN;
            }
            avatarloc[0] -= 1;
            tiles[avatarloc[0]][avatarloc[1]] = Tileset.AVATAR;
        }
    }

    public void avatarright() {
        if (tiles[avatarloc[0] + 1][avatarloc[1]] == Tileset.OCEAN || tiles[avatarloc[0] + 1][avatarloc[1]] == Tileset.C1 || tiles[avatarloc[0] + 1][avatarloc[1]] == Tileset.C2) {
            Integer[] currLoc = {avatarloc[0], avatarloc[1]};
            if (inColorOne(currLoc) && !inFakeIsland(currLoc)) {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.C1;
            } else if (inColorTwo(currLoc) && !inFakeIsland(currLoc)) {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.C2;
            } else {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.OCEAN;
            }
            avatarloc[0] += 1;
            tiles[avatarloc[0]][avatarloc[1]] = Tileset.AVATAR;
        }
    }

    public void avatardown() {
        if (tiles[avatarloc[0]][avatarloc[1] - 1] == Tileset.OCEAN || tiles[avatarloc[0]][avatarloc[1] - 1] == Tileset.C1 || tiles[avatarloc[0]][avatarloc[1] - 1] == Tileset.C2) {
            Integer[] currLoc = {avatarloc[0], avatarloc[1]};
            if (inColorOne(currLoc) && !inFakeIsland(currLoc)) {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.C1;
            } else if (inColorTwo(currLoc) && !inFakeIsland(currLoc)) {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.C2;
            } else {
                tiles[avatarloc[0]][avatarloc[1]] = Tileset.OCEAN;
            }
            avatarloc[1] -= 1;
            tiles[avatarloc[0]][avatarloc[1]] = Tileset.AVATAR;
        }
    }

    public void turnOff() {
        lightOn = !lightOn;
        if (lightOn) {
            uncolorLight();
        } else {
            colorLights();
        }
    }

    public boolean isAvatarLoc(Integer[] a) {
        return avatarloc[0] == a[0] && avatarloc[1] == a[1];
    }


    public void noCopyAdd(ArrayList<Integer[]> list, Integer[] toAdd){
        for (Integer [] obj: list){
            if (obj[0] == toAdd[0] && obj[1] == toAdd[1]){
                return;
            }
        }
        list.add(toAdd);
    }


}
