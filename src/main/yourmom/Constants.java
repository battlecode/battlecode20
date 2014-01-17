package yourmom;

import battlecode.common.Direction;

/**
 * Various magic numbers and useful constants brought into static space
 */
public final class Constants {

	/** Reverse ordinal mappings */
	public static final Direction[] directions = Direction.values();

	/** Numbers of rounds of not seeing an enemy before resetting targets */
	public static final int ENEMY_SPOTTED_SIGNAL_TIMEOUT = 50;

	/** Rounds before the end of the game to go into emergency endgame cap mode */
	public static final int ENDGAME_CAP_MODE_BUFFER = 2000;
	
}
