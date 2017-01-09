package alextestbot;

import battlecode.common.*;

public class Gardener extends Bot {
    public Gardener(RobotController rc_) {
        super(rc_);
    }

    @Override
    public void round() throws GameActionException {
        broadcastExistence();

        // Now, move around until you are 5 away from nearest unit.
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
        if (nearbyRobots.length > 0) {
            MapLocation closest = nearbyRobots[0].location;
            for (int i = 1; i < nearbyRobots.length; ++i) {
                if (nearbyRobots[i].location.distanceTo(location) < closest.distanceTo(location)) {
                    closest = nearbyRobots[i].location;
                }
            }
            if (closest.distanceTo(location) < 5) {
                Direction goal = location.directionTo(closest).opposite();
                for (float rad = 0; rad <= Math.PI; rad += Math.PI / 4) {
                    if (rc.canMove(goal.rotateLeftRads(rad))) {
                        rc.move(goal.rotateLeftRads(rad));
                        break;
                    } else if (rc.canMove(goal.rotateRightRads(rad))) {
                        rc.move(goal.rotateRightRads(rad));
                        break;
                    }
                }
            }
        }

        /*
        // Plant a tree if you can.
        // Left and right directions only.
        for (float rad = 0; rad <= Math.PI * 2; rad += Math.PI) {
            if (rc.canPlantTree(new Direction(rad))) {
                rc.plantTree(new Direction(rad));
                break;
            }
        }
        */

        // Then, make lumberjacks.
        for (float rad = (float) Math.PI / 2; rad <= Math.PI * 2; rad += Math.PI) {
            if (rc.canBuildRobot(RobotType.LUMBERJACK, new Direction(rad))) {
                rc.buildRobot(RobotType.LUMBERJACK, new Direction(rad));
                break;
            }
        }
    }
}
