package battlecode.world;

import battlecode.common.MapLocation;
import battlecode.common.RobotType;
import battlecode.common.Team;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author james
 *
 * so uh
 *
 * this exists
 */
@Ignore
public class GenerateMaps {
    @Test
    public void makeSimple() throws IOException {
        LiveMap map = new TestMapBuilder("simple", 0, 0, 100, 100,30,3000)
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
                .build();

        GameMapIO.writeMap(map, new File("src/main/battlecode/world/resources"));

    }
}
