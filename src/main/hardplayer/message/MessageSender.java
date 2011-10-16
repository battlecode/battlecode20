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
	static public final int messageTypeSuicide=78;
	static public final int messageTypeEnemyUnits=116;
	static public final int messageTypeScoutSawEnemy=72;
	static public final int messageTypeScoutSawEnemyRebroadcast=92;
	static public final int messageTypeEvolve=28;
	static public final int messageTypeBanish=32;
	static public final int messageTypeFindEnemy=115;
	static public final int messageTypeIShotThat=36;
	static public final int messageTypeGoInThisDirection=86;
	static public final int messageTypeGoTheWrongWay=58;
	static public final int messageTypeTeleport=91;
	static public final int messageTypeTeleporters=9;
	static public final int messageTypeStopCamping=43;

	static public final int numTypes=117;
	
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
			myRC.broadcast(m);
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
