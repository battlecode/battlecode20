package dylantestbot;


import battlecode.common.*;

public class Util {

    public static void checkForVPWin(RobotController rc) throws GameActionException {
        float costVP = rc.getVictoryPointCost();
        float currentBullets = rc.getTeamBullets();
        int currentVP = rc.getTeamVictoryPoints();
        int VPsToVictory = 1000 - currentVP;
        float canBuyVPs = currentBullets / costVP;

        if(rc.getRoundNum() == rc.getRoundLimit()-1){
            rc.donate(currentBullets);
        }
        if(canBuyVPs >= VPsToVictory){
            rc.donate(currentBullets);
        }
    }

    public static void buyVPsForRound(RobotController rc) throws GameActionException {
        float costVP = rc.getVictoryPointCost();
        float currentBullets = rc.getTeamBullets();
        float bulletsToSpend = (currentBullets - 1000);
        int toBuy = (int) Math.floor(bulletsToSpend / costVP);
        if (toBuy > 0) {
            rc.donate(toBuy*costVP);
        }
        if (rc.getRoundNum() >= 1000){
            currentBullets = rc.getTeamBullets();
            bulletsToSpend = (currentBullets);
            toBuy = (int) Math.floor(bulletsToSpend / costVP);
            if (toBuy > 0) {
                rc.donate(toBuy*costVP);
            }
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

    public static void moveAwayFrom(RobotController rc, MapLocation toMoveAwayFrom) throws GameActionException {
        if (!rc.hasMoved()){
            Direction[] dirs = getMoveAwayFromDirections(rc, toMoveAwayFrom);
            for (Direction dir: dirs){
                if (rc.canMove(dir)){
                    rc.move(dir);
                    break;
                }
            }
        }
    }

    public static Direction[] getMoveTowardsDirections(RobotController rc, MapLocation toMoveTowards) {
        Direction dirAway = rc.getLocation().directionTo(toMoveTowards);
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

    public static void moveTowards(RobotController rc, MapLocation toMoveTowards) throws GameActionException {
        if (!rc.hasMoved()){
            Direction[] dirs = getMoveTowardsDirections(rc, toMoveTowards);
            for (Direction dir: dirs){
                if (rc.canMove(dir)){
                    rc.move(dir);
                    break;
                }
            }
        }
    }

    public static void runFromClosestEnemy(RobotController rc) throws GameActionException {
        if(!rc.hasMoved()){
            RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
            float closestDist = 1000000;
            RobotInfo nearestEnemy = null;
            for (RobotInfo enemy : nearbyEnemies) {
                if(enemy.getType() == RobotType.ARCHON || enemy.getType() == RobotType.GARDENER){
                    continue;
                }
                float distAway = rc.getLocation().distanceTo(enemy.getLocation());
                if (distAway < closestDist){
                    closestDist = distAway;
                    nearestEnemy = enemy;
                }
            }
            if (nearestEnemy != null){
                moveAwayFrom(rc, nearestEnemy.getLocation());
            }
        }
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


    public static boolean willCollideWithMe(RobotController rc, BulletInfo bullet) {
        MapLocation nextBulletPosition = bullet.getLocation().add(bullet.getDir(), bullet.getSpeed());
        return rc.getLocation().distanceTo(nextBulletPosition) <= rc.getType().bodyRadius;
    }

    public static boolean dodge(RobotController rc) throws GameActionException {
        BulletInfo[] bullets = rc.senseNearbyBullets();
        for (BulletInfo bullet : bullets) {
            if (willCollideWithMe(rc, bullet)) {
                Direction bulletDirection = bullet.dir;
                Direction directionToMe = bullet.location.directionTo(rc.getLocation());

                Direction dodgeDirection;
                if (bulletDirection.radians < directionToMe.radians) {
                    dodgeDirection = bulletDirection.rotateLeftDegrees(90);
                } else {
                    dodgeDirection = bulletDirection.rotateRightDegrees(90);
                }

                if (rc.canMove(dodgeDirection)) {
                    rc.move(dodgeDirection);
                    return true;
                }
            }
        }
        return false;
    }

}
