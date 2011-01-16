package hardplayer;

import hardplayer.goal.Goal;

import battlecode.common.RobotController;

public class LarvaPlayer extends BasePlayer {

	public LarvaPlayer(RobotController rc) {
		super(rc);
		goals = new Goal [0];
	}

	public boolean repurpose() {
		return myRC.components().length >= 3;
	}

}
