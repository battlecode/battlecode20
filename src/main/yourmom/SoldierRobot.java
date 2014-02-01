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
import battlecode.common.Team;

public class SoldierRobot extends BaseRobot {
	SoldierState soldierState = SoldierState.NEW;
	SoldierState nextSoldierState;
	MapLocation enemySpottedTarget;
	MapLocation[] whereToPastr;
	MapLocation[] whereToNoisetower;
	double healthLastTurn;
	MapLocation rallyPoint;

	final MapLocation DEFAULT_RALLY_LOCATION;

	public SoldierRobot(RobotController myRC) throws GameActionException {
		super(myRC);

		whereToPastr = io.readPastrLocations();
		whereToNoisetower = io.readNoisetowerLocations();

		nav.setNavigationMode(NavigationMode.BUG);
		DEFAULT_RALLY_LOCATION = new MapLocation(
			(3 * MY_HQ_LOCATION.x + ENEMY_HQ_LOCATION.x) / 4,
			(3 * MY_HQ_LOCATION.y + ENEMY_HQ_LOCATION.y) / 4
		);
		rallyPoint = DEFAULT_RALLY_LOCATION;
	}

	@Override
	public void run() throws GameActionException {
		rc.setIndicatorString(0, soldierState.toString());

		//boolean gotHitLastRound = curHealth < healthLastTurn;

		radar.scan();

		if (curRound == 1940) {
			switch (rc.senseNearbyGameObjects(Robot.class, myType.sensorRadiusSquared, enemyTeam).length) {
			case 0:
				soldierState = SoldierState.BUILD_PASTR;
			}
		} /*else if (curLoc.equals(whereToPastr[0])) {
			switch (rc.senseNearbyGameObjects(Robot.class, myType.sensorRadiusSquared, enemyTeam).length) {
			case 0:
				soldierState = SoldierState.BUILD_PASTR;
			}
		} else if (curLoc.equals(whereToNoisetower[0])) {
			switch (rc.senseNearbyGameObjects(Robot.class, myType.sensorRadiusSquared, enemyTeam).length) {
			case 0:
				soldierState = SoldierState.BUILD_NOISETOWER;
			}
		}*/ else {
			final MapLocation bestEnemyPastrLoc = getBestEnemyPastr();
			if (bestEnemyPastrLoc != null) {
				rallyPoint = bestEnemyPastrLoc;
			} else {
				switch (myPastrs.length) {
				case 0:
					switch (MAP_SIZE) {
					case SMALL:
						if (curRound >= 1000 && radar.numAllySoldiers >= 12 && myPastrs.length == 0) {
							rallyPoint = whereToPastr[0];
						} else {
							rallyPoint = ENEMY_HQ_LOCATION;
						}
						break;
					case MEDIUM:
						if (curRound >= 500 && radar.numAllySoldiers >= 8 && myPastrs.length == 0) {
							rallyPoint = whereToPastr[0];
						} else {
							rallyPoint = ENEMY_HQ_LOCATION;
						}
						break;
					case LARGE:
						if (radar.numAllySoldiers >= 4 && myPastrs.length == 0) {
							rallyPoint = whereToPastr[0];
						} else {
							rallyPoint = DEFAULT_RALLY_LOCATION;
						}
						break;
					}
					break;
				default:
					// rally at my pastr
					final int whichPastr = (int)(Util.randDouble() * myPastrs.length);
					rallyPoint = myPastrs[whichPastr];
					break;
				}
			}
		}

		//final Message retreatMsg = io.read(ChannelType.RETREAT_CHANNEL);
		//switch (retreatMsg.body) {
		//case Constants.RETREAT:
		//	System.out.println("retreating");
		//	soldierState = SoldierState.RETREAT;
		//}

		switch (soldierState) {
		case NEW:
			newCode();
			break;
		case FIGHTING:
			fightingCode();
			break;
		case RALLYING:
			rallyingCode();
			break;
		case ALL_IN:
			allInCode();
			break;
		case PUSHING:
			pushingCode();
			break;
		case RETREAT:
			retreatCode();
			break;
		case BUILD_NOISETOWER:
			buildNoiseTowerCode();
			break;
		case BUILD_PASTR:
			buildPastrCode();
			break;
		default:
			break;
		}

		attackMove();

		if (nextSoldierState != null) {
			soldierState = nextSoldierState;
			nextSoldierState = null;
		}
	}

	private void newCode() throws GameActionException {
		if (curRound < 50) {
			nextSoldierState = SoldierState.PUSHING;
			pushingCode();
		} else {
			nextSoldierState = SoldierState.RALLYING;
			rallyingCode();
		}
	}

	private void pushingCode() throws GameActionException {
		if (radar.numNearbyEnemySoldiers > 0) {
			nextSoldierState = SoldierState.FIGHTING;
			fightingCode();
		} else {
			pushCodeGetCloser();
		}
	}

	private void pushCodeGetCloser() throws GameActionException {
		nav.setDestination(rallyPoint);
	}

	private void rallyingCode() throws GameActionException {
		if (radar.numNearbyEnemySoldiers > 0) {
			if (radar.numNearbyEnemySoldiers > radar.numNearbyAllySoldiers + 1) {
				nextSoldierState = SoldierState.RETREAT;
				retreatCode();
			} else {
				nextSoldierState = SoldierState.FIGHTING;
				fightingCode();
			}
		} else {
			nav.setDestination(rallyPoint);
		}
	}

	private void retreatCode() throws GameActionException {
		if (radar.numNearbyEnemySoldiers == 0) {
			nextSoldierState = SoldierState.RALLYING;
			rallyingCode();
		} else if (radar.numNearbyAllySoldiers >= Constants.FIGHTING_NOT_ENOUGH_ALLIED_SOLDIERS) {
			nextSoldierState = SoldierState.FIGHTING;
			fightingCode();
		} else {
			nav.setDestination(rallyPoint);
		}
	}

	private void fightingCode() throws GameActionException {
		if (radar.numNearbyEnemySoldiers == 0) {
			if (radar.numNearbyAllySoldiers < Constants.FIGHTING_NOT_ENOUGH_ALLIED_SOLDIERS) {
				if (50 <= curRound) {
					nextSoldierState = SoldierState.PUSHING;
					pushingCode();
				} else {
					nextSoldierState = SoldierState.RALLYING;
					rallyingCode();
				}
			} else {
				nextSoldierState = SoldierState.PUSHING;
				pushingCode();
			}
		} else {
			microCode();
		}
	}

	private void allInCode() throws GameActionException {
		microCode();
	}

	private void microCode() throws GameActionException {
		Robot[] enemiesList = rc.senseNearbyGameObjects(
			Robot.class,
			myType.sensorRadiusSquared,
			enemyTeam
		);
		int[] closestEnemyInfo = getClosestEnemy(enemiesList);
		final MapLocation closestEnemyLocation = new MapLocation(closestEnemyInfo[1], closestEnemyInfo[2]);
		final int enemyDistSquared = closestEnemyLocation.distanceSquaredTo(curLoc);

		if (enemyDistSquared <= myType.attackRadiusMaxSquared + 5) { // if there is enemy in one dist
			double[] our23 = getEnemies2Or3StepsAway();
			if (our23[0] < 1) {
				if (our23[1] >= 1) {
					nav.setDestination(curLoc.subtract(curDir));
				}
			}
		} else if (enemyDistSquared == 25 || enemyDistSquared > 27) {
			nav.setDestination(curLoc.add(curDir));
		} else {
			double[] our23 = getEnemies2Or3StepsAway();
			double[] enemy23 = getEnemies2Or3StepsAwaySquare(closestEnemyLocation, enemyTeam);
			if (our23[1] > 0) {
				if (enemy23[0] > 0) {
					nav.setDestination(closestEnemyLocation);
				} else {
					if (enemy23[1] + enemy23[0] > our23[1] + our23[2] + 1 || our23[1] + our23[2] < 1) {
						nav.setDestination(closestEnemyLocation);
					} else {
						nav.setDestination(curLoc.subtract(curDir));
					}
				}
			} else {
				if (enemy23[0] > 0) {
					nav.setDestination(closestEnemyLocation);
				} else if (enemy23[1] > 0) {
					int closestDist = 100;
					int dist;
					MapLocation closestAllyLocation = null;
					Robot[] twoDistAllies = rc.senseNearbyGameObjects(Robot.class, closestEnemyLocation, 8, myTeam);
					switch (twoDistAllies.length) {
					case 0:
						return;
					}

					for (int i = twoDistAllies.length; --i >= 0;) {
						final Robot ally = twoDistAllies[i];
						RobotInfo aRobotInfo = rc.senseRobotInfo(ally);
						dist = aRobotInfo.location.distanceSquaredTo(curLoc);
						if (dist < closestDist) {
							closestDist = dist;
							closestAllyLocation = aRobotInfo.location;
						}
					}

					double[] ally23 = getEnemies2Or3StepsAwaySquare(closestAllyLocation, myTeam);

					if (enemy23[0] + enemy23[1] + enemy23[2] > ally23[1] + ally23[2]) {
						nav.setDestination(closestEnemyLocation);
					} else {
						nav.setDestination(curLoc.subtract(curDir));
					}
				} else {
					if (enemy23[2] - our23[2] >= 3 || our23[2] < 1) {
						nav.setDestination(closestEnemyLocation);
					} else {
						nav.setDestination(curLoc.subtract(curDir));
					}
				}
			}
		}
	}

	private double[] getEnemies2Or3StepsAway() throws GameActionException {
		double count1 = 0;
		double count2 = 0;
		double count3 = 0;
		final Robot[] enemiesInVision = rc.senseNearbyGameObjects(Robot.class, 18, enemyTeam);
		for (int i = enemiesInVision.length; --i >= 0;) {
			final Robot enemy = enemiesInVision[i];
			final RobotInfo rInfo = rc.senseRobotInfo(enemy);
			final int dist = rInfo.location.distanceSquaredTo(curLoc);
			if (rInfo.type == RobotType.SOLDIER && rInfo.actionDelay < 2.0) {
				if (dist <= 2) {
					++count1;
				} else if (dist <= 8) {
					++count2;
				} else if (dist > 8 && (dist <= 14 || dist == 18)) {
					++count3;
				}
			} else {
				if (dist <= 2) {
					count1 += 0.2;
				} else if (dist <= 8) {
					count2 += 0.2;
				} else if (dist > 8 && (dist <= 14 || dist == 18)) {
					count3 += 0.2;
				}
			}
		}

		double output[] = {count1, count2, count3};
		return output;
	}

	private double[] getEnemies2Or3StepsAwaySquare(MapLocation square, Team squareTeam) throws GameActionException {
		double count1 = 0;
		double count2 = 0;
		double count3 = 0;
		final Robot[] enemiesInVision = rc.senseNearbyGameObjects(Robot.class, 18, squareTeam.opponent());
		for (int i = enemiesInVision.length; --i >= 0;) {
			final Robot enemy = enemiesInVision[i];
			final RobotInfo rInfo = rc.senseRobotInfo(enemy);
			final int dist = rInfo.location.distanceSquaredTo(curLoc);
			if (rInfo.type == RobotType.SOLDIER && rInfo.actionDelay < 2.0) {
				if (dist <= 2) {
					++count1;
				} else if (dist <= 8) {
					++count2;
				} else if (dist > 8 && (dist <= 14 || dist == 18)) {
					++count3;
				}
			} else {
				if (dist <= 2) {
					count1 += 0.2;
				} else if (dist <= 8) {
					count2 += 0.2;
				} else if (dist > 8 && (dist <= 14 || dist == 18)) {
					count3 += 0.2;
				}
			}
		}

		final int selfDist = square.distanceSquaredTo(curLoc);

		if (selfDist <= 2) {
			++count1;
		} else if (selfDist <= 8) {
			++count2;
		} else if (selfDist <= 14 || selfDist == 18) {
			++count3;
		}

		double output[] = {count1, count2, count3};
		return output;
	}

	private int[] getClosestEnemy(Robot[] enemyRobots) throws GameActionException {
		int closestDist = curLoc.distanceSquaredTo(ENEMY_HQ_LOCATION);
		MapLocation closestEnemy = ENEMY_HQ_LOCATION;
		int dist = 0;
		for (int i = enemyRobots.length; --i >= 0;) {
			final RobotInfo aRobotInfo = rc.senseRobotInfo(enemyRobots[i]);
			dist = aRobotInfo.location.distanceSquaredTo(curLoc);
			if (dist < closestDist) {
				closestDist = dist;
				closestEnemy = aRobotInfo.location;
			}
		}

		int[] output = new int[4];
		output[0] = closestDist;
		output[1] = closestEnemy.x;
		output[2] = closestEnemy.y;

		return output;
	}

	private void attackMove() throws GameActionException {
		if (!rc.isActive()) {
			return;
		}

		Robot[] allies32 = rc.senseNearbyGameObjects(Robot.class,32,myTeam);

		switch (radar.numNearbyEnemySoldiers) {
		case 0: {
			Direction dir = nav.navigateToDestination();
			if (dir != null && rc.isActive() && rc.canMove(dir)) {
				final MapLocation newLoc = curLoc.add(dir);
				if (newLoc.distanceSquaredTo(ENEMY_HQ_LOCATION) > RobotType.HQ.attackRadiusMaxSquared) {
					rc.setIndicatorString(1, nav.getDestination().toString());
					rc.move(dir);
				}
			}
			break;
		}
		default: {
			RobotInfo info = rc.senseRobotInfo(radar.nearbyEnemySoldiers[0]);

			double lowestHP = info.health;
			MapLocation weakestEnemy = info.location;
			for (int i=1; i<radar.numNearbyEnemySoldiers; i++) {
				RobotInfo r = rc.senseRobotInfo(radar.nearbyEnemySoldiers[i]);
				if (r.health < lowestHP && r.type != RobotType.HQ) {
					lowestHP = r.health;
					weakestEnemy = r.location;
				}
			}
			if (radar.numNearbyEnemySoldiers > allies32.length) {
				if (rc.senseNearbyGameObjects(Robot.class,10,enemyTeam).length == 0) {
					return;
				}
				rc.attackSquare(weakestEnemy);
				return;
			}
			rc.attackSquare(weakestEnemy);
			return;
		}
		}
	}

	private void buildPastrCode() throws GameActionException {
		if (rc.isActive()) {
			rc.construct(RobotType.PASTR);
			rc.yield();
		}
		nextSoldierState = SoldierState.BUILD_PASTR;
	}

	private void buildNoiseTowerCode() throws GameActionException {
		if (rc.isActive()) {
			rc.construct(RobotType.NOISETOWER);
			rc.yield();
		}
		nextSoldierState = SoldierState.BUILD_NOISETOWER;
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
