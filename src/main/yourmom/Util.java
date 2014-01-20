package yourmom;

import battlecode.common.Clock;
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

	/** @return a random integer between {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE}*/
	public static int randInt()
	{
		return gen();
	}

	/** @return a double between 0 - 1.0 */
	public static double randDouble()
	{
		return (gen() * 2.32830644e-10 + 0.5);
	}

	
	/**
	 * Estimates enemy strength based on robot type, health level, cooldown, and flux
	 */
	public static double getEnemyStrengthEstimate(RobotInfo ri) {
		double strengthEstimate = ri.health;
		switch(ri.type) {
		case PASTR:
		case NOISETOWER:
			strengthEstimate = 0;
			break;
		}
		if(ri.actionDelay > 2) 
			strengthEstimate *= 4/(2+ri.actionDelay);
		return strengthEstimate;
	}
	

	/**
	 * Estimate your own intrinsic value
	 */
	public static double getOwnStrengthEstimate(RobotController rc) {
		switch(rc.getType()) {
		case SOLDIER:
		case HQ:
			return rc.getHealth();			
		default:
			return 0;
		}
	}
}
