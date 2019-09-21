package alextestbot;

import battlecode.common.*;

public class Tank extends Bot {
    MapLocation[] initialEnemyArchonLocs;
    Direction lastDir;
    public Tank(RobotController rc_) {
        super(rc_);
        this.initialEnemyArchonLocs = rc.getInitialArchonLocations(team.opponent());
        this.lastDir = null;
    }

    @Override
    public void round() throws GameActionException {
        broadcastExistence();

        // Now, move away from nearest unit, unless it's an enemy that does no damage.
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
        if (nearbyRobots.length > 0) {
            MapLocation closest = null;
            for (int i = 0; i < nearbyRobots.length; ++i) {
                if ((closest == null || nearbyRobots[i].location.distanceTo(location) < closest.distanceTo(location)) && !(nearbyRobots[i].team != team && !nearbyRobots[i].type.canAttack())) {
                    closest = nearbyRobots[i].location;
                }
            }
            if (closest != null) {
                Direction goal = location.directionTo(closest).opposite();
                for (float rad = 0; rad <= Math.PI; rad += Math.PI / 4) {
                    if (rc.canMove(goal.rotateLeftRads(rad))) {
                        rc.move(goal.rotateLeftRads(rad));
                        lastDir = goal.rotateLeftRads(rad);
                        break;
                    } else if (rc.canMove(goal.rotateRightRads(rad))) {
                        rc.move(goal.rotateRightRads(rad));
                        lastDir = goal.rotateRightRads(rad);
                        break;
                    }
                }
            }
        } else if (lastDir != null) {
            for (float rad = 0; rad <= Math.PI; rad += Math.PI / 4) {
                if (rc.canMove(lastDir.rotateLeftRads(rad))) {
                    rc.move(lastDir.rotateLeftRads(rad));
                    lastDir = lastDir.rotateLeftRads(rad);
                    break;
                } else if (rc.canMove(lastDir.rotateRightRads(rad))) {
                    rc.move(lastDir.rotateRightRads(rad));
                    lastDir = lastDir.rotateRightRads(rad);
                    break;
                }
            }
        }

        // Fire at lowest health visible enemy.
        int choice = -1;
        for (int i = 0; i < nearbyRobots.length; ++i) {
            if (nearbyRobots[i].team != team && (choice < 0 || nearbyRobots[choice].health > nearbyRobots[i].health)) {
                choice = i;
            }
        }
        if (choice >= 0 && rc.getTeamBullets() >= 1) {
            rc.fireSingleShot(location.directionTo(nearbyRobots[choice].location));
        }
    }
}
