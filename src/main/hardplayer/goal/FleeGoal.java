package hardplayer.goal;

import battlecode.common.*;

import hardplayer.ArchonPlayer;
import hardplayer.Static;

public class FleeGoal extends Static implements Goal {

	public int maxPriority() {
		return FLEE;
	}

	static long xsum, ysum, n;
	static public final long [] fear = new long [] { 0, 6, 3, 6, 6, 0 };

	public static final double RETREAT_STAYPUT_VALUE = -.127;
	public static final double RETREAT_EDGE_PENALTY = .06;
	public static final double RETREAT_CORNER_PENALTY = .176;
	public static final double RETREAT_WAIT_FOR_ARCHON_PENALTY = .172;

	public static final double [] dX = new double [] { 0, 1, 1, 1, 0, -1, -1, -1, 0, 0 };
	public static final double [] dY = new double [] { -1, -1, 0, 1, 1, 1, 0, -1, 0, 0 };

	public static final int AVOID_EDGE_TIME = 30;

	static int [] retreatForbiddenTimeout = new int [8];

	public int priority() {
		n = 0;
		xsum = 0;
		ysum = 0;
		int i;
		long w;
		RobotInfo info;
		for(i=enemies.size;i>=0;i--) {
			info = enemyInfos[i];
			if(myLoc.distanceSquaredTo(info.location)>18)
				continue;
			w=fear[info.type.ordinal()];
			n+=w;
			xsum+=w*info.location.x;
			ysum+=w*info.location.y;
		}
		myRC.setIndicatorString(2,Long.toString(n));
		if(n>=6)
			return FLEE;
		else
			return 0;
	}

	public void execute() {
		try {
			int i;
			double dx = -(double)xsum/n + myLoc.getX();
			double dy = -(double)ysum/n + myLoc.getY();
			double dxn, dyn;
			boolean [] edge = new boolean [9]; // we don't actually use the odd ones
			boolean [] forbidden = new boolean [9];
			Direction dir;
			Direction bestDir = null;
			double bestVal = RETREAT_STAYPUT_VALUE;
			double val;
			double dist = Math.sqrt(dx*dx+dy*dy);
			int t=Clock.getRoundNum();
			int numEdges=0;
			Robot r;
			RobotInfo info;
			for(i=6;i>=0;i-=2) {
				if(myRC.senseTerrainTile(myLoc.add(directions[i],6)).getType()==TerrainTile.OFF_MAP) {
					edge[i]=true;
					numEdges++;
				}
				if(t<retreatForbiddenTimeout[i]) {}
					//forbidden[i]=true;
			}
			boolean twoEdges=(numEdges>=2);
			edge[8]=edge[0];
			forbidden[8]=forbidden[0];
			for(i=6;i>=0;i-=2) {
				if(forbidden[i]||(twoEdges&&edge[i])) continue;
				dir=directions[i];
				if(myRC.canMove(dir)) {
					dxn=dx+dX[i];
					dyn=dy+dY[i];
					val=(Math.sqrt(dxn*dxn+dyn*dyn)-dist)/6.;
				}
				else {
					r=(Robot)myRC.senseObjectAtLocation(myLoc.add(dir),RobotLevel.IN_AIR);
					if(r==null) continue;
					info=myRC.senseRobotInfo((Robot)r);
					if(info.team!=myTeam) continue;
					dxn=dx+dX[i];
					dyn=dy+dY[i];
					val=(Math.sqrt(dxn*dxn+dyn*dyn)-dist)/6.-RETREAT_WAIT_FOR_ARCHON_PENALTY;
				}
				if(val>bestVal) {
					bestDir=dir;
					bestVal=val;
				}
			}
			for(i=7;i>=0;i-=2) {
				if(forbidden[i-1]||forbidden[i+1]||(edge[i-1]&&edge[i+1])) continue;
				dir=directions[i];
				if(myRC.canMove(dir)) {
					dxn=dx+dX[i];
					dyn=dy+dY[i];
					val=(Math.sqrt(dxn*dxn+dyn*dyn)-dist)/8.;
				}
				else {
					r=(Robot)myRC.senseObjectAtLocation(myLoc.add(dir),RobotLevel.IN_AIR);
					if(r==null) continue;
					info=myRC.senseRobotInfo(r);
					if(info.team!=myTeam) continue;
					dxn=dx+dX[i];
					dyn=dy+dY[i];
					val=(Math.sqrt(dxn*dxn+dyn*dyn)-dist)/8.-RETREAT_WAIT_FOR_ARCHON_PENALTY;
				}
				if(val>bestVal) {
					bestDir=dir;
					bestVal=val;
				}
			}
			if(bestDir!=null) {
				if(myRC.canMove(bestDir)) {
					myNav.setDirectionAndMoveASAP(bestDir);
					int ord;
					if(bestDir.isDiagonal()) {
						ord=bestDir.opposite().rotateLeft().ordinal();
						if(edge[ord])
							retreatForbiddenTimeout[ord]=t+AVOID_EDGE_TIME;
						ord=bestDir.opposite().rotateRight().ordinal();
						if(edge[ord])
							retreatForbiddenTimeout[ord]=t+AVOID_EDGE_TIME;
					}
					else {
						ord=bestDir.opposite().ordinal();
						if(edge[ord])
							retreatForbiddenTimeout[ord]=t+AVOID_EDGE_TIME;
					}
				}
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}

	}
}
