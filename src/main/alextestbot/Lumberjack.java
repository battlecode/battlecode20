package alextestbot;

import battlecode.common.*;

public class Lumberjack extends Bot {
    public Lumberjack(RobotController rc_) {
        super(rc_);
    }

    @Override
    public void round() throws GameActionException {
        broadcastExistence();
    }
}
