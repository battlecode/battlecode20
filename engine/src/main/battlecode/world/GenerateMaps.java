package battlecode.world;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Team;


import java.io.File;
import java.io.IOException;

/**
 * @author james
 *
 * so uh
 *
 * this exists
 */
public class GenerateMaps {
    public static void makeSimple() throws IOException {
        LiveMap map = new TestMapBuilder("maptestsmall", 0, 0, 32, 32, 30, 3000, 0)
                .addRobot(
                        0,
                        Team.A,
                        RobotType.HQ,
                        new MapLocation(
                                5,
                                5
                        )
                )
                .addRobot(
                        1,
                        Team.B,
                        RobotType.HQ,
                        new MapLocation(
                                26,
                                26
                        )
                )
                .addRobot(
                    2,
                    Team.NEUTRAL,
                    RobotType.COW,
                    new MapLocation(
                        10,
                        10
                    )
                )
                .addRobot(
                    3,
                    Team.NEUTRAL,
                    RobotType.COW,
                    new MapLocation(
                        4,
                        18
                    )
                )
                .setSoup()
                .setWater()
                .setPollution()
                .setDirt()
                .build();

        GameMapIO.writeMap(map, new File("engine/src/main/battlecode/world/resources/"));
        LiveMap test = GameMapIO.loadMap("maptestsmall", new File("engine/src/main/battlecode/world/resources/"));

        // System.out.println(test.toString());
    }
}
