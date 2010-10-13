package hardplayer.message;

import hardplayer.BasePlayer;

import battlecode.common.Message;

public class BuildingMessageSender extends MessageSender {

	public BuildingMessageSender(BasePlayer bp) {
		super(bp);
	}

	public void send(Message m) {
		if(myRC.getEnergonLevel()>m.getNumBytes()*battlecode.common.GameConstants.BROADCAST_COST_PER_BYTE+battlecode.common.GameConstants.BROADCAST_FIXED_COST)
			super.send(m);
	}
}