package battlecode.world.maps;

import battlecode.world.MapBuilder;

import java.io.IOException;

/**
 * Generate a map.
 */
public class CentralSoup {

    // change this!!!
    public static final String mapName = "CentralSoup";

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
        MapBuilder mapBuilder = new MapBuilder(mapName, 48, 48, 219);
        mapBuilder.setWaterLevel(0);
        mapBuilder.setSymmetry(MapBuilder.MapSymmetry.rotational);
        mapBuilder.addSymmetricHQ(10, 10);

        mapBuilder.setSymmetricSoup(6, 6, 400);
        mapBuilder.setSymmetricSoup(5, 6, 400);
        mapBuilder.setSymmetricSoup(6, 5, 400);
        mapBuilder.setSymmetricSoup(5, 5, 400);

        for (int i = 22; i <= 25; i++) {
            for (int j = 22; j <= 25; j++) {
                mapBuilder.setSymmetricSoup(i, j, 800);
            }
        }

        for(int i = 0; i < mapBuilder.width; i++) {
            for (int j = 0; j < mapBuilder.height; j++) {
                mapBuilder.setSymmetricDirt(i, j,  3);
            }
        }

        for (int i = 21; i <= 26; i++) {
            mapBuilder.setSymmetricDirt(i, 21, 15);
            mapBuilder.setSymmetricDirt(i, 26, 15);
            mapBuilder.setSymmetricDirt(21, i, 15);
            mapBuilder.setSymmetricDirt(26, i, 15);
        }
        

        for(int i = 0; i < mapBuilder.width; i++) {
            for (int j = 0; j < 1; j++) {
                mapBuilder.setSymmetricWater(i,j,true);
            }
        }

        mapBuilder.addSymmetricCow(12, 14);
        mapBuilder.addSymmetricCow(8, 18);

        mapBuilder.saveMap(outputDirectory);

    }
}
