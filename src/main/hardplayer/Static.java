package hardplayer;

import hardplayer.navigation.BugNavigation;
import hardplayer.navigation.Navigation;
import hardplayer.navigation.QueuedAction;
import hardplayer.message.*;
import battlecode.common.*;
import hardplayer.goal.Goal;

import java.util.Random;

public abstract class Static {

	public static RobotController myRC;
	public static Navigation myNav;
	public static QueuedAction queued;


	public static FastList allies = new FastList(400);
	public static RobotInfo [] alliedInfos = allies.robotInfos;

	public static FastList enemies = new FastList(400);
	public static RobotInfo [] enemyInfos = enemies.robotInfos;

	public static FastList debris = new FastList(400);

	public static FastList [] allUnits;

	public static Team myTeam;
	public static RobotType myType;
	public static Robot myRobot;
	public static int myID;
	public static int myIDMod1024;

	public static MessageSender mySender;

	public static MessageHandler [] handlers = new MessageHandler [ MessageSender.numTypes ];

	public static Direction [] directions = Direction.values();

	public static MapLocation myLoc;
	public static Direction myDir;

	// for profiling only
	public static int roundTimer;
	public static int timer;

	public static Random rnd;

	public static SuperMessageStack enemyUnitMessages = new SuperMessageStack();

	public static void init(RobotController RC) {
		myRC = RC;
		myNav = new BugNavigation();
		myTeam = myRC.getTeam();
		myType = myRC.getType();
		myRobot = myRC.getRobot();
		myID = myRobot.getID();
		rnd = new Random(myID);
		if(myTeam==Team.A) {
			allUnits = new FastList [] { allies, enemies, debris };
		}
		else {
			allUnits = new FastList [] { enemies, allies, debris };
		}
		mySender = new MessageSender();
		handlers[MessageSender.messageTypeEnemyUnits] = enemyUnitMessages;
	}

	public static RobotInfo closest(FastList fl) {
		int i = fl.size;
		RobotInfo [] infos = fl.robotInfos;
		RobotInfo info;
		RobotInfo best = null;
		int bestd = 99999;
		while(--i>=0) {
			info = infos[i];
			int d = myLoc.distanceSquaredTo(info.location);
			if(d<bestd) {
				bestd = d;
				best = info;
			}
		}
		return best;
}

	public static RobotInfo closestEnemy() {
		int i = enemies.size;
		RobotInfo info;
		RobotInfo best = null;
		int bestd = 99999;
		while(--i>=0) {
			info = enemyInfos[i];
			int d = myLoc.distanceSquaredTo(info.location);
			if(d<bestd) {
				bestd = d;
				best = info;
			}
		}
		return best;
	}

	public static MapLocation nearestAlliedArchonAtLeastDist(int dist) {
		MapLocation [] archons = myRC.senseAlliedArchons();
		int i = archons.length-1, d;
		int bestd = 99999;
		MapLocation best=null, loc;
		while(--i>=0) {
			loc = archons[i];
			d = myLoc.distanceSquaredTo(loc);
			if(d<bestd&&d>=dist) {
				bestd = d;
				best = loc;
			}
		}
		return best;
	}

	public static MapLocation closest(MapLocation [] locs) {
		int i = locs.length-1, d;
		int bestd = 99999;
		MapLocation best=null, loc;
		while(--i>=0) {
			loc = locs[i];
			d = myLoc.distanceSquaredTo(loc);
			if(d<bestd) {
				bestd = d;
				best = loc;
			}
		}
		return best;
	}

	public static void debug_stackTrace(Exception e) {
		System.out.println("CAUGHT EXCEPTION:");
		e.printStackTrace();
	}

	public static void debug_println(String s) {
		System.out.println(s);
	}

	public static void debug_printObject(Object o) {
		System.out.println(o.toString());
	}

	public static void debug_format(String fmt, Object ... obj) {
		System.out.println(String.format(fmt,obj));
	}

	public static void debug_printInt(int i) {
		System.out.println(Integer.toString(i));
	}

	public static void debug_startTiming() {
		roundTimer=Clock.getRoundNum();
		timer=Clock.getBytecodeNum();
	}

	public static void debug_stopTiming() {
		int bytecodes = (Clock.getRoundNum()-roundTimer)*Clock.getBytecodeLimit()+(Clock.getBytecodeNum()-timer);
		System.out.println(bytecodes);
	}

	public static void debug_stopTiming(String s) {
		int t=6000*Clock.getRoundNum()+Clock.getBytecodeNum()-timer;
		System.out.println(t+"\t"+s);
	}

	public static void debug_setIndicatorString(int n, String s) {
		myRC.setIndicatorString(n,s);
	}

	public static void debug_setIndicatorStringFormat(int n, String fmt, Object ... obj) {
		myRC.setIndicatorString(n,String.format(fmt,obj));
	}

	public static void debug_setIndicatorStringObject(int n, Object o) {
		if(o!=null)
			myRC.setIndicatorString(n,o.toString());
		else
			myRC.setIndicatorString(n,null);
	}

	public static void debug_stackTrace() {
		debug_stackTrace(new Exception());
	}

	public static void setQueued(QueuedAction a) {
		if(queued!=null) {
			debug_println("Warning: action already queued");
		}
		queued=a;
	}

	public static void moveAdjacentTo(MapLocation target) {
		try {
			int d = target.distanceSquaredTo(myLoc);
			if(d>2)
				myNav.moveToForward(target);
			else if(d>0)
				myRC.setDirection(myLoc.directionTo(target));
			else
				myNav.moveToForward(target.add(Direction.NORTH_EAST));
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
