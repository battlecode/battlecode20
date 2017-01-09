package alextestbot;

import battlecode.common.*;

public class Lumberjack extends Bot {
    public Lumberjack(RobotController rc_) {
        super(rc_);
    }

    @Override
    public void round() throws GameActionException {
        broadcastExistence();

        Direction dir = new Direction((float) (Math.random() * 2 * Math.PI));
        if (rc.canMove(dir)) {
            rc.move(dir);
        }

        boolean attacked = false;
        TreeInfo[] trees = rc.senseNearbyTrees();
        for (TreeInfo ti : trees) {
            if (rc.canInteractWithTree(ti.ID)) {
                rc.chop(ti.location);
                attacked = true;
                break;
            }
        }

        if (!attacked) {
            rc.strike();
        }
    }
}
