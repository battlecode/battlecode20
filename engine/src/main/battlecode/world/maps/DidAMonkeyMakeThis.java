package maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Generate a map.
 */
public class DidAMonkeyMakeThis {

    // change this!!!
    // this needs to be the same as the name of the file
    // it also cannot be the same as the name of an existing engine map
    public static final String mapName = "DidAMonkeyMakeThis";

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
        width = 56;
        height = 41;
        MapBuilder m = new MapBuilder(mapName, width, height, 89345);
        m.setWaterLevel(0);
        m.setSymmetry(MapBuilder.MapSymmetry.horizontal);
        addRectangleWater(m, 0, 0, 55, 40);

        m.addSymmetricHQ(9, 7);
        makeHill(m, 9, 7, 4, -2, 5);
        removeWaterInCyl(m, 9, 7, 4);
        setSoupInCyl(m, 9, 7, 4, 50);
        setSoupInCyl(m, 9, 7, 3, 0);

        makeSoupHill(m, 15, 31, 3, 1, 4);
        removeWaterInCyl(m, 15, 31, 3);

        removeWaterInCyl(m, 11, 15, 4.3f);
        makeHill(m, 11, 16, 4.3f, 2, 2);

        buildCowFarmInCyl(m, 17, 36, 2);
        removeWaterInCyl(m, 17, 36, 2);

        removeWaterInCyl(m, 6, 20, 5);
        makeHill(m, 6, 20, 3, 0, 489);

        makeSoupHill2(m, 24, 23, 8, 0, 60);
        removeWaterInCyl(m, 24, 23, 8);

        addRectangleSoupDimensioned(m, 38, 27, 10, 7, 20);

        buildCowFarmInCyl(m, 51, 22, 1);
        removeWaterInCyl(m, 51, 22, 1);

        removeWaterInCyl(m, 38, 24, 7);
        makeHill(m, 38, 24, 7, 20, 680);

        m.saveMap(outputDirectory);
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

    public static void buildCowFarmInCyl(MapBuilder mapBuilder, float centerX, float centerY, float radius) {
        for (int[] point : pointsInCyl(centerX, centerY, radius)) {
            buildCowFarm(mapBuilder, point[0],point[1]);
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
                mapBuilder.setSymmetricWater(xl + i, yb + j, false);
                mapBuilder.setSymmetricDirt(xl+i, yb+j, 1);
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

    public static void buildCowFarm(MapBuilder mb, int x, int y) {
        mb.addSymmetricCow(x, y);
        mb.setSymmetricDirt(x, y, Integer.MAX_VALUE / 2);
    }
}
