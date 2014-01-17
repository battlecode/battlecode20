package yourmom;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class NavigationSystem {
	private final BaseRobot br;
	private final RobotController rc;
	private final MapCacheSystem mapCache; 
	public final TangentBug tangentBug; // public purely for optimization's sake
	private final NormalBug normalBug;
	private final MapLocation zeroLoc;
	private NavigationMode mode;
	private MapLocation destination;
	private int movesOnSameTarget;
	private int expectedMovesToReachTarget;
	private int bugTurnsBlocked;
	public NavigationSystem(BaseRobot br) {
		this.br = br;
		this.rc = br.rc;
		mapCache = br.mc;
		tangentBug = new TangentBug(mapCache.isWall);
		normalBug = new NormalBug();
		zeroLoc = new MapLocation(0,0);
		bugTurnsBlocked = 0;
		mode = NavigationMode.RANDOM;
	}
	
	/** Resets the navigator, clearing it of any state. */
	public void reset() {
		if(mode==NavigationMode.TANGENT_BUG) {
			tangentBug.reset();
		} else if(mode==NavigationMode.BUG) {
			normalBug.reset();
			bugTurnsBlocked=0;
		}
	}
	
	public NavigationMode getNavigationMode() {
		return mode;
	}
	/** Sets the navigation mode. <br>
	 * This should only be called when necessary, as changing the mode
	 * resets the internal state of the navigator.
	 */
	public void setNavigationMode(NavigationMode mode) {
		if(this.mode == mode) 
			return;
		this.mode = mode;
		reset();
	}
	
	public MapLocation getDestination() {
		return destination;
	}
	/** Sets the destination that the system is navigating towards. <br>
	 * Whenever the destination changes, the internal state of the 
	 * navigator is reset. 
	 */
	public void setDestination(MapLocation destination) {
		if(destination==null) {
			this.destination = null;
			return;
		}
		if(destination.equals(this.destination)) 
			return;
		movesOnSameTarget = 0;
		expectedMovesToReachTarget = (int)(Math.sqrt(br.curLoc.distanceSquaredTo(destination)) *
				TangentBug.MAP_UGLINESS_WEIGHT)+10;
		this.destination = destination;
		normalBug.setTarget(mapCache.worldToCacheX(destination.x), 
				mapCache.worldToCacheY(destination.y));
		tangentBug.setTarget(mapCache.worldToCacheX(destination.x), 
				mapCache.worldToCacheY(destination.y));
		reset();
	}
	/** If we're in tangent bug mode, returns how many turns we have called prepare
	 * with the same source position. Otherwise returns 0.
	 */
	public int getTurnsPrepared() {
		if(mode==NavigationMode.TANGENT_BUG) {
			return tangentBug.getTurnsPrepared();
		}
		return 0;
	}
	/** If we're in bug mode, returns true if bug is tracing a wall. Otherwise returns false. */
	public boolean isBugTracing() {
		if(mode==NavigationMode.BUG)
			return normalBug.isTracing();
		return false;
	}
	/** Does precomputation to allow the navigationToDestination() to
	 * return a more accurate result. Only matters for tangent bug right now.
	 * @see NavigationSystem#navigateToDestination()
	 */
	public void prepare() {
		if(mode==NavigationMode.TANGENT_BUG) {
			if(mapCache.edgeXMin!=0) tangentBug.edgeXMin = mapCache.edgeXMin;
			if(mapCache.edgeXMax!=0) tangentBug.edgeXMax = mapCache.edgeXMax;
			if(mapCache.edgeYMin!=0) tangentBug.edgeYMin = mapCache.edgeYMin;
			if(mapCache.edgeYMax!=0) tangentBug.edgeYMax = mapCache.edgeYMax;

			tangentBug.prepare(
					mapCache.worldToCacheX(br.curLoc.x), 
					mapCache.worldToCacheY(br.curLoc.y));
		} 
	}
	/** Returns direction to go next in order to reach the destination. <br>
	 * In the case of tangent bug, uses up all 
	 * precomputation from the prepare() calls. <br>
	 * May return null for no movement. <br>
	 * This method only considers walls as blocked movement, not units so it
	 * may return a direction that moves towards another robot. <br>
	 * @return null, or one of the 8 valid directions.
	 */
	public Direction navigateToDestination() {
		if(destination==null || br.curLoc.equals(destination)) 
			return null; 
		
		Direction dir = Direction.NONE;
		
		switch(mode) {
		case RANDOM:
			dir = navigateRandomly(destination);
			break;
		case GREEDY:
			if(movesOnSameTarget > 2 * expectedMovesToReachTarget) 
				dir = navigateRandomly(destination);
			else
				dir = navigateGreedy(destination);
			break;
		case BUG:
			dir = navigateBug();
//			if(movesOnSameTarget % (3*expectedMovesToReachTarget) == 0) {
//				normalBug.reset();
//			}
			break;
		case TANGENT_BUG:
			dir = navigateTangentBug();
			if(movesOnSameTarget % expectedMovesToReachTarget == 0) {
				int n = movesOnSameTarget / expectedMovesToReachTarget;
				if(n>=2) {
					tangentBug.resetWallTrace(Math.min(
							TangentBug.DEFAULT_MIN_PREP_TURNS*n, 50), 0.4);
				}
			}
			break;
		}
		
		if(dir==null || dir==Direction.NONE || dir==Direction.OMNI) 
			return null; 
		
		movesOnSameTarget++;
		return dir;
	}
	/** Given a direction, randomly perturbs it to a direction that the 
	 * robot can move in. Will return null if no direction within the
	 * hemicircle of the given direction is movable.
	 */
	public Direction wiggleToMovableDirection(Direction dir) {
		if(dir==null || dir==Direction.NONE || dir==Direction.OMNI) 
			return null;
		if(rc.canMove(dir)) 
			return dir;
		if(mode==NavigationMode.BUG)
			return wiggleToMovableDirectionLimited(dir);
		Direction d1, d2;
		if(Util.randDouble()<0.5) {
			d1 = dir.rotateLeft();
			if(rc.canMove(d1))
				return d1;
			d2 = dir.rotateRight();
			if(rc.canMove(d2))
				return d2;
			d1 = d1.rotateLeft();
			if(rc.canMove(d1))
				return d1;
			d2 = d2.rotateRight();
			if(rc.canMove(d2))
				return d2;
		} else {
			d2 = dir.rotateRight();
			if(rc.canMove(d2))
				return d2;
			d1 = dir.rotateLeft();
			if(rc.canMove(d1))
				return d1;
			d2 = d2.rotateRight();
			if(rc.canMove(d2))
				return d2;
			d1 = d1.rotateLeft();
			if(rc.canMove(d1))
				return d1;
		}
		return null;
	}
	/** Same as above wiggle code, except when moving diagonally, can't wiggle diagonally. <br>
	 * i.e. When trying to move NW, does not wiggle SW or NE.
	 */
	public Direction wiggleToMovableDirectionLimited(Direction dir) {
		if(dir==null || dir==Direction.NONE || dir==Direction.OMNI) 
			return null;
		if(rc.canMove(dir)) 
			return dir;
		Direction d1, d2;
		if(Util.randDouble()<0.5) {
			d1 = dir.rotateLeft();
			if(rc.canMove(d1))
				return d1;
			d2 = dir.rotateRight();
			if(rc.canMove(d2))
				return d2;
			if(dir.isDiagonal()) {
				d1 = d1.rotateLeft();
				if(rc.canMove(d1))
					return d1;
				d2 = d2.rotateRight();
				if(rc.canMove(d2))
					return d2;
			}
		} else {
			d2 = dir.rotateRight();
			if(rc.canMove(d2))
				return d2;
			d1 = dir.rotateLeft();
			if(rc.canMove(d1))
				return d1;
			if(dir.isDiagonal()) {
				d2 = d2.rotateRight();
				if(rc.canMove(d2))
					return d2;
				d1 = d1.rotateLeft();
				if(rc.canMove(d1))
					return d1;
			}
		}
		return null;
	}
	
	private Direction dxdyToDirection(int dx, int dy) {
		return zeroLoc.directionTo(zeroLoc.add(dx, dy));
	}
	/** This is private because it needs the state of the navigator to work. */
	private Direction navigateBug() {
		if(mapCache.edgeXMin!=0) normalBug.edgeXMin = mapCache.edgeXMin;
		if(mapCache.edgeXMax!=0) normalBug.edgeXMax = mapCache.edgeXMax;
		if(mapCache.edgeYMin!=0) normalBug.edgeYMin = mapCache.edgeYMin;
		if(mapCache.edgeYMax!=0) normalBug.edgeYMax = mapCache.edgeYMax;
		boolean movable[] = new boolean[8];
		for(int i=0; i<8; i++) {
			Direction dir = Constants.directions[i];
			TerrainTile tt = rc.senseTerrainTile(
					br.curLoc.add(dir));
			if(bugTurnsBlocked < 3)
				movable[i] = (tt==null) 
					? rc.canMove(dir) 
					: (tt==TerrainTile.NORMAL||tt==TerrainTile.ROAD);
			else
				movable[i] = rc.canMove(dir);
		}
		
		int[] d = normalBug.computeMove(
				mapCache.worldToCacheX(br.curLoc.x), 
				mapCache.worldToCacheY(br.curLoc.y),
				movable);
		if(d==null) return Direction.NONE;
		Direction ret = dxdyToDirection(d[0], d[1]);
		if(!rc.canMove(ret)) bugTurnsBlocked++;
		else bugTurnsBlocked=0;
		return ret;
	}
	/** This is private because it needs the state of the navigator to work. */
	private Direction navigateTangentBug() {
		int[] d = tangentBug.computeMove(
				mapCache.worldToCacheX(br.curLoc.x), 
				mapCache.worldToCacheY(br.curLoc.y));
		if(d==null) return Direction.NONE;
		return dxdyToDirection(d[0], d[1]);
	}
	/** Goes in a completely random direction, with no bias towards the 
	 * destination. May be useful for getting unstuck.
	 * This method only considers walls as blocked movement, not units so it
	 * may return a direction that moves towards another robot.
	 */
	public Direction navigateCompletelyRandomly() {
		int rand = (int)(Util.randDouble()*16);
		int a = rand/2;
		int b = rand%2*2+3;
		for(int i=0; i<8; i++) {
			Direction dir = Constants.directions[(a+i*b)%8];
			if(!mapCache.isWall(br.curLoc.add(dir)))
				return dir;
		}
		return Direction.NONE;
	}
	/** With 1/4 probability, reset heading towards destination.
	 * Otherwise, randomly perturb current direction by up to 90 degrees. <br>
	 * This method only considers walls as blocked movement, not units so it
	 * may return a direction that moves towards another robot. <br>
	 */
	public Direction navigateRandomly(MapLocation destination) {
		double d = Util.randDouble();
		if(d*1000-(int)(d*1000)<0.25) return br.curLoc.directionTo(destination);
		d=d*2-1;
		d = d*d*Math.signum(d);
		Direction dir = br.curDir;
		if(d<0) {
			do {
				d++;
				dir = dir.rotateLeft();
			} while(d<0 || mapCache.isWall(br.curLoc.add(dir)));
		} else {
			do {
				d++;
				dir = dir.rotateRight();
			} while(d<0 || mapCache.isWall(br.curLoc.add(dir)));
		}
		return dir;
	}
	/** Goes directly towards the destination. Can easily get stuck. 
	 * This method only considers walls as blocked movement, not units so it
	 * may return a direction that moves towards another robot. <br>
	 */
	public Direction navigateGreedy(MapLocation destination) {
		Direction dir = br.curLoc.directionTo(destination);
		if(Util.randDouble()<0.5) {
			while(mapCache.isWall(br.curLoc.add(dir)))
				dir = dir.rotateLeft();
		} else {
			while(mapCache.isWall(br.curLoc.add(dir)))
				dir = dir.rotateRight();
		}
		return dir;
	}
	
	/** Returns a direction at random from the eight standard directions. */
	public static Direction getRandomDirection() {
		return Constants.directions[(int)(Util.randDouble()*8)];
	}
}
