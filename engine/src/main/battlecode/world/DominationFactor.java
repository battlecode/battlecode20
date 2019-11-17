package battlecode.world;

/**
 * Determines roughly by how much the winning team won.
 */
public enum DominationFactor {
    /**
     * Win by highest robot ID (tiebreak 4).
     */
    WON_BY_DUBIOUS_REASONS,
    /**
     * Win by more victory points (tiebreak 1).
     */
    PWNED,
    /**
     * Win by destroying all enemy robots.
     */
    DESTROYED
    /**
     * Won by donating enough currency to reach VICTORY_POINTS_TO_WIN.
     */
    // PHILANTROPIED
}
