package team266;

import team266.RobotPlayer.BaseBot;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;


public class Barracks extends BaseBot {
    public Barracks(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
        RobotPlayer.spawnUnit(RobotType.SOLDIER);
        RobotPlayer.transferSupplies();
        rc.yield();
    }
}