package terrabot;

import battlecode.common.*;
import java.util.*;

//author: lygophile
public class RobotPlayer {
	static RobotController rc;
	static Team myTeam;
	static Team enemyTeam;
	static MapLocation enemyHQ;
	static MapLocation alliedHQ;
	static int myRange;
	static Random rand;
	//a channel containing a public pointer to the lowest undone mission
	static int MISSION_CHANNEL = 65432;
	//a channel containing the next open slot for a mission
	static int OPEN_CHANNEL = 65433;
	//a channel containing the current attack location
	static int ATTACK_CHANNEL = 65434;
	static int myMissionPointer = 0;
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST,
	Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST,
	Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	//0-towers, 1-supply depot, 2-barracks, 3-tech institute, 4-helipad, 5-training field, 6-tank fact, 7-miner fact, 8-aero lab, 9-handwash station
	static RobotType[] structureTypes = {RobotType.TOWER,RobotType.SUPPLYDEPOT,RobotType.BARRACKS,RobotType.TECHNOLOGYINSTITUTE,RobotType.HELIPAD,RobotType.TRAININGFIELD,
	RobotType.TANKFACTORY,RobotType.MINERFACTORY,RobotType.AEROSPACELAB,RobotType.HANDWASHSTATION};
	//0-BEAVER, 1-computer, 2-soldier, 3-basher, 4-miner, 5-drone, 6-tank, 7-commander, 8-launcher
	static RobotType[] unitTypes = {RobotType.BEAVER, RobotType.COMPUTER, RobotType.SOLDIER, RobotType.BASHER, 
	RobotType.MINER, RobotType.DRONE, RobotType.TANK, RobotType.COMMANDER, RobotType.LAUNCHER};
	static int[] structureCount = new int[structureTypes.length];
	static int[] unitCount = new int[unitTypes.length];
	
	static Direction lastDirection = null;
	static RobotInfo[] myRobots;
	
	static int targetAction;
	static RobotType targetType;
	static MapLocation targetLocation;
	
	public static void run(RobotController rc) {
		while (true) {
			rc.yield();
		}
	}
}
	
	/*
	
	public static void run(RobotController tomatojuice) {
		rc = tomatojuice;
		myRange = rc.getType().attackRadiusSquared;
        rand = new Random(rc.getID());
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		//only used by HQ
		ArrayList<Integer> missions;
		double distanceToEnemy;
		MapLocation attackLocation;
		//end only used by HQ
		
		enemyHQ = rc.senseEnemyHQLocation();
		alliedHQ = rc.senseHQLocation();
		
		if (rc.getType() == RobotType.HQ) {
			try {
				missions = new ArrayList<Integer>();
				distanceToEnemy = Math.sqrt(alliedHQ.distanceSquaredTo(enemyHQ));
				attackLocation = new MapLocation((3*alliedHQ.x+enemyHQ.x)/4,(3*alliedHQ.y+enemyHQ.y)/4);
				postAttackMission(attackLocation);
				System.out.println(attackLocation);
			} catch (Exception e) {
				System.out.println("HQ initialization exception: " + e.getMessage());
				e.printStackTrace();
			}
		} else if (rc.getType() == RobotType.BEAVER) {
			try {
				myMissionPointer = rc.readBroadcast(MISSION_CHANNEL);
			} catch (Exception e) {
				System.out.println("BEAVER initialization exception: " + e.getMessage());
				e.printStackTrace();
			}
		}
		
		while(true) {
            try {
                rc.setIndicatorString(0, "Ore here: " + rc.senseOre(rc.getLocation()));
                rc.setIndicatorString(1, "Location: " + rc.getLocation());
                rc.setIndicatorString(2, "My supply level: " + rc.getSupplyLevel());
				
				
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
					
					//reset unit/structure counts
					structureCount = new int[structureTypes.length];
					unitCount = new int[unitTypes.length];
					//count units/structures
					myRobots = rc.senseNearbyRobots(99999, myTeam);
					for (RobotInfo r : myRobots) {
						switch (r.type) {
							case BEAVER:
								unitCount[0]++;
								break;
							case SOLDIER:
								unitCount[2]++;
								break;
							case BASHER:
								unitCount[3]++;
								break;
							case MINER:
								unitCount[4]++;
								break;
							case DRONE:
								unitCount[5]++;
								break;
							case TANK:
								unitCount[6]++;
								break;
							case COMPUTER:
								unitCount[1]++;
								break;
							case LAUNCHER:
								unitCount[8]++;
								break;
							case TOWER:
								structureCount[0]++;
								break;
							case SUPPLYDEPOT:
								structureCount[1]++;
								break;
							case BARRACKS:
								structureCount[2]++;
								break;
							case TECHNOLOGYINSTITUTE:
								structureCount[3]++;
								break;
							case HELIPAD:
								structureCount[4]++;
								break;
							case TRAININGFIELD:
								structureCount[5]++;
								break;
							case TANKFACTORY:
								structureCount[6]++;
								break;
							case MINERFACTORY:
								structureCount[7]++;
								break;
							case AEROSPACELAB:
								structureCount[8]++;
								break;
							case HANDWASHSTATION:
								structureCount[9]++;
								break;
							case COMMANDER:
								unitCount[7]++;
								break;
							default:
								break;
						}
					}
					
					for (int i=0; i<structureCount.length; i++) {
						rc.broadcast(65200+i,structureCount[i]);
					}
					for (int i=0; i<unitCount.length; i++) {
						rc.broadcast(65100+i,unitCount[i]);
					}
					
					int numSoldiers = unitCount[2];
					int numBashers = unitCount[3];
					int numFurbies = unitCount[0];
					int numBarracks = structureCount[2];
					
					//logic
					if (myMissionPointer == 0) {
						postMission(buildMessage(1,2));
						myMissionPointer++;
						
						postMission(buildMessage(1,2));
						myMissionPointer++;
						postMission(buildMessage(1,2));
						myMissionPointer++;
						
					}
					
					//build: 100xy, where xy is the 2-digit number of the structure type
					//request supplies: 2
					
					
					
					if (rc.isAttackActive()) {
						attackSomething();
					}
					if (rc.isMovementActive() && rc.getTeamOre() >= 100 && fate < Math.pow(1.2,12-numFurbies)*10000) {
                        Direction spawndir = directions[rand.nextInt(8)];
						trySpawn(spawndir, RobotType.BEAVER);
						//rc.transferSupplies((int)(rc.getSupplyLevel()/2), spawndir);
					}
				} catch (Exception e) {
					System.out.println("HQ exception: " + e.getMessage());
                    e.printStackTrace();
				}
			} else {
				try {
					for (int i=0; i<structureCount.length; i++) {
						structureCount[i] = rc.readBroadcast(65200+i);
					}
					for (int i=0; i<unitCount.length; i++) {
						unitCount[i] = rc.readBroadcast(65100+i);
					}
				} catch (Exception e) {
					System.out.println("non-HQ exception: " + e.getMessage());
					e.printStackTrace();
				}
			}
			
            if (rc.getType() == RobotType.TOWER) {
                try {					
					if (rc.isAttackActive()) {
						attackSomething();
					}
				} catch (Exception e) {
					System.out.println("tower exception: " + e.getMessage());
                    e.printStackTrace();
				}
            }
			
			
			if (rc.getType() == RobotType.BASHER) {
                try {
					retrieveAttackMission();
					if (rc.getID() == 339 && Clock.getRoundNum() == 100) {
						System.out.println(targetLocation);
					}
					attackMove(targetLocation);
                } catch (Exception e) {
					System.out.println("basher exception: " + e.getMessage());
					e.printStackTrace();
                }
            }
			
            if (rc.getType() == RobotType.SOLDIER) {
                try {
                    retrieveAttackMission();
					attackMove(targetLocation);
                } catch (Exception e) {
					System.out.println("soldier exception: " + e.getMessage());
					e.printStackTrace();
                }
            }
			
			if (rc.getType() == RobotType.BEAVER) {
				try {
					//reading messages, checking missions
					if (targetAction == 0) {
						int mission;
						int openpointer = rc.readBroadcast(OPEN_CHANNEL);
						while (targetAction == 0 && myMissionPointer < openpointer) {
							mission = retrieveMission();
							if (mission > 0) {
								System.out.println(mission);
								int action = actionFromMessage(mission);
								
								//conditions for mission acceptance
								if (action == 1) {
									targetAction = action;
									targetType = structureTypes[infoFromMessage(mission)];
									confirmMission(myMissionPointer);
								} else {
									
								}
								myMissionPointer++;
							}
						}
					}
					
					if (targetAction == 0) {
						RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(myRange,enemyTeam);
						if (nearbyEnemies.length > 0) {
							if (rc.isAttackActive()) {
								rc.attackSquare(nearbyEnemies[0].location);
							}
						} else if (rc.isMovementActive()) {
							smartMine();
						}
					} else if (targetAction == 1) {
						if (rc.isMovementActive()) {
							boolean building = smartBuild(targetType);
							if (building) {
								targetAction = 0;
							}
						}
					} else if (targetAction == 2) {
						
					} else {
						
					}
					
					
					
				} catch (Exception e) {
					System.out.println("BEAVER exception: " + e.getMessage());
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
					System.out.println("barracks exception: " + e.getMessage());
                    e.printStackTrace();
				}
			}
			
			rc.yield();
		}
	}
	
	//evens supply out between self and adjacent units.
	static void distributeSupplies() {
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(2,myTeam);
		int numAllies = 1;
		double mySupply = rc.getSupplyLevel();
		double totalSupply = mySupply;
		for (RobotInfo r : nearbyAllies) {
			//if (r.supplyLevel >= 
		}
	}
	
	//builds something smartly.
	//returns true if the build has started.
	static boolean smartBuild(RobotType r) throws GameActionException {
		MapLocation myloc = rc.getLocation();
		if (r == RobotType.SUPPLYDEPOT || r == RobotType.TECHNOLOGYINSTITUTE || r == RobotType.HANDWASHSTATION) {
			if (myloc.distanceSquaredTo(enemyHQ) < alliedHQ.distanceSquaredTo(enemyHQ)) {
				if (myloc.distanceSquaredTo(alliedHQ) > 100) {
					tryMove(rc.getLocation().directionTo(rc.senseHQLocation()));
					return false;
				} else {
					tryMove(rc.senseEnemyHQLocation().directionTo(alliedHQ));
					return false;
				}
			} else {
				Direction trydir = enemyHQ.directionTo(rc.getLocation());
				Direction bestdir = trydir;
				int basedirindex = directionToInt(trydir);
				int farthestdist = 0;
				for (int i=0; i<8; i++) {
					trydir = directions[basedirindex+i];
					if (rc.canMove(trydir)) {
						MapLocation tryloc = rc.getLocation().add(trydir);
						int trydist = tryloc.distanceSquaredTo(enemyHQ);
						if (trydist > farthestdist) {
							farthestdist = trydist;
							bestdir = trydir;
						}
					}
				}
				if (farthestdist == 0) {
					//blocked, can't do anything.
					return false;
					
				} else if (rc.canBuild(bestdir,r)) {
					rc.build(bestdir,r);
					return true;
				}
			}
		} else {
			if (myloc.distanceSquaredTo(enemyHQ) < 3*myloc.distanceSquaredTo(alliedHQ)) {
				navigate(alliedHQ);
				return false;
			} else {
				Direction trydir = enemyHQ.directionTo(rc.getLocation());
				Direction bestdir = trydir;
				int basedirindex = directionToInt(trydir);
				int farthestdist = 0;
				for (int i=0; i<8; i++) {
					trydir = directions[basedirindex+i];
					if (rc.canMove(trydir) && rc.senseNearbyRobots(rc.getLocation().add(trydir),2,myTeam).length < 3) {
						MapLocation tryloc = rc.getLocation().add(trydir);
						int trydist = tryloc.distanceSquaredTo(enemyHQ);
						if (trydist > farthestdist) {
							farthestdist = trydist;
							bestdir = trydir;
						}
					}
				}
				if (farthestdist == 0) {
					//no good locations detected; move somewhere randomly to find a good spot
					tryMove(directions[rand.nextInt(8)]);
					return false;
				} else if (rc.canBuild(bestdir,r)) {
					rc.build(bestdir,r);
					return true;
				}
			}
		}
		return false;
	}
	
	//Mines or moves to a better mining location. Always performs an action.
	static void smartMine() throws GameActionException {
		int threshold;
		int lowerbound;
		if (rc.getType() == RobotType.BEAVER) {
			threshold = GameConstants.BEAVER_MINE_MAX*GameConstants.BEAVER_MINE_RATE;
			lowerbound = GameConstants.BEAVER_MINE_RATE;
		} else {
			threshold = GameConstants.MINER_MINE_MAX*GameConstants.MINER_MINE_RATE;
			lowerbound = GameConstants.MINER_MINE_RATE;
		}
		RobotInfo[] nearbyAllies = rc.senseNearbyRobots(2,myTeam);
		int clutter = 0;
		if (nearbyAllies.length > 2) {
			for (RobotInfo r : nearbyAllies) {
				if (r.type == RobotType.BEAVER || r.type == RobotType.MINER) {
					clutter++;
				} else if (r.type == RobotType.HQ || r.type == RobotType.BARRACKS || r.type == RobotType.TANKFACTORY || r.type == RobotType.HELIPAD || r.type == RobotType.MINERFACTORY) {
					if (rc.senseNearbyRobots(r.location,2,myTeam).length > 6) {
						clutter+=10;
					}
				}
			}
		}
		if (clutter > 2) {
			tryMove(directions[rand.nextInt(8)]);
			return;
		} else if (rc.senseOre(rc.getLocation()) > threshold) {
			rc.mine();
			return;
		} else {
			boolean shouldmove = false;
			Direction bestdir = null;
			double basemin = rc.senseOre(rc.getLocation());
			double mostmin = basemin;
			for (int i=0; i<8; i++) {
				Direction trydir = directions[i];
				double trymin = rc.senseOre(rc.getLocation().add(trydir));
				if (trymin > mostmin && rc.canMove(trydir) && (trymin > basemin*1.2 || basemin <= lowerbound)) {
					bestdir = trydir;
					shouldmove = true;
					mostmin = trymin;
				}
			}
			if (shouldmove) {
				rc.move(bestdir);
				return;
			} else {
				rc.mine();
				return;
			}
		}
	}
	
	//naive attackmove.
	static void attackMove(MapLocation target) throws GameActionException {
		RobotInfo[] attackableEnemies = rc.senseNearbyRobots(myRange,enemyTeam);
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(35, enemyTeam);
		if (attackableEnemies.length > 0) {
			if (rc.isAttackActive()) {
				rc.attackSquare(attackableEnemies[0].location);
			}
		} else if (rc.isMovementActive()) {
			if (nearbyEnemies.length > 0) {
				MapLocation closestEnemy = rc.getLocation();
				int closestdist = 999999;
				for (RobotInfo r : nearbyEnemies) {
					int trydist = r.location.distanceSquaredTo(rc.getLocation());
					if (trydist < closestdist) {
						closestEnemy = r.location;
						closestdist = trydist;
					}
				}
				navigate(closestEnemy);
			} else {
				navigate(target);
			}
		}
	}
	
	//attempts to navigate to the target location.
	static void navigate (MapLocation target) {
		if (rc.getLocation().equals(target)) {
			return;
		}
		try {
			Direction d = rc.getLocation().directionTo(target);
			int[] moveOffsets;
			int a = rand.nextInt(274);
			if (a % 2 == 0) {
				moveOffsets = new int[] {0, 1, 7, 2, 6};
			} else {
				moveOffsets = new int[] {0, 7, 1, 6, 2};
			}
			Direction[] moves = new Direction[5];
			MapLocation[] locations = new MapLocation[5];
		
			boolean[] notValid = new boolean[5];
			boolean[] canMove = new boolean[5];
			int currentDistance = rc.getLocation().distanceSquaredTo(target);
			boolean[] sameDistance = new boolean[5];
			moves[0] = d;
			moves[1] = directions[directionToInt(d) + moveOffsets[1]];
			moves[2] = directions[directionToInt(d) + moveOffsets[2]];
			moves[3] = directions[directionToInt(d) + moveOffsets[3]];
			moves[4] = directions[directionToInt(d) + moveOffsets[4]];
			locations[0] = rc.getLocation().add(d);
			locations[1] = rc.getLocation().add(moves[1]);
			locations[2] = rc.getLocation().add(moves[2]);
			locations[3] = rc.getLocation().add(moves[3]);
			locations[4] = rc.getLocation().add(moves[4]);
			canMove[0] = rc.canMove(moves[0]);
			canMove[1] = rc.canMove(moves[1]);
			canMove[2] = rc.canMove(moves[2]);
			canMove[3] = rc.canMove(moves[3]);
			canMove[4] = rc.canMove(moves[4]);
			notValid[0] = (!canMove[0]);
			notValid[1] = (!canMove[1] || currentDistance < locations[1].distanceSquaredTo(target));
			notValid[2] = (!canMove[2] || currentDistance < locations[2].distanceSquaredTo(target));
			notValid[3] = (!canMove[3] || currentDistance < locations[3].distanceSquaredTo(target));
			notValid[4] = (!canMove[4] || currentDistance < locations[4].distanceSquaredTo(target));
			int numGoodMoves = 0;
			int[] pointer = new int[5];
			if (!notValid[0] && !sameDistance[0]) {
				pointer[numGoodMoves++] = 0;
			}
			if (!notValid[1] && !sameDistance[1]) {
				pointer[numGoodMoves++] = 1;
			}
			if (!notValid[2] && !sameDistance[2]) {
				pointer[numGoodMoves++] = 2;
			}
			if (!notValid[3] && !sameDistance[3]) {
				pointer[numGoodMoves++] = 3;
				//if (Clock.getRoundNum() == 379 && rc.getRobot().getID() == 111) System.out.println("move3");
			}
			if (!notValid[4] && !sameDistance[4]) {
				pointer[numGoodMoves++] = 4;
				//if (Clock.getRoundNum() == 379 && rc.getRobot().getID() == 111) System.out.println("move4");
			}
			switch (numGoodMoves) {
				case 0:
					//No move available
					return;
				case 1:
					rc.move(moves[pointer[0]]);
					return;
				default:
					int index = 1;
					int minDistance = 9999999;
					for (int i = numGoodMoves; --i >= 0; ) {
						int distance = locations[pointer[i]].distanceSquaredTo(target);
						if (distance < minDistance) {
							minDistance = distance;
							index = pointer[i];
						}
					}
					rc.move(moves[index]);
					break;
			}
		} catch (Exception e) {
			System.out.println("navigate-location exception occurred: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	//attacks an enemy in range. If there are no enemies in range, performs no action.
	static void attackSomething() throws GameActionException {
		RobotInfo[] enemies = rc.senseNearbyRobots(myRange, enemyTeam);
		if (enemies.length > 0) {
			rc.attackSquare(enemies[0].location);
		}
	}
	
	//Attempts to move in a direction. Will not move if completely blocked.
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
	
	//Attempts to spawn in a direction. Will not spawn if completely surrounded.
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
	
	//Attempts to build in a direction. Will not build if completely surrounded.
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
	
	static void postAttackMission(MapLocation loc) throws GameActionException {
		int coordx = 100+loc.x-alliedHQ.x;
		int coordy = 100+loc.y-alliedHQ.y;
		int message = coordx*1000+coordy;
		rc.broadcast(ATTACK_CHANNEL,message);
	}
	
	static void retrieveAttackMission() throws GameActionException {
		int location = rc.readBroadcast(ATTACK_CHANNEL);
		if (location != 0) {
			int locx = location/1000-100+alliedHQ.x;
			int locy = location%1000-100+alliedHQ.y;
			targetLocation = new MapLocation(locx,locy);
		}
	}
	
	static int retrieveMission() throws GameActionException {
		return rc.readBroadcast(myMissionPointer);
	}
	
	static void confirmMission(int mmp) throws GameActionException {
		rc.broadcast(mmp,-1);
		int missionchannel = rc.readBroadcast(MISSION_CHANNEL);
		while (rc.readBroadcast(missionchannel) < 0) {
			rc.broadcast(MISSION_CHANNEL,missionchannel+1);
			System.out.println("channel " + missionchannel + " wiped");
			missionchannel = rc.readBroadcast(MISSION_CHANNEL);
		}
	}
	
	static void postMission(int missionmessage) throws GameActionException {
		int openchannel = rc.readBroadcast(OPEN_CHANNEL);
		rc.broadcast(openchannel, missionmessage);
		rc.broadcast(OPEN_CHANNEL,openchannel+1);
		System.out.println("channel " + openchannel + " posted");
	}
	
	static int buildMessage(int action, int info) {
		return action*10000+info;
	}
	
	static int actionFromMessage(int m) {
		return m/10000;
	}
	
	static int infoFromMessage(int m) {
		return m%10000;
	}
}
*/