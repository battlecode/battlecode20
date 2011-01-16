package hardplayer;

import battlecode.common.Chassis;
import battlecode.common.ComponentController;
import battlecode.common.ComponentType;
import battlecode.common.RobotController;

class MainStrategy extends Static implements Strategy {

	public void execute(RobotController myRC) {
		while(true) {
			try {
				ComponentController [] comp = myRC.components();
				chooseplayer:
				switch(myRC.getChassis()) {
				case BUILDING:
					for(ComponentController c : comp) {
						if(c.type()==ComponentType.RECYCLER) {
							new MinePlayer(myRC).run();
							break chooseplayer;
						}
					}
					myRC.suicide();
					break;
				case LIGHT:
					for(ComponentController c: comp) {
						if(c.type()==ComponentType.CONSTRUCTOR) {
							new ConstructorPlayer(myRC).run();
							break chooseplayer;
						}
					}
					new LarvaPlayer(myRC).run();
					break;
				default:
					myRC.suicide();
				}
			} catch(Exception e) { debug_stackTrace(e); }
		}
	
	}

}

