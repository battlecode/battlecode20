package battlecode.world.control;

import battlecode.common.*;
import battlecode.server.ErrorReporter;
import battlecode.server.Server;
import battlecode.world.GameWorld;
import battlecode.world.InternalRobot;

import java.util.*;

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

    private enum MapSymmetry {rotational, horizontal, vertical};

    /**
     * The symmetry of the world.
     */
    private MapSymmetry s;


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
        this.s = getSymmetry();
        //System.out.println("symmetry is " + this.s + "!!!");
    }

    @Override
    public void matchEnded() {
        assert this.world != null;

        this.world = null;
        this.random = null;
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
                    Direction dir = DIRECTIONS[(int) (random.nextDouble() * (double) DIRECTIONS.length)];
                    MapLocation loc = cow.getLocation();
                    if (cow.getId() % 2 == 1) dir = reverseDirection(dir);
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

    private Direction reverseDirection(Direction dir) {
        //todo: reverse
        return dir;
    }

    private MapSymmetry getSymmetry() {

        ArrayList<MapSymmetry> possible = new ArrayList<MapSymmetry>();
        possible.add(MapSymmetry.vertical); 
        possible.add(MapSymmetry.horizontal);
        possible.add(MapSymmetry.rotational);

        for (int x = 0; x < world.getGameMap().getWidth(); x++) {
            for (int y = 0; y < world.getGameMap().getHeight(); y++) {
                MapLocation current = new MapLocation(x, y);
                InternalRobot bot = world.getRobot(current);
                RobotInfo cri = null;
                if(bot != null)
                    cri = bot.getRobotInfo();
                for (int i = 2; i >= 0; i--) {
                    MapSymmetry symmetry = possible.get(i);
                    MapLocation symm = new MapLocation(symmetricX(x, symmetry), symmetricY(y, symmetry));
                    if (world.getSoup(current) != world.getSoup(symm)) possible.remove(symmetry);
                    bot = world.getRobot(symm);
                    RobotInfo sri = null;  
                    if (bot != null) sri = bot.getRobotInfo();
                    if (!(cri == null) || !(sri == null)) {
                        if (cri == null && sri == null) {
                            possible.remove(symmetry);
                        };
                        if (cri.getType() != sri.getType())
                            possible.remove(symmetry);

                    }
                }
                if (possible.size() == 1) break;
            }
            if (possible.size() == 1) break;
        }

        return possible.get(0);
    }

    public int symmetricY(int y, MapSymmetry symmetry) {
        switch (symmetry) {
            case vertical:
                return y;
            case horizontal:
            case rotational:
            default:
                return world.getGameMap().getHeight() - 1 - y;
        }
    }

    public int symmetricX(int x, MapSymmetry symmetry) {
        switch (symmetry) {
            case horizontal:
                return x;
            case vertical:
            case rotational:
            default:
                return world.getGameMap().getWidth() - 1 - x;
        }
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