package hardplayer.navigation;

import hardplayer.Static;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class QueuedActionMoveForward extends Static implements QueuedAction {
    
	public QueuedActionMoveForward() {
    }

    public boolean doAction() {
		try {
			if(myRC.canMove(myRC.getDirection())) {
				myRC.moveForward();
				return true;
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
		return false;
    }
}
