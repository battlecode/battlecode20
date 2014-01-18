package yourmom;

import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;

public class RadarSystem {
	BaseRobot br;

	public final static int MAX_ROBOTS = 4096;
	public final static int MAX_ENEMY_ROBOTS = 50;
	public final static int MAX_ADJACENT = 17;

	public final RobotInfo[] allyInfos = new RobotInfo[MAX_ROBOTS];
	public final int[] allyTimes = new int[MAX_ROBOTS];
	public final int[] allyRobots = new int[MAX_ROBOTS];

	public int numAdjacentAllies;
	public int numAllyRobots;
	public int numAllyFighters;
	public int alliesOnLeft;
	public int alliesOnRight;
	public int alliesInFront;

	public final RobotInfo[] enemyInfos = new RobotInfo[MAX_ROBOTS];
	public final int[] enemyTimes = new int[MAX_ROBOTS];
	public final int[] enemyRobots = new int[MAX_ENEMY_ROBOTS];
	public int numEnemyRobots;
	public int numEnemySoldiers;
	public int numEnemyNoisetowers;
	public int numEnemyPastrs;

	public int vecEnemyX;
	public int vecEnemyY;

	public int centerEnemyX;
	public int centerEnemyY;

	public int centerAllyX;
	public int centerAllyY;

	public int roundsSinceEnemySighted;

	public int lastScanRound;
	public boolean needToScanEnemies;
	public boolean needToScanAllies;

	public Robot[] robots;

	public RobotInfo closestEnemy;
	public int closestEnemyDist;

	public RobotInfo lowestHealthAllied;

	final boolean cachePositions;

	public RadarSystem(BaseRobot br) {
		this.br = br;
		lastScanRound = -1;
		needToScanAllies = true;
		needToScanEnemies = true;
		robots = null;
		cachePositions = true;
	}

	private void resetEnemyStats() {
		closestEnemy = null;
		closestEnemyDist = Integer.MAX_VALUE;
		numEnemyRobots = 0;
		numEnemyPastrs = 0;
		numEnemyNoisetowers = 0;
		numEnemySoldiers = 0;

		vecEnemyX = 0;
		vecEnemyY = 0;
		centerEnemyX = 0;
		centerEnemyY = 0;
	}

	private void resetAllyStats() {
		numAdjacentAllies = 0;
		numAllyRobots = 0;
		numAllyFighters = 0;
		alliesOnLeft = 0;
		alliesOnRight = 0;
		alliesInFront = 0;
		lowestHealthAllied = null;

		centerAllyX = 0;
		centerAllyY = 0;
	}

	private void addEnemy(RobotInfo rinfo) throws GameActionException {
		int pos = rinfo.robot.getID();

		MapLocation enemyLoc = rinfo.location;
		int dist = enemyLoc.distanceSquaredTo(br.curLoc);

		switch (rinfo.type) {
		case SOLDIER:
			numEnemySoldiers++;
			break;
		case PASTR:
			numEnemyPastrs++;
			break;
		case NOISETOWER:
			numEnemyNoisetowers++;
			break;
		}

		enemyInfos[pos] = rinfo;
		enemyTimes[pos] = Clock.getRoundNum();
		enemyRobots[numEnemyRobots++] = pos;

		centerEnemyX += enemyLoc.x;
		centerEnemyY += enemyLoc.y;
		if (dist < closestEnemyDist) {
			closestEnemy = rinfo;
			closestEnemyDist = dist;
		}
	}

	private void addAlly(RobotInfo rinfo) throws GameActionException {
		int pos = rinfo.robot.getID();
		allyRobots[numAllyRobots++] = pos;
		allyInfos[pos] = rinfo;
		allyTimes[pos] = Clock.getRoundNum();

		int ddir = (br.curLoc.directionTo(rinfo.location).ordinal() - br.curDir.ordinal() + 8) % 8;
		if (ddir >= 5) {
			alliesOnLeft++;
		} else if (ddir >= 1 && ddir <= 3) {
			alliesOnRight++;
		}
		if (ddir <= 1 || ddir == 7) {
			alliesInFront++;
		}
	}

	/**
	 * Call scan to populate radar information. Ally and Enemy information is
	 * guaranteed only to be correct if scanAllies and/or scanEnemies is set to
	 * true.
	 *
	 * @param scanAllies enable ally data collection and scanning
	 * @param scanEnemies enable enemy data collection and scanning
	 */
	 public void scan(boolean scanAllies, boolean scanEnemies) {
	 	if (lastScanRound < br.curRound) {
	 		needToScanAllies = true;
	 		needToScanEnemies = true;
	 		lastScanRound = br.curRound;
	 		robots = br.rc.senseNearbyGameObjects(Robot.class);
	 	}

	 	if (scanAllies) {
	 		if (needToScanAllies) {
	 			needToScanAllies = false;
	 		} else {
	 			scanAllies = false;
	 		}
	 	}

 		if (scanEnemies) {
 			if (needToScanEnemies) {
 				needToScanEnemies = false;
 			} else {
 				scanEnemies = false;
 			}
 		}

 		if (scanAllies || scanEnemies) {
 			Robot[] robots = this.robots;

 			// reset stat collection
 			if (scanEnemies) {
 				resetEnemyStats();
 			}
 			if (scanAllies) {
 				resetAllyStats();
 			}

 			// Bring some vars into local space for the raep loop
 			RobotController rc = br.rc;
 			Team myTeam = br.myTeam;
 			switch (br.myType) {
 			case SOLDIER:
 			case PASTR:
 			case NOISETOWER:
 			case HQ:
 			default:
	 			for (int idx = robots.length; --idx >= 0;) {
	 				Robot r = robots[idx];
	 				try {
	 					if (myTeam == r.getTeam()) {
	 						if (scanAllies) {
	 							addAlly(rc.senseRobotInfo(r));
	 						}
	 					} else {
	 						if (scanEnemies) {
	 							addEnemy(rc.senseRobotInfo(r));
	 						}
	 					}
	 				} catch (Exception e) {
	 					//e.printStackTrace();
	 				}
	 			}
 				break;
 			}

 			if (scanEnemies) {
 				if (numEnemyRobots == 0) {
 					centerEnemyX = centerEnemyY = -1;
 					vecEnemyX = br.curLoc.x;
 					vecEnemyY = br.curLoc.y;
 				} else {
 					centerEnemyX /= numEnemyRobots;
 					centerEnemyY /= numEnemyRobots;
 					vecEnemyX = centerEnemyX - br.curLoc.x;
 					vecEnemyY = centerEnemyY - br.curLoc.y;
 				}

 				// compute global statistics
 				if (numEnemyRobots == 0) {
 					roundsSinceEnemySighted++;
 				} else {
 					roundsSinceEnemySighted = 0;
 				}
 			}

 			if (scanAllies) {
 				if (numAllyFighters == 0) {
 					centerAllyX = centerAllyY = -1;
 				} else {
 					centerAllyX /= numAllyFighters;
 					centerAllyY /= numAllyFighters;
 				}
 			}
 		}
	}

	/**
	 * Check if we have scanned enemies this round
	 */
	public boolean hasScannedEnemies() {
		return (lastScanRound == br.curRound && !needToScanEnemies);
	}

	/**
	 * Check if we have scanned allies this round
	 */
	public boolean hasScannedAllies() {
		return (lastScanRound == br.curRound && !needToScanAllies);
	}

	/**
	 * Get the difference between the two swarms
	 */
	public double getArmyDifference() {
		return numAllyRobots - numEnemyRobots;
	}

	/**
	 * Gets the calculated swarm target in order to chase an enemy swarm
	 */
	public MapLocation getEnemySwarmTarget() {
		double a = Math.sqrt(vecEnemyX * vecEnemyX + vecEnemyY * vecEnemyY) + 0.001;
		return new MapLocation(
			(int)(vecEnemyX * 7 / a) + br.curLoc.x,
			(int)(vecEnemyY * 7 / a) + br.curLoc.y
		);
	}

	/**
	 * Gets the calculated enemy swarm center
	 */
	public MapLocation getEnemySwarmCenter() {
		return new MapLocation(centerEnemyX, centerEnemyY);
	}

	/**
	 * Gets the calculated ally swarm center
	 */
	public MapLocation getAllySwarmCenter() {
		return new MapLocation(centerAllyX, centerAllyY);
	}
}