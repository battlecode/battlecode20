package hardplayer.goal;

import hardplayer.Static;
import battlecode.common.*;

public class BuildConstructorGoal extends Static implements Goal {

	MapLocation target;

	public int maxPriority() { return BUILD_RECYCLER; }

	public int priority() {
		int i = allies.size, j;
		MapLocation tmpTarget = null;
		RobotInfo info;
		try {
		robots:
		while(--i>=0) {
			info = alliedInfos[i];
			if(info.chassis!=Chassis.BUILDING)
				continue;
			ComponentType [] comp = info.components;
			j = comp.length;
			if(sensor.senseObjectAtLocation(info.location,RobotLevel.MINE)==null)
				continue;
			while(--j>=0) {
				if(comp[j]==ComponentType.RECYCLER)
					continue robots;
			}
			tmpTarget = info.location;
			if(tmpTarget.equals(target))
				return BUILD_RECYCLER;
		}
		if(tmpTarget!=null) {
			target = tmpTarget;
			return BUILD_RECYCLER;
		}
		else
			return 0;
		} catch(Exception e) {
			debug_stackTrace(e);
			return 0;
		}
	}

	public void execute() {
		moveAdjacentTo(target);
	}

}
