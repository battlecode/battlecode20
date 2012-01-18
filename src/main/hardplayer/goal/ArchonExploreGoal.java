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

	static public MapLocation target;
	static public MapLocation backup;
	static public boolean millAround;

	public int maxPriority() {
		return EXPLORE;
	}

	public int priority() {
		/*
		if(enemies.size>=0) {
			target = null;
			return 0;
		}
		*/
		return EXPLORE;
	}

	public static void debug_printTarget() {
		myRC.setIndicatorString(1,java.util.Arrays.toString(myRC.senseCapturablePowerNodes()));
		debug_setIndicatorStringObject(2,target);
	}

	public static void chooseTarget() {
		int i, d, dmin = 99999, dmax = 99999;
		boolean backupLegal = false;
		//debug_printTarget();
		MapLocation [] adjacent = myRC.senseCapturablePowerNodes();
		for(MapLocation l : adjacent) {
			//if(l.equals(target))
			//	return;
			d = myLoc.distanceSquaredTo(l);
			if(d<dmin) {
				dmin=d;
				target=l;
			}
			if(l.equals(backup))
				backupLegal = true;

		}
		millAround = false;
		for(i=alliedArchons.size;i>=0;i--) {
			if(target.distanceSquaredTo(alliedArchonInfos[i].location)<dmin) {
				if(!backupLegal)
					backup = adjacent[nextInt()%adjacent.length];
				millAround = target.equals(backup);
				target = backup;
				return;
			}
		}
	}

	public void moveToTarget() {
		try {
			if(millAround) {
				myNav.moveToASAPPreferFwd(target.add(target.directionTo(myLoc).rotateLeft(),5));
			}
			else {
				if(myLoc.isAdjacentTo(target))
					myRC.setDirection(myLoc.directionTo(target));
				else
					moveAdjacentTo(target);
			}
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
		chooseTarget();
		int d = myLoc.distanceSquaredTo(target);
		int i;

		int dist = myLoc.distanceSquaredTo(target); 
		moveToTarget();
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
