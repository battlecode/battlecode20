package mediumbot;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class HQPlayer extends BasePlayer {
	MapLocation[] nearestEncampments;
	int[] assign;
	
	
	public HQPlayer(RobotController rc) throws GameActionException {
		super(rc);
		findNearestEncampments();
	}
	
	public void run() throws GameActionException {
		if(!rc.isActive()) 
			return;
		
		boolean shouldSpawn = rc.senseNearbyGameObjects(Robot.class, curLoc, 1000000, myTeam).length*5 < rc.getTeamPower();
		
		
		if(shouldSpawn) {
			spawnRandomly();
			int i = 0;
			while(i<assign.length&&assign[i]!=0) i++;
			MapLocation ml;
			if(i<assign.length) {
				assign[i] = 1;
				ml = nearestEncampments[i];
			} else 
				ml = null;
			msg.write(1, ml);
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
		int N = enc.length;
		int[] dists = new int[N];
		boolean[] used = new boolean[N];
		for(int n=0; n<N; n++) {
			dists[n] = curLoc.distanceSquaredTo(enc[n]);
			used[n] = false;
		}
		nearestEncampments = new MapLocation[N];
		assign = new int[N];
		for(int n=0; n<N; n++) {
			int best = -1;
			int val = 55555;
			for(int m=0; m<N; m++) {
				if(!used[m] && dists[m]<val) {
					best = m;
					val = dists[m];
				}
			}
			nearestEncampments[n] = enc[best];
			used[best] = true;
		}
	}
}
