package hardplayer;

import hardplayer.goals.Goal;
import hardplayer.message.MessageHandler;
import hardplayer.message.MessageSender;
import hardplayer.message.SuperMessageStack;
import hardplayer.navigation.BugNavigation;
import hardplayer.navigation.Navigation;
import hardplayer.navigation.QueuedAction;
import hardplayer.util.FastList;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Message;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.Team;
import battlecode.common.TerrainTile;

public abstract class BasePlayer {

	public RobotController myRC;
	public Navigation myNav;
	public QueuedAction queued;

	public boolean moving=false;
	public boolean attacking=false;

	public static final double SACRIFICE_SELF_FOR_ARCHON = 10.;
	public static final double MAX_RESERVE=2.;
	public static final double ARCHON_MAX_RESERVE = 5.;

	public static final double ARCHON_DEAD_ENERGON = -1.;
	public static final double SOLDIER_DEAD_ENERGON = -1.+RobotType.SOLDIER.energonUpkeep();
	public static final double TURRET_DEAD_ENERGON = -1.+RobotType.TURRET.energonUpkeep();
	public static final double WOUT_DEAD_ENERGON = -1.+RobotType.WOUT.energonUpkeep();
	public static final double CHAINER_DEAD_ENERGON = -1.+RobotType.CHAINER.energonUpkeep();
	public static final double COMM_DEAD_ENERGON = -1.;
	public static final double AURA_DEAD_ENERGON = -1.;

	public static final int ENEMY_PURSUE_TIME = 75;

	public static final int [] dx = new int [] { 0, 1, 1, 1, 0, -1, -1, -1, 0, 0 };
	public static final int [] dy = new int [] { -1, -1, 0, 1, 1, 1, 0, -1, 0, 0 };

	static public final Direction [] directions = { Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST, Direction.NONE };

	static public final RobotType [] robotTypes = { RobotType.ARCHON, RobotType.WOUT, RobotType.CHAINER, RobotType.SOLDIER, RobotType.TURRET };

	public FastList alliedArchons = new FastList (6);
	public FastList alliedWouts = new FastList (113);
	public FastList alliedChainers = new FastList (113);
	public FastList alliedSoldiers = new FastList (113);
	public FastList alliedTurrets = new FastList (113);
	public FastList alliedTeleporters = new FastList (113);
	public FastList alliedComms = new FastList(113);
	public FastList alliedAuras = new FastList(113);

	public RobotInfo [] alliedArchonInfos = alliedArchons.robotInfos;
	public RobotInfo [] alliedWoutInfos = alliedWouts.robotInfos;
	public RobotInfo [] alliedChainerInfos = alliedChainers.robotInfos;
	public RobotInfo [] alliedSoldierInfos = alliedSoldiers.robotInfos;
	public RobotInfo [] alliedTurretInfos = alliedTurrets.robotInfos;
	public RobotInfo [] alliedTeleporterInfos = alliedTeleporters.robotInfos;
	public RobotInfo [] alliedCommInfos = alliedComms.robotInfos;
	public RobotInfo [] alliedAuraInfos = alliedAuras.robotInfos;

	/*
	public Robot [] alliedArchonRobots = alliedArchons.robots;
	public Robot [] alliedWoutRobots = alliedWouts.robots;
	public Robot [] alliedChainerRobots = alliedChainers.robots;
	public Robot [] alliedSoldierRobots = alliedSoldiers.robots;
	public Robot [] alliedTurretRobots = alliedTurrets.robots;
	public Robot [] alliedTeleporterRobots = alliedTeleporters.robots;
	public Robot [] alliedCommRobots = alliedComms.robots;
	public Robot [] alliedAuraRobots = alliedAuras.robots;
	*/

	public FastList [] alliedUnits = new FastList [] { alliedArchons, alliedWouts, alliedChainers, alliedSoldiers, alliedTurrets, alliedComms, alliedTeleporters, alliedAuras };

	public FastList enemyArchons = new FastList (6);
	public FastList enemyWouts = new FastList (113);
	public FastList enemyChainers = new FastList (113);
	public FastList enemySoldiers = new FastList (113);
	public FastList enemyTurrets = new FastList (113);
	public FastList enemyTeleporters = new FastList (113);
	public FastList enemyComms = new FastList (113);
	public FastList enemyAuras = new FastList (113);

	public RobotInfo [] enemyArchonInfos = enemyArchons.robotInfos;
	public RobotInfo [] enemyWoutInfos = enemyWouts.robotInfos;
	public RobotInfo [] enemyChainerInfos = enemyChainers.robotInfos;
	public RobotInfo [] enemySoldierInfos = enemySoldiers.robotInfos;
	public RobotInfo [] enemyTurretInfos = enemyTurrets.robotInfos;
	public RobotInfo [] enemyTeleporterInfos = enemyTeleporters.robotInfos;
	public RobotInfo [] enemyCommInfos = enemyComms.robotInfos;
	public RobotInfo [] enemyAuraInfos = enemyAuras.robotInfos;

	/*
	public Robot [] enemyArchonRobots = enemyArchons.robots;
	public Robot [] enemyWoutRobots = enemyWouts.robots;
	public Robot [] enemyChainerRobots = enemyChainers.robots;
	public Robot [] enemySoldierRobots = enemySoldiers.robots;
	public Robot [] enemyTurretRobots = enemyTurrets.robots;
	public Robot [] enemyTeleporterRobots = enemyTeleporters.robots;
	public Robot [] enemyCommRobots = enemyComms.robots;
	public Robot [] enemyAuraRobots = enemyAuras.robots;
	*/

	public FastList [] enemyUnits = new FastList [] { enemyArchons, enemyWouts, enemyChainers, enemySoldiers, enemyTurrets, enemyComms, enemyTeleporters, enemyAuras };

	public FastList [][] allUnits;

	public Goal [] movementGoals;
	public Goal [] broadcastGoals;

	public Goal lastGoal;

	public Team myTeam;
	public RobotType myType;
	public Robot myRobot;
	public int myID;
	public int myIDMod1024;
	
	public MessageSender mySender;

	public MessageHandler [] handlers = new MessageHandler [ MessageSender.numTypes ];

	public MapLocation myLoc;

	// don't want to think we heard about the enemy recently at the beginning
	public int lastKnownEnemyTime=-10000;

	// for profiling only
	int timer;

	public SuperMessageStack enemyUnitMessages = new SuperMessageStack();

	static public final int AT_WAR_TIME = 75;
	public boolean atWar;

	public BasePlayer(RobotController RC) {
		myRC = RC;
		myNav = new BugNavigation(this);
		myTeam = myRC.getTeam();
		myType = myRC.getRobotType();
		myRobot = myRC.getRobot();
		myID = myRobot.getID();
		if(myTeam==Team.A) {
			allUnits = new FastList [][] { alliedUnits, enemyUnits };
			//SuperMessageStack.KEEP_TIME=1;
		}
		else {
			allUnits = new FastList [][] { enemyUnits, alliedUnits };
			//SuperMessageStack.KEEP_TIME=2;
		}
		mySender = new MessageSender(this);
		handlers[MessageSender.messageTypeEnemyUnits] = enemyUnitMessages;

	}

	public abstract void run();

		public void checkIfActive() {
		attacking = myRC.isAttackActive();
		moving = myRC.isMovementActive();
	}

	public static void debug_stackTrace(Exception e) {
		//System.arraycopy(null,0,null,0,0);
		System.out.println("CAUGHT EXCEPTION:");
		e.printStackTrace();
	}

	public static void debug_println(String s) {
		System.out.println(s);
	}

	public static void debug_printObject(Object o) {
		System.out.println(o.toString());
	}

	public static void debug_printInt(int i) {
		System.out.println(Integer.toString(i));
	}

	public void debug_startTiming() {
		timer=6000*Clock.getRoundNum()+Clock.getBytecodeNum();
	}

	public void debug_stopTiming() {
		System.out.println(6000*Clock.getRoundNum()+Clock.getBytecodeNum()-timer);
	}

	public void debug_stopTiming(String s) {
		int t=6000*Clock.getRoundNum()+Clock.getBytecodeNum()-timer;
		System.out.println(t+"\t"+s);
	}

	public void debug_setIndicatorString(int n, String s) {
		myRC.setIndicatorString(n,s);
	}

	public void debug_setIndicatorStringObject(int n, Object o) {
		if(o!=null)
			myRC.setIndicatorString(n,o.toString());
		else
			myRC.setIndicatorString(n,null);
	}

	public static MapLocation multipleAddDirection(MapLocation orig, Direction dir, int n) {
		return new MapLocation(orig.getX()+n*dx[dir.ordinal()],orig.getY()+n*dy[dir.ordinal()]);
	}

	public void setQueued(QueuedAction a) {
		if(queued!=null) {
			debug_println("Warning: action already queued");
		}
		queued=a;
	}

	public boolean canSpawnGround(Direction d) throws battlecode.common.GameActionException {
		MapLocation loc=myRC.getLocation().add(d);
		return myRC.senseTerrainTile(loc).getType()==TerrainTile.TerrainType.LAND&&myRC.senseGroundRobotAtLocation(loc)==null;
	}

	public boolean enoughEnergonToTransform(RobotType type) {
		return myRC.getEnergonLevel()>=type.spawnCost()/2.+5*type.energonUpkeep();
	}

	public void senseNearbyRobots() {
		alliedArchons.size = 0;
		alliedWouts.size = 0;
		alliedChainers.size = 0;
		alliedSoldiers.size = 0;
		alliedTurrets.size = 0;
		alliedComms.size = 0;
		alliedTeleporters.size = 0;
		alliedAuras.size = 0;
		enemyArchons.size = 0;
		enemyWouts.size = 0;
		enemyChainers.size = 0;
		enemySoldiers.size = 0;
		enemyTurrets.size = 0;
		enemyComms.size = 0;
		enemyTeleporters.size = 0;
		enemyAuras.size = 0;
		RobotInfo info;
		Robot [] robots;
		Robot r;
		int i;
		FastList fl;
		// I'm not sure that I want to know how many hours of my life
		// I've spent doing tiny optimizations like this.
		FastList [][] allUnits = this.allUnits;
		RobotController myRC = this.myRC;
		try {
			robots = myRC.senseNearbyAirRobots();
			for(i=robots.length-1;i>=0;i--) {
				info=myRC.senseRobotInfo(robots[i]);
				fl=allUnits[info.team.ordinal()][0];
				//fl.robots[fl.size]=robots[i];
				fl.robotInfos[fl.size++]=info;
			}
			robots = myRC.senseNearbyGroundRobots();
			for(i=robots.length-1;i>=0;i--) {
				info=myRC.senseRobotInfo(robots[i]);
				fl=allUnits[info.team.ordinal()][info.type.ordinal()];
				//fl.robots[fl.size]=robots[i];
				fl.robotInfos[fl.size++]=info;
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public RobotInfo nearestOneOf(FastList l) {
		int d, dmin=99999;
		int i;
		RobotInfo best = null;
		RobotInfo [] infos = l.robotInfos;
		for(i=l.size-1;i>=0;i--) {
			d=myLoc.distanceSquaredTo(infos[i].location);
			if(d<dmin) {
				best=infos[i];
				dmin=d;
			}
		}
		return best;
	}

	public RobotInfo nearestOneOf(FastList l, RobotInfo info) {
		int d, dmin=myLoc.distanceSquaredTo(info.location);
		int i;
		RobotInfo best = info;
		RobotInfo [] infos = l.robotInfos;
		for(i=l.size-1;i>=0;i--) {
			d=myLoc.distanceSquaredTo(infos[i].location);
			if(d<dmin) {
				best=infos[i];
				dmin=d;
			}
		}
		return best;
	}

	public MapLocation nearestOneOf(MapLocation [] locs) {
		int d, dmin=99999;
		int i;
		MapLocation best=null;
		for(i=locs.length-1;i>=0;i--) {
			d=myLoc.distanceSquaredTo(locs[i]);
			if(d<dmin) {
				best=locs[i];
				dmin=d;
			}
		}
		return best;
	}

	public MapLocation nearestAlliedArchon() {
		int d, dmin=99999;
		MapLocation [] archons = myRC.senseAlliedArchons();
		MapLocation best=null;
		//MapLocation myLoc=myRC.getLocation();
		int i;
		for(i=archons.length-1;i>=0;i--) {
			d=myLoc.distanceSquaredTo(archons[i]);
			if(d<dmin) {
				best=archons[i];
				dmin=d;
			}
		}
		return best;
	}

	public MapLocation nearestAlliedArchonTo(MapLocation loc) {
		int d, dmin=99999;
		MapLocation [] archons = myRC.senseAlliedArchons();
		MapLocation best=null;
		//MapLocation myLoc=myRC.getLocation();
		int i;
		for(i=archons.length-1;i>=0;i--) {
			d=loc.distanceSquaredTo(archons[i]);
			if(d<dmin) {
				best=archons[i];
				dmin=d;
			}
		}
		return best;
	}

	public MapLocation nearestAlliedArchonAtLeastDist(int mind) {
		int d, dmin=99999;
		MapLocation [] archons = myRC.senseAlliedArchons();
		MapLocation best=null;
		//MapLocation myLoc=myRC.getLocation();
		int i;
		for(i=archons.length-1;i>=0;i--) {
			d=myLoc.distanceSquaredTo(archons[i]);
			if(d>=mind&&d<dmin) {
				best=archons[i];
				dmin=d;
			}
		}
		return best;
	}

	public boolean isThereAnArchonWithin(int dist) {
		int i;
		MapLocation [] archons = myRC.senseAlliedArchons();
		for(i=archons.length-1;i>=0;i--) {
			if(myLoc.distanceSquaredTo(archons[i])<=dist)
				return true;
		}
		return false;
	}

	public boolean isThereAnArchonNear(MapLocation loc, int dist) {
		int i;
		MapLocation [] archons = myRC.senseAlliedArchons();
		for(i=archons.length-1;i>=0;i--) {
			if(loc.distanceSquaredTo(archons[i])<=dist)
				return true;
		}
		return false;
	}

	public static MapLocation awayFrom(MapLocation loc1, MapLocation loc2) {
		return new MapLocation(2*loc1.getX()-loc2.getX(),2*loc1.getY()-loc2.getY());
	}

	public void tryBestGoal(Goal [] goals) {
		//debug_println("trying best goal");
		int best=Goal.NEVER;
		int i, p;
		Goal g;
		for(i=goals.length-1;i>=0;i--) {
			g=goals[i];
			if(g.getMaxPriority()<=best)
				break;
			//debug_startTiming();
			p=g.getPriority();
			//debug_stopTiming("getPriority "+g);
			if(p>=best) {
				best=p;
				lastGoal=g;
			}
		}
		if(best!=Goal.NEVER) {
			//debug_startTiming();
			lastGoal.tryToAccomplish();
			//debug_stopTiming("tta "+best);
		}
	}

	public void tryBestGoalNotSorted(Goal [] goals) {
		//debug_println("trying best goal");
		int best=Goal.NEVER;
		int i, p;
		Goal g;
		for(i=goals.length-1;i>=0;i--) {
			g=goals[i];
			if(g.getMaxPriority()<=best)
				continue;
			p=g.getPriority();
			if(p>=best) {
				best=p;
				lastGoal=g;
			}
		}
		if(best!=Goal.NEVER) {
			lastGoal.tryToAccomplish();
		}
	}

	public void sortMessages() {
		mySender.updateRoundNum();
		// Using Clock.getRoundNum() could prove problematic if we ever go
		// over the bytecode limit
		SuperMessageStack.t=(SuperMessageStack.t+1)%SuperMessageStack.KEEP_TIME;
		enemyUnitMessages.lengths[SuperMessageStack.t]=0;
		Message [] newMessages=myRC.getAllMessages();
		//BasePlayer.debug_println(Integer.toString(newMessages.length));
		int [] ints;
		int type;
		int i;
		// We need to put the most recent message at the top of the stack!
		for(i=0;i<newMessages.length;i++) {
			ints=newMessages[i].ints;
			if(ints==null||
			   ints.length<3||
			   (type=ints[0])<0||
			   type>=MessageSender.numTypes||
			   handlers[type]==null||
			   !mySender.isValid(newMessages[i])) continue;
			handlers[type].receivedMessage(newMessages[i]);
		}
	}
	
	public void checkForEnemy() {
		if(enemyArchons.size+enemyWouts.size+enemyChainers.size+
		   enemySoldiers.size+enemyTurrets.size>0||
		   enemyUnitMessages.lengths[SuperMessageStack.t]>0) {
			lastKnownEnemyTime=Clock.getRoundNum();
			atWar=true;
		}
		else {
			atWar=Clock.getRoundNum()<lastKnownEnemyTime+ENEMY_PURSUE_TIME;
		}
	}

	// for fighting units; archons and wouts
	// have their own transfer energon routines
	public void transferEnergon() {
		double myEnergon=myRC.getEnergonLevel();
		double transferAmount;
		double archonLowEnergon;
		RobotInfo info;
		int i;
		try {
			for(i=alliedArchons.size-1;i>=0;i--) {
				info=alliedArchonInfos[i];
				if(myLoc.distanceSquaredTo(info.location)>2||info.energonLevel<-3.) continue;
				if(info.eventualEnergon<ArchonPlayer.MIN_ENERGON) {
					transferAmount=MAX_RESERVE-info.energonReserve;
					if(transferAmount<=0) continue;
					if(transferAmount>=myEnergon) {
						if(info.energonLevel<SACRIFICE_SELF_FOR_ARCHON) {
							myRC.transferUnitEnergon(myRC.getEnergonLevel(),info.location,RobotLevel.IN_AIR);
						}
					}
					else {
						myRC.transferUnitEnergon(transferAmount,info.location,RobotLevel.IN_AIR);
						myEnergon-=transferAmount;
					}
				}
			}
			if(myEnergon<MAX_RESERVE) return;
			for(i=alliedTurrets.size-1;i>=0;i--) {
				info=alliedTurretInfos[i];
				if((!myLoc.isAdjacentTo(info.location))||info.energonLevel<TURRET_DEAD_ENERGON) continue;
				transferAmount=MAX_RESERVE-info.energonReserve;
				if(transferAmount<=0) continue;
				if(myEnergon>info.eventualEnergon+MAX_RESERVE) {
					myRC.transferUnitEnergon(transferAmount,info.location,RobotLevel.ON_GROUND);
					myEnergon-=transferAmount;
					if(myEnergon<MAX_RESERVE) return;
				}
			}
			for(i=alliedSoldiers.size-1;i>=0;i--) {
				info=alliedSoldierInfos[i];
				if((!myLoc.isAdjacentTo(info.location))||info.energonLevel<SOLDIER_DEAD_ENERGON) continue;
				transferAmount=MAX_RESERVE-info.energonReserve;
				if(transferAmount<=0) continue;
				if(myEnergon>info.eventualEnergon+MAX_RESERVE) {
					myRC.transferUnitEnergon(transferAmount,info.location,RobotLevel.ON_GROUND);
					myEnergon-=transferAmount;
					if(myEnergon<MAX_RESERVE) return;
				}
			}
			for(i=alliedChainers.size-1;i>=0;i--) {
				info=alliedChainerInfos[i];
				if((!myLoc.isAdjacentTo(info.location))||info.energonLevel<CHAINER_DEAD_ENERGON) continue;
				transferAmount=MAX_RESERVE-info.energonReserve;
				if(transferAmount<=0) continue;
				if(myEnergon>info.eventualEnergon+MAX_RESERVE) {
					myRC.transferUnitEnergon(transferAmount,info.location,RobotLevel.ON_GROUND);
					myEnergon-=transferAmount;
					if(myEnergon<MAX_RESERVE) return;
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}