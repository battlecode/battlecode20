package hardplayer.goals;

import hardplayer.BasePlayer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;


public class TurretExploreGoal extends ExploreGoal {

	public TurretExploreGoal(BasePlayer bp) {
		super(bp);
	}

	protected boolean anyFightersNearby() {
		if(player.alliedChainers.size+player.alliedSoldiers.size+player.alliedTurrets.size>0) return true;
		Direction dir=zero.directionTo(new MapLocation(dx,dy));
		return !(myRC.canMove(dir.rotateRight().rotateRight())||myRC.canMove(dir.rotateLeft().rotateLeft()));
	}

	public void tryToAccomplish() {
		MapLocation archonLoc = player.nearestAlliedArchon();
		int d=(player.myLoc.getX()-archonLoc.getX())*dx+(player.myLoc.getY()-archonLoc.getY())*dy;
		if(d<=-12) {
			moveForward();
			return;
		}
		if(d>0) {
			waitForArmy();
			return;
		}
		if(player.alliedChainers.size+player.alliedSoldiers.size+player.alliedTurrets.size>0) {
			moveForward();
			return;
		}
		if(anyFightersNearby())
			moveForward();
		else
			waitForArmy();
	}
}