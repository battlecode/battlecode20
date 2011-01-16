package hardplayer;

import hardplayer.navigation.BugNavigation;
import hardplayer.navigation.Navigation;
import hardplayer.navigation.QueuedAction;
import hardplayer.sensor.Sensor;
import hardplayer.message.*;
import battlecode.common.*;
import hardplayer.goal.Goal;

public abstract class Static {

	public static RobotController myRC;
	public static Navigation myNav;
	public static QueuedAction queued;

	public static FastList alliedAir;
	public static FastList alliedGround;

	public static FastList [] alliedUnits = new FastList [] { null, alliedGround, alliedAir };

	public static FastList enemyAir;
	public static FastList enemyGround;

	public static FastList [] enemyUnits = new FastList [] { null, enemyGround, enemyAir };

	public static FastList mines;

	public static FastList [] neutralObjects = new FastList [] { mines, null, null };

	public static FastList [][] allUnits;

	public static Team myTeam;
	public static Chassis myType;
	public static Robot myRobot;
	public static int myID;
	public static int myIDMod1024;

	public static MessageSender mySender;
	
	public static MessageHandler [] handlers = new MessageHandler [ MessageSender.numTypes ];

	public static MapLocation myLoc;
	
	// for profiling only
	public static int timer;
	
	public static SuperMessageStack enemyUnitMessages = new SuperMessageStack();

	public static BroadcastController radio;
	public static MovementController motor;
	public static SensorController sensor;
	public static BuilderController builder;

	public static Sensor sensorAI;

	public static int seenConstructor;

	public static void checkComponents() {
		for(ComponentController c : myRC.newComponents()) {
			if(c instanceof SensorController)
				sensor = (SensorController)c;
			else if(c instanceof MovementController)
				motor = (MovementController)c;
			else if(c instanceof BroadcastController)
				radio = (BroadcastController)c;
		}
	}

	public static void init(RobotController RC) {
		myRC = RC;
		myNav = new BugNavigation();
		myTeam = myRC.getTeam();
		myType = myRC.getChassis();
		myRobot = myRC.getRobot();
		myID = myRobot.getID();
		if(myTeam==Team.A) {
			allUnits = new FastList [][] { alliedUnits, enemyUnits };
		}
		else {
			allUnits = new FastList [][] { enemyUnits, alliedUnits };
		}
		mySender = new MessageSender();
		handlers[MessageSender.messageTypeEnemyUnits] = enemyUnitMessages;
		sensorAI = new Sensor();
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
		timer=6000*Clock.getRoundNum()+Clock.getBytecodeNum();
	}

	public static void debug_stopTiming() {
		System.out.println(6000*Clock.getRoundNum()+Clock.getBytecodeNum()-timer);
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
	
	public static void setQueued(QueuedAction a) {
		if(queued!=null) {
			debug_println("Warning: action already queued");
		}
		queued=a;
	}

	public static Goal [] asArray(Goal... goals) {
		return goals;
	}

}
