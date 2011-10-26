package hardplayer.goal;

import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import hardplayer.Static;

public class SoldierAttackGoal extends Static implements Goal {
	
	static public final int [] priorities = new int [] { -100, 0, 0, 0, 0, 0 };

	public int maxPriority() { return ATTACK; }

	public int priority() {
		if(enemies.size>=0)
			return ATTACK;
		else
			return 0;
	}

	public void execute() {
		int i;
		int value, bestv = 99999;
		RobotInfo info, best = null;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			value = priorities[info.type.ordinal()]+myLoc.distanceSquaredTo(info.location);
			if(value<bestv) {
				best = info;
				bestv = value;
			}
		}
		if(best!=null) {
			try {
				int d = myLoc.distanceSquaredTo(best.location);
				if(d>=RobotType.SOLDIER.attackRadiusMaxSquared)
					myNav.moveToForward(best.location);
				else if(d>0)
					myRC.setDirection(myLoc.directionTo(best.location));
			} catch(Exception e) {
				debug_stackTrace(e);
			}
		}
	}

}
