package maxplayer4;

import battlecode.common.*;

public class Fight{
	
	public static void tryToShoot(RobotController rc) throws GameActionException{
		Robot[] enemies = rc.senseNearbyGameObjects(Robot.class,rc.getLocation(),rc.getType().sensorRadiusSquared,rc.getTeam().opponent());
		int closestDist = rc.getType().attackRadiusSquared;
		MapLocation targetLoc = null;
		for(Robot r:enemies){
			if(rc.canSenseObject(r)){
				RobotInfo i = rc.senseRobotInfo(r);
				int robotDist = rc.getLocation().distanceSquaredTo(i.location);
				if(robotDist<=closestDist){
					closestDist = robotDist;
					targetLoc = i.location;
				}
			}
		}
		if(targetLoc!=null){
			Verified.attack(rc,targetLoc);
			Message.postEnemyLoc(rc, targetLoc, enemies.length, 0);
		}
	}
	
	//Direction[] checkDirs = Pathfinding.validDirs(rc, true);//valid directions for places to stand for shooting
	//prefers to shoot from a standingLane(MapLocation m) true
}