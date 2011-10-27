package hardplayer.goal;

import hardplayer.BasePlayer;
import hardplayer.Static;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class ArchonExploreGoal extends Static implements Goal {

	static MapLocation target;

	public int maxPriority() {
		return EXPLORE;
	}

	public int priority() {
		return EXPLORE;
	}

	public void chooseTarget() {
		int d, dmin = 99999;
		MapLocation [] adjacent = myRC.senseCapturablePowerNodes();
		for(MapLocation l : adjacent) {
			if(l.equals(target))
				return;
			d = myLoc.distanceSquaredTo(l);
			if(d<dmin) {
				d=dmin;
				target=l;
			}
		}
	}

	public void execute() {
		chooseTarget();
		debug_setIndicatorStringObject(0,target);
		debug_setIndicatorStringObject(1,myLoc);
		int i, d = myLoc.distanceSquaredTo(target);
		RobotInfo info;
		int closer = 0;
		for(i=allies.size;i>=0;i--) {
			info = alliedInfos[i];
			if(info.location.distanceSquaredTo(target)<d) {
				closer += threatWeights[info.type.ordinal()];
				if(closer>=2) {
					myNav.moveToASAPPreferFwd(target);
					return;
				}
			}
		}
		MapLocation archon = closest(myRC.senseAlliedArchons());
		if(myLoc.distanceSquaredTo(archon)>52)
			myNav.moveToASAPPreferFwd(archon);
		else
			myNav.moveToASAP(awayFrom(myLoc,archon));
	}

}
