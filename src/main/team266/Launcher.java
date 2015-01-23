package team266;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team266.RobotPlayer.BaseBot;

public class Launcher extends BaseBot {

    public Launcher(RobotController rc) {
        super(rc);
        // TODO Auto-generated constructor stub
    }
    
    public void execute() throws GameActionException {

        RobotPlayer.attackEnemyZero();
        if (rc.isCoreReady()) {
            int rallyX = rc.readBroadcast(4);
            int rallyY = rc.readBroadcast(5);

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
