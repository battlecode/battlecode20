package team344;

import java.util.Random;

import battlecode.common.*;

public class RobotPlayer{

	static Direction facing;
	static Random rand;
	static RobotController rc;
	public static void run(RobotController myrc){
		rc=myrc;
		rand = new Random(rc.getID());
		facing=getRandomDirection();//randomize starting direction
		while(true){
			try {	
				if(rc.getType()==RobotType.HQ){
					attackEnemyZero();
					spawnUnit(RobotType.BEAVER);
				} else if(rc.getType()==RobotType.BEAVER){
					attackEnemyZero();
					if (Clock.getRoundNum()<700){
						buildUnit(RobotType.MINERFACTORY);	
					}else{
						buildUnit(RobotType.BARRACKS);
					}
					mineAndMove();	
				} else if(rc.getType()==RobotType.MINER){
					attackEnemyZero();
					mineAndMove();
				} else if(rc.getType()==RobotType.MINERFACTORY){
					//if less miners than width of map
					//or if round number not too big (1500?)
					//spawn miners

					spawnUnit(RobotType.MINER);
				} else if(rc.getType()==RobotType.BARRACKS){
					spawnUnit(RobotType.SOLDIER);
				} else if(rc.getType()==RobotType.TOWER){
					attackEnemyZero();
				} else if(rc.getType()==RobotType.SOLDIER){
					if (Clock.getRoundNum()<1500){
						attackEnemyZero();
						moveAround();
					}else{
						MapLocation[] enemyTowers=rc.senseEnemyTowerLocations();
						MapLocation myHQ = rc.senseHQLocation();
						MapLocation closestTarget=rc.senseEnemyHQLocation();

						int minDist=0;
						for (MapLocation tower:enemyTowers){
							int distToTower =myHQ.distanceSquaredTo(tower);
							if (minDist >distToTower){
								minDist = distToTower;
								closestTarget=tower;
							}
						}

						if (rc.canAttackLocation(closestTarget)){
							rc.attackLocation(closestTarget);
						}else{
							Direction attackDir = rc.getLocation().directionTo(closestTarget);
							if (rc.isCoreReady()){
								if (rc.canMove(attackDir)){
									rc.move(attackDir);
								}else{
									if(rc.canMove(attackDir.rotateRight().rotateRight())){
										rc.move(attackDir.rotateRight().rotateRight());
									}else if(rc.canMove(attackDir.rotateLeft().rotateLeft())){
										rc.move(attackDir.rotateLeft().rotateLeft());
									}
								}
							}
						}


					}
				}
				transferSupplies();
			}
			catch (GameActionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			rc.yield();
		}
	}




	private static void transferSupplies() throws GameActionException {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(),GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,rc.getTeam());
		double lowestSupply = rc.getSupplyLevel();
		double transferAmount = 0;
		MapLocation suppliesToThisLocation = null;
		for (RobotInfo ri:nearbyAllies){
			if (ri.supplyLevel < lowestSupply){
				lowestSupply = ri.supplyLevel;
				transferAmount = (rc.getSupplyLevel()-ri.supplyLevel)/2.0;
				suppliesToThisLocation = ri.location;
			}
		}
		if (suppliesToThisLocation != null){
			rc.transferSupplies((int)transferAmount,suppliesToThisLocation);
		}
	}




	private static void buildUnit(RobotType type) throws GameActionException {
		if(rc.getTeamOre()>type.oreCost){
			Direction buildDir = getRandomDirection();
			if(rc.isCoreReady()&&rc.canBuild(buildDir,type)){
				rc.build(buildDir,type);
			}
		}

	}




	private static void attackEnemyZero() throws GameActionException {
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getType().attackRadiusSquared,rc.getTeam().opponent());
		if(nearbyEnemies.length>0){//if enemies in range
			//try to shoot at nearbyEnemies[0]
			if(rc.isWeaponReady()&&rc.canAttackLocation(nearbyEnemies[0].location)){
				rc.attackLocation(nearbyEnemies[0].location);
			}
		}
	}




	private static void spawnUnit(RobotType type) throws GameActionException {
		if (rc.getTeamOre()>type.oreCost){
			Direction randomDir = getRandomDirection();
			if(rc.isCoreReady() && rc.canSpawn(randomDir,type)){
				rc.spawn(randomDir,type);
			} 
		}
	}




	private static Direction getRandomDirection() {
		return Direction.values()[(int)(rand.nextDouble()*8)];
	}




	private static void mineAndMove() throws GameActionException {
		if(rc.senseOre(rc.getLocation())>1){
			if(rc.isCoreReady()&&rc.canMine()){
				rc.mine();
			}
		}else{//no ore nearby
			moveAround();
		}
	}




	private static void moveAround() throws GameActionException {
		if(rand.nextDouble()<0.05){
			if(rand.nextDouble()<.5){
				facing=facing.rotateLeft();
			}else{
				facing=facing.rotateRight();
			}

		}
		MapLocation tileInFront = rc.getLocation().add(facing);

		//check that location in front is not a location that can be attacked by enemy towers
		MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
		boolean tileInFrontSafe = true;
		for(MapLocation m:enemyTowers){
			if( m.distanceSquaredTo(tileInFront)<=RobotType.TOWER.attackRadiusSquared){
				tileInFrontSafe =false;
				break;
			}
		}
		//check we're not facing off the edge of the map
		if(rc.senseTerrainTile(tileInFront)!=TerrainTile.NORMAL||!tileInFrontSafe){
			facing = facing.rotateLeft();
		}else if(rc.isCoreReady()&&rc.canMove(facing)){ //try to move in th	e facing direction
			rc.move(facing);
		}




	}
}

