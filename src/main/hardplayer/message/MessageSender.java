package hardplayer.message;

import java.util.Arrays;

import hardplayer.Static;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class MessageSender extends Static {

	// STALE_TIME is for determining if a message has been rebroadcasted
	// OLD_MESSAGE is the length of time a message is considered useful
	public static final int STALE_TIME=2;
	public static final int OLD_MESSAGE = 5;

	// Use random message numbers to decrease the chance of
	// having to checksum an enemy message.
	// Make sure these are all different and less than numTypes!
	static public final int MSG_ENEMY=112;
	static public final int MSG_EXPLORE=53;
	static public final int MSG_ENEMY_2=57;
	static public final int MSG_ENEMY_UNITS=12;
	static public final int MSG_GO_THIS_WAY=86;

	static public final int numTypes=113;
	
	static public final int broadcastRange=64;
	
	static public final int ID_MODULUS = 1024;

	static boolean [][] seen = new boolean [STALE_TIME][];
	
	static int checksumExtra;
	static int myIDEncoded;
	static int idFactor;
	static int roundNum;

	public MessageSender() {
		if(myRC.getTeam()==Team.A) {
			idFactor = 102181;
		}
		else {
			idFactor = 102253;
		}
		myIDEncoded = (myRC.getRobot().getID()%ID_MODULUS)*idFactor;
	}

	public static void updateRoundNum() {
		int newRoundNum = Clock.getRoundNum();
		while(roundNum<newRoundNum) {
			roundNum++;
			seen[roundNum%STALE_TIME]=new boolean [ID_MODULUS];
		}
	}

	public static boolean isValid(Message m) {
		// sortMessages checks that ints has
		// length at least three
		if(m.locations==null||m.locations.length==0||m.locations[0]==null)
			return false;
		int sentTime = m.ints[m.ints.length-2];
		if(roundNum-sentTime>=STALE_TIME) return false;
		int senderIDEncoded = m.ints[m.ints.length-1];
		if((senderIDEncoded%idFactor)!=sentTime)
			return false;
		if(seen[sentTime%STALE_TIME][senderIDEncoded/idFactor]||myLoc.distanceSquaredTo(m.locations[0])>broadcastRange)
			return false;
		seen[sentTime%STALE_TIME][senderIDEncoded/idFactor]=true;
		return true;
	}

	static void send(Message m) {
		int [] ints = m.ints;
		ints[ints.length-2]=roundNum;
		ints[ints.length-1]=myIDEncoded+roundNum;
		m.locations[0]=myLoc;
		try {
			if(myRC.getFlux()>=m.getFluxCost())
				myRC.broadcast(m);
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public static void sendEnemy(MapLocation loc) {
		Message m = new Message();
		m.ints = new int [3];
		m.ints[0] = MSG_ENEMY;
		m.locations = new MapLocation [2];
		m.locations[1] = loc;
		send(m);
	}

	public static void sendFindEnemy(MapLocation loc, int enemies) {
		Message m = new Message();
		m.ints = new int [4];
		m.ints[0] = MSG_ENEMY_2;
		m.ints[1] = enemies;
		m.locations = new MapLocation [2];
		m.locations[1] = loc;
		send(m);
	}

	public static void sendEnemyUnits(MapLocation [] locs) {
		Message m = new Message();
		m.ints = new int [3];
		m.ints[0] = MSG_ENEMY_UNITS;
		m.locations = locs;
		send(m);
	}
	
	public static void sendExplore(MapLocation loc) {
		Message m = new Message();
		m.ints = new int [3];
		m.ints[0] = MSG_EXPLORE;
		m.locations = new MapLocation [2];
		m.locations[1] = loc;
		send(m);
	}

	public static void sendGoThisWay(int dx, int dy) {
		Message m = new Message();
		m.ints = new int [5];
		m.locations = new MapLocation [1];
		m.ints[0] = MSG_GO_THIS_WAY;
		m.ints[1] = dx;
		m.ints[2] = dy;
		send(m);
	}

}
