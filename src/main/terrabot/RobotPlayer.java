package terrabot;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	static RobotController rc;
	static Team myTeam;
	static Team enemyTeam;
	static int myRange;
	static Random rand;
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	//0-towers, 1-supply depot, 2-barracks, 3-tech institute, 4-metabuilder, 5-helipad, 6-training field, 7-tank fact, 8-miner fact, 9-eng bay, 10-handwash station, 11-biomech lab, 12-aero lab
	static RobotType[] structureTypes = {RobotType.TOWER,RobotType.SUPPLYDEPOT,RobotType.BARRACKS,RobotType.TECHNOLOGYINSTITUTE,RobotType.METABUILDER,RobotType.HELIPAD,RobotType.TRAININGFIELD,
	RobotType.TANKFACTORY,RobotType.MINERFACTORY,RobotType.ENGINEERINGBAY,RobotType.HANDWASHSTATION,RobotType.BIOMECHATRONICRESEARCHLAB,RobotType.AEROSPACELAB};
	//0-furby, 1-computer, 2-soldier, 3-basher, 4-builder, 5-miner, 6-drone, 7-tank, 8-commander, 9-launcher
	static RobotType[] unitTypes = {RobotType.FURBY, RobotType.COMPUTER, RobotType.SOLDIER, RobotType.BASHER, RobotType.BUILDER, 
	RobotType.MINER, RobotType.DRONE, RobotType.TANK, RobotType.COMMANDER, RobotType.LAUNCHER};
	static int[] structureCount = new int[structureTypes.length];
	static int[] unitCount = new int[unitTypes.length];
	
	public static void run(RobotController tomatojuice) {
		rc = tomatojuice;
		myRange = rc.getType().attackRadiusSquared;
        rand = new Random(rc.getID());
		MapLocation enemyLoc = rc.senseEnemyHQLocation();
        Direction lastDirection = null;
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		RobotInfo[] myRobots;
		
		while(true) {
            try {
                rc.setIndicatorString(0, "Ore here: " + rc.senseOre(rc.getLocation()));
                rc.setIndicatorString(1, "Location: " + rc.getLocation());
                rc.setIndicatorString(2, "My supply level: " + rc.getSupplyLevel());
				
				//inefficient for now but w/e
				for (int i=0; i<structureCount.length; i++) {
					//structureCount[i] = rc.getRobotTypeCount(structureTypes[i]);
					// CHANGE THIS
				}
				for (int i=0; i<unitCount.length; i++) {
					//unitCount[i] = rc.getRobotTypeCount(unitTypes[i]);
					// CHANGE THIS
				}
				
				
				
            } catch (Exception e) {
                e.printStackTrace();
				System.out.println("You suck");
            }

			if (rc.getType() == RobotType.HQ) {
				try {					
					int fate = rand.nextInt(10000);
					
					
					
					int numSoldiers = unitCount[2];
					int numBashers = unitCount[3];
					int numFurbies = unitCount[0];
					int numBarracks = structureCount[2];
					
					if (rc.isAttackActive()) {
						attackSomething();
					}
					if (rc.isMovementActive() && rc.getTeamOre() >= 100 && fate < Math.pow(1.2,12-numFurbies)*10000) {
                        Direction spawndir = directions[rand.nextInt(8)];
						trySpawn(spawndir, RobotType.FURBY);
						//rc.transferSupplies((int)(rc.getSupplyLevel()/2), spawndir);
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
                    e.printStackTrace();
				}
			}
			
            if (rc.getType() == RobotType.TOWER) {
                try {					
					if (rc.isAttackActive()) {
						attackSomething();
					}
				} catch (Exception e) {
					System.out.println("Tower Exception");
                    e.printStackTrace();
				}
            }
			
			
			if (rc.getType() == RobotType.BASHER) {
                try {
                    RobotInfo[] adjacentEnemies = rc.senseNearbyRobots(2, enemyTeam);
					if (adjacentEnemies.length > 0 && rc.isAttackActive()) {
						rc.attackSquare(rc.getLocation());
					} else if (rc.isMovementActive()) {
						int fate = rand.nextInt(1000);
						if (fate < 800) {
							tryMove(directions[rand.nextInt(8)]);
						} else {
							tryMove(rc.senseHQLocation().directionTo(rc.getLocation()));
						}
					}
                } catch (Exception e) {
					System.out.println("Basher Exception");
					e.printStackTrace();
                }
            }
			
            if (rc.getType() == RobotType.SOLDIER) {
                try {
                    if (rc.isAttackActive()) {
						attackSomething();
					}
					if (rc.isMovementActive()) {
						int fate = rand.nextInt(1000);
						if (fate < 800) {
							tryMove(directions[rand.nextInt(8)]);
						} else {
							tryMove(rc.senseHQLocation().directionTo(rc.getLocation()));
						}
					}
                } catch (Exception e) {
					System.out.println("Soldier Exception");
					e.printStackTrace();
                }
            }
			
			if (rc.getType() == RobotType.FURBY) {
				try {
					if (rc.isAttackActive()) {
						attackSomething();
					}
					if (rc.isMovementActive()) {
						int fate = rand.nextInt(1000);
						if (fate < 10 && rc.getTeamOre() >= 250) {
							tryBuild(directions[rand.nextInt(8)],RobotType.BARRACKS);
						} else if (fate < 600) {
							rc.mine();
						} else if (fate < 900) {
							tryMove(directions[rand.nextInt(8)]);
						} else {
							tryMove(rc.senseHQLocation().directionTo(rc.getLocation()));
						}
					}
				} catch (Exception e) {
					System.out.println("Furby Exception");
                    e.printStackTrace();
				}
			}

            if (rc.getType() == RobotType.BARRACKS) {
				try {
					int fate = rand.nextInt(10000);
					
					int numSoldiers = unitCount[2];
					int numBashers = unitCount[3];
					int numFurbies = unitCount[0];
					int numBarracks = structureCount[2];
					
					if (rc.isMovementActive() && rc.getTeamOre() >= 50 && fate < Math.pow(1.2,15-numSoldiers-numBashers+numFurbies)*10000) {
						if (rc.getTeamOre() > 80 && fate % 2 == 0) {
							trySpawn(directions[rand.nextInt(8)],RobotType.BASHER);
						} else {
							trySpawn(directions[rand.nextInt(8)],RobotType.SOLDIER);
						}
					}
				} catch (Exception e) {
					System.out.println("Barracks Exception");
                    e.printStackTrace();
				}
			}
			
			rc.yield();
		}
	}
	
	static void attackSomething() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		if (enemies.length > 0) {
			rc.attackSquare(enemies[0].location);
		}
	}
	
	static void tryMove(Direction d) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 5) {
			rc.move(directions[(dirint+offsets[offsetIndex]+8)%8]);
		}
	}
	
	static void trySpawn(Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			rc.spawn(directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}
	
	static void tryBuild(Direction d, RobotType type) throws GameActionException {
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2,3,-3,4};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 8 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 8) {
			rc.build(directions[(dirint+offsets[offsetIndex]+8)%8], type);
		}
	}
	
	static int directionToInt(Direction d) {
		switch(d) {
			case NORTH:
				return 0;
			case NORTH_EAST:
				return 1;
			case EAST:
				return 2;
			case SOUTH_EAST:
				return 3;
			case SOUTH:
				return 4;
			case SOUTH_WEST:
				return 5;
			case WEST:
				return 6;
			case NORTH_WEST:
				return 7;
			default:
				return -1;
		}
	}
}
