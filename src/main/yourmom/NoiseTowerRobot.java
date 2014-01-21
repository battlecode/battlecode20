package yourmom;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class NoiseTowerRobot extends BaseRobot {
	public NoiseTowerRobot(RobotController myRC) throws GameActionException {
		super(myRC);
	}

	private boolean inMap(MapLocation loc) {
		return 0 <= loc.x && loc.x < MAP_WIDTH && 0 <= loc.y && loc.y < MAP_HEIGHT;
	}

	@Override
	public void run() throws GameActionException {
		for (int i = BaseRobot.NDIRECTIONS; --i >= 0;) {
			final Direction d = BaseRobot.USEFUL_DIRECTIONS[i];
			for (int mult = 20; mult >= 0; mult -= 2) {
				final MapLocation attackLoc = curLoc.add(d, mult);
				while (!rc.isActive()) {
					rc.yield();
				}
				if (curLoc.distanceSquaredTo(attackLoc) <= myType.attackRadiusMaxSquared && inMap(attackLoc)) {
					rc.attackSquareLight(attackLoc);
				}
			}
		}
	}
}
