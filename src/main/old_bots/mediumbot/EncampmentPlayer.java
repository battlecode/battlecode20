package mediumbot;

import battlecode.common.GameActionException;
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
		if(myType!=RobotType.ARTILLERY)
			return;
		
		if(!rc.isActive())
			return;

		Robot[] ar = rc.senseNearbyGameObjects(Robot.class, 63, enemyTeam);
		if(ar.length==0) 
			return;
		
		RobotInfo ri = rc.senseRobotInfo(ar[(int)(Util.randDouble()*ar.length)]);
		MapLocation loc = ri.location;
		if(rc.canAttackSquare(loc))
			rc.attackSquare(loc);
		
	}
}

