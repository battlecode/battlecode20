package refplayer.navigation;

import refplayer.BasePlayer;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class QueuedActionMoveBackward implements QueuedAction {
    RobotController myRC;
    public QueuedActionMoveBackward(RobotController rc) {
		myRC=rc;
    }
    public boolean doAction() {
		try {
			if(myRC.canMove(myRC.getDirection().opposite())) {
				myRC.moveBackward();
				return true;
			}
		} catch(Exception e) {
			BasePlayer.debug_stackTrace(e);
		}
		return false;
    }
}