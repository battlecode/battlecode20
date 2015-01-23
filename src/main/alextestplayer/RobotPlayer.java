package alextestplayer;

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
        } else if (rc.getType() == RobotType.TRAININGFIELD) {
            myself = new TrainingField(rc);
        } else if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.TANK) {
            myself = new Soldier(rc);
        } else if (rc.getType() == RobotType.BASHER) {
            myself = new Basher(rc);
        } else if (rc.getType() == RobotType.HELIPAD) {
            myself = new Helipad(rc);
        } else if (rc.getType() == RobotType.LAUNCHER) {
            myself = new Launcher(rc);
        } else if (rc.getType() == RobotType.COMMANDER) {
            myself = new Commander(rc);
        } else if (rc.getType() == RobotType.MISSILE) {
            myself = new Missile(rc);
        } else if (rc.getType() == RobotType.TOWER) {
            myself = new Tower(rc);
        } else if (rc.getType() == RobotType.DRONE) {
            myself = new Drone(rc);
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
        protected RobotType myType;

        public BaseBot(RobotController rc) {
            this.rc = rc;
            this.myHQ = rc.senseHQLocation();
            this.theirHQ = rc.senseEnemyHQLocation();
            this.myTeam = rc.getTeam();
            this.theirTeam = this.myTeam.opponent();
            this.myType = rc.getType();
        }

        public Direction[] getDirectionsTowardEnemy() {
            Direction toEnemyHQ = rc.getLocation().directionTo(theirHQ);
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
            RobotInfo[] enemies = rc.senseNearbyRobots(myType.attackRadiusSquared, theirTeam);
            return enemies;
        }

        public void attackLeastHealthEnemy(RobotInfo[] enemies) throws GameActionException {
            if (enemies.length == 0) {
                return;
            }

            double minEnergon = Double.MAX_VALUE;
            MapLocation toAttack = null;
            for (RobotInfo info : enemies) {
                if (info.health < minEnergon && (myType != RobotType.TANK || info.type == RobotType.LAUNCHER)) {
                    toAttack = info.location;
                    minEnergon = info.health;
                }
            }
            if (toAttack == null) {
            for (RobotInfo info : enemies) {
                if (info.health < minEnergon) {
                    toAttack = info.location;
                    minEnergon = info.health;
                }
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
            if (dir != null && rc.isCoreReady()) {
                rc.spawn(dir, RobotType.BEAVER);
            }

            // also try to attack
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (rc.isWeaponReady() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
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

            // if too close to HQ, move
            if (rc.isCoreReady() && rc.getLocation().distanceSquaredTo(myHQ) < MOVE_AWAY_THRESHOLD) {
                Direction moveDir = getMoveDir();
                if (moveDir != null) {
                    rc.move(moveDir);
                }
            }

            // if ore is low, then mine
            else if (rc.getTeamOre() < REQUIRED_ORE_LEVEL && rc.isCoreReady()) {
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
            else if (buildDir != null && rc.isCoreReady()) {
                rc.build(buildDir, RobotType.HELIPAD);
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
            Direction dir = getSpawnDirection(RobotType.BASHER);
            if (dir != null && rc.isCoreReady()) {
                rc.spawn(dir, RobotType.BASHER);
            }

            rc.yield();
        }
    }

    public static class TrainingField extends BaseBot {
        public TrainingField(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            // spawn a furby if possible
            Direction dir = getSpawnDirection(RobotType.COMMANDER);
            if (dir != null && rc.isCoreReady()) {
                rc.spawn(dir, RobotType.COMMANDER);
            }

            rc.yield();
        }
    }

    public static class Helipad extends BaseBot {
        public Helipad(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            Direction dir = getSpawnDirection(RobotType.DRONE);
            if (dir != null && rc.isCoreReady()) {
                rc.spawn(dir, RobotType.DRONE);
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
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (rc.isWeaponReady() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

            // else try to move to enemy HQ
            else if (rc.isCoreReady()) {
                Direction moveDir = getMoveDir();
                if (moveDir != null) {
                    rc.move(moveDir);
                }
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
            Direction launch = Direction.SOUTH;
            if (rc.getTeam() == Team.A) {
                launch = Direction.NORTH;
            }
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (rc.isWeaponReady() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

            // else try to move to enemy HQ
            else if (rc.isCoreReady()) {
                if (rc.canMove(launch)) {
                    rc.move(launch);
                }
            }

            rc.yield();
        }
    }

    public static class Launcher extends BaseBot {
        public Launcher(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            Direction launch = Direction.SOUTH;
            if (rc.getTeam() == Team.A) {
                launch = Direction.NORTH;
            }
            if (rc.canLaunch(launch)) {
                rc.launchMissile(launch);
            } else if (rc.canMove(launch) && rc.isCoreReady() && rc.senseRobotAtLocation(rc.getLocation().add(launch).add(launch)) == null) {
                //rc.move(launch);
            }

            rc.yield();
        }
    }
    public static class Missile extends BaseBot {
        public Missile(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            Direction launch = Direction.SOUTH;
            if (rc.getTeam() == Team.A) {
                launch = Direction.NORTH;
            }
            if (rc.canMove(launch)) {
                rc.move(launch);
            } else {
                rc.explode();
            }

            rc.yield();
        }
    }

    public static class Basher extends BaseBot {
        public Basher(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            if (rc.isCoreReady()) {
                Direction launch = Direction.SOUTH;
                if (rc.getTeam() == Team.A) {
                    launch = Direction.NORTH;
                }
                if (rc.canMove(launch)) {
                    rc.move(launch);
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
            RobotInfo[] enemies = getEnemiesInAttackingRange();
            if (rc.isWeaponReady() && enemies.length > 0) {
                attackLeastHealthEnemy(enemies);
            }

            rc.setIndicatorString(0, "cooldown: " + rc.getFlashCooldown());
            rc.castFlash(rc.getLocation().add(2, 2));

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
