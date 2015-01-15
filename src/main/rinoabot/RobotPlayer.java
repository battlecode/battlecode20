package rinoabot;

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
	//a channel containing the next open missile channel
	static int MISSILE_CHANNEL = 65435;
	static int BASE_MISSILE_CHANNEL = 50000;
	static int myMissionPointer = 0;

	static Direction[] directions;
	static RobotType[] structureTypes;
	static RobotType[] unitTypes;
	static int[] structureCount;
	static int[] unitCount;
	
	/*
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
	*/

	static RobotInfo[] myRobots;
	static RobotInfo[] suppliableAllies;
	static int supplyWait = 0;
	static boolean supplied = false; //set to true for units to not use supply
	static double LOW_SUPPLY_THRESHOLD = 200;

	static int targetAction;
	static RobotType targetType;
	static MapLocation targetLocation;

	static int NUM_BEAVERS = 12;
	static int NUM_SOLDIERS = 0;
	static int NUM_BASHERS = 5;
	static int NUM_TANKS = 0;
	static int NUM_DRONES = 0;
	static int NUM_LAUNCHERS = 0;
	static int NUM_COMMANDERS = 1;
	static boolean DONE_SPAWNING = false;
	static int WAIT_TURNS = 500;

	public static void run(RobotController tomatojuice) {
		rc = tomatojuice;
		myRange = rc.getType().attackRadiusSquared;
		rand = new Random(rc.getID());
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		enemyHQ = rc.senseEnemyHQLocation();
		alliedHQ = rc.senseHQLocation();

		//################################################################################################################################################################################
		//#MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE #MISSILE 
		if (rc.getType() == RobotType.MISSILE) {
			try {
				myMissionPointer = rc.readBroadcast(MISSILE_CHANNEL);
				while (targetLocation == null) {
					int mission = retrieveMission();
					int myx = mission/10000000;
					int myy = (mission/1000000)%10;
					if (myx == rc.getLocation().x%10 && myy == rc.getLocation().y%10) {
						int locx = (mission/1000)%1000-200+alliedHQ.x;
						int locy = mission%1000-200+alliedHQ.y;
						targetLocation = new MapLocation(locx,locy);
					}
					myMissionPointer--;
				}
			} catch (Exception e) {
				System.out.println("missile initialization exception: " + e.getMessage());
				e.printStackTrace();
			}
			while(true) {
				try {
					missileMove(targetLocation);
				} catch (Exception e) {
					System.out.println("missile exception: " + e.getMessage());
					e.printStackTrace();
				}
				rc.yield();
			}
		}

		directions = new Direction[] {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST,
				Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST,
				Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
		//0-towers, 1-supply depot, 2-barracks, 3-tech institute, 4-helipad, 5-training field, 6-tank fact, 7-miner fact, 8-aero lab, 9-handwash station
		structureTypes = new RobotType[] {RobotType.TOWER,RobotType.SUPPLYDEPOT,RobotType.BARRACKS,RobotType.TECHNOLOGYINSTITUTE,RobotType.HELIPAD,RobotType.TRAININGFIELD,
				RobotType.TANKFACTORY,RobotType.MINERFACTORY,RobotType.AEROSPACELAB,RobotType.HANDWASHSTATION};
		//0-BEAVER, 1-computer, 2-soldier, 3-basher, 4-miner, 5-drone, 6-tank, 7-commander, 8-launcher
		unitTypes = new RobotType[] {RobotType.BEAVER, RobotType.COMPUTER, RobotType.SOLDIER, RobotType.BASHER,
				RobotType.MINER, RobotType.DRONE, RobotType.TANK, RobotType.COMMANDER, RobotType.LAUNCHER};
		structureCount = new int[structureTypes.length];
		unitCount = new int[unitTypes.length];

		//only used by HQ
		ArrayList<Integer> missions;
		double distanceToEnemy;
		MapLocation attackLocation = null;
		double supplyFlowIn = 0;
		double supplyFlowOut = 0;
		double netSupply = 0;
		int waitBuildDepot = 0;
		//end only used by HQ

		if (rc.getType() == RobotType.HQ) {
			try {
				missions = new ArrayList<Integer>();
				distanceToEnemy = Math.sqrt(alliedHQ.distanceSquaredTo(enemyHQ));
				attackLocation = new MapLocation((3*alliedHQ.x+enemyHQ.x)/4,(3*alliedHQ.y+enemyHQ.y)/4);
				postAttackMission(attackLocation);
				//set the base missile channel
				rc.broadcast(MISSILE_CHANNEL,BASE_MISSILE_CHANNEL);
			} catch (Exception e) {
				System.out.println("HQ initialization exception: " + e.getMessage());
				e.printStackTrace();
			}
		} else if (rc.getType() == RobotType.BEAVER) {
			try {
				myMissionPointer = rc.readBroadcast(MISSION_CHANNEL);
			} catch (Exception e) {
				System.out.println("non-HQ initialization exception: " + e.getMessage());
				e.printStackTrace();
			}
		} else if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.BASHER || rc.getType() == RobotType.TANK || rc.getType() == RobotType.DRONE || rc.getType() == RobotType.LAUNCHER || rc.getType() == RobotType.COMMANDER) {
			//combat unit
		}

		while(true) {
			try {
				if (rc.getType() != RobotType.HQ) {
					rc.setIndicatorString(0, "Ore here: " + rc.senseOre(rc.getLocation()));
					rc.setIndicatorString(1, "Action: " + targetAction);
					rc.setIndicatorString(2, "My supply level: " + rc.getSupplyLevel());
				} else {
					rc.setIndicatorString(0, "Team ore: " + rc.getTeamOre());
					rc.setIndicatorString(1, "Net supply usage: " + netSupply);
					rc.setIndicatorString(2, "My supply level: " + rc.getSupplyLevel());
				}
				suppliableAllies = null;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("You suck");
			}

			//################################################################################################################################################################################
			//#HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ #HQ 
			if (rc.getType() == RobotType.HQ) {
				try {
					waitBuildDepot++;

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

					supplyFlowIn = GameConstants.SUPPLY_GEN_BASE * (GameConstants.SUPPLY_GEN_MULTIPLIER + Math.pow(structureCount[1], GameConstants.SUPPLY_GEN_EXPONENT));
					supplyFlowOut = 0;
					for (int i=0; i<unitCount.length; i++) {
						supplyFlowOut+=unitCount[i]*unitTypes[i].supplyUpkeep;
					}
					netSupply = supplyFlowIn-supplyFlowOut;

					for (int i=0; i<structureCount.length; i++) {
						rc.broadcast(65200+i,structureCount[i]);
					}
					for (int i=0; i<unitCount.length; i++) {
						rc.broadcast(65100+i,unitCount[i]);
					}

					int numSoldiers = unitCount[2];
					int numBashers = unitCount[3];
					int numBeavers = unitCount[0];
					int numTanks = unitCount[6];
					int numDrones = unitCount[5];
					int numLaunchers = unitCount[8];
					int numCommanders = unitCount[7];

					//check missions
					int mission;
					int openpointer = rc.readBroadcast(OPEN_CHANNEL);
					while (rc.getSupplyLevel() > 2*LOW_SUPPLY_THRESHOLD && myMissionPointer < openpointer) {
						mission = retrieveMission();
						if (mission > 0) {
							//System.out.println(mission);
							int action = actionFromMessage(mission);
							//conditions for mission acceptance
							if (action == 2) {
								int idToSupply = infoFromMessage(mission);
								if (suppliableAllies == null) {
									suppliableAllies = rc.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,myTeam);
								}
								for (int i=0; i<suppliableAllies.length; i++) {
									RobotInfo r = suppliableAllies[i];
									if (r.ID == idToSupply) {
										//transfer supplies
										rc.transferSupplies((int)(rc.getSupplyLevel()-r.supplyLevel)/2,r.location);
										i = suppliableAllies.length;
									}
								}
							} else {

							}
						}
						myMissionPointer++;
					}

					//econ logic
					if (Clock.getRoundNum() == 0) {
						postMission(buildMessage(1,2));
						myMissionPointer++;
					} else if (Clock.getRoundNum() == 100) {
						postMission(buildMessage(1,3));
						myMissionPointer++;
					} else if (Clock.getRoundNum() == 200) {
						postMission(buildMessage(1,4));
						myMissionPointer++;
						postMission(buildMessage(1,6));
						myMissionPointer++;
					} else if (Clock.getRoundNum() == 300) {
						postMission(buildMessage(1,5));
						myMissionPointer++;
						postMission(buildMessage(1,8));
						myMissionPointer++;
					}
					if (netSupply < supplyFlowIn/4 && waitBuildDepot > 20) {
						postMission(buildMessage(1,1));
						System.out.println("We require more vespene gas");
						waitBuildDepot=0;
					}
					/*
					build: 100xy, where xy is the 2-digit number of the structure type
					request supplies: 2
					*/


					if (rc.isWeaponReady()) {
						attackSomething();
					}
					if (rc.isCoreReady() && rc.getTeamOre() >= 100 && numBeavers < NUM_BEAVERS && !DONE_SPAWNING) {
						Direction spawndir = directions[rand.nextInt(8)];
						trySpawn(spawndir, RobotType.BEAVER);
						//rc.transferSupplies((int)(rc.getSupplyLevel()/2), spawndir);
					}

					if (numBeavers == NUM_BEAVERS && numSoldiers == NUM_SOLDIERS && numBashers == NUM_BASHERS && numTanks == NUM_TANKS && numDrones == NUM_DRONES && numLaunchers == NUM_LAUNCHERS && numCommanders == NUM_COMMANDERS) {
						DONE_SPAWNING = true;
					}

					if (DONE_SPAWNING && Clock.getRoundNum() > WAIT_TURNS) {
						if (Clock.getRoundNum()%6 == 0) {
							attackLocation = attackLocation.add(attackLocation.directionTo(enemyHQ));
							postAttackMission(attackLocation);
						}
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

			//################################################################################################################################################################################
			//#BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER #BEAVER 
			if (rc.getType() == RobotType.BEAVER) {
				try {
					//reading messages, checking missions

					if (targetAction == 0) {
						int mission;
						int openpointer = rc.readBroadcast(OPEN_CHANNEL);
						boolean acceptingMissions = rc.isCoreReady();
						while (acceptingMissions && targetAction == 0 && myMissionPointer < openpointer) {
							mission = retrieveMission();
							if (mission > 0) {
								int action = actionFromMessage(mission);

								//conditions for mission acceptance
								if (action == 1) {
									targetAction = action;
									targetType = structureTypes[infoFromMessage(mission)];
									confirmMission(myMissionPointer);
								} else if (action == 2) {
									int idToSupply = infoFromMessage(mission);
									if (idToSupply == rc.getID()) {
										//No one has supplied me, so I should head back to HQ
										targetAction = 2;
									}
									if (rc.getSupplyLevel() > 2*LOW_SUPPLY_THRESHOLD) {
										if (suppliableAllies == null) {
											suppliableAllies = rc.senseNearbyRobots(GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED,myTeam);
										}
										for (int i=0; i<suppliableAllies.length; i++) {
											RobotInfo r = suppliableAllies[i];
											if (r.ID == idToSupply && r.location.distanceSquaredTo(alliedHQ) > GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED) {
												//transfer supplies
												rc.transferSupplies((int)(rc.getSupplyLevel()-r.supplyLevel)/2,r.location);
												confirmMission(myMissionPointer);
												i = suppliableAllies.length;
											}
										}
									}
								}
							}
							myMissionPointer++;
						}
					}

					if (targetAction == 0) {
						RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(myRange,enemyTeam);
						if (nearbyEnemies.length > 0) {
							if (rc.isWeaponReady()) {
								rc.attackLocation(nearbyEnemies[0].location);
							}
						} else if (rc.isCoreReady()) {
							smartMine();
						}

						if (rc.getSupplyLevel() <= LOW_SUPPLY_THRESHOLD) {
							postSupplyMission();
							//supplyWait = 1;
						}
					} else if (targetAction == 1) {
						if (rc.isCoreReady()) {
							boolean building = smartBuild(targetType);
							if (building) {
								targetAction = 0;
							}
						}
					} else if (targetAction == 2) {
						//go to HQ and get supply.
						if (rc.isCoreReady()) {
							navigate(alliedHQ);
						}
						if (rc.getLocation().distanceSquaredTo(alliedHQ) <= GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED && Clock.getRoundNum()%3 == 0) {
							postSupplyMission();
						}
						if (rc.getSupplyLevel() > LOW_SUPPLY_THRESHOLD) {
							targetAction = 0;
						}
					} else {

					}

				} catch (Exception e) {
					System.out.println("BEAVER exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			//################################################################################################################################################################################
			//#TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER #TOWER 
			if (rc.getType() == RobotType.TOWER) {
				try {
					//System.out.println(myRange);
					if (rc.isWeaponReady()) {
						attackSomething();
					}
				} catch (Exception e) {
					System.out.println("tower exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			//################################################################################################################################################################################
			//#BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER #BASHER 
			if (rc.getType() == RobotType.BASHER) {
				try {
					if (!supplied) {
						bashMove(alliedHQ);
						if (rc.getLocation().distanceSquaredTo(alliedHQ) < GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED && Clock.getRoundNum()%3 == 0) {
							postSupplyMission();
							supplied = true;
						}
					} else {
						retrieveAttackMission();
						bashMove(targetLocation);
					}
				} catch (Exception e) {
					System.out.println("basher exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			//################################################################################################################################################################################
			//#SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER #SOLDIER 
			if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.DRONE || rc.getType() == RobotType.TANK) {
				try {
					if (!supplied) {
						attackMove(alliedHQ);
						if (rc.getLocation().distanceSquaredTo(alliedHQ) < GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED && Clock.getRoundNum()%3 == 0) {
							postSupplyMission();
							supplied = true;
						}
					} else {
						retrieveAttackMission();
						attackMove(targetLocation);
					}
				} catch (Exception e) {
					System.out.println("combat unit exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			//################################################################################################################################################################################
			//#COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER #COMMANDER 
			if (rc.getType() == RobotType.COMMANDER) {
				try {
					if (!supplied) {
						attackMove(alliedHQ);
						if (rc.getLocation().distanceSquaredTo(alliedHQ) < GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED && Clock.getRoundNum()%3 == 0) {
							postSupplyMission();
							supplied = true;
						}
					} else {
						if (Clock.getRoundNum() == 500) {
							rc.castFlash(rc.getLocation().add(Direction.NORTH,3));

						} else {
							retrieveAttackMission();
							attackMove(targetLocation);
						}
					}
				} catch (Exception e) {
					System.out.println("commander exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			//################################################################################################################################################################################
			//#LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER #LAUNCHER 
			if (rc.getType() == RobotType.LAUNCHER) {
				try {
					if (!supplied) {
						launcherMove(alliedHQ);
						if (rc.getLocation().distanceSquaredTo(alliedHQ) < GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED && Clock.getRoundNum()%3 == 0) {
							postSupplyMission();
							supplied = true;
						}
					} else {
						retrieveAttackMission();
						/*
						if (Clock.getRoundNum() == 500) {
							rc.move(Direction.EAST);
						} if (Clock.getRoundNum() == 501) {
							rc.launchMissile(Direction.NORTH);
							postMissileMission(rc.getLocation().add(Direction.NORTH),alliedHQ);
							rc.launchMissile(Direction.EAST);
							postMissileMission(rc.getLocation().add(Direction.EAST),alliedHQ);
						}*/
						launcherMove(targetLocation);
					}
				} catch (Exception e) {
					System.out.println("launcher exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.BARRACKS && !DONE_SPAWNING) {
				try {


					int numSoldiers = unitCount[2];
					int numBashers = unitCount[3];
					int numBeavers = unitCount[0];

					if (rc.isCoreReady() && rc.getTeamOre() >= 200 && numSoldiers < NUM_SOLDIERS) {
						trySpawn(directions[rand.nextInt(8)],RobotType.SOLDIER);
					} else if (rc.isCoreReady() && rc.getTeamOre() >= 200 && numBashers < NUM_BASHERS) {
						trySpawn(directions[rand.nextInt(8)],RobotType.BASHER);
					}

					if (numSoldiers == NUM_SOLDIERS && numBashers == NUM_BASHERS) {
						DONE_SPAWNING = true;
					}
				} catch (Exception e) {
					System.out.println("barracks exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.TANKFACTORY && !DONE_SPAWNING) {
				try {
					int numTanks = unitCount[6];

					if (rc.isCoreReady() && rc.getTeamOre() >= 300 && numTanks < NUM_TANKS) {
						trySpawn(directions[rand.nextInt(8)],RobotType.TANK);
					}

					if (numTanks == NUM_TANKS) {
						DONE_SPAWNING = true;
					}
				} catch (Exception e) {
					System.out.println("barracks exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.HELIPAD && !DONE_SPAWNING) {
				try {
					int numDrones = unitCount[5];

					if (rc.isCoreReady() && rc.getTeamOre() >= 300 && numDrones < NUM_DRONES) {
						trySpawn(directions[rand.nextInt(8)],RobotType.DRONE);
					}

					if (numDrones == NUM_DRONES) {
						DONE_SPAWNING = true;
					}
				} catch (Exception e) {
					System.out.println("barracks exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.TRAININGFIELD && !DONE_SPAWNING) {
				try {
					int numCommanders = unitCount[7];

					if (rc.isCoreReady() && rc.getTeamOre() >= 300 && numCommanders < NUM_COMMANDERS) {
						trySpawn(directions[rand.nextInt(8)],RobotType.COMMANDER);
					}

					if (numCommanders == NUM_COMMANDERS) {
						DONE_SPAWNING = true;
					}
				} catch (Exception e) {
					System.out.println("training field exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.AEROSPACELAB && !DONE_SPAWNING) {
				try {
					int numLaunchers = unitCount[8];

					if (rc.isCoreReady() && rc.getTeamOre() >= 600 && numLaunchers < NUM_LAUNCHERS) {
						trySpawn(directions[rand.nextInt(8)],RobotType.LAUNCHER);
					}

					if (numLaunchers == NUM_LAUNCHERS) {
						DONE_SPAWNING = true;
					}
				} catch (Exception e) {
					System.out.println("aerospace lab exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.SUPPLYDEPOT) {
				try {
					//rc.transferSuppliesToHQ();
				} catch (Exception e) {
					System.out.println("supply depot exception: " + e.getMessage());
					e.printStackTrace();
				}
			}

			if (rc.getType() == RobotType.MISSILE) {
				System.out.println("I SHOULDNT BE HERE");
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

		/*
		if (!rc.hasBuildRequirements(r)) {
			return false;
		}
		*/
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
	//	static RobotType[] structureTypes = {RobotType.TOWER,RobotType.SUPPLYDEPOT,RobotType.BARRACKS,RobotType.TECHNOLOGYINSTITUTE,RobotType.HELIPAD,RobotType.TRAININGFIELD,
	//RobotType.TANKFACTORY,RobotType.MINERFACTORY,RobotType.HANDWASHSTATION,RobotType.AEROSPACELAB};

	//Mines or moves to a better mining location. Always performs an action.
	static void smartMine() throws GameActionException {
		int threshold;
		int lowerbound;
		if (rc.getType() == RobotType.BEAVER) {
			threshold = (int) GameConstants.BEAVER_MINE_MAX*GameConstants.BEAVER_MINE_RATE;
			lowerbound = GameConstants.BEAVER_MINE_RATE;
		} else {
			threshold = (int) GameConstants.MINER_MINE_MAX*GameConstants.MINER_MINE_RATE;
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

	static void tryLaunch(MapLocation target) throws GameActionException {
		Direction d = rc.getLocation().directionTo(target);
		int offsetIndex = 0;
		int[] offsets = {0,1,-1,2,-2};
		int dirint = directionToInt(d);
		boolean blocked = false;
		while (offsetIndex < 5 && !rc.canMove(directions[(dirint+offsets[offsetIndex]+8)%8])) {
			offsetIndex++;
		}
		if (offsetIndex < 5) {
			Direction launchDir = directions[(dirint+offsets[offsetIndex]+8)%8];
			rc.launchMissile(launchDir);
			postMissileMission(rc.getLocation().add(launchDir),target);
		}
	}

	static void launcherMove(MapLocation target) throws GameActionException {
		RobotInfo[] attackableEnemies = rc.senseNearbyRobots(24, enemyTeam);
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(35, enemyTeam);

		if (attackableEnemies.length > 0) {
			MapLocation closestEnemy = null;
			int closestDistance = 999999;
			for (RobotInfo r : attackableEnemies) {
				MapLocation tryloc = r.location;
				int trydist = tryloc.distanceSquaredTo(rc.getLocation());
				if (trydist < closestDistance) {
					closestEnemy = tryloc;
					closestDistance = trydist;
				}
			}
			if (rc.isCoreReady()) {
				tryMove(closestEnemy.directionTo(rc.getLocation()));
				return;
			} else if (rc.getMissileCount() > 0) {
				//find good target location
				MapLocation closestLoc = null;
				int dist = 999999;
				for (MapLocation adj : MapLocation.getAllMapLocationsWithinRadiusSq(closestEnemy,2)) {
					int trydist = adj.distanceSquaredTo(rc.getLocation());
					if (trydist > 2 && trydist < dist) {
						closestLoc = adj;
						dist = trydist;
					}
				}

				tryLaunch(closestLoc);
				return;
			}
		} else if (nearbyEnemies.length > 0 && rc.getMissileCount() > 0) {
			tryLaunch(nearbyEnemies[0].location.add(nearbyEnemies[0].location.directionTo(rc.getLocation())));
			return;
		} else if (rc.isCoreReady()) {
			navigate(target);
		}
	}

	static void missileMove(MapLocation target) throws GameActionException {
		RobotInfo[] attackableEnemies = rc.senseNearbyRobots(2,enemyTeam);
		if (attackableEnemies.length > 1 || rc.getLocation().equals(target)) {
			rc.explode();
		}
		if (rc.isCoreReady()) {
			Direction toMove = rc.getLocation().directionTo(target);
			if (rc.canMove(toMove)) {
				rc.move(toMove);
				return;
			}
			Direction right = toMove.rotateRight();
			Direction left = toMove.rotateLeft();
			MapLocation rightLoc = rc.getLocation().add(right);
			MapLocation leftLoc = rc.getLocation().add(left);
			if (rightLoc.distanceSquaredTo(target) < leftLoc.distanceSquaredTo(target)) {
				rc.move(right);
			} else {
				rc.move(left);
			}
		}
	}

	static void bashMove(MapLocation target) throws GameActionException {
		RobotInfo[] attackableEnemies = rc.senseNearbyRobots(2,enemyTeam);
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(35, enemyTeam);
		boolean shouldAttack = false;

		if (attackableEnemies.length > 0) {
			Direction bestdir = null;
			int bestNumEnemies = attackableEnemies.length;
			for (int i=0; i<8; i++) {
				Direction trydir = directions[i];
				if (rc.canMove(trydir)) {
					int tryNumEnemies = rc.senseNearbyRobots(rc.getLocation().add(trydir),2,enemyTeam).length;
					if (tryNumEnemies > bestNumEnemies) {
						bestdir = trydir;
						bestNumEnemies = tryNumEnemies;
					}
				}
			}
			if (bestdir != null) {
				if (rc.isCoreReady()) {
					rc.move(bestdir);
				}
			}
			shouldAttack = bestNumEnemies > 0;
		} else if (rc.isCoreReady()) {
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
		// commented out by Alex since bashers always attack
		//if (shouldAttack && rc.isWeaponReady()) {
		//rc.bash();
		//}
	}

	//naive attackmove.
	static void attackMove(MapLocation target) throws GameActionException {
		RobotInfo[] attackableEnemies = rc.senseNearbyRobots(myRange,enemyTeam);
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(35, enemyTeam);
		if (attackableEnemies.length > 0) {
			if (rc.isWeaponReady()) {
				rc.attackLocation(attackableEnemies[0].location);
			}
		} else if (rc.isCoreReady()) {
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
			rc.attackLocation(enemies[0].location);
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
		//wtf
		while (offsetIndex < 8 && !rc.canSpawn(directions[(dirint+offsets[offsetIndex]+8)%8],type)) {
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

	static void postMissileMission(MapLocation missileloc, MapLocation target) throws GameActionException {
		int coordx = 200+target.x-alliedHQ.x;
		int coordy = 200+target.y-alliedHQ.y;
		int missilex = missileloc.x%10;
		int missiley = missileloc.y%10;
		int message = missilex*10000000+missiley*1000000+coordx*1000+coordy;
		int channelNo = rc.readBroadcast(MISSILE_CHANNEL);
		rc.broadcast(channelNo,message);
		rc.broadcast(MISSILE_CHANNEL,channelNo+1);
		//  abxxxyyy
		//a is the last digit of the x coordinate of the missile's location
		//b is the last digit of the y coordinate of the missile's location
	}
	
	/*
	static void confirmMissileMission(int mmp) throws GameActionException {
		rc.broadcast(mmp,-1);
		int missionchannel = rc.readBroadcast(OPEN_MISSILE_CHANNEL);
		while (rc.readBroadcast(missionchannel) < 0) {
			rc.broadcast(OPEN_MISSILE_CHANNEL,missionchannel+1);
			missionchannel = rc.readBroadcast(MISSILE_CHANNEL);
		}
	}*/

	static void postSupplyMission() throws GameActionException {
		postMission(buildMessage(2,rc.getID()));
	}

	static void postAttackMission(MapLocation loc) throws GameActionException {
		int coordx = 200+loc.x-alliedHQ.x;
		int coordy = 200+loc.y-alliedHQ.y;
		int message = coordx*1000+coordy;
		rc.broadcast(ATTACK_CHANNEL,message);
	}

	static void retrieveAttackMission() throws GameActionException {
		int location = rc.readBroadcast(ATTACK_CHANNEL);
		if (location != 0) {
			int locx = location/1000-200+alliedHQ.x;
			int locy = location%1000-200+alliedHQ.y;
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
			missionchannel = rc.readBroadcast(MISSION_CHANNEL);
		}
	}

	static void postMission(int missionmessage) throws GameActionException {
		int openchannel = rc.readBroadcast(OPEN_CHANNEL);
		rc.broadcast(openchannel, missionmessage);
		rc.broadcast(OPEN_CHANNEL,openchannel+1);
	}

	static int buildMessage(int action, int info) {
		return action*1000000+info;
	}

	static int actionFromMessage(int m) {
		return m/1000000;
	}

	static int infoFromMessage(int m) {
		return m%1000000;
	}

	static MapLocation locationFromMessage(int m) {
		int locx = (m%1000000)/1000-200+alliedHQ.x;
		int locy = m%1000-200+alliedHQ.y;
		return new MapLocation(locx,locy);
	}
}
