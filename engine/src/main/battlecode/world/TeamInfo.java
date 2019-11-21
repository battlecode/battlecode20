package battlecode.world;

import battlecode.common.GameConstants;
import battlecode.common.Team;
import java.util.*;

/**
 * This class is used to hold information regarding team specific values such as
 * team names, and victory points.
 */
public class TeamInfo {

    private GameWorld gameWorld;
    private int[] teamSoup;

    public TeamInfo(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.teamSoup = new int[2];
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

    public void addSoupIncome(int amount) {
        adjustSoup(Team.A, amount);
        adjustSoup(Team.B, amount);
    }

    public void adjustSoup(Team t, int amount) {
        this.teamSoup[t.ordinal()] += amount;
        this.gameWorld.getMatchMaker().addTeamStat(t, amount);
    }
}
