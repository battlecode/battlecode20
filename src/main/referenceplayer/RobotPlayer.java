package referenceplayer;

import battlecode.common.*;
import java.util.Random;
import java.util.ArrayList;

public class RobotPlayer {
	static RobotController rc = null;
    
    static int INFINITY = 2000;
    static int SHORTRANGE = 200;
    static Team myTeam = null;
    static Team enemyTeam = null;
    
    static boolean leader = true;
    static int LEADER_ELECTION_SIGNAL = 192837;
    static int RALLY_SIGNAL = 853765;
    static int TARGET_SIGNAL = 474727;
    
    static int myAttackRange = 0;
    static MapLocation rallyPoint = null;
    static MapLocation patrolPoint = null;
    
    static Random rand = null;
    
    static ArrayList<MapLocation> targets = null;
    
    static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, 
		Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, 
		Direction.NORTH_WEST, Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, 
		Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, 
		Direction.NORTH_WEST};
    
    public static void run(RobotController battlecode) throws GameActionException{
        rc = battlecode;
        rand = new Random(rc.getID());
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();
        myAttackRange = rc.getType().attackRadiusSquared;
        
        rallyPoint = rc.getLocation();
        
        if (rc.getType() != RobotType.ARCHON) {
            leader = false;
        }
        
        if (rc.getType() == RobotType.ARCHON) {
            
            try {
				// Any code here gets executed exactly once at the beginning of the game.
                rc.broadcastMessageSignal(LEADER_ELECTION_SIGNAL, 0, INFINITY);
                processMessages();
                if (leader) {
                    rc.broadcastMessageSignal(RALLY_SIGNAL, 0, INFINITY);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
                try {
					processMessages();
                    
                    if (leader) {
                        rc.setIndicatorString(0, "I'm LEADER");
                        if (rc.getLocation() != rallyPoint) {
                            rallyPoint = rc.getLocation();
                            rc.broadcastMessageSignal(RALLY_SIGNAL, 0, INFINITY);
                        } else {
                            rc.broadcastMessageSignal(RALLY_SIGNAL, 0, SHORTRANGE);
                        }
                        
                        if (rc.isCoreReady()) {
                            RobotInfo[] nearbyAllies = rc.senseNearbyRobots(INFINITY, myTeam);
                            
                            int numTurrets = 0;
                            int numScouts = 0;
                            
                            for (RobotInfo ally : nearbyAllies) {
                                if (ally.type == RobotType.TURRET || ally.type == RobotType.TTM) {
                                    numTurrets++;
                                } else if (ally.type == RobotType.SCOUT) {
                                    numScouts++;
                                }
                            }
                            
                            if (rc.getRoundNum() < 100 && rc.getTeamParts() >= RobotType.SOLDIER.partCost) {
                                tryBuild(directions[rand.nextInt(8)], RobotType.SOLDIER);
                            } else if (rc.getRoundNum() >= 100 && rc.getTeamParts() >= RobotType.TURRET.partCost) {
                                tryBuild(directions[rand.nextInt(8)], RobotType.TURRET);
                            } else if (rc.getRoundNum() >= 100 && numScouts < 3) {
                                tryBuild(directions[rand.nextInt(8)], RobotType.SCOUT);
                            }
                        }
                    } else {
                        if (rc.getLocation().distanceSquaredTo(rallyPoint) > 2) {
                            navigate(rallyPoint);
                        }
                        
                        if (rc.isWeaponReady()) {
                            RobotInfo[] nearbyAllies = rc.senseNearbyRobots(myAttackRange, myTeam);
                            double highestPriority = -1;
                            MapLocation priorityLoc = null;
                            for (RobotInfo ally : nearbyAllies) {
                                if (ally.health == ally.type.maxHealth || ally.type == RobotType.ARCHON) {
                                    continue;
                                }
                                double allyPriority = attackPriority(ally);
                                if (allyPriority > highestPriority) {
                                    priorityLoc = ally.location;
                                    highestPriority = allyPriority;
                                }
                            }
                            if (priorityLoc != null) {
                                rc.repair(priorityLoc);
                            }
                        }   
                        
                        if (rc.isCoreReady() && rc.getLocation().distanceSquaredTo(rallyPoint) <= 2) {
                            Direction patrolDir = rc.getLocation().directionTo(rallyPoint).rotateLeft();
                            if (rc.canMove(patrolDir)) {
                                rc.move(patrolDir);
                            }
                        }
                        
                    }
                    
                    
                    
					Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
		} else if (rc.getType() == RobotType.SOLDIER) {
            patrolPoint = rc.getLocation();
            while (true) {
                // This is a loop to prevent the run() method from returning. Because of the Clock.yield()
                //  at the end of it, the loop will iterate once per game round.
                try {
					processMessages();
                    
                    if (rc.getLocation().distanceSquaredTo(rallyPoint) > 35) {
                        attackMove(rallyPoint);
                    } else {
                        attackMove(patrolPoint);
                    }
                    
                    if (rc.isCoreReady()) {
                        RobotInfo[] adjacentAllies = rc.senseNearbyRobots(2, myTeam);
                        if (adjacentAllies.length > 4) {
                            disperse();
                            patrolPoint = rc.getLocation();
                        }
                    }
                    
					Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (rc.getType() == RobotType.TTM || rc.getType() == RobotType.TURRET) {
            while (true) {
                try {
                    targets = new ArrayList<MapLocation>();
                    if (rc.getType() == RobotType.TTM) {
                        processMessages();
                        RobotInfo[] adjacentAllies = rc.senseNearbyRobots(2, myTeam);
                        if (!checkerboard() && rc.isCoreReady()) {
                            turretNavigateTowards(directions[rand.nextInt(8)]);
                        } else if (checkerboard()) {
                            rc.unpack();
                        }
                    } else {
                        processMessages();
                        turretAttack();
                        
                        if (rc.isWeaponReady()) {
                            for (int i=0; i<targets.size(); i++) {
                                if (rc.canSenseLocation(targets.get(i))) {
                                    continue;
                                } else if (rc.canAttackLocation(targets.get(i))) {
                                    rc.attackLocation(targets.get(i));
                                    break;
                                }
                            }
                        }
                        
                        if (!checkerboard()) {
                            rc.pack();
                        }
                    }
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (rc.getType() == RobotType.SCOUT) {
            while (true) {
                try {
                    RobotInfo[] nearbyEnemies = rc.senseHostileRobots(rc.getLocation(), INFINITY);
                    for (RobotInfo enemy : nearbyEnemies) {
                        rc.broadcastMessageSignal(TARGET_SIGNAL, locationToInt(enemy.location), SHORTRANGE);
                    }
                    
                    RobotInfo[] adjacentAllies = rc.senseNearbyRobots(2, myTeam);
                    boolean wander = false;
                    for (RobotInfo ally : adjacentAllies) {
                        if (ally.type == RobotType.TURRET || ally.type == RobotType.ARCHON) {
                            wander = true;
                            break;
                        }
                    }
                    if (!wander) {
                        navigate(rallyPoint);
                    } else {
                        navigateTowards(directions[rand.nextInt(8)]);
                    }
                    
                    Clock.yield();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static boolean checkerboard() {
        return (rc.getLocation().x + rc.getLocation().y) % 2 == (rallyPoint.x + rallyPoint.y) % 2;
    }
    
    public static void disperse() throws GameActionException {
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(INFINITY, myTeam);
        double totalX = 0;
        double totalY = 0;
        for (RobotInfo ally : nearbyAllies) {
            totalX += ally.location.x;
            totalY += ally.location.y;
        }
        totalX /= nearbyAllies.length;
        totalY /= nearbyAllies.length;
        
        int myX = rc.getLocation().x;
        int myY = rc.getLocation().y;
        
        MapLocation destination = rc.getLocation();
        if (totalX < myX) {
            destination = destination.add(Direction.EAST);
        } else if (totalX > myX) {
            destination = destination.add(Direction.WEST);
        }
        if (totalY < myY) {
            destination = destination.add(Direction.SOUTH);
        } else if (totalY > myY) {
            destination = destination.add(Direction.NORTH);
        }
        
        if (rc.getType() != RobotType.TURRET && rc.getType() != RobotType.TTM) {
            navigateTowards(rc.getLocation().directionTo(destination));
        } else {
            turretNavigateTowards(rc.getLocation().directionTo(destination));
        }
    }
    
    public static void processMessages() {
        Signal[] signals = rc.emptySignalQueue();
        if (rc.getType() == RobotType.SOLDIER) {
            for (Signal signal : signals) {
                if (signal.getTeam() == myTeam) {
                    if (signal.getMessage() != null) {
                        int[] message = signal.getMessage();
                        if (message[0] == RALLY_SIGNAL) {
                            rallyPoint = signal.getLocation();
                        } else {
                        
                        }
                    } else {
                        
                    }
                }
            }
        } else if (rc.getType() == RobotType.TURRET || rc.getType() == RobotType.TTM) {
            for (Signal signal : signals) {
                if (signal.getTeam() == myTeam) {
                    if (signal.getMessage() != null) {
                        int[] message = signal.getMessage();
                        if (message[0] == RALLY_SIGNAL) {
                            rallyPoint = signal.getLocation();
                        } else if (message[0] == TARGET_SIGNAL) {
                            targets.add(intToLocation(message[1]));
                        }
                    } else {
                        
                    }
                }
            }
        } else if (rc.getType() == RobotType.ARCHON) {
            for (Signal signal : signals) {
                if (signal.getTeam() == myTeam) {
                    if (signal.getMessage() != null) {
                        int[] message = signal.getMessage();
                        if (message[0] == RALLY_SIGNAL) {
                            rallyPoint = signal.getLocation();
                        } else if (message[0] == LEADER_ELECTION_SIGNAL && rc.getRoundNum() == 0) {
                            leader = false;
                        }
                    } else {
                        
                    }
                }
            }
        }
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
	
    public static void turretNavigateTowards(Direction dirToTarget) throws GameActionException {
        MapLocation targetloc = rc.getLocation().add(dirToTarget).add(dirToTarget).add(dirToTarget);
        if (!rc.isCoreReady() || rc.getLocation().equals(targetloc)) {
			return;
		}
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
		}
    }
    
    public static void navigateTowards(Direction dirToTarget) throws GameActionException {
        MapLocation targetloc = rc.getLocation().add(dirToTarget).add(dirToTarget).add(dirToTarget);
        if (!rc.isCoreReady() || rc.getLocation().equals(targetloc)) {
			return;
		}
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
