package battlecode.world.maps;

import battlecode.world.MapBuilder;

import java.io.IOException;

/**
 * Generate a map.
 */
public class CowFarm {

    // change this!!!
    // this needs to be the same as the name of the file
    // it also cannot be the same as the name of an existing engine map
    public static final String mapName = "CowFarm";

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
        width = 40;
        height = 40;
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 148);
        mapBuilder.setWaterLevel(0);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.vertical);
        mapBuilder.addSymmetricHQ(1, 1);

        addRectangleSoup(mapBuilder, 3, 4, 5, 6, 6);
        addRectangleSoup(mapBuilder, 1, 11, 2, 14, 90);
        addRectangleSoup(mapBuilder, 6, 8, 8, 10, 31);
        addRectangleSoup(mapBuilder, 11, 20, 13, 21, 330);
        addRectangleSoup(mapBuilder, 7, 20, 7, 20, 80);
        addRectangleSoup(mapBuilder, 18, 1, 18, 1, 2);
        addRectangleSoup(mapBuilder, 18, 3, 18, 3, 5);
        addRectangleSoup(mapBuilder, 10, 5, 11, 5, 10);
        addRectangleSoup(mapBuilder, 17, 4, 17, 4, 8);
        addRectangleSoup(mapBuilder, 18, 0, 18, 1, 18);
        addRectangleSoup(mapBuilder, 19, 5, 21, 5, 60);
        addRectangleSoup(mapBuilder, 20, 4, 20, 4, 50);
        addRectangleSoup(mapBuilder, 21, 17, 22, 17, 60);
        addRectangleSoup(mapBuilder, 21, 28, 22, 29, 60);
        addRectangleSoup(mapBuilder, 14, 7, 21, 7, 130);
        addRectangleSoup(mapBuilder, 27, 8, 27, 8, 210);
        addRectangleSoup(mapBuilder, 30, 5, 30, 5, 100);
        addRectangleSoup(mapBuilder, 34, 20, 34, 20, 89);
        addRectangleSoup(mapBuilder, 34, 15, 34, 15, 329);
        addRectangleSoup(mapBuilder, 35, 4, 35, 4, 4);
        addRectangleSoup(mapBuilder, 38, 25, 39, 25, 280);
        addRectangleSoup(mapBuilder, 18, 2, 19, 3, 243);
        addRectangleSoup(mapBuilder, 20, 26, 20, 26, 432);
        addRectangleSoup(mapBuilder, 34, 1, 35, 2, 43);
        addRectangleSoup(mapBuilder, 37, 5, 37, 5, 60);
        addRectangleSoup(mapBuilder, 36, 4, 39, 4, 50);
        addRectangleSoup(mapBuilder, 13, 17, 16, 19, 60);


        for(int i = 0; i < mapBuilder.width; i++) {
            for (int j = 0; j < mapBuilder.height; j++) {
                mapBuilder.setSymmetricDirt(i, j, 4);
            }
        }

        for(int i = 18; i < 22; i++) {
            for (int j = 17; j < 23; j++) {
                mapBuilder.setSymmetricWater(i,j,true);
                mapBuilder.setSymmetricDirt(i, j, Integer.MIN_VALUE / 2);
            }
        }

        buildCowFarm(mapBuilder, 6, 6);
        buildCowFarm(mapBuilder, 6, 7);

        mapBuilder.saveMap(outputDirectory);

    }

    public static void addRectangleSoup(MapBuilder mapBuilder, int xl, int yb, int xr, int yt, int v) {
        for (int i = xl; i < xr+1; i++) {
            for (int j = yb; j < yt+1; j++) {
                mapBuilder.setSymmetricSoup(i, j, v);
            }
        }
    }

    public static void buildCowFarm(MapBuilder mb, int x, int y) {
        mb.addSymmetricCow(x, y);
        mb.setSymmetricDirt(x, y, Integer.MAX_VALUE / 2);
    }
}
