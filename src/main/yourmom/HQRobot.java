package yourmom;

import battlecode.common.GameActionException;
import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public class HQRobot extends BaseRobot {
	final static int MAX_ROBOTS = 15;

	int nRobots = 0;

	public HQRobot(RobotController myRC) throws GameActionException {
		super(myRC);
	}

	/**
	 * Attacking is more important than spawning.
	 * If there is an enemy around, attack it.
	 * Then try to spawn.
	 */
	@Override
	public void run() throws GameActionException {
		tryToAttack();

		tryToSpawn();
	}

	private void tryToAttack() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		RobotInfo bestInfo = null;
		double bestValue = Double.MAX_VALUE;

		for (int n = radar.numEnemyRobots; --n >= 0;) {
			RobotInfo ri = radar.enemyInfos[radar.enemyRobots[n]];
			if (!rc.canAttackSquare(ri.location)) {
				continue;
			}

			if ((bestValue <= myType.attackPower && ri.health <= myType.attackPower) ? ri.health > bestValue : ri.health < bestValue) {
				bestInfo = ri;
				bestValue = ri.health;
			}
		}

		if (bestInfo != null) {
			// TODO broadcast?
			rc.attackSquare(bestInfo.location);
			rc.yield();
		}
	}

	public void tryToSpawn() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		if (nRobots > HQRobot.MAX_ROBOTS) {
			return;
		}

		for (int i = BaseRobot.NDIRECTIONS; --i >= 0;) {
			final Direction d = BaseRobot.USEFUL_DIRECTIONS[i];
			if (rc.canMove(d)) {
				rc.spawn(d);
				rc.yield();
				break;
			}
		}
	}

	@Override
	public void updateRoundVariables() {
		super.updateRoundVariables();

		nRobots = rc.senseRobotCount();
	}
}
