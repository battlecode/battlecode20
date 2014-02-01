package yourmom;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public class RobotPlayer {
	public static void run(RobotController myRC) {
		BaseRobot br = null;

		final int rseed = myRC.getRobot().getID();
		Util.randInit(rseed, rseed * Clock.getRoundNum());

		try {
			switch (myRC.getType()) {
			case HQ:
				br = new HQRobot(myRC);
				break;
			case SOLDIER:
				br = new SoldierRobot(myRC);
				break;
			case PASTR:
				br = new PastrRobot(myRC);
				break;
			case NOISETOWER:
				br = new NoiseTowerRobot(myRC);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				br.loop();
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
}
