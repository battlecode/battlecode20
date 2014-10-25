package maxplayer1;

import battlecode.common.*;

import java.util.*;

public class RobotPlayer {
	static Random rand;
	static RobotController rc;
	static MapLocation enemyHQLocation;
	static int techBroadcastChannel = 0;
	static RobotType myType;
	static ArrayList<RobotType> types = new ArrayList<RobotType>();
	static int sensorRadius;
	static int arraySize;
	static int[][] surroundings=null;
	static MapLocation myLoc;
	
	public static void run(RobotController myRC) {
		rc=myRC;
		for(RobotType r:RobotType.values())
			types.add(r);
		enemyHQLocation = rc.senseEnemyHQLocation();
		myType = rc.getType();
		while(true) {
            try {
            	if (myType==RobotType.FURBY){
            		int c = Clock.getBytecodeNum();
            		achieveTech(RobotType.LAUNCHER);
            		int c2 = (Clock.getBytecodeNum()-c);
            		//rc.setIndicatorString(2,"achieveTech took "+c2);
            		if(Clock.getRoundNum()==100){
            			printSurroundings();
            		}
            		//rc.setIndicatorString(2, "building north = "+surroundingsIsBuilding(0, 1));
            	}else if (myType==RobotType.HQ){
            		Direction randomDir = Direction.EAST;//Direction.values()[(int)(Math.random()*8)];
            		if(rc.isActive()&&rc.senseObjectAtLocation(rc.getLocation().add(randomDir))==null){
            			rc.spawn(randomDir,RobotType.FURBY);
            		}
//            		rc.setIndicatorString(0, "try to build at "+randomDir);
            		rc.setIndicatorString(0, "index "+types.indexOf(RobotType.AEROSPACELAB));
            	}else{
            		rc.setIndicatorString(0,"constructing "+rc.getConstructingType()+" r="+rc.getConstructingRounds());
            	}
            	surroundings = null;//make the surroundings array out of date
        		rc.yield();
            }catch(Exception e){
            	e.printStackTrace();
            	//something
            }
		}
	}
	
	private static void printSurroundings() {
		for(int i=0;i<arraySize;i++){
			System.out.println("");
			for(int j=0;j<arraySize;j++){
				System.out.print("   "+surroundings[j][i]);
				//int i2 = surroundingsIsBuilding(j-sensorRadius,i-sensorRadius)?1:0;
				//System.out.print("   "+i2);
			}
		}
	}

	static void achieveTech(RobotType robot) throws GameActionException{
		if(robot==null)
			return;
		RobotType desiredBuilding;
		if(robot.isBuilding){
			desiredBuilding=robot;
		}else{
			desiredBuilding=robot.spawnSource;
		}
		if(desiredBuilding==RobotType.HQ)
			return;
		rc.setIndicatorString(0, "goal: "+desiredBuilding);
		
		if(rc.readBroadcast(techBroadcastChannel)==types.indexOf(desiredBuilding)){//check if it is built already
			return;
		}else{//need to build it
			//check if you can build it
			MapLocation site = findAvailableSite();
			if(site!=null){
				rc.setIndicatorString(1, "going to site "+site);
				if(rc.canBuild(rc.getLocation().directionTo(site), desiredBuilding)){
					rc.build(rc.getLocation().directionTo(site), desiredBuilding);
				}else{//need another tech:
					achieveTech(desiredBuilding.dependency1);
					achieveTech(desiredBuilding.dependency2);
				}
			}else{//go look for another site
				Direction rd = randomDir();
				if(rc.isActive()&&rc.canMove(rd)){
					rc.move(rd);
				}
			}
			
		}
		
	}

	private static Direction randomDir(){
		return Direction.values()[(int)(Math.random()*8)];
	}
	private static MapLocation findAvailableSite() throws GameActionException {
		myLoc = rc.getLocation();
		if(surroundings==null){
			Robot[] nearbyRobots = rc.senseNearbyGameObjects(Robot.class,myType.sensorRadiusSquared);
			surroundings = robotsTo2DArray(nearbyRobots,myLoc);
		}
		for(Direction d:Direction.values()){//could extend the range here
			//if(rc.senseTerrainTile(myLoc.add(d)).equals(TerrainTile.NORMAL)){
			if (!adjacentBuilding(d.dx,d.dy)){
				return myLoc.add(d);
			}
			//}
		}
		return null;
	}

	private static int[][] robotsTo2DArray(Robot[] nearbyRobots,MapLocation myLoc) throws GameActionException {
		sensorRadius = (int)(Math.pow(myType.sensorRadiusSquared, 0.5))+1;
		arraySize = 1+2*sensorRadius;
		surroundings = new int[arraySize][arraySize];
		for(Robot r: nearbyRobots){
			RobotInfo i = rc.senseRobotInfo(r);
			MapLocation sensedLoc = i.location;
			surroundings[sensedLoc.x-myLoc.x+sensorRadius][sensedLoc.y-myLoc.y+sensorRadius] = types.indexOf(i.type)+100;
		}
		return surroundings;
	}

	private static boolean surroundingsIsBuilding(int dx, int dy){
		if(!rc.senseTerrainTile(myLoc.add(dx,dy)).equals(TerrainTile.NORMAL))//if it's not traversible terrain, consider it a building
			return true;
		int val = surroundings[dx+sensorRadius][dy+sensorRadius];
		if(val==0){
			return false;
		}else if(val>=100){
			return types.get(val-100).isBuilding;
		}else{
			return false;
		}
		
	}
	
	private static boolean adjacentBuilding(int dx,int dy) throws GameActionException {
		return surroundingsIsBuilding(0+dx,0+dy)
				||surroundingsIsBuilding(1+dx,0+dy)
				||surroundingsIsBuilding(0+dx,1+dy)
				||surroundingsIsBuilding(-1+dx,0+dy)
				||surroundingsIsBuilding(0+dx,-1+dy);
	}
	
}