package dylantestbot;


import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class Soldier implements Robot{

    private RobotController rc;

    Soldier(RobotController rc){
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

        // Dodge

        // Move

        // Broadcast stuff
    }

}
