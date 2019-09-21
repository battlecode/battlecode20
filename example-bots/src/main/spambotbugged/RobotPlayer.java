package spambotbugged;
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

            // Check if there are nearby gardeners
            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.GARDENER)
                    nearbyGardener = true;
            }

            // Try to build a gardener
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
            boolean nearbySoldier = false;

            for (RobotInfo robot : robots) {
                if (robot.type == RobotType.SOLDIER)
                    nearbySoldier = true;
            }

            if (!nearbySoldier && rc.hasRobotBuildRequirements(RobotType.SOLDIER) && rc.isBuildReady())
                for (int i = 0; i < 5; i++) {
                    Direction dir = randDir();
                    if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                        rc.buildRobot(RobotType.SOLDIER, dir);
                    }
                }

            if (nearbyTreeCount < 2 && rc.hasTreeBuildRequirements() && rc.isBuildReady())
                for (int i = 0; i < 5; i++) {
                    Direction dir = randDir();
                    if (rc.canPlantTree(dir)) {
                        rc.plantTree(dir);
                    }
                }

            for (TreeInfo tree : nearbyTrees) {
                rc.water(tree.getLocation());
            }
            
            randMove(rc, core);

            Clock.yield();
        }
    }
    
    public static void runSoldier(RobotController rc) throws GameActionException {
        Direction dir = Direction.getEast();

        // Length of time to spend pursuing an enemy
        int cooldown = 0;

        // Don't move for a bit after firing to avoid running into own bullets
        int moveCooldown = 0;
        
        while (true) {
            RobotInfo[] enemies = rc.senseNearbyRobots(rc.getType().sensorRadius, rc.getTeam().opponent());
            
            if (enemies.length > 0) {
                dir = rc.getLocation().directionTo(enemies[0].getLocation());
                cooldown = 5;

                if (rc.canFirePentadShot()) {
                    rc.firePentadShot(dir);
                    moveCooldown = 2;
                }
                
            } else if (cooldown > 0 && moveCooldown <= 0) {
                moveTowards(rc, dir);
                cooldown--;
            } 

            if (moveCooldown <= 0) {
                randMove(rc);
            }

            moveCooldown--;

            Clock.yield();
        }
    }
    
    public static Direction randDir() {
        return new Direction((float) (Math.random() * 2 * Math.PI));
    }

    // Move randomly
    public static void randMove(RobotController rc) throws GameActionException {
        for (int i = 0; i < 5; i++) {
            Direction dir = randDir();
            if (rc.canMove(dir)) {
                rc.move(dir);
                return;
            }
        }
    }

    // Move randomly, while trying to stay within a given distance
    // from the core
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
            
    // Returns the "core", or main focal spot for this team to 
    // gather archons/gardeners and plant trees
    public static MapLocation getCore(RobotController rc) throws GameActionException {
        int coreY = rc.readBroadcast(0);
        int coreX = rc.readBroadcast(1);

        if (coreX == 0 && coreY == 0) {
            MapLocation core = rc.getLocation();
            rc.broadcast(0, (int) core.x);
            rc.broadcast(1, (int) core.y);

            return core;
        }

        return new MapLocation(coreX, coreY);
    }
}
