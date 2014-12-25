package nothingbot;

import battlecode.common.*;
import java.util.*;

//author: lygophile
public class RobotPlayer {
	public static void run(RobotController rc) {
		while(true) {
			rc.setIndicatorString(0, "My supply level: " + rc.getSupplyLevel());
			rc.yield();
		}
	}
}
