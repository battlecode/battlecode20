package hardplayer;

import hardplayer.navigation.BugNavigation;
import hardplayer.navigation.Navigation;
import hardplayer.navigation.QueuedAction;
import hardplayer.sensor.Sensor;
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

	public static Mine [] mines;

	public static Team myTeam;
	public static Chassis myType;
	public static Robot myRobot;
	public static int myID;
	public static int myIDMod1024;

	public static MessageSender mySender;
	
	public static MessageHandler [] handlers = new MessageHandler [ MessageSender.numTypes ];

	public static MapLocation myLoc;
	
	// for profiling only
	public static int roundTimer;
	public static int timer;

	public static Random rnd;

	public static SuperMessageStack enemyUnitMessages = new SuperMessageStack();

	public static BroadcastController radio;
	public static MovementController motor;
	public static SensorController sensor;
	public static BuilderController builder;
	public static WeaponController [] weapons = new WeaponController [0];

	public static Sensor sensorAI;

	public static int seenConstructor;

	public static double resourcesLastRound;
	public static boolean resourcesIncreased;

	public static void init(RobotController RC) {
		myRC = RC;
		myNav = new BugNavigation();
		myTeam = myRC.getTeam();
		myType = myRC.getChassis();
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
		sensorAI = new Sensor();
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

	public static void debug_stackTrace(Exception e) {
		System.err.println("CAUGHT EXCEPTION:");
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

	public static boolean canBuild(MapLocation loc, RobotLevel level) throws GameActionException {
		return sensor.senseObjectAtLocation(loc,level)==null&&myRC.senseTerrainTile(loc).isTraversableAtHeight(level);
	}

	public static void buildIfPossible(ComponentType t, MapLocation loc, RobotLevel l) {
		if(resourcesIncreased&&myRC.getTeamResources()>=t.cost+1)
			try {
				builder.build(t,loc,l);
			} catch(Exception e) {
				debug_stackTrace(e);
			}
	}

	public static void moveAdjacentTo(MapLocation target) {
		try {
			int d = target.distanceSquaredTo(myLoc);
			if(d>2)
				myNav.moveToForward(target);
			else if(d>0)
				motor.setDirection(myLoc.directionTo(target));
			else
				myNav.moveToForward(target.add(Direction.NORTH_EAST));
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public static Goal [] asArray(Goal... goals) {
		return goals;
	}

}
