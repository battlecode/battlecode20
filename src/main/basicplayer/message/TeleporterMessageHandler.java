package basicplayer.message;

import basicplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class TeleporterMessageHandler implements MessageHandler {

	static public final int staleTime = 100;

	BasePlayer player;

	public MapLocation [] locs;
	int timeout;

	public TeleporterMessageHandler(BasePlayer bp) {
		player = bp;
		bp.handlers[MessageSender.messageTypeTeleporters]=this;
	}

	public void receivedMessage(Message m) {
		if(Clock.getRoundNum()>=timeout||
		   player.myLoc.distanceSquaredTo(m.locations[0])<=player.myLoc.distanceSquaredTo(locs[0])) {
			locs = m.locations;
			timeout = Clock.getRoundNum()+staleTime;
		}
	}

}