package hardplayer;

import battlecode.common.Robot;
import battlecode.common.RobotInfo;

import scala.Function1;

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

	public <T> void foreach(Function1<RobotInfo,T> f) {
		RobotInfo [] robotInfos = this.robotInfos;
		for(int i=size;i>=0;i--) {
			f.apply(robotInfos[i]);
		}
	}

	public void foreachwhile(Function1<RobotInfo,Boolean> f) {
		RobotInfo [] robotInfos = this.robotInfos;
		for(int i=size;i>=0&&f.apply(robotInfos[i]);i--);
	}
}
