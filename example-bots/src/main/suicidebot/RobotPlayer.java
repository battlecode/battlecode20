package suicidebot;
import battlecode.common.*;
import battlecode.world.ObjectInfo;

import java.util.Arrays;

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

        }
    }

    static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");
        while (true) {
            Direction dir = new Direction((float) Math.random() * 2 * (float) Math.PI);
            if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01 && rc.isBuildReady()) {
                rc.buildRobot(RobotType.SOLDIER, dir);
            }
            moveSuicide();
            Clock.yield();
        }
    }

    static void runArchon() throws GameActionException {
        System.out.println("I'm an archon!");
        while (true) {
            Direction dir = new Direction((float) Math.random() * 2 * (float) Math.PI);
            if (rc.canBuildRobot(RobotType.GARDENER, dir) && Math.random() < .01 && rc.isBuildReady()) {
                rc.buildRobot(RobotType.GARDENER, dir);
            }
            moveSuicide();
            Clock.yield();
        }
    }

    static void runSoldier() throws GameActionException {
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam() == Team.A ? Team.B : Team.A;
        while (true) {
            RobotInfo[] robots = rc.senseNearbyRobots(100, enemy);
            if (robots.length > 1) {
                if (rc.getTeamBullets() > 1) {
                    rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                }
            }
            moveSuicide();

            Clock.yield();
        }
    }

    static void moveSuicide() throws GameActionException {
        BulletInfo[] bullets = rc.senseNearbyBullets(100);

        for (BulletInfo bullet : bullets) {
            Direction dir = rc.getLocation().directionTo(bullet.getLocation());
            if (rc.canMove(dir)) {
                rc.move(dir);
                return;
            }
        }

        RobotInfo[] robots = rc.senseNearbyRobots(100,rc.getTeam().opponent());

        for (RobotInfo robot : robots) {
            Direction dir = rc.getLocation().directionTo(robot.getLocation());
            if (rc.canMove(dir)) {
                rc.move(dir);
                return;
            }
        }

        Direction dir = new Direction((float)Math.random() * 2 * (float)Math.PI);
        if (rc.canMove(dir)) {
            rc.move(dir);
        }
    }
}
