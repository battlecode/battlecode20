package exampleForAaron;

import java.util.Random;

import battlecode.common.*;

public class RobotPlayer {
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	static int[] dirOffset = new int[]{0,-1,1,-2,2};
	static Random rand;
	static Direction persistentDir;
	
	public static void run(RobotController rc) {
		
		rand = new Random(rc.getRobot().getID());
		persistentDir = getRandomDir();
		while(true) {
			try{
				if (rc.getType() == RobotType.HQ) {
					Direction spawnDir = getRandomDir();
					if(rc.isActive()&&rc.canMove(spawnDir)&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
						rc.spawn(spawnDir);
					}
				}else if (rc.getType() == RobotType.SOLDIER){
					Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class,rc.getType().sensorRadiusSquared,rc.getTeam().opponent());
					if(enemyRobots.length>0){//enemies sighted, so go attack them
						RobotInfo firstRobotInfo = rc.senseRobotInfo(enemyRobots[0]);
						if(firstRobotInfo.location.distanceSquaredTo(rc.getLocation())>rc.getType().attackRadiusMaxSquared){
							Direction towardEnemy = rc.getLocation().directionTo(firstRobotInfo.location);
							tryToMove(towardEnemy,rc);
						}else{//enemy in range, fire!
							if(rc.isActive()){
								rc.attackSquare(firstRobotInfo.location);
							}
						}
					}else{//walk around randomly and maybe build a tower
						if(rc.isActive()){
							double arand = rand.nextDouble();
							if(arand<0.01){//small chance to build something
								int whichBuilding = (int)(rand.nextDouble()*3);
								switch(whichBuilding){
								case 0:
									rc.construct(RobotType.PASTR);
									break;
								case 1:
									rc.construct(RobotType.NOISETOWER);
									break;
								case 2:
									rc.construct(RobotType.WALL);
									break;
								}
							}else{//otherwise just walks around
								double thing = rand.nextDouble();
								if(thing<0.2){
									persistentDir=persistentDir.rotateLeft();
								}else if(thing>0.8){
									persistentDir=persistentDir.rotateRight();
								}
								tryToMove(persistentDir,rc);
							}
						}
					}
				}else if (rc.getType() == RobotType.NOISETOWER){
					if(rc.isActive()){
						MapLocation[] attackable = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), 25);//goes out to 400
						int randomChoice = (int)(rand.nextDouble()*attackable.length);
						rc.attackSquareLight(attackable[randomChoice]);
					}
				}
			}catch(GameActionException e){
				e.printStackTrace();
			}
			rc.yield();
		}
	}
	public static void tryToMove(Direction d,RobotController rc) throws GameActionException{
		if(rc.isActive()){
			int startingOrdinal = d.ordinal()+8;
			for(int offset:dirOffset){
				Direction attempt = directions[(startingOrdinal+offset)%8];
				if(rc.canMove(attempt)){
					rc.move(attempt);
					break;
				}
			}
		}
	}
	public static Direction getRandomDir(){
		return directions[(int)(rand.nextDouble()*8)];
	}
}