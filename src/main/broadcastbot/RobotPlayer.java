package broadcastbot;

import battlecode.common.*;

/** The example funcs player is a player meant to demonstrate basic usage of the most common commands.
 * Robots will move around randomly, occasionally mining and writing useless messages.
 * The HQ will spawn soldiers continuously. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
        try {
            if (rc.getType() == RobotType.HQ) {
                rc.wearHat();
                rc.yield();
                rc.wearHat();
                rc.yield();
            }
        } catch (Exception e) { System.out.println("fail"); }
        while (true) {
            try {
                if (rc.getType() == RobotType.HQ) {
                    if (rc.isActive()) {
                        rc.spawn(rc.senseHQLocation().directionTo(rc.senseEnemyHQLocation()));
                        rc.yield();
                    }
                } else if (rc.getType() == RobotType.SOLDIER) {
                    if (rc.isActive()) {
                        if (rc.canMove(rc.senseHQLocation().directionTo(rc.senseEnemyHQLocation()))) {
                            rc.move(rc.senseHQLocation().directionTo(rc.senseEnemyHQLocation()));
                        } else {
                            if (rc.senseTeamMilkQuantity(rc.getTeam()) > GameConstants.HAT_MILK_COST) {
                                rc.wearHat();
                            } else {
                                //rc.construct(RobotType.PASTR);
                                rc.selfDestruct();
                            }
                        }
                    }
                } else if (rc.getType() == RobotType.PASTR) {
                    if (rc.isActive()) {
                        if (rc.senseTeamMilkQuantity(rc.getTeam()) > GameConstants.HAT_MILK_COST) {
                            rc.wearHat();
                        }
                    }
                }
            } catch (Exception e) {}

            rc.yield();
        }
	}
}
