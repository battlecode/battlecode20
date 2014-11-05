package examplefuncsplayer;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	static Random rand = new Random();
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	public static void run(RobotController rc) {
        MapLocation enemyLoc = rc.senseEnemyHQLocation();
        Direction lastDirection = null;
		Team myTeam = rc.getTeam();
		Team enemyTeam = myTeam.opponent();

		while(true) {
            try {
                rc.setIndicatorString(0, "Ore here: " + rc.senseOre(rc.getLocation()));
                rc.setIndicatorString(1, "Location: " + rc.getLocation());
                rc.setIndicatorString(2, "My supply level: " + rc.getSupplyLevel());
            } catch (Exception e) {
                System.out.println("You suck");
            }

			if (rc.getType() == RobotType.HQ) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					if (rc.canMove() && rc.getTeamOre() >= 100) {
                        Direction spawndir;
						if (enemyLoc != null) {
							spawndir = rc.getLocation().directionTo(enemyLoc);
						} else {
							spawndir = directions[rand.nextInt(8)];
						}
						int offsetIndex = 0;
						int[] offsets = {0,1,-1,2,-2,3,-3,4};
						int spawndirint = directionToInt(spawndir);
						boolean blocked = false;
						while (offsetIndex < 8 && !rc.canMove(directions[(spawndirint+offsets[offsetIndex]+8)%8])) {
							offsetIndex++;
						}
						if (offsetIndex >= 8) {
							System.out.println("creep blocked");
						} else {
							rc.spawn(directions[(spawndirint+offsets[offsetIndex]+8)%8], RobotType.FURBY);
						}
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
                    e.printStackTrace();
				}
			}
			if (rc.getType() == RobotType.BARRACKS) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					
				} catch (Exception e) {
					System.out.println("HQ Exception");
                    e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.SUPPLYDEPOT) {
				try {					
                    rc.transferSuppliesToHQ();
				} catch (Exception e) {
					System.out.println("supplydepot exception");
                    e.printStackTrace();
				}
			}

            if (rc.getType() == RobotType.TRAININGFIELD) {
                try {					
					
				} catch (Exception e) {
					System.out.println("trainingfield Exception");
                    e.printStackTrace();
				}
            }

            if (rc.getType() == RobotType.MINER) {
                try {
                    
				} catch (Exception e) {
					System.out.println("miner Exception");
                    e.printStackTrace();
				}
            }

            if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.DRONE || rc.getType() == RobotType.COMMANDER) {
                try {
                    
                } catch (Exception e) {
                }
            }
			
			if (rc.getType() == RobotType.FURBY || rc.getType() == RobotType.BUILDER) {
				try {
					if (rc.canAttack()) {
						Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 8, enemyTeam);
						if (enemies.length > 0) {
							rc.attackSquare(rc.senseLocationOf(enemies[0]));
						}
					}
					if (rc.canMove()) {
						int fate = rand.nextInt(1000);
						
						if (fate < 500) {
							rc.mine();
						} else if (fate < 900) {
							Direction movedir = directions[rand.nextInt(8)];
							
							tryMove(rc, movedir);
						} else {
							tryMove(rc, rc.senseHQLocation().directionTo(rc.getLocation()));
						}
					}
				} catch (Exception e) {
					System.out.println("Soldier Exception");
                    e.printStackTrace();
				}
			}

            if (rc.getType() == RobotType.BARRACKS) {
				try {					
				} catch (Exception e) {
					System.out.println("Barracks Exception");
                    e.printStackTrace();
				}
            }

			rc.yield();
		}
	}
	
	static void tryMove(RobotController rc, Direction d) throws GameActionException {
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
