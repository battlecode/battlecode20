package nothingbot;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	static Random rand;
	
	public static void run(RobotController rc) {
		rand = new Random();
		Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
		
		while(true) {
			if (rc.getType() == RobotType.HQ) {
				try {					
                    if (rc.canAttack()) {
                        Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,100,rc.getTeam().opponent());
                        if (nearbyEnemies.length > 0) {
                            RobotInfo robotInfo = rc.senseRobotInfo(nearbyEnemies[0]);
                            if (rc.canAttackSquare(robotInfo.location)) {
                                rc.attackSquare(robotInfo.location);
                            }
                        }
                    }
				} catch (Exception e) {
					System.out.println("HQ Exception");
				}
			}
			
			rc.yield();
		}
	}
}
