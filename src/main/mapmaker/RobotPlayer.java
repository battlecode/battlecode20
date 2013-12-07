package mapmaker;

import java.util.ArrayList;

import battlecode.common.*;

public class RobotPlayer {
	
	public static MapLocation enemy;
	public static MapLocation myLoc;
	public static int height;
	public static int width;
	public static Direction[][] pathingData;
	public static int[][] distanceData;//integer comparisons are apparently cheaper (2 vs 4 b)
	public static int[][] mapData;//3 NORMAL, 2 ROAD, 1 VOID, 0 OFF_MAP;
	public static int[][] voidID;//unique ID for each contiguous void
	public static ArrayList<Direction> path = new ArrayList<Direction>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
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
//						makeMap();
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
		return mapData[x+2][y+2];
	}	
	private static int getMapData(MapLocation m){
		return getMapData(m.x,m.y);
	}
	private static void setMapData(int x, int y, int val){
		mapData[x+2][y+2] = val;
	}
	private static int getVoidID(int x, int y){
		return voidID[x+2][y+2];
	}
	private static int getVoidID(MapLocation m){
		return getVoidID(m.x,m.y);
	}
	private static void setVoidID(int x, int y, int val){
		voidID[x+2][y+2] = val;
	}
	
	private static void updateInternalMap(RobotController rc){//can take several rounds, but ultimately saves time
		mapData = new int[width+4][height+4];
		//move battlecode map representation to an internal integer array to save bytecode
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				setMapData(x,y,3-rc.senseTerrainTile(new MapLocation(x,y)).ordinal());//3 NORMAL, 2 ROAD, 1 VOID, 0 OFF_MAP;
			}
		}
		//put traversible tiles outside the map so that the contiguous void checker doesn't go there.
		setOuterRing(3);
	}
	
	private static void setOuterRing(int val){
		for(int x=-2;x<width+2;x++){
			setMapData(x,-2,val);
			setMapData(x,height+1,val);
		}
		for(int y=-2;y<width+2;y++){
			setMapData(-2,y,val);
			setMapData(width+1,y,val);
		}
	}
	
	private static void makeMap(){
		//want to find corridors between contiguous voids
		pathingData = new Direction[width][height];//direction to arrive at this tile fastest
		distanceData = new int[width][height];//closest distance to this tile
		voidID = new int[width+4][height+4];//unique ID for each contiguous void
		ArrayList<MapLocation> contiguousVoids = new ArrayList<MapLocation>();
		int voidID = 1;
		for(int x=-1;x<=width;x++){
			for(int y=-1;y<=height;y++){
				if(getMapData(x,y)<2&&getVoidID(x,y)==0){//void or offmap && voidID is not set (default 0)
					//find all contiguous voids
					findContiguousVoids(contiguousVoids,new MapLocation(x,y),voidID);
					voidID++;
					//System.out.println("contig void reached "+contiguousVoids.size());
				}
			}
		}
		//fill contiguous voids until they intersect
		fillContiguousVoids(contiguousVoids);
		//now, stored in the voidID array, are -1's that form roads around all the obstacles.
		//a unit can reach a road by a straight line from wherever he is to the closest road, I'm pretty sure.
		//To get somewhere by the roads requires connectivity info.
		//The connectivity should be computed from nodes. 
		//For each node, find which nodes it connects, and store them in an arraylist.
		//for each connecting pathway, store a list of maplocations making up the path.
		//(e.g. a dictionary like "from 2 to 5" -> 2005 -> arraylist<maplocation>)
		//to build a complete path to any node, a pathing algorithm must be applied to the nodal map.
		//then the connecting pathway maplocations should be all added into one big path. 
		
		//reaching the path network:
		//the HQ stores the directions when the pathnet tiles were formed
		//these directions always point toward the path.
		//then the soldier can request directions to a location
		//and the HQ can easily supply the next maplocation.
		
		//if there are no nodes, then the map is very open. 
		//There may be a loop path. Choose a single node arbitrarily, and compute its 180 degree opposite.
		//Use just these two nodes to navigate, I guess. 
		//One approach is to populate the map with artificial barriers, uniformly spaced.
		//Then the roads would not be too far apart in open-style maps.
	}
	//LOCATE CONTIGUOUS VOIDS
	private static void findContiguousVoids(ArrayList<MapLocation> contiguousVoids,MapLocation start,int voidID){
		ArrayList<MapLocation> outermost = new ArrayList<MapLocation>();
		outermost.add(start);
		addContiguousVoid(contiguousVoids,start,voidID);
		while(outermost.size()>0){
			outermost = findOutermostVoids(contiguousVoids, outermost,voidID);
		}
	}
	private static ArrayList<MapLocation> findOutermostVoids(ArrayList<MapLocation> contiguousVoids,ArrayList<MapLocation> outermost,int voidID){
		ArrayList<MapLocation> newOutermost = new ArrayList<MapLocation>();
		for(MapLocation current:outermost){
			//get new outermost
			for (Direction d: orthoDirs){
				MapLocation trial = current.add(d);
				//System.out.println("trying ("+trial.x+","+trial.y+") ... mapdata "+(getMapData(trial)<2)+", voidID "+(getVoidID(trial)==0));
				if(getMapData(trial)<2&&getVoidID(trial)==0){//void or offmap && voidID is not set (default 0)
					newOutermost.add(trial);
					addContiguousVoid(contiguousVoids,trial,voidID);
				}
			}
		}
		return newOutermost;
	}
	private static void addContiguousVoid(ArrayList<MapLocation> contiguousVoids,MapLocation site, int voidID){
		//for each void, set the ID and add it to the contiguous voids list, which will start the "flood"
		setVoidID(site.x,site.y,voidID);
		contiguousVoids.add(site);
	}
	//FILL CONTIGUOUS VOIDS UNTIL THEY INTERSECT
	private static void fillContiguousVoids(ArrayList<MapLocation> contiguousVoids){
		ArrayList<MapLocation> outermost = contiguousVoids;//**may be a reference error here
		setOuterRing(0);//makes the outside voids, so that there are no paths there.
		while(outermost.size()>0){
			outermost = fillFromVoids(outermost);
			//System.out.println("filling from "+outermost.size()+" tiles...");
		}
		//display the voidID array
		//displayArray(voidID);
		//displayDirectionalArray(pathingData);
	}
	private static void displayArray(int[][] intArray){
		for(int y = 0;y<intArray.length;y++){
			String line = "";
			for(int x=0;x<intArray[0].length;x++){
				//line+=(voidID[x][y]==-1)?"_":".";
				int i = intArray[x][y];
				if(i==-1){
					line+="-";
				}else{
					line+=i;
				}
			}
			System.out.println(line);
		}
	}
	private static void displayDirectionalArray(Direction[][] pathingData){
		String[] representations = {"^","/",">","\\","v","/","<","\\"};
		for(int y = 0;y<pathingData.length;y++){
			String line = "";
			for(int x=0;x<pathingData[0].length;x++){
				Direction d = pathingData[x][y];
				if(d==null){
					line+=" ";
				}else{
					line+=representations[d.ordinal()];
				}
			}
			System.out.println(line);
		}
	}
	private static ArrayList<MapLocation> fillFromVoids(ArrayList<MapLocation> outermost){
		ArrayList<MapLocation> newOutermost = new ArrayList<MapLocation>();
		for(MapLocation current:outermost){
			for(Direction d:orthoDirs){
				MapLocation trial = current.add(d);
				int myID = getVoidID(current.x,current.y);
				if(myID!=-1){//kill intersection-originated searches
					if(getMapData(trial)>1){//land or road && voidID is not set (default 0)
						int trialID = getVoidID(trial);
						if(trialID==0){//fill empty tile
							setVoidID(trial.x,trial.y,myID);
							newOutermost.add(trial);
							pathingData[trial.x][trial.y]=d;
						}else if(trialID!=myID){//intersected a neighbor contiguous void expansion
							setVoidID(trial.x,trial.y,-1);//for now, intersections have ID = -1; this is throwing away information?
						}//if it is equal to myID, do nothing.
					}
				}
			}
		}
		return newOutermost;
	}
	
	//FIND A PATH TO A LOCATION USIG FLOOD FILL
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
			//System.out.println("outermost Length is "+outermost.size());
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