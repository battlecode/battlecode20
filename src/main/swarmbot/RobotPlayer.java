package swarmbot;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	public static void run(RobotController rc) {
        BaseBot myself;

        if (rc.getType() == RobotType.HQ) {
            myself = new HQ(rc);
        } else if (rc.getType() == RobotType.BEAVER) {
            myself = new Beaver(rc);
        } else if (rc.getType() == RobotType.BARRACKS) {
            myself = new Barracks(rc);
        } else if (rc.getType() == RobotType.SOLDIER) {
            myself = new Soldier(rc);
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
        public static final int REQUIRED_ORE_LEVEL = 200;
        public static final int MOVE_AWAY_THRESHOLD = 50;
	public static final int BARRACKS_GOAL = 3;
	public static final int TURN_TO_ATTACK = 600;

        protected RobotController rc;
        protected MapLocation myHQ, theirHQ;
        protected Team myTeam, theirTeam;

        public BaseBot(RobotController rc) {
            this.rc = rc;
            this.myHQ = rc.senseHQLocation();
            this.theirHQ = rc.senseEnemyHQLocation();
            this.myTeam = rc.getTeam();
            this.theirTeam = this.myTeam.opponent();
        }

        public Direction[] getDirectionsToward(MapLocation dest) {
            Direction toDest = rc.getLocation().directionTo(dest);
            Direction[] dirs = {toDest,
		    		toDest.rotateLeft(), toDest.rotateRight(),
				toDest.rotateLeft().rotateLeft(), toDest.rotateRight().rotateRight()};

            return dirs;
        }

        public Direction getMoveDir(MapLocation dest) {
            Direction[] dirs = getDirectionsToward(dest);
            for (Direction d : dirs) {
                if (rc.canMove(d)) {
                    return d;
                }
            }
            return null;
        }

        public Direction getSpawnDirection(RobotType type) {
            Direction[] dirs = getDirectionsToward(this.theirHQ);
            for (Direction d : dirs) {
                if (rc.canSpawn(d, type)) {
                    return d;
                }
            }
            return null;
        }

        public Direction getBuildDirection(RobotType type) {
            Direction[] dirs = getDirectionsToward(this.theirHQ);
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

        public RobotInfo[] getEnemiesInAttackingRange() {
            RobotInfo[] enemies = rc.senseNearbyRobots(RobotType.SOLDIER.attackRadiusSquared, theirTeam);
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

            rc.attackLocation(toAttack);
        }

        public void beginningOfTurn() {
            if (rc.senseEnemyHQLocation() != null) {
                this.theirHQ = rc.senseEnemyHQLocation();
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
        }

        public void execute() throws GameActionException {
            Direction dir = getSpawnDirection(RobotType.BEAVER);
            if (dir != null && rc.isCoreReady()) {
                rc.spawn(dir, RobotType.BEAVER);
            }

            // also try to attack
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (rc.isWeaponReady() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

	    int xpos = rc.readBroadcast(0), ypos = rc.readBroadcast(1);
	    if (xpos == 0 && ypos == 0) {
		rc.broadcast(0, (this.myHQ.x + this.theirHQ.x) / 2);
		rc.broadcast(1, (this.myHQ.y + this.theirHQ.y) / 2);
	    }
	    else if (Clock.getRoundNum() == TURN_TO_ATTACK) {
		rc.broadcast(0, this.theirHQ.x);
		rc.broadcast(1, this.theirHQ.y);
	    }

            rc.yield();
        }
    }

    public static class Beaver extends BaseBot {
        public Beaver(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            Direction buildDir = getBuildDirection(RobotType.BARRACKS);

	    int numBarracks = rc.readBroadcast(2);
	    rc.setIndicatorString(2, "" + numBarracks);

	    if (rc.isCoreReady()) {
		if (rc.getLocation().distanceSquaredTo(myHQ) < MOVE_AWAY_THRESHOLD) {
		    Direction moveDir = getMoveDir(this.theirHQ);
		    if (moveDir != null) {
			rc.move(moveDir);
		    }
		}

		else if (rc.getTeamOre() >= REQUIRED_ORE_LEVEL && numBarracks < BARRACKS_GOAL) {
		    rc.build(buildDir, RobotType.BARRACKS);
		    rc.broadcast(2, numBarracks+1);
		}

		else {
		    if (rc.senseOre(rc.getLocation()) > 0) {
			rc.mine();
		    } else {
			Direction moveDir = getMoveDir(this.theirHQ);
			if (moveDir != null) {
			    rc.move(moveDir);
			}
		    }
		}
	    }

            rc.yield();
        }
    }

    public static class Barracks extends BaseBot {
        public Barracks(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            Direction dir = getSpawnDirection(RobotType.SOLDIER);
            if (dir != null && rc.isCoreReady()) {
                rc.spawn(dir, RobotType.SOLDIER);
            }

            rc.yield();
        }
    }

    public static class Soldier extends BaseBot {
        public Soldier(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (enemies.length > 0) {
		if (rc.isWeaponReady()) {
		    attackLeastHealthEnemy(enemies);
		}
            }
	    else {
		// try to move to rally point
		int xpos = rc.readBroadcast(0), ypos = rc.readBroadcast(1);
		MapLocation rally = new MapLocation(xpos, ypos);

		if (rc.isCoreReady()) {
		    Direction moveDir = getMoveDir(rally);
		    if (moveDir != null) {
			rc.move(moveDir);
		    }
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
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (rc.isWeaponReady() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

            rc.yield();
        }
    }
}
