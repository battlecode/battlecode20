package hardplayer.goal;

import battlecode.common.Clock;

public class ScoutFindNodeGoal extends FindNodeGoal {

	public int priority() {
		if(!atWar&&Clock.getRoundNum()-requestTime<=10)
			return FIND_NODE;
		else
			return 0;
	}

}
