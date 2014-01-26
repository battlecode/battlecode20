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
                            GameObject[] objs = rc.senseNearbyGameObjects(Robot.class, rc.getLocation(), 1000, rc.getTeam().opponent());
                            MapLocation m = rc.getLocation().add(rc.senseHQLocation().directionTo(rc.senseEnemyHQLocation()));
                            System.out.println(m + " is in front of me");
                            System.out.println("sensed " + objs.length + " enemies");
                            System.out.println("can sense square in front of me? " + rc.canSenseSquare(m));
                            System.out.println("sensing object at square in front of me is null: " + (rc.senseObjectAtLocation(m) == null));
                            for (GameObject obj : objs) {
                                System.out.println(rc.senseLocationOf(obj) + "");
                            }
                            int rand = (int) (Math.random() * objs.length);
                            if (rand < objs.length) {
                                rc.attackSquare(rc.senseLocationOf(objs[rand]));
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
