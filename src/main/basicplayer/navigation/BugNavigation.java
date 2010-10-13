package basicplayer.navigation;

import basicplayer.BasePlayer;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;
import battlecode.common.RobotController;

public class BugNavigation extends Navigation {
	MapLocation dest;
	Direction lastDir;
	int turns;

	public BugNavigation(BasePlayer bp) {
		super(bp);
		lastDir=myRC.getDirection();
		dest=myRC.getLocation();
	}
	
	public void setDirectionAndMoveForward(Direction d) {
		lastDir=d;
		super.setDirectionAndMoveForward(d);
	}

	public void setDirectionAndMoveBackward(Direction d) {
		lastDir=d;
		super.setDirectionAndMoveBackward(d);
	}

	public void setDirectionAndMoveASAP(Direction d) {
		lastDir=d;
		super.setDirectionAndMoveASAP(d);
	}

	public void setDirectionAndMoveASAPPreferFwd(Direction d) {
		lastDir=d;
		super.setDirectionAndMoveASAPPreferFwd(d);
	}
	
	public void setLocation(MapLocation loc){

		MapLocation myLoc=myRC.getLocation();
		// Is this the right condition?	 -Dan
		if(!equalOrAdjacent(myLoc.directionTo(loc),myLoc.directionTo(dest))){
			rotationDirection = 0;
		}
		dest = loc;
	}
	
	/**
	 * Resets the rotation direction to 0, should be used
	 * only when fleeing where getting away from the enemy
	 * is more important than bugging around your own units
	 * or something like that
	 * Don't use this; it's only for the crippled
	 * player's retreat code.
	 */

	public void resetStoredData() { 
		rotationDirection = 0; 
	}
	
	protected int rotationDirection;
	protected MapLocation bugStartLoc;
	protected static final int RIGHT = 1;
	protected static final int LEFT = 2;	
	
	public static Direction [] directionsToward(MapLocation start, MapLocation loc) {
		/**
		 * Generally the direction that we need to move is a combination
		 * of two of the eight Directions.  So it makes sense to not
		 * bug if we can go in either of the two directions.  We prefer
		 * the one that would be chosen by directionToward.
		 */
		int dx = loc.getX()-start.getX();
		int dy = loc.getY()-start.getY();
		if(dx>=0) {
			if(dy>=0) {
				if(dx>=dy) {
					if(12*dx>=29*dy) {
						return new Direction []{Direction.EAST,Direction.SOUTH_EAST};
					}
					else {
						return new Direction []{Direction.SOUTH_EAST,Direction.EAST};
					}
				}
				else {
					if(12*dy>=29*dx) {
						return new Direction []{Direction.SOUTH,Direction.SOUTH_EAST};
					}
					else {
						return new Direction []{Direction.SOUTH_EAST,Direction.SOUTH};
					}
				}
			}
			else {
				if(dx>=-dy) {
					if(12*dx>=-29*dy) {
						return new Direction []{Direction.EAST,Direction.NORTH_EAST};
					}
					else {
						return new Direction []{Direction.NORTH_EAST,Direction.EAST};
					}
				}
				else {
					if(-12*dy>=29*dx) {
						return new Direction []{Direction.NORTH,Direction.NORTH_EAST};
					}
					else {
						return new Direction []{Direction.NORTH_EAST,Direction.NORTH};
					}
				}
			}
		}
		else {
			if(dy>=0) {
				if(-dx>=dy) {
					if(-12*dx>=29*dy) {
						return new Direction []{Direction.WEST,Direction.SOUTH_WEST};
					}
					else {
						return new Direction []{Direction.SOUTH_WEST,Direction.WEST};
					}
				}
				else {
					if(12*dy>=-29*dx) {
						return new Direction []{Direction.SOUTH,Direction.SOUTH_WEST};
					}
					else {
						return new Direction []{Direction.SOUTH_WEST,Direction.SOUTH};
					}
				}
			}
			else {
				if(dx<=dy) {
					if(-12*dx>=-29*dy) {
						return new Direction []{Direction.WEST,Direction.NORTH_WEST};
					}
					else {
						return new Direction []{Direction.NORTH_WEST,Direction.WEST};
					}
				}
				else {
					if(-12*dy>=-29*dx) {
						return new Direction []{Direction.NORTH,Direction.NORTH_WEST};
					}
					else {
						return new Direction []{Direction.NORTH_WEST,Direction.NORTH};
					}
				}
			}
		}
	}
	
	public Direction getDirectionToMove() {
		/*
		 * This should never be the case!  Print a debug message
		 * for testing purposes, but we should comment out the
		 * entire if statement for the competition.
		 */
		if (myRC.isMovementActive()) {
			BasePlayer.debug_println("Trying to move while active!");
			return null;
		}
		
		RobotController myRC=this.myRC;
		MapLocation myLoc=player.myLoc;

		Direction [] dirs=directionsToward(myLoc,dest);
		Direction dir=dirs[0];
		Direction secondaryDir=dirs[1];
		Direction newDir;

		// directionsToward doesn't return NONE anymore
		//if (dir.equals(Direction.NONE)) {
			// Right not this sometimes gets printed when all of our
			// opponent's archons are dead because some goals try to
			// move toward the nearest archon.
//			DebugMethods.debug_println("Trying to move to the square we're on");
		//	return;
		//}

		if(rotationDirection==LEFT) {
			// Check if the obstacle we were bugging around has
			// disappeared.	 If we are bugging left then it should
			// be on the right or right and behind.
			if(myRC.canMove(lastDir.opposite().rotateLeft())&&myRC.canMove(lastDir.rotateRight().rotateRight())) {
				rotationDirection=0;
			}
			// Also check if we are bugging around the outside of
			// the map and going away from our destination.
			// Check in front and to the side instead of just
			// in front because snipers can't see directly
			// to the side.  (It takes fewer bytecodes anyway.)
			else  {
				Direction myDir=myRC.getDirection();
				if((myRC.senseTerrainTile(myLoc.add(myDir.rotateRight())).getType()==TerrainTile.TerrainType.OFF_MAP||
					myRC.senseTerrainTile(myLoc.add(myDir.rotateLeft())).getType()==TerrainTile.TerrainType.OFF_MAP)&&
				   myLoc.distanceSquaredTo(dest)<myLoc.add(lastDir).distanceSquaredTo(dest)) {
					rotationDirection=0;
				}
			}
		}
		else if(rotationDirection==RIGHT) {
			if(myRC.canMove(lastDir.opposite().rotateRight())&&myRC.canMove(lastDir.rotateLeft().rotateLeft())) {
				rotationDirection=0;
			}
			else  {
				Direction myDir=myRC.getDirection();
				if((myRC.senseTerrainTile(myLoc.add(myDir.rotateRight())).getType()==TerrainTile.TerrainType.OFF_MAP||
					myRC.senseTerrainTile(myLoc.add(myDir.rotateLeft())).getType()==TerrainTile.TerrainType.OFF_MAP)&&
				   myLoc.distanceSquaredTo(dest)<myLoc.add(lastDir).distanceSquaredTo(dest)) {
					rotationDirection=0;
				}
			}
		}

		if(rotationDirection==0) {
			if(myRC.canMove(dir)) {
				return dir;
			}
			if(myRC.canMove(secondaryDir)) {
				return secondaryDir;
			}
			turns=0;
			Direction leftTurn=dir.rotateLeft();
			while(!myRC.canMove(leftTurn)) {
				leftTurn=leftTurn.rotateLeft();
				if(leftTurn==dir) return null;
			}
			Direction rightTurn=dir.rotateRight();
			while(!myRC.canMove(rightTurn)) {
				rightTurn=rightTurn.rotateRight();
				if(rightTurn==leftTurn) break;
			}
			if(myLoc.add(leftTurn).distanceSquaredTo(dest)<=
			   myLoc.add(rightTurn).distanceSquaredTo(dest)) {
				rotationDirection=LEFT;
				bugStartLoc=myLoc;
				return leftTurn ;
			}
			else {
				rotationDirection=RIGHT;
				bugStartLoc=myLoc;
				return rightTurn;
			}
		}
		else if(turns>0||myLoc.distanceSquaredTo(dest)<=bugStartLoc.distanceSquaredTo(dest)) {
			if(myRC.canMove(dir)) {
				rotationDirection=0;
				return dir;
			}
			else {
				if(myRC.canMove(secondaryDir)) {
					rotationDirection=0;
					return secondaryDir;
				}
			}
		}
		if (rotationDirection == RIGHT) { // have been turning right to avoid walls
			newDir=lastDir.rotateLeft().rotateLeft();
			Direction stop=newDir;
			turns+=2;
			while(!myRC.canMove(newDir)) {
				newDir=newDir.rotateRight();
				turns--;
				if(stop==newDir) {
					rotationDirection=0;
					return null;
				}
			}
			return newDir;
		} 
		else {
			newDir=lastDir.rotateRight().rotateRight();
			Direction stop=newDir;
			turns+=2;
			while(!myRC.canMove(newDir)) {
				newDir=newDir.rotateLeft();
				turns--;
				if(stop==newDir) {
					rotationDirection=0;
					return null;
				}
			}
			return newDir;
		}
	}
}
