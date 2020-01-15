package battlecode.world.maps;

import battlecode.common.GameConstants;
import battlecode.world.MapBuilder;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Generate a map.
 */
public class RandomSoup1 {

    // change this!!!
    // this needs to be the same as the name of the file
    // it also cannot be the same as the name of an existing engine map
    public static final String mapName = "RandomSoup1";

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
        width = 41;
        height = 56;
        MapBuilder mapBuilder = new MapBuilder(mapName, width, height, 148);
        mapBuilder.setWaterLevel(0);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.horizontal);
        mapBuilder.addSymmetricHQ(18, 6);

        addRectangleSoup(mapBuilder,2, 4, 2+1,4+1, 130);
        addRectangleSoup(mapBuilder,3, 8,3+1,8+1, 210);
        addRectangleSoup(mapBuilder, 6, 5,6+1,5+1, 100);
        addRectangleSoup(mapBuilder,6, 19,6+1,19+1, 150);
        addRectangleSoup(mapBuilder,7, 20,7+1,20+1, 80);
        addRectangleSoup(mapBuilder,18, 1,18+1, 1+1,280);
        addRectangleSoup(mapBuilder,18, 3,18+1,3+1, 280);
        addRectangleSoup(mapBuilder,19, 5,19+1,5+1, 580);
        addRectangleSoup(mapBuilder,17, 4,17+1,4+1, 280);
        addRectangleSoup(mapBuilder,18, 0,18+1,0+1, 280);
        addRectangleSoup(mapBuilder,19, 5,19+1,5+1, 60);
        addRectangleSoup(mapBuilder,20, 4,20+1,4+1, 50);
        addRectangleSoup(mapBuilder,21, 17,21+1,17, 60+1);
        addRectangleSoup(mapBuilder,24, 7,24+1,7+1, 130);
        addRectangleSoup(mapBuilder,27, 8,27+1,8+1, 210);
        addRectangleSoup(mapBuilder,30, 5,30+1,5+1, 100);
        addRectangleSoup(mapBuilder,34, 20,34+1,20, 800+1);
        addRectangleSoup(mapBuilder,34, 15,34+1,15, 140+1);
        addRectangleSoup(mapBuilder,35, 4,35+1,4+1, 100);
        addRectangleSoup(mapBuilder,39, 25,39+1,25, 280+1);


        for(int i = 0; i < mapBuilder.width; i++) {
            for (int j = 0; j < mapBuilder.height; j++) {
                mapBuilder.setSymmetricDirt(i, j, 4);
            }
        }

        for(int i = 11; i < mapBuilder.width-15; i++) {
            for (int j = 25; j < mapBuilder.height-19; j++) {
                mapBuilder.setSymmetricWater(i,j,true);
                mapBuilder.setSymmetricDirt(i, j, GameConstants.MIN_WATER_ELEVATION);
            }
        }

        mapBuilder.addSymmetricCow(5, 18);
        mapBuilder.addSymmetricCow(17, 3);

        mapBuilder.saveMap(outputDirectory);

    }

    public static void addRectangleSoup(MapBuilder mapBuilder, int xl, int yb, int xr, int yt, int v) {
        for (int i = xl; i < xr+1; i++) {
            for (int j = yb; j < yt+1; j++) {
                mapBuilder.setSymmetricSoup(i, j, v);
            }
        }
    }
}
