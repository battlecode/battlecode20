package yourmom;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
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
		enemySpottedRound = -1;
	}

	@Override
	public void run() throws GameActionException {
		boolean gotHitLastRound = curHealth < healthLastTurn;
		if ((behavior == BehaviorState.SWARM || behavior == BehaviorState.LOST)) {
			healthLastTurn = curHealth;
			return;
		}

		radar.scan(true, true);

		int closestEnemyID = er.getClosestEnemyID();
		MapLocation closestEnemyLocation = closestEnemyID == -1 ? null :
			er.enemyLocationInfo[closestEnemyID];
		if (closestEnemyLocation != null && rc.canSenseSquare(closestEnemyLocation)) {
			closestEnemyLocation = null;
		}
		RobotInfo radarClosestEnemy = radar.closestEnemy;
		if (radarClosestEnemy != null && (closestEnemyLocation == null || (radar.closestEnemyDist <= curLoc.distanceSquaredTo(closestEnemyLocation)))) {
			closestEnemyLocation = radarClosestEnemy.location;
		}
		boolean enemyNearby = closestEnemyLocation != null && curLoc.distanceSquaredTo(closestEnemyLocation) <= myType.attackRadiusMaxSquared;
		// TODO broadcasting?!

		tryToAttack();

		nav.setDestination(rc.senseEnemyHQLocation());

		tryToMove();
	}

	private void tryToAttack() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		RobotInfo bestInfo = null;
		double bestValue = Double.MAX_VALUE;
		for (int n = 0; n < radar.numEnemyRobots; ++n) {
			RobotInfo ri = radar.enemyInfos[radar.enemyRobots[n]];
			if (!rc.canAttackSquare(ri.location)) {
				continue;
			}
			// TODO determine whether to SD
			if ((bestValue <= myType.attackPower && ri.health <= myType.attackPower) ? ri.health > bestValue : ri.health < bestValue) {
				bestInfo = ri;
				bestValue = ri.health;
			}
		}

		if (bestInfo != null) {
			// TODO broadcast?
			rc.attackSquare(bestInfo.location);
		}
	}

	private void tryToMove() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		final Direction dirToMove = nav.navigateToDestination();
		if (dirToMove != null && rc.canMove(dirToMove)) {
			System.out.println("moving to dest");
			rc.move(dirToMove);
		} else {
			Direction randomMove = nav.navigateCompletelyRandomly();
			while (!rc.canMove(randomMove)) {
				randomMove = nav.navigateCompletelyRandomly();
			}
			System.out.println("moving randomly");
			rc.move(randomMove);
		}
		rc.yield();
	}
}
