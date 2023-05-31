package byow;

import java.util.ArrayList;

public class XHallway {
    private final int hLength;
    private final int hWidth;
    private final int x;
    private final int y;

    public XHallway(int l, int w, int sx, int sy, World world) {
        hLength = l;
        hWidth = w;
        x = sx;
        y = sy;

        this.addToLocations(world);
    }


    public void addToLocations(World world) {
        // to cycle through the widths and heights and add arraylists of locations
        for (int currY = y; currY < y + hWidth; currY++) {
            for (int currX = x; currX < x + hLength; currX++) {
                Integer[] adder = new Integer[2];
                adder[0] = currX;
                adder[1] = currY;
                world.noCopyAdd(world.hallwayLocations, adder);
            }
        }
    }


}
