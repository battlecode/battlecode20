package dylantestbot;


import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Gardner implements Robot{

    private RobotController rc;
    private boolean amGardening;
    private int closeCounter;

    Gardner(RobotController rc){
        this.rc = rc;
        this.amGardening = false;
        this.closeCounter = 0;
    }

    @Override
    public void runRound() throws GameActionException {
        // Random variable gathering
        RobotInfo nearestPartner = Util.getClosestPartner(this.rc);

        // Check if can win with VPs
        Util.checkForVPWin(this.rc);

        // Buy VPs if wanting too
        Util.buyVPsForRound(this.rc);

        // Shake trees
        Util.checkForTreesToShake(this.rc);

        // Move

        // Switch modes

        // Spawn Trees

        // Spawn LumberJacks

        // Spawn Soldiers

        // Water Trees

        // Broadcast stuff

    }

    public void move() {
        if(!this.amGardening) {

        }
    }

    public void switchModes(RobotInfo nearestPartner) {
        if(!this.amGardening){
            if(this.rc.getLocation().distanceTo(nearestPartner.getLocation())){

            }
        }
    }

}
