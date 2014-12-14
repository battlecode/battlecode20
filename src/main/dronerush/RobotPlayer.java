package dronerush;

import battlecode.common.*;
import java.util.*;


public class RobotPlayer {
	public static void run(RobotController rc) {
        BaseBot myself;

        if (rc.getType() == RobotType.HQ) {
            myself = new HQ(rc);
        } else if (rc.getType() == RobotType.FURBY) {
            myself = new Furby(rc);
        } else if (rc.getType() == RobotType.BARRACKS) {
            myself = new Barracks(rc);
	} else if (rc.getType() == RobotType.HELIPAD) {
            myself = new Helipad(rc);
        } else if (rc.getType() == RobotType.SOLDIER) {
            myself = new Soldier(rc);
	} else if (rc.getType() == RobotType.DRONE) {
            myself = new Drone(rc);
        } else if (rc.getType() == RobotType.TOWER) {
            myself = new Tower(rc);
        } else {
            myself = new BaseBot(rc);
        }

        while (true) {
            try {
                myself.go();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

    public static class BaseBot {
        public static final int REQUIRED_ORE_LEVEL = 2000;
        public static final int MOVE_AWAY_THRESHOLD = 9;

	public static final int CH_ID = 10;
	public static final int ID_MULTIPLIER = 10000;
	
	public static final int CH_TOWER_COUNT = 50;
	public static final int CH_TOWER_REMOVED = 51;
	public static final int CH_TOWER_X = 60;
	public static final int CH_TOWER_Y = 70;

        protected RobotController rc;
        protected MapLocation myHQ, theirHQ;
        protected Team myTeam, theirTeam;
	protected int id;

	protected RobotType type;

	protected MapLocation targetLocation;

        public BaseBot(RobotController rc) {
            this.rc = rc;
            this.myHQ = rc.senseHQLocation();
            this.theirHQ = rc.senseEnemyHQLocation();
            this.myTeam = rc.getTeam();
            this.theirTeam = this.myTeam.opponent();
	    this.type = rc.getType();

	    try {
		int ordinal = rc.getType().ordinal();
		int idChannel = CH_ID + ordinal;
		this.id = rc.readBroadcast(idChannel);
		rc.broadcast(idChannel, this.id + 1);
		this.id += ID_MULTIPLIER * ordinal;
	    } catch (Exception e) {
		e.printStackTrace();
	    }
        }

	public Direction[] attemptDirections(Direction target) {
	    Direction[] dirs = {target, target.rotateLeft(), target.rotateRight(), target.rotateLeft().rotateLeft(), target.rotateRight().rotateRight(), target.opposite().rotateLeft(), target.opposite().rotateRight(), target.opposite()};
	    return dirs;
	}

	public Direction getExploreDirection() {
	    return Direction.values()[this.id % 8];
	}

        public Direction getDirectionTowardEnemy() {
	    MapLocation enemyTarget = null;
	    try {
		int i = rc.readBroadcast(CH_TOWER_REMOVED);
		int towerCount = rc.readBroadcast(CH_TOWER_COUNT);
		while (towerCount > 0) {
		    MapLocation towerLoc = new MapLocation(rc.readBroadcast(CH_TOWER_X + i),
							   rc.readBroadcast(CH_TOWER_Y + i));
		    boolean setTarget = true;
		    if (rc.canSenseSquare(towerLoc)) {
			RobotInfo allegedTower = rc.senseRobotAtLocation(towerLoc);
			if (allegedTower == null || allegedTower.type != RobotType.TOWER) {
			    i++;
			    System.out.printf("removing tower %d, round %d, count %d\n", i, Clock.getRoundNum(), rc.readBroadcast(CH_TOWER_COUNT));
			    rc.broadcast(CH_TOWER_REMOVED, i);
			    towerCount--;
			    rc.broadcast(CH_TOWER_COUNT, rc.readBroadcast(CH_TOWER_COUNT) - 1);
			    setTarget = false;
			} else if (allegedTower != null) {
			    rc.setIndicatorString(0, allegedTower.type.toString());
			    rc.setIndicatorString(1, towerLoc.toString());
			} 
		    }
		    if (setTarget) {
			enemyTarget = towerLoc;
			break;
		    }
		    
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    if (enemyTarget == null && theirHQ != null) {
		enemyTarget = theirHQ;
	    }
            Direction toEnemy = enemyTarget != null ? rc.getLocation().directionTo(enemyTarget)
		: getExploreDirection();
            return toEnemy;
        }

	public int readCastNE(int channel) {
	    try {
		return rc.readBroadcast(channel);
	    } catch (Exception e) {
	    }
	    return 0;
	}

	public boolean canMove(Direction dir) {
	    if (!rc.canMove(dir)) {
		return false;
	    }
	    MapLocation wouldBe = rc.getLocation().add(dir);
	    if (theirHQ != null
		&& readCastNE(CH_TOWER_REMOVED) < 2
		&& wouldBe.distanceSquaredTo(theirHQ) <= GameConstants.ATTACK_RADIUS_SQUARED_BUFFED_HQ) {
		return false;
	    }
	    return true;
	}

        public Direction getMoveDir(Direction dir) {
	    Direction[] dirs = attemptDirections(dir);
            for (Direction d : dirs) {
                if (canMove(d)) {
                    return d;
                }
            }
            return null;
        }

        public Direction getSpawnDirection(RobotType type) {
            Direction[] dirs = attemptDirections(getDirectionTowardEnemy());
            for (Direction d : dirs) {
                if (rc.canSpawn(d, type)) {
                    return d;
                }
            }
            return null;
        }

        public Direction getBuildDirection(RobotType type) {
	    Direction[] dirs = attemptDirections(getDirectionTowardEnemy());
            for (Direction d : dirs) {
                if (rc.canBuild(d, type)) {
                    return d;
                }
            }
            return null;
        }

        public RobotInfo[] getAllies() {
            RobotInfo[] allies = rc.senseNearbyRobots(Integer.MAX_VALUE, myTeam);
            return allies;
        }

        public RobotInfo[] getEnemiesInRange(int rad2) {
            RobotInfo[] enemies = rc.senseNearbyRobots(rad2, theirTeam);
            return enemies;
        }

        public void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
            if (enemies.length == 0) {
                return;
            }

            double minEnergon = Double.MAX_VALUE;
            MapLocation toAttack = null;
            for (RobotInfo info : enemies) {
                if (info.health < minEnergon) {
                    toAttack = info.location;
                    minEnergon = info.health;
                }
            }

            rc.attackSquare(toAttack);
        }

	public void addTowerToRadio (RobotInfo towerInfo) throws GameActionException{
	    int prevCount = rc.readBroadcast(CH_TOWER_COUNT);
	    int towerBase = rc.readBroadcast(CH_TOWER_REMOVED);
	    for (int i = towerBase; i < prevCount; i++) {
		if (towerInfo.location.x == rc.readBroadcast(CH_TOWER_X + i)
		    && towerInfo.location.y == rc.readBroadcast(CH_TOWER_Y + i))
		{
		    return;
		}
	    }
	    rc.broadcast(CH_TOWER_COUNT, prevCount + 1);
	    rc.broadcast(CH_TOWER_X + prevCount + towerBase, towerInfo.location.x);
	    rc.broadcast(CH_TOWER_Y + prevCount + towerBase, towerInfo.location.y);
	    System.out.printf("%d added tower %s  at round %d, towerCount now %d\n", id,
			      towerInfo.location.toString(), Clock.getRoundNum(), prevCount + 1);
	}

        public void beginningOfTurn() {
            if (rc.senseEnemyHQLocation() != null) {
                this.theirHQ = rc.senseEnemyHQLocation();
            }
	    RobotInfo[] nearbyRobots = getEnemiesInRange(rc.getType().sensorRadiusSquared);
	    for (int i = 0; i < nearbyRobots.length; i++) {
		RobotInfo rbt = nearbyRobots[i];
		if (rbt.type == RobotType.TOWER) {
		    try {
			addTowerToRadio(rbt);
		    } catch (Exception e) {
		    }
		}
	    }
        }

        public void endOfTurn() {
        }

        public void go() throws GameActionException {
            beginningOfTurn();
            execute();
            endOfTurn();
        }

        public void execute() throws GameActionException {
            rc.yield();
        }
    }

    public static class HQ extends BaseBot {
        public HQ(RobotController rc) {
            super(rc);

	    try{
	    rc.broadcast(3331, 555);
	    System.out.printf("hq test %d\n", rc.readBroadcast(3331));
	    } catch (Exception e) {}
        }

        public void execute() throws GameActionException {
	    // sort the towers by distance to the enemy hq
	    /*int towerCount = readCastNE(CH_TOWER_COUNT);
	    int towerRemoved = readCastNE(CH_TOWER_REMOVED);
	    if (theirHQ != null) {
		for (int i = towerRemoved; i < towerCount - 1; i++) {
		    MapLocation maxLoc
			= null;
		    int distMax = 0;
		    int maxInd = i;
		    for (int j = i; j < towerCount; j++) {
			MapLocation b
			    = new MapLocation(readCastNE(CH_TOWER_X + j),
					      readCastNE(CH_TOWER_Y + j));

			int distB = b.distanceSquaredTo(theirHQ);
			if (distB > distMax) {
			    distMax = distB;
			    maxInd = j;
			    maxLoc = b;
			}
		    }
		    MapLocation a
			= new MapLocation(readCastNE(CH_TOWER_X + i),
					  readCastNE(CH_TOWER_Y + i));
		    
		    rc.broadcast(CH_TOWER_X + i, maxLoc.x);
		    rc.broadcast(CH_TOWER_Y + i, maxLoc.y);
		    rc.broadcast(CH_TOWER_X + maxInd, a.x);
		    rc.broadcast(CH_TOWER_Y + maxInd, a.y);
		}
		}*/
            // spawn a furby if possible
            Direction dir = getSpawnDirection(RobotType.FURBY);
            if (dir != null && rc.isMovementActive()) {
                rc.spawn(dir, RobotType.FURBY);
            }

            // also try to attack
            RobotInfo[] enemies = getEnemiesInRange(type.attackRadiusSquared);
            if (rc.isAttackActive() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }
	    
            rc.yield();
        }
    }

    public static class Furby extends BaseBot {
        public Furby(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            Direction buildDir = getBuildDirection(RobotType.BARRACKS);
	    boolean hasBuilt = false;

            // if too close to HQ, move
            if (rc.isMovementActive() && rc.getLocation().distanceSquaredTo(myHQ) < MOVE_AWAY_THRESHOLD) {
                Direction moveDir = getMoveDir(getExploreDirection());
                if (moveDir != null) {
                    rc.move(moveDir);
                }
            }
            // if ore is low, then mine
            else if (rc.getTeamOre() < REQUIRED_ORE_LEVEL && rc.isMovementActive()) {
                if (rc.senseOre(rc.getLocation()) > 0) {
                    rc.mine();
                } else {
                    Direction moveDir = getMoveDir(getExploreDirection());
                    if (moveDir != null) {
                        rc.move(moveDir);
                    }
                }
            }

            // else, build barracks
            else if (buildDir != null && rc.isMovementActive() && !hasBuilt) {
		if (this.id % ID_MULTIPLIER == 5) {
		    rc.build(buildDir, RobotType.TECHNOLOGYINSTITUTE);
		} else if (rc.checkDependencyProgress(RobotType.TECHNOLOGYINSTITUTE)
			   != DependencyProgress.DONE) {
		    rc.build(buildDir, RobotType.BARRACKS);
		} else {
		    rc.build(buildDir, RobotType.HELIPAD);
		}
		hasBuilt = true;
            }

            rc.yield();
        }
    }

    public static class Barracks extends BaseBot {
        public Barracks(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            // spawn a furby if possible
            Direction dir = getSpawnDirection(RobotType.SOLDIER);
            if (dir != null && rc.isMovementActive()) {
                rc.spawn(dir, RobotType.SOLDIER);
            }

            rc.yield();
        }
    }

    public static class Helipad extends BaseBot {
        public Helipad(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            // spawn a furby if possible
            Direction dir = getSpawnDirection(RobotType.DRONE);
            if (dir != null && rc.isMovementActive()) {
                rc.spawn(dir, RobotType.DRONE);
            }

            rc.yield();
        }
    }
    

    public static class Soldier extends BaseBot {
        public Soldier(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            // if can attack, then attack
            RobotInfo[] enemies = getEnemiesInRange(type.attackRadiusSquared);
            if (rc.isAttackActive() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

            // else try to move to enemy HQ
            else if (rc.isMovementActive()) {
                Direction moveDir = getMoveDir(getDirectionTowardEnemy());
                if (moveDir != null) {
                    rc.move(moveDir);
                }
            }

            rc.yield();
        }
    }

    public static class Drone extends BaseBot {
        public Drone(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            // if can attack, then attack
            RobotInfo[] enemies = getEnemiesInRange(type.attackRadiusSquared);
	    if (enemies.length > 0) {
		rc.setIndicatorString(0, "attacking!");
		if (rc.isAttackActive()) {
		    attackLeastHealthEnemy(enemies);
		}
	    } else {
		rc.setIndicatorString(0, "no enemy in range");
	    }

            // else try to move to enemy HQ
            if (rc.isMovementActive()) {
                Direction moveDir = getMoveDir(getDirectionTowardEnemy());
                if (moveDir != null) {
                    rc.move(moveDir);
                }
            }

            rc.yield();
        }
    }


    public static class Tower extends BaseBot {
        public Tower(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            RobotInfo[] enemies = getEnemiesInRange(type.attackRadiusSquared);
            if (rc.isAttackActive() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

            rc.yield();
        }
    }
}
