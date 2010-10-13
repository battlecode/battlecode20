package basicplayer;

import basicplayer.goals.Goal;
import basicplayer.goals.SeekEnergonGoal;
import basicplayer.goals.TransportEnergonGoal;
import basicplayer.goals.WoutDistributeFluxGoal;
import basicplayer.goals.WoutSpreadOutGoal;
import basicplayer.goals.WoutSuicideGoal;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;

public class WoutPlayer extends BasePlayer {

	public static final double AURA_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.AURA.maxEnergon();
	public static final double SOLDIER_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.SOLDIER.maxEnergon();
	public static final double TURRET_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.TURRET.maxEnergon();
	public static final double CHAINER_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.CHAINER.maxEnergon();
	public static final double COMM_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.COMM.maxEnergon();
	public static final double TELEPORTER_ENERGON_RATIO = RobotType.WOUT.maxEnergon() / RobotType.TELEPORTER.maxEnergon();

	public WoutPlayer(RobotController RC) {
		super(RC);
	}

	public void setGoals() {

		movementGoals = new Goal [] {
			new WoutSpreadOutGoal(this),
			new TransportEnergonGoal(this),
			new WoutDistributeFluxGoal(this),
			new SeekEnergonGoal(this),
			new WoutSuicideGoal(this)
		};

		broadcastGoals = new Goal [] {
		};

	}

	public void run() {
		setGoals();
		while(myRC.getRobotType()==RobotType.WOUT) {
			try {
				myLoc=myRC.getLocation();
				senseNearbyRobots();
				transferEnergon();
				sortMessages();
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
						debug_setIndicatorStringObject(0,lastGoal);
					}
				}
				//tryBestGoal(broadcastGoals);
				transferFlux();
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
			robots = alliedAuraInfos;
			for(i=alliedAuras.size-1;i>=0;i--) {
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
			/*
			robots=alliedTeleporterInfos;
			for(i=alliedTeleporters.size-1;i>=0;i--) {
				info=robots[i];
				if(myLoc.distanceSquaredTo(info.location)>2||info.energonLevel<TELEPORTER_DEAD_ENERGON) continue;
				transferAmount=MAX_RESERVE-info.energonReserve;
				if(transferAmount<=0) continue;
				myRC.transferEnergon(transferAmount,info.location,RobotLevel.ON_GROUND);
				myEnergon-=transferAmount;
				if(myEnergon<MAX_RESERVE) return;
			}
			*/
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void transferFlux() {
		try {
			RobotInfo [] infos = alliedArchonInfos;
			int i;
			for(i=alliedArchons.size-1;i>=0;i--) {
				if(infos[i].location.distanceSquaredTo(myLoc)<=2) {
					myRC.transferFlux(myRC.getFlux(),infos[i].location,RobotLevel.IN_AIR);
					return;
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
}