package networkmaker4;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import battlecode.common.*;

public class NetworkPathing{
	//initNetworkPathing creates a node-edge structure for navigation
	//ArrayList<MapLocation> testPath = findPath(2,5); gives a list of maplocations between nodes 2 and 5
	//for minions to reach nodes, minionData has directions to the closest edge.
	//once on the edge, the minion can follow it to either node. edge value e.g. 1002003: Edge from node 1 to 2, travel dir 3.
	//on the nodes, the value is -1000+nodeID
	
	//suggested usage: coordinated attacks
	//robots traverse network on their own, quite easily
	//HQ posts flags at nodes, such as wait here, or exit in direction 3.
	//the "wait here" flag requires the robot move aside a little to let others in, and keep checking the node post
	//this allows many robots to coordinate an attack
	
	//suggested usage: building towers
	//robots traversing will naturally create clumps of cows
	//the hq locates a good clump, traces it to a path location
	//the hq posts a maplocation target on a path somewhere
	//a soldier who comes by that path (by chance) checks the message array and finds the maplocation target
	//that soldier can go straight to the target and build something there, no pathing computation needed.
	
	//note on emergent micro:
	//condition for running away:
	//run away if the number of allies in range of the closest enemy is less than the number of enemies in range of me
	//this may form concaves-- it seems pretty good.
	
	public static int height;
	public static int width;
	public static Direction[][] pathingData;
	public static int[][] distanceData;//integer comparisons are apparently cheaper (2 vs 4 b)
	public static int[][] mapData;//3 NORMAL, 2 ROAD, 1 VOID, 0 OFF_MAP;
	public static int[][] voidID;//unique ID for each contiguous void
	public static int[][] minionData;//unique ID for each contiguous void
	public static ArrayList<Direction> path = new ArrayList<Direction>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	
	public static ArrayList<MapLocation> nodes;
	public static ArrayList<ArrayList<Integer>> nodeConnections;
	public static Dictionary<Integer,ArrayList<MapLocation>> pathingDictionary = new Hashtable<Integer,ArrayList<MapLocation>>();
	public static Dictionary<Integer,ArrayList<Direction>> directionDictionary = new Hashtable<Integer,ArrayList<Direction>>();
	public static Dictionary<Integer,ArrayList<Direction>> direction2Dictionary = new Hashtable<Integer,ArrayList<Direction>>();
	
	public static int[] distances;
	public static int[] fromNode;
	
	public static void initNetworkPathing(RobotController rc) throws GameActionException{
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		rc.broadcast(height*width-1, -9999);//tell minions that the minionData array has not yet been uploaded
		//load all terrain info into local arrays
		updateInternalMap(rc);//3 rounds
		//locate effective pathways and directions to those pathways
		makeMap();//54 rounds (30x30 map). Interrupt this with other actions.
		//make a network out of the pathways
		makeNetwork(rc);//25 rounds.
		//assemble minion data for broadcasting
		prepareMinionData();//4 rounds
		//post minion data to broadcast network
		broadcastMinionData(rc);//1 round
		
	}

	private static void broadcastMinionData(RobotController rc) throws GameActionException{
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				int index = y*width+x;
				rc.broadcast(index, minionData[x][y]);
			}
		}
	}
	
	private static void prepareMinionData() {
		minionData = new int[width][height];
		//write direction ordinals
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				Direction d = pathingData[x][y];
				minionData[x][y]=(d==null)?0:pathingData[x][y].ordinal();//these codes are from 0 to 7
			}
		}
		//write infocode on edges from low to high node numbers - path direction from low to high node number
		Enumeration<Integer> pe = directionDictionary.keys();
		while(pe.hasMoreElements()){
			Integer p = pe.nextElement();
			int node1 = p/1000;
			int node2 = p-1000*node1;
			if(node1>node2){
				ArrayList<MapLocation> locToWrite = pathingDictionary.get(p);
				ArrayList<Direction> dirToWrite = directionDictionary.get(p);
				ArrayList<Direction> dir2ToWrite = direction2Dictionary.get(p);
				for(int i=0;i<locToWrite.size();i++){
					MapLocation loc = locToWrite.get(i);
					Direction dir = dirToWrite.get(i);
					Direction dir2 = dir2ToWrite.get(i);
					int ordinal=dir.opposite().ordinal();
					int ordinal2=dir2.ordinal();
					int code = node1*1000000+node2*1000+10*ordinal2+ordinal;//it's impossible for both node 1 and node 2 to be 0, so this number is > 7, indicating an edge
					minionData[loc.x][loc.y] = code;
				}
			}
		}
		//write node codes
		for(int i=0;i<nodes.size();i++){
			MapLocation n =nodes.get(i);
			minionData[n.x][n.y]=i-1000;//these codes are from -1000 to -1000+nodeNumber. Node number <<1000, so you know it's a node.
		}
	}

	private static void makeNetwork(RobotController rc) throws GameActionException{
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
		for(int i=0;i<nodes.size();i++){//was for(int i=0;i<nodes.size();i++){
			ArrayList<Integer> connectivity = new ArrayList<Integer>();
			MapLocation node = nodes.get(i);
			for(Direction d: nodeExits.get(i)){
				ArrayList<MapLocation> internodePath = new ArrayList<MapLocation>();
				ArrayList<Direction> internodeDirection = new ArrayList<Direction>();
				ArrayList<Direction> internodeDirection2 = new ArrayList<Direction>();
				MapLocation currentLoc = node.add(d);
				Direction currentDir = d;
				int nodeLocated = mapContains(nodes,currentLoc);//the immediately adjacent tile might be a node.
				while(nodeLocated==-1){
					internodePath.add(currentLoc);
					internodeDirection.add(currentDir);
					internodeDirection2.add(currentDir);
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
				//if the key exists already, make a new node, change nodeLocated to that new node, truncate the path contents, create a nodeExits object with two items.
				if(pathingDictionary.get(1000*i+nodeLocated)!=null){//writing would overwrite an existing path, so make a new node.
					nodes.add(internodePath.get(internodePath.size()-1));
					nodeLocated = nodes.size();
					ArrayList<Direction> exits = new ArrayList<Direction>();
					exits.add(currentDir);
					exits.add(internodeDirection.get(internodeDirection.size()-1).opposite());
					nodeExits.add(exits);
				}else{
					internodePath.add(currentLoc);//add the end node to the path
					internodeDirection.add(currentDir);
					internodeDirection2.add(currentDir);
				}
				connectivity.add(nodeLocated);
				pathingDictionary.put(1000*i+nodeLocated, internodePath);
				directionDictionary.put(1000*i+nodeLocated, internodeDirection);//this is just for minionData
				internodeDirection2.remove(0);
				internodeDirection2.add(currentDir);
				direction2Dictionary.put(1000*i+nodeLocated, internodeDirection2);//this is also just for minionData
				//System.out.println("node #"+i+" exit "+d+" has path length "+internodePath.size()+" to node #"+nodeLocated);

				RobotPlayer.tryToSpawn();//this method takes a while, so keep up with the spawning.
			}
			nodeConnections.add(connectivity);
		}
		
	}
	
	private static void runTests(){//just storing some test code here
		//LOOK AT MAP
		int[][] nodeVisual = new int[width][height];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				nodeVisual[x][y]=-1;
			}
		}
		for(int i=0;i<nodes.size();i++){
			MapLocation m = nodes.get(i);
			nodeVisual[m.x][m.y] = i;
		}
		
		//DICTIONARY TEST
		Enumeration<Integer> pe = pathingDictionary.keys();
		while(pe.hasMoreElements()){
			Integer p = pe.nextElement();
			System.out.println(p+" length "+pathingDictionary.get(p).size());
		}
		//TEST
		ArrayList<MapLocation> testPath = findPath(2,5);
		for(MapLocation k:testPath){
			System.out.println("path "+k.x+","+k.y);
		}
		//PAINT TEST PATH
		for(MapLocation m:testPath){
			nodeVisual[m.x][m.y] = 9;
		}
		
		displayArray(nodeVisual);
		displayArray(voidID);
		
		//TEST - visualize an internode path
		Enumeration<Integer> keyEnum = pathingDictionary.keys();
		Integer akey = keyEnum.nextElement();
		int[][] visual = new int[width][height];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				visual[x][y]=-1;
			}
		}
		ArrayList<MapLocation> path = pathingDictionary.get(akey);
		ArrayList<Direction> dirList = directionDictionary.get(akey);
		for(int i=0;i<path.size();i++){
			visual[path.get(i).x][path.get(i).y] = dirList.get(i).opposite().ordinal();
		}
		displayArray(visual);
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

	//NETWORKED PATHFINDING
	private static ArrayList<MapLocation> findPath(int startNode, int endNode){
		ArrayList<MapLocation> completePath = new ArrayList<MapLocation>();
		distances = new int[nodes.size()];
		fromNode = new int[nodes.size()];
		ArrayList<Integer> outermost = new ArrayList<Integer>();
		outermost.add(startNode);
		distances[startNode]=-10000;
		while(outermost.size()>0){
			outermost = getNetworkOutermost(outermost,endNode);
		}
		ArrayList<Integer> nodalPath = getNodalPath(startNode,endNode);
		completePath = getCompletePath(nodalPath);
		return completePath;
	}
	private static ArrayList<Integer> getNetworkOutermost(ArrayList<Integer> outermost,int endNode) {
		ArrayList<Integer> newOutermost = new ArrayList<Integer>();
		for(Integer node:outermost){
			for(Integer connectedNode:nodeConnections.get(node)){
				int dist = distances[node]+pathingDictionary.get(1000*node+connectedNode).size();//size isn't a perfect representation of the path length, but it's cheap.
				if(dist<distances[connectedNode]){
					distances[connectedNode]=dist;
					fromNode[connectedNode]=node;
					if(connectedNode!=endNode)
						newOutermost.add(connectedNode);
				}
			}
		}
		return newOutermost;
	}
	private static ArrayList<Integer> getNodalPath(int startNode,int endNode){
		ArrayList<Integer> nodalPath = new ArrayList<Integer>();
		int currentNode = endNode;
		while(currentNode!=startNode){
			int nextNode = fromNode[currentNode];
			nodalPath.add(0,1000*nextNode+currentNode);
			currentNode = nextNode;
		}
		return nodalPath;
	}
	private static ArrayList<MapLocation> getCompletePath(ArrayList<Integer> nodalPath) {
		ArrayList<MapLocation> completePath = new ArrayList<MapLocation>();
		for(Integer code:nodalPath){
			ArrayList<MapLocation> partialPath = pathingDictionary.get(code);
			for(MapLocation p:partialPath){
				completePath.add(p);
			}
		}
		return completePath;
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
	private static void makeMap() throws GameActionException{
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
	private static void findContiguousVoids(ArrayList<MapLocation> contiguousVoids,MapLocation start,int voidID) throws GameActionException{
		ArrayList<MapLocation> outermost = new ArrayList<MapLocation>();
		outermost.add(start);
		addContiguousVoid(contiguousVoids,start,voidID);
		while(outermost.size()>0){
			outermost = findOutermostVoids(contiguousVoids, outermost,voidID);
			RobotPlayer.tryToSpawn();//this method takes a while, so keep up with the spawning.
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