package hardplayer.goal;

import hardplayer.BasePlayer;
import hardplayer.Static;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotLevel;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class ArchonExploreGoal extends Static implements Goal {

	static public MapLocation closest;
	static public MapLocation farthest;
	static public MapLocation target;

	public int maxPriority() {
		return EXPLORE;
	}

	public int priority() {
		if(enemies.size>=0) {
			target = null;
			return 0;
		}
		chooseTarget();
		int d = myLoc.distanceSquaredTo(closest);
		int i;
		for(i=alliedArchons.size;i>=0;i--) {
			if(closest.distanceSquaredTo(alliedArchonInfos[i].location)<d) {
				target = farthest;
				return EXPLORE;
			}
		}
		target = closest;
		return EXPLORE;
	}

	public void debug_printTarget() {
		myRC.setIndicatorString(1,java.util.Arrays.toString(myRC.senseCapturablePowerNodes()));
		debug_setIndicatorStringObject(2,target);
	}

	public void chooseTarget() {
		int d, dmin = 99999, dmax = 0;
		debug_printTarget();
		MapLocation [] adjacent = myRC.senseCapturablePowerNodes();
		for(MapLocation l : adjacent) {
			//if(l.equals(target))
			//	return;
			d = myLoc.distanceSquaredTo(l);
			if(d<dmin) {
				dmin=d;
				closest=l;
			}
			if(d>dmax) {
				dmax=d;
				farthest=l;
			}

		}
	}

	public void moveToTarget() {
		try {
			if(myLoc.isAdjacentTo(target))
				myRC.setDirection(myLoc.directionTo(target));
			else
				moveAdjacentTo(target);
		} catch(Exception e) {
			debug_stackTrace(e);
		}
		//myRC.setIndicatorString(2,"TARGET");
	}

	public void spreadOut() {
		MapLocation archon = closestAtLeastDist(myRC.senseAlliedArchons(),1);
		if(myLoc.distanceSquaredTo(archon)>20)
			myNav.moveToASAPPreferFwd(archon);
		else
			myNav.moveToASAP(awayFrom(myLoc,archon));
		//myRC.setIndicatorString(2,"SPREAD");
	}

	public void execute() {
		//chooseTarget();
		int dist = myLoc.distanceSquaredTo(target); 
		//debug_setIndicatorStringObject(1,target);
		for(MapLocation l : myRC.senseAlliedArchons()) {
			if((!l.equals(myLoc))&&l.distanceSquaredTo(target)<dist) {
				spreadOut();
				return;
			}
		}
		moveToTarget();
		//debug_setIndicatorStringObject(0,target);
		//debug_setIndicatorStringObject(1,myLoc);
		/*
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
		*/
	}

}
