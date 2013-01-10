package mediumbot;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class Util {

	static int m_z = Clock.getBytecodeNum();
	static int m_w = Clock.getRoundNum();
	
	/**
	 * sets up our RNG given two seeds
	 * @param seed1
	 * @param seed2
	 */
	public static void randInit(int seed1, int seed2)
	{
		m_z = seed1;
		m_w = seed2;
	}

	private static int gen()
	{
		m_z = 36969 * (m_z & 65535) + (m_z >> 16);
	    m_w = 18000 * (m_w & 65535) + (m_w >> 16);
	    return (m_z << 16) + m_w;
	}

	/** @return an integer between 0 and MAX_INT */
	public static int randInt()
	{
		return gen();
	}

	/** @return a double between 0 - 1.0 */
	public static double randDouble()
	{
		return (gen() * 2.32830644e-10 + 0.5);
	}
	
	public static RobotInfo nearestEnemy(RobotController rc, int distThreshold) throws GameActionException {
		Robot[] ar = rc.senseNearbyGameObjects(Robot.class, distThreshold);

		if(ar==null || ar.length==0)
			return null;
		RobotInfo nearest = null;
		for(Robot r: ar) {
			if(r.getTeam()==rc.getRobot().getTeam())
				continue;
			RobotInfo ri = rc.senseRobotInfo(r);
			MapLocation loc = ri.location;
			if(nearest == null || rc.getLocation().distanceSquaredTo(nearest.location) > rc.getLocation().distanceSquaredTo(loc))
				nearest = ri;
		}
		return nearest;
	}
}
