package hardplayer.navigation;

import hardplayer.Static;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.MovementController;
import battlecode.common.RobotController;

public abstract class Navigation extends Static {

	public abstract Direction getDirectionToMove();

	public abstract void setLocation(MapLocation dest);
	
	public void moveToBackward(MapLocation dest) {
		setLocation(dest);
		Direction d=getDirectionToMove();
		if(d!=null)
			setDirectionAndMoveBackward(d);
	}
	
	public void moveToForward(MapLocation dest) {
		setLocation(dest);
		Direction d=getDirectionToMove();
		if(d!=null)
			setDirectionAndMoveForward(d);
	}

	public void moveToASAP(MapLocation dest) {
		setLocation(dest);
 		Direction d=getDirectionToMove();
		if(d!=null)
			setDirectionAndMoveASAP(d);
	}

	public void moveToASAPPreferFwd(MapLocation dest) {
		setLocation(dest);
 		Direction d=getDirectionToMove();
		if(d!=null)
			setDirectionAndMoveASAPPreferFwd(d);
	}
	
	
	/**
	 * Resets information stored by Bug, currently not
	 * really used by other types of navigation.
	 * Don't use this; it's only for the crippled
	 * player's retreat code.
	 */
	public void resetStoredData() {}
	
	/**
	 * If the robot is not correctly oriented, then the robot re-orients itself.
	 * Otherwise, the robot moves when there are no obstacles.
	 * @param dir The direction that the robots needs to go.
	 */

	public void setDirectionAndMoveForward(Direction dir) {
		try {
			if(myRC.getDirection()!=dir) {
				myRC.setDirection(dir);
				setQueued(new QueuedActionMoveForward());
			}
			else if(myRC.canMove(dir)) {
				myRC.moveForward();
			}
		} catch(GameActionException e) {
			debug_stackTrace(e);
		}
	}

	public void setDirectionAndMoveBackward(Direction dir) {
		try {
			if(myRC.getDirection()!=dir.opposite()) {
				myRC.setDirection(dir.opposite());
				setQueued(new QueuedActionMoveBackward());
			}
			else if(myRC.canMove(dir)) {
				myRC.moveBackward();
			}
		} catch(GameActionException e) {
			debug_stackTrace(e);
		}
	}

	// Same as setDirectionAndMove, except that it will move either
	// forward or backward if it can do so without turning.  Moves
	// backward otherwise, since only archons use it and archons
	// move backward by default
	public void setDirectionAndMoveASAP(Direction dir) {
		try {
			if(myRC.getDirection()==dir)
				myRC.moveForward();
			else if(myRC.getDirection()==dir.opposite()) {
				myRC.moveBackward();
			}
			else {
				myRC.setDirection(dir.opposite());
				setQueued(new QueuedActionMoveBackward());
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}

	public void setDirectionAndMoveASAPPreferFwd(Direction dir) {
		try {
			if(myRC.getDirection()==dir)
				myRC.moveForward();
			else if(myRC.getDirection()==dir.opposite()) {
				myRC.moveBackward();
			}
			else {
				myRC.setDirection(dir);
				setQueued(new QueuedActionMoveForward());
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
	}
	
	/**
	 * Tests whether two directions differ by at most 90 degrees.
	 * @param d1 First direction.
	 * @param d2 Second direction.
	 * @return Returns true if the two directions differ by at most 90 degrees,
	 * false otherwise.
	 */
	static public boolean equalOrAdjacent(Direction d1, Direction d2) {
		return ((d1.ordinal()-d2.ordinal()+9)%8)<=2;
	}
}
