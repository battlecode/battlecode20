package refplayer.goal;

import refplayer.Static;
import refplayer.message.MessageHandler;
import refplayer.message.MessageSender;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class FindNodeGoal extends Static implements Goal, MessageHandler {

	static MapLocation archonLoc;
	static MapLocation requestLoc;
	static int requestTime;
	static int archonDist;
	static int requestDist;

	public FindNodeGoal() {
		handlers[MessageSender.MSG_EXPLORE]=this;
	}

	public int maxPriority() { return FIND_NODE; }

	public int priority() {
		if(Clock.getRoundNum()-requestTime<=10) {
			for(MapLocation l : myRC.senseAlliedArchons())
				if(l.distanceSquaredTo(myLoc)<=16)
					return FIND_NODE;
			return 0;
		}
		else
			return 0;
	}

	public void execute() {
		Direction d = archonLoc.directionTo(requestLoc);
		myNav.moveToForward(myLoc.add(d,4));
	}

	public void receivedMessage(Message m) {
		//System.out.println("got message");
		int d = myLoc.distanceSquaredTo(m.locations[0]);
		if(d>=archonDist&&Clock.getRoundNum()<=requestTime+1)
			return;
		archonLoc = m.locations[0];
		requestLoc = m.locations[1];
		requestTime = Clock.getRoundNum();
		archonDist = d;
	}

}
