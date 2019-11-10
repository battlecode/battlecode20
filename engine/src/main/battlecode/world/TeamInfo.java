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

    private int[] teamSoup = new int[2];

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
