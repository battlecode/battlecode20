package maxplayer3;

import battlecode.common.*;

public class Furby{
	static MapLocation myLoc;
	static double thresh = 1;
	static MapLocation exploreLoc;
	static int myID = 0;
	
	public static void run(RobotController rc){
		while(true) {
			try {
				//update ID number
				myID = Message.furbyGetID(rc);
				rc.setIndicatorString(1, "ID "+myID+" supply "+rc.getSupplyLevel());
				//1st priority = shoot enemies in sight
				Fight.tryToShoot(rc);
				//2nd priority = go to enemies that have been broadcast
				Message.furbyReceiveCombat(rc,myID);
				//
				myLoc = rc.getLocation();
				int command = rc.readBroadcast(0);
				if(command==-1){//mining
					collectOre(rc);
				}else{//tech
					Technology.achieveTech(Technology.types.get(command),rc,true);
					collectOre(rc);
				}
				rc.yield();
			}catch(Exception e){
				e.printStackTrace();
				//something
			}
		}
	}


	private static void collectOre(RobotController rc) throws GameActionException {
		boolean inStandingLane = Pathfinding.standingLane(rc.getLocation());//either you're in a standing lane or a mining lane - it's like a chessboard, but the colors reverse at intervals
		if(!inStandingLane){//first check the tile you're standing on, if it's a valid mining tile
			if(Verified.mine(rc, thresh)){
				rc.setIndicatorString(2,"mining. Ore="+rc.senseOre(rc.getLocation()));
				return;
			}
		}
		//next check the tiles around you
		Direction[] checkDirs = Pathfinding.validDirs(rc, true);//valid directions for mining
		for(Direction d:checkDirs){
			MapLocation m1 = rc.getLocation().add(d);
			if(rc.senseOre(m1)>thresh&&rc.senseObjectAtLocation(m1)==null){
				//Verified.move(rc, d);
				Pathfinding.goTo(rc,d);
				rc.setIndicatorString(2,"stepping to "+d);
				return;
			}
		}
		//check farther away for some ores
		MapLocation[] visibleTiles = MapLocation.getAllMapLocationsWithinRadiusSq(rc.getLocation(), rc.getType().sensorRadiusSquared);
		for(MapLocation m2:visibleTiles){
			if(!Pathfinding.standingLane(m2)){
				if(rc.senseOre(m2)>thresh&&rc.senseObjectAtLocation(m2)==null){
					Direction towardTarget = rc.getLocation().directionTo(m2);
					Pathfinding.goTo(rc,towardTarget);
					rc.setIndicatorString(2,"going far to "+m2);
					return;
				}
			}
		}
		//no ores anywhere in sight, so explore
		if(exploreLoc==null){//get a new exploring location if the current one is null
			exploreLoc = Pathfinding.randomLocation(rc.senseHQLocation(),100);
		}
		//try to go toward the explore location
		Direction towardTarget = rc.getLocation().directionTo(exploreLoc);
		if(!Pathfinding.goTo(rc,towardTarget)){//if you get stuck, reset explore location
			exploreLoc=null;
		}
		rc.setIndicatorString(2,"exploring "+exploreLoc);
		//check if you are stuck in a pathing loop
		if(Verified.pastMoves.contains(rc.getLocation())){
			exploreLoc=null;
		}
	}
	
}