package hardplayer.goal;

import hardplayer.BasePlayer;
import hardplayer.Static;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class ArchonExploreGoal extends Static implements Goal {

	static public MapLocation target;

	public int maxPriority() {
		return EXPLORE;
	}

	public int priority() {
		return EXPLORE;
	}

	public void debug_printTarget() {
		myRC.setIndicatorString(1,java.util.Arrays.toString(myRC.senseCapturablePowerNodes()));
		debug_setIndicatorStringObject(2,target);
	}

	public void chooseTarget() {
		int d, dmin = 99999;
		debug_printTarget();
		MapLocation [] adjacent = myRC.senseCapturablePowerNodes();
		for(MapLocation l : adjacent) {
			//if(l.equals(target))
			//	return;
			d = myLoc.distanceSquaredTo(l);
			if(d<dmin) {
				d=dmin;
				target=l;
			}
		}
	}

	public void moveToTarget() {
		moveAdjacentTo(target);
	}

	public void spreadOut() {
		MapLocation archon = closest(myRC.senseAlliedArchons());
		if(myLoc.distanceSquaredTo(archon)>52)
			myNav.moveToASAPPreferFwd(archon);
		else
			myNav.moveToASAP(awayFrom(myLoc,archon));
	}

	public void execute() {
		chooseTarget();
		int dist = myLoc.distanceSquaredTo(target); 
		for(MapLocation l : myRC.senseAlliedArchons()) {
			if(l.distanceSquaredTo(target)<dist) {
				spreadOut();
				return;
			}
		}
		//debug_setIndicatorStringObject(0,target);
		//debug_setIndicatorStringObject(1,myLoc);
		if(myLoc.distanceSquaredTo(target)<=36&&enemies.size<0) {
			// if we can see the target and we don't see enemies,
			// it's probably safe
			moveToTarget();
			return;
		}
		RobotInfo info;
		int closer = 0;
		int dx = target.x - myLoc.x;
		int dy = target.y - myLoc.y;
		int d, myd = dx * myLoc.x + dy * myLoc.y;
		int i;
		MapLocation loc;
		for(i=allies.size;i>=0;i--) {
			info = alliedInfos[i];
			loc = info.location;
			d = dx * loc.x + dy*loc.y;
			if(d>myd) {
				closer += threatWeights[info.type.ordinal()];
				if(closer>=2) {
					moveToTarget();
					return;
				}
			}
		}
	}

}
