package refplayer;

import battlecode.common.Robot;
import battlecode.common.RobotInfo;

public class FastList {
	public RobotInfo [] robotInfos;
	//public Robot [] robots;
	
	// this is actually the size MINUS ONE
	public int size;
	
	public FastList(int n) {
		robotInfos = new RobotInfo [n];
		//robots = new Robot [n];
		size = -1;
	}
}
