package turtlebot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class HQPlayer extends BasePlayer {
	MapLocation[] nearestEncampments;
	int[] assign;
	boolean allIn = false;
	boolean techFast = false;
	boolean sentInitialSoldier = false;
	
	public HQPlayer(RobotController rc) throws GameActionException {
		super(rc);
		findNearestEncampments();
	}
	
	public void run() throws GameActionException {
		if(rc.checkResearchProgress(Upgrade.NUKE)<Upgrade.NUKE.numRounds/2+2 && rc.senseEnemyNukeHalfDone())
			allIn = true;
		else if(rc.senseEnemyNukeHalfDone()) 
			techFast = true;
		RobotInfo nearestEnemy = Util.nearestEnemy(rc, 9999);
		int nearestEnemyDist = nearestEnemy==null?9999:nearestEnemy.location.distanceSquaredTo(curLoc);
		msg.write(7, nearestEnemy==null?null:nearestEnemy.location);
		if(allIn) msg.write(13, 1);
		if(!rc.isActive()) 
			return;
		
		boolean shouldSpawn = !(techFast || rc.checkResearchProgress(Upgrade.NUKE)>320 || Clock.getRoundNum()>1700) && 
				rc.senseNearbyGameObjects(Robot.class, curLoc, 1000000, myTeam).length*5 < rc.getTeamPower();
		
		if(shouldSpawn) {
			spawnRandomly();
			int i = 0;
			while(i<assign.length&&assign[i]!=0) i++;
			MapLocation ml;
			if(i<assign.length) {
				assign[i] = 1;
				ml = nearestEncampments[i];
			} else if(!sentInitialSoldier) {
				sentInitialSoldier = true;
				ml = enemyHQLoc;
			} else
				ml = null;
			msg.write(1, ml);
		} else {
			if(!rc.hasUpgrade(Upgrade.DEFUSION) && allIn)
				rc.researchUpgrade(Upgrade.DEFUSION);
			else if(!rc.hasUpgrade(Upgrade.VISION))
				rc.researchUpgrade(Upgrade.VISION);
			else
				rc.researchUpgrade(Upgrade.NUKE);
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
			r+=9;
			enc = rc.senseEncampmentSquares(curLoc, r, Team.NEUTRAL);
			if(enc.length > 5 || r > 99)
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
