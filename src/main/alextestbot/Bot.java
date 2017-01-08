package alextestbot;

import battlecode.common.*;

public class Bot {
    RobotController rc;
    RobotType type;
    int ID;
    MapLocation location;
    Team team;
    public Bot(RobotController rc_) {
        this.rc = rc_;

        this.ID = rc.getID();
        this.type = rc.getType();
        this.team = rc.getTeam();
    }

    public void run() {
        while (true) {
            try {
                initRound();
                round();
            } catch (Exception e) {
                System.out.println("Exception! " + e);
                e.printStackTrace();
            }
            Clock.yield();
        }
    }

    public void initRound() throws GameActionException {
        this.location = rc.getLocation();
    }

    public void broadcastExistence() throws GameActionException {
        rc.broadcast(type.ordinal(), rc.readBroadcast(type.ordinal()) + 1);
    }

    public void round() throws GameActionException {
        broadcastExistence();
    }
}
