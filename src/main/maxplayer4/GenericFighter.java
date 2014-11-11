package maxplayer4;

import battlecode.common.*;

public class GenericFighter{
	
	static void run(RobotController rc) throws GameActionException{
		//TODO better micro
		//- still follow the path to the goal
		//- while maintaining unit advantage
		//- while pursuing easy off-path targets (no wall in the way)
		//- and preserving injured units
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
			int orderStatus = rc.readBroadcast(99);
			if(orderStatus<2){//if the map data is not ready, go to fixed rally point
				MapLocation rallypt = Message.readLocation(rc, 4);
				rc.setIndicatorString(2, "rally at "+rallypt);
				Direction toRally = rc.getLocation().directionTo(rallypt);
				Pathfinding.goTo(rc, toRally);
			}else{//if the map data is ready, follow it to the goal loc
				Direction toGoal = Message.readPostedDirection(rc).opposite();
				Pathfinding.goTo(rc, toGoal);
			}
			
			//end turn
			rc.yield();
		}
		
	}
	
}