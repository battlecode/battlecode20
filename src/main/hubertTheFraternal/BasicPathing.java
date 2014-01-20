package hubertTheFraternal;

import java.util.ArrayList;

import battlecode.common.*;

public class BasicPathing{
	
	static ArrayList<MapLocation> snailTrail = new ArrayList<MapLocation>();
	
	public static boolean canMove(Direction dir, boolean selfAvoiding, boolean avoidEnemyHQ){
		//include both rc.canMove and the snail Trail requirements
		MapLocation resultingLocation = RobotPlayer.rc.getLocation().add(dir);
		if(selfAvoiding){
			for(int i=0;i<snailTrail.size();i++){
				MapLocation m = snailTrail.get(i);
				if(!m.equals(RobotPlayer.rc.getLocation())){
					if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)){
						return false;
					}
				}
			}
		}
		if(avoidEnemyHQ)
			if(closeToEnemyHQ(resultingLocation))
				return false;
		//if you get through the loop, then dir is not adjacent to the icky snail trail
		return RobotPlayer.rc.canMove(dir);
	}
	
	public static boolean closeToEnemyHQ(MapLocation loc){
		return RobotPlayer.enemyHQ.distanceSquaredTo(loc)<=RobotType.HQ.attackRadiusMaxSquared;
	}
	
	public static void tryToMove(Direction chosenDirection,boolean selfAvoiding,boolean avoidEnemyHQ, boolean sneak) throws GameActionException{
		while(snailTrail.size()<2)
			snailTrail.add(new MapLocation(-1,-1));
		if(RobotPlayer.rc.isActive()){
			snailTrail.remove(0);
			snailTrail.add(RobotPlayer.rc.getLocation());
			for(int directionalOffset:RobotPlayer.directionalLooks){
				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = RobotPlayer.allDirections[(forwardInt+directionalOffset+8)%8];
				if(canMove(trialDir,selfAvoiding, avoidEnemyHQ)){
					if(sneak){
						RobotPlayer.rc.sneak(trialDir);
					}else{
						RobotPlayer.rc.move(trialDir);
					}
					//snailTrail.remove(0);
					//snailTrail.add(rc.getLocation());
					break;
				}
			}
			//System.out.println("I am at "+rc.getLocation()+", trail "+snailTrail.get(0)+snailTrail.get(1)+snailTrail.get(2));
		}
	}
	
}