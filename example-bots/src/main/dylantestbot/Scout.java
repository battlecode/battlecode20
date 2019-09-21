package dylantestbot;


import battlecode.common.*;

public class Scout implements Robot{

    private RobotController rc;

    private Direction scoutingDir;

    Scout(RobotController rc){
        this.rc = rc;
        this.scoutingDir = new Direction((float) (2*Math.PI * Math.random()));
    }

    @Override
    public void runRound() throws GameActionException {
        // Check if can win with VPs
        Util.checkForVPWin(this.rc);

        // Buy VPs if wanting too
        Util.buyVPsForRound(this.rc);

        // Move towards trees to shake
        moveToShakeTrees();

        // Shake trees
        Util.checkForTreesToShake(this.rc);

        // Dodge
        Util.dodge(this.rc);

        // Run from enemies
        Util.runFromClosestEnemy(this.rc);

        // Move
        move();
    }

    public void moveToShakeTrees() throws GameActionException {
        TreeInfo[] trees = this.rc.senseNearbyTrees(this.rc.getType().sensorRadius, Team.NEUTRAL);
        TreeInfo closestTree = null;
        float closestDist = 100000;
        for (TreeInfo tree : trees){
            if (tree.containedBullets > 0 && tree.getTeam() != this.rc.getTeam()){
                float distAway = rc.getLocation().distanceTo(tree.getLocation());
                if (distAway < closestDist){
                    closestDist = distAway;
                    closestTree = tree;
                }
            }
        }
        if (closestTree == null){
            return;
        }
        Util.moveTowards(rc, closestTree.getLocation());
    }

    public void move() throws GameActionException {
        if (!this.rc.hasMoved() && this.rc.canMove(this.scoutingDir)) {
            this.rc.move(this.scoutingDir);
        } else {
            this.scoutingDir = new Direction((float) (2*Math.PI * Math.random()));
        }
    }

}
