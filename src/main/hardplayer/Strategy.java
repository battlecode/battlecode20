package hardplayer;

import battlecode.common.RobotController;

public abstract class Strategy {
	
	/**
	 * Takes in the RobotController and assigns everything from there,
	 * this essentially is the crux of any strategy we choose to use
	 * @param rc
	 */
	public abstract void execute(RobotController rc);
}