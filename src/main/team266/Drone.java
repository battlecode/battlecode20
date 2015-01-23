package team266;

import team266.RobotPlayer.BaseBot;
import battlecode.common.*;

public class Drone extends BaseBot {
    public Drone(RobotController rc) {
        super(rc);
    }

    public void execute() throws GameActionException {

        RobotPlayer.attackEnemyZero();
        if (rc.isCoreReady()) {
            int rallyX = rc.readBroadcast(2);
            int rallyY = rc.readBroadcast(3);
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