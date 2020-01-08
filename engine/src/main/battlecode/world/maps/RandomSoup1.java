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
        mapBuilder.addSymmetricHQ(20, 8);


        // add some nice central soup
        addSoup(mapBuilder, 30, 20, 5, 10);
        // add some team soup
        addSoup(mapBuilder, 10, 30, 4, 5);


        for(int i = 18; i < mapBuilder.width-10; i++) {
            for (int j = 22; j < mapBuilder.height-10; j++) {
                mapBuilder.setSymmetricWater(i,j,true);
            }
        }

        for(int i = 0; i < mapBuilder.width; i++) {
            for (int j = 0; j < mapBuilder.height; j++) {
                mapBuilder.setSymmetricDirt(i, j, 4);
            }
        }

        mapBuilder.addSymmetricCow(5, 18);
        mapBuilder.addSymmetricCow(17, 21);

        mapBuilder.saveMap(outputDirectory);

    }
}
