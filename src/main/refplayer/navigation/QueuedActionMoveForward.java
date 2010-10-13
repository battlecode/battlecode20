package refplayer.navigation;

import refplayer.BasePlayer;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class QueuedActionMoveForward implements QueuedAction {
    RobotController myRC;
    public QueuedActionMoveForward(RobotController rc) {
		myRC=rc;
    }
    public boolean doAction() {
		try {
			if(myRC.canMove(myRC.getDirection())) {
				myRC.moveForward();
				return true;
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
		return false;
    }
}