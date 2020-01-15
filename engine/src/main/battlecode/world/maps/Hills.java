package maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Generate a map.
 */
public class Hills {

    // change this!!!
    // this needs to be the same as the name of the file
    // it also cannot be the same as the name of an existing engine map
    public static final String mapName = "Hills";

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

    public static void makeSimple() throws IOException {
        width = 60;
        height = 60;
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 198248);
        mapBuilder.setWaterLevel(0);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.rotational);

        mapBuilder.addSymmetricHQ(14, 11);
        makeHill(mapBuilder, 14, 11, 5, -1, 4);

        addRectangleWater(mapBuilder, 0, 0, 0, 59);
        addRectangleWater(mapBuilder, 0, 0, 59, 0);
        addRectangleWater(mapBuilder, 59, 0, 59, 59);
        addRectangleWater(mapBuilder, 0, 59, 59, 59);

        addRectangleWater(mapBuilder, 29, 29, 30, 30);

        makeHill(mapBuilder, 3, 8, 6, -10, 30);
        makeHill(mapBuilder, 10, 33, 9, -15, 55);
        makeHill(mapBuilder, 26, 17, 7, 0, 120);

        makeSoupHill(mapBuilder, 18, 8, 2.5f, -1, 3);

        setSoupInCyl(mapBuilder, 14, 51, 6.4f, 30);
        makeSoupHill2(mapBuilder, 14, 51, 5.7f, 0, 400);

        mapBuilder.addSymmetricCow(24, 52);
        mapBuilder.addSymmetricCow(35, 30);

        mapBuilder.saveMap(outputDirectory);

    }

    public static void setSoupInCyl(MapBuilder mapbuilder, float centerX, float centerY, float radius, int soup) {
        for (int [] point : pointsInCyl(centerX, centerY, radius)) {
            mapbuilder.setSymmetricSoup(point[0], point[1], soup);
        }
    }
    public static void setHeightInCyl(MapBuilder mapbuilder, float centerX, float centerY, float radius, int height) {
        for (int [] point : pointsInCyl(centerX, centerY, radius)) {
            mapbuilder.setSymmetricDirt(point[0], point[1], height);
        }
    }

    public static void makeHill(MapBuilder m, float x, float y, float r, int hmin, int hmax) {
        int diff = (int)((hmax - hmin) / r);
        for (int i = (int)r; i >= 0; i--) {
            for (int[] p : pointsInCyl(x, y, i)) {
                m.setSymmetricDirt(p[0], p[1], hmax - i * diff);
            }
        }
    }

    public static void makeSoupHill(MapBuilder m, float x, float y, float r, int hmin, int hmax) {
        int diff = (int)((hmax - hmin) / r);
        for (int i = (int)r; i >= 0; i--) {
            for (int[] p : pointsInCyl(x, y, i)) {
                m.setSymmetricDirt(p[0], p[1], hmax - i * diff);
                m.setSymmetricSoup(p[0], p[1], (hmax - i * diff) * 40 + 50);
            }
        }
    }

    public static void makeSoupHill2(MapBuilder m, float x, float y, float r, int hmin, int hmax) {
        int diff = (int)((hmax - hmin) / r);
        for (int i = (int)r; i >= 0; i--) {
            for (int[] p : pointsInCyl(x, y, i)) {
                m.setSymmetricDirt(p[0], p[1], hmax - i * diff);
                m.setSymmetricSoup(p[0], p[1], (int)((hmax - i * diff) * 0.3));
            }
        }
    }


    public static void removeWaterInCyl(MapBuilder mapBuilder, float centerX, float centerY, float radius) {
        for (int[] point : pointsInCyl(centerX, centerY, radius)) {
            mapBuilder.setSymmetricWater(point[0],point[1],false);
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

    public static void addRectangleSoup(MapBuilder mapBuilder, int xl, int yb, int xr, int yt, int v) {
        for (int i = xl; i < xr+1; i++) {
            for (int j = yb; j < yt+1; j++) {
                mapBuilder.setSymmetricSoup(i, j, v);
            }
        }
    }
    public static void addRectangleSoupDimensioned(MapBuilder mapBuilder, int xl, int yb, int w, int h, int v) {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                mapBuilder.setSymmetricSoup(xl + i, yb + j, v);
            }
        }
    }

    public static void addRectangleWater(MapBuilder mapBuilder, int xl, int yb, int xr, int yt) {
        for (int i = xl; i < xr+1; i++) {
            for (int j = yb; j < yt+1; j++) {
                mapBuilder.setSymmetricWater(i, j, true);
                mapBuilder.setSymmetricDirt(i,j, GameConstants.MIN_WATER_ELEVATION);
            }
        }
    }
}
