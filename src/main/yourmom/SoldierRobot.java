package yourmom;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class SoldierRobot extends BaseRobot {
	public SoldierRobot(RobotController myRC) throws GameActionException {
		super(myRC);

		nav.setDestination(rc.senseEnemyHQLocation());
	}

	@Override
	public void run() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		final Direction dirToMove = nav.navigateToDestination();
		if (rc.canMove(dirToMove)) {
			rc.move(dirToMove);
		} else {
			final Direction dirToWiggle = nav.wiggleToMovableDirection(dirToMove);
			if (dirToWiggle != null) {
				rc.move(dirToWiggle);
			} else {
				rc.move(nav.wiggleToMovableDirection(dirToMove.opposite()));
			}
		}
		rc.yield();
	}
}
