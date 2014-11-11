package maxplayer3;

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
			Direction d = Direction.values()[(towardTarget.ordinal()+i+8)%8];
			if(Verified.move(rc, d))
				return true;
		}
		return false;
	}

	static MapLocation randomLocation(MapLocation loc, int dx) {
		return new MapLocation(
				loc.x+(int)(dx*(RobotPlayer.rand.nextDouble()-.5)),
				loc.y+(int)(dx*(RobotPlayer.rand.nextDouble()-.5))
				);
	}
	
}