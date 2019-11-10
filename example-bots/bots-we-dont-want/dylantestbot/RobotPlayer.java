package dylantestbot;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;

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
            case ARCHON:
                run(new Archon(rc));
                break;
            case GARDENER:
                run(new Gardner(rc));
                break;
            case LUMBERJACK:
                run(new Lumberjack(rc));
                break;
            case SOLDIER:
                run(new Soldier(rc));
                break;
            case TANK:
                run(new Tank(rc));
                break;
            case SCOUT:
                run(new Scout(rc));
                break;
        }
	}

    static void run(Robot robot) throws GameActionException {
        // The code you want your robot to perform every round should be in this loop
        while (true) {
            try {
                robot.runRound();
                Clock.yield();
            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
}
