package hardplayer;

import hardplayer.goals.ChainerTurretExploreGoal;
import hardplayer.goals.FindEnemyGoal;
import hardplayer.goals.Goal;
import hardplayer.goals.MoreConservativeFindEnemyGoal;
import hardplayer.goals.SeekEnergonGoal;
import hardplayer.goals.TurretAttackGoal;
import hardplayer.goals.CampingTurretAttackGoal;
import hardplayer.goals.TurretDeployGoal;
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

public class BuildingTurretPlayer extends TurretPlayer {

	public BuildingTurretPlayer(RobotController RC) {
		super(RC);
	}

	public void setGoals() {

		handlers[MessageSender.messageTypeIShotThat] = enemyUnitMessages;
		
		findEnemyGoal = new MoreConservativeFindEnemyGoal(this);
		attackGoal = new CampingTurretAttackGoal(this);

		movementGoals = new Goal [] {
			new TurretDeployGoal(this),
			attackGoal,
			new SeekEnergonGoal(this)
		};

		broadcastGoals = new Goal [] {
		};

	}

}