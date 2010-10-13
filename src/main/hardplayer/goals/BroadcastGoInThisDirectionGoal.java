package hardplayer.goals;

import hardplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;

public class BroadcastGoInThisDirectionGoal extends Goal {

	ArchonExploreGoal exploreGoal;
	FindEnemyGoal findEnemyGoal;

	static public final int CHECK_EVERY = 10;
	int nextBroadcast;

	public BroadcastGoInThisDirectionGoal(BasePlayer bp, ArchonExploreGoal g, FindEnemyGoal f) {
		super(bp);
		exploreGoal = g;
		findEnemyGoal = f;
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
				if(player.alliedArchonInfos[i].id<player.myID) {
					nextBroadcast=t+CHECK_EVERY;
					return NEVER;
				}
			}
			return BROADCAST_GO_IN_THIS_DIRECTION;
		}
		return NEVER;
	}

	public void tryToAccomplish() {
		//if(player.atWar) {
		//	player.mySender.sendGoInThisDirection(findEnemyGoal.getEnemyDX(),findEnemyGoal.getEnemyDY());
		//}
		//else {
		player.mySender.sendGoInThisDirection(exploreGoal.dx,exploreGoal.dy);
		//}
		nextBroadcast=Clock.getRoundNum()+CHECK_EVERY;
	}

}