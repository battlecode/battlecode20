package hardplayer;

import hardplayer.goals.ChainerTurretExploreGoal;
import hardplayer.goals.FindEnemyGoal;
import hardplayer.goals.Goal;
import hardplayer.goals.MoreConservativeFindEnemyGoal;
import hardplayer.goals.SeekEnergonGoal;
import hardplayer.goals.TurretAttackGoal;
import hardplayer.goals.CampingTurretAttackGoal;
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

public class CampingTurretPlayer extends TurretPlayer {

	public CampingTurretPlayer(RobotController RC) {
		super(RC);
	}

	public void setGoals() {

		handlers[MessageSender.messageTypeIShotThat] = enemyUnitMessages;
		
		findEnemyGoal = new MoreConservativeFindEnemyGoal(this);
		attackGoal = new CampingTurretAttackGoal(this);

		movementGoals = new Goal [] {
			attackGoal
		};

		broadcastGoals = new Goal [] {
		};

	}

	public void run() {
		while(myRC.isMovementActive()) {
			myRC.yield();
		}
		try {
			myRC.deploy();
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
		super.run();
	}

}