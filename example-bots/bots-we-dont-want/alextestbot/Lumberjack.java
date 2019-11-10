package alextestbot;

import battlecode.common.*;

public class Lumberjack extends Bot {
    Direction lastDir;
    public Lumberjack(RobotController rc_) {
        super(rc_);
        this.lastDir = null;
    }

    @Override
    public void round() throws GameActionException {
        broadcastExistence();

        boolean moved = false;
        TreeInfo[] trees = rc.senseNearbyTrees(-1, Team.NEUTRAL);
        if (trees.length > 0) {
            moveTowardsClosest(trees);
        }

        TreeInfo[] enemyTrees = rc.senseNearbyTrees(-1, team.opponent());
        if (!rc.hasMoved()) {
            if (enemyTrees.length > 0) {
                moveTowardsClosest(enemyTrees);
            }
        }

        RobotInfo[] enemies = rc.senseNearbyRobots(-1, team.opponent());
        if (!rc.hasMoved()) {
            if (enemies.length > 0) {
                moveTowardsClosest(enemies);
            }
        }

        if (!rc.hasMoved() && trees.length == 0 && enemyTrees.length == 0 && enemies.length == 0) {
            if (lastDir != null && rc.canMove(lastDir)) {
                rc.move(lastDir);
            } else {
                Direction dir = new Direction((float) (Math.random() * 2 * Math.PI));
                if (rc.canMove(dir)) {
                    rc.move(dir);
                    lastDir = dir;
                }
            }
        }

        for (TreeInfo ti : trees) {
            if (rc.canInteractWithTree(ti.ID)) {
                rc.chop(ti.location);
                break;
            }
        }

        if (!rc.hasAttacked()) {
            for (TreeInfo ti : enemyTrees) {
                if (rc.canInteractWithTree(ti.ID)) {
                    rc.chop(ti.location);
                    break;
                }
            }
        }

        if (!rc.hasAttacked()) {
            rc.strike();
        }
    }
}
