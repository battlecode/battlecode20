package dylantestbot;


import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Lumberjack implements Robot{

    private RobotController rc;

    Lumberjack(RobotController rc){
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

        // Attack

        // Chop trees

        // Dodge

        // Move

        // Broadcast stuff
    }

}
