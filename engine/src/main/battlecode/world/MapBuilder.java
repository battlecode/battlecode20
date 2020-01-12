package battlecode.world;

import battlecode.common.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Build and validate maps easily.
 */
public class MapBuilder {

    public enum MapSymmetry {rotational, horizontal, vertical};

    public String name;
    public int width;
    public int height;
    public int seed;
    private MapSymmetry symmetry;
    private int[] soupArray;
    private int[] pollutionArray;
    private boolean[] waterArray;
    private int[] dirtArray;
    private int waterLevel;
    private int idCounter;

    private List<RobotInfo> bodies;

    public MapBuilder(String name, int width, int height, int seed) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.bodies = new ArrayList<>();

        // default values
        this.symmetry = MapSymmetry.vertical;
        this.waterLevel = 0;
        this.idCounter = 0;
        this.soupArray = new int[width*height];
        this.pollutionArray = new int[width*height];
        this.waterArray = new boolean[width*height];
        this.dirtArray = new int[width*height];
    }




    // ********************
    // BASIC METHODS
    // ********************

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
        // check if something already exists here, if so shout
        for (RobotInfo r : bodies) {
            if (r.location.equals(loc)) {
                throw new RuntimeException("CANNOT ADD ROBOT TO SAME LOCATION AS OTHER ROBOT");
            }
        }
        bodies.add(new RobotInfo(
                id,
                team,
                type,
                loc
        ));
    }

    public void addHQ(int x, int y, Team team) {
        addRobot(
                idCounter++,
                team,
                RobotType.HQ,
                new MapLocation(x, y)
        );
    }

    public void addCow(int x, int y) {
        addRobot(
                idCounter++,
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

    // The water level should always be set to 0!!
    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    public void setSymmetry(MapSymmetry symmetry) {
        this.symmetry = symmetry;
    }


    // ********************
    // SYMMETRY METHODS
    // ********************

    public int symmetricY(int y) {
        switch (symmetry) {
            case vertical:
                return y;
            case horizontal:
            case rotational:
            default:
                return height - 1 - y;
        }
    }

    public int symmetricX(int x) {
        switch (symmetry) {
            case horizontal:
                return x;
            case vertical:
            case rotational:
            default:
                return width - 1 - x;
        }
    }

    /**
     * Add team A HQ to (x,y) and team B HQ to symmetric position.
     * @param x x position
     * @param y y position
     */
    public void addSymmetricHQ(int x, int y) {
        addHQ(x, y, Team.A);
        addHQ(symmetricX(x), symmetricY(y), Team.B);
    }

    public void addSymmetricCow(int x, int y) {
        addCow(x, y);
        addCow(symmetricX(x), symmetricY(y));
    }

    public void setSymmetricSoup(int x, int y, int value) {
        this.soupArray[locationToIndex(x, y)] = value;
        this.soupArray[locationToIndex(symmetricX(x), symmetricY(y))] = value;
    }

    public void setSymmetricPollution(int x, int y, int value) {
        this.pollutionArray[locationToIndex(symmetricX(x), symmetricY(y))] = value;
    }

    public void setSymmetricWater(int x, int y, boolean value) {
        this.waterArray[locationToIndex(x, y)] = value;
        this.waterArray[locationToIndex(symmetricX(x), symmetricY(y))] = value;
    }

    public void setSymmetricDirt(int x, int y, int value) {
        this.dirtArray[locationToIndex(x, y)] = value;
        this.dirtArray[locationToIndex(symmetricX(x), symmetricY(y))] = value;
    }


//    public void addRectangleDirt(int xl, int yb, int xr, int yt, int v) {
//        for (int i = xl; i < xr+1; i++) {
//            for (int j = yb; j < yt+1; j++) {
//                setSymmetricDirt(i, j, v);
//            }
//        }
//    }
//
//    public void addRectangleWater(int xl, int yb, int xr, int yt, int v) {
//        for (int i = xl; i < xr+1; i++) {
//            for (int j = yb; j < yt+1; j++) {
//                setSymmetricWater(i, j, true);
//                setSymmetricDirt(i,j, v);
//            }
//        }
//        setSymmetricDirt((xl + xr)/2, (yb + yt)/2, Integer.MIN_VALUE);
//    }
//    public void addRectangleSoup(int xl, int yb, int xr, int yt, int v) {
//        for (int i = xl; i < xr+1; i++) {
//            for (int j = yb; j < yt+1; j++) {
//                setSymmetricSoup(i, j, v);
//            }
//        }
//    }
//
//
//    /*
//     * Add a nice circular lake centered at (x,y).
//     */
//    public static void addSoup(int x, int y, int r2, int v) {
//        for (int xx = 0; xx < width; xx++) {
//            for (int yy = 0; yy < height; yy++) {
//                int d = (xx-x)*(xx-x)/2 + (yy-y)*(yy-y);
//                if (d <= r2) {
//                    setSymmetricSoup(xx, yy, v*(d+v));
//                }
//            }
//        }
//    }
//
//    /*
//     * Add a nice circular lake centered at (x,y).
//     */
//    public static void addLake(int x, int y, int r2, int v) {
//        for (int xx = 0; xx < width; xx++) {
//            for (int yy = 0; yy < height; yy++) {
//                int d = (xx-x)*(xx-x) + (yy-y)*(yy-y);
//                if (d <= r2) {
//                    setSymmetricWater(xx, yy, true);
//                    setSymmetricDirt(xx, yy, v);
//                }
//            }
//        }
//    }

    // ********************
    // INFORMATION
    // ********************

    public int getTotalSoup() {
        int tot = 0;
        for (int x = 0; x < width; x++)
            for (int y=0;y<height;y++)
                tot += this.soupArray[locationToIndex(x,y)];
        return tot;
    }

    // ********************
    // BUILDING AND SAVING
    // ********************


    public LiveMap build() {
        return new LiveMap(width, height, new MapLocation(0, 0), seed, GameConstants.GAME_MAX_NUMBER_OF_ROUNDS, name,
                bodies.toArray(new RobotInfo[bodies.size()]), soupArray, pollutionArray, waterArray, dirtArray, waterLevel);
    }

    /**
     * Saves the map to the specified location.
     * @param pathname
     * @throws IOException
     */
    public void saveMap(String pathname) throws IOException {
        // validate
        assertIsValid();
        System.out.println("Saving " + this.name + ": has " + Integer.toString(getTotalSoup())+ " total soup.");
        GameMapIO.writeMap(this.build(), new File(pathname));
    }

    /**
     * Returns true if the map is valid.
     *
     * WARNING: DON'T TRUST THIS COMPLETELY.
     * @return
     */
    public void assertIsValid() {

        if (width < 32 || height < 32 || width > 64 || height > 64)
            throw new RuntimeException("The map size must be between 32x32 and 64x64, inclusive.");

        // check HQ effective elevation (floods between 1 and 5)
        ArrayList<MapLocation> HQLocations = new ArrayList<MapLocation>();
        for (RobotInfo r : bodies) {
            if (r.getType() == RobotType.HQ) {
                HQLocations.add(r.getLocation());
            }
        }
        boolean[] waterTestArray = this.waterArray;
        ArrayList<MapLocation> floodOrigins = new ArrayList<MapLocation>();
        for (int i = 0; i < 5; i++) {
            waterLevel++;
            for (int idx = 0; idx < waterTestArray.length; idx++) {
                if (waterTestArray[idx])
                    floodOrigins.add(indexToLocation(idx));
                for (MapLocation center : floodOrigins) {
                    for (Direction dir : Direction.allDirections()) {
                        MapLocation targetLoc = center.add(dir);
                        if (!this.onTheMap(targetLoc))
                            continue;
                        int idx2 = locationToIndex(targetLoc.x, targetLoc.y);
                        if (waterTestArray[idx2] || this.dirtArray[idx2] >= waterLevel)
                            continue;
                        waterTestArray[idx2] = true;
                    }
                }
            }
            for (MapLocation HQLoc : HQLocations) {
                if (waterLevel == 1 && waterTestArray[locationToIndex(HQLoc.x, HQLoc.y)]) {
                    throw new RuntimeException("The HQ must not flood at an effective elevation of 1! It currently floods at 0 or 1.");
                }
                if (waterLevel == 5 && !waterTestArray[locationToIndex(HQLoc.x, HQLoc.y)]) {
                    throw new RuntimeException("The HQ must not flood at an effective elevation between 2-5! It currently does not flood at 5.");
                }
            }
        }

        // check if there is a -inf water tile
        // note: we don't want to use Integer.MIN_VALUE because we then risk underflow
        // we can never build higher than this
        // A good option is Integer.MIN_VALUE / 2.
        int impossibleHeight = 10000000;
        boolean hasMinInfWater = false;
        for (int x = 0; x < width; x++)
            for (int y=0; y < height; y++)
                if (waterArray[locationToIndex(x,y)] && dirtArray[locationToIndex(x,y)] == GameConstants.MIN_WATER_ELEVATION) {
                    hasMinInfWater = true;
                }
        if (!hasMinInfWater)
            throw new RuntimeException("Every map is required to have a water tile with elevation GameConstants.MIN_WATER_ELEVATION. This map does not have that.");

    }
    public boolean onTheMap(MapLocation loc) {
        return loc.x >= 0 && loc.y >= 0 && loc.x < width && loc.y < height;
    }
    public MapLocation indexToLocation(int idx) {
        return new MapLocation(idx % this.width,
                               idx / this.width);
    }
}
