package maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generate a map.
 */
public class IsThisProcedural {

    // change this!!!
    public static final String mapName = "IsThisProcedural";

    // don't change this!!
    public static final String outputDirectory = "maps/";

    private static int width;
    private static int height;

    /**
     * @param args unused
     */
    public static void main(String[] args) {
        try {
            makeSimple();
        } catch (IOException e) {
            System.out.println(e);
        }
        System.out.println("Generated a map!");
    }

    public static ArrayList<ArrayList<Boolean>> makemap(Boolean contents,int w,int h) {
        ArrayList<ArrayList<Boolean>> arr = new ArrayList<>();
        for (int i=0; i<h; i++) {
            ArrayList<Boolean> b = new ArrayList<>();
            for (int j=0; j<w; j++) {
                b.add(contents);
            }
            arr.add(b);
        }
        return arr;
    }

    public static void makeSimple() throws IOException {
        width = 46;
        height = 32;
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 4444);
        mapBuilder.setWaterLevel(0);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.horizontal);
        mapBuilder.addSymmetricHQ(6, 24);

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                mapBuilder.setSymmetricDirt(x, y, 3);


        mapBuilder.setSymmetricDirt(6,10,120);
        mapBuilder.setSymmetricDirt(6,21,120);
        mapBuilder.setSymmetricDirt(7,5,120);
        mapBuilder.setSymmetricDirt(7,6,120);
        mapBuilder.setSymmetricDirt(7,9,120);
        mapBuilder.setSymmetricDirt(7,10,120);
        mapBuilder.setSymmetricDirt(7,11,120);
        mapBuilder.setSymmetricDirt(7,20,120);
        mapBuilder.setSymmetricDirt(7,21,120);
        mapBuilder.setSymmetricDirt(7,22,120);
        mapBuilder.setSymmetricDirt(7,25,120);
        mapBuilder.setSymmetricDirt(7,26,120);
        mapBuilder.setSymmetricDirt(8,5,120);
        mapBuilder.setSymmetricDirt(8,6,120);
        mapBuilder.setSymmetricDirt(8,7,120);
        mapBuilder.setSymmetricDirt(8,8,120);
        mapBuilder.setSymmetricDirt(8,9,120);
        mapBuilder.setSymmetricDirt(8,10,120);
        mapBuilder.setSymmetricDirt(8,21,120);
        mapBuilder.setSymmetricDirt(8,22,120);
        mapBuilder.setSymmetricDirt(8,23,120);
        mapBuilder.setSymmetricDirt(8,24,120);
        mapBuilder.setSymmetricDirt(8,25,120);
        mapBuilder.setSymmetricDirt(8,26,120);
        mapBuilder.setSymmetricDirt(9,5,120);
        mapBuilder.setSymmetricDirt(9,6,120);
        mapBuilder.setSymmetricDirt(9,7,120);
        mapBuilder.setSymmetricDirt(9,8,120);
        mapBuilder.setSymmetricDirt(9,9,120);
        mapBuilder.setSymmetricDirt(9,22,120);
        mapBuilder.setSymmetricDirt(9,23,120);
        mapBuilder.setSymmetricDirt(9,24,120);
        mapBuilder.setSymmetricDirt(9,25,120);
        mapBuilder.setSymmetricDirt(9,26,120);
        mapBuilder.setSymmetricDirt(10,5,120);
        mapBuilder.setSymmetricDirt(10,6,120);
        mapBuilder.setSymmetricDirt(10,7,120);
        mapBuilder.setSymmetricDirt(10,8,120);
        mapBuilder.setSymmetricDirt(10,9,120);
        mapBuilder.setSymmetricDirt(10,22,120);
        mapBuilder.setSymmetricDirt(10,23,120);
        mapBuilder.setSymmetricDirt(10,24,120);
        mapBuilder.setSymmetricDirt(10,25,120);
        mapBuilder.setSymmetricDirt(10,26,120);
        mapBuilder.setSymmetricDirt(11,5,120);
        mapBuilder.setSymmetricDirt(11,6,120);
        mapBuilder.setSymmetricDirt(11,7,120);
        mapBuilder.setSymmetricDirt(11,8,120);
        mapBuilder.setSymmetricDirt(11,23,120);
        mapBuilder.setSymmetricDirt(11,24,120);
        mapBuilder.setSymmetricDirt(11,25,120);
        mapBuilder.setSymmetricDirt(11,26,120);
        mapBuilder.setSymmetricDirt(12,5,120);
        mapBuilder.setSymmetricDirt(12,6,120);
        mapBuilder.setSymmetricDirt(12,7,120);
        mapBuilder.setSymmetricDirt(12,8,120);
        mapBuilder.setSymmetricDirt(12,9,120);
        mapBuilder.setSymmetricDirt(12,22,120);
        mapBuilder.setSymmetricDirt(12,23,120);
        mapBuilder.setSymmetricDirt(12,24,120);
        mapBuilder.setSymmetricDirt(12,25,120);
        mapBuilder.setSymmetricDirt(12,26,120);
        mapBuilder.setSymmetricDirt(13,5,120);
        mapBuilder.setSymmetricDirt(13,6,120);
        mapBuilder.setSymmetricDirt(13,7,120);
        mapBuilder.setSymmetricDirt(13,8,120);
        mapBuilder.setSymmetricDirt(13,14,120);
        mapBuilder.setSymmetricDirt(13,15,120);
        mapBuilder.setSymmetricDirt(13,16,120);
        mapBuilder.setSymmetricDirt(13,17,120);
        mapBuilder.setSymmetricDirt(13,23,120);
        mapBuilder.setSymmetricDirt(13,24,120);
        mapBuilder.setSymmetricDirt(13,25,120);
        mapBuilder.setSymmetricDirt(13,26,120);
        mapBuilder.setSymmetricDirt(14,5,120);
        mapBuilder.setSymmetricDirt(14,6,120);
        mapBuilder.setSymmetricDirt(14,7,120);
        mapBuilder.setSymmetricDirt(14,8,120);
        mapBuilder.setSymmetricDirt(14,14,120);
        mapBuilder.setSymmetricDirt(14,17,120);
        mapBuilder.setSymmetricDirt(14,23,120);
        mapBuilder.setSymmetricDirt(14,24,120);
        mapBuilder.setSymmetricDirt(14,25,120);
        mapBuilder.setSymmetricDirt(14,26,120);
        mapBuilder.setSymmetricDirt(15,5,120);
        mapBuilder.setSymmetricDirt(15,6,120);
        mapBuilder.setSymmetricDirt(15,7,120);
        mapBuilder.setSymmetricDirt(15,24,120);
        mapBuilder.setSymmetricDirt(15,25,120);
        mapBuilder.setSymmetricDirt(15,26,120);
        mapBuilder.setSymmetricDirt(18,13,120);
        mapBuilder.setSymmetricDirt(18,18,120);
        mapBuilder.setSymmetricDirt(19,12,120);
        mapBuilder.setSymmetricDirt(19,13,120);
        mapBuilder.setSymmetricDirt(19,14,120);
        mapBuilder.setSymmetricDirt(19,17,120);
        mapBuilder.setSymmetricDirt(19,18,120);
        mapBuilder.setSymmetricDirt(19,19,120);
        mapBuilder.setSymmetricDirt(20,5,120);
        mapBuilder.setSymmetricDirt(20,11,120);
        mapBuilder.setSymmetricDirt(20,12,120);
        mapBuilder.setSymmetricDirt(20,13,120);
        mapBuilder.setSymmetricDirt(20,14,120);
        mapBuilder.setSymmetricDirt(20,17,120);
        mapBuilder.setSymmetricDirt(20,18,120);
        mapBuilder.setSymmetricDirt(20,19,120);
        mapBuilder.setSymmetricDirt(20,20,120);
        mapBuilder.setSymmetricDirt(20,26,120);
        mapBuilder.setSymmetricDirt(21,7,120);
        mapBuilder.setSymmetricDirt(21,10,120);
        mapBuilder.setSymmetricDirt(21,11,120);
        mapBuilder.setSymmetricDirt(21,12,120);
        mapBuilder.setSymmetricDirt(21,13,120);
        mapBuilder.setSymmetricDirt(21,14,120);
        mapBuilder.setSymmetricDirt(21,17,120);
        mapBuilder.setSymmetricDirt(21,18,120);
        mapBuilder.setSymmetricDirt(21,19,120);
        mapBuilder.setSymmetricDirt(21,20,120);
        mapBuilder.setSymmetricDirt(21,21,120);
        mapBuilder.setSymmetricDirt(21,24,120);
        mapBuilder.setSymmetricDirt(22,6,120);
        mapBuilder.setSymmetricDirt(22,7,120);
        mapBuilder.setSymmetricDirt(22,10,120);
        mapBuilder.setSymmetricDirt(22,11,120);
        mapBuilder.setSymmetricDirt(22,12,120);
        mapBuilder.setSymmetricDirt(22,13,120);
        mapBuilder.setSymmetricDirt(22,18,120);
        mapBuilder.setSymmetricDirt(22,19,120);
        mapBuilder.setSymmetricDirt(22,20,120);
        mapBuilder.setSymmetricDirt(22,21,120);
        mapBuilder.setSymmetricDirt(22,24,120);
        mapBuilder.setSymmetricDirt(22,25,120);
        mapBuilder.setSymmetricDirt(23,6,120);
        mapBuilder.setSymmetricDirt(23,7,120);
        mapBuilder.setSymmetricDirt(23,8,120);
        mapBuilder.setSymmetricDirt(23,23,120);
        mapBuilder.setSymmetricDirt(23,24,120);
        mapBuilder.setSymmetricDirt(23,25,120);
        mapBuilder.setSymmetricDirt(24,5,120);
        mapBuilder.setSymmetricDirt(24,6,120);
        mapBuilder.setSymmetricDirt(24,7,120);
        mapBuilder.setSymmetricDirt(24,24,120);
        mapBuilder.setSymmetricDirt(24,25,120);
        mapBuilder.setSymmetricDirt(24,26,120);
        mapBuilder.setSymmetricDirt(25,5,120);
        mapBuilder.setSymmetricDirt(25,6,120);
        mapBuilder.setSymmetricDirt(25,7,120);
        mapBuilder.setSymmetricDirt(25,24,120);
        mapBuilder.setSymmetricDirt(25,25,120);
        mapBuilder.setSymmetricDirt(25,26,120);
        mapBuilder.setSymmetricDirt(26,10,120);
        mapBuilder.setSymmetricDirt(26,11,120);
        mapBuilder.setSymmetricDirt(26,12,120);
        mapBuilder.setSymmetricDirt(26,19,120);
        mapBuilder.setSymmetricDirt(26,20,120);
        mapBuilder.setSymmetricDirt(26,21,120);
        mapBuilder.setSymmetricDirt(27,10,120);
        mapBuilder.setSymmetricDirt(27,11,120);
        mapBuilder.setSymmetricDirt(27,12,120);
        mapBuilder.setSymmetricDirt(27,13,120);
        mapBuilder.setSymmetricDirt(27,18,120);
        mapBuilder.setSymmetricDirt(27,19,120);
        mapBuilder.setSymmetricDirt(27,20,120);
        mapBuilder.setSymmetricDirt(27,21,120);
        mapBuilder.setSymmetricDirt(28,11,120);
        mapBuilder.setSymmetricDirt(28,12,120);
        mapBuilder.setSymmetricDirt(28,13,120);
        mapBuilder.setSymmetricDirt(28,18,120);
        mapBuilder.setSymmetricDirt(28,19,120);
        mapBuilder.setSymmetricDirt(28,20,120);
        mapBuilder.setSymmetricDirt(29,11,120);
        mapBuilder.setSymmetricDirt(29,12,120);
        mapBuilder.setSymmetricDirt(29,19,120);
        mapBuilder.setSymmetricDirt(29,20,120);
        mapBuilder.setSymmetricDirt(31,5,120);
        mapBuilder.setSymmetricDirt(31,26,120);
        mapBuilder.setSymmetricDirt(32,3,120);
        mapBuilder.setSymmetricDirt(32,4,120);
        mapBuilder.setSymmetricDirt(32,5,120);
        mapBuilder.setSymmetricDirt(32,26,120);
        mapBuilder.setSymmetricDirt(32,27,120);
        mapBuilder.setSymmetricDirt(32,28,120);
        mapBuilder.setSymmetricDirt(33,2,120);
        mapBuilder.setSymmetricDirt(33,3,120);
        mapBuilder.setSymmetricDirt(33,4,120);
        mapBuilder.setSymmetricDirt(33,27,120);
        mapBuilder.setSymmetricDirt(33,28,120);
        mapBuilder.setSymmetricDirt(33,29,120);
        mapBuilder.setSymmetricDirt(34,2,120);
        mapBuilder.setSymmetricDirt(34,3,120);
        mapBuilder.setSymmetricDirt(34,4,120);
        mapBuilder.setSymmetricDirt(34,14,120);
        mapBuilder.setSymmetricDirt(34,17,120);
        mapBuilder.setSymmetricDirt(34,27,120);
        mapBuilder.setSymmetricDirt(34,28,120);
        mapBuilder.setSymmetricDirt(34,29,120);
        mapBuilder.setSymmetricDirt(35,2,120);
        mapBuilder.setSymmetricDirt(35,3,120);
        mapBuilder.setSymmetricDirt(35,13,120);
        mapBuilder.setSymmetricDirt(35,14,120);
        mapBuilder.setSymmetricDirt(35,15,120);
        mapBuilder.setSymmetricDirt(35,16,120);
        mapBuilder.setSymmetricDirt(35,17,120);
        mapBuilder.setSymmetricDirt(35,18,120);
        mapBuilder.setSymmetricDirt(35,28,120);
        mapBuilder.setSymmetricDirt(35,29,120);
        mapBuilder.setSymmetricDirt(36,12,120);
        mapBuilder.setSymmetricDirt(36,13,120);
        mapBuilder.setSymmetricDirt(36,14,120);
        mapBuilder.setSymmetricDirt(36,15,120);
        mapBuilder.setSymmetricDirt(36,16,120);
        mapBuilder.setSymmetricDirt(36,17,120);
        mapBuilder.setSymmetricDirt(36,18,120);
        mapBuilder.setSymmetricDirt(36,19,120);
        mapBuilder.setSymmetricDirt(37,4,120);
        mapBuilder.setSymmetricDirt(37,12,120);
        mapBuilder.setSymmetricDirt(37,13,120);
        mapBuilder.setSymmetricDirt(37,14,120);
        mapBuilder.setSymmetricDirt(37,17,120);
        mapBuilder.setSymmetricDirt(37,18,120);
        mapBuilder.setSymmetricDirt(37,19,120);
        mapBuilder.setSymmetricDirt(37,27,120);
        mapBuilder.setSymmetricDirt(38,3,120);
        mapBuilder.setSymmetricDirt(38,4,120);
        mapBuilder.setSymmetricDirt(38,5,120);
        mapBuilder.setSymmetricDirt(38,26,120);
        mapBuilder.setSymmetricDirt(38,27,120);
        mapBuilder.setSymmetricDirt(38,28,120);
        mapBuilder.setSymmetricDirt(39,3,120);
        mapBuilder.setSymmetricDirt(39,4,120);
        mapBuilder.setSymmetricDirt(39,5,120);
        mapBuilder.setSymmetricDirt(39,26,120);
        mapBuilder.setSymmetricDirt(39,27,120);
        mapBuilder.setSymmetricDirt(39,28,120);
        mapBuilder.setSymmetricDirt(42,11,120);
        mapBuilder.setSymmetricDirt(42,12,120);
        mapBuilder.setSymmetricDirt(42,13,120);
        mapBuilder.setSymmetricDirt(42,18,120);
        mapBuilder.setSymmetricDirt(42,19,120);
        mapBuilder.setSymmetricDirt(42,20,120);
        mapBuilder.setSymmetricDirt(43,10,120);
        mapBuilder.setSymmetricDirt(43,11,120);
        mapBuilder.setSymmetricDirt(43,12,120);
        mapBuilder.setSymmetricDirt(43,13,120);
        mapBuilder.setSymmetricDirt(43,14,120);
        mapBuilder.setSymmetricDirt(43,17,120);
        mapBuilder.setSymmetricDirt(43,18,120);
        mapBuilder.setSymmetricDirt(43,19,120);
        mapBuilder.setSymmetricDirt(43,20,120);
        mapBuilder.setSymmetricDirt(43,21,120);
        mapBuilder.setSymmetricDirt(44,10,120);
        mapBuilder.setSymmetricDirt(44,11,120);
        mapBuilder.setSymmetricDirt(44,12,120);
        mapBuilder.setSymmetricDirt(44,13,120);
        mapBuilder.setSymmetricDirt(44,18,120);
        mapBuilder.setSymmetricDirt(44,19,120);
        mapBuilder.setSymmetricDirt(44,20,120);
        mapBuilder.setSymmetricDirt(44,21,120);

        int[] xs = {0,0,1,1,1,1,1,1,1,1,2,2,2,2,2,2,3,3,3,3,3,3,3,3,4,4,4,4,5,5,10,10,10,10,14,14,16,16,17,17,17,17,17,17,18,18,18,18,18,18,18,18,18,18,19,19,19,19,19,19,19,19,19,19,20,20,20,20,20,20,20,20,20,20,21,21,21,21,21,21,21,21,22,22,22,22,22,22,22,22,22,22,23,23,23,23,23,23,23,23,24,24,24,24,24,24,29,29,29,29,29,29,29,29,30,30,30,30,30,30,30,30,30,30,30,30,30,30,31,31,31,31,31,31,31,31,31,31,31,31,32,32,32,32,32,32,32,32,32,32,32,32,33,33,33,33,33,33,33,33,33,33,33,33,34,34,35,35,35,35,35,35,36,36,36,36,36,36,36,36,36,36,36,36,37,37,37,37,37,37,37,37,37,37,38,38,38,38,38,38,38,38,38,38,39,39,39,39,39,39,40,40,40,40,40,40,41,41,41,41,41,41,42,42,42,42,42,42,42,42,43,43,43,43,43,43,44,44};
        int[] ys = {10,21,2,3,4,10,21,27,28,29,2,3,4,27,28,29,1,2,3,4,27,28,29,30,2,3,28,29,2,29,8,9,22,23,2,29,2,29,8,9,10,21,22,23,7,8,9,10,11,20,21,22,23,24,6,7,8,9,10,21,22,23,24,25,5,6,7,8,9,22,23,24,25,26,5,6,7,8,23,24,25,26,4,5,6,7,8,23,24,25,26,27,4,5,6,7,24,25,26,27,5,6,7,24,25,26,2,7,8,9,22,23,24,29,2,3,4,5,6,7,8,23,24,25,26,27,28,29,3,4,5,6,7,13,18,24,25,26,27,28,4,5,6,12,13,14,17,18,19,25,26,27,3,4,5,12,13,14,17,18,19,26,27,28,13,18,4,5,6,25,26,27,3,4,5,6,7,8,23,24,25,26,27,28,2,3,4,5,6,25,26,27,28,29,2,3,4,5,6,25,26,27,28,29,1,2,3,28,29,30,10,11,12,19,20,21,11,12,13,18,19,20,11,12,13,14,17,18,19,20,12,13,14,17,18,19,13,18};

        for (int i = 0; i < xs.length; i++) {
            mapBuilder.setSymmetricSoup(xs[i],ys[i], 50);
        }



        mapBuilder.addSymmetricCow(11,22);

        mapBuilder.setSymmetricWater(45,29, true);
        mapBuilder.setSymmetricDirt(45,29,GameConstants.MIN_WATER_ELEVATION);



        mapBuilder.saveMap(outputDirectory);

    }

    public static void addRectangleDirt(MapBuilder mapBuilder, int xl, int yb, int xr, int yt, int v) {
        for (int i = xl; i < xr+1; i++) {
            for (int j = yb; j < yt+1; j++) {
                mapBuilder.setSymmetricDirt(i, j, v);
                if (((i + j) % 10) == 0 && j >= 35)
                    try {
                        mapBuilder.addSymmetricCow(i, j);
                    } catch (RuntimeException e) {}
            }
        }
    }


    /*
     * Add a nice circular lake centered at (x,y).
     */
    public static void addSoup(MapBuilder mapBuilder, int x, int y, int r2, int v) {
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int d = (xx-x)*(xx-x)/2 + (yy-y)*(yy-y);
                if (d <= r2) {
                    mapBuilder.setSymmetricSoup(xx, yy, v*(d+v));
                }
            }
        }
    }

    /*
     * Add a nice circular lake centered at (x,y).
     */
    public static void addLake(MapBuilder mapBuilder, int x, int y, int r2, int v) {
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int d = (xx-x)*(xx-x) + (yy-y)*(yy-y);
                if (d <= r2) {
                    mapBuilder.setSymmetricWater(xx, yy, true);
                    mapBuilder.setSymmetricDirt(xx, yy, v);
                }
            }
        }
        mapBuilder.setSymmetricDirt(x, y, GameConstants.MIN_WATER_ELEVATION);
    }
}
