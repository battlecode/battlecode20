package dylantestbot;


import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;


public class Archon implements Robot{

    private RobotController rc;
    private Direction moveDir;

    Archon(RobotController rc){
        this.rc = rc;
        this.moveDir = new Direction((float) (2*Math.PI * Math.random()));
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
        Util.dodge(this.rc);

        // Run from enemies
        Util.runFromClosestEnemy(this.rc);

        // Move around
        move();

        // First three bots check
        if(performBeginning()){
            // Spawn Gardeners
            spawnGardenerIfShould();
        }
    }

    public boolean performBeginning() throws GameActionException {
        boolean haveGardener = rc.readBroadcastBoolean(0);
        boolean haveScout = rc.readBroadcastBoolean(1);
        boolean haveLumberJack = rc.readBroadcastBoolean(2);
        if(!haveGardener){
            if (this.rc.getBuildCooldownTurns() == 0 && rc.hasRobotBuildRequirements(RobotType.GARDENER)) {
                Direction[] spawnDirs = Util.getSpawnDirections(360);
                int randomStartIndex = (int) Math.floor(Math.random()*360);
                boolean shouldContinue = true;
                for (int i = randomStartIndex; i < 360; i++){
                    Direction dir = spawnDirs[i];
                    if (rc.canHireGardener(dir)){
                        rc.hireGardener(dir);
                        rc.broadcastBoolean(0, true);
                        shouldContinue = false;
                        break;
                    }
                }
                if(shouldContinue){
                    for (int i = 0; i < randomStartIndex; i++){
                        Direction dir = spawnDirs[i];
                        if (rc.canHireGardener(dir)){
                            rc.hireGardener(dir);
                            rc.broadcastBoolean(0, true);
                            break;
                        }
                    }
                }
            }
        }
        if(haveGardener && haveScout && haveLumberJack){
            return true;
        }
        return false;
    }

    public void move() throws GameActionException {
        if (!this.rc.hasMoved() && this.rc.canMove(this.moveDir)) {
            this.rc.move(this.moveDir);
        } else {
            this.moveDir = new Direction((float) (2*Math.PI * Math.random()));
        }
    }

    public void spawnGardenerIfShould() throws GameActionException {
        int robotCount = rc.getRobotCount();
        if (robotCount < 50 && rc.getTeamBullets() > 250) {
            if (this.rc.getBuildCooldownTurns() == 0 && rc.hasRobotBuildRequirements(RobotType.GARDENER)) {
                Direction[] spawnDirs = Util.getSpawnDirections(36);
                int randomStartIndex = (int) Math.floor(Math.random()*36);
                boolean shouldContinue = true;
                for (int i = randomStartIndex; i < 36; i++){
                    Direction dir = spawnDirs[i];
                    if (rc.canHireGardener(dir)){
                        rc.hireGardener(dir);
                        shouldContinue = false;
                        break;
                    }
                }
                if(shouldContinue){
                    for (int i = 0; i < randomStartIndex; i++){
                        Direction dir = spawnDirs[i];
                        if (rc.canHireGardener(dir)){
                            rc.hireGardener(dir);
                            break;
                        }
                    }
                }
            }
        }
    }

}
