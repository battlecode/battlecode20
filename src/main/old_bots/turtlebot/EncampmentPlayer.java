package turtlebot;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class EncampmentPlayer extends BasePlayer {
	public EncampmentPlayer(RobotController rc) {
		super(rc);
	}
	public void run() throws GameActionException {
		if(myType!=RobotType.ARTILLERY) {
			if(myType==RobotType.MEDBAY)
				msg.write(11, curLoc);
			return;
		}
		
		if(!rc.isActive())
			return;

		Robot[] ar = rc.senseNearbyGameObjects(Robot.class, 63, enemyTeam);
		if(ar.length==0) 
			return;
		
		MapLocation bestTarget = null;
		double bestHeur = 0;
		
		for(Robot r: ar) {
			if(Clock.getBytecodesLeft()<4000) break;
			RobotInfo ri = rc.senseRobotInfo(r);
			double heur = 0;
			for(int dx=-1; dx<=1; dx++) for(int dy=-1; dy<=1; dy++) {
				MapLocation loc = ri.location.add(dx, dy);
				if(!rc.canSenseSquare(loc)) continue;
				GameObject go = rc.senseObjectAtLocation(loc);
				if(go==null) continue;
				RobotInfo ri2 = rc.senseRobotInfo((Robot)go);
				if(ri2.team==myTeam) heur-=ri2.energon;
				else heur+=ri2.energon;
			}
			if(heur > bestHeur) {
				bestTarget = ri.location;
				bestHeur = heur;
			}
		}
		if(bestTarget!=null)
			rc.attackSquare(bestTarget);
		
	}
}

