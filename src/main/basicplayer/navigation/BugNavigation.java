package basicplayer.navigation;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.TerrainTile;
import battlecode.common.RobotController;

public class BugNavigation extends Navigation {
	MapLocation dest;
	Direction lastDir;

	public BugNavigation() {
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
			//myRC.setIndicatorString(1,"reset because loc changed "+Clock.getRoundNum());
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
		int dx = loc.x-start.x;
		int dy = loc.y-start.y;
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
			debug_println("Trying to move while active!");
			debug_stackTrace();
			return null;
		}
		
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
				//myRC.setIndicatorString(1,"reset because obstacle disappeared left"+Clock.getRoundNum());
				rotationDirection=0;
			}
			// Also check if we are bugging around the outside of
			// the map and going away from our destination.
			// Check in front and to the side instead of just
			// in front because snipers can't see directly
			// to the side.  (It takes fewer bytecodes anyway.)
			else  {
				Direction myDir=myRC.getDirection();
				if((myRC.senseTerrainTile(myLoc.add(myDir.rotateRight()))==TerrainTile.OFF_MAP||
					myRC.senseTerrainTile(myLoc.add(myDir.rotateLeft()))==TerrainTile.OFF_MAP)&&
				   myLoc.distanceSquaredTo(dest)<myLoc.add(lastDir).distanceSquaredTo(dest)) {
					rotationDirection=0;
				}
			}
		}
		else if(rotationDirection==RIGHT) {
			if(myRC.canMove(lastDir.opposite().rotateRight())&&myRC.canMove(lastDir.rotateLeft().rotateLeft())) {
				//myRC.setIndicatorString(1,"reset because obstacle disappeared right"+Clock.getRoundNum());
				rotationDirection=0;
			}
			else  {
				Direction myDir=myRC.getDirection();
				if((myRC.senseTerrainTile(myLoc.add(myDir.rotateRight()))==TerrainTile.OFF_MAP||
					myRC.senseTerrainTile(myLoc.add(myDir.rotateLeft()))==TerrainTile.OFF_MAP)&&
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
		else if(myLoc.distanceSquaredTo(dest)<=bugStartLoc.distanceSquaredTo(dest)) {
			if(myRC.canMove(dir)) {
				//myRC.setIndicatorString(1,"reset because closer "+Clock.getRoundNum());
				rotationDirection=0;
				return dir;
			}
			else {
				if(myRC.canMove(secondaryDir)) {
					//myRC.setIndicatorString(1,"reset because closer "+Clock.getRoundNum());
					rotationDirection=0;
					return secondaryDir;
				}
			}
		}
		if (rotationDirection == RIGHT) { // have been turning right to avoid walls
			newDir=lastDir.rotateLeft().rotateLeft();
			Direction stop=newDir;
			while(!myRC.canMove(newDir)) {
				newDir=newDir.rotateRight();
				if(stop==newDir) {
					//myRC.setIndicatorString(1,"reset because can't move "+Clock.getRoundNum());
					mySender.sendCrowded();
					rotationDirection=0;
					return null;
				}
			}
			return newDir;
		} 
		else {
			newDir=lastDir.rotateRight().rotateRight();
			Direction stop=newDir;
			while(!myRC.canMove(newDir)) {
				newDir=newDir.rotateLeft();
				if(stop==newDir) {
					//myRC.setIndicatorString(1,"reset because can't move "+Clock.getRoundNum());
					mySender.sendCrowded();
					rotationDirection=0;
					return null;
				}
			}
			return newDir;
		}
	}
}
