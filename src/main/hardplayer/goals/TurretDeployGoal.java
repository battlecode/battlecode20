package hardplayer.goals;

import hardplayer.BasePlayer;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

public class TurretDeployGoal extends Goal {

	MapLocation enemyLocation;

	public TurretDeployGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return TURRET_DEPLOY;
	}

	public int getPriority() {
		if(player.atWar&&(!myRC.isDeployed())&&player.isThereAnArchonWithin(2))
			return TURRET_DEPLOY;
		else
			return NEVER;
	}

	public void tryToAccomplish() {
		try {
			myRC.deploy();
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
	}
	
}