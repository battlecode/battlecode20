package hardplayer.goals;

import hardplayer.BasePlayer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

public class MakeChainGoal extends Goal {

	MapLocation loc;

	public MakeChainGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return MAKE_CHAIN;
	}

	public int getPriority() {
		return MAKE_CHAIN;
	}

	public void tryToAccomplish() {
		if(player.atWar) {
			RobotInfo turret = player.nearestOneOf(player.alliedTurrets);
			if(turret!=null) {
				player.myNav.moveToASAP(turret.location);
				return;
			}
		}
		if(player.alliedComms.size+player.alliedAuras.size>0) {
			RobotInfo info = player.nearestOneOf(player.alliedComms,player.nearestOneOf(player.alliedAuras));
			MapLocation loc = info.location;
			if(player.myLoc.distanceSquaredTo(loc)>=16) {
				Direction dir = loc.directionTo(player.myLoc);
				int n = dir.isDiagonal()?5:3;
				player.myNav.moveToASAP(BasePlayer.multipleAddDirection(loc,dir.rotateRight(),n));
			}
			else {
				try {
					if(myRC.canMove(myRC.getDirection().opposite()))
						myRC.moveBackward();
					else
						myRC.setDirection(myRC.getDirection().rotateLeft());
				} catch(Exception e) {}
			}
		}
		else {
			MapLocation loc = player.nearestAlliedArchonAtLeastDist(3);
			if(loc!=null) player.myNav.moveToASAP(loc);
		}
	}

}