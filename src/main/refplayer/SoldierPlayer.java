package refplayer;

import refplayer.goals.SoldierAttackGoal;
import refplayer.goals.SoldierExploreGoal;
import refplayer.goals.FindEnemyGoal;
import refplayer.goals.Goal;
import refplayer.goals.SeekEnergonGoal;
import refplayer.goals.EvilBroadcastGoal;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;

public class SoldierPlayer extends BasePlayer {

	public SoldierPlayer(RobotController RC) {
		super(RC);
	}

	FindEnemyGoal findEnemyGoal;
	SoldierAttackGoal attackGoal;

	public void setGoals() {
		
		findEnemyGoal = new FindEnemyGoal(this);
		attackGoal = new SoldierAttackGoal(this);

		movementGoals = new Goal [] {
			new SoldierExploreGoal(this),
			findEnemyGoal,
			new SeekEnergonGoal(this),
			attackGoal
		};

		broadcastGoals = new Goal [] {
			//new EvilBroadcastGoal(this)
		};

	}

	public void run() {
		setGoals();
		while(myRC.getRobotType()==RobotType.SOLDIER) {
			try {
				myLoc=myRC.getLocation();
				senseNearbyRobots();
				transferEnergon();
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
					if(!attacking) attackGoal.shootOnly();
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
}