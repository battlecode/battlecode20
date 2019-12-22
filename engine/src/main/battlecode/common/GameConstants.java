package battlecode.common;

/**
 * Defines constants that affect gameplay.
 */
@SuppressWarnings("unused")
public interface GameConstants {

    /**
     * The current spec version the server compiles with.
     */
    String SPEC_VERSION = "1.0";

    // *********************************
    // ****** MAP CONSTANTS ************
    // *********************************

    /** The minimum possible map height. */
    int MAP_MIN_HEIGHT = 32;

    /** The maximum possible map height. */
    int MAP_MAX_HEIGHT = 64;

    /** The minumum possible map width. */
    int MAP_MIN_WIDTH = 32;

    /** The maxiumum possible map width. */
    int MAP_MAX_WIDTH = 64;

    // *********************************
    // ****** GAME PARAMETERS **********
    // *********************************

    /** The initial amount of soup each team starts off with. */
    int INITIAL_SOUP = 250;

    /** The amount of soup each team receives per turn. */
    int BASE_INCOME_PER_ROUND = 1;

    /** The amount of soup that a miner gets when performing one mine action. */
    int SOUP_MINING_RATE = 5;

    /** The number of indicator strings that a player can associate with a robot. */
    int NUMBER_OF_INDICATOR_STRINGS = 3;

    /** The bytecode penalty that is imposed each time an exception is thrown. */
    int EXCEPTION_BYTECODE_PENALTY = 500;

    /** Maximum ID a Robot will have */
    int MAX_ROBOT_ID = 32000;

    // *********************************
    // ****** MOVEMENT *****************
    // *********************************

    /** The maximum difference between dirt levels that a robot can cross. */
    int MAX_DIRT_DIFFERENCE = 3;

    // *********************************
    // ****** ATTACKING ****************
    // *********************************

    /** The radius that delivery drones can pick up. */
    int DELIVERY_DRONE_PICKUP_RADIUS_SQUARED = 2;

    /** The radius that net guns can shoot. */
    int NET_GUN_SHOOT_RADIUS_SQUARED = 3;

    // *********************************
    // ****** BLOCKCHAINNNN ************
    // *********************************

    /** The maximum number of integers that can be sent in one message. */
    int MAX_BLOCKCHAIN_TRANSACTION_LENGTH = 10;

    /** The number of transactions that get broadcasted every round. */
    int NUMBER_OF_TRANSACTIONS_PER_BLOCK = 10;
    
    // *********************************
    // ****** GAMEPLAY PROPERTIES ******
    // *********************************

    /** The default game seed. **/
    int GAME_DEFAULT_SEED = 6370;

    /** The default game maxiumum number of rounds. **/
    int GAME_DEFAULT_ROUNDS = 500;
}
