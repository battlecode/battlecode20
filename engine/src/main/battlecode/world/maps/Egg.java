package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generate a map.
 */
public class Egg {

    // change this!!!
    public static final String mapName = "Egg";

    // don't change this!!
    public static final String outputDirectory = "engine/src/main/battlecode/world/resources/";

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
        width = 38;
        height = 34;
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 4444);
        mapBuilder.setWaterLevel(0);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.horizontal);
        mapBuilder.addSymmetricHQ(9, 6);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                mapBuilder.setSymmetricDirt(x,y,1);
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Math.pow(x-9, 2) + Math.pow(y-6, 2) < 400) {
                    mapBuilder.setSymmetricDirt(x,y,3);
                }
                if (Math.pow(x-9, 2) + Math.pow(y-6, 2) < 40) {
                    mapBuilder.setSymmetricDirt(x,y,4);
                }
            }
        }

        Random r = new Random(12312);

        int[] dys = {1,12,21,32,1,8,9,10,11,12,13,20,21,22,23,24,25,32,7,8,9,10,11,12,21,22,23,24,25,26,8,9,10,23,24,25,3,8,9,10,23,24,25,30,2,3,4,8,9,10,23,24,25,29,30,31,2,3,4,9,24,29,30,31,3,4,29,30,4,12,13,20,21,29,12,21,12,21,2,3,7,8,25,26,30,31,2,3,4,5,6,7,8,11,12,21,22,25,26,27,28,29,30,31,2,3,4,5,6,7,11,12,13,20,21,22,26,27,28,29,30,31,3,4,5,6,7,10,11,12,13,14,19,20,21,22,23,26,27,28,29,30,3,4,5,6,10,11,12,13,20,21,22,23,27,28,29,30,3,4,5,6,10,11,12,21,22,23,27,28,29,30,3,4,5,10,11,12,21,22,23,28,29,30,2,3,4,5,10,11,22,23,28,29,30,31,2,3,4,5,28,29,30,31,1,2,3,4,29,30,31,32,1,2,3,30,31,32,1,2,3,4,29,30,31,32,1,2,3,4,5,28,29,30,31,32,1,2,3,4,5,28,29,30,31,32,1,2,3,4,5,11,12,21,22,28,29,30,31,32,1,2,3,4,10,11,22,23,29,30,31,32,1,2,3,30,31,32,1,2,10,23,31,32,10,11,22,23,10,11,22,23,10,11,22,23,5,28,3,4,5,28,29,30,3,30};
        int[] dxs = {1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,3,3,4,4,4,4,4,4,5,5,5,5,5,5,5,5,6,6,6,6,6,6,6,6,6,6,6,6,7,7,7,7,7,7,7,7,8,8,8,8,9,9,9,9,9,9,10,10,11,11,12,12,12,12,12,12,12,12,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,15,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,17,17,17,17,17,17,17,17,17,17,17,17,17,17,18,18,18,18,18,18,18,18,18,18,18,18,19,19,19,19,19,19,19,19,19,19,19,19,20,20,20,20,20,20,20,20,21,21,21,21,21,21,21,21,22,22,22,22,22,22,23,23,23,23,23,23,23,23,24,24,24,24,24,24,24,24,24,24,25,25,25,25,25,25,25,25,25,25,26,26,26,26,26,26,26,26,26,26,26,26,26,26,27,27,27,27,27,27,27,27,27,27,27,27,28,28,28,28,28,28,29,29,29,29,29,29,30,30,30,30,31,31,31,31,32,32,32,32,33,33,34,34,34,34,34,34,35,35};

        for (int i = 0; i < dxs.length; i++) {
            mapBuilder.setSymmetricDirt(dxs[i],dys[i],1000);
        }

        mapBuilder.setSymmetricDirt(37, 33, 3);



//        int[] xs = {0,0,1,1,1,1,1,1,1,1,2,2,2,2,2,2,3,3,3,3,3,3,3,3,4,4,4,4,5,5,10,10,10,10,14,14,16,16,17,17,17,17,17,17,18,18,18,18,18,18,18,18,18,18,19,19,19,19,19,19,19,19,19,19,20,20,20,20,20,20,20,20,20,20,21,21,21,21,21,21,21,21,22,22,22,22,22,22,22,22,22,22,23,23,23,23,23,23,23,23,24,24,24,24,24,24,29,29,29,29,29,29,29,29,30,30,30,30,30,30,30,30,30,30,30,30,30,30,31,31,31,31,31,31,31,31,31,31,31,31,32,32,32,32,32,32,32,32,32,32,32,32,33,33,33,33,33,33,33,33,33,33,33,33,34,34,35,35,35,35,35,35,36,36,36,36,36,36,36,36,36,36,36,36,37,37,37,37,37,37,37,37,37,37,38,38,38,38,38,38,38,38,38,38,39,39,39,39,39,39,40,40,40,40,40,40,41,41,41,41,41,41,42,42,42,42,42,42,42,42,43,43,43,43,43,43,44,44};
//        int[] ys = {10,21,2,3,4,10,21,27,28,29,2,3,4,27,28,29,1,2,3,4,27,28,29,30,2,3,28,29,2,29,8,9,22,23,2,29,2,29,8,9,10,21,22,23,7,8,9,10,11,20,21,22,23,24,6,7,8,9,10,21,22,23,24,25,5,6,7,8,9,22,23,24,25,26,5,6,7,8,23,24,25,26,4,5,6,7,8,23,24,25,26,27,4,5,6,7,24,25,26,27,5,6,7,24,25,26,2,7,8,9,22,23,24,29,2,3,4,5,6,7,8,23,24,25,26,27,28,29,3,4,5,6,7,13,18,24,25,26,27,28,4,5,6,12,13,14,17,18,19,25,26,27,3,4,5,12,13,14,17,18,19,26,27,28,13,18,4,5,6,25,26,27,3,4,5,6,7,8,23,24,25,26,27,28,2,3,4,5,6,25,26,27,28,29,2,3,4,5,6,25,26,27,28,29,1,2,3,28,29,30,10,11,12,19,20,21,11,12,13,18,19,20,11,12,13,14,17,18,19,20,12,13,14,17,18,19,13,18};
//
//        for (int i = 0; i < xs.length; i++) {
//            mapBuilder.setSymmetricSoup(xs[i],ys[i], 50);
//        }


        addLake(mapBuilder, 32, 16, 9, -100);

        mapBuilder.setSymmetricDirt(32,16,GameConstants.MIN_WATER_ELEVATION);


        addSoup(mapBuilder,12,4,2,13);
        addSoup(mapBuilder,22,16,25,5);

        mapBuilder.addSymmetricCow(3,4);
        mapBuilder.addSymmetricCow(1,13);
//
//        mapBuilder.addSymmetricCow(10,14);
//        mapBuilder.addSymmetricCow(20,57);
//        mapBuilder.addSymmetricCow(39,39);

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
