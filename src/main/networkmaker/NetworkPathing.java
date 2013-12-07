package networkmaker;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import battlecode.common.*;

public class NetworkPathing{
	
	public static int height;
	public static int width;
	public static Direction[][] pathingData;
	public static int[][] distanceData;//integer comparisons are apparently cheaper (2 vs 4 b)
	public static int[][] mapData;//3 NORMAL, 2 ROAD, 1 VOID, 0 OFF_MAP;
	public static int[][] voidID;//unique ID for each contiguous void
	public static ArrayList<Direction> path = new ArrayList<Direction>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	
	public static ArrayList<MapLocation> nodes;
	public static ArrayList<ArrayList<Integer>> nodeConnections;
	public static Dictionary<int[],ArrayList<MapLocation>> pathingDictionary = new Hashtable<int[],ArrayList<MapLocation>>();
	
	public static void initNetworkPathing(RobotController rc){
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		//load all terrain info into local arrays
		updateInternalMap(rc);
		//locate effective pathways and directions to those pathways
		makeMap();//TODO this takes a while. Interrupt this with other actions.
		//make a network out of the pathways
		makeNetwork();
	}

	private static void makeNetwork(){
		//locate nodes
		nodes = new ArrayList<MapLocation>();
		ArrayList<ArrayList<Direction>> nodeExits = new ArrayList<ArrayList<Direction>>();
		for (int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				if(getVoidID(x,y)==-1){//if it's a path
					ArrayList<Direction> exits = new ArrayList<Direction>();
					//count neighboring paths
					int neighboringPaths = 0;
					MapLocation loc = new MapLocation(x,y);
					for(Direction d:dirs){
						if(getVoidID(loc.add(d))==-1){
							neighboringPaths++;
							exits.add(d);
						}
					}
					if(neighboringPaths>2){
						nodes.add(loc);
						nodeExits.add(exits);
					}
				}
			}
		}
		
		//for each node, see where the paths go
		nodeConnections = new ArrayList<ArrayList<Integer>>();
		int[] dirSearch = new int[]{0,1,-1,2,-2,-3,3};//don't look behind last dir
		//store each path (both ways) in a dictionary
		for(int i=0;i<nodes.size();i++){
			ArrayList<Integer> connectivity = new ArrayList<Integer>();
			MapLocation node = nodes.get(i);
			for(Direction d: nodeExits.get(i)){
				ArrayList<MapLocation> internodePath = new ArrayList<MapLocation>();
				MapLocation currentLoc = node.add(d);
				Direction currentDir = d;
				int nodeLocated = -1;
				while(nodeLocated==-1){
					internodePath.add(currentLoc);
					for(int dirIncrement:dirSearch){
						Direction trialDir = rotateDir(currentDir,dirIncrement);
						MapLocation trialLoc = currentLoc.add(trialDir);
						if(getVoidID(trialLoc)==-1){//found next tile of the path
							currentLoc = trialLoc;
							currentDir = trialDir;
							break;
						}
					}
					//is a dead end street possible?
					nodeLocated = mapContains(nodes,currentLoc);
				}
				connectivity.add(nodeLocated);
				pathingDictionary.put(new int[]{i,nodeLocated}, internodePath);
				//System.out.println("node #"+i+" exit "+d+" has path length "+internodePath.size()+" to node #"+nodeLocated);
			}
			nodeConnections.add(connectivity);
		}
		
		//pathfinding methods can now use nodeConnections to see which nodes connect to which others
		//also use the pathing dictionary to see how node A gets to node B, and how far is the path.
		
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
		
		//ugh, that one step takes a really long time. ~20 turns of 10k bytecodes
		//well. 
		//the . . . HQ can dump all the pathing data to messages.
		//then the soldiers can make their way to a node or the target, depending on which is closer.
		//once the soldier has reached the node, he can ask the HQ which path to take
		
		//should definitely store data on which nodes a path connects.
		//can command robots from HQ easily if it can sense their locations via senseNearbyGameObjects
		//can use different parts of the message array for different squads
		//it is useful to indicate the enemy location in terms of nodes and paths
		//if the enemy is on a path, can surround by going to either node
		//er, except the pathing would include the contingency that you can't use that interconnection
		
		//so, generally, store 0-7, direction toward path
		//on the path, store a packaged integer (path from node 2 to node 3 has integer 2003005. the last number indicates direction.)
		//on the node, store the node ID minus 1000. 
		//hmm...
		
		//HQ tells a soldier (squad) which node to go to. 
		//HQ by default provides path data, which implies the node positions.
		//soldier finds his way to path, the follows direction or antidirection to the node. 
		//soldier travels along the path until the correct maplocation is reached, then waits.
		//HQ notices when the maplocation is reached.
		//HQ can then tell the soldier to go to a maplocation, stepping along a path to somewhere far away.
	}
	
	private static ArrayList<MapLocation> findPath(MapLocation start, MapLocation end){
		ArrayList<MapLocation> completePath = new ArrayList<MapLocation>();
		//first find a route to the network
		//then find which nodes are at either end of the line segment (edge case, you land directly on a node)
		//this seeds the flood-fill with two start points at different distances
		//propagate flood-fill using nodeConnections for connectivity and pathingDictionary for distances
		//it should be fast, so continue until all paths have terminated
		//then work backwards from the goal node to assemble the path
		
		//if the goal maplocation is not a node, use the same process to find which nodes are closest to it.
		//there's a special case where the goal and the current tile are close together, and no nodal pathing is necessary.
		//so the easiest way might be to make the network entrance and network exit actually into real nodes (temporarily).
		//then the pathing can proceed without edge cases.
		//to insert a node:
		//give it a new node number
		//locate its two neighbors
		//for each neighbor, modify the connectivity list
		//update the new node's connectivity list
		//update the pathing dictionary
		
		//how long does the nodal analysis take? If it's not long, then you can use the original method.
		//too long! 20 turns.
		
		return completePath;
	}
	
	private static Direction rotateDir(Direction d, int increment){
		return dirs[(d.ordinal()+increment+8)%8];
	}
	
	private static int mapContains(ArrayList<MapLocation> list, MapLocation item){
		for(int i=0;i<list.size();i++){
			MapLocation m = list.get(i);
			if(m.x==item.x&&m.y==item.y)
				return i;
		}
		return -1;
	}
	
	//access map info in local arrays with an offset to accommodate tiles outside the map
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
	//load all terrain info into local arrays
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
	//locate effective pathways and directions to those pathways
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
					//TODO if there less than 3 void tiles in a clump, it's not a significant obstacle. Skip it here, and it should simplify pathing computations later. It can be handled with bug.
					//System.out.println("contig void reached "+contiguousVoids.size());
				}
			}
		}
		//fill contiguous voids until they intersect
		fillContiguousVoids(contiguousVoids);
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
	
}