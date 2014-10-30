package maxplayer2;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	static RobotController rc;
	static MapLocation enemyHQLocation;
	static RobotType myType;
	static Random rand;

	public static void run(RobotController myRC) throws GameActionException {//NEED TO COLLECT ORE-- first assess the cost of a technology
		rc=myRC;
		rand = new Random(rc.getRobot().getID());
		Technology.init();
		enemyHQLocation = rc.senseEnemyHQLocation();
		myType = rc.getType();
		if (myType==RobotType.HQ)
			HQ.run(rc);
		else if(myType==RobotType.FURBY)
			Furby.run(rc);
		else
			while(true)
				rc.yield();
	}

}