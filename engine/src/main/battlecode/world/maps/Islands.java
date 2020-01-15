package maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Generate a map.
 */
public class Islands {

    // change this!!!
    // this needs to be the same as the name of the file
    // it also cannot be the same as the name of an existing engine map
    public static final String mapName = "Islands";

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
        width = 55;
        height = 55;
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 89123);
        mapBuilder.setWaterLevel(0);
        addRectangleWater(mapBuilder, 0, 0, 54, 54);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.horizontal);

        mapBuilder.addSymmetricHQ(27, 7);
        setHeightInCyl(mapBuilder, 27, 7, 6, 4);
        removeWaterInCyl(mapBuilder, 27, 7, 6);

        setSoupInCyl(mapBuilder, 27, 7, 6, 25);
        setSoupInCyl(mapBuilder, 27, 7, 4, 0);

        setHeightInCyl(mapBuilder, 45, 7, 3, 6);
        removeWaterInCyl(mapBuilder, 45, 7, 3);
        setSoupInCyl(mapBuilder, 45, 7, 3, 400);

        setHeightInCyl(mapBuilder, 27, 20, 2, 500);
        removeWaterInCyl(mapBuilder, 27, 20, 2);
        for (int x = 26; x <= 28; x++) {
            for (int y = 33; y <= 35; y++) {
                mapBuilder.addSymmetricCow(x, y);
            }
        }

        setHeightInCyl(mapBuilder, 8, 15, 4, 5);
        removeWaterInCyl(mapBuilder, 8, 15, 4);

        setHeightInCyl(mapBuilder, 15, 27, 5, 5);
        removeWaterInCyl(mapBuilder, 15, 27, 5);

        for (int x = 34; x <= 41; x++) {
            mapBuilder.setSymmetricDirt(x, 7, 1);
            mapBuilder.setSymmetricWater(x, 7, false);
        }

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

    public static void setHeightInRect(MapBuilder mapBuilder, int x, int y, int xf, int yf, int height) {
        for (int i = x; i <= xf && i < width; i++) {
            for(int j = y; j <= yf && j < Islands.height; j++) {
                mapBuilder.setSymmetricDirt(i,j,height);
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
