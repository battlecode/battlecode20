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
        LiveMap map = new TestMapBuilder("maptest", 0, 0, 100, 100,30,3000)
                .addRobot(
                        0,
                        Team.A,
                        RobotType.HQ,
                        new MapLocation(
                                1,
                                1
                        )
                )
                .addRobot(
                        1,
                        Team.B,
                        RobotType.HQ,
                        new MapLocation(
                                99,
                                99
                        )
                )
                .addRobot(
                    2,
                    Team.NEUTRAL,
                    RobotType.COW,
                    new MapLocation(
                        50,
                        50
                    )
                )
                .setSoup()
                .setWater()
                .setPollution()
                .build();
        GameMapIO.writeMap(map, new File("/Users/mnocito/desktop/battlecode20/engine/src/main/battlecode/world/resources/"));
        LiveMap test = GameMapIO.loadMap("maptest", new File("/Users/mnocito/desktop/battlecode20/engine/src/main/battlecode/world/resources"));
        System.out.println(test.toString());

    }
}
