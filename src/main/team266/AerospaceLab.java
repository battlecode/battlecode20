package team266;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team266.RobotPlayer.BaseBot;

public class AerospaceLab extends BaseBot{

    public AerospaceLab(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }

    public void execute() throws GameActionException {
        if (Clock.getRoundNum()<1200) {
            RobotPlayer.spawnUnit(RobotType.LAUNCHER);
            rc.yield();
        }
    }
}
