package hardplayer;

import battlecode.common.RobotController;
import battlecode.common.Team;

public class RobotPlayer implements Runnable {

	RobotController myRC;

	public RobotPlayer(RobotController rc) {
		myRC = rc;
	}

	public void run() {
		while(true) {
			Static.init(myRC);
			try {
				switch(myRC.getTeam()) {
					case A:
						new MainStrategy().execute(myRC);
					case B:
						new MainStrategy().execute(myRC);
				}
			} catch(Exception e) {
				Static.debug_stackTrace(e);
			}
		}
	}

}
