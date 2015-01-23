package team266;

import team266.RobotPlayer.BaseBot;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class MinerFactory extends BaseBot {
    public MinerFactory(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
//        if (Clock.getRoundNum() < 1400) { 
            RobotPlayer.spawnUnit(RobotType.MINER);
//        }
        RobotPlayer.transferSupplies();
        rc.yield();
    }
}