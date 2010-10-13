package hardplayer;

import hardplayer.goals.ChainerAttackGoal;
import hardplayer.goals.ChainerTurretExploreGoal;
import hardplayer.goals.FindEnemyGoal;
import hardplayer.goals.NewChainerFindEnemyGoal;
import hardplayer.goals.Goal;
import hardplayer.goals.LimitedVisionBroadcastEnemyUnitLocationsGoal;
import hardplayer.goals.SeekEnergonGoal;

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

	FindEnemyGoal findEnemyGoal;
	ChainerAttackGoal attackGoal;

	public void setGoals() {
		
		findEnemyGoal = new NewChainerFindEnemyGoal(this);
		attackGoal = new ChainerAttackGoal(this);

		movementGoals = new Goal [] {
			new ChainerTurretExploreGoal(this),
			findEnemyGoal,
			new SeekEnergonGoal(this),
			attackGoal
		};

		broadcastGoals = new Goal [] {
			new LimitedVisionBroadcastEnemyUnitLocationsGoal(this,findEnemyGoal)
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

}