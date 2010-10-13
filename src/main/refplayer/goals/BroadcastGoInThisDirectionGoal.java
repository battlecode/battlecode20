package refplayer.goals;

import refplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;

public class BroadcastGoInThisDirectionGoal extends Goal {

	ArchonExploreGoal exploreGoal;

	static public final int CHECK_EVERY = 10;
	int nextBroadcast;

	public BroadcastGoInThisDirectionGoal(BasePlayer bp, ArchonExploreGoal g) {
		super(bp);
		exploreGoal = g;
	}

	public int getMaxPriority() {
		return BROADCAST_GO_IN_THIS_DIRECTION;
	}

	public int getPriority() {
		if(player.atWar) return NEVER;
		int t=Clock.getRoundNum();
		if(t>nextBroadcast) {
			int i;
			for(i=player.alliedArchons.size-1;i>=0;i--) {
				if(player.alliedArchonRobots[i].getID()<player.myID) {
					nextBroadcast=t+CHECK_EVERY;
					return NEVER;
				}
			}
			return BROADCAST_GO_IN_THIS_DIRECTION;
		}
		return NEVER;
	}

	public void tryToAccomplish() {
		player.mySender.sendGoInThisDirection(exploreGoal.dx,exploreGoal.dy);
		nextBroadcast=Clock.getRoundNum()+CHECK_EVERY;
	}

}