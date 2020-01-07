package battlecode.world;

import java.io.IOException;

/**
 * Generate a map.
 */
public class BuildMaps {

    // change this!!!
    public static final String[] mapNames = {"ALandDivided",
    "CentralLake",
    "CentralSoup",
    "FourLakeLand",
    "SoupOnTheSide",
    "TwoForOneAndTwoForAll",
    "WaterBot"};

    // don't change this!!
    public static final String outputDirectory = "engine/src/main/battlecode/world/resources/";

    /**
     * @param args unused
     */
    public static void main(String[] args) {
        for (String mapName : mapNames)
        System.out.println("Generated a map!");
    }

}
