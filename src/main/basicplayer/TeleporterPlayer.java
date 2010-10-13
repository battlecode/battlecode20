package basicplayer;

import basicplayer.message.MessageHandler;
import basicplayer.message.MessageSender;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import battlecode.common.Message;

import static battlecode.common.GameConstants.TELEPORT_FLUX_COST;

import java.util.ArrayDeque;

public class TeleporterPlayer extends BasePlayer implements MessageHandler {

	ArrayDeque<Message> teleporterMessages = new ArrayDeque<Message>();

	static public final int [] dx = {3,0,-3,0,2,2,-2,-2,1,2,2,1,-1,-2,-2,-1,2,0,-2,0,1,1,-1,-1,1,0,-1,0,0};
	static public final int [] dy = {0,-3,0,3,2,-2,-2,2,2,1,-1,-2,-2,-1,1,2,0,-2,0,2,1,-1,-1,1,0,-1,0,1,0};
	static public final int sensor_squares_minus_1 = dx.length-1;
	
	static public final int SENSE_EVERY = 10;

	public TeleporterPlayer(RobotController RC) {
		super(RC);
		myLoc = myRC.getLocation();
	}

	public void run() {
		handlers[MessageSender.messageTypeTeleport]=this;
		while(true) {
			try {
				if(myRC.getFlux()>=TELEPORT_FLUX_COST) {
					sortMessages();
					teleport:
					while(!teleporterMessages.isEmpty()) {
						Message m = teleporterMessages.remove();
						MapLocation robotLoc = m.locations[0];
						if(!myRC.canSenseSquare(robotLoc)) continue;
						RobotLevel level = RobotLevel.values()[m.ints[1]];
						Robot robotToTeleport=(level==RobotLevel.ON_GROUND)?myRC.senseGroundRobotAtLocation(robotLoc):myRC.senseAirRobotAtLocation(robotLoc);
						if(myRC.senseRobotInfo(robotToTeleport).teleporting)
							continue;
						MapLocation teleporterLoc = m.locations[1];
						MapLocation teleportLoc;
						int x = teleporterLoc.getX();
						int y = teleporterLoc.getY();
						int i;
						for(i=sensor_squares_minus_1;i>=0;i--) {
							teleportLoc = new MapLocation(x+dx[i],y+dy[i]);
							if(myRC.canTeleport(teleporterLoc,teleportLoc,level)) {
								myRC.teleport(robotToTeleport,teleporterLoc,teleportLoc);
								BasePlayer.debug_println("Teleporting "+myLoc+" "+robotToTeleport+" "+teleporterLoc+" "+teleportLoc);
								break teleport;
							}
						}
					}
				}
				else {
					myRC.getAllMessages();
					teleporterMessages.clear();
				}
				if(Clock.getRoundNum()%SENSE_EVERY==0) {
					MapLocation [] locs = myRC.senseAlliedTeleporters();
					int i;
					// All messages must have this robot's location
					// in the first spot.  The message sender will put
					// it there, but we need to move the location that's
					// currently in the first spot to somewhere else.
					for(i=locs.length-1;i>0;i--) {
						if(locs[i].equals(myLoc)) {
							locs[i]=locs[0];
							break;
						}
					}
					mySender.sendTeleporters(locs);
				}
			} catch(Exception e) {
				BasePlayer.debug_stackTrace(e);
			}
			myRC.yield();
		}
	}

	public void receivedMessage(Message m) {
		teleporterMessages.add(m);
	}
}