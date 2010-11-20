package hardplayer.sensor;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;

public class LongRangeSensor extends Sensor implements SensorConstants {

	static public final int range = 10;
	static public final int range2 = 2*range+1;
	static public final int range2sq = range2 * range2;
	static public final int mid = range2sq/2;

	static public int [] enemyGroundCodes;
	static public int [] enemyAirCodes;
	static public int [][] allCodes;

	public void sense() {
		try {
			Robot [] objs = sensor.senseNearbyGameObjects(Robot.class);
			int i = objs.length;
			enemyGroundCodes = new int [range2sq];
			enemyAirCodes = new int [range2sq];
			int offset = myLoc.x + myLoc.y * range2 - mid;
			while(--i>0) {
				Robot r = objs[i];
				RobotInfo info = sensor.senseRobotInfo(r);
				int code = NEVER_MASK;
				MapLocation loc = info.location;
				enemyGroundCodes[loc.x+loc.y*range2-offset] = code;	
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

}
