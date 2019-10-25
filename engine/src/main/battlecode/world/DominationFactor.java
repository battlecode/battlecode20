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
     * Win by soup count + value of units.
     */
    WON_BY_NET_WORTH,  
    /**
     * Win by robot count.
     */
    WON_BY_ROBOT_COUNT,
    /**
     * Win by more transactions in the blockchain.
     */
    WON_BY_BLOCKCHAIN,
}
