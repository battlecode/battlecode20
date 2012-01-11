package basicplayer.goal;

import battlecode.common.*;

import basicplayer.Static;

public class GetHelpGoal extends Static implements Goal {

	static MapLocation target;

	public int maxPriority() {
		return GET_HELP;
	}

	public int priority() {
		if(alliedSoldiers.size+alliedScorchers.size+alliedDisrupters.size>=
			enemySoldiers.size+enemyScorchers.size+enemyDisrupters.size)
			return 0;
		target = nearestAlliedArchonAtLeastDist(21);
		if(target==null)
			return 0;
		//if(myLoc.distanceSquaredTo(target)<=20)
		//	return 0;
		else
			return GET_HELP;
	}

	public void execute() {
		myNav.moveToASAP(target);
	}

}
