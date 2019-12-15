package battlecode.world.maps;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.world.GameMapIO;
import battlecode.world.LiveMap;
import battlecode.world.MapBuilder;
import battlecode.world.TestMapBuilder;

import java.io.File;
import java.io.IOException;

/**
 * @author james
 *
 * so uh
 *
 * this exists
 */
public class MapTestSmall {

    // change this!!!
    public static final String mapName = "maptestsmall";

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
        MapBuilder mapBuilder = new MapBuilder(mapName, 32, 32, 30);
        mapBuilder.setWaterLevel(0);
        mapBuilder.addHQ(5, 5, 0, Team.A);
        mapBuilder.addHQ(26, 26, 1, Team.B);
        mapBuilder.addCow(10, 10, 2);
        mapBuilder.addCow(4, 18, 3);

        for(int i = 0; i < mapBuilder.width; i++) {
            for (int j = 0; j < mapBuilder.height; j++) {
                mapBuilder.setSoup(i, j,  i * j + i + j);
            }
        }

        for(int i = 0; i < mapBuilder.width; i++) {
            for (int j = 0; j < mapBuilder.height; j++) {
                mapBuilder.setWater(i, j,  false);
                if (i < 4 && j < 4) {
                    mapBuilder.setWater(i,j,true);
                }
            }
        }

        for(int i = 0; i < mapBuilder.width; i++) {
            for (int j = 0; j < mapBuilder.height; j++) {
                mapBuilder.setDirt(i, j,  3);
                if (i < 16 && j < 8) {
                    mapBuilder.setDirt(i,j,2);
                }
                if (i < 8 && j < 8) {
                    mapBuilder.setDirt(i,j,1);
                }
            }
        }

        mapBuilder.saveMap(outputDirectory);

    }
}
