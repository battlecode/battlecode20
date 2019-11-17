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

    /** The initial amount of soup each team gets. */
    int INITIAL_SOUP = 1000;

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

    /** The radius that delivery drones can pick up in */
    int DELIVERY_DRONE_PICKUP_RADIUS = 2;

    // *********************************
    // ****** MISCELLANEOUS ************
    // *********************************

    /** The maximum number of integers that can be sent in one message. */
    int MAX_BLOCKCHAIN_MESSAGE_LENGTH = 10;

    /** The number of messages that get broadcasted every turn. */
    int NUMBER_OF_BROADCASTED_MESSAGES = 10;
    
    // *********************************
    // ****** GAMEPLAY PROPERTIES ******
    // *********************************

    /** The default game seed. **/
    int GAME_DEFAULT_SEED = 6370;

    /** The default game maxiumum number of rounds. **/
    int GAME_DEFAULT_ROUNDS = 3000;
}
