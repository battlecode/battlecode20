package refplayer.goals;

import refplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotType;

public class StayTogetherGoal extends Goal {
	
	public MapLocation targetLoc;
	
	public int nextCallThreshold;

	public StayTogetherGoal(BasePlayer ap) {
		super(ap);
		nextCallThreshold = 0;
	}

	public int getMaxPriority() {
		return STAY_TOGETHER_VERY_HIGH;
	}

	public int getPriority() {
		int curRound = Clock.getRoundNum();
		if (curRound < nextCallThreshold) {
			return STAY_TOGETHER_VERY_HIGH;
		}
		MapLocation [] archons = myRC.senseAlliedArchons();
		if(2*(player.alliedArchons.size+1) < archons.length) {
			MapLocation myLoc = player.myLoc;
			MapLocation farthest = null;
			MapLocation closest = null;
			int i, d, dmin = 999999, dmax=36;
			for(i = archons.length-1; i>=0; i--) {
				d = myLoc.distanceSquaredTo(archons[i]);
				if(d>36&&d<dmin) {
					dmin=d;
					closest=archons[i];
				}
				if(d>dmax) {
					dmax=d;
					farthest=archons[i];
				}
			}
			if(dmin>121&&closest!=null) {
				targetLoc = closest;
				if(dmin < 169) {
					nextCallThreshold = curRound + 40;
				}
				else if (dmin < 324) {
					nextCallThreshold = curRound + 80;
				}
				else {
					nextCallThreshold = curRound + 120;
				}
				return STAY_TOGETHER_VERY_HIGH;
			}
			if(dmax>225&&farthest!=null) {
				targetLoc = farthest;
				nextCallThreshold = curRound + 30;
				return STAY_TOGETHER_HIGH;
			}
			if(closest!=null) targetLoc = closest;
			return STAY_TOGETHER_MEDIUM;
		}
		else {
			return NEVER;
		}
	}

	public void tryToAccomplish() {
		player.myNav.moveToForward(targetLoc);
	}

}