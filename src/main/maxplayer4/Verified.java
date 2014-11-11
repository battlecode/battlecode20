package maxplayer4;

import java.util.ArrayList;

import battlecode.common.*;

public class Verified{
	
	static ArrayList<MapLocation> pastMoves = new ArrayList<MapLocation>();
	
	public static void spawn(RobotController rc,Direction d,RobotType rtype,int reserveOre) throws GameActionException{
		MapLocation goal = rc.getLocation().add(d);
		if(rc.isActive()&&rc.senseObjectAtLocation(goal)==null&&(rc.getTeamOre()-reserveOre>rtype.oreCost)&&rc.senseTerrainTile(goal)==TerrainTile.NORMAL){
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
		if(rc.canMove()&&rc.canMove(d)){//rc.isActive()&&
			if(pastMoves.size()>0)
				pastMoves.remove(0);
			pastMoves.add(rc.getLocation());
			rc.move(d);
			return true;
		}
		return false;
	}

	public static void attack(RobotController rc, MapLocation targetLoc) throws GameActionException {
		if(rc.canAttack()&&rc.canAttackSquare(targetLoc)){//rc.isActive()&&
			rc.attackSquare(targetLoc);
		}
	}
	
}