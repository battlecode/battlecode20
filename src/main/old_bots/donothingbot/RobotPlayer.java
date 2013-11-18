package donothingbot;

import battlecode.common.RobotController;

/** This bot does absolutely nothing.
 *
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			rc.yield();
		}
	}
}
