package easybot;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.Team;

/** This bot does a poor version of a standard macro strategy. 
 * Robots repeatedly move towards the nearest untaken encampment and build a generator on it.
 * They will defuse mines in their way if no enemies are adjacent to them.
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			turn: try {
				if (rc.getType() == RobotType.HQ) {
					if (!rc.isMovementActive()) {
						Direction dir = Direction.values()[(int)(Math.random()*8)];
						if (rc.canMove(dir))
							rc.spawn(dir);
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					// If we're on move delay for whatever reason, send useless message and end turn
					if (rc.isMovementActive()) {
						rc.broadcast(rc.getRobot().getID()%GameConstants.MAX_RADIO_CHANNEL, 5);
						break turn;
					}
					
					// If adjacent to enemy (soldier or building), do nothing and wait for autoattack
					for(int i=0; i<8; i++) {
						MapLocation loc = rc.getLocation().add(Direction.values()[i]);
						GameObject go = rc.senseObjectAtLocation(loc, RobotLevel.ON_GROUND);
						if(go!=null && go.getTeam()!=rc.getTeam())
							break turn;
					}
					
					// Compute nearest encampment (counting the enemy HQ)
					MapLocation nearestEncampment = rc.senseEnemyHQLocation();
					MapLocation[] alliedEncampments = rc.senseAlliedEncampments();
					outer: for(MapLocation enc: rc.senseAllEncampments()) {
						for(MapLocation aenc: alliedEncampments) 
							if(aenc.equals(enc))
								continue outer;
						if(nearestEncampment==null || enc.distanceSquaredTo(rc.getLocation()) <
								nearestEncampment.distanceSquaredTo(rc.getLocation()))
							nearestEncampment = enc;
					}
					
					// If on encampment, capture it
					if(nearestEncampment.equals(rc.getLocation())) {
						rc.captureEncampment(RobotType.GENERATOR);
						break turn;
					}
					
					// Compute direction to encampment (greedy + wiggle + randomness)
					MapLocation target = nearestEncampment;
					Direction dir = rc.getLocation().directionTo(target);
					if(Math.random()<0.5) 
						dir = Direction.values()[(int)(Math.random()*8)];
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
					
				} else {
					// encampments never do anything
				}

				
			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
			rc.yield();
		}
	}
}
