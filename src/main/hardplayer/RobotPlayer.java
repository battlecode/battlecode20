package hardplayer;

import hardplayer.BasePlayer;
import battlecode.common.RobotController;

public class RobotPlayer implements Runnable {
	private final RobotController myRC;
	
	public RobotPlayer(RobotController rc) {
	   myRC = rc;
	}
	
	public void run() {
		while(true) {
			try {
				Strategy ourStrat;
				if(myRC.getTeam()==battlecode.common.Team.A)
					ourStrat = new MainStrategy();
				else
					ourStrat = new MainStrategy();
				ourStrat.execute(myRC);
			}
			catch (Exception ex) {
				BasePlayer.debug_stackTrace(ex);
			}
		}
	}
}