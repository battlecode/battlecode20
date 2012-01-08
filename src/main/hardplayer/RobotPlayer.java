package hardplayer;

import battlecode.common.RobotController;
import battlecode.common.Team;

public class RobotPlayer {

	public static void run(RobotController myRC) {
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
