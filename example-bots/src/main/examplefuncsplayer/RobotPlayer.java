package examplefuncsplayer;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case HQ:
                runHQ();
                break;
            case MINER:
                runMiner();
                break;
        }
	}

    static void runHQ() throws GameActionException {
        System.out.println("I'm an HQ!");
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                for (Direction dir : directions)
                    if (rc.canBuildRobot(RobotType.MINER, dir))
                        rc.buildRobot(RobotType.MINER, dir);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("HQ Exception");
                e.printStackTrace();
            }
        }
    }

    static void runMiner() throws GameActionException {
        System.out.println("I'm a Miner!");
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                for (Direction dir : directions)
                    if (rc.canBuildRobot(RobotType.MINER, dir))
                        rc.buildRobot(RobotType.MINER, dir);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Miner Exception");
                e.printStackTrace();
            }
        }
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
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * 4)];
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
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
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeft())) {
                rc.move(dir.rotateLeft());
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRight())) {
                rc.move(dir.rotateRight());
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }
}
