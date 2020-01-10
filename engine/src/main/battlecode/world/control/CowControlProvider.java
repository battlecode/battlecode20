package battlecode.world.control;

import battlecode.common.*;
import battlecode.server.ErrorReporter;
import battlecode.server.Server;
import battlecode.world.GameWorld;
import battlecode.world.InternalRobot;

import java.util.*;

/**
 * TODO: either have cows increase pollution here, or have something global that increases pollution based on cows
 * in some other file
 */
public class CowControlProvider implements RobotControlProvider {

    /**
     * The directions a cow cares about.
     */
    private final Direction[] DIRECTIONS = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST
    };

    /**
     * The types & order to spawn cows in.
     */
    private final RobotType COW_TYPE = RobotType.COW;

    /**
     * The world we're operating in.
     */
    private GameWorld world;


    /**
     * An rng based on the world seed.
     */
    private static Random random;


    /**
     * Create a CowControlProvider.
     */
    public CowControlProvider() {
    }

    @Override
    public void matchStarted(GameWorld world) {
        assert this.world == null;

        this.world = world;
        this.random = new Random(world.getMapSeed());
    }

    @Override
    public void matchEnded() {
        assert this.world != null;

        this.world = null;
        this.random = null;
        //this.denQueues.clear();
    }

    @Override
    public void roundStarted() {}

    @Override
    public void roundEnded() {}

    @Override
    public void robotSpawned(InternalRobot robot) {
    }

    @Override
    public void robotKilled(InternalRobot robot) {}

    @Override
    public void runRobot(InternalRobot robot) {
        if (robot.getType() == COW_TYPE) {
            processCow(robot);
        } else {
            // We're somehow controlling a non-cow robot.
            // ...
            // just do nothing lol, this will never happen
        }
    }

    private void processCow(InternalRobot cow) {
        assert cow.getType() == COW_TYPE;
        final RobotController rc = cow.getController();

        try {
            if (rc.isReady()) {
                int i = 4;
                while (i-->0) { 
                    Direction dir = randomDirection();
                    if (rc.canMove(dir) && !world.isFlooded(rc.adjacentLocation(dir))) {
                        rc.move(dir);
                        break;
                    }
                }
                return;
            }
        } catch (Exception e) {
            ErrorReporter.report(e, true);
        }
    }

    Direction randomDirection() {
        return DIRECTIONS[(int) (random.nextDouble() * (double) DIRECTIONS.length)];
    }

    @Override
    public int getBytecodesUsed(InternalRobot robot) {
        // Cows don't think.
        return 0;
    }

    @Override
    public boolean getTerminated(InternalRobot robot) {
        // Cows never terminate due to computation errors.
        return false;
    }
}