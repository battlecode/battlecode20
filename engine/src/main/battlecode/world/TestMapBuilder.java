package battlecode.world;

import battlecode.common.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lets maps be built easily, for testing purposes.
 */
public class TestMapBuilder {
    private String name;
    private MapLocation origin;
    private int width;
    private int height;
    private int seed;
    private int rounds;
    private int[] soupArray;
    private int[] pollutionArray;
    private int[] waterArray;

    private List<RobotInfo> bodies;

    public TestMapBuilder(String name, int oX, int oY, int width, int height, int seed, int rounds) {
        this(name, new MapLocation(oX, oY), width, height, seed, rounds);
    }

    public TestMapBuilder(String name, MapLocation origin, int width, int height, int seed, int rounds) {
        this.name = name;
        this.origin = origin;
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.bodies = new ArrayList<>();
    }

    public TestMapBuilder addRobot(int id, Team team, RobotType type, MapLocation loc){
        bodies.add(new RobotInfo(
                id,
                team,
                type,
                loc,
                0,
                0
        ));

        return this;
    }
    
    public TestMapBuilder setSoup() {
        this.soupArray = new int[width*height];
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.soupArray[i + j * width] = i * j + i + j;
            }
        }
        return this;
    }
    public TestMapBuilder setPollution() {
        this.pollutionArray = new int[width*height];
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.pollutionArray[i + j * width] = i * j + j;
            }
        }
        return this;
    }
    public TestMapBuilder setWater() {
        this.waterArray = new int[width*height];
        for(int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.waterArray[i + j * width] = i * i + j * j + j - i;
            }
        }
        return this;
    }

    public TestMapBuilder addBody(RobotInfo info) {
        bodies.add(info);

        return this;
    }

    public LiveMap build() {
        return new LiveMap(
                width, height, origin, seed, GameConstants.GAME_DEFAULT_ROUNDS, name,
                bodies.toArray(new RobotInfo[bodies.size()]), soupArray, pollutionArray, waterArray
        );
    }
}
