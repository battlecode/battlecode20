package hardplayer.navigation;

import hardplayer.Static;
import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public class QueuedActionMoveBackward extends Static implements QueuedAction {

    public QueuedActionMoveBackward() {
    }
    
	public boolean doAction() {
		try {
			if(myRC.canMove(myRC.getDirection().opposite())) {
				myRC.moveBackward();
				return true;
			}
		} catch(Exception e) {
			debug_stackTrace(e);
		}
		return false;
    }
}
