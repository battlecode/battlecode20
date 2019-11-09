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
    int MAP_MIN_HEIGHT = 30;

    /** The maximum possible map height. */
    int MAP_MAX_HEIGHT = 100;

    /** The minumum possible map width. */
    int MAP_MIN_WIDTH = 30;

    /** The maxiumum possible map width. */
    int MAP_MAX_WIDTH = 100;

    // *********************************
    // ****** GAME PARAMETERS **********
    // *********************************

    /** The number of victory points required to win the game. */
    int VICTORY_POINTS_TO_WIN = 1000;

    /** The number of longs that your team can remember between games. */
    int TEAM_MEMORY_LENGTH = 32;

    /** The number of indicator strings that a player can associate with a robot. */
    int NUMBER_OF_INDICATOR_STRINGS = 3;

    /** The bytecode penalty that is imposed each time an exception is thrown. */
    int EXCEPTION_BYTECODE_PENALTY = 500;

    /** Maximum archons that can appear on a map (per team). */
    int NUMBER_OF_ARCHONS_MAX = 3;

    /** Maximum ID a Robot will have */
    int MAX_ROBOT_ID = 32000;
    
    /**
     * The fraction of max health which gardener-produced robots start at.
     */
    float PLANTED_UNIT_STARTING_HEALTH_FRACTION = 0.2f;

    // *********************************
    // ****** ATTACKING ****************
    // ********************************* 

    /** The radius around a lumberjack affected by a strike(). */
    float LUMBERJACK_STRIKE_RADIUS = 2;

    float DELIVERY_DRONE_PICKUP_RADIUS = 2;

    // *********************************
    // ****** MISCELLANEOUS ************
    // *********************************

    /** The price of 1 victory point at the start of the game */
    float VP_BASE_COST = 7.5f;

    /** The price the victory point cost increases each turn */
    float VP_INCREASE_PER_ROUND = 12.5f / 3000f;

    /**
     * The distance, as measured at its minimum value, between the bodies
     * of a creator robot and the robot it spawns.
     */
    float GENERAL_SPAWN_OFFSET = .01f;

    /** The distance around a robot's edge it can interact with robots */
    float INTERACTION_DIST_FROM_EDGE = 1f;

    /** The maximum radius a robot can have. */
    float MAX_ROBOT_RADIUS = 2;
    
    // *********************************
    // ****** GAMEPLAY PROPERTIES ******
    // *********************************

    /** The default game seed. **/
    int GAME_DEFAULT_SEED = 6370;

    /** The default game maxiumum number of rounds. **/
    int GAME_DEFAULT_ROUNDS = 3000;
}
