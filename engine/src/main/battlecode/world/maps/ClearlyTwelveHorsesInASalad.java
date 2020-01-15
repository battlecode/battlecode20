package maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Generate a map.
 */
public class ClearlyTwelveHorsesInASalad {

    // change this!!!
    public static final String mapName = "ClearlyTwelveHorsesInASalad";

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
        width = 50;
        height = 40;
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 4989);
        mapBuilder.setWaterLevel(0);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.rotational);
        mapBuilder.addSymmetricHQ(10, 5);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                mapBuilder.setSymmetricDirt(x, y, 3);
            }
        }

        Random r = new Random(12312);

        int[] dys = {1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3,3,3,3,3,3,3,4,4,4,4,4,4,5,5,8,8,9,9,9,9,9,9,10,10,10,10,10,10,10,10,10,10,10,10,10,10,11,11,11,11,11,11,11,11,11,11,11};
        int[] dxs = {5,6,7,15,16,23,24,32,33,34,4,5,6,7,8,14,15,16,17,22,23,24,25,31,32,33,34,35,5,6,7,8,15,16,23,24,31,32,33,34,7,8,9,30,31,32,8,31,9,30,9,11,17,22,28,30,9,10,11,12,16,17,18,21,22,23,27,28,29,30,9,10,11,12,17,18,21,22,27,28,29,30,6,7,8,9,10,11,17,18,19,20,21,22,28,29,30,31,32,33,8,10,16,17,18,19,20,21,22,23,29,31,16,17,18,21,22,23,12,17,22,27,11,12,13,26,27,28,11,12,27,28,17,18,21,22,6,7,8,16,17,18,21,22,23,31,32,33,6,7,17,22,32,33,12,13,26,27,11,12,13,14,25,26,27,28,11,12,13,14,25,26,27,28,11,12,13,14,16,23,25,26,27,28,11,12,13,14,16,23,25,26,27,28,12,27,14,25,13,14,15,24,25,26,13,14,15,16,23,24,25,26,4,5,14,15,16,23,24,25,34,35,3,4,5,6,15,16,23,24,33,34,35,36,4,5,15,16,23,24,34,35,15,16,23,24,1,2,16,17,22,23,37,38,2,3,4,5,17,22,34,35,36,37,2,3,4,5,6,7,32,33};

        for (int i = 0; i < dys.length; i++) {
            mapBuilder.setSymmetricDirt(dxs[i],dys[i]+10,1000);
        }




//        int[] xs = {0,0,1,1,1,1,1,1,1,1,2,2,2,2,2,2,3,3,3,3,3,3,3,3,4,4,4,4,5,5,10,10,10,10,14,14,16,16,17,17,17,17,17,17,18,18,18,18,18,18,18,18,18,18,19,19,19,19,19,19,19,19,19,19,20,20,20,20,20,20,20,20,20,20,21,21,21,21,21,21,21,21,22,22,22,22,22,22,22,22,22,22,23,23,23,23,23,23,23,23,24,24,24,24,24,24,29,29,29,29,29,29,29,29,30,30,30,30,30,30,30,30,30,30,30,30,30,30,31,31,31,31,31,31,31,31,31,31,31,31,32,32,32,32,32,32,32,32,32,32,32,32,33,33,33,33,33,33,33,33,33,33,33,33,34,34,35,35,35,35,35,35,36,36,36,36,36,36,36,36,36,36,36,36,37,37,37,37,37,37,37,37,37,37,38,38,38,38,38,38,38,38,38,38,39,39,39,39,39,39,40,40,40,40,40,40,41,41,41,41,41,41,42,42,42,42,42,42,42,42,43,43,43,43,43,43,44,44};
//        int[] ys = {10,21,2,3,4,10,21,27,28,29,2,3,4,27,28,29,1,2,3,4,27,28,29,30,2,3,28,29,2,29,8,9,22,23,2,29,2,29,8,9,10,21,22,23,7,8,9,10,11,20,21,22,23,24,6,7,8,9,10,21,22,23,24,25,5,6,7,8,9,22,23,24,25,26,5,6,7,8,23,24,25,26,4,5,6,7,8,23,24,25,26,27,4,5,6,7,24,25,26,27,5,6,7,24,25,26,2,7,8,9,22,23,24,29,2,3,4,5,6,7,8,23,24,25,26,27,28,29,3,4,5,6,7,13,18,24,25,26,27,28,4,5,6,12,13,14,17,18,19,25,26,27,3,4,5,12,13,14,17,18,19,26,27,28,13,18,4,5,6,25,26,27,3,4,5,6,7,8,23,24,25,26,27,28,2,3,4,5,6,25,26,27,28,29,2,3,4,5,6,25,26,27,28,29,1,2,3,28,29,30,10,11,12,19,20,21,11,12,13,18,19,20,11,12,13,14,17,18,19,20,12,13,14,17,18,19,13,18};
//
//        for (int i = 0; i < xs.length; i++) {
//            mapBuilder.setSymmetricSoup(xs[i],ys[i], 50);
//        }





        setWaterInCyl(mapBuilder, 41, 6, 4);
        setHeightInCyl(mapBuilder, 41, 6, 4, -200);
        mapBuilder.setSymmetricDirt(41,5,GameConstants.MIN_WATER_ELEVATION);


        addRectangleDirt(mapBuilder, 14, 0, 17, 10, 20);

        setSoupInCyl(mapBuilder,3,3,3,70);
        setSoupInCyl(mapBuilder, 41, 6, 5, 100);
        setSoupInCyl(mapBuilder, 41, 6, 4, 0);

        mapBuilder.addSymmetricCow(17,11);
        mapBuilder.addSymmetricCow(14,11);

        mapBuilder.saveMap(outputDirectory);

    }
    public static void setWaterInCyl(MapBuilder mapBuilder, float centerX, float centerY, float radius) {
        for (int[] point : pointsInCyl(centerX, centerY, radius)) {
            mapBuilder.setSymmetricWater(point[0],point[1],true);
        }
    }
    public static void setHeightInCyl(MapBuilder mapbuilder, float centerX, float centerY, float radius, int height) {
        for (int [] point : pointsInCyl(centerX, centerY, radius)) {
            mapbuilder.setSymmetricDirt(point[0], point[1], height);
        }
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
    public static void addRectangleWater(MapBuilder mapBuilder, int xl, int yb, int xr, int yt, int v) {
        for (int i = xl; i < xr+1; i++) {
            for (int j = yb; j < yt+1; j++) {
                mapBuilder.setSymmetricDirt(i, j, v);
                mapBuilder.setSymmetricWater(i, j, true);
            }
        }
    }
    public static void setSoupInCyl(MapBuilder mapbuilder, float centerX, float centerY, float radius, int soup) {
        for (int [] point : pointsInCyl(centerX, centerY, radius)) {
            mapbuilder.setSymmetricSoup(point[0], point[1], soup);
        }
    }
    public static int[][] pointsInCyl(float centerX, float centerY, float radius) {
        ArrayList<int[]> points = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2)) <= radius)
                    points.add(new int[]{x, y});
            }
        }
        return points.toArray(new int[0][]);
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
