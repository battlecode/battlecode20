package battlecode.world;

import battlecode.common.GameConstants;
import battlecode.common.Team;
import java.util.*;

/**
 * This class is used to hold information regarding team specific values such as
 * team names, and victory points.
 */
public class TeamInfo {

    private int[] teamSoup = new int[2];

    public TeamInfo() {
        Arrays.fill(teamSoup, GameConstants.INITIAL_SOUP);
    }

    // *********************************
    // ***** GETTER METHODS ************
    // *********************************

    // Breaks if t.ordinal() > 1 (Team NEUTRAL)
    public int getSoup(Team t) {
        return teamSoup[t.ordinal()];
    }

    // *********************************
    // ***** UPDATE METHODS ************
    // *********************************

    public void adjustSoup(Team t, int amount) {
        teamSoup[t.ordinal()] += amount;
    }
}
