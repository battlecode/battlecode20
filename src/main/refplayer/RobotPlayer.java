package refplayer;

import refplayer.BasePlayer;
import battlecode.common.RobotController;

public class RobotPlayer implements Runnable {
	private final RobotController myRC;
	
	public RobotPlayer(RobotController rc) {
	   myRC = rc;
	}
	
	public void run() {
		while(true) {
			try {
				BasePlayer player;
				switch (myRC.getRobotType()) {
				case ARCHON:
					player = new ArchonPlayer(myRC);
					break;
				case WOUT:
					player = new WoutPlayer(myRC);
					break;
				case AURA:
					player = new AuraPlayer(myRC);
					break;
				case SOLDIER:
					player = new SoldierPlayer(myRC);
					break;
				default:
					BasePlayer.debug_println("I don't know what kind of robot I am!");
					myRC.suicide();
					player = null;
				}
				player.run();
			}
			catch (Exception ex) {
				BasePlayer.debug_stackTrace(ex);
			}
		}
	}
}