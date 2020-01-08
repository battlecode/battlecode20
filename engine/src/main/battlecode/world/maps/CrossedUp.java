package battlecode.world.maps;

import battlecode.world.MapBuilder;

import java.io.IOException;

/**
 * Generate a map.
 */
public class CrossedUp {

    // change this!!!
    public static final String mapName = "CrossedUp";

    // don't change this!!
    public static final String outputDirectory = "engine/src/main/battlecode/world/resources/";

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
        MapBuilder mapBuilder = new MapBuilder(mapName, 41, 41, 432);
        mapBuilder.setWaterLevel(0);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.rotational);
        mapBuilder.addSymmetricHQ(7, 7);

        // add soup close to HQ
        mapBuilder.setSymmetricSoup(9, 9, 1000);
        mapBuilder.setSymmetricSoup(9, 8, 1000);
        mapBuilder.setSymmetricSoup(8, 9, 1000);
        mapBuilder.setSymmetricSoup(8, 8, 1000);

        mapBuilder.addSymmetricCow(36,36);
        mapBuilder.addSymmetricCow(35,35);

        for (int i = 17; i < mapBuilder.width-17; i++) {
            for (int j = 0; j < 3; j++) {
                mapBuilder.setSymmetricSoup(i, j, 200*(j+1));
            }
        }

        for(int i = 19; i < mapBuilder.width-19; i++) {
            for (int j = 19; j < mapBuilder.height-19; j++) {
                mapBuilder.setSymmetricDirt(i,j,8);
            }
        }

        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mapBuilder.setSymmetricWater(i,j,true);
            }
        }

        mapBuilder.setSymmetricDirt(9, 9, 2);
        mapBuilder.setSymmetricDirt(9, 8, 2);
        mapBuilder.setSymmetricDirt(8, 9, 2);
        mapBuilder.setSymmetricDirt(8, 8, 2);

        mapBuilder.saveMap(outputDirectory);

    }
}
