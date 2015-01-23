package franbot;

import battlecode.common.*;
import java.util.*;
import javax.script.*;

//author: lygophile
public class RobotPlayer {
	
	public static void run(RobotController rc) {
		try {
		if (rc.getType() == RobotType.HQ) {
			while (true) {
				rc.spawn(Direction.NORTH,RobotType.BEAVER);
				while(true) {
					rc.yield();
				}
			}
		}
		
		if (rc.getType() == RobotType.BEAVER) {
			System.out.println(rc.isCoreReady());
			rc.move(Direction.NORTH);
			System.out.println(rc.isCoreReady());
			while(true) {
				rc.yield();
			}
		}
		}catch (Exception e) {}
	}
}