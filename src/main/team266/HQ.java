//package team266;
//
//import java.util.*;
//
//import team266.RobotPlayer.BaseBot;
//import battlecode.common.*;
//
//public class HQ extends BaseBot {
//    public HQ(RobotController rc) {
//        super(rc);
//    }
//
//    public void execute() throws GameActionException {  
//        if (Clock.getRoundNum()<500) {
//            RobotPlayer.spawnUnit(RobotType.BEAVER);
//        } else {
//            RobotPlayer.buildUnit(RobotType.HELIPAD);
//        }
//
//        MapLocation rallyPoint;
//        if (Clock.getRoundNum() < 800) {
//            rallyPoint = rally();
//        } else if ((Clock.getRoundNum() > 900) && (Clock.getRoundNum() < 1100)) {
//            rallyPoint = rally();
//        } else if ((Clock.getRoundNum() > 1200) && (Clock.getRoundNum() < 1400)) {
//            rallyPoint = rally();
//        } else if ((Clock.getRoundNum() > 1600) && (Clock.getRoundNum() < 1750)) {
//            rallyPoint = rally();
//        } else if ((Clock.getRoundNum() > 1850) && (Clock.getRoundNum() < 1900)) {
//            rallyPoint = rally();
//        } else {
//            rallyPoint = attackPoint();
//            if (rc.senseEnemyTowerLocations().length <3) { //attack HQ if less than 3 towers
//                rallyPoint = rc.senseEnemyHQLocation();
//            }
//        }
//        rc.broadcast(0,rallyPoint.x);
//        rc.broadcast(1,rallyPoint.y);
//        
//    }
//    
//    public MapLocation rally() {
//        MapLocation[] TowerLocations = rc.senseTowerLocations();
//        MapLocation rallyTower = this.myHQ;
//        double distanceHQs = Math.sqrt(Math.pow((this.myHQ.x - this.theirHQ.x), 2) + Math.pow((this.myHQ.y - this.theirHQ.y), 2));
//        double referenceDistance = 2*distanceHQs;
//        
//        for (MapLocation tower : TowerLocations) {
//            double towerToEnemy = Math.sqrt(Math.pow((tower.x - this.theirHQ.x), 2) + Math.pow((tower.y - this.theirHQ.y), 2));
//            double towerToHQ = Math.sqrt(Math.pow((tower.x - this.myHQ.x), 2) + Math.pow((tower.y - this.myHQ.y), 2));
//            
//            if ((1.3*towerToEnemy+towerToHQ)< 1.9*distanceHQs) {
//                if ((1.3*towerToEnemy+towerToHQ)<referenceDistance) {
//                    referenceDistance =towerToEnemy+towerToHQ;
//                    rallyTower = tower;
//                }
//            }
//            
//        }
//        return rallyTower;
//    }
//    public MapLocation attackPoint() {
//        MapLocation[] TowerLocations = rc.senseEnemyTowerLocations();
//        MapLocation rallyTower = this.theirHQ;
////        double distanceHQs = Math.sqrt(Math.pow((this.myHQ.x - this.theirHQ.x), 2) + Math.pow((this.myHQ.y - this.theirHQ.y), 2));
//        Map<Double, MapLocation> towerLocations= new HashMap<Double, MapLocation>();
//        List<Double> towerDistances = new ArrayList<Double>();
//        for (MapLocation tower : TowerLocations) {
////            double towerToEnemy = Math.sqrt(Math.pow((tower.x - this.theirHQ.x), 2) + Math.pow((tower.y - this.theirHQ.y), 2));
//            double towerToHQ = Math.sqrt(Math.pow((tower.x - this.myHQ.x), 2) + Math.pow((tower.y - this.myHQ.y), 2));
//            towerLocations.put(towerToHQ, tower);
//            towerDistances.add(towerToHQ);
//        }
//        double closestTower = Collections.min(towerDistances);
//        rallyTower = towerLocations.get(closestTower);
//        return rallyTower;
//    }
//}