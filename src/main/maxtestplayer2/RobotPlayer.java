package maxtestplayer2;

import java.util.Random;

import battlecode.common.*;

public class RobotPlayer {
	public static void run(RobotController rc) {
		
		Utility.initRand(rc.getID());
		RobotType techGoal = RobotType.TANK;
		double minerfactoryRatio = 1.0;
		//TODO:transfer supplies
		//stay away from enemy HQ and towers
		//attack en masse
		//fighters follow one another
		//fighters periodically go back to HQ
		//build supply depots? Some ratio to the number of minerfactories?
		
        while (true) {
            try {
            	if (rc.getType() == RobotType.HQ) {
                    Utility.spawn(rc, Direction.values()[(int)(Math.random()*8)], RobotType.FURBY, 0);
                    Utility.tryToShoot(rc);
                    Comms.listAlliedRobotCount(rc);
                }else if(rc.getType() == RobotType.FURBY){
                	Utility.buildInRatio(rc, RobotType.MINER.spawnSource, techGoal.spawnSource,  techGoal.oreCost,minerfactoryRatio);
                	//Utility.tryToBuild(rc, RobotType.MINER.spawnSource, techGoal.oreCost, 5);
                	//Utility.tryToBuild(rc, techGoal.spawnSource, techGoal.oreCost, 5);
                	Utility.mine(rc, 0);
                }else if(rc.getType()==RobotType.MINERFACTORY){
                	Utility.spawn(rc, Direction.values()[(int)(Math.random()*8)], RobotType.MINER, 50);
                }else if(rc.getType()==RobotType.MINER){
                	Utility.mine(rc, 0);
                }else if(rc.getType()==techGoal.spawnSource){
                	Utility.spawn(rc, Direction.values()[(int)(Math.random()*8)], techGoal, 50);
                }else if(rc.getType()==techGoal){
                	Utility.roamAndFight(rc);
                }
            	rc.yield();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

}