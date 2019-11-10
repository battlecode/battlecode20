package alextestbot;

import battlecode.common.*;

public class Archon extends Bot {
    public Archon(RobotController rc_) {
        super(rc_);
    }

    @Override
    public void round() throws GameActionException {
        broadcastExistence();
        boolean leader = false;

        // Am I the leader?
        if (rc.readBroadcast(type.ordinal()) == 1) {
            leader = true;
        }

        if (leader) {
            leaderArchonRound();
        } else {
            normalArchonRound();
        }
    }

    public void leaderArchonRound() throws GameActionException {
        // Figure out how many units you have.
        int[] counts = new int[RobotType.values().length];
        for (RobotType t : RobotType.values()) {
            counts[t.ordinal()] = rc.readBroadcast(t.ordinal());
            rc.broadcast(t.ordinal(), 0); // reset
        }

        if (counts[RobotType.GARDENER.ordinal()] < rc.getRoundNum() / 400 + 1) {
            System.out.println("Found " + counts[RobotType.GARDENER.ordinal()] + " gardeners, need more");
            for (float rad = 0; rad <= Math.PI * 2; rad += Math.PI / 8) {
                if (rc.canHireGardener(new Direction(rad))) {
                    rc.hireGardener(new Direction(rad));
                    break;
                }
            }
        }
    }

    public void normalArchonRound() throws GameActionException {
    }
}
