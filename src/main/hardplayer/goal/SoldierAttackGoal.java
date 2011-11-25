package hardplayer.goal;

import battlecode.common.*;

import hardplayer.Static;
import hardplayer.message.MessageHandler;
import hardplayer.message.MessageSender;

public class SoldierAttackGoal extends Static implements Goal, MessageHandler {
	
	static public final int [] priorities = new int [] { -100, 0, 0, 0, 0, 0 };
	
	static RobotInfo target;
	static MapLocation msgTarget;

	static Message message;
	
	public SoldierAttackGoal() {
		handlers[MessageSender.MSG_ENEMY_UNITS] = this;
	}

	public int maxPriority() { return ATTACK; }

	public int priority() {
		int i;
		int value, bestv = 99999;
		RobotInfo info;
		target = null;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			if(info.type==RobotType.TOWER&&!senseConnected(info.location))
				continue;
			value = priorities[info.type.ordinal()]+myLoc.distanceSquaredTo(info.location);
			if(value<bestv) {
				target = info;
				bestv = value;
			}
		}
		if(target!=null)
			return ATTACK;
		if(message!=null)
			if(Clock.getRoundNum()-message.ints[1]>2)
				message = null;
			else
				return ATTACK;
		return 0;
	}

	public void moveTo(MapLocation l) throws GameActionException {
		int d = myLoc.distanceSquaredTo(l);
		if(d>=RobotType.SOLDIER.attackRadiusMaxSquared)
			myNav.moveToForward(l);
		else if(d>0)
			myRC.setDirection(myLoc.directionTo(l));
	}

	public void execute() {

		try {
			if(target!=null) {
				moveTo(target.location);
			}
			else {
				int i, best = 0;
				int d, dmin=99999;
				MapLocation [] locs = message.locations;
				for(i=locs.length-1;i>0;i--) {
					d = myLoc.distanceSquaredTo(locs[i]);
					if(d<dmin) {
						dmin = d;
						best = i;
					}
				}
				if(best>0)
					moveTo(locs[best]);
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}
	
	public void receivedMessage(Message m) {
		if(message==null||m.ints[1]-message.ints[1]>1||myLoc.distanceSquaredTo(m.locations[0])<=myLoc.distanceSquaredTo(message.locations[0]))
			message = m;
	}

}
