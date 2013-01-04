package rushbot;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.Team;

/** The rushbot goes straight for the enemy's HQ, avoiding moving onto detected mines. 
 * It does not defuse or micro or take encampments.
 * It uses a greedy pathfinding algorithm, with some wiggling and randomness mixed in.
 */
public class RobotPlayer {
	public static void run(RobotController rc) {
		while (true) {
			try {
				if (rc.getType() == RobotType.HQ) {
					if (!rc.isMovementActive()) {
						Direction dir = Direction.values()[(int)(Math.random()*8)];
						if (rc.canMove(dir))
							rc.spawn(dir);
					}
				} else if (rc.getType() == RobotType.SOLDIER) {
					if (!rc.isMovementActive()) {
						Direction dir = Direction.values()[(int)(Math.random()*8)];
						if(Math.random()<0.5)
							dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						int[] wiggle = new int[] {0, -1, 1, -2, 2};
						if(Math.random()<0.5) 
							for(int i=0; i<wiggle.length; i++) 
								wiggle[i]*=-1;
						for(int d: wiggle) {
							Direction wdir = Direction.values()[(dir.ordinal()+d+8)%8];
							if(rc.canMove(wdir)) {
								Team mineTeam = rc.senseMine(rc.getLocation().add(wdir));
								if(mineTeam==null || mineTeam==rc.getTeam()) {
									rc.move(wdir);
									break;
								}
							}
						}
					}
				}

				rc.yield();
			} catch (Exception e) {
				System.out.println("caught exception:");
				e.printStackTrace();
			}
		}
	}
}
