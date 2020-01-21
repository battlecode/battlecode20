package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Generate a map.
 */
public class Volcano {

    // change this!!!
    // this needs to be the same as the name of the file
    // it also cannot be the same as the name of an existing engine map
    public static final String mapName = "Volcano";

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

    public static void makeSimple() throws IOException {
        width = 56;
        height = 56;
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 148);
        mapBuilder.setWaterLevel(0);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.rotational);


        //center hill
        setHeightInCyl(mapBuilder, 28, 28, 20, 2);
        setHeightInCyl(mapBuilder, 28, 28, 18, 4);
        setHeightInCyl(mapBuilder, 28, 28, 14, 7);
        setHeightInCyl(mapBuilder, 28, 28, 10, 9);
        setHeightInCyl(mapBuilder, 28, 28, 8, 16);
        setHeightInCyl(mapBuilder, 28, 28, 6, 25);
        setHeightInCyl(mapBuilder, 28,28,5, 30);
        setHeightInCyl(mapBuilder, 28,28,2, 35);

        setSoupInCyl(mapBuilder, 28,28, 6, 30);


        //corner volcanos
        setHeightInCyl(mapBuilder, 8,48,10, 2);
        setHeightInCyl(mapBuilder, 8,48,8, 4);
        setHeightInCyl(mapBuilder, 8,48,7, 7);
        setHeightInCyl(mapBuilder, 8,48,6, 9);
        setHeightInCyl(mapBuilder, 8,48,5, 12);
        setHeightInCyl(mapBuilder, 8,48,4, 15);

        //poking holes in the volcano
        setHeightInRect(mapBuilder, 8,40,8,48,5);


        setHeightInCyl(mapBuilder, 8, 48, 2.5f, GameConstants.MIN_WATER_ELEVATION);
        setWaterInCyl(mapBuilder, 8,48,2.5f);

        setSoupInCyl(mapBuilder, 8, 48, 5,20);


        //hq hills
        setHeightInCyl(mapBuilder, 6,6, 9, 5);
        setHeightInCyl(mapBuilder, 6,6, 8, 5);
        setHeightInCyl(mapBuilder, 6,6, 7, 4);


        //soup-rings
        setSoupInCyl(mapBuilder, 6,6,8, 60);
        setSoupInCyl(mapBuilder, 6,6,7, 0);

        setSoupInCyl(mapBuilder, 6,6,14, 80);
        setSoupInCyl(mapBuilder, 6,6,13, 0);

        mapBuilder.setSymmetricSoup(7, 7, 1500);


        mapBuilder.addSymmetricHQ(6, 6);

        mapBuilder.addSymmetricCow(5, 18);
        mapBuilder.addSymmetricCow(17, 3);

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
            for(int j = y; j <= yf && j < Volcano.height; j++) {
                mapBuilder.setSymmetricDirt(i,j,height);
            }
        }
    }
    public static void setWaterInCyl(MapBuilder mapBuilder, float centerX, float centerY, float radius) {
        for (int[] point : pointsInCyl(centerX, centerY, radius)) {
            mapBuilder.setSymmetricWater(point[0],point[1],true);
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
}
