package yourmom;

import battlecode.common.GameConstants;

public enum ChannelType {
	// robots occupy the first 30 * 10 channels
        // for broadcasting move-out
        MOVE_OUT (300),
        
        // retreat channel
        RETREAT_CHANNEL (999),

		// kill channel
		ENEMY_KILL_CHANNEL (100),

		// number of channels
		NUM_CHANNELS (GameConstants.BROADCAST_MAX_CHANNELS);

		private final int val;
		ChannelType(int x) { val = x; }

		public int getValue() { return val; }
        
        public static final int size = ChannelType.values().length;
        public static final int range = GameConstants.BROADCAST_MAX_CHANNELS / size;
}
