package dylantestbot;


import battlecode.common.*;

public class Util {

    public static void checkForVPWin(RobotController rc) throws GameActionException {
        float costVP = rc.getVictoryPointCost();
        float currentBullets = rc.getTeamBullets();
        int currentVP = rc.getTeamVictoryPoints();
        int VPsToVictory = 1000 - currentVP;
        float canBuyVPs = currentBullets / costVP;

        if(canBuyVPs >= VPsToVictory){
            rc.donate(currentBullets);
        }
    }

    public static void buyVPsForRound(RobotController rc) throws GameActionException {
        float costVP = rc.getVictoryPointCost();
        float currentBullets = rc.getTeamBullets();
        float bulletsToSpend = (currentBullets - 1000);
        float toBuy = (float) Math.floor(bulletsToSpend / costVP);
        if (toBuy > 0) {
            rc.donate(toBuy);
        }
    }

    public static void checkForTreesToShake(RobotController rc) throws GameActionException {
        TreeInfo[] shakeableTrees = rc.senseNearbyTrees(GameConstants.INTERACTION_DIST_FROM_EDGE + rc.getType().bodyRadius);
        for (TreeInfo tree : shakeableTrees) {
            if (tree.containedBullets > 0) {
                rc.shake(tree.getID());
                break;
            }
        }
    }

    public static Direction[] getSpawnDirections(int spread) {
        Direction[] dirs = new Direction[spread];
        for(int i = 0; i < spread; i++){
            dirs[i] = new Direction((float) (i * 360.0/spread));
        }
        return dirs;
    }

    public static Direction[] getMoveAwayFromDirections(RobotController rc, MapLocation toMoveAwayFrom) {
        Direction dirAway = toMoveAwayFrom.directionTo(rc.getLocation());
        Direction[] dirsAway = new Direction[19];
        dirsAway[0] = dirAway;
        for (int i = 0; i < 9; i++) {
            int indexRight = 2*i + 1;
            int indexLeft = 2*i + 2;
            dirsAway[indexRight] = dirAway.rotateRightDegrees((i+1)*10f);
            dirsAway[indexLeft] = dirAway.rotateLeftDegrees((i+1)*10f);
        }
        return dirsAway;
    }

    public static RobotInfo getClosestPartner(RobotController rc) {
        RobotInfo[] nearbyPartners = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam());
        float closestDist = 1000000;
        RobotInfo nearestPartner = null;
        for (RobotInfo partner : nearbyPartners) {
            float distAway = rc.getLocation().distanceTo(partner.getLocation());
            if (distAway < closestDist){
                closestDist = distAway;
                nearestPartner = partner;
            }
        }
        return nearestPartner;
    }

}
