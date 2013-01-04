package spambot;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/** The purpose of this bot is to send as many messages as possible.
 * It is not a well executed spammer, it just spams randomly. 
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (rc.isActive()) {
						Direction dir = Direction.values()[(int)(Math.random()*8)];
						if (rc.canMove(dir))
							rc.spawn(dir);
					}
					rc.yield();
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (rc.isActive()) {
						Direction dir = Direction.values()[(int)(Math.random()*8)];
						if(rc.canMove(dir))
							rc.move(dir);
					}
					if (Math.random() > 0.99)
						rc.layMine();
					for(int i=0; i<500; i++)
						rc.broadcast(((int)(Math.random()*Integer.MAX_VALUE))%GameConstants.MAX_RADIO_CHANNEL, 
								(int)(Math.random()*Integer.MAX_VALUE)+(int)(Math.random()*Integer.MIN_VALUE));
					rc.yield();
				}
			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
	}
}
