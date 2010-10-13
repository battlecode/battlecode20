package teleportingplayer.goals;

import teleportingplayer.BasePlayer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class MakeChainGoal extends Goal {

	MapLocation loc;

	public MakeChainGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return MAKE_CHAIN;
	}

	public int getPriority() {
		if(player.alliedTeleporters.size>0) {
			loc = player.nearestOneOf(player.alliedTeleporters).location;
			if(player.myLoc.distanceSquaredTo(loc)>=16)
				return MAKE_CHAIN;
		}
		return NEVER;
	}

	public void tryToAccomplish() {
		Direction dir = loc.directionTo(player.myLoc);
		int n = dir.isDiagonal()?5:3;
		player.myNav.moveToASAP(BasePlayer.multipleAddDirection(loc,dir.rotateRight(),n));
	}

}