package nukebot;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.Upgrade;

/**
 * This bot rushes nukes
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			try {
				rc.researchUpgrade(Upgrade.values()[(int)(Math.random()*5)]);
			} catch (GameActionException e) {
				e.printStackTrace();
			}
			rc.yield();
		}
	}
}
