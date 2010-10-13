package basicplayer;

import basicplayer.goals.Goal;
import basicplayer.goals.SeekEnergonGoal;
import basicplayer.goals.WanderForwardGoal;
//import basicplayer.goals.ChainerAttackGoal;
import basicplayer.util.FastList;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;

public class ChainerPlayer extends BasePlayer {

	public ChainerPlayer(RobotController RC) {
		super(RC);
	}

	public void setGoals() {

		movementGoals = new Goal [] {
			new WanderForwardGoal(this),
			//new ChainerAttackGoal(this),
			new SeekEnergonGoal(this)
		};

		broadcastGoals = new Goal [] {
		};

	}

	public void run() {
		setGoals();
		while(myRC.getRobotType()==RobotType.CHAINER) {
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
			} catch(Exception e) {
				debug_stackTrace(e);
			}
			myRC.yield();
		}
	}

	public boolean shootAir(FastList fl) throws battlecode.common.GameActionException {
		int i;
		for(i=fl.size-1;i>=0;i--) {
			if(myRC.canAttackSquare(fl.robotInfos[i].location)) {
				myRC.attackAir(fl.robotInfos[i].location);
				return true;
			}
		}
		return false;
	}

	public boolean shootGround(FastList fl) throws battlecode.common.GameActionException {
		int i;
		for(i=fl.size-1;i>=0;i--) {
			if(myRC.canAttackSquare(fl.robotInfos[i].location)) {
				myRC.attackGround(fl.robotInfos[i].location);
				return true;
			}
		}
		return false;
	}

	public void shoot() {
		try {
			if(shootAir(enemyArchons)) return;
			if(shootGround(enemyChainers)) return;
			if(shootGround(enemySoldiers)) return;
			if(shootGround(enemyTurrets)) return;
			if(shootGround(enemyTeleporters)) return;
			if(shootGround(enemyComms)) return;
			if(shootGround(enemyWouts)) return;
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}

}