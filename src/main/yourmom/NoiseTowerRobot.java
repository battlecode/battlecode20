package yourmom;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class NoiseTowerRobot extends BaseRobot {
	public NoiseTowerRobot(RobotController myRC) throws GameActionException {
		super(myRC);
	}

	@Override
	public void run() throws GameActionException {
		for (int i = BaseRobot.NDIRECTIONS; --i >= 0;) {
			final Direction d = BaseRobot.USEFUL_DIRECTIONS[i];
			for (int mult = 15; mult >= 0; mult -= 2) {
				final MapLocation attackLoc = curLoc.add(d, mult + 2);
				while (!rc.isActive()) {
					rc.yield();
				}
				if (curLoc.distanceSquaredTo(attackLoc) <= myType.attackRadiusMaxSquared && inMap(attackLoc)) {
					rc.attackSquare(attackLoc);
				}
			}
		}
	}
}
