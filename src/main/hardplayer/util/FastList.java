package hardplayer.util;

import battlecode.common.Robot;
import battlecode.common.RobotInfo;

public class FastList {
	public RobotInfo [] robotInfos;
	//public Robot [] robots;
	public int size;
	
	public FastList(int n) {
		robotInfos = new RobotInfo [n];
		//robots = new Robot [n];
		size = 0;
	}
}
