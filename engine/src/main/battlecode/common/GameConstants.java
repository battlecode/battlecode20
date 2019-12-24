package battlecode.common;

/**
 * Defines constants that affect gameplay.
 */
@SuppressWarnings("unused")
public class GameConstants {

    /**
     * The current spec version the server compiles with.
     */
    public static final String SPEC_VERSION = "1.0";

    // *********************************
    // ****** MAP CONSTANTS ************
    // *********************************

    /** The minimum possible map height. */
    public static final int MAP_MIN_HEIGHT = 32;

    /** The maximum possible map height. */
    public static final int MAP_MAX_HEIGHT = 64;

    /** The minimum possible map width. */
    public static final int MAP_MIN_WIDTH = 32;

    /** The maximum possible map width. */
    public static final int MAP_MAX_WIDTH = 64;

    // *********************************
    // ****** GAME PARAMETERS **********
    // *********************************

    /** The number of indicator strings that a player can associate with a robot. */
    public static final int NUMBER_OF_INDICATOR_STRINGS = 3;

    /** The bytecode penalty that is imposed each time an exception is thrown. */
    public static final int EXCEPTION_BYTECODE_PENALTY = 500;

    /** Maximum ID a Robot will have */
    public static final int MAX_ROBOT_ID = 32000;

    // *********************************
    // ****** SOUP *********************
    // *********************************

    /** The initial amount of soup each team starts off with. */
    public static final int INITIAL_SOUP = 250;

    /** The amount of soup each team receives per turn. */
    public static final int BASE_INCOME_PER_ROUND = 1;

    /** The amount of soup that a miner gets when performing one mine action. */
    public static final int SOUP_MINING_RATE = 5;

    // *********************************
    // ****** POLLUTION ****************
    // *********************************

    /** The coefficient that the sensor radius will be multiplied by, as a function of pollution. */
    public static float getSensorRadiusPollutionCoefficient(int pollution) {
        return (float) Math.max(0, (1.0 - (pollution / 10000.0)));
    }

    // *********************************
    // ****** MOVEMENT *****************
    // *********************************

    /** The maximum difference between dirt levels that a robot can cross. */
    public static final int MAX_DIRT_DIFFERENCE = 3;

    // *********************************
    // ****** ATTACKING ****************
    // *********************************

    /** The radius that delivery drones can pick up. */
    public static final int DELIVERY_DRONE_PICKUP_RADIUS_SQUARED = 2;

    /** The radius that net guns can shoot. */
    public static final int NET_GUN_SHOOT_RADIUS_SQUARED = 3;

    // *********************************
    // ****** BLOCKCHAINNNN ************
    // *********************************

    /** The maximum number of integers that can be sent in one message. */
    public static final int MAX_BLOCKCHAIN_TRANSACTION_LENGTH = 10;

    /** The number of transactions that get broadcasted every round. */
    public static final int NUMBER_OF_TRANSACTIONS_PER_BLOCK = 10;
    
    // *********************************
    // ****** GAMEPLAY PROPERTIES ******
    // *********************************

    /** The default game seed. **/
    public static final int GAME_DEFAULT_SEED = 6370;

    /** The default game maxiumum number of rounds. **/
    public static final int GAME_DEFAULT_ROUNDS = 500;
}
