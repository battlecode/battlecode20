package maxplayer4;

import battlecode.common.*;

public class HQ{
	
	static int oreGoal;
	static RobotType techGoal = RobotType.TANK;
	static double furbyCombatFraction = 0.5;
	static int professionalArmySize = 10;//at this size, furbies no longer fight.
	static int attackRound = 700;
	
	static MapLocation[] corners;
	static int[][] internalMap;
	
	public static void run(RobotController rc) throws GameActionException{
		oreGoal = Technology.techCost(techGoal,rc);
		Message.initCombatList(rc);
		Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		MapLocation rallypt= rc.getLocation().add(toEnemy.dx*8,toEnemy.dy*8);
		while(true) {
			try {
				//give instructions to furbies and buildings
				if(rc.getRobotTypeCount(techGoal.spawnSource)==0&&rc.getTeamOre()<oreGoal){//mine ore
					rc.broadcast(0, -1);
					rc.setIndicatorString(0, "minions, mine");
				}else{
					rc.broadcast(0, Technology.types.indexOf(techGoal));///choose a tech
					rc.setIndicatorString(0, "minions, build tech");
				}
				
				//tell furbies whether to fight
				if(rc.getRobotTypeCount(techGoal)>professionalArmySize){//furbies need not fight
					Message.furbyCombatFraction(rc,0);
				}else{//we have no soldiers, so instruct some furbies to fight
					Message.furbyCombatFraction(rc,furbyCombatFraction);
				}
				
				//spawn furbies
				spawnFurbies(rc);
				
				//set fighter destination
				if(Clock.getRoundNum()<attackRound){
					Message.writeLoc(rc, rallypt, 4);
				}else{
					//Message.writeLoc(rc, rc.senseEnemyHQLocation(), 4);
					rc.broadcast(99, 2);
				}
				
				if(Clock.getRoundNum()==5){//compute map data on round 5
					rc.setIndicatorString(1, "finding corners");
					corners = Pathfinding.findMapCorners(rc);
					rc.setIndicatorString(1, "building internal representation");
					internalMap = Pathfinding.buildInternalRepresentation(rc, corners);
					rc.setIndicatorString(1, "pathing to point");
					internalMap = Pathfinding.pathToPoint(rc, corners, internalMap, rc.senseEnemyHQLocation());
					rc.setIndicatorString(1, "broadcasting path data");
					Message.broadcastInternalRepresentation(rc, corners, internalMap);
					rc.setIndicatorString(1, "done computing map data");
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			Fight.tryToShoot(rc);
			rc.yield();
		}
	}
	public static void spawnFurbies(RobotController rc) throws GameActionException{
		//spawn furbies
		Direction randomDir = Pathfinding.randomDir();
		Verified.spawn(rc, randomDir, RobotType.FURBY,0);
	}
}