package maxplayer4;

import java.util.ArrayList;

import battlecode.common.*;

public class Pathfinding{
	
	static int[] tryInt = new int[]{0,-1,1,-2,2};
	
	static Direction findAvailableSite(RobotController rc) {//TODO only build near allied towers?
		MapLocation myLoc = rc.getLocation();
		for(Direction d:Direction.values()){//a more efficient way would check your own tile, then either choose orthogonal or diagonal
			boolean ws = whiteSite(myLoc.x+d.dx,myLoc.y+d.dy);
			if(ws)
				if(rc.canMove(d))
					return d;
		}
		return null;
	}
	
	static Direction[] validDirs(RobotController rc,boolean mining){
		boolean tileForStanding = standingLane(rc.getLocation());
		boolean valid = mining?!tileForStanding:tileForStanding;
		if(valid){
			return new Direction[]{Direction.NORTH_EAST,Direction.SOUTH_EAST,Direction.SOUTH_WEST,Direction.NORTH_WEST};
		}else{
			return new Direction[]{Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
		}
	}
	
	public static boolean standingLane(MapLocation m){
		boolean ws = whiteSite(m);
		if(Clock.getRoundNum()/500%2==0)
			return ws;
		else
			return !ws;
	}
	
	public static boolean whiteSite(MapLocation m){
		return whiteSite(m.x,m.y);
	}
	
	public static boolean whiteSite(int x, int y){
		return (x+y)%2==1;
	}
	
	static Direction randomDir() {
		return Direction.values()[(int)(RobotPlayer.rand.nextDouble()*8)];
	}

	public static boolean goTo(RobotController rc, Direction towardTarget) throws GameActionException {
		for(int i:tryInt){
			Direction d = rotateDir(towardTarget,i);
			if(Verified.move(rc, d))
				return true;
		}
		return false;
	}
	
	static Direction rotateDir(Direction forward, int rotateAmount){
		return rotateDir(forward.ordinal(),rotateAmount);
	}
	static Direction rotateDir(int forward, int rotateAmount){
		return Direction.values()[(forward+rotateAmount+8)%8];
	}

	static MapLocation randomLocation(MapLocation loc, int dx) {
		return new MapLocation(
				loc.x+(int)(dx*(RobotPlayer.rand.nextDouble()-.5)),
				loc.y+(int)(dx*(RobotPlayer.rand.nextDouble()-.5))
				);
	}
	static MapLocation findMapEdge(RobotController rc, Direction d){
		MapLocation loc = rc.getLocation();
		while(rc.senseTerrainTile(loc)!=TerrainTile.OFF_MAP){
			loc=loc.add(d);
		}
		return loc;//padded so that the offmap adjacent to map edges can be queried in bounds
	}
	static MapLocation[] findMapCorners(RobotController rc){
		int top = findMapEdge(rc,Direction.NORTH).y;
		int left = findMapEdge(rc,Direction.WEST).x;
		int bottom = findMapEdge(rc,Direction.SOUTH).y;
		int right = findMapEdge(rc,Direction.EAST).x;
		int width = right-left+1;
		int height = bottom-top+1;
		return new MapLocation[]{new MapLocation(left,top),new MapLocation(right,bottom),new MapLocation(width,height)};
	}
	static int[][] buildInternalRepresentation(RobotController rc, MapLocation[] corners){
		//store the map locally to save bytecodes
		int width = corners[2].x;
		int height = corners[2].y;
		int internalMap[][] = new int[width][height];
		int startx = corners[0].x;
		int starty = corners[0].y;
		int x;
		int y;
		MapLocation look;
		TerrainTile t;
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				x = startx+i;
				y = starty+j;
				look = new MapLocation(x,y);
				t = rc.senseTerrainTile(look);
				if(t.equals(TerrainTile.NORMAL)){
					internalMap[i][j]=-1;
					//System.out.print(" ");
				}else if(t.equals(TerrainTile.VOID)){
					internalMap[i][j]=-2;
					//System.out.print("V");
				}else if(t.equals(TerrainTile.OFF_MAP)){
					internalMap[i][j]=-3;
					//System.out.print("O");
				}
			}
			//System.out.println("\n");
		}
		return internalMap;
	}
	
	
	
	static int[][] pathToPoint(RobotController rc,MapLocation[] corners,int[][] internalMap,MapLocation target) throws GameActionException{
		//draw arrows on the map pointing toward the target
		//uses a directional flood fill method
		ArrayList<MapLocation> particleList = new ArrayList<MapLocation>();
		MapLocation internalTarget = new MapLocation(target.x-corners[0].x,target.y-corners[0].y);
		particleList.add(internalTarget);
		while(particleList.size()>0){
			ArrayList<MapLocation> newParticleList = new ArrayList<MapLocation>();
			for(int dDir=0;dDir<8;dDir++){
				for(MapLocation particle:particleList){
					Direction checkDir = rotateDir(internalMap[particle.x][particle.y],dDir);
					buildArrowFrom(particle,checkDir,internalMap,newParticleList);
				}
			}
			particleList=newParticleList;
			//TODO consider yield
			//spawn furbies
			HQ.spawnFurbies(rc);
		}
		return internalMap;
	}
	
	static void buildArrowFrom(MapLocation particle,Direction d,int[][] internalMap, ArrayList<MapLocation> newParticleList){
		MapLocation move = particle.add(d);
		if(internalMap[move.x][move.y]==-1){//direction not recorded and place is empty
			internalMap[move.x][move.y] = d.ordinal();//record direction
			newParticleList.add(move);//add new place to particle list
		}
	}
	
}