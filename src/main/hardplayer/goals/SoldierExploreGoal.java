package hardplayer.goals;

import hardplayer.BasePlayer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;


public class SoldierExploreGoal extends ExploreGoal {

	public SoldierExploreGoal(BasePlayer bp) {
		super(bp);
	}

	protected boolean anyFightersNearby() {
		return player.alliedChainers.size+player.alliedSoldiers.size+player.alliedTurrets.size>0;
	}

}