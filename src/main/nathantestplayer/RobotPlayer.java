package nathantestplayer;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
	public static void run(RobotController rc) {
        BaseBot myself;

        if (rc.getType() == RobotType.HQ) {
            myself = new HQ(rc);
        } else if (rc.getType() == RobotType.BEAVER) {
            myself = new Furby(rc);
        } else if (rc.getType() == RobotType.BARRACKS) {
            myself = new Barracks(rc);
        } else if (rc.getType() == RobotType.SOLDIER) {
            myself = new Soldier(rc);
        } else if (rc.getType() == RobotType.COMMANDER) {
	    myself = new Commander(rc);
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
        public static final int MOVE_AWAY_THRESHOLD = 50;

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

        public Direction[] getDirectionsTowardEnemy() {
            Direction toEnemyHQ = theirHQ != null ? myHQ.directionTo(theirHQ) : Direction.NORTH_EAST;
            Direction[] dirs = {toEnemyHQ, toEnemyHQ.rotateLeft(), toEnemyHQ.rotateRight(), toEnemyHQ.rotateLeft().rotateLeft(), toEnemyHQ.rotateRight().rotateRight(), toEnemyHQ.opposite().rotateLeft(), toEnemyHQ.opposite().rotateRight(), toEnemyHQ.opposite()};
            return dirs;
        }

        public Direction getMoveDir() {
            Direction[] dirs = getDirectionsTowardEnemy();
            for (Direction d : dirs) {
                if (rc.canMove(d)) {
                    return d;
                }
            }
            return null;
        }

        public Direction getSpawnDirection(RobotType type) {
            Direction[] dirs = getDirectionsTowardEnemy();
            for (Direction d : dirs) {
                if (rc.canSpawn(d, type)) {
                    return d;
                }
            }
            return null;
        }

        public Direction getBuildDirection(RobotType type) {
            Direction[] dirs = getDirectionsTowardEnemy();
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
            // spawn a furby if possible
            Direction dir = getSpawnDirection(RobotType.BEAVER);
            if (dir != null && rc.isMovementActive()) {
                rc.spawn(dir, RobotType.BEAVER);
            }

            // also try to attack
            RobotInfo[] enemies = getEnemiesInAttackingRange();
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

            // if too close to HQ, move
            if (rc.isMovementActive() && rc.getLocation().distanceSquaredTo(myHQ) < MOVE_AWAY_THRESHOLD) {
                Direction moveDir = getMoveDir();
                if (moveDir != null) {
                    rc.move(moveDir);
                }
            }

            // if ore is low, then mine
            else if (rc.getTeamOre() < REQUIRED_ORE_LEVEL && rc.isMovementActive()) {
                if (rc.senseOre(rc.getLocation()) > 0) {
                    rc.mine();
                } else {
                    Direction moveDir = getMoveDir();
                    if (moveDir != null) {
                        rc.move(moveDir);
                    }
                }
            }

            // else, build barracks
            else if (buildDir != null && rc.isMovementActive()) {
                rc.build(buildDir, RobotType.BARRACKS);
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

    public static class Soldier extends BaseBot {
        public Soldier(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            // if can attack, then attack
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (rc.isAttackActive() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

            // else try to move to enemy HQ
            else if (rc.isMovementActive()) {
                Direction moveDir = getMoveDir();
                if (moveDir != null) {
                    rc.move(moveDir);
                }
            }

            rc.yield();
        }
    }
    public static class Commander extends BaseBot {
        public Commander(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            // if can attack, then attack
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (rc.isAttackActive() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

            // else try to move to enemy HQ
            else if (rc.isMovementActive()) {
                Direction moveDir = getMoveDir();
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
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (rc.isAttackActive() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

            rc.yield();
        }
    }
}
