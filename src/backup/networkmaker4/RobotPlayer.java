package networkmaker4;

import java.util.ArrayList;

import battlecode.common.*;

public class RobotPlayer {
	
	//hq stuff
	public static int lastSpawnRound = -10000;
	//soldier stuff
	public static MapLocation enemy;
	public static MapLocation myLoc;
	public static int myID;
	public static int height;
	public static int width;
	public static ArrayList<Direction> path = new ArrayList<Direction>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	public static Direction[] orthoDirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	public static int[] dirSearch = new int[]{0,1,-1,2,-2,-3,3,4};
	public static int[][] minionData;
	public static boolean minionDataDownloaded=false;
	public static boolean positiveFollow = true;
	public static int lastNode = -1;
	public static int currentNode = -1;
	public static RobotController rc;
	
	public static Direction persistentRandomDirection;
	public static double runAwayHitpoints = 50;
	public static Robot[] enemyRobots;
	public static boolean fleeing = false;
	public static MapLocation closestEnemy;
	public static MapLocation enemyCenter;
	public static boolean foundPastrSite = false;
	//issue: the robot checks 1/4 of the tiles to build pastrs
	//so when it finds a good spot, it starts going that way
	//but then it forgets which tile was important, and it goes back to patrolling
	//it needs to be more persistent
	
	//sometimes multiple robots are trying to build at the same time
	//it'd be handy to have a way of telling if a robot is constructing something
	//otherwise, it's necessary to build only one thing at a time, and wait a bunch of rounds before building another.
	
	public static void run(RobotController myRC) throws GameActionException{
		rc=myRC;
		enemy = rc.senseEnemyHQLocation();
		width = rc.getMapWidth();
		height = rc.getMapHeight();
		if(rc.getType()==RobotType.HQ)
			rc.broadcast(height*width-1, -9999);
		if(rc.getType()==RobotType.HQ&&rc.getTeam()==Team.A){
			NetworkPathing.initNetworkPathing(rc);
		}else if(rc.getType()==RobotType.SOLDIER){
			minionData = new int[width][height];
			persistentRandomDirection = dirs[(int)(8*random())];
		}
		//Robot[] nearby = rc.senseNearbyGameObjects(Robot.class,10000,rc.getTeam());
		while(true){
			try{
				if(rc.getType()==RobotType.HQ){
					tryToSpawn();
				}else if (rc.getType()==RobotType.SOLDIER){
					myLoc = rc.getLocation();
					myID = rc.getRobot().getID();
					soldier();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			rc.yield();
		}
	}
	
	public static void tryToSpawn() throws GameActionException {
		if(Clock.getRoundNum()>=lastSpawnRound+GameConstants.HQ_SPAWN_DELAY_CONSTANT_1){
			if(rc.isActive()&&rc.canMove(dirs[0])&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
				rc.spawn(dirs[0]);
				lastSpawnRound=Clock.getRoundNum();
			}
		}
	}

	public static void soldier() throws GameActionException{
		//compute center of enemies nearby
		enemyCenter = findEnemyCenter();
		Robot[] alliedRobots;
		if(enemyRobots.length>0){//count the allies that are in range of the closest enemy
			alliedRobots = rc.senseNearbyGameObjects(Robot.class,closestEnemy,rc.getType().attackRadiusMaxSquared,rc.getTeam());
		}else{
			alliedRobots = rc.senseNearbyGameObjects(Robot.class,rc.getType().attackRadiusMaxSquared,rc.getTeam());
		}
		if(!minionDataDownloaded){
			//check the network to see if minion data is available for download
			if(rc.readBroadcast(height*width-1)>-9999){//minion map is ready when its last value has been written
				downloadMinionMap();
			}
			randomPathing();
		}else{//data has been downloaded, so use it
			//detect all cows

			MapLocation[] es = rc.senseAlliedEncampmentSquares();
			MapLocation pastrSite = null;
			if(es.length<5){
				pastrSite = findPastrSite(foundPastrSite?1:4);
				foundPastrSite=(pastrSite!=null);
			}
			
			if(pastrSite!=null){//then go to pastr site and build there
				rc.setIndicatorString(1, "trying to build pastr");
				goBuildPastr(pastrSite);
			}else{
				rc.setIndicatorString(1, "patrolling");
				//TODO maybe there should be some followers of a pather.
				if(((rc.getEnergon()>runAwayHitpoints//if healthy, don't flee
						||(alliedRobots.length+1)>enemyRobots.length)//can be unhealthy if you outnumber enemy
						||enemyRobots.length==0)//if no enemies around, don't flee
						&&myLoc.distanceSquaredTo(enemy)>RobotType.HQ.attackRadiusMaxSquared){//if near enemy HQ, flee
					fleeing=false;
					networkWalk();
				}else{
					fleeing=true;
					networkFlee();
				}
			}
		}
	}

	private static void goBuildPastr(MapLocation pastrSite) throws GameActionException{
		int myLocCode = minionData[myLoc.x][myLoc.y];
		if(myLocCode>1000||myLocCode<0||myLoc.distanceSquaredTo(pastrSite)>=GameConstants.PASTR_RANGE){
			tryToGo(rc.getLocation().directionTo(pastrSite));
			myLoc = rc.getLocation();
		}else{
			if(rc.isActive()){
				rc.construct(RobotType.PASTR);
			}
		}
	}
	
	private static MapLocation findPastrSite(int checkFraction) throws GameActionException{
		//int checkFraction = 4;//don't check all the sites every round. 
		double cowThreshold = 20.0/(1.0-GameConstants.NEUTRALS_TURN_DECAY);
		double mostCows = cowThreshold;
		double challengerCows = 0;
		MapLocation mostCowLoc = null;
		
		if(Clock.getBytecodeNum()<2000){
			MapLocation[] checkLocs = MapLocation.getAllMapLocationsWithinRadiusSq(myLoc, rc.getType().sensorRadiusSquared);
			double phase =(double) (Clock.getRoundNum()%checkFraction)/(double) checkFraction;
			int start = (int)(phase*checkLocs.length);
			int end = (int)((phase+1.0/checkFraction)*checkLocs.length);
			for(int i=start;i<end;i++){
				MapLocation m = checkLocs[i];
				challengerCows=rc.senseCowsAtLocation(m);
				if(challengerCows>mostCows){
					mostCowLoc = m;
					mostCows=challengerCows;
				}
			}
		}

		if(mostCowLoc!=null){
			//check that there is no allied robot about to take that tile
			if(tileOccupied(mostCowLoc))
				return null;
			
			//check for existing pastrs that cover the area in question
			int outsidePastrRange = GameConstants.PASTR_RANGE+1;
			for(MapLocation m:rc.senseAlliedEncampmentSquares()){
				if(mostCowLoc.distanceSquaredTo(m)<outsidePastrRange){
					return null;
				}
			}
			
			//a cow location is only valid if it is accessible by a straight line.
			MapLocation currentLoc = mostCowLoc;
			while(!rc.senseTerrainTile(currentLoc).equals(TerrainTile.VOID)){//testing for open path toward the mostCowLoc
				Direction d = currentLoc.directionTo(myLoc);
				currentLoc = currentLoc.add(d);
				if(currentLoc.equals(myLoc)){
					rc.setIndicatorString(2, "viable location found at "+mostCowLoc.x+","+mostCowLoc.y);
					return mostCowLoc;
				}
			}
		}
		
		return null;
	}
	
	private static boolean tileOccupied(MapLocation t){
		return (rc.senseNearbyGameObjects(Robot.class,t,1,rc.getTeam()).length!=0);
	}
	
	private static void randomPathing() throws GameActionException{
		if(random()>0.9){
			if(random()>0.5){
				persistentRandomDirection = persistentRandomDirection.rotateLeft();
			}else{
				persistentRandomDirection = persistentRandomDirection.rotateRight();
			}
		}
		if(!rc.canMove(persistentRandomDirection)){
			TerrainTile ahead = rc.senseTerrainTile(myLoc.add(persistentRandomDirection));
			if(ahead.equals(TerrainTile.OFF_MAP)||ahead.equals(TerrainTile.VOID)){
				if(random()>0.5){
					persistentRandomDirection = persistentRandomDirection.rotateLeft();
				}else{
					persistentRandomDirection = persistentRandomDirection.rotateRight();
				}
			}
		}
		rc.setIndicatorString(0, ""+persistentRandomDirection);
		tryToGo(persistentRandomDirection);//run around at random
	}
	
	private static void networkFlee() throws GameActionException{
		int currentTile = minionData[myLoc.x][myLoc.y];
		rc.setIndicatorString(0, "network flee, "+currentTile);
		
		if(currentTile<0){//on a node: take exit that runs away
			chooseFarthestExit(enemyCenter);
		}else if(currentTile<8){//out in the open: follow arrows to the closest edge
			tryToGo(currentTile);
		}else{//on an edge: take the direction that runs away
			int twodir = currentTile%1000;
			int dir1 = twodir%10;
			int dir2 = twodir/10;
			int dist1 = myLoc.add(dirs[dir1]).distanceSquaredTo(enemyCenter);
			int dist2 = myLoc.add(dirs[dir2]).distanceSquaredTo(enemyCenter);
			if(dist1>dist2){
				positiveFollow=false;
				tryToGo(dir1);
			}else{
				positiveFollow=true;
				tryToGo(dir2);
			}
			tryToGo(positiveFollow?dir2:dir1);
		}
	}
	private static MapLocation findEnemyCenter() throws GameActionException{
		//finds center of enemies and also closest enemy
		enemyRobots = rc.senseNearbyGameObjects(Robot.class,rc.getType().attackRadiusMaxSquared,rc.getTeam().opponent());
		if(enemyRobots.length>0){
			int closestDist = 100000;
			int challengerDist = 100000;
			int xtot=0;
			int ytot=0;
			int tot=0;
			for(Robot r:enemyRobots){
				MapLocation rl = rc.senseRobotInfo(r).location;
				xtot+=rl.x;
				ytot+=rl.y;
				tot++;
				challengerDist = myLoc.distanceSquaredTo(rl);
				if(challengerDist<closestDist){
					closestDist=challengerDist;
					closestEnemy = rl;
				}
			}
			return new MapLocation(xtot/tot,ytot/tot);
		}else{
			return null;
		}
	}
	
	private static void chooseFarthestExit(MapLocation enemyCenter) throws GameActionException{//TODO this method will fail at map edge
		//locate valid directions
		ArrayList<Direction> validNeighbors = new ArrayList<Direction>();
		ArrayList<Boolean> followSign = new ArrayList<Boolean>();
		for(Direction d:dirs){
			MapLocation checkLoc = myLoc.add(d);
			int locationCode = minionData[checkLoc.x][checkLoc.y];
			if(locationCode>1000){//adjacent *edge*
				int node1 = locationCode/1000000;
				int node2 = (locationCode%1000000)/1000;
				if(currentNode==node2){
					validNeighbors.add(d);
					followSign.add(currentNode>node1);
				}else{// assuming (currentNode==node1)
					validNeighbors.add(d);
					followSign.add(currentNode>node2);
				}
			}else if(locationCode<0){//adjacent *node* is also a valid destination
				validNeighbors.add(d);
				followSign.add(positiveFollow);//follow sign doesn't matter.
			}
		}
		//now choose whichever is farthest
		if(validNeighbors.size()>0){
			//choose the direction giving the greatest distance
			int greatestDist = 0;
			int challengerDist;
			int choice=0;
			for(int i=0;i<validNeighbors.size();i++){
				MapLocation trial = myLoc.add(validNeighbors.get(i));
				challengerDist=trial.distanceSquaredTo(enemyCenter);
				if(challengerDist>greatestDist){
					greatestDist=challengerDist;
					choice=i;
				}
			}
			//go in that direction
			positiveFollow = followSign.get(choice);
			tryToGo(validNeighbors.get(choice));
		}else{
			//this should never happen
		}
	}
	
	
	private static void networkWalk() throws GameActionException{
		int currentTile = minionData[myLoc.x][myLoc.y];
		rc.setIndicatorString(0, "network walk,  "+currentTile);
		if(currentTile<0){//on a node: choose a random direction to move and choose a positive or negative direction
			int nodeID = currentTile+1000;
			if(currentNode!=nodeID){
				lastNode = currentNode;
				currentNode = nodeID;
			}
			chooseRandomExit();
		}else if(currentTile<8){//out in the open: follow arrows to the closest edge
			tryToGo(currentTile);
		}else{//on an edge: follow arrows in positive or negative direction
			int dirs = currentTile%1000;
			int dir1 = dirs%10;
			int dir2 = dirs/10;
			tryToGo(positiveFollow?dir2:dir1);
		}
	}
	
	private static void chooseRandomExit() throws GameActionException{//TODO this method will fail at map edge
		//locate valid directions
		ArrayList<Direction> validNeighbors = new ArrayList<Direction>();
		ArrayList<Boolean> followSign = new ArrayList<Boolean>();
		for(Direction d:dirs){
			MapLocation checkLoc = myLoc.add(d);
			int locationCode = minionData[checkLoc.x][checkLoc.y];
			if(locationCode>1000){
				int node1 = locationCode/1000000;
				int node2 = (locationCode%1000000)/1000;
				if((node1==lastNode&&node2==currentNode)||(node2==lastNode&&node1==currentNode)){
					//do not go back along old path
				}else if(currentNode==node2){
					validNeighbors.add(d);
					followSign.add(currentNode>node1);
				}else{// assuming (currentNode==node1)
					validNeighbors.add(d);
					followSign.add(currentNode>node2);
				}
			}else if(locationCode<0){//adjacent node is a valid destination
				validNeighbors.add(d);
				followSign.add(positiveFollow);//follow sign doesn't matter.
			}
		}
		
		if(validNeighbors.size()>0){
			//choose the random direction
			int choice = (int)(validNeighbors.size()*random());
			//go in that direction
			positiveFollow = followSign.get(choice);
			tryToGo(validNeighbors.get(choice));
		}else{
			//this should never happen
		}
	}
	
	private static void tryToGo(int dirInt) throws GameActionException{
		tryToGo(dirs[dirInt],false);
	}
	
	private static void tryToGo(Direction d) throws GameActionException{
		tryToGo(d,false);
	}
	
	private static void tryToGo(Direction d,boolean sneak) throws GameActionException{
		if(rc.isActive()&&!fleeing){
			//see if there's something to shoot
			//Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class,rc.getType().attackRadiusMaxSquared,rc.getTeam().opponent());
			if(enemyRobots.length>0){
				rc.attackSquare(closestEnemy);//rc.senseRobotInfo(enemyRobots[0]).location
			}
		}
		if(rc.isActive()){
			//otherwise, try to move in the given direction
			for(int dirInt:dirSearch){
				Direction trialDir = dirs[(d.ordinal()+8+dirInt)%8];
				if(rc.canMove(trialDir)){
					if(sneak){
						rc.sneak(trialDir);
					}else{
						rc.move(trialDir);
					}
					break;
				}
			}
		}
	}

	private static void downloadMinionMap() throws GameActionException {
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				int index = y*width+x;
				minionData[x][y] = rc.readBroadcast(index);
			}
		}
		minionDataDownloaded=true;
	}
	
	private static double random(){
		double d = (Math.random()*myID*Clock.getRoundNum());
		return d-(int)d;
	}
	
}