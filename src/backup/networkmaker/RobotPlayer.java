package networkmaker;

import java.util.ArrayList;

import battlecode.common.*;

public class RobotPlayer {
	
	public static MapLocation enemy;
	public static MapLocation myLoc;
	public static int height;
	public static int width;
	public static ArrayList<Direction> path = new ArrayList<Direction>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	
	public static void run(RobotController rc){
		enemy = rc.senseEnemyHQLocation();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		if(rc.getType()==RobotType.HQ&&rc.getTeam()==Team.A){
			NetworkPathing.initNetworkPathing(rc);
		}
		//Robot[] nearby = rc.senseNearbyGameObjects(Robot.class,10000,rc.getTeam());
		while(true){
			try{
//				if(rc.getType()==RobotType.HQ&&
//						rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
//					if(rc.canMove(Direction.NORTH)&&rc.isActive())
//						rc.spawn(Direction.NORTH);
				if(rc.getType()==RobotType.HQ){
					
				}else if (rc.getType()==RobotType.SOLDIER){
					myLoc = rc.getLocation();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			rc.yield();
		}
	}
	
	//mapData[0][0], array query apparently costs 6 bytecodes
	//using getMapData, which adds an offset, costs 14 bytecodes (8 extra, confirmed two ways)
	//checking whether x and y are in bounds, four comparisons, costs a lot, like 10. (2x4 comparisons, 2 accesses ?)
	//one logical comparison costs 3. Maybe that's 2 for the comparison and 1 for the access. nope!
	//even with more accesses, it still costs 9 for three if statements
	//addition costs 2 as well. . .
	//assignment apparently costs 2 also
	//rotateright costs 2. 
	//1d array access costs 2.
	//for(int i=5;i>0;i--) is cheaper than 
	//for(int i=0;i<5;i++)
	//because comparison with zero is cheaper by 1 bc. But only if the zero appears second!
	
	
}