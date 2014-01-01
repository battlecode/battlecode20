package navbot;

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
	static int NChannelsForMessaging = 65536;
	static int rows;
	static int cols;
	static double[][] cows;
	static double avgcows;
	static boolean[][] deadEnd;
	
	public static void run(RobotController rc) {
		MapLocation enemyHQ = rc.senseEnemyHQLocation();
		rand = new Random();
		myTeam = rc.getTeam();
		enemyTeam = myTeam.opponent();
		int targetAction = 0;
		MapLocation targetLocation = enemyHQ;
		cows = rc.senseCowGrowth();
		rows = cows.length;
		cols = cows[0].length;
		
		if (rc.getType() == RobotType.HQ) {
			try {
				trySpawn(rc,rc.getLocation().directionTo(enemyHQ));
				rc.broadcast(channelSpawn(),convertToMessage(1,enemyHQ));
				
				while (true) {
					rc.yield();
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
						if (targetAction == 1) {
							if (rc.isActive()) {
								attackMove(rc, targetLocation, false);
							}
						} else if (targetAction == 2) {
							if (rc.isActive()) {
								if (rc.getLocation().equals(targetLocation)) {
									rc.construct(RobotType.PASTR);
								}
								if (rc.getLocation().distanceSquaredTo(targetLocation) < 15) {
									attackMove(rc,targetLocation, true);
								} else {
									attackMove(rc,targetLocation,false);
								}
							}
						}
						
						rc.yield();
						//attackMove(rc, rc.getLocation().directionTo(enemyHQ));
					} catch (Exception e) {System.out.println("soldier exception occurred: " + e.getMessage()); rc.yield();}
				}
			} catch (Exception e) {System.out.println("soldier initializing exception occurred: " + e.getMessage()); rc.yield();}	
		}
		
		while(true) {
			rc.yield();
		}
	}
	
	
	public static void attackMove (RobotController rc, MapLocation target, boolean sneak) throws GameActionException {
		Robot[] enemies16 = rc.senseNearbyGameObjects(Robot.class,16,enemyTeam);
		if (enemies16.length != 0) {
			rc.attackSquare(rc.senseRobotInfo(enemies16[0]).location);
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
	
	public static int actionFromMessage(int m) {
		return (m/100000);
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