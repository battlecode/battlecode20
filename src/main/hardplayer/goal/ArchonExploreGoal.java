package hardplayer.goal;

import hardplayer.BasePlayer;
import hardplayer.Static;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class ArchonExploreGoal extends Static implements Goal {

	public static int dx, dy;

	int stuckTimeout;
	int numTurnsAdvanceFailed;
	static public final int UNSTUCK_TIME = 25;
	static public final int MAX_FAILURE_TURNS = 30;

	public ArchonExploreGoal() {
		dx = 6;
		dy = 6;
	}

	public int maxPriority() {
		return EXPLORE;
	}

	public int priority() {
		return EXPLORE;
	}

	public void execute() {
		int x = myLoc.getX(), y = myLoc.getY();
		if(myRC.senseTerrainTile(new MapLocation(x+dx,y)).getType() == TerrainTile.OFF_MAP)
			dx=-dx;
		if(myRC.senseTerrainTile(new MapLocation(x,y+dy)).getType() == TerrainTile.OFF_MAP)
			dy=-dy;
		int t = Clock.getRoundNum();
		if(allies.size<15) {
			// don't go looking for trouble until we have a big army
			MapLocation nearbyArchonLoc = nearestAlliedArchonAtLeastDist(4);
			if(nearbyArchonLoc!=null)
				myNav.moveToForward(nearbyArchonLoc);
			return;
		}
		if(t>stuckTimeout) {
			int myPos = myLoc.getX()*dx+myLoc.getY()*dy;
			int i;
			int d, dmin=0x7FFFFFFF;
			int numArmyInFront=0;
			MapLocation loc, closestLowEnergon=null;
			RobotInfo info;
			for(i=allies.size;i>=0;i--) {
				info=alliedInfos[i];
				if(info.type==RobotType.ARCHON) continue;
				if(info.location.x*dx+info.location.y*dy>myPos)
					numArmyInFront++;
				if(info.flux<5.) {
					d=myLoc.distanceSquaredTo(info.location);
					if(d<dmin) {
						dmin=d;
						closestLowEnergon=info.location;
					}
				}
			}
			if(numArmyInFront<15) {
					if(closestLowEnergon!=null&&dmin>2) {
						myNav.moveToBackward(closestLowEnergon);
						numTurnsAdvanceFailed+=7;
					}
					else
						numTurnsAdvanceFailed++;
					if(numTurnsAdvanceFailed>MAX_FAILURE_TURNS)
						stuckTimeout=t+UNSTUCK_TIME;
					return;
			}
		}
		myNav.moveToForward(new MapLocation(x+dx,y+dy));
		numTurnsAdvanceFailed=0;
	}

}
