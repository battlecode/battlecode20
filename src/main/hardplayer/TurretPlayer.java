package hardplayer;

import hardplayer.goals.TurretExploreGoal;
import hardplayer.goals.FindEnemyGoal;
import hardplayer.goals.Goal;
import hardplayer.goals.MoreConservativeFindEnemyGoal;
import hardplayer.goals.SeekEnergonGoal;
import hardplayer.goals.TurretAttackGoal;
import hardplayer.goals.TurretRetreatGoal;
import hardplayer.message.MessageSender;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.MapLocation;

public class TurretPlayer extends BasePlayer {

	public TurretPlayer(RobotController RC) {
		super(RC);
	}

	FindEnemyGoal findEnemyGoal;
	TurretAttackGoal attackGoal;

	public void setGoals() {

		handlers[MessageSender.messageTypeIShotThat] = enemyUnitMessages;
		
		findEnemyGoal = new MoreConservativeFindEnemyGoal(this);
		attackGoal = new TurretAttackGoal(this);

		movementGoals = new Goal [] {
			new TurretExploreGoal(this),
			findEnemyGoal,
			new TurretRetreatGoal(this),
			new SeekEnergonGoal(this),
			attackGoal
		};

		broadcastGoals = new Goal [] {
		};

	}

	public void run() {
		setGoals();
		while(myRC.getRobotType()==RobotType.TURRET) {
			try {
				myLoc=myRC.getLocation();
				senseNearbyRobots();
				transferEnergon();
				sortMessages();
				findEnemyGoal.read();
				checkForEnemy();
				moving=myRC.isMovementActive();
				attacking=myRC.isAttackActive();
				debug_setIndicatorString(1,Boolean.toString(myRC.isAttackActive()));
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
						tryBestGoalNotSorted(movementGoals);
						debug_setIndicatorStringObject(0,lastGoal);
						debug_setIndicatorString(1,Boolean.toString(myRC.isDeployed()));
					}
					if(myRC.hasActionSet())
						break actions;
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