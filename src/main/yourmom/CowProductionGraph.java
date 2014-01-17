package yourmom;

import battlecode.common.MapLocation;

/** This is a sub data structure in the map cache that dynamically stores information 
 * about the power nodes a robot has explored. <br>
 * Probably only archons and scouts will store this information, and only archons will use it. <br>
 * <br> 
 * Whenever we sense a new power node, we add it and its new neighbors into nodeLocations,
 * and add its connections into an adjacency list.
 */
public class CowProductionGraph {
	/** The locations of the power nodes we know of. <br>
	 * The 0th element should always be null.
	 */
	final MapLocation[] nodeLocations;
	/** How many nodes we have heard of the whereabouts of. */
	short nodeCount;
	/** How many nodes we have sensed, either directly or via shared exploration. <br>
	 * This number can be at most nodeCount. 
	 */
	short nodeSensedCount;
	/** Have we sensed this power node, either directly or via shared exploration? <br>
	 * The 0th element should always be false.
	 */
	final boolean[] nodeSensed;
	/** a[i][j] is the index of an edge that node i is connected to. */
	final short[][] adjacencyList;
	/** a[i] is how many edges are currently stored in the adjacency list of node i.  <br>
	 * This gets incremented as we learn about the node through its neighbor nodes, and then
	 * becomes the accurate number when we sensed the power node.
	 */
	final short[] degreeCount;
	/** This id, when used as an index in the nodeLocations array, 
	 * will give the location of the enemy power core.
	 * 0 means we don't know about it yet.
	 */
	short enemyPowerCoreID;
	
	public CowProductionGraph() {
		nodeLocations = new MapLocation[51];
		adjacencyList = new short[51][50];
		degreeCount = new short[51];
		nodeSensed = new boolean[51];
	}
	
	@Override
	public String toString() {
		String ret = "\n";
		for(int i=1; i<=nodeCount; i++) {
			ret+="node #"+i+" "+nodeLocations[i]+" "+nodeSensed[i];
			for(int j=0; j<degreeCount[i]; j++) {
				ret+=" "+adjacencyList[i][j];
			}
			ret+="\n";
		}
		ret+="enemy core is node #"+enemyPowerCoreID+"\n";
		return ret;
	}
	
}