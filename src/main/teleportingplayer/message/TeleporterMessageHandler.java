package teleportingplayer.message;

import teleportingplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class TeleporterMessageHandler implements MessageHandler {

	BasePlayer player;

	public MapLocation [] locs;

	public TeleporterMessageHandler(BasePlayer bp) {
		player = bp;
		bp.handlers[MessageSender.messageTypeTeleporters]=this;
	}

	public void receivedMessage(Message m) {
		locs = m.locations;
		//System.out.println("got message");
	}

}