package basicplayer;

import java.util.HashSet;
import java.util.Set;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.Upgrade;

/** Basic player. A general macro-based player which does almost everything, but does everything poorly.
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			turn: try {
				rc.setIndicatorString(2, "Delay: "+rc.roundsUntilActive());
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						if(rc.getTeamPower()>5) {
							Direction dir = Direction.values()[(int)(Math.random()*8)];
							if (rc.canMove(dir))
								rc.spawn(dir);
						} else {
							rc.researchUpgrade(Upgrade.NUKE);
						}
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (!rc.isActive()) {
						break turn;
					}
					
					MapLocation[] allEncampments = rc.senseAllEncampments();
					MapLocation[] alliedEncampments = rc.senseAlliedEncampments();
					Set<MapLocation> allLocs = new HashSet<MapLocation>();
					Set<MapLocation> alliedLocs = new HashSet<MapLocation>();
					for(MapLocation ml: allEncampments) allLocs.add(ml);
					for(MapLocation ml: alliedEncampments) alliedLocs.add(ml);
					if(rc.getTeamPower()>10) for(Robot r: rc.senseNearbyGameObjects(Robot.class, 1000000, rc.getTeam())) {
						int x = rc.readBroadcast(r.getID());
						if(x!=0) {
							x--;
							alliedLocs.add(new MapLocation(x>>16, x%(1<<16)));
						}
					}
					MapLocation target = rc.senseEnemyHQLocation();
					for(MapLocation enc: allLocs) {
						if(alliedLocs.contains(enc)) continue;
						if(target==null || enc.distanceSquaredTo(rc.getLocation()) <
								target.distanceSquaredTo(rc.getLocation()))
							target = enc;
					}
					int tint = (target.x<<16)+target.y+1;
					rc.setIndicatorString(1, "target: "+(target.x-rc.getLocation().x)+","+(target.y-rc.getLocation().y));
					if(rc.getTeamPower()>2) rc.broadcast(rc.getRobot().getID(), tint);
					
					// If on encampment, capture it or wait for more energy to capture it
					if(target.equals(rc.getLocation())) {
						try {
							if(rc.getLocation().distanceSquaredTo(rc.senseHQLocation())>=rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation())) {
								double d = Math.random();
								rc.captureEncampment(d<0.5?RobotType.ARTILLERY:d<0.75?RobotType.SHIELDS:RobotType.MEDBAY);
							} else {
								int x = rc.readBroadcast(5555+rc.getTeam().ordinal());
								rc.captureEncampment(x%3==0?RobotType.GENERATOR:RobotType.SUPPLIER);
								rc.broadcast(5555+rc.getTeam().ordinal(), x+1);
							}
						} catch(GameActionException e) {}
						break turn;
					}
					

					if(Math.random()<0.01 && rc.senseMine(rc.getLocation())==null) {
						rc.layMine();
						break turn;
					}
					
					Direction dir = null;
					RobotInfo enemy = nearestEnemy(rc, 8);
					if(enemy==null) {
						dir = rc.getLocation().directionTo(target);
						
					} else {
						dir = rc.getLocation().directionTo(enemy.location);
						
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
					
				} else if(rc.getType() == RobotType.ARTILLERY) {
					if(!rc.isActive())
						break turn;

					RobotInfo enemy = nearestEnemy(rc, rc.getType().attackRadiusMaxSquared);
					if(enemy!=null && rc.canAttackSquare(enemy.location))
						rc.attackSquare(enemy.location);
					
				}

				
			} catch (Exception e) {
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
