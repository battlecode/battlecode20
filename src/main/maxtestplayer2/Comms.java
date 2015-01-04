package maxtestplayer2;

import battlecode.common.*;

public class Comms{
	
	public static void listAlliedRobotCount(RobotController rc) throws GameActionException{
		RobotInfo[] allies = rc.senseNearbyRobots(10000000,rc.getTeam());
		int[] allyNumbers = new int[RobotType.values().length];
		for(RobotInfo ri:allies)
			allyNumbers[ri.type.ordinal()]++;
		for(int channel=0;channel<allyNumbers.length;channel++)
			rc.broadcast(channel, allyNumbers[channel]);
	}
	
	public static int getAlliedRobotCount(RobotController rc,RobotType type) throws GameActionException{
		return rc.readBroadcast(type.ordinal());
	}
	
}
