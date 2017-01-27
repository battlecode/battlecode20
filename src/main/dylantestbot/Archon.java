package dylantestbot;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;


public class Archon implements Robot{

    private RobotController rc;

    Archon(RobotController rc){
        this.rc = rc;
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

        // Move around

        // Spawn Gardeners
        spawnGardenerIfShould();

        // Broadcast stuff

    }

    public void spawnGardenerIfShould() throws GameActionException {
        int gardenerCount = rc.readBroadcast(1);
        if (gardenerCount < 10) {
            if (this.rc.getBuildCooldownTurns() == 0 && rc.hasRobotBuildRequirements(RobotType.GARDENER)) {
                Direction[] spawnDirs = Util.getSpawnDirections(36);
                for (Direction dir : spawnDirs) {
                    if (rc.canHireGardener(dir)){
                        rc.hireGardener(dir);
                        rc.broadcast(1, gardenerCount+1);
                        break;
                    }
                }
            }
        }
    }

}
