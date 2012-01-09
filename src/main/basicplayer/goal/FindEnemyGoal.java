package basicplayer.goal;

import basicplayer.message.MessageHandler;
import basicplayer.message.MessageSender;
import basicplayer.navigation.BugNavigation;
import basicplayer.BasePlayer;
import basicplayer.Static;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.RobotInfo;

public class FindEnemyGoal extends Static implements Goal, MessageHandler {

	public static double xsum;
	public static double ysum;
	public static double n;

	static final double DECAY_RATE = 15./16.;

	public static final long [] unitWeights = new long [] { 6000L, 2000L, 1500L,  2000L,  2000L, 0L };
	public static final long messageWeight = 1000L;

	public FindEnemyGoal() {
		handlers[MessageSender.MSG_ENEMY_2] = this;
	}

	public static void decay() {
		n*=DECAY_RATE;
		xsum*=DECAY_RATE;
		ysum*=DECAY_RATE;
	}

	static public void read() {
		MapLocation loc;
		int i, j;
		long newn=0;
		long newxsum=0;
		long newysum=0;
		long w;
		RobotInfo info;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			loc = info.location;
			w=unitWeights[info.type.ordinal()]/(myLoc.distanceSquaredTo(loc)+1);
			newn+=w;
			newxsum+=w*loc.x;
			newysum+=w*loc.y;
		}
		n+=newn;
		xsum+=newxsum-newn*myLoc.x;
		ysum+=newysum-newn*myLoc.y;
	}

	public int maxPriority() {
		return FIND_ENEMY;
	}

	public int priority() {
		return atWar?FIND_ENEMY:0;
	}

	public MapLocation getEnemyLoc() {
		return new MapLocation((int)(xsum/n)+myLoc.x,(int)(ysum/n)+myLoc.y);
	}

	public int getEnemyDX() {
		return (int)(xsum/n);
	}

	public int getEnemyDY() {
		return (int)(ysum/n);
	}

	public void execute() {
		if(n>0)
			debug_setIndicatorStringFormat(1,"%f %f %f",n,xsum/n,ysum/n);
		myNav.moveToForward(getEnemyLoc());
	}

	public void receivedMessage(Message m) {
		MapLocation loc = m.locations[1];
		long w = m.ints[1] * messageWeight;
		n+=w;
		xsum+=w*(loc.x-myLoc.x);
		ysum+=w*(loc.y-myLoc.y);
		setAtWar();
	}

}
