package hardplayer;

import hardplayer.navigation.BugNavigation;
import hardplayer.navigation.Navigation;
import hardplayer.navigation.QueuedAction;
import hardplayer.message.*;
import battlecode.common.*;
import hardplayer.goal.Goal;

public abstract class Static {

	public static RobotController myRC;
	public static Navigation myNav;
	public static QueuedAction queued;


	public static FastList allies = new FastList(400);
	public static RobotInfo [] alliedInfos = allies.robotInfos;

	public static FastList alliedArchons = new FastList(400);
	public static FastList alliedSoldiers = new FastList(400);
	public static FastList alliedScouts = new FastList(400);
	public static FastList alliedDisrupters = new FastList(400);
	public static FastList alliedScorchers = new FastList(400);
	public static FastList alliedTowers = new FastList(400);

	public static RobotInfo [] alliedArchonInfos = alliedArchons.robotInfos;

	public static FastList [] alliesByType = new FastList [] { alliedArchons, alliedSoldiers, alliedScouts, alliedDisrupters, alliedScorchers, alliedTowers };

	public static FastList enemies = new FastList(400);
	public static RobotInfo [] enemyInfos = enemies.robotInfos;

	public static FastList enemyArchons = new FastList(400);
	public static FastList enemySoldiers = new FastList(400);
	public static FastList enemyScouts = new FastList(400);
	public static FastList enemyDisrupters = new FastList(400);
	public static FastList enemyScorchers = new FastList(400);
	public static FastList enemyTowers = new FastList(400);

	public static FastList [] enemiesByType = new FastList [] { enemyArchons, enemySoldiers, enemyScouts, enemyDisrupters, enemyScorchers, enemyTowers };

	public static FastList [] allUnits;
	public static FastList [][] unitsByType;

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

	public static SuperMessageStack enemyUnitMessages = new SuperMessageStack();

	public static final int [] threatWeights = new int [] { 0, 2, 1, 3, 4, 0 };
	public static final boolean [] isFighter = new boolean [] { false, true, false, true, true, false };

	public static MapLocation base;

	public static final int ENEMY_PURSUE_TIME = 75;

	public static boolean atWar;
	public static boolean justNowAtWar;
	public static int lastKnownEnemyTime = -200;

	public static MapLocation [] archons;

	public static final boolean debugOutput = "true".equals(System.getProperty("bc.testing.log-dev-players"));

	private static long seed;
    private final static long rnd_multiplier = 0x5DEECE66DL;
    private final static long rnd_addend = 0xBL;
    private final static long rnd_mask = (1L << 48) - 1;	

	public static void init(RobotController RC) {
		myRC = RC;
		myNav = new BugNavigation();
		myTeam = myRC.getTeam();
		myType = myRC.getType();
		myRobot = myRC.getRobot();
		myID = myRobot.getID();
		seed = myID+myRC.getLocation().hashCode();
		base = myRC.sensePowerCore().getLocation();
		if(myTeam==Team.A) {
			allUnits = new FastList [] { allies, enemies };
			unitsByType = new FastList [][] { alliesByType, enemiesByType };
		}
		else {
			allUnits = new FastList [] { enemies, allies };
			unitsByType = new FastList [][] { enemiesByType, alliesByType };
		}
		mySender = new MessageSender();
	}

	// Similar to Random.nextInt(), but always returns a positive
	// integer.
	public static int nextInt() {
		seed = (seed * rnd_multiplier + rnd_addend) & rnd_mask;
		return (int)(seed>>17);
	}

	public static RobotInfo closest(FastList fl, MapLocation otherLoc) {
		int i = fl.size+1;
		RobotInfo [] infos = fl.robotInfos;
		RobotInfo info;
		RobotInfo best = null;
		int bestd = 99999;
		while(--i>=0) {
			info = infos[i];
			int d = otherLoc.distanceSquaredTo(info.location);
			if(d<bestd) {
				bestd = d;
				best = info;
			}
		}
		return best;
	}

	public static RobotInfo closestNoTower(FastList fl, MapLocation otherLoc) {
		int i = fl.size+1;
		RobotInfo [] infos = fl.robotInfos;
		RobotInfo info;
		RobotInfo best = null;
		int bestd = 99999;
		while(--i>=0) {
			info = infos[i];
			if(info.type==RobotType.TOWER)
				continue;
			int d = otherLoc.distanceSquaredTo(info.location);
			if(d<bestd) {
				bestd = d;
				best = info;
			}
		}
		return best;
	}

	public static RobotInfo closest(FastList fl) {
		return closest(fl,myLoc);
	}

	public static RobotInfo closestEnemy() {
		return closest(enemies,myLoc);
	}

	public static int armySize() {
		return threatWeights[1]*(alliedSoldiers.size+1)+threatWeights[2]*(alliedScouts.size+1)+threatWeights[3]*(alliedDisrupters.size+1)+threatWeights[4]*(alliedScorchers.size+1);
	}

	public static PowerNode closestAlliedNode() {
		int bestd = 99999;
		MapLocation loc;
		PowerNode best = null;
		PowerNode [] nodes = myRC.senseAlliedPowerNodes();
		int i = nodes.length-1, d;
		while(--i>=0) {
			loc = nodes[i].getLocation();
			d = myLoc.distanceSquaredTo(loc);
			if(d<bestd) {
				bestd = d;
				best = nodes[i];
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
	
	public static MapLocation closestAtLeastDist(MapLocation [] locs, int mind) {
		int i = locs.length-1, d;
		int bestd = 99999;
		MapLocation best=null, loc;
		while(--i>=0) {
			loc = locs[i];
			d = myLoc.distanceSquaredTo(loc);
			if(d<bestd&&d>=mind) {
				bestd = d;
				best = loc;
			}
		}
		return best;
	}

	public static boolean senseConnected(MapLocation loc) {
		try {
			PowerNode p = (PowerNode)myRC.senseObjectAtLocation(loc,RobotLevel.POWER_NODE);
			return myRC.senseConnected(p);
		} catch(Exception e) {
			debug_stackTrace(e);
		}
		return false;
	}

	public static void debug_stackTrace(Throwable e) {
		if(debugOutput) {
			System.out.println("CAUGHT EXCEPTION:");
			e.printStackTrace();
		}
	}

	public static void debug_println(String s) {
		if(debugOutput)
			System.out.println(s);
	}

	public static void debug_printObject(Object o) {
		debug_println(o.toString());
	}

	public static void debug_format(String fmt, Object ... obj) {
		debug_println(String.format(fmt,obj));
	}

	public static void debug_printInt(int i) {
		debug_println(Integer.toString(i));
	}

	public static void debug_startTiming() {
		roundTimer=Clock.getRoundNum();
		timer=Clock.getBytecodeNum();
	}

	public static void debug_stopTiming() {
		int bytecodes = (Clock.getRoundNum()-roundTimer)*GameConstants.BYTECODE_LIMIT+(Clock.getBytecodeNum()-timer);
		debug_printInt(bytecodes);
	}

	public static void debug_stopTiming(String s) {
		int t=GameConstants.BYTECODE_LIMIT*Clock.getRoundNum()+Clock.getBytecodeNum()-timer;
		debug_println(t+"\t"+s);
	}

	public static void debug_setIndicatorString(int n, String s) {
		if(debugOutput)
			myRC.setIndicatorString(n,s);
	}

	public static void debug_setIndicatorStringFormat(int n, String fmt, Object ... obj) {
		debug_setIndicatorString(n,String.format(fmt,obj));
	}

	public static void debug_setIndicatorStringObject(int n, Object o) {
		if(o!=null)
			debug_setIndicatorString(n,o.toString());
		else
			debug_setIndicatorString(n,null);
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

	public static void checkAtWar() {
		justNowAtWar = false;
		for(int i = enemies.size;i>=0;i--) {
			if(enemyInfos[i].type!=RobotType.TOWER) {
				if(!atWar)
					justNowAtWar = true;
				setAtWar();
				return;
			}
		}
		atWar = (Clock.getRoundNum() - lastKnownEnemyTime) <= ENEMY_PURSUE_TIME;
	}

	public static void setAtWar() {
		atWar = true;
		lastKnownEnemyTime = Clock.getRoundNum();
	}

	public static MapLocation awayFrom(MapLocation from, MapLocation to) {
		return new MapLocation(2*from.x-to.x,2*from.y-to.y);
	}

}
