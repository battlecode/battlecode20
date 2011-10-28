package hardplayer.goal;

import hardplayer.Static;
import hardplayer.message.MessageHandler;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Message;

public class FindEnemyGoal extends Static implements Goal, MessageHandler {

	static MapLocation enemyLoc;
	static int requestTime;
	static int enemyDist;

	public int maxPriority() { return FIND_NODE; }

	public int priority() {
		if(Clock.getRoundNum()-requestTime<=10)
			return FIND_ENEMY;
		else
			return 0;
	}

	public void execute() {
		myNav.moveToForward(enemyLoc);
	}

	public void receivedMessage(Message m) {
		int d = myLoc.distanceSquaredTo(m.locations[1]);
		if(d>=enemyDist&&Clock.getRoundNum()<=requestTime)
			return;
		enemyLoc = m.locations[1];
		requestTime = Clock.getRoundNum();
		enemyDist = d;
	}

}
