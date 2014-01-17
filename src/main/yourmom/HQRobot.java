package yourmom;

import battlecode.common.GameActionException;
import battlecode.common.Direction;
import battlecode.common.RobotController;

public class HQRobot extends BaseRobot {
	int nRobots = 0;

	public HQRobot(RobotController myRC) throws GameActionException {
		super(myRC);
	}

	@Override
	public void run() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		if (nRobots < 5) {
			for (int i = BaseRobot.NDIRECTIONS; i-- >= 0;) {
				final Direction d = BaseRobot.USEFUL_DIRECTIONS[i];
				if (rc.canMove(d)) {
					rc.spawn(d);
					rc.yield();
					break;
				}
			}
		}
	}

	@Override
	public void updateRoundVariables() {
		super.updateRoundVariables();

		nRobots = rc.senseRobotCount();
	}
}
