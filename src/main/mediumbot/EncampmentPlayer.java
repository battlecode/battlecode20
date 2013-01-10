package mediumbot;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
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

		MapLocation nearest = Util.nearestEnemy(rc, 63).location;
		if(nearest!=null && rc.canAttackSquare(nearest))
			rc.attackSquare(nearest);
	}
}

