package mediumbot;

import java.util.ArrayList;
import java.util.Comparator;

import scala.actors.threadpool.Arrays;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class HQPlayer extends BasePlayer {
	MapLocation[] nearestEncampments;
	
	public HQPlayer(RobotController rc) throws GameActionException {
		super(rc);
		spawnRandomly();
		findNearestEncampments();
	}
	
	public void run() throws GameActionException {
		if(!rc.isActive()) 
			return;
		
		boolean shouldSpawn = rc.senseNearbyGameObjects(Robot.class, curLoc, 1000000, myTeam).length*5 < rc.getTeamPower();
		
		
		if(shouldSpawn) {
			spawnRandomly();
		}
		
	}
	
	public void spawnRandomly() throws GameActionException {
		Direction dir = Direction.NORTH;
		do {
			if(rc.canMove(dir)) {
				rc.spawn(dir);
				return;
			} 
			dir = dir.rotateLeft();
		} while (dir!=Direction.NORTH);
	}
	
	public void findNearestEncampments() throws GameActionException {
		MapLocation[] enc;
		int r = 0;
		while(true) {
			r+=100;
			enc = rc.senseEncampmentSquares(curLoc, r, Team.NEUTRAL);
			if(enc.length > 20 || r > 2500)
				break;
		}
		Arrays.sort(enc, new Comparator<MapLocation>() {

			@Override
			public int compare(MapLocation arg0, MapLocation arg1) {
				return arg0.distanceSquaredTo(curLoc) - arg1.distanceSquaredTo(curLoc);
			}
			
		});
	}
}
