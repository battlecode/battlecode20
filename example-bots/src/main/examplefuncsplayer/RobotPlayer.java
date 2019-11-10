package examplefuncsplayer;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
                                         RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        turnCount = 0;

        System.out.println("I'm a " + rc.getType() + "!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                switch (rc.getType()) {
                    case HQ:                 runHQ();                break;
                    case MINER:              runMiner();             break;
                    case REFINERY:           runRefinery();          break;
                    case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:      runDesignSchool();      break;
                    case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         runLandscaper();        break;
                    case DRONE:              runDrone();             break;
                    case NET_GUN:            runNetGun();            break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
	}

    static void runHQ() throws GameActionException {
        for (Direction dir : directions)
            tryBuild(RobotType.MINER, dir);
    }

    static void runMiner() throws GameActionException {
        tryBlockchain();
        tryMove(randomDirection());
        tryBuild(randomSpawnedByMiner(), randomDirection());
    }

    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[10];
            for (int i = 0; i < 10; i++) {
                message[i] = 123;
            }
            rc.sendMessage(message, 10);
        }
    }

    static void runRefinery() throws GameActionException {

    }

    static void runVaporator() throws GameActionException {
        
    }

    static void runDesignSchool() throws GameActionException {
        
    }

    static void runFulfillmentCenter() throws GameActionException {
        
    }

    static void runLandscaper() throws GameActionException {
        
    }

    static void runDrone() throws GameActionException {
        
    }

    static void runNetGun() throws GameActionException {
        
    }

    // static void runArchon() throws GameActionException {
    //     System.out.println("I'm an archon!");

    //     // The code you want your robot to perform every round should be in this loop
    //     while (true) {

    //         // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
    //         try {

    //             // Generate a random direction
    //             Direction dir = randomDirection();

    //             // Randomly attempt to build a gardener in this direction
    //             if (rc.canHireGardener(dir) && Math.random() < .01) {
    //                 rc.hireGardener(dir);
    //             }

    //             // Move randomly
    //             tryMove(randomDirection());

    //             // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
    //             Clock.yield();

    //         } catch (Exception e) {
    //             System.out.println("Archon Exception");
    //             e.printStackTrace();
    //         }
    //     }
    // }

	// static void runGardener() throws GameActionException {
 //        System.out.println("I'm a gardener!");

 //        // The code you want your robot to perform every round should be in this loop
 //        while (true) {

 //            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
 //            try {
 //                // Listen for home archon's location
 //                // archonLoc = rc.getLocation();

 //                // Generate a random direction
 //                Direction dir = randomDirection();

 //                // Randomly attempt to build a soldier or lumberjack in this direction
 //                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
 //                    rc.buildRobot(RobotType.SOLDIER, dir);
 //                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
 //                    rc.buildRobot(RobotType.LUMBERJACK, dir);
 //                }

 //                // Move randomly
 //                tryMove(randomDirection());

 //                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
 //                Clock.yield();

 //            } catch (Exception e) {
 //                System.out.println("Gardener Exception");
 //                e.printStackTrace();
 //            }
 //        }
 //    }

    // static void runSoldier() throws GameActionException {
    //     System.out.println("I'm an soldier!");
    //     Team enemy = rc.getTeam().opponent();

    //     // The code you want your robot to perform every round should be in this loop
    //     while (true) {

    //         // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
    //         try {
    //             MapLocation myLocation = rc.getLocation();

    //             // See if there are any nearby enemy robots
    //             RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

    //             // Move randomly
    //             tryMove(randomDirection());

    //             // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
    //             Clock.yield();

    //         } catch (Exception e) {
    //             System.out.println("Soldier Exception");
    //             e.printStackTrace();
    //         }
    //     }
    // }

    // static void runLumberjack() throws GameActionException {
    //     System.out.println("I'm a lumberjack!");
    //     Team enemy = rc.getTeam().opponent();

    //     // The code you want your robot to perform every round should be in this loop
    //     while (true) {

    //         // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
    //         try {

    //             // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
    //             RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);

    //             if(robots.length > 0 && !rc.hasAttacked()) {
    //                 // Use strike() to hit all nearby robots!
    //                 rc.strike();
    //             } else {
    //                 // No close robots, so search for robots within sight radius
    //                 robots = rc.senseNearbyRobots(-1,enemy);

    //                 // If there is a robot, move towards it
    //                 if(robots.length > 0) {
    //                     MapLocation myLocation = rc.getLocation();
    //                     MapLocation enemyLocation = robots[0].getLocation();
    //                     Direction toEnemy = myLocation.directionTo(enemyLocation);

    //                     tryMove(toEnemy);
    //                 } else {
    //                     // Move Randomly
    //                     tryMove(randomDirection());
    //                 }
    //             }

    //             // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
    //             Clock.yield();

    //         } catch (Exception e) {
    //             System.out.println("Lumberjack Exception");
    //             e.printStackTrace();
    //         }
    //     }
    // }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }
}
