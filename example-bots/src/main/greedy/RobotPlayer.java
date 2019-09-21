package greedy;
import battlecode.common.*;
import battlecode.world.ObjectInfo;

import java.util.Arrays;

public class RobotPlayer {
    static RobotController rc;

    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;
        while (true) {
            // For a good cause
            if (rc.getTeamBullets() >= 10) {
                rc.donate(10);   
            }
            moveRandom();
            Clock.yield();
        }
    }

    static void moveRandom() throws GameActionException {
        Direction dir = new Direction((float)Math.random() * 2 * (float)Math.PI);
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
