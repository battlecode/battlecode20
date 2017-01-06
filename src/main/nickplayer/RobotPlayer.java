package nickplayer;
import battlecode.common.*;

public class RobotPlayer {
    static RobotController rc;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    public static void run(RobotController rc) throws GameActionException {
        RobotPlayer.rc = rc;
        switch (rc.getType()) {
            case ARCHON:
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
            case SOLDIER:
                runSoldier();
                break;
            case TANK:
                runTank();
                break;
            case SCOUT:
                runScout();
        }
	}

	static void runGardener() throws GameActionException {
        boolean firstGardener = rc.getRoundNum() < 10;
        MapLocation myLocation = rc.getLocation();
        MapLocation homeLocation = null;

        int roundNum = rc.getRoundNum();
        int numScoutsAlive = 0;
        int desiredScouts = 2;

        //MapLocation homeLocation = readLocation(RadioChannels.GARDENER_LOC);

        System.out.println("I'm a gardener! First gardener "+firstGardener+" \n, starting out at location "+myLocation);

        Direction toCenter = null;

        if(firstGardener) {
            RobotInfo[] nearbyRobots = rc.senseNearbyRobots(2);
            // First one should just be the archon
            MapLocation archonLocation=nearbyRobots[0].getLocation();
            toCenter = archonLocation.directionTo(myLocation);
            if(rc.canBuildRobot(RobotType.SCOUT,toCenter)){
                rc.buildRobot(RobotType.SCOUT,toCenter);
            }
            homeLocation = myLocation.add(toCenter.rotateLeftDegrees(45),5);
            System.out.println("First gardener's home location: "+homeLocation);
        }

        while (true) {
            roundNum = rc.getRoundNum();
            myLocation = rc.getLocation();

            // Service scout blip and build a new one if necessary
            if(roundNum % 15 == 1) {
                numScoutsAlive = rc.readBroadcast(RadioChannels.SCOUT_ALIVE_BLIP);
                if(numScoutsAlive < desiredScouts) {
                    for (int i = 0; i < 8; i++) {
                        Direction testDirection = toCenter.rotateRightDegrees(i * 360 / 8);
                        if (rc.canBuildRobot(RobotType.SCOUT, testDirection)) {
                            rc.buildRobot(RobotType.SCOUT, testDirection);
                            rc.broadcast(RadioChannels.SCOUT_ALIVE_BLIP, numScoutsAlive + 1);
                        }
                    }
                }
            } else if (roundNum % 15 == 2) {
                numScoutsAlive = rc.readBroadcast(RadioChannels.SCOUT_ALIVE_BLIP);
                if(numScoutsAlive > 0) {
                    // Reset to zero for next count
                    rc.broadcast(RadioChannels.SCOUT_ALIVE_BLIP,0);
                }
            }

            float distFromHome = myLocation.distanceTo(homeLocation);
            if (distFromHome > 0.5) {
                tryMove(myLocation.directionTo(homeLocation));
            } else {
                // Plant trees at home
                if (rc.canPlantBulletTree(Direction.getWest())) {
                    rc.plantBulletTree(Direction.getWest());
                } else if (rc.canPlantBulletTree(Direction.getEast())) {
                    rc.plantBulletTree(Direction.getEast());
                }

            }

            // Water trees
            TreeInfo[] trees = rc.senseNearbyTrees(2,rc.getTeam());
            for(TreeInfo tree:trees) {
                if(tree.health < 9 && rc.canWater()) {
                    rc.water(tree.ID);
                    break;
                }
            }

            Direction dir = Direction.getNorth();//new Direction((float) Math.random() * 2 * (float) Math.PI);
            if (rc.canBuildRobot(RobotType.TANK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                rc.buildRobot(RobotType.TANK, dir);
            }

            Clock.yield();
        }
    }

	static void runArchon() throws GameActionException {
        System.out.println("I'm an archon!");
        int desiredGardeners = 0;
        int currentGardeners = 0;

        MapLocation myLocation = rc.getLocation();
        Direction toCenter = null; // Will represent direction to what we think is the center of the map

        boolean isKing = false; // Which archon controls them all?
        int kingId = rc.readBroadcast(RadioChannels.KING_ID);
        if(kingId == 0) {
            isKing = true;
        }

        /*
        if(isKing) {
            rc.broadcast(RadioChannels.HOME_X, (int) myLocation.x);
            rc.broadcast(RadioChannels.HOME_Y, (int) myLocation.y);
            rc.broadcast(RadioChannels.KING_ID, rc.getID());
        }
        */

        // Identify walls of the map if nearby. Goalpoint will be average away from map walls.

        MapLocation goalPoint = myLocation;
        for(int i=0; i<4; i++) {
            Direction testDirection = Direction.getNorth().rotateRightDegrees(90*i);
            if(rc.onTheMap(myLocation.add(testDirection, RobotType.ARCHON.sensorRadius))) {
                goalPoint = goalPoint.add(testDirection);
            }
        }
        toCenter = myLocation.directionTo(goalPoint);
        if(toCenter == null) {
            toCenter = Direction.getNorth();
        }

        // Spawn a gardener pointing toward this center direction

        if(rc.canBuildRobot(RobotType.GARDENER,toCenter)) {
            rc.buildRobot(RobotType.GARDENER,toCenter);
        }

        // First, build a gardener to build a scout



        while (true) {
            Direction dir = new Direction((float) Math.random() * 2 * (float) Math.PI);
            myLocation = rc.getLocation();
            if (currentGardeners < desiredGardeners && rc.canBuildRobot(RobotType.GARDENER, dir) && rc.isBuildReady()) {
                MapLocation gardenerLoc = rc.getLocation().add(Direction.getEast(),10*(currentGardeners+1));
                sendLocation(RadioChannels.GARDENER_LOC,gardenerLoc);
                rc.buildRobot(RobotType.GARDENER, dir);
                currentGardeners++;
            }




            Clock.yield();
        }
    }

    static void runSoldier() throws GameActionException {
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam() == Team.A ? Team.B : Team.A;
        while (true) {

            RobotInfo[] robots = rc.senseNearbyRobots(100, enemy);
            if (robots.length > 0) {
                if (rc.getTeamBullets() >= 4) {
                    //System.out.println("FIRING");
                    rc.fireTriadShot(rc.getLocation().directionTo(robots[0].location));
                }
                boolean foundArchon = false;
                for(RobotInfo robot:robots) {
                    if(robot.type == RobotType.ARCHON) {
                        sendLocation(RadioChannels.ENEMY_ATTACK_LOC,robot.getLocation());
                        rc.broadcast(RadioChannels.ENEMY_ATTACK_VALID,1);
                        foundArchon = true;
                    }
                }
                if (!foundArchon && rc.readBroadcast(RadioChannels.ENEMY_ATTACK_VALID) == 0) {
                    sendLocation(RadioChannels.ENEMY_ATTACK_LOC,robots[0].getLocation());
                    rc.broadcast(RadioChannels.ENEMY_ATTACK_VALID,1);
                }
            } else if(rc.readBroadcast(RadioChannels.ENEMY_ATTACK_VALID) == 1) {
                MapLocation attackLoc = readLocation(RadioChannels.ENEMY_ATTACK_LOC);
                if(rc.getLocation().distanceTo(attackLoc) < RobotType.SOLDIER.sensorRadius) {
                    robots = rc.senseNearbyRobots(attackLoc, 4, enemy);
                    if (robots.length == 0)
                        rc.broadcast(RadioChannels.ENEMY_ATTACK_VALID, 0);
                }
                tryMove(rc.getLocation().directionTo(attackLoc));
            } else {
                moveRandom();
            }


            Clock.yield();
        }
    }

    static void runTank() throws GameActionException {
        System.out.println("I'm an tank!");
        Team enemy = rc.getTeam() == Team.A ? Team.B : Team.A;
        while (true) {

            RobotInfo[] robots = rc.senseNearbyRobots(100, enemy);
            if (robots.length > 0) {
                if (rc.getTeamBullets() >= 4) {
                    //System.out.println("FIRING with "+rc.getTeamBullets()+" bullets left.");
                   // try {
                        rc.fireTriadShot(rc.getLocation().directionTo(robots[0].location));
                   // } catch (GameActionException e) {
                   //     e.printStackTrace();
                   // }
                }
                boolean foundArchon = false;
                for(RobotInfo robot:robots) {
                    if(robot.type == RobotType.ARCHON) {
                        sendLocation(RadioChannels.ENEMY_ATTACK_LOC,robot.getLocation());
                        rc.broadcast(RadioChannels.ENEMY_ATTACK_VALID,1);
                        foundArchon = true;
                    }
                }
                if (!foundArchon && rc.readBroadcast(RadioChannels.ENEMY_ATTACK_VALID) == 0) {
                    sendLocation(RadioChannels.ENEMY_ATTACK_LOC,robots[0].getLocation());
                    rc.broadcast(RadioChannels.ENEMY_ATTACK_VALID,1);
                }
            } else if(rc.readBroadcast(RadioChannels.ENEMY_ATTACK_VALID) == 1) {
                MapLocation attackLoc = readLocation(RadioChannels.ENEMY_ATTACK_LOC);
                if(rc.getLocation().distanceTo(attackLoc) < RobotType.SOLDIER.sensorRadius) {
                    robots = rc.senseNearbyRobots(attackLoc, 4, enemy);
                    if (robots.length == 0)
                        rc.broadcast(RadioChannels.ENEMY_ATTACK_VALID, 0);
                }
                tryMove(rc.getLocation().directionTo(attackLoc));
            } else {
                moveRandom();
            }


            Clock.yield();
        }
    }

    static void runScout() throws GameActionException {
        System.out.println("I'm a scout!");
        RobotInfo[] nearbyRobots = rc.senseNearbyRobots(4);
        MapLocation gardenerLoc = nearbyRobots[0].getLocation();
        MapLocation myLocation = rc.getLocation();
        MapLocation homeLocation = myLocation;
        Direction toCenter = gardenerLoc.directionTo(homeLocation);
        Team enemy = rc.getTeam() == Team.A ? Team.B : Team.A;

        // Survey continuously and point out interesting things
        Direction currentDirection = toCenter;
        MapLocation enemyAttackLocation = null;
        int turnsAttackValid = 0;

        while(true) {
            RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1,enemy);
            myLocation = rc.getLocation();

            if(nearbyEnemies.length > 0) {
                // Report location and attack location
                // System.out.println("Enemy spotted!");
                enemyAttackLocation = nearbyEnemies[0].getLocation();
                sendLocation(RadioChannels.ENEMY_ATTACK_LOC,enemyAttackLocation);
                rc.broadcast(RadioChannels.ENEMY_ATTACK_VALID,1);
                turnsAttackValid = 20;
                currentDirection = currentDirection.opposite();
            }

            if(turnsAttackValid > 0) {
                if(rc.getTeamBullets() >= 1) {
                    rc.fireSingleShot(myLocation.directionTo(enemyAttackLocation));
                }
                turnsAttackValid--;
            }

            if(!tryMove(currentDirection)) {
               currentDirection = new Direction((float)Math.random() * 2 * (float)Math.PI);
            }

            if(rc.getRoundNum()%15 == 0) {
                int numScouts = rc.readBroadcast(RadioChannels.SCOUT_ALIVE_BLIP);
                rc.broadcast(RadioChannels.SCOUT_ALIVE_BLIP,numScouts+1);
            }


            Clock.yield();
        }
    }

    static void moveRandom() throws GameActionException {
        Direction dir = new Direction((float)Math.random() * 2 * (float)Math.PI);
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }

    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {
        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    static void sendLocation(int startChannel, MapLocation loc) throws GameActionException {
        rc.broadcast(startChannel,(int)(loc.x*100));
        rc.broadcast(startChannel+1,(int)(loc.y*100));
    }

    static MapLocation readLocation(int startChannel) throws GameActionException {
        float x = ((float)rc.readBroadcast(startChannel))/100f;
        float y = ((float)rc.readBroadcast(startChannel+1))/100f;
        return new MapLocation(x,y);
    }
}
