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

	/**
	 * Broadcasting
	 */
	// for channels that are used for flagging (like ChannelType.ARTILLERY_SEEN)
	public static final int TRUE = 263;
	
	// for retreat channel
	public static final int RETREAT = 1543;
	
	// number of redundant channels we use for communication
	public static final int REDUNDANT_CHANNELS = 2;
	
	// how frequently we change the channels we use for broadcasting
	// var = n means channel will cycle every n turns
	// IMPORTANT: YOU MUST RUN THE MAIN FUNCTION IN BROADCASTSYSTEM.JAVA IF YOU CHANGE THIS CONSTANT
	// IMPORTANT: ADDITIONALLY, MAKE SURE TO ADJUST MAX_PRECOMPUTED_ROUNDS SO JAVA DOES NOT COMPLAIN
	public static final int CHANNEL_CYCLE = 17;        
	
	// the maximum number of precomputed rounds of channels (if too high, Java will spit out wrong numbers)
	public static final int MAX_PRECOMPUTED_ROUNDS = 2500;
	
	// used primarily for broadcasting in EncampmentJobSystem (resetting channels)
	public static final int MAX_MESSAGE = 0xFFFFFF;
	
}
