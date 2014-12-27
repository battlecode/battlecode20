package maxtestplayer2;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.*;

public class Utility{
	
	static ArrayList<MapLocation> pastMoves = new ArrayList<MapLocation>();
	static int[] tryDir = new int[]{0,1,-1,2,-2,3,-3};
	static Random rand;
	
	public static void spawn(RobotController rc,Direction d,RobotType rtype,int reserveOre) throws GameActionException{
		MapLocation goal = rc.getLocation().add(d);
		if(rc.isMovementActive()&&rc.canSpawn(d, rtype)&&(rc.getTeamOre()-reserveOre>rtype.oreCost)&&rc.senseTerrainTile(goal)==TerrainTile.NORMAL){
			rc.spawn(d,rtype);
		}
	}
	
	public static void build(RobotController rc,Direction d,RobotType rtype) throws GameActionException{
		if(!rc.isMovementActive()&&rc.canBuild(d, rtype)&&rc.getTeamOre()>rtype.oreCost){
			rc.build(d, rtype);
		}
	}
	
	public static MapLocation randomLoc(RobotController rc,double dist){
		return rc.getLocation().add((int)((rand.nextDouble()-.5)*dist*2),(int)((rand.nextDouble()-.5)*dist*2));
	}
	
	public static boolean mine(RobotController rc,double thresh) throws GameActionException{
		if(rc.isMovementActive()&&rc.senseOre(rc.getLocation())>thresh){
			rc.mine();
			return true;
		}else{//look for a better place to mine
			//first look nearby
			if(searchToMine(rc,2,thresh))return true;
			//if that doesn't work, look farther
			goOffToMine(rc,randomLoc(rc,20),thresh);
		}
		return false;
	}
	
	private static void goOffToMine(RobotController rc, MapLocation loc,double thresh) throws GameActionException {
		while(!searchToMine(rc,rc.getType().sensorRadiusSquared,thresh)){
			rc.setIndicatorString(1,"going to "+rc.getLocation().directionTo(loc)+" dist "+rc.getLocation().distanceSquaredTo(loc));
			if(rc.senseTerrainTile(rc.getLocation().add(rc.getLocation().directionTo(loc)))==TerrainTile.OFF_MAP)break;//exit search if map edge found
			if(rand.nextDouble()<0.01)break;//chance to exit search
			goTo(rc,loc);
			rc.yield();
		}
		rc.setIndicatorString(1,"");
	}

	public static boolean searchToMine(RobotController rc, int radiusSquared, double thresh) throws GameActionException{
		MapLocation[] adj = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), radiusSquared);
		for(MapLocation a:adj){
			if(rc.senseTerrainTile(a)==TerrainTile.NORMAL&&rc.senseRobotAtLocation(a)==null&&rc.senseOre(a)>thresh){
				goTo(rc, a);
				return true;
			}
		}
		return false;
	}

	public static boolean move(RobotController rc, Direction d) throws GameActionException {
		if(rc.isMovementActive()&&rc.canMove(d)){
			if(pastMoves.size()>0)
				pastMoves.remove(0);
			pastMoves.add(rc.getLocation());
			rc.move(d);
			return true;
		}
		return false;
	}

	public static void attack(RobotController rc, MapLocation targetLoc) throws GameActionException {
		if(rc.isAttackActive()&&rc.canAttackLocation(targetLoc)){
			rc.attackLocation(targetLoc);
		}
	}
	
	public static void goTo(RobotController rc, MapLocation loc) throws GameActionException{
		tryToShoot(rc);//TODO avoid enemy towers and HQ?
		Direction toTarget = rc.getLocation().directionTo(loc);
		for(int dirOffset:tryDir){
			Direction d = Direction.values()[(toTarget.ordinal()+16+dirOffset)%8];
			MapLocation aheadLoc = rc.getLocation().add(d);
			if(rc.canMove(d)){
				if(!pastMoves.contains(aheadLoc)){
					move(rc,d);
					break;
				}
			}
		}
	}

	public static void tryToShoot(RobotController rc) throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(rc.getLocation(), rc.getType().sensorRadiusSquared, rc.getTeam().opponent());
		int closestDist = rc.getType().attackRadiusSquared;
		MapLocation targetLoc = null;
		for(RobotInfo i:enemies){
			int robotDist = rc.getLocation().distanceSquaredTo(i.location);
			if(robotDist<=closestDist){
				closestDist = robotDist;
				targetLoc = i.location;
			}
		}
		if(targetLoc!=null){
			attack(rc,targetLoc);
		}
	}

	public static void initRand(int id) {
		rand = new Random(id);
	}

	public static void buildInRatio(RobotController rc, RobotType building1, RobotType building2, double reserveOre, double idealRatio1to2) throws GameActionException{
		int nb1 = Comms.getAlliedRobotCount(rc, building1);
		int nb2 = Comms.getAlliedRobotCount(rc, building2);
		if(nb2==0){
			tryToBuild(rc,building2,reserveOre,1);
		}else{
			double currentRatio1to2 = ((double)nb1)/((double)nb2);
			if(currentRatio1to2<idealRatio1to2){
				tryToBuild(rc,building1,reserveOre,1000000);
			}else{
				tryToBuild(rc,building2,reserveOre,1000000);
			}
		}
		
	}
	
	public static void tryToBuild(RobotController rc, RobotType goalStructure, double reserveOre,int buildingRepeats) throws GameActionException{
		if(!rc.isMovementActive()){
			if(rc.checkDependencyProgress(goalStructure)==DependencyProgress.NONE||Comms.getAlliedRobotCount(rc, goalStructure)<buildingRepeats){
				if(requisitesComplete(rc,goalStructure,reserveOre)){
					if(rc.getTeamOre()>(reserveOre+goalStructure.oreCost)){
						for(Direction d:Direction.values()){
							if(rc.canBuild(d,goalStructure)){
								build(rc, d, goalStructure);
								return;
							}
						}
					}
				}
			}
		}
	}
	
	private static boolean requisitesComplete(RobotController rc, RobotType goalStructure,double reserveOre) throws GameActionException{
        if(rc.checkDependencyProgress(goalStructure.dependency)!=DependencyProgress.DONE){
            tryToBuild(rc,goalStructure.dependency,reserveOre,1);
            return false;
        }
		return true;
	}
	
	public static void roamAndFight(RobotController rc) throws GameActionException{
		MapLocation goal = randomLoc(rc,100);
		while(rc.getLocation().distanceSquaredTo(goal)>9){
			rc.setIndicatorString(1,"going to "+rc.getLocation().directionTo(goal)+" dist "+rc.getLocation().distanceSquaredTo(goal));
			if(rc.senseTerrainTile(rc.getLocation().add(rc.getLocation().directionTo(goal)))==TerrainTile.OFF_MAP)break;//exit search if map edge found
			if(rand.nextDouble()<0.01)break;//chance to exit search
			goTo(rc,goal);
			rc.yield();
		}
	}
	
}
