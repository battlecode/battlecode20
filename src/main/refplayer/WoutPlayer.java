package refplayer;

import refplayer.goals.Goal;
import refplayer.goals.BroadcastEnemyUnitLocationsGoal;
import refplayer.goals.SeekEnergonGoal;
import refplayer.goals.TransportEnergonGoal;
import refplayer.goals.FindEnemyGoal;
import refplayer.goals.GoScoutingGoal;
import refplayer.goals.EvilBroadcastGoal;
import refplayer.message.SuperMessageStack;
import refplayer.message.MessageSender;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;
import battlecode.common.Message;

import static battlecode.common.GameConstants.ENERGON_TO_FLUX_CONVERSION;

public class WoutPlayer extends BasePlayer {

	public static final int JUSTSPAWNED_THRESH = 5;

	public static final double AURA_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.AURA.maxEnergon();
	public static final double SOLDIER_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.SOLDIER.maxEnergon();
	public static final double TURRET_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.TURRET.maxEnergon();
	public static final double CHAINER_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.CHAINER.maxEnergon();
	public static final double COMM_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.COMM.maxEnergon();

	public int lastKnownFighterTime;
	public boolean seenFighterThisTurn;
	
	public boolean scouting;
	
	public WoutPlayer(RobotController RC) {
		super(RC);
	}

	FindEnemyGoal findEnemyGoal;

	public void setGoals() {
		
		findEnemyGoal = new FindEnemyGoal(this);

		movementGoals = new Goal [] {
			new TransportEnergonGoal(this),
			new GoScoutingGoal(this), // not for scouting, just for flux
			new SeekEnergonGoal(this)
		};

		broadcastGoals = new Goal [] {
			new BroadcastEnemyUnitLocationsGoal(this, findEnemyGoal),
			new EvilBroadcastGoal(this)
		};

	}

	public void run() {
		setGoals();
		while(myRC.getRobotType()==RobotType.WOUT) {
			try {
				myLoc=myRC.getLocation();
				senseNearbyRobots();
				transferEnergon();
				transferFlux();
				sortMessages();
				findEnemyGoal.read();
				checkForEnemy();
				moving=myRC.isMovementActive();
				attacking=myRC.isAttackActive();
				actions: {
					if((!moving)&&queued!=null) {
						queued.doAction();
						queued=null;
					}
					if(myRC.hasActionSet())
						break actions;
					if(!attacking) shoot();
					if(myRC.hasActionSet())
						break actions;
					if(!moving) { 
						tryBestGoal(movementGoals);
						//debug_setIndicatorStringObject(0,lastGoal);
					}
				}
				tryBestGoal(broadcastGoals);
				findEnemyGoal.decay();
			} catch(Exception e) {
				debug_stackTrace(e);
			}
			myRC.yield();
		}
	}

	public void transferEnergon() {
		double myEnergon=myRC.getEnergonLevel();
		double transferAmount;
		double archonLowEnergon;
		RobotInfo info;
		int i;
		try {
			RobotInfo [] robots = alliedArchonInfos;
			for(i=alliedArchons.size-1;i>=0;i--) {
				info=robots[i];
				if((!myLoc.isAdjacentTo(info.location))||info.energonLevel<-3.) continue;
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
			robots = alliedTurretInfos;
			for(i=alliedTurrets.size-1;i>=0;i--) {
				info=robots[i];
				if(myLoc.distanceSquaredTo(info.location)>2||info.energonLevel<TURRET_DEAD_ENERGON) continue;
				transferAmount=MAX_RESERVE-info.energonReserve;
				if(transferAmount<=0) continue;
				if(myEnergon>info.eventualEnergon*TURRET_ENERGON_RATIO) {
					myRC.transferUnitEnergon(transferAmount,info.location,RobotLevel.ON_GROUND);
					myEnergon-=transferAmount;
					if(myEnergon<MAX_RESERVE) return;
				}
			}
			robots=alliedSoldierInfos;
			for(i=alliedSoldiers.size-1;i>=0;i--) {
				info=robots[i];
				if(myLoc.distanceSquaredTo(info.location)>2||info.energonLevel<SOLDIER_DEAD_ENERGON) continue;
				transferAmount=MAX_RESERVE-info.energonReserve;
				if(transferAmount<=0) continue;
				if(myEnergon>info.eventualEnergon*SOLDIER_ENERGON_RATIO) {
					myRC.transferUnitEnergon(transferAmount,info.location,RobotLevel.ON_GROUND);
					myEnergon-=transferAmount;
					if(myEnergon<MAX_RESERVE) return;
				}
			}
			robots=alliedChainerInfos;
			for(i=alliedChainers.size-1;i>=0;i--) {
				info=robots[i];
				if(myLoc.distanceSquaredTo(info.location)>2||info.energonLevel<CHAINER_DEAD_ENERGON) continue;
				transferAmount=MAX_RESERVE-info.energonReserve;
				if(transferAmount<=0) continue;
				if(myEnergon>info.eventualEnergon*CHAINER_ENERGON_RATIO) {
					myRC.transferUnitEnergon(transferAmount,info.location,RobotLevel.ON_GROUND);
					myEnergon-=transferAmount;
					if(myEnergon<MAX_RESERVE) return;
				}
			}
			robots=alliedWoutInfos;
			for(i=alliedWouts.size-1;i>=0;i--) {
				info=robots[i];
				if(myLoc.distanceSquaredTo(info.location)>2||info.energonLevel<WOUT_DEAD_ENERGON) continue;
				transferAmount=MAX_RESERVE-info.energonReserve;
				if(transferAmount<=0) continue;
				if(myEnergon>info.eventualEnergon) {
					myRC.transferUnitEnergon(transferAmount,info.location,RobotLevel.ON_GROUND);
					myEnergon-=transferAmount;
					if(myEnergon<MAX_RESERVE) return;
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void transferFlux() {
		double myFlux = myRC.getFlux();
		double transferAmount;
		if(myFlux<=0) return;
		try {
			RobotInfo info;
			int i;
			RobotInfo [] infos = alliedAuraInfos;
			for(i=alliedAuras.size-1;i>=0;i--) {
				info = infos[i];
				if(info.location.isAdjacentTo(myLoc)) {
					transferAmount = Math.min(myFlux,ENERGON_TO_FLUX_CONVERSION*(MAX_RESERVE-info.energonReserve));
					if(transferAmount<=0) continue;
					myRC.transferFlux(transferAmount,info.location,RobotLevel.ON_GROUND);
					myFlux-=transferAmount;
					if(myFlux<=0) return;
				}
			}
			infos = alliedCommInfos;
			for(i=alliedComms.size-1;i>=0;i--) {
				info = infos[i];
				if(info.location.isAdjacentTo(myLoc)) {
					transferAmount = Math.min(myFlux,ENERGON_TO_FLUX_CONVERSION*(MAX_RESERVE-info.energonReserve));
					if(transferAmount<=0) continue;
					myRC.transferFlux(transferAmount,info.location,RobotLevel.ON_GROUND);
					myFlux-=transferAmount;
					if(myFlux<=0) return;
				}
			}
			infos = alliedArchonInfos;
			for(i=alliedArchons.size-1;i>=0;i--) {
				info = infos[i];
				if(info.location.distanceSquaredTo(myLoc)<=2) {
					transferAmount = Math.min(myFlux,info.type.maxFlux()-info.flux);
					if(transferAmount<=0) continue;
					myRC.transferFlux(transferAmount,info.location,RobotLevel.IN_AIR);
					myFlux-=transferAmount;
					if(myFlux<=0) return;
				}
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

	public void shoot() {
		try {
			RobotInfo [] infos = enemyArchonInfos;
			int i;
			for(i=enemyArchons.size-1;i>=0;i--) {
				if(myRC.canAttackSquare(infos[i].location)) {
					myRC.attackAir(infos[i].location);
					return;
				}
			}
			Robot r;
			for(i=7;i>=0;i--) {
				r=myRC.senseGroundRobotAtLocation(myLoc.add(directions[i]));
				if(r!=null&&myRC.senseRobotInfo(r).team!=myTeam) {
					myRC.attackGround(myLoc.add(directions[i]));
					return;
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void checkForEnemy() {
		super.checkForEnemy();
		/*
		if(enemyUnitMessages.lengths[SuperMessageStack.t]>0)
			scouting=false;
		*/
		int i;
		RobotInfo [] robots;
		robots=enemySoldierInfos;
		for(i=enemySoldiers.size-1;i>=0;i--) {
			if(robots[i].roundsUntilAttackIdle<=JUSTSPAWNED_THRESH) {
				lastKnownFighterTime=Clock.getRoundNum();
				seenFighterThisTurn=true;
				return;
			}
		}
		robots=enemyTurretInfos;
		for(i=enemyTurrets.size-1;i>=0;i--) {
			if(robots[i].roundsUntilAttackIdle<=JUSTSPAWNED_THRESH) {
				lastKnownFighterTime=Clock.getRoundNum();
				seenFighterThisTurn=true;
				return;
			}
		}
		robots=enemyChainerInfos;
		for(i=enemyChainers.size-1;i>=0;i--) {
			if(robots[i].roundsUntilAttackIdle<=JUSTSPAWNED_THRESH) {
				lastKnownFighterTime=Clock.getRoundNum();
				seenFighterThisTurn=true;
				return;
			}
		}
		seenFighterThisTurn=false;
		int j;
		int [] ints;
		Message [] stack=enemyUnitMessages.messages[SuperMessageStack.t];
		for(j=enemyUnitMessages.lengths[SuperMessageStack.t]-1;j>=0;j--) {
			ints=stack[j].ints;
			for(i=ints.length-3;i>0;i--) {
				lastKnownFighterTime=Clock.getRoundNum();
				return;
			}
		}
	}

	public void sortMessages() {
		mySender.updateRoundNum();
		evilSender.updateRoundNum();
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
			   type>=MessageSender.numTypes) continue;
			if(handlers[type]==null||
			   !mySender.isValid(newMessages[i]))
				evilSender.receivedMessage(newMessages[i]);
			else
				handlers[type].receivedMessage(newMessages[i]);
		}
	}
}