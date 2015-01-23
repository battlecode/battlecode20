package team266;

import java.util.*;

import team266.RobotPlayer.BaseBot;
import battlecode.common.*;

public class Helipad extends BaseBot {

    public Helipad(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
        if (Clock.getRoundNum()<900) {
        RobotPlayer.spawnUnit(RobotType.DRONE);
        } else {
            RobotPlayer.buildUnit(RobotType.AEROSPACELAB);
        }
        RobotPlayer.transferSupplies();
        rc.yield();
    }
}
