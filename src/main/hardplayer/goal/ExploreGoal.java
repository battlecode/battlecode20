package hardplayer.goal;

import hardplayer.Static;
import hardplayer.BasePlayer;
import hardplayer.message.MessageHandler;
import hardplayer.message.MessageSender;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class ExploreGoal extends Static implements Goal, MessageHandler {

	static protected final MapLocation zero = new MapLocation(0,0);

	protected int dx, dy;

	protected int timeout;

	public ExploreGoal() {
		handlers[MessageSender.MSG_GO_THIS_WAY]=this;
	}

	public int maxPriority() {
		return EXPLORE;
	}

	public int priority()  {
		if(Clock.getRoundNum()<=timeout)
			return EXPLORE;
		else
			return 0;
	}

	protected boolean anyFightersNearby() {
		return armySize()>0;
	}

	public void moveForward() {
		myNav.moveToForward(new MapLocation(myLoc.x+dx,myLoc.y+dy));
	}

	public void waitForArmy() {
		try {
			myRC.setDirection(zero.directionTo(new MapLocation(dx,dy)));
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

	public void execute() {
		MapLocation archonLoc = closestAtLeastDist(myRC.senseAlliedArchons(),1);
		int d=(myLoc.x-archonLoc.x)*dx+(myLoc.y-archonLoc.y)*dy;
		//debug_setIndicatorStringFormat(1,"%d %d %s %s",dx,dy,myLoc,archonLoc);
		if(d<=0) {
			moveForward();
			return;
		}
		if(d>12) {
			waitForArmy();
			return;
		}
		if(anyFightersNearby())
			moveForward();
		else
			waitForArmy();
	}

	public void receivedMessage(Message m) {
		dx=m.ints[1];
		dy=m.ints[2];
		timeout = Clock.getRoundNum()+20;
	}
}
