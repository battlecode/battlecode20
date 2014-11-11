package maxplayer3;

import battlecode.common.*;

public class GenericBuilding{
	
	static void run(RobotController rc) throws GameActionException{
		while(true){
			//read tech order
			rc.setIndicatorString(0, "do nothing");
			int techOrder = rc.readBroadcast(0);
			if(techOrder!=-1){//if the tech is ordered, try to produce it
				RobotType tech = Technology.types.get(techOrder);
				if(rc.getType()==tech.spawnSource){
					rc.setIndicatorString(0, "spawn minions");
					//spawn minions
					Direction randomDir = Pathfinding.randomDir();
					Verified.spawn(rc, randomDir,tech,(int)(RobotType.FURBY.oreCost*1.5));
				}
			}
			rc.yield();
		}
	}
	
}