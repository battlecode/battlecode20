package yourmom;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

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
	};

	final int INITIAL_SWARM_SIZE = 5;

	BehaviorState behavior;
	MapLocation target;
	MapLocation previousBugTarget;
	MapLocation enemySpottedTarget;
	double healthLastTurn;
	int enemySpottedRound;
	int lastRoundTooClose;
	boolean checkedBehind;
	boolean movingTarget;
	int targetSwarmSize;

	MapLocation bestPastrSpot;

	public SoldierRobot(RobotController myRC) throws GameActionException {
		super(myRC);

		nav.setNavigationMode(NavigationMode.BUG);
		enemySpottedRound = -1;
		lastRoundTooClose = -1;
		behavior = BehaviorState.SWARM;
		checkedBehind = false;
		movingTarget = false;
		target = MY_HQ_LOCATION.add(DIRECTION_TO_ENEMY_HQ, 3);

		targetSwarmSize = INITIAL_SWARM_SIZE;
		final int shouldBuildPastr = (int)(8 * Util.randDouble());
		System.out.println(shouldBuildPastr);

		switch (shouldBuildPastr) {
		case 7:
			System.out.println("finding best pastr spot");
			behavior = BehaviorState.BECOME_PASTR;
			findBestSeat();
			nav.setDestination(bestPastrSpot);
			System.out.println("best seat is "+bestPastrSpot);
		default:
			break;
		}
	}

	private void findBestSeat() throws GameActionException {
		double bestAggregateCowProduction = 0;
		double tmpAggregateCowProduction = 0;
		double distToBestLocation = Double.MAX_VALUE;
		for (int x = MAP_WIDTH; --x >= 0;) {
			for (int y = MAP_HEIGHT; --y >= 0;) {
				tmpAggregateCowProduction = cowProductions[x][y];
				if (tmpAggregateCowProduction >= bestAggregateCowProduction) {
					final MapLocation tmpMapLocation = new MapLocation(x, y);
					final double distToLoc = MY_HQ_LOCATION.distanceSquaredTo(tmpMapLocation);
					if (distToLoc < distToBestLocation && (myPastrs.length == 0 || myPastrs.length > 0 && tmpMapLocation.distanceSquaredTo(myPastrs[0]) > 2*GameConstants.PASTR_RANGE)) {
						distToBestLocation = distToLoc;
						bestAggregateCowProduction = tmpAggregateCowProduction;
						bestPastrSpot = tmpMapLocation;
					}
				}
			}
		}
	}

	@Override
	public void run() throws GameActionException {
		rc.setIndicatorString(0, behavior.toString());

		switch (behavior) {
		case BECOME_PASTR: {
			final double distToBestSeat = curLoc.distanceSquaredTo(bestPastrSpot);
			tryToAttack();
			if (distToBestSeat < 5 && rc.isActive()) {
				System.out.println("build pastr");
				rc.construct(RobotType.PASTR);
				rc.yield();
			}
			tryToMove();
			return;
		}}

		boolean gotHitLastRound = curHealth < healthLastTurn;
		if ((behavior == BehaviorState.SWARM || behavior == BehaviorState.LOST) && !rc.isActive()) {
			healthLastTurn = curHealth;
			return;
		}

		radar.scan(true, true);

		int closestEnemyID = er.getClosestEnemyID();
		final MapLocation bestEnemyPastrLoc = getBestEnemyPastr();
		MapLocation closestEnemyLocation = (closestEnemyID == -1) ? null : er.enemyLocationInfo[closestEnemyID];
		if (closestEnemyLocation != null && rc.canSenseSquare(closestEnemyLocation)) {
			closestEnemyLocation = null;
		}
		RobotInfo radarClosestEnemy = radar.closestEnemy;
		if (radarClosestEnemy != null && radarClosestEnemy.type != RobotType.HQ && (closestEnemyLocation == null || (radar.closestEnemyDist <= curLoc.distanceSquaredTo(closestEnemyLocation)))) {
			closestEnemyLocation = radarClosestEnemy.location;
		}
		if (bestEnemyPastrLoc != null && behavior != BehaviorState.BECOME_PASTR) {
			closestEnemyLocation = bestEnemyPastrLoc;
			behavior = BehaviorState.SEEK;
		}
		boolean enemyNearby = closestEnemyLocation != null && curLoc.distanceSquaredTo(closestEnemyLocation) <= myType.attackRadiusMaxSquared;
		if(curRound%ExtendedRadarSystem.ALLY_MEMORY_TIMEOUT == myID%ExtendedRadarSystem.ALLY_MEMORY_TIMEOUT) {
			radar.broadcastEnemyInfo(enemyNearby && curHealth > 12);
		}

		movingTarget = true;

		if (gotHitLastRound && (closestEnemyLocation == null || curLoc.distanceSquaredTo(closestEnemyLocation) > 20) || (behavior == BehaviorState.LOOK_AROUND_FOR_ENEMIES)) {
			behavior = BehaviorState.LOOK_AROUND_FOR_ENEMIES;
		} else if (closestEnemyLocation != null) {
			if (enemyNearby) {
				behavior = BehaviorState.ENEMY_DETECTED;
			} else {
				behavior = BehaviorState.SEEK;
			}
			target = closestEnemyLocation;
		} else if (behavior == BehaviorState.ENEMY_DETECTED) {
			behavior = BehaviorState.ENEMY_DETECTED;
			//final int numEnemyRobots = rc.senseNearbyGameObjects(Robot.class, 35, enemyTeam).length;
			//targetSwarmSize = (numEnemyRobots > targetSwarmSize) ? numEnemyRobots : targetSwarmSize;
		} /*else if (curRound < enemySpottedRound + Constants.ENEMY_SPOTTED_SIGNAL_TIMEOUT) {
			behavior = BehaviorState.SEEK;
			target = enemySpottedTarget;
			movingTarget = false;
		}*/ else if (behavior == BehaviorState.SWARM) {
			final int nearbySoldiers = rc.senseNearbyGameObjects(Robot.class, 4, myTeam).length;
			if (nearbySoldiers < targetSwarmSize) {
				behavior = BehaviorState.SWARM;
			} else {
				behavior = BehaviorState.SEEK;
				targetSwarmSize = INITIAL_SWARM_SIZE;
			}
		} else {
			behavior = BehaviorState.LOST;
			// find the enemy pastr furthest from enemy HQ.
			// if no pastrs, go to enemy HQ.
			if (bestEnemyPastrLoc != null) {
				target = bestEnemyPastrLoc;
			} else if (myPastrs != null && myPastrs.length > 0) {
				target = myPastrs[(int)(Util.randDouble() * myPastrs.length)];
			} else {
				MY_HQ_LOCATION.add(DIRECTION_TO_ENEMY_HQ, 3);
			}
		}

		tryToAttack();

		if (!movingTarget || previousBugTarget == null || !nav.isBugTracing() || target.distanceSquaredTo(previousBugTarget) > 20 || curLoc.distanceSquaredTo(previousBugTarget) <= 2) {
			nav.setDestination(target);
			previousBugTarget = target;
		}

		healthLastTurn = curHealth;

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
			// check self destruct
			if (curLoc.distanceSquaredTo(ri.location) <= 2 && curHealth - 20 <= 0) {
				System.out.println("self destruct");
				rc.selfDestruct();
			}

			if (!rc.canAttackSquare(ri.location) || ri.type == RobotType.HQ) {
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

	private void tryToMove() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		Direction dirToMove = computeMoveDirection();
		if (dirToMove != null && rc.canMove(dirToMove)) {
			if (curLoc.add(dirToMove).distanceSquaredTo(ENEMY_HQ_LOCATION) > 15) {
				rc.move(dirToMove);
			} else {
				// turn until it can move
				int ntrials = 10;
				for (; --ntrials >= 0 && (!rc.canMove(dirToMove) || curLoc.add(dirToMove).distanceSquaredTo(ENEMY_HQ_LOCATION) <= RobotType.HQ.attackRadiusMaxSquared);) {
					dirToMove = nav.navigateRandomly(nav.getDestination());
				}
				switch (ntrials) {
				case -1:
					System.out.println("trial failed");
					break;
				default:
					rc.move(dirToMove);
				}
			}
			rc.yield();
		}
	}

	private Direction computeMoveDirection() throws GameActionException {
		if (behavior == BehaviorState.LOOK_AROUND_FOR_ENEMIES) {
			checkedBehind = true;
			return curDir.opposite();
		} else if (behavior == BehaviorState.SWARM) {
			if (curLoc.equals(target)) {
				return nav.navigateCompletelyRandomly();
			}
			if (curLoc.distanceSquaredTo(target) >= 11) {
				Direction dirToMove = nav.navigateToDestination();
				if (dirToMove == null) {
					return null;
				}
				if (behavior == BehaviorState.SWARM && !nav.isBugTracing()) {
					if (radar.alliesInFront == 0 && Util.randDouble() < 0.6) {
						return null;
					}
					if (radar.alliesInFront > 3 && Util.randDouble() < 0.05 * radar.alliesInFront) {
						return nav.navigateCompletelyRandomly();
					}
					if (radar.alliesOnLeft > radar.alliesOnRight && Util.randDouble() < 0.4) {
						return dirToMove.rotateRight();
					} else if (radar.alliesOnLeft < radar.alliesOnRight && Util.randDouble() < 0.4) {
						return dirToMove.rotateLeft();
					}
				}
				return null;
			} else if (curLoc.distanceSquaredTo(target) >= 2) {
				if (radar.alliesInFront > 3 && Util.randDouble() < 0.05 * radar.alliesInFront) {
					return nav.navigateCompletelyRandomly();
				}
			}
		} else if (behavior == BehaviorState.ENEMY_DETECTED) {
			final MapLocation midpoint = new MapLocation(
				(curLoc.x + target.x) / 2,
				(curLoc.y + target.y) / 2
			);
			final double strengthDifference = er.getStrengthDifference(midpoint, 24);
			final boolean weHaveBiggerFront = strengthDifference > 6.;
			final int tooClose = weHaveBiggerFront ? -1 : 5;
			final int tooFar = weHaveBiggerFront ? 4 : 26;
			final int distToTarget = curLoc.distanceSquaredTo(target);
			final Direction dirToTarget = curLoc.directionTo(target);

			if (distToTarget <= 13 && (curDir.ordinal() - dirToTarget.ordinal() + 9) % 8 > 2) {
				return dirToTarget;
			} else if (distToTarget <= tooClose) {
				lastRoundTooClose = curRound;
				final Direction opp = curDir.opposite();
				if (rc.canMove(opp)) {
					return opp;
				}
				Direction dir = opp.rotateLeft();
				if (isOptimalRetreatingDirection(dir, target) && rc.canMove(dir)) {
					return dir;
				}
				dir = opp.rotateRight();
				if (isOptimalRetreatingDirection(dir, target) && rc.canMove(dir)) {
					return dir;
				}
				dir = opp.rotateLeft().rotateLeft();
				if (isOptimalRetreatingDirection(dir, target) && rc.canMove(dir)) {
					return dir;
				}
			} else if (distToTarget >= tooFar) {
				if (curRound < lastRoundTooClose + 12) {
					return curLoc.directionTo(target);
				}
				if (distToTarget <= 5) {
					if (rc.canMove(dirToTarget)) {
						return dirToTarget;
					}
					Direction dir = dirToTarget.rotateLeft();
					if (rc.canMove(dir) && isOptimalAdvancingDirection(dir, target, dirToTarget)) {
						return dir;
					}
					dir = dirToTarget.rotateRight();
					if (rc.canMove(dir) && isOptimalAdvancingDirection(dir, target, dirToTarget)) {
						return dir;
					}
					return dirToTarget;
				} else if (distToTarget >= 20) {
					return nav.navigateToDestination();
				} else {
					return dirToTarget;
				}
			}
		} else if (behavior == BehaviorState.BECOME_PASTR) {
			return nav.navigateToDestination();
		} else if (curLoc.distanceSquaredTo(target) >= 10) {
			return nav.navigateToDestination();
		}

		return curLoc.directionTo(target);
	}

	private MapLocation getBestEnemyPastr() {
		final int numEnemyPastrs = enemyPastrs.length;
		double maxDistToEnemyHQ = 0;
		MapLocation targetPastr = null;

		for (int i = numEnemyPastrs; --i >= 0;) {
			final double distToEnemyHQ = ENEMY_HQ_LOCATION.distanceSquaredTo(enemyPastrs[i]);
			if (distToEnemyHQ > maxDistToEnemyHQ) {
				targetPastr = enemyPastrs[i];
				maxDistToEnemyHQ = distToEnemyHQ;
			}
		}

		return targetPastr;
	}

	private boolean isOptimalAdvancingDirection(Direction dir, MapLocation target, Direction dirToTarget) {
		final int dx = target.x-curLoc.x;
		final int dy = target.y-curLoc.y;
		switch(dx) {
		case -2:
			if(dy==1) return dir==Direction.WEST || dir==Direction.SOUTH_WEST;
			else if(dy==-1) return dir==Direction.WEST || dir==Direction.NORTH_WEST;
			break;
		case -1:
			if(dy==2) return dir==Direction.SOUTH || dir==Direction.SOUTH_WEST;
			else if(dy==-2) return dir==Direction.NORTH || dir==Direction.NORTH_WEST;
			break;
		case 1:
			if(dy==2) return dir==Direction.SOUTH || dir==Direction.SOUTH_EAST;
			else if(dy==-2) return dir==Direction.NORTH || dir==Direction.NORTH_EAST;
			break;
		case 2:
			if(dy==1) return dir==Direction.EAST || dir==Direction.SOUTH_EAST;
			else if(dy==-1) return dir==Direction.EAST || dir==Direction.NORTH_EAST;
			break;
		default:
			break;
		}
		return dir == dirToTarget;
	}

	private boolean isOptimalRetreatingDirection(Direction dir, MapLocation target) {
		int dx = curLoc.x-target.x;
		final int dy = curLoc.y-target.y;
		if(dx==0) {
			if(dy==0) return true;
			if(dy>0) return dir==Direction.SOUTH || dir==Direction.SOUTH_EAST || dir==Direction.SOUTH_WEST;
			return dir==Direction.NORTH || dir==Direction.NORTH_EAST || dir==Direction.NORTH_WEST;
		}
		if(dx>0) {
			if(dy>dx) return dir==Direction.SOUTH_EAST || dir==Direction.SOUTH;
			if(dy==dx) return dir==Direction.SOUTH_EAST || dir==Direction.SOUTH || dir==Direction.EAST;
			if(dy>0) return dir==Direction.EAST || dir==Direction.SOUTH_EAST;
			if(dy==0) return dir==Direction.EAST || dir==Direction.NORTH_EAST || dir==Direction.SOUTH_EAST;
			if(dy>-dx) return dir==Direction.EAST || dir==Direction.NORTH_EAST;
			if(dy==-dx) return dir==Direction.EAST || dir==Direction.NORTH_EAST || dir==Direction.NORTH;
			return dir==Direction.NORTH || dir==Direction.NORTH_EAST;
		}
		dx = -dx;
		if(dy>dx) return dir==Direction.SOUTH_WEST || dir==Direction.SOUTH;
		if(dy==dx) return dir==Direction.SOUTH_WEST || dir==Direction.SOUTH || dir==Direction.WEST;
		if(dy>0) return dir==Direction.WEST || dir==Direction.SOUTH_WEST;
		if(dy==0) return dir==Direction.WEST || dir==Direction.NORTH_WEST || dir==Direction.SOUTH_WEST;
		if(dy>-dx) return dir==Direction.WEST || dir==Direction.NORTH_WEST;
		if(dy==-dx) return dir==Direction.WEST || dir==Direction.NORTH_WEST || dir==Direction.NORTH;
		return dir==Direction.NORTH || dir==Direction.NORTH_WEST;
	}
}
