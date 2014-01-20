package hubertTheFraternal;

import java.util.ArrayList;
import java.util.Hashtable;

import battlecode.common.*;

public class BreadthFirst {

	public static RobotController rc;
	public static MapLocation enemy;
	public static MapLocation myLoc;
	public static int height;
	public static int width;
	public static Direction[][] pathingData;
	public static int[][] distanceData;//integer comparisons are apparently cheaper (2 vs 4 b)
	public static int[][] mapData;
	public static ArrayList<MapLocation> path = new ArrayList<MapLocation>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	//public static Direction[] dirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	public static boolean shortestPathLocated;
	public static RobotController rci;
	public static Hashtable<MapLocation,Direction[][]> storedPathingData = new Hashtable<MapLocation,Direction[][]>();
	
	//pathTo(myLoc,enemy,1000000);
	
	public static void init(RobotController rci,int bigBoxSize){
		rc = rci;
		width = rc.getMapWidth()/bigBoxSize;
		height = rc.getMapHeight()/bigBoxSize;
		MapAssessment.assessMap(bigBoxSize, rci);
		//MapAssessment.printCoarseMap();
		//MapAssessment.printBigCoarseMap(rci);
		updateInternalMap(rc);
		
	}
	
	private static int getMapData(int x, int y){
		return mapData[x+1][y+1];
	}
	
	public static int getMapData(MapLocation m){
		return getMapData(m.x,m.y);
	}
	
	private static void setMapData(int x, int y, int val){
		mapData[x+1][y+1] = val;
	}
	
	private static void updateInternalMap(RobotController rc){//can take several rounds, but ultimately saves time
		mapData = new int[width+2][height+2];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				int val = MapAssessment.coarseMap[x][y];
				if(val==MapAssessment.bigBoxSize*MapAssessment.bigBoxSize){//completely filled with voids
					val=0;//if it's zero, consider it non-traversible
				}else{
					val+=10000;//if it's >= 10000, consider it on-map
				}
				setMapData(x,y,val);//0 off map, >= 10000 on-map, with val-10000 obstacles in the box
			}
		}
	}
	
	@SuppressWarnings("unused")
	public static ArrayList<MapLocation> pathTo(MapLocation start,MapLocation uncheckedGoal, int maxSearchDist) throws GameActionException {
		//check that goal is inside the size of the coarsened map
		MapLocation goal = trimGoal(uncheckedGoal);
		//save pathingData for each starting location- it is a reusable solution for any goal!
		if(storedPathingData.containsKey(start)){
			pathingData = storedPathingData.get(start);
		}else{
			//clear path info for next computation
			shortestPathLocated = false;
			pathingData = new Direction[width][height];//direction to arrive at this tile fastest
			distanceData = new int[width][height];//closest distance to this tile
			ArrayList<MapLocation> outermost = new ArrayList<MapLocation>();
			outermost.add(start);
			distanceData[start.x][start.y] = -maxSearchDist*10;//the 10 allows a multiple of 14 for diagonals
			while(!shortestPathLocated&&outermost.size()>0){
				//System.out.println("outermost Length is "+outermost.size());
				outermost = getNewOutermost(outermost,start,goal);
			}
			storedPathingData.put(start, pathingData);
		}
		try{
			listDirections(start,goal);//write maplocations to "path"\
		}catch(Exception e){
			printDirectionArray(start,goal);
			MapAssessment.printBigCoarseMap(rc);
			//RobotPlayer.die=true;
			//the goal is inaccessible
			//find a different goal
		}
		return path;
	}
	
	public static MapLocation trimGoal(MapLocation uncheckedGoal){
		//make sure the goal is inside bounds
		return new MapLocation(Math.min(uncheckedGoal.x, width-1),Math.min(uncheckedGoal.y, height-1));
	}
	
	private static ArrayList<MapLocation> getNewOutermost(ArrayList<MapLocation> outermost,MapLocation start,MapLocation goal){
		//this function locates new outermost tiles to examine (like flood fill)
		ArrayList<MapLocation> newOutermost = new ArrayList<MapLocation>();
		ArrayList<Proposal> props = new ArrayList<Proposal>();//new proposed outermost tiles
		//propose tiles adjacent to the outermost ones
		for(MapLocation m:outermost){
			Proposal.generateProposals(m, distanceData[m.x][m.y],1, props, dirs);//all proposals are traversible
		}
		//evaluate those proposed tiles
		for(Proposal p:props){
				if(p.dist<distanceData[p.loc.x][p.loc.y]){//if the proposal is good,
					if(distanceData[p.loc.x][p.loc.y]!=0){//overwrite a previous proposal
						newOutermost.remove(p.loc);
					}
					distanceData[p.loc.x][p.loc.y]=p.dist;//update closest distance infomap
					pathingData[p.loc.x][p.loc.y]=p.dir;//update direction infomap
					newOutermost.add(p.loc);//add this to the list of outermost to consider
				}
				//the following is commented: keeps searching even when goal has been reached once
//			if(p.loc.equals(goal)){
//				shortestPathLocated = true;
//				break;
//			}
		}
		return newOutermost;
		//if there were no new outermost, and the goal was not reached, should look for closest accessible tile?
		//end condition: record closest dist to goal. when all other active "threads" exceed this, end.
		//simple end condition: end when the goal is reached by any thread.
		
	}
	
	private static void listDirections(MapLocation start,MapLocation end){
		//a badly named function. It compiles a list of maplocations now.
		path = new ArrayList<MapLocation>();
		//(path used to be an ArrayList of Directions)
		MapLocation currentLoc = end;
		while(!currentLoc.equals(start)){
			Direction d = pathingData[currentLoc.x][currentLoc.y];
			path.add(0,currentLoc);
			currentLoc = currentLoc.add(d.opposite());//current location moves backwards to start
		}
		path.add(0,start);
		//if(path.size()==0)
		//	path.add(end);
		//System.out.println("located goal in "+path.size()+" dir steps!");
	}

	//once you have a path, you want to get the next direction you need to go in.
	//this function should truncate the path as you move along it, and also give the next direction.
	public static Direction getNextDirection(ArrayList<MapLocation> path, int bigBoxSize){
		
		//loop through the path, looking for the closest tile.
		MapLocation myLocation = VectorFunctions.mldivide(rc.getLocation(),bigBoxSize);
		int closestIndex = VectorFunctions.findClosest(path, myLocation);
		
		//delete all the tiles before the closest one
		for(int i=0;i<closestIndex;i++){
			path.remove(0);
		}
		
		//just check the bottom member of the path, to see if it needs truncating
		if(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize).equals(path.get(0))
				&&path.size()>1){//will not delete the path entirely
			path.remove(0);
		}
		return rc.getLocation().directionTo(VectorFunctions.bigBoxCenter(path.get(0),bigBoxSize));
	}
	
	public static void printDirectionArray(MapLocation start, MapLocation goal){
		System.out.println("Direction map:");
		System.out.println("start: "+start+", goal: "+goal);
		for(int x=0;x<BreadthFirst.pathingData[0].length;x++){
			for(int y=0;y<BreadthFirst.pathingData.length;y++){
				Direction d = BreadthFirst.pathingData[x][y];
				if(d==null){
					System.out.print("X");
				}else{
					System.out.print(d.ordinal());
				}
			}
			System.out.println();
		}
		System.out.println("done printing map.");
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