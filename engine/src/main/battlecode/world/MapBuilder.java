package battlecode.world;

import battlecode.common.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lets maps be built easily, for testing purposes.
 */
public class MapBuilder {
    public String name;
    public int width;
    public int height;
    public int seed;
    private int[] soupArray;
    private int[] pollutionArray;
    private boolean[] waterArray;
    private int[] dirtArray;
    private int waterLevel;

    private List<RobotInfo> bodies;

    public MapBuilder(String name, int width, int height, int seed) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.bodies = new ArrayList<>();


        this.waterLevel = 0;
        this.soupArray = new int[width*height];
        this.pollutionArray = new int[width*height];
        this.waterArray = new boolean[width*height];
        this.dirtArray = new int[width*height];
    }

    /**
     * Convert location to index. Critical: must conform with GameWorld.indexToLocation.
     * @param x
     * @param y
     * @return
     */
    private int locationToIndex(int x, int y) {
        return x + y * width;
    }

    public void addRobot(int id, Team team, RobotType type, MapLocation loc){
        bodies.add(new RobotInfo(
                id,
                team,
                type,
                loc
        ));
    }

    public void addHQ(int x, int y, int id, Team team) {
        addRobot(
                id,
                team,
                RobotType.HQ,
                new MapLocation(x, y)
        );
    }

    public void addCow(int x, int y, int id) {
        addRobot(
                id,
                Team.NEUTRAL,
                RobotType.COW,
                new MapLocation(x, y)
        );
    }

    public void setSoup(int x, int y, int value) {
        this.soupArray[locationToIndex(x, y)] = value;
    }

    public void setPollution(int x, int y, int value) {
        this.pollutionArray[locationToIndex(x, y)] = value;
    }

    public void setWater(int x, int y, boolean value) {
        this.waterArray[locationToIndex(x, y)] = value;
    }

    public void setDirt(int x, int y, int value) {
        this.dirtArray[locationToIndex(x, y)] = value;
    }

    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public LiveMap build() {
        return new LiveMap(width, height, new MapLocation(0, 0), seed, GameConstants.GAME_DEFAULT_ROUNDS, name,
                bodies.toArray(new RobotInfo[bodies.size()]), soupArray, pollutionArray, waterArray, dirtArray, waterLevel);
    }

    /**
     * Saves the map to the specified location.
     * @param pathname
     * @throws IOException
     */
    public void saveMap(String pathname) throws IOException {
        GameMapIO.writeMap(this.build(), new File(pathname));
    }
}
