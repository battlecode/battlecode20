package basicplayer.message;

import java.util.Arrays;

import basicplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

public class MessageSender {

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
	static public final int messageTypeStaircase=23;
	static public final int messageTypeFindEnemy=115;
	static public final int messageTypeHighGround=21;
	static public final int messageTypeAlternateHighGround=74;
	static public final int messageTypeIShotThat=36;
	static public final int messageTypeIFoundABlock=39;
	static public final int messageTypeGoInThisDirection=86;
	static public final int messageTypeGoTheWrongWay=58;
	static public final int messageTypeTeleport=91;
	static public final int messageTypeTeleporters=9;

	static public final int numTypes=117;
	
	static public final int broadcastRange=64;
	
	static public final int ID_MODULUS = 1024;

	boolean [][] seen = new boolean [STALE_TIME][];
	
	int checksumExtra;
	int myIDEncoded;
	int idFactor;
	int roundNum;

	BasePlayer player;
	RobotController myRC;

	public MessageSender(BasePlayer bp) {
		player=bp;
		myRC=bp.myRC;
		if(myRC.getTeam()==Team.A) {
			idFactor = 102181;
		}
		else {
			idFactor = 102253;
		}
		myIDEncoded = (myRC.getRobot().getID()%ID_MODULUS)*idFactor;
	}

	public void updateRoundNum() {
		int newRoundNum = Clock.getRoundNum();
		while(roundNum<newRoundNum) {
			roundNum++;
			seen[roundNum%STALE_TIME]=new boolean [ID_MODULUS];
		}
	}

	public boolean isValid(Message m) {
		// sortMessages checks that ints has
		// length at least three
		if(m.locations==null||m.locations.length==0||m.locations[0]==null)
			return false;
		int sentTime = m.ints[m.ints.length-2];
		if(roundNum-sentTime>=STALE_TIME) return false;
		int senderIDEncoded = m.ints[m.ints.length-1];
		if((senderIDEncoded%idFactor)!=sentTime)
			return false;
		if(seen[sentTime%STALE_TIME][senderIDEncoded/idFactor]||player.myLoc.distanceSquaredTo(m.locations[0])>broadcastRange)
			return false;
		seen[sentTime%STALE_TIME][senderIDEncoded/idFactor]=true;
		return true;
	}

	void send(Message m) {
		int [] ints = m.ints;
		ints[ints.length-2]=roundNum;
		ints[ints.length-1]=myIDEncoded+roundNum;
		m.locations[0]=player.myLoc;
		try {
			myRC.broadcast(m);
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

	public void sendEnemyUnits(int [] ints, MapLocation[] locs) {
		Message m = new Message();
		ints[0]=messageTypeEnemyUnits;
		m.ints=ints;
		m.locations=locs;
		send(m);
	}

	public void sendIShotThat(int id, MapLocation loc) {
		Message m = new Message ();
		m.ints = new int [4];
		m.ints[0] = messageTypeIShotThat;
		m.ints[1] = id;
		m.locations = new MapLocation [2];
		m.locations[1] = loc;
		send(m);
	}

	public void sendGoInThisDirection(int dx, int dy) {
		Message m = new Message();
		m.ints = new int [5];
		m.ints[0] = messageTypeGoInThisDirection;
		m.ints[1] = dx;
		m.ints[2] = dy;
		m.locations = new MapLocation [1];
		send(m);
	}

	// tells robot to get near the teleporter if
	// toTeleporter is null, and to teleport if
	// toTeleporter is not null
	public void sendTeleport(MapLocation toTeleporter) {
		Message m = new Message();
		m.ints = new int[4];
		m.ints[0] = messageTypeTeleport;
		m.ints[1] = myRC.getRobotType().isAirborne()?2:1;
		m.locations = new MapLocation [2];
		m.locations[1] = toTeleporter;
		send(m);
	}

	public void sendTeleporters(MapLocation [] teleporters) {
		Message m = new Message();
		m.ints = new int[3];
		m.ints[0] = messageTypeTeleporters;
		m.locations = teleporters;
		send(m);
	}

}
