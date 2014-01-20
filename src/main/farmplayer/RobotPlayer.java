package farmplayer;

import battlecode.common.*;

public class RobotPlayer {
	// mutables
	static RobotController rc;

	// constants, or things that should be constants
	static Team HOME;
	static Team VISITOR;
	final static Direction[] USEFUL_DIRECTIONS = new Direction[] {
		Direction.EAST,
		Direction.NORTH_EAST,
		Direction.NORTH,
		Direction.NORTH_WEST,
		Direction.WEST,
		Direction.SOUTH_WEST,
		Direction.SOUTH,
		Direction.SOUTH_EAST,
	};
	final static int NDIR = USEFUL_DIRECTIONS.length;

	// noisetower specific
	static double[] averageCowGrowth;

	public static void run(RobotController rc_) {
		rc = rc_;
		HOME = rc.getTeam();
		VISITOR = HOME.opponent();

		switch (rc.getType()) {
			case HQ:
				while (true) {
					try {
						runHQ();
					} catch (Exception e) {
						;
					}
				}
			case SOLDIER:
				while (true) {
					try {
						runSoldier();
					} catch (Exception e) {
						;
					}
				}
			case NOISETOWER:
				averageCowGrowth = calculateAverageCowGrowth();
				while (true) {
					try {
						runNoiseTower();
					} catch (Exception e) {
						;
					}
				}
			case PASTR:
				while (true) {
					rc.yield();
				}
			default:
				break;
		}
	}

	static void runSoldier() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		Robot[] myRobots = rc.senseNearbyGameObjects(
			Robot.class,
			rc.getType().attackRadiusMaxSquared,
			HOME
		);
		switch (myRobots.length) {
			case 1:
				rc.construct(RobotType.NOISETOWER);
				break;
			default:
				rc.construct(RobotType.PASTR);
				break;
		}

		rc.yield();
	}

	/**
	 * Kills nearby soldiers if there are any.
	 * Get a list of squares adjacent to HQ (up to 8)
	 * Build N-1 pastrs and 1 noise tower to herd
	 */
	static void runHQ() throws GameActionException {
		Robot[] enemyRobots = rc.senseNearbyGameObjects(
			Robot.class,
			rc.getType().attackRadiusMaxSquared,
			VISITOR
		);

		if (enemyRobots.length > 0) {
			RobotInfo firstRobotInfo = rc.senseRobotInfo(enemyRobots[0]);
			if (rc.isActive()) {
				rc.attackSquare(firstRobotInfo.location);
				rc.yield();
			}
		}

		final MapLocation myLoc = rc.getLocation();
		for (Direction d : USEFUL_DIRECTIONS) {
			if (rc.isActive() && rc.canMove(d) && rc.senseRobotCount() < GameConstants.MAX_ROBOTS) {
				rc.spawn(d);
				break;
			}
		}

		rc.yield();
	}

	static boolean inMap(MapLocation loc) {
		return 0 <= loc.x && loc.x < rc.getMapWidth() && 0 <= loc.y && loc.y < rc.getMapHeight();
	}

	static double[] calculateAverageCowGrowth() {
		final double[][] cows = rc.senseCowGrowth();
		double[] averageCowGrowth = new double[NDIR];

		final MapLocation myLoc = rc.getLocation();
		for (int i = 0; i < NDIR; ++i) {
			final Direction d = USEFUL_DIRECTIONS[i];
			for (int mult = 18; --mult >= 0;) {
				final MapLocation attackLoc = myLoc.add(d, mult);
				if (myLoc.distanceSquaredTo(attackLoc) < 300 && inMap(attackLoc)) {
					averageCowGrowth[i] += cows[attackLoc.x][attackLoc.y];
				}
			}
			averageCowGrowth[i] /= 17;
		}

		return averageCowGrowth;
	}

	/**
	 * Makes noise at the square in attacking range with the most cows
	 */
	static void runNoiseTower() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		final MapLocation myLoc = rc.getLocation();
		for (int i = 0; i < NDIR; ++i) {
			if (averageCowGrowth[i] < 0.1) {
				continue;
			}
			final Direction d = USEFUL_DIRECTIONS[i];
			for (int mult = 18; --mult >= 0;) {
				final MapLocation attackLoc = myLoc.add(d, mult);
				while (!rc.isActive()) {
					rc.yield();
				}
				if (myLoc.distanceSquaredTo(attackLoc) < 300 && inMap(attackLoc)) {
					rc.attackSquareLight(attackLoc);
					rc.yield();
				}
			}
		}
	}
}
