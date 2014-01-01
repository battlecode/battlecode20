package pathingbot;

import java.util.ArrayList;

import battlecode.common.*;

public class RobotPlayer {
	
	public static MapLocation enemy;
	public static MapLocation myLoc;
	public static int height;
	public static int width;
	public static Direction[][] pathingData;
	public static int[][] distanceData;//integer comparisons are apparently cheaper (2 vs 4 b)
	public static int[][] mapData;
	public static ArrayList<Direction> path = new ArrayList<Direction>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	//public static Direction[] dirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	public static boolean shortestPathLocated;
	
	public static void run(RobotController rc){
		enemy = rc.senseEnemyHQLocation();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		updateInternalMap(rc);
		while(true){
			try{
				if(rc.getType()==RobotType.HQ&&
						rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
					if(rc.canMove(Direction.NORTH)&&rc.isActive())
						rc.spawn(Direction.NORTH);
				}else if (rc.getType()==RobotType.SOLDIER){
					myLoc = rc.getLocation();
					if(Clock.getRoundNum()==20&&rc.getTeam()==Team.A){
						if(path.size()==0){
							pathTo(myLoc,enemy,1000000);
						}
					}
					if(path.size()>0){
						if(rc.canMove(path.get(0))&&rc.isActive()){
							rc.move(path.get(0));
							path.remove(0);
						}
					}
//					if(rc.canMove(Direction.NORTH)&&rc.isActive())
//						rc.move(Direction.NORTH);
//					boolean b;
//					int n = Clock.getBytecodeNum();
//					if(width>0);
//					rc.setIndicatorString(0, ""+(Clock.getBytecodeNum()-n));
//					int n2 = Clock.getBytecodeNum();
//					if(width>1);
//					rc.setIndicatorString(2, ""+(Clock.getBytecodeNum()-n2));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			rc.yield();
		}
	}
	
	private static int getMapData(int x, int y){
		return mapData[x+1][y+1];
	}
	
	private static int getMapData(MapLocation m){
		return getMapData(m.x,m.y);
	}
	
	private static void setMapData(int x, int y, int val){
		mapData[x+1][y+1] = val;
	}
	
	private static void updateInternalMap(RobotController rc){//can take several rounds, but ultimately saves time
		mapData = new int[width+2][height+2];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				setMapData(x,y,3-rc.senseTerrainTile(new MapLocation(x,y)).ordinal());//3 NORMAL, 2 ROAD, 1 VOID, 0 OFF_MAP;
			}
		}
	}
	
	private static void pathTo(MapLocation start,MapLocation goal, int maxSearchDist) {
		//clear path info for next computation
		shortestPathLocated = false;
		path = new ArrayList<Direction>();
		pathingData = new Direction[width][height];//direction to arrive at this tile fastest
		distanceData = new int[width][height];//closest distance to this tile
		ArrayList<MapLocation> outermost = new ArrayList<MapLocation>();
		outermost.add(start);
		distanceData[start.x][start.y] = -maxSearchDist*10;//the 10 allows a multiple of 14 for diagonals
		while(!shortestPathLocated&&outermost.size()>0){
			System.out.println("outermost Length is "+outermost.size());
			outermost = getNewOutermost(outermost,start,goal);
		}
	}
	
	private static ArrayList<MapLocation> getNewOutermost(ArrayList<MapLocation> outermost,MapLocation start,MapLocation goal){
		//this function locates new outermost tiles to examine (like flood fill)
		ArrayList<MapLocation> newOutermost = new ArrayList<MapLocation>();
		ArrayList<Proposal> props = new ArrayList<Proposal>();//new proposed outermost tiles
		//propose tiles adjacent to the outermost ones
		for(MapLocation m:outermost){
			Proposal.generateProposals(m, distanceData[m.x][m.y],1, props, dirs);//TODO limit search to smart directions
		}
		//evaluate those proposed tiles
		for(Proposal p:props){
			//TODO right now, this only traverses "normal" tiles. it should handle roads.
			if(getMapData(p.loc)==3){//discard proposals that are off-map
				if(p.dist<distanceData[p.loc.x][p.loc.y]){//if the proposal is good,
					if(distanceData[p.loc.x][p.loc.y]!=0){//overwrite a previous proposal
						newOutermost.remove(p.loc);
					}
					distanceData[p.loc.x][p.loc.y]=p.dist;//update closest distance infomap
					pathingData[p.loc.x][p.loc.y]=p.dir;//update direction infomap
					newOutermost.add(p.loc);//add this to the list of outermost to consider
				}
			}
			if(p.loc.equals(goal)){
				listDirections(start,p.loc);
				shortestPathLocated = true;
				break;
			}
		}
		return newOutermost;
		//if there were no new outermost, and the goal was not reached, should look for closest accessible tile?
		//end condition: record closest dist to goal. when all other active "threads" exceed this, end.
		//simple end condition: end when the goal is reached by any thread.
		
	}
	
	private static void listDirections(MapLocation start,MapLocation end){
		MapLocation currentLoc = end;
		while(!currentLoc.equals(start)){
			Direction d = pathingData[currentLoc.x][currentLoc.y];
			path.add(0,d);
			currentLoc = currentLoc.add(d.opposite());//current location moves backwards to start
		}
		System.out.println("located goal in "+path.size()+" dir steps!");
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