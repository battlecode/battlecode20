package team266;

import java.util.Random;

import team266.RobotPlayer.BaseBot;
import battlecode.common.*;

public  class Soldier extends BaseBot {
    Random rand;
    public Soldier(RobotController rc) {
        super(rc);
        rand = new Random(rc.getID());
    }

    public void execute() throws GameActionException {

        RobotPlayer.attackEnemyZero();
        if (rc.isCoreReady()) {
            int rallyX = rc.readBroadcast(0);
            int rallyY = rc.readBroadcast(1);

            MapLocation rallyPoint = new MapLocation(rallyX,rallyY);

            Direction newDir = this.getMoveDir(rallyPoint);
            if (newDir != null) {
                rc.move(newDir);
            }
            
        }
        
        RobotPlayer.move();
        RobotPlayer.transferSupplies();
        rc.yield();
    }
}