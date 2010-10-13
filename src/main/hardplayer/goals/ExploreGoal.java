package hardplayer.goals;

import hardplayer.BasePlayer;
import hardplayer.message.MessageHandler;
import hardplayer.message.MessageSender;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public abstract class ExploreGoal extends Goal implements MessageHandler {

	static protected final MapLocation zero = new MapLocation(0,0);

	protected int dx, dy;

	public ExploreGoal(BasePlayer bp) {
		super(bp);
		bp.handlers[MessageSender.messageTypeGoInThisDirection]=this;
	}

	public int getMaxPriority() {
		return EXPLORE_PRIORITY;
	}

	public int getPriority()  {
		return EXPLORE_PRIORITY;
	}

	protected abstract boolean anyFightersNearby();

	public void moveForward() {
		player.myNav.moveToForward(new MapLocation(player.myLoc.getX()+dx,player.myLoc.getY()+dy));
	}

	public void waitForArmy() {
		try {
			myRC.setDirection(zero.directionTo(new MapLocation(dx,dy)));
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

	public void tryToAccomplish() {
		MapLocation archonLoc = player.nearestAlliedArchon();
		int d=(player.myLoc.getX()-archonLoc.getX())*dx+(player.myLoc.getY()-archonLoc.getY())*dy;
		if(d<=0) {
			moveForward();
			return;
		}
		if(d>12) {
			waitForArmy();
			return;
		}
		if(player.alliedChainers.size+player.alliedSoldiers.size+player.alliedTurrets.size>0) {
			moveForward();
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
	}
}