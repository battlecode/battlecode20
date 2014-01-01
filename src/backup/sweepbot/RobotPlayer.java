package sweepbot;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	static Random rand;
	static Team myTeam;
	static Team enemyTeam;
	static MapLocation enemyHQ;
	static MapLocation targetLocation;
	static int targetAction;
	static int NChannelsForMessaging = 65536;
	static int rows;
	static int cols;
	static double[][] cows;
	static double avgcows;
	static boolean[][] deadEnd;
	static MapLocation[] pastrLocations;
	static Direction lastDirection;
	static int pastrID;
	
	static ArrayList<Integer> IDs;
	static int numIDs;
	
	public static void run(RobotController rc) {
		enemyHQ = rc.senseEnemyHQLocation();
		rand = new Random();
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		enemyHQ = rc.senseEnemyHQLocation();
		targetAction = 0;
		targetLocation = enemyHQ;
		cows = rc.senseCowGrowth();
		rows = cows.length;
		cols = cows[0].length;
		
		if (rc.getType() == RobotType.HQ) {
			try {
				//trySpawn(rc,rc.getLocation().directionTo(enemyHQ));
				//rc.broadcast(channelSpawn(),convertToMessage(2,rc.getLocation().add(Direction.EAST)));
				MapLocation pastrLocation = new MapLocation((3*rc.getLocation().x+enemyHQ.x)/4,(3*rc.getLocation().y+enemyHQ.y)/4);
				trySpawn(rc,rc.getLocation().directionTo(pastrLocation));
				rc.broadcast(channelSpawn(),convertToMessage(2,pastrLocation));
				
				while (true) {
					try {
						
						
						if (rc.isActive()) {
							Robot[] enemies15 = rc.senseNearbyGameObjects(Robot.class, 15, enemyTeam);
							if (enemies15.length != 0) {
								rc.attackSquare(rc.senseRobotInfo(enemies15[0]).location);
							} else {
								trySpawn(rc,rc.getLocation().directionTo(pastrLocation));
								rc.broadcast(channelSpawn(),convertToMessage(3,pastrLocation));
							}
						}
						rc.yield();
					} catch (Exception e) {System.out.println("hq exception occurred: " + e.getMessage()); rc.yield();}
				}
			} catch (Exception e) {System.out.println("hq initializing exception occurred: " + e.getMessage()); rc.yield();}	
		}
		
		if (rc.getType() == RobotType.SOLDIER) {
			
			
			try {
				deadEnd = new boolean[cows.length][cows[0].length];
				int m = rc.readBroadcast(channelSpawn());
				targetAction = actionFromMessage(m);
				targetLocation = locationFromMessage(m);
				rc.setIndicatorString(1,Integer.toString(targetAction));
				rc.setIndicatorString(2,targetLocation.toString());
				
				
				while (true) {
					try {
						
						if (rc.isActive()) {
							pastrLocations = rc.sensePastrLocations(myTeam);
						}
						
						int message = rc.readBroadcast(channelInbound(rc.getRobot().getID()));
						if (message != 0) {
							rc.broadcast(channelInbound(rc.getRobot().getID()),0);
						}
						
						//idle
						if (targetAction == 0) {
							m = rc.readBroadcast(channelSpawn());
							targetAction = actionFromMessage(m);
							targetLocation = locationFromMessage(m);
							rc.setIndicatorString(1,Integer.toString(targetAction));
							rc.setIndicatorString(2,targetLocation.toString());
						//attack move
						} else if (targetAction == 1) {
							if (rc.isActive()) {
								attackMove(rc, targetLocation);
							}
						//construct PASTR
						} else if (targetAction == 2) {
							if (rc.isActive()) {
								if (rc.getLocation().equals(targetLocation)) {
									rc.construct(RobotType.PASTR);
								}
								if (rc.getLocation().distanceSquaredTo(targetLocation) < 15) {
									attackMove(rc,targetLocation, true);
								} else {
									attackMove(rc,targetLocation);
								}
							}
						} else if (targetAction == 3) {
							if (pastrID == 0) {
								if (rc.isActive()) {
									attackMove(rc,targetLocation);
								}
								if (rc.canSenseSquare(targetLocation)) {
									Robot obj = (Robot)rc.senseObjectAtLocation(targetLocation);
									if (obj == null) {
										targetAction = 0;
									} else {
										RobotInfo r = rc.senseRobotInfo(obj);
										if (r.type == RobotType.PASTR && r.team == myTeam) {										
											pastrID = obj.getID();
											rc.broadcast(channelInbound(pastrID),convertToMessage(33,rc.getRobot().getID()));
										}
									}
								}
							} else {
								Robot[] enemies = rc.senseNearbyGameObjects(Robot.class,36,enemyTeam);
								if (enemies.length != 0) {
									MapLocation enemyLocation = rc.senseRobotInfo(enemies[0]).location;
									rc.broadcast(channelOutbound(rc.getRobot().getID()),convertToMessage(33,enemyLocation));
								}
		
								if (rc.isActive()) {
									if (actionFromMessage(message) == 31) {
										attackMove(rc,locationFromMessage(message));
									} else {
										patrol(rc,targetLocation);
									}
								}
							}
							/*
							if (rc.isActive()) {
								MapLocation closestPastr = null;
								int distanceSquared = 9999999;
								for (int i=0; i<pastrLocations.length; i++) {
									if (pastrLocations[i].distanceSquaredTo(rc.getLocation()) < distanceSquared) {
										distanceSquared = pastrLocations[i].distanceSquaredTo(rc.getLocation());
									}
								}
								
								Robot[] enemies36 = rc.senseNearbyGameObjects(Robot.class, 36, enemyTeam);
								if (enemies36.length > 0) {
									attackMove(rc,rc.senseRobotInfo(enemies36[0]).location);
								} else if (rc.getLocation().distanceSquaredTo(targetLocation) <= 20) {
									navigateAway(rc, targetLocation);
								} else {
									navigate(rc,targetLocation);
								}
							}
							*/
						}
						
						rc.yield();
						//attackMove(rc, rc.getLocation().directionTo(enemyHQ));
					} catch (Exception e) {System.out.println("soldier exception occurred: " + e.getMessage()); rc.yield();}
				}
			} catch (Exception e) {System.out.println("soldier initializing exception occurred: " + e.getMessage()); rc.yield();}	
		}
		
		if (rc.getType() == RobotType.PASTR) {
			try {
				IDs = new ArrayList<Integer>(500);
				
				while (true) {
					try {
						int m = rc.readBroadcast(channelInbound(rc.getRobot().getID()));
						if (m != 0 && actionFromMessage(m) == 33) {
							IDs.add(idFromMessage(m));
							System.out.println("added " + idFromMessage(m));
							//
							rc.broadcast(channelInbound(rc.getRobot().getID()),0);
						}
						
						MapLocation[] enemyLocs = new MapLocation[30];
						int numEnemies=0;
						for (int i=0; i<IDs.size(); i++) {
							m = rc.readBroadcast(channelOutbound(IDs.get(i)));
							if (actionFromMessage(m) == 33) {
								enemyLocs[numEnemies++] = locationFromMessage(m);
								rc.broadcast(channelOutbound(IDs.get(i)),0);
							}
						}
						if (numEnemies > 0) {
							MapLocation closestEnemy = enemyLocs[0];
							int closestDist = enemyLocs[0].distanceSquaredTo(rc.getLocation());
							for (int i=1; i<numEnemies; i++) {
								int temp = enemyLocs[i].distanceSquaredTo(rc.getLocation());
								if (temp < closestDist) {
									closestEnemy = enemyLocs[0];
									closestDist = temp;
								}
							}
							for (int i=0; i<IDs.size(); i++) {
								rc.broadcast(channelInbound(IDs.get(i)),convertToMessage(31,closestEnemy));
							}
						}
						
						rc.yield();
					} catch (Exception e) {System.out.println("PASTR exception occurred: " + e.getMessage()); rc.yield();}
				}
			} catch (Exception e) {System.out.println("PASTR initializing exception occurred: " + e.getMessage()); rc.yield();}
		}
		
		while(true) {
			rc.yield();
		}
	}
	
	public static void patrol (RobotController rc, MapLocation target) throws GameActionException {
		
		Robot[] enemies10 = rc.senseNearbyGameObjects(Robot.class,10,enemyTeam);
		if (enemies10.length != 0) {
			rc.attackSquare(rc.senseRobotInfo(enemies10[0]).location);
			return;
		}
		Robot[] enemies35 = rc.senseNearbyGameObjects(Robot.class,35,enemyTeam);
		if (enemies35.length != 0) {
			navigate(rc,rc.senseRobotInfo(enemies35[0]).location);
		} else {
			//no enemies around.
			//too far away
			if (rc.getLocation().distanceSquaredTo(target) > 60) {
				navigate(rc, target);
				return;
			}
			Direction[] moves = new Direction[5];
			Direction[] validMoves = new Direction[5];
			int[] turn = {6,7,0,1,2};
			int numValidMoves = 0;
			for (int i=0; i<5; i++) {
				moves[i] = toDirection[toInteger(lastDirection)+turn[i]];
				if (rc.canMove(moves[i])) {
					validMoves[numValidMoves++] = moves[i];
				}
			}
			if (numValidMoves == 0) {
				return;
			}
			int closestMoveIndex = 0;
			int closestDistance = 999999;
			for (int i=0; i<numValidMoves; i++) {
				MapLocation destination = rc.getLocation().add(validMoves[i]);
				int temp = Math.abs(27-destination.distanceSquaredTo(target));
				if (temp < closestDistance) {
					closestMoveIndex = i;
					closestDistance = temp;
				}
			}
			MapLocation destination = rc.getLocation().add(validMoves[closestMoveIndex]);
			boolean inPastrRange = false;
			for (int i=0; i<pastrLocations.length; i++) {
				if (destination.distanceSquaredTo(pastrLocations[i]) <= 13) {
					inPastrRange = true;
					i = pastrLocations.length;
				}
			}
			
			if (inPastrRange) {
				rc.sneak(validMoves[closestMoveIndex]);
			} else {
				rc.move(validMoves[closestMoveIndex]);
			}
			lastDirection = validMoves[closestMoveIndex];
		}
	}
	
	public static void attackMove (RobotController rc, MapLocation target) throws GameActionException {
		Robot[] enemies10 = rc.senseNearbyGameObjects(Robot.class,10,enemyTeam);
		if (enemies10.length != 0) {
			rc.attackSquare(rc.senseRobotInfo(enemies10[0]).location);
			return;
		} else {
			Robot[] enemies35 = rc.senseNearbyGameObjects(Robot.class,35,enemyTeam);
			if (enemies35.length != 0) {
				navigate(rc,rc.senseRobotInfo(enemies35[0]).location);
			} else {
				navigate(rc, target);
			}
		}
	}
	
	
	public static void navigate (RobotController rc, MapLocation target) throws GameActionException {
		if (rc.getLocation().equals(target)) {
			return;
		}
		Direction[] validMoves = new Direction[8];
		int numValidMoves=0;
		for (int i=0; i<8; i++) {
			if (rc.canMove(toDirection[i])) {
				validMoves[numValidMoves++] = toDirection[i];
			}
		}
		if (numValidMoves==0) {
			return;
		}
		int closestMoveIndex = 0;
		int closestDistance = 999999;
		for (int i=0; i<numValidMoves; i++) {
			MapLocation destination = rc.getLocation().add(validMoves[i]);
			int temp = destination.distanceSquaredTo(target);
			if (temp < closestDistance && !deadEnd[destination.x][destination.y]) {
				closestMoveIndex = i;
				closestDistance = temp;
			}
		}
		if (closestDistance >= rc.getLocation().distanceSquaredTo(target)) {
			deadEnd[rc.getLocation().x][rc.getLocation().y] = true;
			System.out.println("DEAD END "+ rc.getLocation().toString());
		}
		MapLocation destination = rc.getLocation().add(validMoves[closestMoveIndex]);
		boolean inPastrRange = false;
		for (int i=0; i<pastrLocations.length; i++) {
			
			if (destination.distanceSquaredTo(pastrLocations[i]) <= 13) {
				inPastrRange = true;
				i = pastrLocations.length;
			}
		}
		if (inPastrRange) {
			rc.sneak(validMoves[closestMoveIndex]);
		} else {
			rc.move(validMoves[closestMoveIndex]);
		}
		lastDirection = validMoves[closestMoveIndex];
	}
	
	public static void navigateAway (RobotController rc, MapLocation target) throws GameActionException {
		Direction[] validMoves = new Direction[8];
		int numValidMoves=0;
		for (int i=0; i<8; i++) {
			if (rc.canMove(toDirection[i])) {
				validMoves[numValidMoves++] = toDirection[i];
			}
		}
		if (numValidMoves==0) {
			return;
		}
		int farthestMoveIndex = 0;
		int farthestDistance = -1;
		for (int i=0; i<numValidMoves; i++) {
			MapLocation destination = rc.getLocation().add(validMoves[i]);
			int temp = destination.distanceSquaredTo(target);
			if (temp > farthestDistance) {
				farthestMoveIndex = i;
				farthestDistance = temp;
			}
		}
		MapLocation destination = rc.getLocation().add(validMoves[farthestMoveIndex]);
		boolean inPastrRange = false;
		for (int i=0; i<pastrLocations.length; i++) {
			if (destination.distanceSquaredTo(pastrLocations[i]) <= 13) {
				inPastrRange = true;
				i = pastrLocations.length;
			}
		}
		
		if (inPastrRange) {
			rc.sneak(validMoves[farthestMoveIndex]);
		} else {
			rc.move(validMoves[farthestMoveIndex]);
		}
		lastDirection = validMoves[farthestMoveIndex];
	}
	
	public static void attackMove (RobotController rc, MapLocation target, boolean sneak) throws GameActionException {
		Robot[] enemies10 = rc.senseNearbyGameObjects(Robot.class,10,enemyTeam);
		if (enemies10.length != 0) {
			rc.attackSquare(rc.senseRobotInfo(enemies10[0]).location);
			return;
		} else {
			navigate(rc, target, sneak);
		}
	}
	
	
	public static void navigate (RobotController rc, MapLocation target, boolean sneak) throws GameActionException {
		if (rc.getLocation().equals(target)) {
			return;
		}
		Direction[] validMoves = new Direction[8];
		int numValidMoves=0;
		for (int i=0; i<8; i++) {
			if (rc.canMove(toDirection[i])) {
				validMoves[numValidMoves++] = toDirection[i];
			}
		}
		if (numValidMoves==0) {
			return;
		}
		int closestMoveIndex = 0;
		int closestDistance = 999999;
		for (int i=0; i<numValidMoves; i++) {
			MapLocation destination = rc.getLocation().add(validMoves[i]);
			int temp = destination.distanceSquaredTo(target);
			if (temp < closestDistance && !deadEnd[destination.x][destination.y]) {
				closestMoveIndex = i;
				closestDistance = temp;
			}
		}
		if (closestDistance >= rc.getLocation().distanceSquaredTo(target)) {
			deadEnd[rc.getLocation().x][rc.getLocation().y] = true;
			System.out.println("DEAD END "+ rc.getLocation().toString());
		}
		if (sneak) {
			rc.sneak(validMoves[closestMoveIndex]);
		} else {
			rc.move(validMoves[closestMoveIndex]);
		}
		lastDirection = validMoves[closestMoveIndex];
	}
	
	public static boolean trySpawn (RobotController rc, Direction d) throws GameActionException {
		int a = rand.nextInt(186);
		int[] moveOffsets;
		if (a % 2 == 0) {
			moveOffsets = new int[] {0, 1, 7, 2, 6, 3, 5, 4};
		} else {
			moveOffsets = new int[] {0, 7, 1, 6, 2, 5, 3, 4};
		}
	//for (int i = 0; i < 8; i++) {
		Direction turn = d;
		if (rc.canMove(turn)) {
			rc.spawn(turn);
			return true;
		}
		turn = toDirection[toInteger(d) + moveOffsets[1]];
		if (rc.canMove(turn)) {
			rc.spawn(turn);
			return true;
		}
		turn = toDirection[toInteger(d) + moveOffsets[2]];
		if (rc.canMove(turn)) {
			rc.spawn(turn);
			return true;
		}
		turn = toDirection[toInteger(d) + moveOffsets[3]];
		if (rc.canMove(turn)) {
			rc.spawn(turn);
			return true;
		}
		turn = toDirection[toInteger(d) + moveOffsets[4]];
		if (rc.canMove(turn)) {
			rc.spawn(turn);
			return true;
		}
		turn = toDirection[toInteger(d) + moveOffsets[5]];
		if (rc.canMove(turn)) {
			rc.spawn(turn);
			return true;
		}
		turn = toDirection[toInteger(d) + moveOffsets[6]];
		if (rc.canMove(turn)) {
			rc.spawn(turn);
			return true;
		}
		turn = toDirection[toInteger(d) + moveOffsets[7]];
		if (rc.canMove(turn)) {
			rc.spawn(turn);
			return true;
		}
		
		return false; // didn't spawn anything
	}
	
	public static int convertToMessage(int action, MapLocation loc) {
		return action*100000+loc.x*1000+loc.y;
	}
	
	public static int convertToMessage(int action, int id) {
		return action*100000+id;
	}
	
	/* #ACTIONS:
	HQ to robot
	0: idle
	1: attack
	2: build pastr
	3: patrol
	
	Robot to pastr
	30: add my id
	33: enemy location
	
	PASTR to robot:
	31: enemy location
	*/
	public static int actionFromMessage(int m) {
		return (m/100000);
	}
	
	public static int idFromMessage(int m) {
		return (m%100000);
	}
	
	public static MapLocation locationFromMessage(int m) {
		int x = (m%100000)/1000;
		int y = m%1000;
		MapLocation out = new MapLocation(x,y);
		return out;
	}
	
	public static int channelSpawn() {
		return NChannelsForMessaging-1;
	}
	
	//messages being broadcast from the robot
	public static int channelOutbound (Robot r) {
		return ((r.getID()*2) % (NChannelsForMessaging-1));
	}
	
	public static int channelOutbound (int r) {
		return ((r*2) % (NChannelsForMessaging-1));
	}
	
	//messages going in to the robot
	public static int channelInbound (Robot r) {
		return ((r.getID()*2+1) % (NChannelsForMessaging-1));
	}
	
	public static int channelInbound (int r) {
		return ((r*2+1) % (NChannelsForMessaging-1));
	}
	
	/*
	public static int channelOutboundByHQ (Robot r) {
		return ((r.getID()*4) % (NChannelsForMessaging-1));
	}
	
	public static int channelInboundByHQ (Robot r) {
		return ((r.getID()*4+1) % (NChannelsForMessaging-1));
	}
	
	public static int channelOutboundByRobot (Robot r) {
		return ((r.getID()*4+2) % (NChannelsForMessaging-1));
	}
	
	public static int channelInboundByRobot (Robot r) {
		return ((r.getID()*4+3) % (NChannelsForMessaging-1));
	}
	*/
	
	public static int toInteger (Direction d) {
		switch (d) {
			case NORTH: return 0;
			case NORTH_EAST: return 1;
			case EAST: return 2;
			case SOUTH_EAST: return 3;
			case SOUTH: return 4;
			case SOUTH_WEST: return 5;
			case WEST: return 6;
			case NORTH_WEST: return 7;
			default: return -1;
		}
	}
	
	public static Direction toDirection (int i) {
		switch (i%8) {
			case 0: return Direction.NORTH;
			case 1: return Direction.NORTH_EAST;
			case 2: return Direction.EAST;
			case 3: return Direction.SOUTH_EAST;
			case 4: return Direction.SOUTH;
			case 5: return Direction.SOUTH_WEST;
			case 6: return Direction.WEST;
			case 7: return Direction.NORTH_WEST;
			default: return Direction.NONE;
		}
	}
	
	static Direction[] toDirection = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

	static class naivePastrLocation {
		MapLocation loc;
		
		public naivePastrLocation(MapLocation l) {
			loc = l;
		}
		
		public ArrayList<naivePastrLocation> neighbors() {
			ArrayList<naivePastrLocation> output = new ArrayList<naivePastrLocation>();
			
			if (loc.x+5 < cols-1) {
				output.add(new naivePastrLocation(new MapLocation(loc.x+5,loc.y)));
			}
			if (loc.x-5 >=1 ) {
				output.add(new naivePastrLocation(new MapLocation(loc.x-5,loc.y)));
			}
			if (loc.y+5 < rows-1) {
				output.add(new naivePastrLocation(new MapLocation(loc.x,loc.y+5)));
			}
			if (loc.y-5 >= 1) {
				output.add(new naivePastrLocation(new MapLocation(loc.x,loc.y-5)));
			}
			return output;
		}
		
		public double avgCows() {
			return (cows[loc.x][loc.y]+cows[loc.x][loc.y-1]+cows[loc.x][loc.y+1]+cows[loc.x-1][loc.y-1]+cows[loc.x-1][loc.y]+cows[loc.x-1][loc.y+1]+cows[loc.x+1][loc.y-1]+cows[loc.x+1][loc.y]+cows[loc.x+1][loc.y+1])/9.0;
		}
	}
	
}