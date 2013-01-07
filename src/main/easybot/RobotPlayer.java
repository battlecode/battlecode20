package easybot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;

/** Haitao's first attempt at a good rush bot. Will disregard mining, capturing, and upgrades, 
 * and focus on pure rushing.
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			turn: try {
				if (rc.getType() == RobotType.HQ) {
					rc.setIndicatorString(0, "delay: "+rc.roundsUntilActive());
					
					if (rc.isActive()) {
						Direction dir = Direction.values()[(int)(Math.random()*8)];
						if (rc.canMove(dir))
							rc.spawn(dir);
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (!rc.isActive()) {
							rc.broadcast(rc.getRobot().getID()%GameConstants.BROADCAST_MAX_CHANNELS, 5);
						break turn;
					}
					
					// Compute relative power
					int sum = 0;
					for(Robot r: rc.senseNearbyGameObjects(Robot.class, 13)) {
						RobotInfo ri = rc.senseRobotInfo(r);
						if(ri.type!=RobotType.SOLDIER)
							continue;
						if(r.getTeam()==rc.getTeam()) sum++;
						else sum--;
					}
					rc.setIndicatorString(0, sum+"");
					int sumToAttack = 0;
					
					if(sum>=sumToAttack) {
						for(int i=0; i<8; i++) {
							MapLocation loc = rc.getLocation().add(Direction.values()[i]);
							GameObject go = rc.senseObjectAtLocation(loc);
							if(go!=null && go.getTeam()!=rc.getTeam())
								break turn;
						}
					}
					
					Direction dir = null;
					RobotInfo enemy = nearestEnemy(rc, 13);
					if(enemy==null) {
						dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						
					} else if(sum>=sumToAttack) {
						dir = rc.getLocation().directionTo(enemy.location);
						
					} else {
						dir = rc.getLocation().directionTo(enemy.location).opposite();
						
					}
					
					// Wiggle
					int[] wiggle = new int[] {0, -1, 1, -2, 2};
					if(Math.random()<0.5) 
						for(int i=0; i<wiggle.length; i++) 
							wiggle[i]*=-1;
					for(int d: wiggle) {
						Direction wdir = Direction.values()[(dir.ordinal()+d+8)%8];
						if(rc.canMove(wdir)) {
							dir = wdir;
							break;
						}
					}
					
					// If blocked, end turn
					if(!rc.canMove(dir))
						break turn;
					
					// If there is an enemy mine in the way, defuse it
					Team mineTeam = rc.senseMine(rc.getLocation().add(dir));
					if(mineTeam!=null && mineTeam!=rc.getTeam()) {
						rc.defuseMine(rc.getLocation().add(dir));
						break turn;
					}
					
					// Move in computed direction
					rc.move(dir);
					break turn;
					
				}

				
			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
			rc.yield();
		}
	}
	
	public static RobotInfo nearestEnemy(RobotController rc, int distThreshold) throws GameActionException {
		Robot[] ar = rc.senseNearbyGameObjects(Robot.class, distThreshold);

		if(ar==null || ar.length==0)
			return null;
		RobotInfo nearest = null;
		for(Robot r: ar) {
			if(r.getTeam()==rc.getRobot().getTeam())
				continue;
			RobotInfo ri = rc.senseRobotInfo(r);
			MapLocation loc = ri.location;
			if(nearest == null || rc.getLocation().distanceSquaredTo(nearest.location) > rc.getLocation().distanceSquaredTo(loc))
				nearest = ri;
		}
		return nearest;
	}
}
