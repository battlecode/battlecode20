package hardplayer;

import battlecode.common.RobotController;

public class CampingStrategy extends Strategy {

	public CampingStrategy() {}
	
	public void execute(RobotController myRC) {
		BasePlayer player;
		switch (myRC.getRobotType()) {
		case ARCHON:
			player = new CampingArchonPlayer(myRC);
			break;
		case CHAINER:
			player = new ChainerPlayer(myRC);
			break;
		case SOLDIER:
			player = new SoldierPlayer(myRC);
			break;
		case TURRET:
			player = new CampingTurretPlayer(myRC);
			break;
		case WOUT:
			player = new CampingWoutPlayer(myRC);
			break;
		case COMM:
			player = new CommPlayer(myRC);
			break;
		case AURA:
			player = new AuraPlayer(myRC);
			break;
		case TELEPORTER:
			player = new TeleporterPlayer(myRC);
			break;
		default:
			BasePlayer.debug_println("I don't know what kind of robot I am!");
			player = null;
			myRC.suicide();
		}
		player.run();
	}
	
}