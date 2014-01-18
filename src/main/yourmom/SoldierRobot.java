package yourmom;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class SoldierRobot extends BaseRobot {
	private enum BehaviorState {
		/** No enemies to deal with, swarming. */
		SWARM,
		/** Heard of an enemy spotted call, but no enemy info calls yet. */
		SEEK,
		/** Far from target. Use bug to navigate. */
		LOST,
		/** Tracking closest enemy, even follow them for 12 turns. */
		ENEMY_DETECTED,
		/** Getting hit somehow, don't know from where. */
		LOOK_AROUND_FOR_ENEMIES,
		/** Become a pastr. */
		BECOME_PASTR,
		/** Become a noisetower. */
		BECOME_NOISETOWER,
	};
	BehaviorState behavior;
	MapLocation target;
	MapLocation previousBugTarget;
	MapLocation enemySpottedTarget;
	double healthLastTurn;
	int enemySpottedRound;

	public SoldierRobot(RobotController myRC) throws GameActionException {
		super(myRC);

		nav.setNavigationMode(NavigationMode.BUG);
		enemySpottedRound = -1
	}

	@Override
	public void run() throws GameActionException {
		boolean gotHitLastRound = curHealth < healthLastTurn;
		if ((behavior == BehaviorState.SWARM || behavior == BehaviorState.LOST)) {
			healthLastTurn = curHealth;
			return;
		}
	}

	// TODO
	private void tryToAttack() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}
	}

	private void tryToMove() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		final Direction dirToMove = nav.navigateToDestination();
		System.out.println("Direction of movement: "+dirToMove);
		if (dirToMove != null) {
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
		} else {
			rc.move(nav.navigateCompletelyRandomly());
		}
		rc.yield();
	}
}
