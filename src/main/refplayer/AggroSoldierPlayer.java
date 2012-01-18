package refplayer;

import battlecode.common.RobotController;

import refplayer.goal.*;

public class AggroSoldierPlayer extends SoldierPlayer {

	public AggroSoldierPlayer(RobotController rc) {
		super(rc);
	}

	public void setGoals() {
		goals = new Goal [] {
			new SoldierAttackGoal(),
			new FindEnemyGoal(),
			new ExploreGoal(),
			new SeekFluxGoal(),
		};
	}

}
