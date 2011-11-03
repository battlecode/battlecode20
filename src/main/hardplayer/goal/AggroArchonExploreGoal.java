package hardplayer.goal;

import hardplayer.Static;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.TerrainTile;

public class AggroArchonExploreGoal extends Static implements Goal {

	public static int dx=6, dy=6;

	int stuckTimeout;
	int numTurnsAdvanceFailed;
	static public final int UNSTUCK_TIME = 25;
	static public final int MAX_FAILURE_TURNS = 30;

	public int maxPriority() {
		return EXPLORE;
	}

	public int priority() {
		return EXPLORE;
	}

	public void spreadOut() {
		MapLocation archon = closest(myRC.senseAlliedArchons());
		if(myLoc.distanceSquaredTo(archon)>26)
			myNav.moveToASAPPreferFwd(archon);
		else
			myNav.moveToASAP(awayFrom(myLoc,archon));
		//myRC.setIndicatorString(2,"SPREAD");
	}

	public void execute() {
		int x = myLoc.x, y = myLoc.y;
		if(myRC.senseTerrainTile(new MapLocation(x+dx,y)).getType() == TerrainTile.OFF_MAP)
			dx=-dx;
		if(myRC.senseTerrainTile(new MapLocation(x,y+dy)).getType() == TerrainTile.OFF_MAP)
			dy=-dy;
		int t = Clock.getRoundNum();
		int armyWanted=5*(alliedArchons.size+2);
		debug_setIndicatorStringObject(1,armySize());
		if(armySize()<armyWanted) {
			// don't go looking for trouble until we have a big army
			spreadOut();
			/*
			MapLocation nearbyArchonLoc = nearestAlliedArchonAtLeastDist(4);
			if(nearbyArchonLoc!=null)
				myNav.moveToForward(nearbyArchonLoc);
			*/
			return;
		}
		/*
		if(t>stuckTimeout) {
			int myPos = x*dx+y*dy;
			int i;
			int d, dmin=0x7FFFFFFF;
			int numArmyInFront=0;
			MapLocation loc, closestLowFlux=null;
			RobotInfo info;
			for(i=allies.size;i>=0;i--) {
				info=alliedInfos[i];
				if(info.location.x*dx+info.location.y*dy>myPos)
					numArmyInFront+=threatWeights[info.type.ordinal()];
				if(info.flux<30.) {
					d=myLoc.distanceSquaredTo(info.location);
					if(d<dmin) {
						dmin=d;
						closestLowFlux=info.location;
					}
				}
			}
			if(numArmyInFront<armyWanted) {
					if(closestLowFlux!=null&&dmin>2) {
						myNav.moveToBackward(closestLowFlux);
						numTurnsAdvanceFailed+=7;
					}
					else
						numTurnsAdvanceFailed++;
					if(numTurnsAdvanceFailed>MAX_FAILURE_TURNS)
						stuckTimeout=t+UNSTUCK_TIME;
					return;
			}
		}
		*/
		myNav.moveToForward(new MapLocation(x+dx,y+dy));
		numTurnsAdvanceFailed=0;
	}
	
	private static int nextBroadcast;
	public static final int CHECK_EVERY = 10;

	public static boolean broadcast() {
		if(atWar) return false;
		int t = Clock.getRoundNum();
		if(t<nextBroadcast)
			return false;
		nextBroadcast=t+CHECK_EVERY;
		int i;
		for(i=alliedArchons.size;i>=0;i--) {
			if(alliedArchonInfos[i].robot.getID()<myID)
				return false;
		}
		mySender.sendGoThisWay(dx,dy);
		return true;
	}

}
