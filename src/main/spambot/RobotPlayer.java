package spambot;
import battlecode.common.*;

public class RobotPlayer {
    public static void run(RobotController rc) throws GameActionException {
        switch (rc.getType()) {
            case ARCHON:
                runArchon(rc);
                break;
            case GARDENER:
                runGardener(rc);
                break;
            case SOLDIER:
                runSoldier(rc);
                break;

        }
    }

    public static void runArchon(RobotController rc) throws GameActionException {
        while (true) {
            MapLocation core = getCore(rc);
            
            RobotInfo[] robots = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam());
            boolean nearbyGardener = false;

            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.GARDENER)
                    nearbyGardener = true;
            }

            if (!nearbyGardener && rc.hasRobotBuildRequirements(RobotType.GARDENER) && rc.isBuildReady())
                for (int i = 0; i < 5; i++) {
                    Direction dir = randDir();
                    if (rc.canBuildRobot(RobotType.GARDENER, dir)) {
                        rc.buildRobot(RobotType.GARDENER, dir);
                        break;
                    }
                }

            randMove(rc, core);
            
            Clock.yield();
        }
    }

    public static void runGardener(RobotController rc) throws GameActionException {
        while (true) {
            MapLocation core = getCore(rc);

            TreeInfo[] nearbyTrees = rc.senseNearbyTrees(rc.getType().sensorRadius, rc.getTeam());
            int nearbyTreeCount = nearbyTrees.length;
            
            RobotInfo[] robots = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam());
            boolean nearbyTank = false;

            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.SOLDIER)
                    nearbyTank = true;
            }

            if (!nearbyTank && rc.hasRobotBuildRequirements(RobotType.SOLDIER) && rc.isBuildReady())
                for (int i = 0; i < 5; i++) {
                    Direction dir = randDir();
                    if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                        rc.buildRobot(RobotType.SOLDIER, dir);
                        break;
                    }
                }

            if (nearbyTreeCount < 2 && rc.hasTreeBuildRequirements() && rc.isBuildReady())
                for (int i = 0; i < 5; i++) {
                    Direction dir = randDir();
                    if (rc.canPlantTree(dir)) {
                        rc.plantTree(dir);
                        break;
                    }
                }

            for (TreeInfo tree : nearbyTrees)
                if (rc.canInteractWithTree(tree.getLocation())) {
                    rc.water(tree.getLocation());
                    break;
                }
            
            randMove(rc, core);

            Clock.yield();
        }
    }
    
    public static void runSoldier(RobotController rc) throws GameActionException {
        Direction dir = Direction.getEast();
        int cooldown = 0;

        int moveCooldown = 0;
        
        while (true) {
            RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
            
            if (enemies.length > 0) {
                dir = rc.getLocation().directionTo(enemies[0].getLocation());
                cooldown = 5;

                if (rc.canPentadShot()) {
                    rc.firePentadShot(dir);
                    moveCooldown = 2;
                }
                
            } else if (cooldown > 0 && moveCooldown <= 0) {
                moveTowards(rc, dir);
                cooldown--;
            } else if (moveCooldown <= 0) {
                randMove(rc);
            }

            moveCooldown--;

            Clock.yield();
        }
    }
    
    public static Direction randDir() {
        return new Direction((float) (Math.random() * 2 * Math.PI));
    }

    public static void randMove(RobotController rc) throws GameActionException {
        for (int i = 0; i < 5; i++) {
            Direction dir = randDir();
            if (rc.canMove(dir)) {
                rc.move(dir);
                return;
            }
        }
    }

    public static void randMove(RobotController rc, MapLocation core) throws GameActionException {
        for (int i = 0; i < 5; i++) {
            Direction dir = randDir();
            if (rc.canMove(dir) && rc.getLocation().add(dir, rc.getType().strideRadius).distanceTo(core) < 10) {
                rc.move(dir);
                return;
            }
        }
    }

    public static void moveTowards(RobotController rc, Direction dir) throws GameActionException {
        if (rc.canMove(dir)) {
            rc.move(dir);
            return;
        }

        for (int i = 0; i < 3; i++) {
            Direction dir2 = dir.rotateLeftDegrees((float) (Math.random()*Math.PI/2 - Math.PI/4));
            if (rc.canMove(dir2)) {
                rc.move(dir2);
                return;
            }
        }

        for (int i = 0; i < 3; i++) {
            Direction dir2 = dir.rotateLeftDegrees((float) (Math.random()*Math.PI - Math.PI/2));
            if (rc.canMove(dir2)) {
                rc.move(dir2);
                return;
            }
        }
    }
            

    public static MapLocation getCore(RobotController rc) throws GameActionException {
        int coreX = rc.readBroadcast(0);
        int coreY = rc.readBroadcast(1);

        if (coreX == 0 && coreY == 0) {
            MapLocation core = rc.getLocation();
            rc.broadcast(0, (int) core.x + 100);
            rc.broadcast(1, (int) core.y + 100);

            return core;
        }

        return new MapLocation(coreX - 100, coreY - 100);
    }
}
