package hardplayer.goals;

import hardplayer.BasePlayer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;


public class ChainerTurretExploreGoal extends ExploreGoal {

	public ChainerTurretExploreGoal(BasePlayer bp) {
		super(bp);
	}

	protected boolean anyFightersNearby() {
		if(player.alliedChainers.size+player.alliedSoldiers.size+player.alliedTurrets.size>0) return true;
		Direction dir=zero.directionTo(new MapLocation(dx,dy));
		return !(myRC.canMove(dir.rotateRight().rotateRight())||myRC.canMove(dir.rotateLeft().rotateLeft()));
	}

}