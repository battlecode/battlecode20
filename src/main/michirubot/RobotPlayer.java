package michirubot;
import battlecode.common.*;
import java.util.Arrays;

public class RobotPlayer {
    
	static RobotController rc = null;
	static int myAttackRange = 0;
	static Team myTeam;
	static Team enemyTeam;
	
	static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, 
		Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, 
		Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, 
		Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, 
		Direction.NORTH_WEST};
	
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
    **/
    public static void run(RobotController grisaia) {
		rc = grisaia;
		// You can instantiate variables here.
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();
		
		int CHANNEL = 1337;
		//MapLocation RALLY_LOCATION = new MapLocation(431,154);
		MapLocation RALLY_LOCATION = new MapLocation(449, 172);
		//MapLocation ENEMY_LOCATION = new MapLocation(466,189);
		MapLocation ENEMY_LOCATION = new MapLocation(449,172);
		int WAIT_TIME = 2000;
		boolean GO = false;
		int DELAY = 40;
        int numSoldiers = 150;
		int numGuards = 0;
		int numVipers = 0;
		int numTurrets = 0;
		
        if (rc.getType() == RobotType.ARCHON) {
            try {
				// Any code here gets executed exactly once at the beginning of the game.
            } catch (Exception e) {
                // Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
                // Caught exceptions will result in a bytecode penalty.
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
                try {
					//rc.broadcast(CHANNEL, locationToInt(RALLY_LOCATION));
                    if (!GO && rc.isCoreReady()) {
						if (numSoldiers > 0 && rc.getTeamParts() > RobotType.SOLDIER.partCost) {
							tryBuild(rc.getLocation().directionTo(RALLY_LOCATION), RobotType.SOLDIER);
							numSoldiers--;
						} else if (numGuards > 0 && rc.getTeamParts() > RobotType.GUARD.partCost) {
							tryBuild(rc.getLocation().directionTo(RALLY_LOCATION), RobotType.GUARD);
							numGuards--;
						} else if (numVipers > 0 && rc.getTeamParts() > RobotType.VIPER.partCost) {
							tryBuild(rc.getLocation().directionTo(RALLY_LOCATION), RobotType.VIPER);
							numVipers--;
						} else if (numTurrets > 0 && rc.getTeamParts() > RobotType.TURRET.partCost) {
							tryBuild(rc.getLocation().directionTo(RALLY_LOCATION), RobotType.TURRET);
							numTurrets--;
						} else if (DELAY > 0) {
							DELAY--;
						} else if (rc.getRoundNum() > WAIT_TIME) {
							RALLY_LOCATION = ENEMY_LOCATION;
							GO = true;
						}
					}
					
					Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
		} else if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.GUARD) {
            try {
				// Any code here gets executed exactly once at the beginning of the game.
                myAttackRange = rc.getType().attackRadiusSquared;
            } catch (Exception e) {
                // Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
                // Caught exceptions will result in a bytecode penalty.
				System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
                try {
					//RALLY_LOCATION = intToLocation(rc.readBroadcast(CHANNEL));
					
					attackMove(RALLY_LOCATION);
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (rc.getType() == RobotType.TURRET) {
            try {
                myAttackRange = rc.getType().attackRadiusSquared;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
				try {
                    //RALLY_LOCATION = intToLocation(rc.readBroadcast(CHANNEL));
					// If this robot type can attack, check for enemies within range and attack one
					if (rc.getType() == RobotType.TURRET) {
						if (!turretAttack()) {
							//pack up
							System.out.println("PACKING");
							rc.pack();
						}
					} else if (rc.getType() == RobotType.TTM) {
						RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(48, enemyTeam);
						RobotInfo[] nearbyZombies = rc.senseNearbyRobots(48, Team.ZOMBIE);
						if (nearbyEnemies.length > 0 || nearbyZombies.length > 0) {
							System.out.println("UNPACKING");
							rc.unpack();
						} else {
							navigate(RALLY_LOCATION);
						}
					}
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
			}
        } else if (rc.getType() == RobotType.VIPER) {
			try {
				// Any code here gets executed exactly once at the beginning of the game.
                myAttackRange = rc.getType().attackRadiusSquared;
            } catch (Exception e) {
                // Throwing an uncaught exception makes the robot die, so we need to catch exceptions.
                // Caught exceptions will result in a bytecode penalty.
				System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
                try {
					//RALLY_LOCATION = intToLocation(rc.readBroadcast(CHANNEL));
					attackMoveViper(RALLY_LOCATION);
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
		}
	}
	
	public static double viperAttackPriority(RobotInfo r) {
		RobotType t = r.type;
		int infectionPriority = 1000;
		if (r.viperInfectedTurns > 0) {
			infectionPriority = 0;
		}
		if (t.attackDelay == 0) {
			return infectionPriority;
		}
		return infectionPriority + (t.attackPower * 100 / r.health / t.attackDelay);
	}
	
	public static double attackPriority(RobotInfo r) {
		RobotType t = r.type;
		if (t.attackDelay == 0) {
			return 0;
		}
		return t.attackPower * 100 / r.health / t.attackDelay;
	}
	
	public static int locationToInt(MapLocation m) {
		return 10000*m.x+m.y;
	}
	
	public static MapLocation intToLocation(int i) {
		return new MapLocation(i/10000, i%10000);
	}
	
	public static int directionToInt(Direction d) {
		switch(d) {
			case NORTH:
				return 0;
			case NORTH_EAST:
				return 1;
			case EAST:
				return 2;
			case SOUTH_EAST:
				return 3;
			case SOUTH:
				return 4;
			case SOUTH_WEST:
				return 5;
			case WEST:
				return 6;
			case NORTH_WEST:
				return 7;
			default:
				return -1;
		}
	}
	
	public static void tryBuild(Direction dir, RobotType type) throws GameActionException {
		int dirInt = directionToInt(dir);
		int[] toTry = {0,1,7,2,6,3,5,4};
		for (int num : toTry) {
			Direction tryDir = directions[num + dirInt];
			if (rc.canMove(tryDir)) {
				rc.build(tryDir, type);
				return;
			}
		}
		return;
	}
	
	public static boolean turretAttack() throws GameActionException {
		RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
		RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(80, enemyTeam);
		RobotInfo[] nearbyZombies = rc.senseNearbyRobots(80, Team.ZOMBIE);
		if (enemiesWithinRange.length > 0) {
			if (rc.isWeaponReady()) {
				double lowestHealth = 9999999;
				RobotInfo enemyToAttack = null;
				for (RobotInfo enemy : enemiesWithinRange) {
					// Check whether the enemy is in a valid attack range (turrets have a minimum range)
					if (enemy.health < lowestHealth && rc.canAttackLocation(enemy.location)) {
						lowestHealth = enemy.health;
						enemyToAttack = enemy;
					}
				}
				if (enemyToAttack != null) {
					rc.attackLocation(enemyToAttack.location);
				}
			}
			return true;
		}
		
		else if (zombiesWithinRange.length > 0) {
			if (rc.isWeaponReady()) {
				double lowestHealth = 9999999;
				RobotInfo zombieToAttack = null;
				for (RobotInfo zombie : zombiesWithinRange) {
					// Check whether the enemy is in a valid attack range (turrets have a minimum range)
					if (zombie.health < lowestHealth && rc.canAttackLocation(zombie.location)) {
						lowestHealth = zombie.health;
						zombieToAttack = zombie;
					}
				}
				if (zombieToAttack != null) {
					rc.attackLocation(zombieToAttack.location);
				}
			}
			return true;
		} else if (nearbyEnemies.length > 0 || nearbyZombies.length > 0) {
			return true;
		}
		return false;
	}
	
	public static void attackMoveViper(MapLocation targetloc) throws GameActionException {
		RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
		RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(80, enemyTeam);
		if (enemiesWithinRange.length > 0) {
			if (rc.isWeaponReady()) {
				double highestPriority = -1;
				RobotInfo enemyToAttack = null;
				for (RobotInfo enemy : enemiesWithinRange) {
					// Check whether the enemy is in a valid attack range (turrets have a minimum range)
					double priority = viperAttackPriority(enemy);
					if (priority > highestPriority && rc.canAttackLocation(enemy.location)) {
						highestPriority = priority;
						enemyToAttack = enemy;
					}
				}
				if (enemyToAttack != null) {
					rc.attackLocation(enemyToAttack.location);
				}
			}
		}
		
		else if (zombiesWithinRange.length > 0) {
			if (rc.isWeaponReady()) {
				double highestPriority = -1;
				RobotInfo zombieToAttack = null;
				for (RobotInfo zombie : zombiesWithinRange) {
					// Check whether the enemy is in a valid attack range (turrets have a minimum range)
					double priority = viperAttackPriority(zombie);
					if (priority > highestPriority && rc.canAttackLocation(zombie.location)) {
						highestPriority = priority;
						zombieToAttack = zombie;
					}
				}
				if (zombieToAttack != null) {
					rc.attackLocation(zombieToAttack.location);
				}
			}
		} else {
			if (nearbyEnemies.length > 0) {
				MapLocation closestEnemyLoc = null;
				int closestDistance = 999999;
				for (RobotInfo enemy : nearbyEnemies) {
					int currDistance = enemy.location.distanceSquaredTo(rc.getLocation());
					if (currDistance < closestDistance) {
						closestEnemyLoc = enemy.location;
						closestDistance = currDistance;
					}
				}
				navigate(closestEnemyLoc);
			} else {
				navigate(targetloc);
			}
		}
	}
	
	public static void attackMove(MapLocation targetloc) throws GameActionException {
		RobotInfo[] enemiesWithinRange = rc.senseNearbyRobots(myAttackRange, enemyTeam);
		RobotInfo[] zombiesWithinRange = rc.senseNearbyRobots(myAttackRange, Team.ZOMBIE);
		RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(80, enemyTeam);
		RobotInfo[] nearbyZombies = rc.senseNearbyRobots(80, Team.ZOMBIE);
		if (enemiesWithinRange.length > 0) {
			if (rc.isWeaponReady()) {
				double highestPriority = -1;
				RobotInfo enemyToAttack = null;
				for (RobotInfo enemy : enemiesWithinRange) {
					// Check whether the enemy is in a valid attack range (turrets have a minimum range)
					double priority = attackPriority(enemy);
					if (priority > highestPriority && rc.canAttackLocation(enemy.location)) {
						highestPriority = priority;
						enemyToAttack = enemy;
					}
				}
				if (enemyToAttack != null) {
					rc.attackLocation(enemyToAttack.location);
				}
			}
		}
		
		else if (zombiesWithinRange.length > 0) {
			if (rc.isWeaponReady()) {
				double highestPriority = -1;
				RobotInfo zombieToAttack = null;
				for (RobotInfo zombie : zombiesWithinRange) {
					// Check whether the enemy is in a valid attack range (turrets have a minimum range)
					double priority = attackPriority(zombie);
					if (priority > highestPriority && rc.canAttackLocation(zombie.location)) {
						highestPriority = priority;
						zombieToAttack = zombie;
					}
				}
				if (zombieToAttack != null) {
					rc.attackLocation(zombieToAttack.location);
				}
			}
		} else {
			if (nearbyEnemies.length > 0) {
				MapLocation closestEnemyLoc = null;
				int closestDistance = 999999;
				for (RobotInfo enemy : nearbyEnemies) {
					int currDistance = enemy.location.distanceSquaredTo(rc.getLocation());
					if (currDistance < closestDistance) {
						closestEnemyLoc = enemy.location;
						closestDistance = currDistance;
					}
				}
				navigate(closestEnemyLoc);
			} else if (nearbyZombies.length > 0) {
				MapLocation closestZombieLoc = null;
				int closestDistance = 999999;
				for (RobotInfo zombie : nearbyZombies) {
					int currDistance = zombie.location.distanceSquaredTo(rc.getLocation());
					if (currDistance < closestDistance) {
						closestZombieLoc = zombie.location;
						closestDistance = currDistance;
					}
				}
				navigate(closestZombieLoc);
			} else {
				navigate(targetloc);
			}
		}
	}
	
	public static void navigate(MapLocation targetloc) throws GameActionException {
		if (!rc.isCoreReady() || rc.getLocation().equals(targetloc)) {
			return;
		}
		Direction dirToTarget = rc.getLocation().directionTo(targetloc);
		Direction[] candidateDirections = {dirToTarget, dirToTarget.rotateLeft(), dirToTarget.rotateRight(), 
			dirToTarget.rotateLeft().rotateLeft(), dirToTarget.rotateRight().rotateRight()};
		MapLocation[] candidateLocations = {rc.getLocation().add(candidateDirections[0]), 
			rc.getLocation().add(candidateDirections[1]), rc.getLocation().add(candidateDirections[2]), 
			rc.getLocation().add(candidateDirections[3]), rc.getLocation().add(candidateDirections[4])};
		int[] scores = {0,0,0,0,0};
		// high score is bad
		int bestIndex = -1;
		int currDistance = rc.getLocation().distanceSquaredTo(targetloc);
		int bestScore = 1000 + currDistance;
		for (int i=0; i<5; i++) {
			if (rc.isLocationOccupied(candidateLocations[i])) {
				scores[i] += 10000;
			} else if (!rc.canMove(candidateDirections[i])) {
				scores[i] += 1000;
			}
			scores[i] += candidateLocations[i].distanceSquaredTo(targetloc);
			if (scores[i] < bestScore) {
				bestScore = scores[i];
				bestIndex = i;
			}
		}
		if (bestIndex >= 0 && rc.canMove(candidateDirections[bestIndex]) && bestScore < currDistance) {
			rc.move(candidateDirections[bestIndex]);
			return;
		} else if (bestIndex >= 0 && !rc.isLocationOccupied(candidateLocations[bestIndex]) && bestScore < currDistance) {
			rc.clearRubble(candidateDirections[bestIndex]);
			return;
		} else {
			// Every location is occupied. Clear rubble if not doing anything
			for (int i=0; i<5; i++) {
				if (rc.senseRubble(candidateLocations[i]) > GameConstants.RUBBLE_OBSTRUCTION_THRESH) {
					rc.clearRubble(candidateDirections[i]);
					return;
				}
			}
			for (int i=0; i<5; i++) {
				if (rc.senseRubble(candidateLocations[i]) > GameConstants.RUBBLE_SLOW_THRESH) {
					rc.clearRubble(candidateDirections[i]);
					return;
				}
			}
		}
	}
}
