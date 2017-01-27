package dylantestbot;


import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Scout implements Robot{

    private RobotController rc;

    private Direction scoutingDir;

    Scout(RobotController rc){
        this.rc = rc;

        this.scoutingDir = new Direction(2*Math.PI * random());
    }

    @Override
    public void runRound() throws GameActionException {
        // Check if can win with VPs
        Util.checkForVPWin(this.rc);

        // Buy VPs if wanting too
        Util.buyVPsForRound(this.rc);

        // Shake trees
        Util.checkForTreesToShake(this.rc);

        // Dodge
        dodge(rc);

        // Move
        if (!rc.hasMoved() && rc.canMove(scoutingDir)) {
            rc.move(scoutingDir);
        } else {
            scoutingDir = new Direction(2*Math.PI * random());
        }

        // Shoot gardeners if visible
        if (rc.canFireSingleShot()) {
            RobotInfo[] robots = senseNearbyRobots(-1, rc.getTeam().opponent());
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.GARDENER) {
                    rc.fireSingleShot(rc.getLocation().directionTo(robot.location));
                    break;
                }
            }
        }

        // Broadcast stuff
    }

}
