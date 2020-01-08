package battlecode.world.maps;

import battlecode.world.MapBuilder;

import java.io.IOException;

/**
 * Generate a map.
 */
public class RandomSoup1 {

    // change this!!!
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

        mapBuilder.setSymmetricSoup(2, 4, 130);
        mapBuilder.setSymmetricSoup(3, 8, 210);
        mapBuilder.setSymmetricSoup(6, 5, 100);
        mapBuilder.setSymmetricSoup(6, 19, 150);
        mapBuilder.setSymmetricSoup(7, 20, 80);
        mapBuilder.setSymmetricSoup(18, 1, 280);
        mapBuilder.setSymmetricSoup(19, 5, 60);
        mapBuilder.setSymmetricSoup(20, 4, 50);
        mapBuilder.setSymmetricSoup(21, 17, 60);
        mapBuilder.setSymmetricSoup(24, 7, 130);
        mapBuilder.setSymmetricSoup(27, 8, 210);
        mapBuilder.setSymmetricSoup(30, 5, 100);
        mapBuilder.setSymmetricSoup(34, 20, 80);
        mapBuilder.setSymmetricSoup(34, 15, 140);
        mapBuilder.setSymmetricSoup(35, 4, 100);
        mapBuilder.setSymmetricSoup(39, 25, 280);
        mapBuilder.setSymmetricSoup(41, 16, 80);
        mapBuilder.setSymmetricSoup(42, 9, 150);
        mapBuilder.setSymmetricSoup(44, 1, 280);
        mapBuilder.setSymmetricSoup(46, 5, 60);
        mapBuilder.setSymmetricSoup(49, 4, 50);
        mapBuilder.setSymmetricSoup(52, 17, 60);

        for(int i = 11; i < mapBuilder.width-15; i++) {
            for (int j = 25; j < mapBuilder.height-19; j++) {
                mapBuilder.setSymmetricWater(i,j,true);
            }
        }

        for(int i = 0; i < mapBuilder.width; i++) {
            for (int j = 0; j < mapBuilder.height; j++) {
                mapBuilder.setSymmetricDirt(i, j, 4);
            }
        }

        mapBuilder.addSymmetricCow(5, 18);
        mapBuilder.addSymmetricCow(17, 3);

        mapBuilder.saveMap(outputDirectory);

    }
}
