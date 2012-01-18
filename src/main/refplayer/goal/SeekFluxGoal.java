package refplayer.goal;

import refplayer.Static;

import battlecode.common.MapLocation;

public class SeekFluxGoal extends Static implements Goal {

	public int maxPriority() { return SEEK_FLUX_LOW; }

	public int priority() { return SEEK_FLUX_LOW; }

	public void execute() {
		MapLocation target = closest(myRC.senseAlliedArchons());
		if(target!=null&&myLoc.distanceSquaredTo(target)>2)
			myNav.moveToBackward(target);
	}

}
