package hardplayer;

import hardplayer.goals.BroadcastScoutingFindGoal;
import hardplayer.goals.GatherFluxGoal;
import hardplayer.goals.GoScoutingGoal;
import hardplayer.goals.Goal;
import hardplayer.goals.GoodVisionBroadcastEnemyUnitLocationsGoal;
import hardplayer.goals.SeekEnergonGoal;
import hardplayer.goals.SpotForArmyGoal;
import hardplayer.goals.TransportEnergonGoal;
import hardplayer.goals.WoutFindEnemyGoal;
import hardplayer.goals.WoutAttackBuildingGoal;
import hardplayer.message.SuperMessageStack;

import static hardplayer.goals.BroadcastEnemyUnitLocationsGoal.*;

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

	WoutFindEnemyGoal findEnemyGoal;

	public void setGoals() {
		
		findEnemyGoal = new WoutFindEnemyGoal(this);

		movementGoals = new Goal [] {
			new SpotForArmyGoal(this,findEnemyGoal),
			new TransportEnergonGoal(this),
			new GatherFluxGoal(this),
			new GoScoutingGoal(this),
			new WoutAttackBuildingGoal(this),
			new SeekEnergonGoal(this)
		};

		broadcastGoals = new Goal [] {
			new BroadcastScoutingFindGoal(this,findEnemyGoal),
			new GoodVisionBroadcastEnemyUnitLocationsGoal(this, findEnemyGoal)
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
					spawn();
					if(myRC.hasActionSet())
						break actions;
					if(!moving) { 
						tryBestGoal(movementGoals);
						debug_setIndicatorStringObject(0,lastGoal);
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
				if(info.location.isAdjacentTo(myLoc)&&info.energonLevel<45.) {
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

	public RobotType chooseTypeToSpawn() {
		return RobotType.AURA;
	}

	public void spawn() {
		if(!myRC.canMove(myRC.getDirection())) return;
		try {
			// if we have more than 4552 flux, we could have more than
			// 5000 next turn
			if(myRC.getFlux()>=4552) {
				myRC.spawn(chooseTypeToSpawn());
			}
			else if(myRC.getFlux()>3000) {
				int i,d;
				boolean anyCloseEnough=false;
				MapLocation buildingLoc=myLoc.add(myRC.getDirection());
				for(i=alliedAuras.size-1;i>=0;i--) {
					d=buildingLoc.distanceSquaredTo(alliedAuraInfos[i].location);
					if(d<16) return;
					if(d<=25) anyCloseEnough=true;
				}
				for(i=alliedComms.size-1;i>=0;i--) {
					d=buildingLoc.distanceSquaredTo(alliedCommInfos[i].location);
					if(d<16) return;
					if(d<=25) anyCloseEnough=true;
				}
				if(anyCloseEnough) {
					myRC.spawn(chooseTypeToSpawn());
				}
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
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
			for(i=ints.length-3;i>0&&((ints[i]&TYPE_PRIORITY_MASK)==(1<<TYPE_PRIORITY_OFFSET));i--) {
				if((ints[i]&JUSTSPAWNED_MASK)!=0) {
					lastKnownFighterTime=Clock.getRoundNum();
					return;
				}
			}
		}
	}
}