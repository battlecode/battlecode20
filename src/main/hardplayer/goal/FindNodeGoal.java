package hardplayer.goal;

import hardplayer.Static;

import battlecode.common.MapLocation;

public class FindNodeGoal extends Static implements Goal {

	public int maxPriority() { return FIND_NODE; }

	public int priority() {
		if((allies.size>=0||!myRC.canMove(myDir.opposite())))
			return FIND_NODE;
		else
			return 0;
	}

	public void execute() {
		MapLocation target = closest(myRC.senseCapturablePowerNodes());
		if(target!=null)
			myNav.moveToForward(target);
	}

}
