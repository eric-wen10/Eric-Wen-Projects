package byow;

import java.util.ArrayList;

public class YHallway {

    private final int hLength;
    private final int hWidth;
    private final int x;
    private final int y;

    public YHallway(int l, int w, int sx, int sy, World world) {
        hLength = l;
        hWidth = w;
        x = sx;
        y = sy;

        this.addToLocations(world);
    }


    public void addToLocations(World world) {
        // to cycle through the widths and heights and add arraylists of locations
        for (int currX = x; currX < x + hWidth; currX++) {
            for (int currY = y; currY < y + hLength; currY++) {
                Integer[] adder = new Integer[2];
                adder[0] = currX;
                adder[1] = currY;
                world.noCopyAdd(world.hallwayLocations, adder);
            }
        }
    }
}
