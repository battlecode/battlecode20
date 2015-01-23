package team266;

import battlecode.common.*;

import java.util.*;

/*
 * TODO: find way to spawn things in better direction (lots of examples given in sample code)
 */

public class RobotPlayer {
    
    static Direction facing = Direction.NORTH;
    static Random rand;
    static RobotController rc;
    
    public static void run(RobotController myrc) {
        rc = myrc;
        BaseBot myself;
        rand = new Random(rc.getID());
        facing = getRandomDirection();


        
//        if (rc.getType() == RobotType.HQ) {
//            myself = new HQ(rc);
//        } else if (rc.getType() == RobotType.BEAVER) {
//            myself = new Beaver(rc);
//        } else if (rc.getType() == RobotType.BARRACKS || rc.getType() == RobotType.HELIPAD || rc.getType() == RobotType.TANKFACTORY) {
//            myself = new SimpleBuilding(rc);
//        } else if (rc.getType() == RobotType.SOLDIER || rc.getType() == RobotType.DRONE || rc.getType() == RobotType.TANK) {
//            myself = new SimpleFighter(rc);
//        } else if (rc.getType() == RobotType.TOWER) {
//            myself = new Tower(rc);
//        } else {
//            myself = new BaseBot(rc);
//        }


        if (rc.getType() == RobotType.HQ) {
            myself = new HQ(rc);
        } else if (rc.getType() == RobotType.BEAVER) {
            myself = new Beaver(rc);
        } else if (rc.getType() == RobotType.MINER) {
            myself = new Miner(rc);
        } else if (rc.getType() == RobotType.MINERFACTORY) {
            myself = new MinerFactory(rc);
        } else if (rc.getType() == RobotType.BARRACKS) {
            myself = new Barracks(rc);
        } else if (rc.getType() == RobotType.SOLDIER) {
            myself = new Soldier(rc);
        } else if (rc.getType() == RobotType.TOWER) {
            myself = new Tower(rc);
        } else if (rc.getType() == RobotType.HELIPAD) {
            myself = new Helipad(rc);
        } else if (rc.getType() == RobotType.DRONE) {
            myself = new Drone(rc);
        } else if (rc.getType() == RobotType.AEROSPACELAB) {
            myself = new AerospaceLab(rc);
        } else if (rc.getType() == RobotType.COMMANDER) {
            myself = new Commander(rc);
        } else if (rc.getType() == RobotType.LAUNCHER) {
            myself = new Launcher(rc);
        } else {
            myself = new BaseBot(rc);
        }
        

        while (true) {
            try {
                myself.go();
//                System.err.println("running go()");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class BaseBot {
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
        
        public void go() throws GameActionException {
            beginningOfTurn();
            execute();
            endOfTurn();
        }

        public void execute() throws GameActionException {
            rc.yield();
        }

        public void beginningOfTurn() throws GameActionException {
            if (rc.senseEnemyHQLocation() != null) {
                this.theirHQ = rc.senseEnemyHQLocation();
            }
            attackEnemyZero();
        }

        public void endOfTurn() throws GameActionException {
            transferSupplies();
            rc.yield();
        }
        
    }
    
    
    public static class Beaver extends BaseBot {
        public Beaver(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
          
            if (Clock.getRoundNum()>100) {
                if (Clock.getRoundNum() < 200) { 
                    RobotPlayer.buildUnit(RobotType.MINERFACTORY);
                } else if (Clock.getRoundNum() < 850 && Clock.getRoundNum() > 500 ) {
                    RobotPlayer.buildUnit(RobotType.HELIPAD);
                } else {
                    RobotPlayer.buildUnit(RobotType.BARRACKS);
                }
            }
            RobotPlayer.mineAndMove();
            RobotPlayer.transferSupplies();
            rc.yield();
        }
        
    }
    
   

   
    public static class Tower extends BaseBot {
        public Tower(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {
            RobotPlayer.transferSupplies();
            rc.yield();
        }
    }

    public static class HQ extends BaseBot {
        public HQ(RobotController rc) {
            super(rc);
        }

        public void execute() throws GameActionException {  
            if (Clock.getRoundNum()<500) {
                RobotPlayer.spawnUnit(RobotType.BEAVER);
            } else {
                RobotPlayer.buildUnit(RobotType.HELIPAD);
            }

            MapLocation rallyPoint1;
            if (Clock.getRoundNum() < 800) {
                rallyPoint1 = rally();
            } else if ((Clock.getRoundNum() > 900) && (Clock.getRoundNum() < 1100)) {
                rallyPoint1 = rally();
            } else if ((Clock.getRoundNum() > 1200) && (Clock.getRoundNum() < 1400)) {
                rallyPoint1 = rally();
            } else if ((Clock.getRoundNum() > 1600) && (Clock.getRoundNum() < 1750)) {
                rallyPoint1 = rally();
            } else if ((Clock.getRoundNum() > 1850) && (Clock.getRoundNum() < 1900)) {
                rallyPoint1 = rally();
            } else {
                rallyPoint1 = attackPoint();
                if (rc.senseEnemyTowerLocations().length <3) { //attack HQ if less than 3 towers
                    rallyPoint1 = rc.senseEnemyHQLocation();
                }
            }
            
            MapLocation rallyPoint2;
            if (Clock.getRoundNum() < 1800) {
                rallyPoint2 = attackPoint2();
            } else {
                rallyPoint2 = attackPoint2();
                if (rc.senseEnemyTowerLocations().length <3) { //attack HQ if less than 3 towers
                    rallyPoint1 = rc.senseEnemyHQLocation();
                }
            }
            
            rc.broadcast(0,rallyPoint1.x);
            rc.broadcast(1,rallyPoint1.y);
            
            
            MapLocation droneRallyPoint;
            droneRallyPoint = droneRally();
            
            rc.broadcast(2, droneRallyPoint.x);
            rc.broadcast(3, droneRallyPoint.y);
            
            rc.broadcast(4,rallyPoint2.x);
            rc.broadcast(5,rallyPoint2.y);
            
        }
        
        public MapLocation attackPoint2() {
            MapLocation attackHQ = this.theirHQ;
            return attackHQ;
        }

        public MapLocation rally() throws GameActionException {
            MapLocation[] TowerLocations = rc.senseTowerLocations();
            MapLocation rallyTower = new MapLocation(rc.readBroadcast(4),rc.readBroadcast(5));
            double distanceHQs = Math.sqrt(Math.pow((this.myHQ.x - this.theirHQ.x), 2) + Math.pow((this.myHQ.y - this.theirHQ.y), 2));
            double referenceDistance = 2*distanceHQs;
            
            for (MapLocation tower : TowerLocations) {
                double towerToEnemy = Math.sqrt(Math.pow((tower.x - this.theirHQ.x), 2) + Math.pow((tower.y - this.theirHQ.y), 2));
                double towerToHQ = Math.sqrt(Math.pow((tower.x - this.myHQ.x), 2) + Math.pow((tower.y - this.myHQ.y), 2));
                
                if ((1.3*towerToEnemy+towerToHQ)< 1.9*distanceHQs) {
                    if ((1.3*towerToEnemy+towerToHQ)<referenceDistance) {
                        referenceDistance =towerToEnemy+towerToHQ;
                        rallyTower = tower;
                    }
                }
                
            }
            return rallyTower;
        }
        
        public MapLocation rallyHQ() {
            MapLocation rally = this.myHQ;
            return rally;
        }
        
        public MapLocation droneRally() {
            MapLocation rally = this.myHQ;
            if (Clock.getRoundNum()>1900) {
                rally = this.theirHQ;
            }
            return rally;
        }
        
        public MapLocation attackPoint() {
            MapLocation[] TowerLocations = rc.senseEnemyTowerLocations();
            MapLocation rallyTower = this.theirHQ;
//            double distanceHQs = Math.sqrt(Math.pow((this.myHQ.x - this.theirHQ.x), 2) + Math.pow((this.myHQ.y - this.theirHQ.y), 2));
            Map<Double, MapLocation> towerLocations= new HashMap<Double, MapLocation>();
            List<Double> towerDistances = new ArrayList<Double>();
            for (MapLocation tower : TowerLocations) {
//                double towerToEnemy = Math.sqrt(Math.pow((tower.x - this.theirHQ.x), 2) + Math.pow((tower.y - this.theirHQ.y), 2));
                double towerToHQ = Math.sqrt(Math.pow((tower.x - this.myHQ.x), 2) + Math.pow((tower.y - this.myHQ.y), 2));
                towerLocations.put(towerToHQ, tower);
                towerDistances.add(towerToHQ);
            }
            double closestTower = Collections.min(towerDistances);
            rallyTower = towerLocations.get(closestTower);
            return rallyTower;
        }
        
    }
    
    
    
    protected static void transferSupplies() throws GameActionException {
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(rc.getLocation(), GameConstants.SUPPLY_TRANSFER_RADIUS_SQUARED, rc.getTeam());
        double lowestSupply = rc.getSupplyLevel();
        double transferAmount = 0;
        MapLocation suppliesToThisLoc = null;
        
        for (RobotInfo ri : nearbyAllies) {
            if (ri.supplyLevel < lowestSupply) {
                lowestSupply = ri.supplyLevel;
                transferAmount = (rc.getSupplyLevel() - ri.supplyLevel) / 2;
                suppliesToThisLoc = ri.location;
            }
        }
        if (suppliesToThisLoc != null) {
            rc.transferSupplies((int)transferAmount, suppliesToThisLoc);
        }
    }

    protected static void buildUnit(RobotType type) throws GameActionException {
        if (rc.getTeamOre() > type.oreCost) {
            Direction buildDir = getRandomDirection();
            if (rc.isCoreReady() && rc.canBuild(buildDir, type)) {
                rc.build(buildDir, type);
            }
        }
    }

    protected static void attackEnemyZero() throws GameActionException {
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(rc.getLocation(), rc.getType().attackRadiusSquared, rc.getTeam().opponent());
        if (nearbyEnemies.length > 0) { //enemies nearby
            //try to shoot at them
            //specifically enemy specified by nearbyEnemies[0] in this case
            if (rc.isWeaponReady() && rc.canAttackLocation(nearbyEnemies[0].location)) {
                rc.attackLocation(nearbyEnemies[0].location);
            }
        }
    }

    protected static void spawnUnit(RobotType type) throws GameActionException {
        Direction buildDir = getRandomDirection();
        if(rc.isCoreReady() && rc.canSpawn(buildDir, type)) {
            rc.spawn(buildDir, type);
        } 
    }

    protected static Direction getRandomDirection() {
       return Direction.values()[(int)rand.nextDouble()*8]; //randomize starting direction
    }

    protected static void mineAndMove() throws GameActionException {
        if (rc.senseOre(rc.getLocation()) > 0) { //there is ore, try to mine
            if (rc.isCoreReady() && rc.canMine()) {
                rc.mine();
            }
        } else { //no ore, so continue looking, possibly use senseOre at other locs
            move();
        }
    }

    protected static void move() throws GameActionException { // better direction to move?
        if (rand.nextDouble() < 0.05) {
            if (rand.nextDouble() < 0.5) {
                facing = facing.rotateLeft();
            } else {
                facing = facing.rotateRight();
            }
        }
        MapLocation tileInFront = rc.getLocation().add(facing);
        
        //check that direction in front is not a tile that can be attacked by enemy towers
        MapLocation[] enemyTowers = rc.senseEnemyTowerLocations();
        boolean tileInFrontSafe = true;
        for (MapLocation m : enemyTowers) {
            if (m.distanceSquaredTo(tileInFront) < RobotType.TOWER.attackRadiusSquared) {
                tileInFrontSafe = false;
                break;
            }
        }
        
        //check that we are not facing the edge of the map or within range of enemy towers
        if (rc.senseTerrainTile(tileInFront) != TerrainTile.NORMAL || !tileInFrontSafe) {
            facing.rotateLeft();
        } else {
            //try to move forward
            if (rc.isCoreReady() && rc.canMove(facing)) {
                rc.move(facing);
            }
        }
    }
}