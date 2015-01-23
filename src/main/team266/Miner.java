package team266;

import team266.RobotPlayer.BaseBot;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;


public class Miner extends BaseBot {
    public Miner(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {
        RobotPlayer.attackEnemyZero();
        RobotPlayer.mineAndMove();
        RobotPlayer.transferSupplies();
        rc.yield();
    }
}
