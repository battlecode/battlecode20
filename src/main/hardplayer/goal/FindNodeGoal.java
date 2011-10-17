package hardplayer.goal;

import hardplayer.Static;

import battlecode.common.MapLocation;

public class FindNodeGoal extends Static implements Goal {

	public int maxPriority() { return FIND_NODE; }

	public int priority() { return FIND_NODE; }

	public void execute() {
		MapLocation target = closest(myRC.senseAdjacentPowerNodes());
		if(target!=null)
			myNav.moveToForward(target);
	}

}
