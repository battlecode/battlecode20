package yourmom;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Robot;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class HQRobot extends BaseRobot {
	final static int MAX_ROBOTS = 25;

	int nRobots = 0;
	MapLocation[] bestLocations = new MapLocation[1000];
	int bestLocationsIndex = 0;

	public HQRobot(RobotController myRC) throws GameActionException {
		super(myRC);
		System.out.println(Clock.getBytecodeNum());

		findGoodPastrLocations();
		System.out.println(Clock.getBytecodeNum());
		final MapLocation pastrLocation = findBestPastrLocation();
		System.out.println(Clock.getBytecodeNum());
		final MapLocation noisetowerLocation = findNoisetowerLocation(pastrLocation);
		System.out.println(Clock.getBytecodeNum());
		io.broadcastPastrLocations(new MapLocation[]{pastrLocation});
		io.broadcastNoisetowerLocations(new MapLocation[]{noisetowerLocation});
		System.out.println(Clock.getBytecodeNum());
	}

	private MapLocation findNoisetowerLocation(MapLocation pastrLocation) {
		for (int i = 8; --i >= 0;) {
			final MapLocation tryLocation = pastrLocation.add(BaseRobot.USEFUL_DIRECTIONS[i]);
			switch (rc.senseTerrainTile(tryLocation)) {
			case NORMAL:
			case ROAD:
				return tryLocation;
			default:
				break;
			}
		}
		return pastrLocation;
	}

	private void findGoodPastrLocations() throws GameActionException {
		double highestCows = 1;

		for (int c = MAP_WIDTH; --c >= 0;) {
			for (int r = MAP_HEIGHT - 1 - (c & 1); r >= 0; r -= 2) {
				if (cowProductions[c][r] > highestCows) {
					highestCows = cowProductions[c][r];
					bestLocations[0] = new MapLocation(c, r);
					bestLocationsIndex = 1;
				} else if (cowProductions[c][r] == highestCows) {
					final MapLocation add = new MapLocation(c, r);
					if (add.distanceSquaredTo(MY_HQ_LOCATION) < add.distanceSquaredTo(ENEMY_HQ_LOCATION)) {
						bestLocations[bestLocationsIndex++] = add;
					}
				}
			}
		}
	}

	private MapLocation findBestPastrLocation() throws GameActionException {
		MapLocation bestLocation = null;
		int lowestBadness = Integer.MAX_VALUE;
		if (bestLocationsIndex > 100) {
			for (int i = 0; i < 100; ++i) {
				final int random = (int)(bestLocationsIndex * Util.randDouble());
				final MapLocation testLocation = bestLocations[random];
				final int badness = evaluateLocation(testLocation, 20);
				if (badness < lowestBadness) {
					bestLocation = testLocation;
					lowestBadness = badness;
				}
			}
		} else {
			if (myTeam == Team.A) {
				for (int i = bestLocationsIndex; --i >= 0;) {
					final int badness = evaluateLocation(bestLocations[i], 24);
					if (badness < lowestBadness) {
						bestLocation = bestLocations[i];
						lowestBadness = badness;
					}
				}
			} else {
				for (int i = 0; i < bestLocationsIndex; ++i) {
					final int badness = evaluateLocation(bestLocations[i], 24);
					if (badness < lowestBadness) {
						bestLocation = bestLocations[i];
						lowestBadness = badness;
					}
				}
			}
		}
		return bestLocation;
	}

	private int evaluateLocation(MapLocation loc, int radius) {
		int badness = 0;

		final MapLocation[] nearbyLocations = MapLocation.getAllMapLocationsWithinRadiusSq(loc, radius);
		final int l = nearbyLocations.length;
		for (int i = l; --i >= 0;) {
			final MapLocation nearbyLoc = nearbyLocations[i];
			if (!inMap(nearbyLoc)) {
				++badness;
			} else {
				badness += 1 - cowProductions[nearbyLoc.x][nearbyLoc.y];
			}
		}

		return badness;
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

		final Robot[] enemies24 = rc.senseNearbyGameObjects(Robot.class, 24, enemyTeam);
		if (enemies24.length != 0) {
			RobotInfo r = rc.senseRobotInfo(enemies24[0]);
			if (r.location.distanceSquaredTo(rc.getLocation()) < 16) {
				rc.attackSquare(r.location);
			} else {
				rc.attackSquare(r.location.add(r.location.directionTo(rc.getLocation())));
			}
		}
	}

	public void tryToSpawn() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		if (nRobots > HQRobot.MAX_ROBOTS) {
			return;
		}

		final int randOffset = (int)(Util.randDouble() * BaseRobot.NDIRECTIONS);
		for (int i = BaseRobot.NDIRECTIONS; --i >= 0;) {
			final Direction d = BaseRobot.USEFUL_DIRECTIONS[(randOffset + i) & 7];
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
