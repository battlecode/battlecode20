package battlecode.world;

import battlecode.common.GameConstants;
import battlecode.common.Team;

/**
 * This class is used to hold information regarding team specific values such as
 * team names, and victory points.
 */
public class TeamInfo {

    private final long[][] teamMemory;
    private final long[][] oldTeamMemory;

    private int[] teamVictoryPoints = new int[3];

    public TeamInfo(long[][] oldTeamMemory){
        this.teamMemory = new long[2][oldTeamMemory[0].length];
        this.oldTeamMemory = oldTeamMemory;
    }

    // *********************************
    // ***** GETTER METHODS ************
    // *********************************

    public long[][] getTeamMemory() {
        return teamMemory;
    }

    public long[][] getOldTeamMemory() {
        return oldTeamMemory;
    }

    public int getVictoryPoints(Team t) {
        return teamVictoryPoints[t.ordinal()];
    }

    // *********************************
    // ***** UPDATE METHODS ************
    // *********************************

    public void adjustVictoryPoints(Team t, int amount) {
        teamVictoryPoints[t.ordinal()] += amount;
    }

    public void setTeamMemory(Team t, int index, long state) {
        teamMemory[t.ordinal()][index] = state;
    }

    public void setTeamMemory(Team t, int index, long state, long mask) {
        long n = teamMemory[t.ordinal()][index];
        n &= ~mask;
        n |= (state & mask);
        teamMemory[t.ordinal()][index] = n;
    }
}
