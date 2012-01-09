package hardplayer.goal;

import battlecode.common.Clock;
import battlecode.common.MapLocation;

import hardplayer.Static;

public class StayTogetherGoal extends Static implements Goal {

	public static MapLocation target;

	public static int nextCallThreshold;

	public int maxPriority() {
		return STAY_TOGETHER_HIGH;
	}

	public int priority() {
		if(myTeam==battlecode.common.Team.A)
			return 0;
		int round = Clock.getRoundNum();
		if(round<nextCallThreshold) {
			return STAY_TOGETHER_HIGH;
		}
		int closestFarAwayDist = 99999;
		int numFarAway = 0;
		int i, d;
		for(MapLocation l : archons) {
			d = myLoc.distanceSquaredTo(l);
			if(d>169) {
				numFarAway++;
				if(d<closestFarAwayDist) {
					closestFarAwayDist = d;
					target = l;
				}
					
			}
		}
		if(numFarAway>archons.length/2) {
			if(closestFarAwayDist < 169)
				nextCallThreshold = round + 40;
			else if(closestFarAwayDist < 324)
				nextCallThreshold = round + 80;
			else
				nextCallThreshold = round + 120;
			return STAY_TOGETHER_HIGH;
		}
		return 0;
	}

	public void execute() {
		myNav.moveToASAP(target);
	}

}
