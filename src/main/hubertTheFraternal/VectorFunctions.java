package hubertTheFraternal;

import java.util.ArrayList;

import battlecode.common.*;

public class VectorFunctions {
	public static MapLocation findClosest(MapLocation[] manyLocs, MapLocation point){
		int closestDist = 10000000;
		int challengerDist = closestDist;
		MapLocation closestLoc = null;
		for(MapLocation m:manyLocs){
			challengerDist = point.distanceSquaredTo(m);
			if(challengerDist<closestDist){
				closestDist = challengerDist;
				closestLoc = m;
			}
		}
		return closestLoc;
	}
	public static int findClosest(ArrayList<MapLocation> manyLocs, MapLocation point){
		int closestDist = 10000000;
		int challengerDist = closestDist;
		int closestLoc = 0;
		for(int i=0;i<manyLocs.size();i++){
			MapLocation m = manyLocs.get(i);
			challengerDist = point.distanceSquaredTo(m);
			if(challengerDist<closestDist){
				closestDist = challengerDist;
				closestLoc = i;
			}
		}
		return closestLoc;
	}
	public static MapLocation mladd(MapLocation m1, MapLocation m2){
		return new MapLocation(m1.x+m2.x,m1.y+m2.y);
	}
	
	public static MapLocation mlsubtract(MapLocation m1, MapLocation m2){
		return new MapLocation(m1.x-m2.x,m1.y-m2.y);
	}
	
	public static MapLocation mldivide(MapLocation bigM, int divisor){
		return new MapLocation(bigM.x/divisor, bigM.y/divisor);
	}
	
	public static MapLocation mlmultiply(MapLocation bigM, int factor){
		return new MapLocation(bigM.x*factor, bigM.y*factor);
	}
	
	public static int locToInt(MapLocation m){
		return (m.x*100 + m.y);
	}
	
	public static MapLocation intToLoc(int i){
		return new MapLocation(i/100,i%100);
	}
	
	public static void printPath(ArrayList<MapLocation> path, int bigBoxSize){
		for(MapLocation m:path){
			MapLocation actualLoc = bigBoxCenter(m,bigBoxSize);
			System.out.println("("+actualLoc.x+","+actualLoc.y+")");
		}
	}
	public static MapLocation bigBoxCenter(MapLocation bigBoxLoc, int bigBoxSize){
		return mladd(mlmultiply(bigBoxLoc,bigBoxSize),new MapLocation(bigBoxSize/2,bigBoxSize/2));
	}
	public static MapLocation[] robotsToLocations(Robot[] robotList,RobotController rc, boolean ignoreHQ) throws GameActionException{
		if(robotList.length==0)
			return new MapLocation[]{};
		ArrayList<MapLocation> robotLocs = new ArrayList<MapLocation>();
		for(int i=0;i<robotList.length;i++){
			Robot anEnemy = robotList[i];
			RobotInfo anEnemyInfo = rc.senseRobotInfo(anEnemy);
			if(!ignoreHQ||anEnemyInfo.type!=RobotType.HQ)
				robotLocs.add(anEnemyInfo.location);
		}
		return robotLocs.toArray(new MapLocation[]{});
	}
	public static MapLocation meanLocation(MapLocation[] manyLocs){
		if(manyLocs.length==0)
			return null;
		MapLocation runningTotal = new MapLocation(0,0);
		for(MapLocation m:manyLocs){
			runningTotal = mladd(runningTotal,m);
		}
		return mldivide(runningTotal,manyLocs.length);
	}
}

