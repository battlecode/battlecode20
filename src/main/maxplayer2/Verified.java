package maxplayer2;

import java.util.ArrayList;

import battlecode.common.*;

public class Verified{
	
	static ArrayList<MapLocation> pastMoves = new ArrayList<MapLocation>();
	
	public static void spawn(RobotController rc,Direction d,RobotType rtype) throws GameActionException{
		if(rc.isActive()&&rc.senseObjectAtLocation(rc.getLocation().add(d))==null&&rc.getTeamOre()>rtype.oreCost){
			rc.spawn(d,rtype);
		}
	}
	
	public static void build(RobotController rc,Direction d,RobotType rtype) throws GameActionException{
		if(rc.canBuild(d, rtype)&&rc.getTeamOre()>rtype.oreCost){
			rc.build(d, rtype);
		}
	}
	
	public static boolean mine(RobotController rc,double thresh) throws GameActionException{
		if(rc.isActive()&&rc.senseOre(rc.getLocation())>thresh){
			rc.mine();
			return true;
		}
		return false;
	}

	public static boolean move(RobotController rc, Direction d) throws GameActionException {
		if(rc.isActive()&&rc.canMove(d)){
			if(pastMoves.size()>0)
				pastMoves.remove(0);
			pastMoves.add(rc.getLocation());
			rc.move(d);
			return true;
		}
		return false;
	}
	
}