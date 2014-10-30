package maxplayer2;

import battlecode.common.*;

public class HQ{
	
	static int oreGoal;
	static RobotType techGoal = RobotType.LAUNCHER;
	
	public static void run(RobotController rc) throws GameActionException{
		oreGoal = Technology.techCost(techGoal,rc);
		while(true) {
			try {
				//give instructions to minions
				if(rc.getTeamOre()<oreGoal){
					rc.broadcast(0, -1);
				}else{
					rc.broadcast(0, Technology.types.indexOf(techGoal));///choose a tech
				}
				//spawn minions
				Direction randomDir = Pathfinding.randomDir();
				Verified.spawn(rc, randomDir, RobotType.FURBY);
			}catch(Exception e){
				e.printStackTrace();
			}
			rc.yield();
		}
		
	}
	
}