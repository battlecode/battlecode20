package mediumbot;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Team;
import battlecode.common.Upgrade;

public class HQPlayer extends BasePlayer {
	final boolean[][] map = new boolean[width+2][height+2];
	final int ffMaxDist = 5;
	final int ffMaxBuffer = 100;
	/** x, y */
	final int[][] dist = new int[width+2][height+2];
	/** distance, buffer position, x or y */
	final int[][][] distQueue = new int[ffMaxDist][1000][2];
	/** distance */
	final int[] distQueuePos = new int[ffMaxDist];
	int ffCurDist = 0;
	int ffCurPos = 0;
	int ffCurZ = 0;
	public HQPlayer(RobotController rc) {
		super(rc);
		floodfill();
	}
	public void run() throws GameActionException {
		if(!rc.isActive()) 
			return;
		
		boolean shouldSpawn = rc.senseNearbyGameObjects(Robot.class, curLoc, 1000000, myTeam).length*5 < rc.getTeamPower();
		
		
		if(shouldSpawn) {
			//spawnRandomly();
		}
		
		rc.researchUpgrade(Upgrade.NUKE);
		
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
	
	public void floodfill() {
		rc.setIndicatorString(1, "goo");
		for(int x=0; x<width; x++) {
			for(int y=0; y<height; y++) {
				map[x][y] = rc.senseMine(new MapLocation(x, y)) != Team.NEUTRAL;
			}
		}
		rc.setIndicatorString(1, "gay");
		int ymax = dist[0].length; int xmax = dist.length;
		for(int y=0; y<ymax; y++)
			dist[0][y] = 55555;
		for(int x=1; x<xmax; x++) {
			System.arraycopy(dist[0], 0, dist[x], 0, ymax);
		}
		for(int x=0; x<xmax; x++) {
			dist[x][0] = -5;
			dist[x][ymax-1] = -5;
		}
		for(int y=0; y<ymax; y++) {
			dist[0][y] = -5;
			dist[xmax-1][y] = -5;
		}
		dist[curLoc.x+1][curLoc.y+1] = 0;
		distQueue[0][0][0] = curLoc.x+1;
		distQueue[0][0][1] = curLoc.y+1;
		distQueuePos[0]++;
		while(true) {
			rc.setIndicatorString(1, ffCurZ+" "+ffCurDist+" "+ffCurPos);
			flood(distQueue[ffCurDist][ffCurPos][0], distQueue[ffCurDist][ffCurPos][1]);
			ffCurPos++;
			while(ffCurDist<ffMaxDist-1 && (ffCurPos==distQueuePos[ffCurDist] || ffCurPos>ffMaxBuffer)) {
				ffCurDist++;
				ffCurPos = 0;
			}
			if(ffCurDist==ffMaxDist-1)
				break;
		}
	}
	
	public void flood(int x, int y) {
		System.out.println(x+ " "+y+" "+dist[x][y]);
		for(int dx=-1; dx<=1; dx++) for(int dy=-1; dy<=1; dy++) {
			if(dx==0&&dy==0) continue;
			int d = dist[x][y]+(map[x-1][y-1]?0:1);
			if(d<dist[x+dx][y+dy]) {
				dist[x+dx][y+dy] = d;
				distQueue[d][distQueuePos[d]][0] = x+dx;
				distQueue[d][distQueuePos[d]++][1] = y+dy;
			}
		}
	}
}
