package hardplayer.goal;

import battlecode.common.PowerNode;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

import hardplayer.Static;

public class SoldierAttackGoal extends Static implements Goal {
	
	static public final int [] priorities = new int [] { -100, 0, 0, 0, 0, 0 };

	public int maxPriority() { return ATTACK; }

	static RobotInfo target;

	public int priority() {
		int i;
		int value, bestv = 99999;
		RobotInfo info;
		target = null;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			if((info.robot instanceof PowerNode) && !myRC.senseConnected((PowerNode)info.robot))
				continue;
			value = priorities[info.type.ordinal()]+myLoc.distanceSquaredTo(info.location);
			if(value<bestv) {
				target = info;
				bestv = value;
			}
		}
		if(target!=null)
			return ATTACK;
		else
			return 0;
	}

	public void execute() {

		if(target!=null) {
			try {
				int d = myLoc.distanceSquaredTo(target.location);
				if(d>=RobotType.SOLDIER.attackRadiusMaxSquared)
					myNav.moveToForward(target.location);
				else if(d>0)
					myRC.setDirection(myLoc.directionTo(target.location));
			} catch(Exception e) {
				debug_stackTrace(e);
			}
		}
	}

}
