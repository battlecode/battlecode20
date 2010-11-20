package hardplayer.navigation;

import hardplayer.Static;
import battlecode.common.GameActionException;
import battlecode.common.MovementController;
import battlecode.common.RobotController;

public class QueuedActionMoveForward extends Static implements QueuedAction {
    
	public QueuedActionMoveForward() {
    }

    public boolean doAction() {
		try {
			if(motor.canMove(myRC.getDirection())) {
				motor.moveForward();
				return true;
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
		return false;
    }
}
