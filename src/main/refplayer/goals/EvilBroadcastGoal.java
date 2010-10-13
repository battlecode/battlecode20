package refplayer.goals;

import refplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;

public class EvilBroadcastGoal extends Goal {

	public EvilBroadcastGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return BROADCAST_EVIL;
	}

	public int getPriority() {
		return BROADCAST_EVIL;
	}

	public void tryToAccomplish() {
		player.evilSender.send();
	}

}