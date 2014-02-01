package yourmom;

import battlecode.common.GameConstants;

public enum ChannelType {
	// robots occupy the first 30 * 10 channels
        // for broadcasting move-out
        MOVE_OUT,
        
        // retreat channel
        RETREAT_CHANNEL,

		// pastr channels
		PASTR1,
		PASTR2,
		PASTR3,

		// noisetower channels
		NOISETOWER1,
		NOISETOWER2,
		NOISETOWER3,

		// number of channels
		NUM_CHANNELS;

        public static final int size = ChannelType.values().length;
        public static final int range = GameConstants.BROADCAST_MAX_CHANNELS / size;
}
