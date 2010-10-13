package hardplayer.goals;

import hardplayer.BasePlayer;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;
import battlecode.common.TerrainTile;

public class ArchonExploreGoal extends Goal {

	public int dx, dy;

	int stuckTimeout;
	int numTurnsAdvanceFailed;
	static public final int UNSTUCK_TIME = 25;
	static public final int MAX_FAILURE_TURNS = 30;

	public ArchonExploreGoal(BasePlayer bp) {
		super(bp);
		dx = 6;
		dy = 6;
	}

	public int getMaxPriority() {
		return EXPLORE_PRIORITY;
	}

	public int getPriority() {
		return EXPLORE_PRIORITY;
	}

	public void tryToAccomplish() {
		MapLocation myLoc = player.myLoc;
		int x = myLoc.getX(), y = myLoc.getY();
		if(myRC.senseTerrainTile(new MapLocation(x+dx,y)).getType() == TerrainTile.TerrainType.OFF_MAP)
			dx=-dx;
		if(myRC.senseTerrainTile(new MapLocation(x,y+dy)).getType() == TerrainTile.TerrainType.OFF_MAP)
			dy=-dy;
		int t = Clock.getRoundNum();
		int armyWanted=5*(player.alliedArchons.size+1);
		if(4*player.alliedTurrets.size+3*player.alliedChainers.size+2*player.alliedSoldiers.size<armyWanted) {
			// don't go looking for trouble until we have a big army
			MapLocation nearbyArchonLoc = player.nearestAlliedArchonAtLeastDist(4);
			if(nearbyArchonLoc!=null)
				player.myNav.moveToForward(nearbyArchonLoc);
			return;
		}
		if(t>stuckTimeout) {
			int myPos = myLoc.getX()*dx+myLoc.getY()*dy;
			int i;
			int d, dmin=0x7FFFFFFF;
			int numArmyInFront=0;
			MapLocation loc, closestLowEnergon=null;
			RobotInfo info;
			for(i=player.alliedTurrets.size-1;i>=0;i--) {
				info=player.alliedTurretInfos[i];
				if(info.location.getX()*dx+info.location.getY()*dy>myPos)
					numArmyInFront+=4;
				if(info.energonLevel<5.) {
					d=myLoc.distanceSquaredTo(info.location);
					if(d<dmin) {
						dmin=d;
						closestLowEnergon=info.location;
					}
				}
			}
			for(i=player.alliedSoldiers.size-1;i>=0;i--) {
				info=player.alliedSoldierInfos[i];
				if(info.location.getX()*dx+info.location.getY()*dy>myPos)
					numArmyInFront+=2;
				if(info.energonLevel<5.) {
					d=myLoc.distanceSquaredTo(info.location);
					if(d<dmin) {
						dmin=d;
						closestLowEnergon=info.location;
					}
				}
			}
			for(i=player.alliedChainers.size-1;i>=0;i--) {
				info=player.alliedChainerInfos[i];
				if(info.location.getX()*dx+info.location.getY()*dy>myPos)
					numArmyInFront+=3;
				if(info.energonLevel<5.) {
					d=myLoc.distanceSquaredTo(info.location);
					if(d<dmin) {
						dmin=d;
						closestLowEnergon=info.location;
					}
				}
			}
			if(numArmyInFront<armyWanted) {
					if(closestLowEnergon!=null&&dmin>2) {
						player.myNav.moveToBackward(closestLowEnergon);
						numTurnsAdvanceFailed+=7;
					}
					else
						numTurnsAdvanceFailed++;
					if(numTurnsAdvanceFailed>MAX_FAILURE_TURNS)
						stuckTimeout=t+UNSTUCK_TIME;
					return;
			}
		}
		player.myNav.moveToForward(new MapLocation(x+dx,y+dy));
		numTurnsAdvanceFailed=0;
	}

}