package maxplayer3;

import battlecode.common.*;

public class GenericFighter{
	
	static void run(RobotController rc) throws GameActionException{
		
		while(true){
			//1 shoot at enemies
			Fight.tryToShoot(rc);
			//2 go toward calls for help
			MapLocation enemyLoc = Message.readEnemy(rc);
			if(enemyLoc!=null){
				Direction toEnemy = rc.getLocation().directionTo(enemyLoc);
				Pathfinding.goTo(rc, toEnemy);
			}
			//3 go to rally point
			MapLocation rallypt = Message.readLocation(rc, 4);
			rc.setIndicatorString(2, "rally at "+rallypt);
			Direction toRally = rc.getLocation().directionTo(rallypt);
			Pathfinding.goTo(rc, toRally);
			//end turn
			rc.yield();
		}
		
	}
	
}