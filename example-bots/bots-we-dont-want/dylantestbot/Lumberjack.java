package dylantestbot;


import battlecode.common.*;

public class Lumberjack implements Robot{

    private RobotController rc;
    private MapLocation initialLoc;
    private Direction moveDir;
    private boolean shouldMove;

    Lumberjack(RobotController rc){
        this.rc = rc;
        this.shouldMove = true;
        this.initialLoc = rc.getLocation();
        this.moveDir = new Direction((float) (2*Math.PI * Math.random()));
    }

    @Override
    public void runRound() throws GameActionException {
        this.shouldMove = true;

        // Check if can win with VPs
        Util.checkForVPWin(this.rc);

        // Buy VPs if wanting too
        Util.buyVPsForRound(this.rc);

        // Shake trees
        Util.checkForTreesToShake(this.rc);

        // Dodge
        Util.dodge(this.rc);

        // Attack (doesn't account for partners)
        attackMoveToGardners();

        // Run from enemies
        if(this.shouldMove){
            Util.runFromClosestEnemy(this.rc);
        }

        // Chop Trees (includes moving towards them)
        moveAndChopTrees();

        // Move
        if(this.shouldMove){
           move();
        }
    }

    public void attackMoveToGardners() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
        RobotInfo closestGardner = null;
        float closestDist = 1000000;
        for(RobotInfo enemy : nearbyEnemies){
            if(enemy.getType() == RobotType.GARDENER){
                float distAway = rc.getLocation().distanceTo(enemy.getLocation());
                if (distAway < closestDist){
                    closestDist = distAway;
                    closestGardner = enemy;
                }
            }
        }

        nearbyEnemies = rc.senseNearbyRobots(rc.getType().bodyRadius + GameConstants.INTERACTION_DIST_FROM_EDGE, rc.getTeam().opponent());
        if(nearbyEnemies.length > 0 && rc.canStrike()){
            rc.strike();
            this.shouldMove = false;
        }

        if(closestGardner != null && this.shouldMove){
            Util.moveTowards(rc, closestGardner.getLocation());
        }
    }

    public void moveAndChopTrees() throws GameActionException {
        TreeInfo[] trees = this.rc.senseNearbyTrees();
        TreeInfo closestTreeToMe = null;
        float closestDistToMe = 100000;
        for (TreeInfo tree : trees){
            if (tree.getTeam() != this.rc.getTeam()){
                float distAway = rc.getLocation().distanceTo(tree.getLocation());
                if (distAway < closestDistToMe){
                    closestDistToMe = distAway;
                    closestTreeToMe = tree;
                }
            }
        }
        if (closestTreeToMe != null && rc.canChop(closestTreeToMe.getID())){
            rc.chop(closestTreeToMe.getID());
            this.shouldMove = false;
        }
        if (closestTreeToMe != null && this.shouldMove){
            Util.moveTowards(rc, closestTreeToMe.getLocation());
        }
    }

    public void move() throws GameActionException {
        if (!this.rc.hasMoved() && this.rc.canMove(this.moveDir)) {
            this.rc.move(this.moveDir);
        } else {
            this.moveDir = new Direction((float) (2*Math.PI * Math.random()));
        }
    }

}
