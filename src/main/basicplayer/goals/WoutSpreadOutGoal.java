package basicplayer.goals;

import basicplayer.BasePlayer;

import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

import static battlecode.common.GameConstants.FLUX_RADIUS_SQUARED;

public class WoutSpreadOutGoal extends Goal {

	public WoutSpreadOutGoal(BasePlayer bp) {
		super(bp);
	}

	public int getMaxPriority() {
		return SPREAD_OUT;
	}

	public int getPriority() {
		return SPREAD_OUT;
	}

	public void tryToAccomplish() {
		if(player.alliedWouts.size==0) {
			try {
				if (myRC.canMove(myRC.getDirection())) {
					myRC.moveForward();
				} else {
					myRC.setDirection(myRC.getDirection().rotateRight());
				}
			} catch(Exception e) {
				BasePlayer.debug_stackTrace(e);
			}
			return;
		}
		int i;
		long n=0, xsum=0, ysum=0, w;
		RobotInfo [] infos = player.alliedWoutInfos;
		MapLocation loc, myLoc=player.myLoc;
		int x=myLoc.getX();
		int y=myLoc.getY();
		for(i=player.alliedWouts.size-1;i>=0;i--) {
			loc=infos[i].location;
			w=6000000L/(loc.distanceSquaredTo(myLoc)+FLUX_RADIUS_SQUARED);
			n+=w;
			xsum+=w*loc.getX();
			ysum+=w*loc.getY();
		}
		player.myNav.moveToASAPPreferFwd(BasePlayer.awayFrom(myLoc,new MapLocation((int)(xsum/n),(int)(ysum/n))));
	}

}